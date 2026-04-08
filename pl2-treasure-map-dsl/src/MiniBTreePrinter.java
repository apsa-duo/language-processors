import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * ANTLR listener that pretty-prints the MiniBasic parse tree.
 *
 * <p>For each parser rule, this listener prints the rule name and its
 * text content with hierarchical indentation. Terminal nodes are also
 * printed with their literal text.</p>
 *
 * @author Asier Alamo, Sergio Alonso Zarcero, Andrea Pascual Aguilera
 */
public class MiniBTreePrinter extends MiniBasicParserBaseListener {

    /** Current indentation depth for tree visualization. */
    private int indentLevel = 0;

    /**
     * Returns a string of spaces proportional to the current indent level.
     *
     * @return the indentation whitespace
     */
    private String indent() {
        return " ".repeat(indentLevel * 2);
    }

    /**
     * Prints terminal (leaf) nodes with their text value.
     */
    @Override
    public void visitTerminal(TerminalNode node) {
        System.out.println(indent() + "Terminal node - " + node.getText());
    }

    /**
     * Called when entering any parser rule — prints the rule type and
     * formatted text, then increases indentation for child nodes.
     */
    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        System.out.println(indent() + "Node type: "
                + MiniBasicParser.ruleNames[ctx.getRuleIndex()]);
        indentLevel++;
        System.out.println(indent() + "Text: " + formatText(ctx.getText()));
    }

    /**
     * Called when exiting any parser rule — decreases indentation.
     */
    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        indentLevel--;
    }

    /**
     * Adds whitespace between tokens for readability.
     *
     * <p>Inserts spaces at camelCase boundaries, between letters and digits,
     * and between digits and non-digits.</p>
     *
     * @param text the raw concatenated text from the parse tree
     * @return the formatted text with added spacing
     */
    private String formatText(String text) {
        return text.replaceAll("(?<=\\w)(?=[A-Z])", " ")
                   .replaceAll("(?<=[a-zA-Z])(?=[0-9])", " ")
                   .replaceAll("(?<=\\d)(?=\\D)", " ")
                   .trim();
    }
}
