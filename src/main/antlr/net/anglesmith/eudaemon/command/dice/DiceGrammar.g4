grammar DiceGrammar;

@header {
    package net.anglesmith.eudaemon.command.dice;
}

fragment LITERAL_DICE : ([0-9]*[dD][0-9]+) ;

fragment LITERAL_NUMBER : [0-9]+ ;

fragment WHITESPACE : [ \r\t\n]+ -> skip ;

operatorPlus : '+' ;
operatorMinus : '-' ;
operatorMultiply : '*' ;
operatorDivide : '/' ;

statementDivRight : ')';

statementDivLeft : '(';

diceValue : LITERAL_DICE | LITERAL_NUMBER ;

diceStatementCapture : statementDivLeft diceStatement statementDivRight ;

diceOperation : diceOperationPlus
    | diceOperationMinus
    | diceOperationMultiply
    | diceOperationDivide;

diceOperationPlus : diceStatement operatorPlus diceStatement ;
diceOperationMinus : diceStatement operatorMinus diceStatement ;
diceOperationMultiply : diceStatement operatorMultiply diceStatement ;
diceOperationDivide : diceStatement operatorDivide diceStatement ;

diceStatement : diceValue
    | diceStatementCapture ;

diceExpression : diceStatement
    | diceOperation ;



