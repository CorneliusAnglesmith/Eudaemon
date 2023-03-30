package net.anglesmith.eudaemon.command.dice;

public class DiceResultAggregator {
    private int value;

    private StringBuilder lexicalValue;

    public DiceResultAggregator() {
        this.value = 0;
        this.lexicalValue = new StringBuilder();
    }

    public DiceResultAggregator(int value, String lexicalValue) {
        this.value = value;
        this.lexicalValue = new StringBuilder(lexicalValue);
    }

    public int getValue() {
        return value;
    }

    public String getLexicalValue() {
        return lexicalValue.toString();
    }

    public DiceResultAggregator join(DiceResultAggregator other) {
        if (other != null) {
            this.value += other.value;
            this.lexicalValue.append(other.lexicalValue.toString());
        }

        return this;
    }

    public DiceResultAggregator addValue(int value) {
        this.value += value;
        return this;
    }

    public DiceResultAggregator appendLexicalValue(String value) {
        this.lexicalValue.append(value);
        return this;
    }
}
