package net.anglesmith.eudaemon.command.dice;

import net.anglesmith.eudaemon.exception.EudaemonParsingException;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DiceGrammarParseTreeVisitor extends AbstractParseTreeVisitor<Integer> {

    private static final Random RANDOM = new Random();

    private static final String LITERAL_DICE_TYPE = "LITERAL_DICE";

    private static final String LITERAL_NUMBER_TYPE = "LITERAL_NUMBER";

    private static final String WHITESPACE = "WHITESPACE";

    @Override
    protected Integer defaultResult() {
        return 0;
    }

    @Override
    protected Integer aggregateResult(Integer aggregate, Integer nextResult) {
        return aggregate + nextResult;
    }

    @Override
    public Integer visitTerminal(TerminalNode node) {
        final Token nodeToken = node.getSymbol();

        final String nodeType = DiceGrammarLexer.VOCABULARY.getSymbolicName(nodeToken.getType());

        if (nodeType == null) {
            return this.defaultResult();
        } else {
            return this.calculateTerminalNodeValue(nodeToken, nodeType);
        }
    }

    @Override
    public Integer visitChildren(RuleNode node) {
        if (this.isOperatorStatement(node.getRuleContext())) {
            return this.visitOperatorStatement(node.getRuleContext());
        }

        return super.visitChildren(node);
    }

    /**
     * Breaks down an operator statement and interprets an appropriate result.
     *
     * @param operationContext A {@link RuleContext} able to be interpreted as a binary mathematical operation.
     * @return an {@link Integer}, the result of the mathematical operation.
     * @throws EudaemonParsingException if the {@link RuleContext} cannot be successfully interpreted.
     */
    private Integer visitOperatorStatement(RuleContext operationContext) {
        if (!this.isOperatorStatement(operationContext)) {
            throw new EudaemonParsingException(
                "How did we even get here?  This isn't an operation: " + operationContext.getText());
        }

        final ParseTree operator = operationContext.getChild(1).getChild(0);
        final ParseTree leftArgument = operationContext.getChild(0);
        final ParseTree rightArgument = operationContext.getChild(2);

        if (operator instanceof DiceGrammarParser.OperatorPlusContext) {
            return this.visit(leftArgument) + this.visit(rightArgument);
        } else if (operator instanceof DiceGrammarParser.OperatorMinusContext) {
            return this.visit(leftArgument) - this.visit(rightArgument);
        } else if (operator instanceof DiceGrammarParser.OperatorMultiplyContext) {
            return this.visit(leftArgument) * this.visit(rightArgument);
        } else if (operator instanceof DiceGrammarParser.OperatorDivideContext) {
            return this.visit(leftArgument) / this.visit(rightArgument);
        } else {
            throw new EudaemonParsingException("Operator contents unrecognized: " + operator.getText());
        }
    }

    /**
     * ANTLR doesn't like left-recursive rules.  This method compensates by identifying binary operations, which always
     * follow this pattern.
     *
     * @param context A {@link RuleContext} to check for an operator statement.
     * @return <code>true</code> if the statement is a binary mathematical operation.
     */
    private boolean isOperatorStatement(RuleContext context) {
        return context instanceof DiceGrammarParser.DiceExpressionContext
            && context.getChildCount() == 3
            && context.getChild(1) instanceof DiceGrammarParser.OperatorContext;
    }

    private Integer calculateTerminalNodeValue(Token nodeToken, String nodeType) {
        Integer nodeValue = this.defaultResult();

        switch (nodeType) {
            case LITERAL_DICE_TYPE:
                nodeValue = this.rollDice(nodeToken);
                break;
            case LITERAL_NUMBER_TYPE:
                nodeValue = this.interpretNumber(nodeToken);
                break;
            case WHITESPACE:
                break;
            default:
                throw new EudaemonParsingException("Parser hit an invalid terminal token: "
                        + nodeToken.getText() + ", type: " + nodeType);
        }

        return nodeValue;
    }

    private Integer rollDice(Token diceToken) {
        final String diceText = diceToken.getText().trim().toUpperCase();

        int numerator;
        int denominator;

        final int splitCharIndex = diceText.indexOf('D');

        try {
            if (splitCharIndex == 0) {
                numerator = 1;
                denominator = Integer.valueOf(diceText.substring(1));
            } else if (splitCharIndex > 0) {
                numerator = Integer.valueOf(diceText.substring(0, splitCharIndex));
                denominator = Integer.valueOf(diceText.substring(splitCharIndex + 1));
            } else {
                throw new EudaemonParsingException("Dice rolling subroutine passed invalid dice spec: " + diceText);
            }
        } catch (NumberFormatException ex) {
            throw new EudaemonParsingException("Dice text \"" + diceText + "\" failed to parse.", ex);
        }

        numerator = numerator < 1 ? 1 : numerator;

        List<Integer> diceResults = Arrays.asList(new Integer[numerator]);

        // Streams, baby
        diceResults.replaceAll(o -> RANDOM.nextInt(denominator) + 1);

        return diceResults.stream().mapToInt(Integer::intValue).sum();
    }

    private Integer interpretNumber(Token numberToken) {
        try {
            return Integer.valueOf(numberToken.getText().trim());
        } catch (NumberFormatException ex) {
            throw new EudaemonParsingException("Could not interpret \"" + numberToken.getText() + "\" as a number.");
        }
    }

}
