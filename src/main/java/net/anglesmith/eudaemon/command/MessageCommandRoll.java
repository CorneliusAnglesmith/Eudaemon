package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.command.dice.DiceGrammarErrorStrategy;
import net.anglesmith.eudaemon.command.dice.DiceGrammarLexer;
import net.anglesmith.eudaemon.command.dice.DiceGrammarParseTreeVisitor;
import net.anglesmith.eudaemon.command.dice.DiceGrammarParser;
import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.anglesmith.eudaemon.exception.EudaemonParsingException;
import net.anglesmith.eudaemon.message.Constants;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
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
        // FIXME - this should probably check the command for validity before parsing it...
        return true;
    }

    @Override
    public Message execute(MessageReceivedEvent messageEvent, List<String> messageTokens) throws EudaemonCommandException {
        final CommonTokenStream tokenStream = new CommonTokenStream(this.retrieveLexerForCommand(messageTokens));

        DiceGrammarParser parser = new DiceGrammarParser(tokenStream);

        parser.setErrorHandler(ERROR_STRATEGY);

        final ParseTreeVisitor<Integer> visitor = new DiceGrammarParseTreeVisitor();

        Integer result = 0;

        final MessageBuilder responseMessageBuilder = new MessageBuilder();

        responseMessageBuilder.append(messageEvent.getAuthor().getAsMention());
        responseMessageBuilder.append(" ");

        try {
            ParseTree tree = parser.diceExpression();

            result = visitor.visit(tree);

            responseMessageBuilder.append(String.valueOf(result));

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
            + "\t> @user 6\n"
            + "DESCRIPTION\n\tUse the Roll command with a typical tabletop dice roll expression to prompt the bot "
            + "to simulate a roll with those properties.  Supports basic arithmetic and parenthetical expressions.",
            "");

        return docMessageBuilder.build();
    }

    @Override
    public String invocationToken() {
        return CommandToken.COMMAND_ROLL.getCommandName();
    }
}
