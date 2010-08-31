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
    _LEA("A", "1E", "Limited Edition Alpha"),
    _LEB("B", "2E", "Limited Edition Beta"),
    _2ED("U", "2U", "Unlimited Edition"),
    _3ED("R", "3E", "Revised Edition"),
    _4ED(null, "4E", "Fourth Edition"),
    _5ED(null, "5E", "Fifth Edition"),
    _6ED(null, "6E", "Classic Sixth Edition"),
    _7ED(null, "7E", "Seventh Edition"),
    _8ED("8E", "Eighth Edition"),
    _9ED("9E", "Ninth Edition"),
    _10E("Tenth Edition"),
    _M10("Magic 2010"),
    _M11("Magic 2011"),
    
    //Decks
    _EVG("Duel Decks: Elves vs. Goblins"),
    _JVC("JVC", "DD2", "Duel Decks: Jace vs. Chandra"),
    _DVD("DVD", "DDC", "Duel Decks: Divine vs. Demonic"),
    _GVL(null, "DDD", "Duel Decks: Garruk vs. Liliana"),
    _PVC(null, "DDE", "Duel Decks: Phyrexia vs. the Coalition"),
    _PDS(null, "H09", "Premium Deck Series: Slivers"),
    
    //Box Sets
    _BRB(null, "BR", "Battle Royale Box Set"),
    _BTD(null, "BD", "Beatdown Box Set"),
    
    //From the Vault
    _FVD(null, "DRB", "From the Vault: Dragons"),
    _FVE(null, "V09", "From the Vault: Exiled"),
    
    //Special Items
    _VAN("Vanguard"),
    _HOP("Planechase"),
    _TBA(null, "ARC", "Archenemy"),
    
    //Promo-Sets
    _PPR("Promo set for Gatherer"),
    
    //Un-Sets
    _UGL(null, "UG", "Unglued"),
    _UNH("Unhinged"),
    
    //Magic Online Masters Editon
    _MED("Masters Edition"),
    _ME2("Masters Edition II"),
    _ME3("Masters Edition III"),
    
    //Starter Sets
    _POR("PT", "PO", "Portal"),
    _P02(null, "P2", "Portal Second Age"),
    _PTK("P3", "PK", "Portal Three Kingdoms"),
    _S99("ST", "P3", "Starter 1999"),
    _S00("S2K", "P4", "Starter 2000"),
    
    _CHR(null, "CH", "Chronicles"),
    
    //Expansion sets
    
    _ARN(null, "AN", "Arabian Nights"),
    _ATQ(null, "AQ", "Antiquities"),
    _LEG(null, "LE", "Legends"),
    _DRK(null, "DK", "The Dark"),
    _FEM(null, "FE", "Fallen Empires"),
    
    //Ice Age Block
    _ICE(null, "IA", "Ice Age"),
    _ALL(null, "AL", "Alliances"),
    _CSP("CS", "Coldsnap"),
    
    //Homelands is not officially part of the ice age block
    _HML("HL", "HM", "Homelands"),
    
    //Mirage Block
    _MIR(null, "MI", "Mirage"),
    _VIS(null, "VI", "Visions"),
    _WTH(null, "WL", "Weatherlight"),
    
    //Tempest Block
    _TMP(null, "TE", "Tempest"),
    _STH("SH", "ST", "Stronghold"),
    _EXO(null, "EX", "Exodus"),
    
    //Urza Block
    _USG("US", "UZ", "Urza's Saga"),
    _ULG("UL", "GU", "Urza's Legacy"),
    _UDS("UD", "CG", "Urza's Destiny"),
    
    //Masques Block
    _MMQ(null, "MM", "Mercadian Masques"),
    _NMS(null, "NE", "Nemesis"),
    _PCY("PY", "PR", "Prophecy"),
    
    //Invasion Block
    _INV(null, "IN", "Invasion"),
    _PLS(null, "PS", "Planeshift"),
    _APC(null, "AP", "Apocalypse"),
    
    //Odyssey Block
    _ODY(null, "OD", "Odyssey"),
    _TOR("TO", "Torment"),
    _JUD("JU", "Judgment"),
    
    //Onslaught Block
    _ONS("ON", "Onslaught"),
    _LGN("LE", "Legions"),
    _SCG("SC", "Scourge"),
    
    //Mirrodin Block
    _MRD("MR", "Mirrodin"),
    _DST("DS", "Darksteel"),
    _5DN("FD", "Fifth Dawn"),
    
    //Kamigawa Block
    _CHK("Champions of Kamigawa"),
    _BOK("Betrayers of Kamigawa"),
    _SOK("Saviors of Kamigawa"),
    
    //Ravnica Block
    _RAV("Ravnica: City of Guilds"),
    _GPT("GP", "Guildpact"),
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
    _CON("CFX", "Conflux"),
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
            Pattern p = Pattern.compile("(.*?)\t(.*?)\t(.*?)\t(.*)");
            String s;
            while((s = r.readLine()) != null) {
                if(s.length() > 0 && s.charAt(0) == '#') continue;
                Matcher m = p.matcher(s);
                if(!m.matches()) {
                    System.out.println(s);
                    continue;
                }
                
                String wikipedia = m.group(3);
                String gatherer = m.group(1);
                String mws = m.group(2);
                String longName = m.group(4).replaceAll("\"", "\\\\\"");
                if(wikipedia.equals("")) {
                    wikipedia = gatherer;
                    gatherer = "";
                }
                
                switch((gatherer.equals("")? 0:1) | (mws.equals("")? 0:2)) {
                    case 0: //!gatherer && !mws
                        System.out.printf("_%s(\"%s\"),%n", wikipedia, longName);
                    break;
                    case 1: //gatherer && !mws
                        System.out.printf("_%s(null, \"%s\", \"%s\"),%n", wikipedia, gatherer, longName);
                    break;
                    case 2: //!gatherer && mws
                        System.out.printf("_%s(\"%s\", \"%s\"),%n", wikipedia, mws, longName);
                    break;
                    case 3: //gatherer && mws
                        System.out.printf("_%s(\"%s\", \"%s\", \"%s\"),%n", wikipedia, mws, gatherer, longName);
                    break;
                    default:
                        throw new AssertionError();
                }
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
    
    private final String shortName, mwsName, gathererName, longName;
    
    private Expansion(String longName) {
        this(null, null, longName);
    }
    
    private Expansion(String mwsName, String longName) {
        this(mwsName, null, longName);
    }
    
    private Expansion(String mwsName, String gathererName, String longName) {
        shortName = name().substring(1);
        this.gathererName = gathererName != null? gathererName:shortName;
        this.mwsName = mwsName != null? mwsName:gathererName;
        this.longName = longName;
    }
    
    public String getShortName() {
        return shortName;
    }
    
    public String getMwsName() {
        return mwsName;
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
