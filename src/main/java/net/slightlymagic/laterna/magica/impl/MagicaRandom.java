/**
 * MagicaRandom.java
 * 
 * Created on 01.12.2009
 */

package net.slightlymagic.laterna.magica.impl;


import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import net.slightlymagic.laterna.magica.Game;
import net.slightlymagic.laterna.magica.GameContent;
import net.slightlymagic.laterna.magica.edit.Edit;


/**
 * The class MagicaRandom is a subclass of {@link Random} that duplicates its instance variables and some of its
 * methods (namely {@link #setSeed(long)}, {@link #next(int)} and {@link #nextGaussian()}) to delegate all work to
 * those instance variables. By doing so, MagicaRandom traces all of its state modifications and thus enables undo
 * down to the level of random parts of the game.
 * 
 * @version V1.0 18.01.2009
 * @author Clemens Koza
 */
public class MagicaRandom extends Random implements GameContent {
    private static final long serialVersionUID     = -4408352337561360743L;
    
    private Game              game;
    
    private long              initialSeed;
    private AtomicLong        seed;
    
    private double            nextNextGaussian;
    private boolean           haveNextNextGaussian = false;
    
    private final static long multiplier           = 0x5DEECE66DL;
    private final static long addend               = 0xBL;
    private final static long mask                 = (1L << 48) - 1;
    
    public MagicaRandom(Game game) {
        super();
        init(game);
    }
    
    public MagicaRandom(Game game, long seed) {
        super(seed);
        init(game);
    }
    
    private void init(Game game) {
        if(game == null) throw new IllegalArgumentException("game == null");
        this.game = game;
        seed = new AtomicLong();
        setSeed(initialSeed);
    }
    
    public Game getGame() {
        return game;
    }
    
    @Override
    public synchronized void setSeed(long seed) {
        if(game == null) {
            //when called by the superconstructor, cache the parameter to recall after initialization
            initialSeed = seed;
            return;
        }
        seed = (seed ^ multiplier) & mask;
        new SetSeedEdit(seed).execute();
    }
    
    @Override
    protected int next(int bits) {
        NextEdit e = new NextEdit(bits);
        e.execute();
        return e.getNext();
    }
    
    @Override
    synchronized public double nextGaussian() {
        // See Knuth, ACP, Section 3.4.1 Algorithm C.
        if(haveNextNextGaussian) {
            new NextNextGaussianEdit().execute();
            return nextNextGaussian;
        } else {
            double v1, v2, s;
            do {
                v1 = 2 * nextDouble() - 1; // between -1 and 1
                v2 = 2 * nextDouble() - 1; // between -1 and 1 
                s = v1 * v1 + v2 * v2;
            } while(s >= 1 || s == 0);
            double multiplier = Math.sqrt(-2 * Math.log(s) / s);
            new NextNextGaussianEdit(v2 * multiplier).execute();
            return v1 * multiplier;
        }
    }
    
    /**
     * Able to store and restore the current seed, and next next gaussian, but does no actual modification
     */
    private abstract class MagicaRandomEdit extends Edit {
        private static final long serialVersionUID = -8130906941036299876L;
        
        private long              seed;
        private double            nextNextGaussian;
        private boolean           haveNextNextGaussian;
        
        public MagicaRandomEdit() {
            super(MagicaRandom.this.getGame());
        }
        
        @Override
        protected void execute() {
            seed = MagicaRandom.this.seed.get();
            nextNextGaussian = MagicaRandom.this.nextNextGaussian;
            haveNextNextGaussian = MagicaRandom.this.haveNextNextGaussian;
        }
        
        @Override
        protected void rollback() {
            MagicaRandom.this.seed.set(seed);
            MagicaRandom.this.nextNextGaussian = nextNextGaussian;
            MagicaRandom.this.haveNextNextGaussian = haveNextNextGaussian;
        }
    }
    
    private class SetSeedEdit extends MagicaRandomEdit {
        private static final long serialVersionUID = -7902404739056800275L;
        
        private long              seed;
        
        public SetSeedEdit(long seed) {
            this.seed = seed;
        }
        
        @Override
        protected void execute() {
            super.execute();
            MagicaRandom.this.seed.set(seed);
            haveNextNextGaussian = false;
        }
        
        @Override
        public String toString() {
            return "Set seed";
        }
    }
    
    private class NextEdit extends MagicaRandomEdit {
        private static final long serialVersionUID = -1957216154245204384L;
        
        private int               bits;
        private int               next;
        
        public NextEdit(int bits) {
            this.bits = bits;
        };
        
        @Override
        protected void execute() {
            super.execute();
            long oldseed, nextseed;
            AtomicLong seed = MagicaRandom.this.seed;
            do {
                oldseed = seed.get();
                nextseed = (oldseed * multiplier + addend) & mask;
            } while(!seed.compareAndSet(oldseed, nextseed));
            next = (int) (nextseed >>> (48 - bits));
        }
        
        public int getNext() {
            return next;
        }
        
        @Override
        public String toString() {
            return "Generate " + bits + " random bits";
        }
    }
    
    private class NextNextGaussianEdit extends MagicaRandomEdit {
        private static final long serialVersionUID = 2517892875209765927L;
        
        private double            nextNextGaussian;
        private boolean           haveNextNextGaussian;
        
        public NextNextGaussianEdit() {
            haveNextNextGaussian = false;
        }
        
        public NextNextGaussianEdit(double nextNextGaussian) {
            this.nextNextGaussian = nextNextGaussian;
            haveNextNextGaussian = true;
        }
        
        @Override
        protected void execute() {
            super.execute();
            MagicaRandom.this.nextNextGaussian = nextNextGaussian;
            MagicaRandom.this.haveNextNextGaussian = haveNextNextGaussian;
        }
        
        @Override
        public String toString() {
            return "Generate gaussian";
        }
    }
}
