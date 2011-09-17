/**
 * TextCardCompiler.java
 * 
 * Created on 03.04.2010
 */

package net.slightlymagic.laterna.magica.cards.text;


import static java.lang.Integer.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.slightlymagic.laterna.magica.ability.Ability;
import net.slightlymagic.laterna.magica.ability.ActivatedAbility;
import net.slightlymagic.laterna.magica.action.GameAction;
import net.slightlymagic.laterna.magica.action.play.CastAction;
import net.slightlymagic.laterna.magica.action.play.PlayAction;
import net.slightlymagic.laterna.magica.action.play.PlayInformation;
import net.slightlymagic.laterna.magica.action.play.impl.AbstractPlayInformation;
import net.slightlymagic.laterna.magica.action.play.impl.PlayInformationFunction;
import net.slightlymagic.laterna.magica.card.CardTemplate;
import net.slightlymagic.laterna.magica.card.Printing;
import net.slightlymagic.laterna.magica.card.impl.CardPartsImpl;
import net.slightlymagic.laterna.magica.card.impl.CardTemplateImpl;
import net.slightlymagic.laterna.magica.cards.CardCompiler;
import net.slightlymagic.laterna.magica.cards.CompileHandler;
import net.slightlymagic.laterna.magica.cards.IgnoredCardException;
import net.slightlymagic.laterna.magica.cards.InvalidCardException;
import net.slightlymagic.laterna.magica.characteristics.CardType;
import net.slightlymagic.laterna.magica.characteristics.Expansion;
import net.slightlymagic.laterna.magica.characteristics.MagicColor;
import net.slightlymagic.laterna.magica.characteristics.Rarity;
import net.slightlymagic.laterna.magica.characteristics.SubType;
import net.slightlymagic.laterna.magica.characteristics.SuperType;
import net.slightlymagic.laterna.magica.cost.impl.ManaCostInformation;
import net.slightlymagic.laterna.magica.mana.ManaSequence;
import net.slightlymagic.laterna.magica.mana.impl.ManaFactoryImpl;
import net.slightlymagic.laterna.magica.util.FactoryFunction;
import net.slightlymagic.laterna.magica.util.MagicaUtils;
import net.slightlymagic.treeProperties.plain.PlainProperties;
import net.slightlymagic.treeProperties.plain.PlainProperties.LogicalLine;

import com.google.common.base.Predicate;


/**
 * The class TextCardCompiler.
 * 
 * @version V0.0 03.04.2010
 * @author Clemens Koza
 */
public class TextCardCompiler implements CardCompiler {
//    private static final Logger            log          = LoggerFactory.getLogger(TextCardCompiler.class);
    
    private static final Pattern           set          = Pattern.compile("(\\w+) (land|common|uncommon|rare|mythic) (\\d+)");
    
    private final Map<String, LineHandler> lineHandlers = new HashMap<String, LineHandler>();
    private File                           path;
    
    public void setLine(String key, LineHandler line) {
        lineHandlers.put(key, line);
    }
    
    public void setPath(URL path) {
        try {
            this.path = new File(path.toURI());
        } catch(URISyntaxException ex) {
            throw new IllegalArgumentException("Supplied URL must belong to a file: " + path, ex);
        }
    }
    
    public File getPath() {
        return path;
    }
    
    public CardTemplate compile(InputStream is, List<? extends CompileHandler> handlers) throws IOException, InvalidCardException {
        PlainProperties p = new PlainProperties();
        p.read(is);
        
        List<LogicalLine> lines = p.getLogicalLines();
        
        ParseContext context = new ParseContext(handlers);
        
        try {
            for(LogicalLine line:lines) {
                String key = line.getKey();
                
                if(key.equals("part")) {
                    //begin a new part
                    context.startPart();
                    continue;
                } else if(key.startsWith("@")) {
                    key = key.substring(1);
                    if("ignore".equals(key)) throw new IgnoredCardException("Card is ignored");
                    else if("set".equals(key)) {
                        Matcher m = set.matcher(line.getValue());
                        if(m.matches()) {
                            Expansion e = null;
                            try {
                                e = Expansion.getExpansion(m.group(1).toUpperCase());
                            } catch(IllegalArgumentException ex) {}
                            Rarity r = null;
                            try {
                                r = Rarity.valueOf(m.group(2).toUpperCase());
                            } catch(IllegalArgumentException ex) {}
                            int mid = 0;
                            try {
                                mid = valueOf(m.group(3));
                            } catch(NumberFormatException ex) {}
                            context.template.getPrintings().add(new Printing(context.template, e, r, mid));
                        }
                    } else context.annotate(key, line.getValue());
                } else {
                    LineHandler h = lineHandlers.get(key);
                    if(h != null) h.apply(new LineContext(context, line));
                    else context.warn("No parser found for property: " + line.getLine());
                }
                
            }
        } catch(InvalidCardException ex) {
            throw ex;
        } catch(Exception ex) {
            throw new InvalidCardException(ex);
        }
        
        return context.getTemplate();
    }
    
    /**
     * The class ParseContext. Stores information about a card while its being parsed. The parse context can be
     * passed as a parameter to delegates which modify the card being parsed.
     */
    public static class ParseContext {
        private final List<? extends CompileHandler> handlers;
        
        private CardTemplate                         template;
        private CardPartsImpl                        current;
        PlayInformationFunction<CastAction>          castInfo;
        
        ParseContext(List<? extends CompileHandler> handlers) {
            this.handlers = handlers;
            template = new CardTemplateImpl();
        }
        
        public void warn(Object detail) {
            for(CompileHandler handler:handlers)
                handler.handleWarn(detail);
        }
        
        public void annotate(String key, String value) {
            for(CompileHandler handler:handlers)
                handler.handleAnnotation(key, value);
        }
        
        /**
         * Returns the template encapsulated in this context
         */
        public CardTemplate getTemplate() {
            return template;
        }
        
        /**
         * Creates and adds a new {@link CardPartsImpl} to the template,
         */
        public void startPart() {
            current = new CardPartsImpl();
            current.setLegal(new Legal());
            current.setPlayInformation(castInfo = new PlayInformationFunction<CastAction>());
            template.getCardParts().add(current);
            
        }
        
        /**
         * Returns the part currently being edited
         */
        public CardPartsImpl getPart() {
            if(current == null) startPart();
            return current;
        }
        
        /**
         * Returns the conversion function for retrieving the {@link PlayInformation} for casting this card.
         */
        public PlayInformationFunction<CastAction> getCastInfo() {
            return castInfo;
        }
    }
    
    /**
     * The class LineContext. The line context stores information about a single line found in the parsed file. The
     * line context can be passed as a parameter to delegates which modify the card being parsed.
     */
    public static class LineContext {
        private ParseContext context;
        private String       key, value;
        
        public LineContext(ParseContext context, LogicalLine line) {
            this(context, line.getKey(), line.getValue());
        }
        
        public LineContext(ParseContext context, String key, String value) {
            this.context = context;
            this.key = key;
            this.value = value;
        }
        
        /**
         * Returns the parse context in which this line is parsed.
         */
        public ParseContext getContext() {
            return context;
        }
        
        /**
         * Returns the line being parsed
         */
        public String getLine() {
            return key + " " + value;
        }
        
        /**
         * Returns the key of the line being parsed
         */
        public String getKey() {
            return key;
        }
        
        /**
         * Returns the value of the line being parsed
         */
        public String getValue() {
            return value;
        }
    }
    
    public static class Legal implements Predicate<PlayAction>, Serializable {
        private static final long serialVersionUID = 5099952621198941520L;
        
        
        public boolean apply(PlayAction input) {
            if(input.getObject().getZone() != input.getController().getHand()) return false;
            if(input.getObject().getCharacteristics().get(0).hasType(CardType.INSTANT)) {
                return MagicaUtils.canPlayInstant(input.getController());
            } else {
                return MagicaUtils.canPlaySorcery(input.getController());
            }
        }
    }
    
    public static class CastInformation extends AbstractPlayInformation {
        public CastInformation(List<GameAction> effects, CastAction action) {
            super(new ArrayList<PlayInformation>(), action);
        }
        
//        private static GameAction getCost(CastAction action) {
////            return DummyCostFunction.EMPTY.apply(action.getGame());
//            return MagicaUtils.createManaCost(action, action.getObject().getCharacteristics().get(0).getManaCost());
//        }
    }
    
    public static interface LineHandler {
        /**
         * Modifies the ParseContext as applicable.
         */
        public void apply(LineContext context) throws InvalidCardException;
    }
    
    public static class NameHandler implements LineHandler {
        public void apply(LineContext from) {
            from.getContext().getPart().setName(from.getValue());
        }
    }
    
    public static class CostHandler implements LineHandler {
        public void apply(LineContext from) {
            ManaSequence cost = ManaFactoryImpl.INSTANCE.parseSequence(from.getValue());
            from.getContext().getPart().setManaCost(cost);
            from.getContext().getPart().takeColors();
            from.getContext().getCastInfo().getDelegates().add(
                    FactoryFunction.getInstance(ManaSequence.class, cost, PlayAction.class,
                            ManaCostInformation.class));
        }
    }
    
    public static class ColorHandler implements LineHandler {
        public void apply(LineContext from) {
            from.getContext().getPart().getColors().clear();
            
            String[] colors = from.getValue().trim().split(",?\\s+");
            for(int i = 0; i < colors.length; i++)
                from.getContext().getPart().getColors().add(MagicColor.valueOf(colors[i].toUpperCase()));
        }
    }
    
    public static class SuperHandler implements LineHandler {
        public void apply(LineContext from) {
            from.getContext().getPart().getSuperTypes().clear();
            
            String[] supers = from.getValue().trim().split(",?\\s+");
            for(int i = 0; i < supers.length; i++)
                from.getContext().getPart().getSuperTypes().add(SuperType.valueOf(supers[i].toUpperCase()));
        }
    }
    
    public static class TypeHandler implements LineHandler {
        private Map<SubType, ActivatedAbility> manaAbilities;
        
        private void init() {
            if(manaAbilities != null) return;
            
            manaAbilities = new HashMap<SubType, ActivatedAbility>();
            ActivatedAbilityParser p = new ActivatedAbilityParser();
            String[] subtypes = {"plains", "island", "swamp", "mountain", "forest"};
            for(int i = 0; i < subtypes.length; i++) {
                SubType subtype = SubType.getSubtype(CardType.LAND, subtypes[i]);
                ActivatedAbility ability = p.getAbility("{T}: Add {" + MagicColor.values()[i].getShortChar()
                        + "} to your mana pool.");
                manaAbilities.put(subtype, ability);
            }
        }
        
        public void apply(LineContext from) throws InvalidCardException {
            init();
            
            String[] parts = from.getValue().trim().split("\\s*-\\s*");
            if(parts.length < 1 || parts.length > 2) throw new InvalidCardException(
                    "type must be either \"<type>\" or \"<type> - <subtypes> ...\"", from.getLine());
            
            CardType type = CardType.valueOf(parts[0].toUpperCase());
            from.getContext().getPart().getTypes().add(type);
            
            if(parts.length > 1) for(String subtype:parts[1].split(",?\\s+")) {
                SubType sub = SubType.getSubtype(type, subtype);
                from.getContext().getPart().getSubTypes().add(sub);
                
                Ability ab = manaAbilities.get(sub);
                if(ab != null) from.getContext().getPart().getAbilities().add(ab);
            }
        }
    }
    
    public static class PTHandler implements LineHandler {
        public void apply(LineContext from) throws InvalidCardException {
            String[] parts = from.getValue().trim().split("\\s*/\\s*");
            if(parts.length != 2) throw new InvalidCardException("pt must be \"<power> / <toughness>\"",
                    from.getLine());
            
            // "*/*" is not a number, but also legal, so we ignore values which are not numbers
            // there would be too many variants to check: "*+1" etc.
            if(parts[0].matches("-?\\d+")) from.getContext().getPart().setPower(parseInt(parts[0]));
            else from.getContext().warn(
                    from.getContext().getPart().toString() + ": Power is not a number: \"" + parts[0] + "\"");
            
            if(parts[1].matches("-?\\d+")) from.getContext().getPart().setToughness(parseInt(parts[1]));
            else from.getContext().warn(
                    from.getContext().getPart().toString() + ": Toughness is not a number: \"" + parts[1] + "\"");
        }
    }
    
    public static class LHandler implements LineHandler {
        public void apply(LineContext from) throws InvalidCardException {
            String value = from.getValue().trim();
            
            // "X" is not a number, but also legal, so we ignore values which are not numbers
            // there would be too many variants to check: "X+1" etc.
            if(value.matches("-?\\d+")) from.getContext().getPart().setLoyalty(parseInt(value));
            else from.getContext().warn(
                    from.getContext().getPart().toString() + ": Loyalty is not a number: \"" + value + "\"");
        }
    }
}
