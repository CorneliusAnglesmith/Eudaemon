package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.command.dice.DiceGrammarLexer;
import net.anglesmith.eudaemon.command.dice.DiceGrammarParser;
import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class MessageCommandRoll implements MessageCommand {
    private DiceGrammarLexer lexer;

    @Override
    public boolean accept(MessageReceivedEvent messageEvent, List<String> messageTokens) {
        boolean commandAccepted = true;

        final String joinedTokens = String.join(" ", messageTokens);

        Reader tokenReader = new StringReader(joinedTokens);

        CharStream charStream = null;

        try {
            charStream = CharStreams.fromReader(tokenReader);
            this.lexer = new DiceGrammarLexer(charStream);
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

        ParseTree tree = parser.diceExpression();

        final MessageBuilder responseMessageBuilder = new MessageBuilder();

        responseMessageBuilder.append(tree.getText());

        return responseMessageBuilder.build();
    }
}
