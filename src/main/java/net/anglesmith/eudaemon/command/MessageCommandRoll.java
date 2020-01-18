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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class MessageCommandRoll implements MessageCommand {
    private DiceGrammarLexer lexer;

    private User user;

    private final ANTLRErrorStrategy errorStrategy = new DiceGrammarErrorStrategy();

    @Override
    public boolean accept(MessageReceivedEvent messageEvent, List<String> messageTokens) {
        boolean commandAccepted = true;

        final String joinedTokens = String.join(" ", messageTokens.subList(1, messageTokens.size()));

        Reader tokenReader = new StringReader(joinedTokens);

        CharStream charStream;

        try {
            charStream = CharStreams.fromReader(tokenReader);
            this.lexer = new DiceGrammarLexer(charStream);
            this.user = messageEvent.getAuthor();
        } catch (IOException ex) {
            commandAccepted = false;
        }

        return commandAccepted;
    }

    private long retrieveTokenValue() {
        return 0;
    }

    @Override
    public Message execute() throws EudaemonCommandException {
        CommonTokenStream tokenStream = new CommonTokenStream(this.lexer);

        DiceGrammarParser parser = new DiceGrammarParser(tokenStream);

        parser.setErrorHandler(this.errorStrategy);

        final ParseTreeVisitor<Integer> visitor = new DiceGrammarParseTreeVisitor();

        Integer result = 0;

        final MessageBuilder responseMessageBuilder = new MessageBuilder();

        responseMessageBuilder.append(this.user.getAsMention());
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
}
