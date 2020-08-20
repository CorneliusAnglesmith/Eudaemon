package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.command.dice.DiceAggregatingParseTreeVisitor;
import net.anglesmith.eudaemon.command.dice.DiceGrammarErrorStrategy;
import net.anglesmith.eudaemon.command.dice.DiceGrammarLexer;
import net.anglesmith.eudaemon.command.dice.DiceGrammarParser;
import net.anglesmith.eudaemon.command.dice.DiceResultAggregator;
import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.anglesmith.eudaemon.exception.EudaemonParsingException;
import net.anglesmith.eudaemon.message.Constants;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

@Component
public class MessageCommandRoll implements MessageCommand {
    private final ANTLRErrorStrategy ERROR_STRATEGY = new DiceGrammarErrorStrategy();

    @Override
    public boolean validate(MessageReceivedEvent messageEvent, List<String> messageTokens) {
        return messageTokens.stream().anyMatch(token -> {
            if (token.matches("[0-9]+[dD][0-9]+")) {
                final String[] tokens = StringUtils.split(token, "dD");
                return !(StringUtils.isNumeric(tokens[0])
                    && Integer.parseInt(tokens[0]) > DiceAggregatingParseTreeVisitor.MAX_DICE_COUNT);
            }
            return true;
        });
    }

    @Override
    public Message execute(MessageReceivedEvent messageEvent, List<String> messageTokens) throws EudaemonCommandException {
        final CommonTokenStream tokenStream = new CommonTokenStream(this.retrieveLexerForCommand(messageTokens));

        DiceGrammarParser parser = new DiceGrammarParser(tokenStream);

        parser.setErrorHandler(ERROR_STRATEGY);

        final ParseTreeVisitor<DiceResultAggregator> visitor = new DiceAggregatingParseTreeVisitor();

        final MessageBuilder responseMessageBuilder = new MessageBuilder();

        try {
            ParseTree tree = parser.diceExpression();

            final DiceResultAggregator result = visitor.visit(tree);
            responseMessageBuilder.appendCodeBlock(
                "Result: " + result.getValue() + "\n\nTrace:\n" + result.getLexicalValue(), "");

        } catch (ParseCancellationException e) {
            if (e.getCause() instanceof RecognitionException) {
                responseMessageBuilder.append("That roll looks invalid.  Check your spelling?");
            } else {
                throw new EudaemonCommandException("Input cannot be parsed as a dice expression.", e);
            }
        } catch (EudaemonParsingException e) {
            throw new EudaemonCommandException("Problem while interpreting user dice roll input.", e);
        }

        return responseMessageBuilder.build();
    }

    private DiceGrammarLexer retrieveLexerForCommand(List<String> messageTokens) throws EudaemonCommandException {
        final DiceGrammarLexer lexer;
        final Reader tokenReader = new StringReader(
            String.join(" ", messageTokens.subList(1, messageTokens.size())));

        try {
            lexer = new DiceGrammarLexer(CharStreams.fromReader(tokenReader));
        } catch (IOException ex) {
            throw new EudaemonCommandException("Could not open character stream.");
        }

        return lexer;
    }

    @Override
    public Message documentation() {
        final MessageBuilder docMessageBuilder = new MessageBuilder();
        final String invokeExpression = Constants.COMMAND_INVOCATION_TOKEN + " " + CommandToken.COMMAND_ROLL.getCommandName();

        docMessageBuilder.appendCodeBlock(
            "Dice roll command.\n\n"
            + "SYNOPSIS\n\t" + invokeExpression + " [dice roll expression]\n"
            + "EXAMPLE\n\t" + invokeExpression + " 1d8 + 1\n"
            + "DESCRIPTION\n\tUse the Roll command with a typical tabletop dice roll expression to prompt the bot "
            + "to simulate a roll with those properties.  Supports basic arithmetic and parenthetical expressions."
            + "\n\n\tOnly supports 50 simultaneous dice rolls in a single statement (e.g., 50d6 + 12d8 is fine, but "
            + "100d6 is not).  Operation order is determined left-to-right, not by precedence.  Use parentheses if "
            + "operation order matters for your roll.",
            "");

        return docMessageBuilder.build();
    }

    @Override
    public String invocationToken() {
        return CommandToken.COMMAND_ROLL.getCommandName();
    }
}
