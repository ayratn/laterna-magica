/**
 * Expansion.java
 * 
 * Created on 26.03.2010
 */

package net.slightlymagic.laterna.magica.characteristics;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The class Expansion.
 * 
 * @version V0.0 26.03.2010
 * @author Clemens Koza
 */
public enum Expansion {
    //Core Sets
    _LEA("1E", "Limited Edition Alpha"),
    _LEB("2E", "Limited Edition Beta"),
    _2ED("2U", "Unlimited Edition"),
    _3ED("3E", "Revised Edition"),
    _4ED("4E", "Fourth Edition"),
    _5ED("5E", "Fifth Edition"),
    _6ED("6E", "Classic Sixth Edition"),
    _7ED("7E", "Seventh Edition"),
    _8ED("Eighth Edition"),
    _9ED("Ninth Edition"),
    _10E("Tenth Edition"),
    _M10("Magic 2010"),
    _M11("Magic 2011"),
    
    //Decks
    _EVG("Duel Decks: Elves vs. Goblins"),
    _JVC("DD2", "Duel Decks: Jace vs. Chandra"),
    _DVD("DDC", "Duel Decks: Divine vs. Demonic"),
    _GVL("DDD", "Duel Decks: Garruk vs. Liliana"),
    _PVC("DDE", "Duel Decks: Phyrexia vs. the Coalition"),
    _PDS("H09", "Premium Deck Series: Slivers"),
    
    //Box Sets
    _BRB("BR", "Battle Royale Box Set"),
    _BTD("BD", "Beatdown Box Set"),
    
    //From the Vault
    _FVD("DRB", "From the Vault: Dragons"),
    _FVE("V09", "From the Vault: Exiled"),
    
    //Special Items
    _VAN("VAN", "Vanguard"),
    _HOP("Planechase"),
    _TBA("ARC", "Archenemy"),
    
    //Promo-Sets
    _PPR("PPR", "Promo set for Gatherer"),
    
    //Un-Sets
    _UGL("UG", "Unglued"),
    _UNH("Unhinged"),
    
    //Magic Online Masters Editon
    _MED("Masters Edition"),
    _ME2("Masters Edition II"),
    _ME3("Masters Edition III"),
    
    //Starter Sets
    _POR("PO", "Portal"),
    _P02("P2", "Portal Second Age"),
    _PTK("PK", "Portal Three Kingdoms"),
    _S99("P3", "Starter 1999"),
    _S00("P4", "Starter 2000"),
    
    _CHR("CH", "Chronicles"),
    
    //Expansion sets
    
    _ARN("AN", "Arabian Nights"),
    _ATQ("AQ", "Antiquities"),
    _LEG("LE", "Legends"),
    _DRK("DK", "The Dark"),
    _FEM("FE", "Fallen Empires"),
    
    //Ice Age Block
    _ICE("IA", "Ice Age"),
    _ALL("AL", "Alliances"),
    _CSP("Coldsnap"),
    
    //Homelands is not officially part of the ice age block
    _HML("HM", "Homelands"),
    
    //Mirage Block
    _MIR("MI", "Mirage"),
    _VIS("VI", "Visions"),
    _WTH("WL", "Weatherlight"),
    
    //Tempest Block
    _TMP("TE", "Tempest"),
    _STH("ST", "Stronghold"),
    _EXO("EX", "Exodus"),
    
    //Urza Block
    _USG("UZ", "Urza's Saga"),
    _ULG("GU", "Urza's Legacy"),
    _UDS("CG", "Urza's Destiny"),
    
    //Masques Block
    _MMQ("MM", "Mercadian Masques"),
    _NMS("NE", "Nemesis"),
    _PCY("PR", "Prophecy"),
    
    //Invasion Block
    _INV("IN", "Invasion"),
    _PLS("PS", "Planeshift"),
    _APC("AP", "Apocalypse"),
    
    //Odyssey Block
    _ODY("OD", "Odyssey"),
    _TOR("Torment"),
    _JUD("Judgment"),
    
    //Onslaught Block
    _ONS("Onslaught"),
    _LGN("Legions"),
    _SCG("Scourge"),
    
    //Mirrodin Block
    _MRD("Mirrodin"),
    _DST("Darksteel"),
    _5DN("Fifth Dawn"),
    
    //Kamigawa Block
    _CHK("Champions of Kamigawa"),
    _BOK("Betrayers of Kamigawa"),
    _SOK("Saviors of Kamigawa"),
    
    //Ravnica Block
    _RAV("Ravnica: City of Guilds"),
    _GPT("Guildpact"),
    _DIS("Dissension"),
    
    //Time Spiral Block
    _TSP("Time Spiral"),
    _TSB("Time Spiral \"Timeshifted\""),
    _PLC("Planar Chaos"),
    _FUT("Future Sight"),
    
    //Lorwyn Block
    _LRW("Lorwyn"),
    _MOR("Morningtide"),
    //Shadowmoor Block
    _SHM("Shadowmoor"),
    _EVE("Eventide"),
    
    //Shards of Alara Block
    _ALA("Shards of Alara"),
    _CON("Conflux"),
    _ARB("Alara Reborn"),
    
    //Zendikar Block
    _ZEN("Zendikar"),
    _WWK("Worldwake"),
    _ROE("Rise of the Eldrazi"),
    
    //Scars of Mirrodin
    _SOM("Scars of Mirrodin");
    
    /**
     * Reads the "sets.txt" resource and creates the enum constants
     */
    public static void main(String[] args) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(
                Expansion.class.getResourceAsStream("sets.txt")));
        try {
            Pattern p = Pattern.compile("(.*?)\t(.*?)\t(.*)");
            String s;
            while((s = r.readLine()) != null) {
                if(s.length() > 0 && s.charAt(0) == '#') continue;
                Matcher m = p.matcher(s);
                if(!m.matches()) {
                    System.out.println(s);
                    continue;
                }
                
                String wikipedia = m.group(2);
                String gatherer = m.group(1);
                String longName = m.group(3).replaceAll("\"", "\\\\\"");
                if(wikipedia.equals("")) wikipedia = gatherer;
                
                if(gatherer.equals("")) System.out.printf("_%s(\"%s\"),%n", wikipedia, longName);
                else System.out.printf("_%s(\"%s\", \"%s\"),%n", wikipedia, gatherer, longName);
            }
        } finally {
            try {
                r.close();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private static final Map<String, Expansion> wikipedia;
    private static final Map<String, Expansion> gatherer;
    
    static {
        wikipedia = new HashMap<String, Expansion>();
        for(Expansion ex:values())
            wikipedia.put(ex.shortName, ex);
        
        gatherer = new HashMap<String, Expansion>();
        for(Expansion ex:values())
            gatherer.put(ex.gathererName, ex);
    }
    
    public static Expansion getExpansion(String shortName) {
        return wikipedia.get(shortName);
    }
    
    public static Expansion getExpansionFromGatherer(String gathererName) {
        return gatherer.get(gathererName);
    }
    
    private final String shortName, gathererName, longName;
    
    private Expansion(String longName) {
        this(null, longName);
    }
    
    private Expansion(String gathererName, String longName) {
        shortName = name().substring(1);
        this.gathererName = gathererName != null? gathererName:shortName;
        this.longName = longName;
    }
    
    public String getShortName() {
        return shortName;
    }
    
    public String getGathererName() {
        return gathererName;
    }
    
    public String getLongName() {
        return longName;
    }
    
    @Override
    public String toString() {
        return shortName + " - " + longName;
    }
}
