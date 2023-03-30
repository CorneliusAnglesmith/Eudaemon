grammar DiceGrammar;

@header {
    package net.anglesmith.eudaemon.command.dice;
}

LITERAL_DICE : WHITESPACE? DIGIT* DICE_SEPARATOR DIGIT+ ;

LITERAL_NUMBER : WHITESPACE? DIGIT+ ;

DIGIT : [0-9];

DICE_SEPARATOR : [dD];

WHITESPACE : [ \r\t\n]+;

operatorPlus : WHITESPACE? '+' ;
operatorMinus : WHITESPACE? '-' ;
operatorMultiply : WHITESPACE? '*' ;
operatorDivide : WHITESPACE? '/' ;

statementDivRight : WHITESPACE? ')';

statementDivLeft : WHITESPACE? '(';

diceValue : LITERAL_DICE | LITERAL_NUMBER ;

operator : operatorPlus
    | operatorMinus
    | operatorMultiply
    | operatorDivide;

diceExpression : WHITESPACE? diceValue
    | statementDivLeft diceExpression statementDivRight
    | diceExpression operator diceExpression;
