/**
 * TextCardCompiler.java
 * 
 * Created on 03.04.2010
 */

package net.slightlymagic.laterna.magica.cards.text;


import static java.lang.Integer.*;
import static net.slightlymagic.laterna.magica.LaternaMagica.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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
import net.slightlymagic.treeProperties.TreeProperty;
import net.slightlymagic.treeProperties.plain.PlainProperties;
import net.slightlymagic.treeProperties.plain.PlainProperties.LogicalLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;


/**
 * The class TextCardCompiler.
 * 
 * @version V0.0 03.04.2010
 * @author Clemens Koza
 */
public class TextCardCompiler implements CardCompiler {
    private static final Logger                   log                = LoggerFactory.getLogger(TextCardCompiler.class);
    
    private static final Pattern                  set                = Pattern.compile("(\\w+) (land|common|uncommon|rare|mythic) (\\d+)");
    
    private static final String                   LINE_HANDLER_CLASS = "/laterna/res/cards/uncompiled/text/lines/class";
    private static final Map<String, LineHandler> lineHandlers;
    
    static {
        lineHandlers = new HashMap<String, LineHandler>();
        
        List<TreeProperty> classes = PROPS().getAllProperty(LINE_HANDLER_CLASS);
        //loop through each class
        for(TreeProperty pr:classes) {
            //get an object of the compiler class
            String clazz = (String) pr.getValue();
            LineHandler parser;
            try {
                parser = (LineHandler) Class.forName(clazz).newInstance();
            } catch(Exception ex) {
                log.warn(clazz + " couldn't be instantiated", ex);
                continue;
            }
            lineHandlers.put(parser.getKey(), parser);
        }
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
                    if("ignore".equals(key)) throw new InvalidCardException("Card is ignored");
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
         * Returns the key if this line handler. the handler will be used for lines with that key.
         */
        public String getKey();
        
        /**
         * Modifies the ParseContext as applicable.
         */
        public void apply(LineContext context) throws InvalidCardException;
    }
    
    static class NameHandler implements LineHandler {
        
        public String getKey() {
            return "name";
        }
        
        
        public void apply(LineContext from) {
            from.getContext().getPart().setName(from.getValue());
        }
    }
    
    static class CostHandler implements LineHandler {
        
        public String getKey() {
            return "cost";
        }
        
        
        public void apply(LineContext from) {
            ManaSequence cost = ManaFactoryImpl.INSTANCE.parseSequence(from.getValue());
            from.getContext().getPart().setManaCost(cost);
            from.getContext().getPart().takeColors();
            from.getContext().getCastInfo().getDelegates().add(
                    FactoryFunction.getInstance(ManaSequence.class, cost, PlayAction.class,
                            ManaCostInformation.class));
        }
    }
    
    static class ColorHandler implements LineHandler {
        
        public String getKey() {
            return "color";
        }
        
        
        public void apply(LineContext from) {
            from.getContext().getPart().getColors().clear();
            
            String[] colors = from.getValue().trim().split(",?\\s+");
            for(int i = 0; i < colors.length; i++)
                from.getContext().getPart().getColors().add(MagicColor.valueOf(colors[i].toUpperCase()));
        }
    }
    
    static class SuperHandler implements LineHandler {
        
        public String getKey() {
            return "super";
        }
        
        
        public void apply(LineContext from) {
            from.getContext().getPart().getSuperTypes().clear();
            
            String[] supers = from.getValue().trim().split(",?\\s+");
            for(int i = 0; i < supers.length; i++)
                from.getContext().getPart().getSuperTypes().add(SuperType.valueOf(supers[i].toUpperCase()));
        }
    }
    
    static class TypeHandler implements LineHandler {
        private static final Map<SubType, ActivatedAbility> manaAbilities;
        
        static {
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
        
        
        public String getKey() {
            return "type";
        }
        
        
        public void apply(LineContext from) throws InvalidCardException {
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
    
    static class PTHandler implements LineHandler {
        
        public String getKey() {
            return "pt";
        }
        
        
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
    
    static class LHandler implements LineHandler {
        
        public String getKey() {
            return "l";
        }
        
        
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
