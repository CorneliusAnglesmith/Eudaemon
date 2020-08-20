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
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class DiceAggregatingParseTreeVisitor extends AbstractParseTreeVisitor<DiceResultAggregator> {
    private static final Random RANDOM = new Random();

    private static final String LITERAL_DICE_TYPE = "LITERAL_DICE";

    private static final String LITERAL_NUMBER_TYPE = "LITERAL_NUMBER";

    private static final String WHITESPACE = "WHITESPACE";

    public static final int MAX_DICE_COUNT = 50;

    @Override
    protected DiceResultAggregator defaultResult() {
        return new DiceResultAggregator();
    }

    @Override
    protected DiceResultAggregator aggregateResult(DiceResultAggregator aggregate, DiceResultAggregator nextResult) {
        return aggregate.join(nextResult);
    }

    @Override
    public DiceResultAggregator visitTerminal(TerminalNode node) {
        final Token nodeToken = node.getSymbol();

        final String nodeType = DiceGrammarLexer.VOCABULARY.getSymbolicName(nodeToken.getType());

        if (nodeType == null) {
            return new DiceResultAggregator();
        } else {
            return this.calculateTerminalNodeValue(nodeToken, nodeType);
        }
    }

    @Override
    public DiceResultAggregator visitChildren(RuleNode node) {
        if (this.isOperatorStatement(node.getRuleContext())) {
            return this.visitOperatorStatement(node.getRuleContext());
        }

        return super.visitChildren(node);
    }

    private DiceResultAggregator calculateTerminalNodeValue(Token nodeToken, String nodeType) {
        final DiceResultAggregator nodeValue;

        switch (nodeType) {
            case LITERAL_DICE_TYPE:
                nodeValue = this.rollDice(nodeToken);
                break;
            case LITERAL_NUMBER_TYPE:
                nodeValue = this.interpretNumber(nodeToken);
                break;
            case WHITESPACE:
                nodeValue = new DiceResultAggregator();
                break;
            default:
                throw new EudaemonParsingException("Parser hit an invalid terminal token: "
                    + nodeToken.getText() + ", type: " + nodeType);
        }

        return nodeValue;
    }

    private DiceResultAggregator rollDice(Token diceToken) {
        final String diceText = diceToken.getText().trim().toUpperCase();

        int numerator;
        int denominator;

        final int splitCharIndex = diceText.indexOf('D');

        try {
            if (splitCharIndex == 0) {
                numerator = 1;
                denominator = Integer.parseInt(diceText.substring(1));
            } else if (splitCharIndex > 0) {
                numerator = Integer.parseInt(diceText.substring(0, splitCharIndex));
                denominator = Integer.parseInt(diceText.substring(splitCharIndex + 1));
            } else {
                throw new EudaemonParsingException("Dice rolling subroutine passed invalid dice spec: " + diceText);
            }
        } catch (NumberFormatException ex) {
            throw new EudaemonParsingException("Dice text \"" + diceText + "\" failed to parse.", ex);
        }

        numerator = Math.max(numerator, 1);

        List<Integer> diceResults = Arrays.asList(new Integer[numerator]);

        if (diceResults.size() > MAX_DICE_COUNT) {
            throw new EudaemonParsingException("Cannot roll more than " + MAX_DICE_COUNT + " dice simultaneously.");
        }

        // Streams, baby
        diceResults.replaceAll(o -> RANDOM.nextInt(denominator) + 1);

        final int diceValue = diceResults.stream().mapToInt(Integer::intValue).sum();

        String lexicalValue = "[" + diceText + ": "
            + diceResults.stream().map(Objects::toString).collect(Collectors.joining(", "))
            + (diceResults.size() > 1 ? " (" + diceValue + ")" : "")
            + "]";
        return new DiceResultAggregator(diceValue, lexicalValue);
    }

    private DiceResultAggregator interpretNumber(Token numberToken) {
        try {
            final String literalValue = numberToken.getText().trim();

            return new DiceResultAggregator(Integer.parseInt(literalValue), literalValue);
        } catch (NumberFormatException ex) {
            throw new EudaemonParsingException("Could not interpret \"" + numberToken.getText() + "\" as a number.");
        }
    }

    private DiceResultAggregator visitOperatorStatement(RuleContext operationContext) {
        if (!this.isOperatorStatement(operationContext)) {
            throw new EudaemonParsingException(
                "How did we even get here?  This isn't an operation: " + operationContext.getText());
        }

        final ParseTree operator = operationContext.getChild(1).getChild(0);
        final DiceResultAggregator leftArgument = this.visit(operationContext.getChild(0));
        final DiceResultAggregator rightArgument = this.visit(operationContext.getChild(2));

        final DiceResultAggregator nodeValue = new DiceResultAggregator(0, "(");

        if (operator instanceof DiceGrammarParser.OperatorPlusContext) {
            nodeValue.addValue(leftArgument.getValue() + rightArgument.getValue());
            nodeValue.appendLexicalValue(leftArgument.getLexicalValue() + " + " + rightArgument.getLexicalValue());
        } else if (operator instanceof DiceGrammarParser.OperatorMinusContext) {
            nodeValue.addValue(leftArgument.getValue() - rightArgument.getValue());
            nodeValue.appendLexicalValue(leftArgument.getLexicalValue() + " - " + rightArgument.getLexicalValue());
        } else if (operator instanceof DiceGrammarParser.OperatorMultiplyContext) {
            nodeValue.addValue(leftArgument.getValue() * rightArgument.getValue());
            nodeValue.appendLexicalValue(leftArgument.getLexicalValue() + " * " + rightArgument.getLexicalValue());
        } else if (operator instanceof DiceGrammarParser.OperatorDivideContext) {
            nodeValue.addValue(leftArgument.getValue() / rightArgument.getValue());
            nodeValue.appendLexicalValue(leftArgument.getLexicalValue() + " / " + rightArgument.getLexicalValue());
        } else {
            throw new EudaemonParsingException("Operator contents unrecognized: " + operator.getText());
        }

        return nodeValue.appendLexicalValue(")");
    }

    private boolean isOperatorStatement(RuleContext context) {
        return context instanceof DiceGrammarParser.DiceExpressionContext
            && context.getChildCount() == 3
            && context.getChild(1) instanceof DiceGrammarParser.OperatorContext;
    }
}
