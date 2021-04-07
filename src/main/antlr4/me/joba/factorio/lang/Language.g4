grammar Language;

file: function+;

function: functionHeader block;
functionHeader: 'function ' name=functionName '(' functionParams ')' '->' returnType=type;
functionParams: functionParam (',' functionParam)*;
functionParam: varName ':' type ('<' signalName '>')?;

block: '{' statement+ '}';

statement
    : block
    | assignment ';'
    | ifExpr
    | whileExpr
    | returnStatement ';'
    ;

assignment: var=varName '=' x=expr;

ifExpr: 'if' '(' ifCond=boolExpr ')' ifPart=ifStatement ('else' elsePart=elseStatement)?;
elseStatement: statement;
ifStatement: statement;

whileExpr: 'while' '(' loopCond=boolExpr ')' loopStatement=loopBody;
loopBody: statement;

returnStatement: 'return' '(' returnValues ')';
returnValues: expr (',' expr)*;

functionCall: functionName '(' argumentList ')';
argumentList: expr (',' expr)*;

expr
    : '(' wrapped=expr ')'
    | left=expr (op=MUL | op=DIV | op=MOD) right=expr
    | left=expr (op=ADD | op=SUB) right=expr
    | left=expr (op=LSH | op=RSH) right=expr
    | left=expr op=BAND right=expr
    | left=expr op=BOR right=expr
    | left=expr op=BXOR right=expr
    | numberLit=intLiteral
    | call=functionCall
    | var=varName
    | NETWORK_IN
    ;

boolExpr
    : '(' boolExpr ')'
    | NOT negated=boolExpr
    | left=boolExpr op=(AND | OR | XOR) right=boolExpr
    | leftComponent=expr op=(LT | GT | LEQ | GEQ | EQ | NEQ) rightComponent=expr;

type
    : singleType=TypeName
    | '(' typeList ')'
    ;
typeList: type (',' type)+;

intLiteral: '-'? NumberCharacter+;
varName: NameCharacterFirst (NameCharacterFirst|NameCharacterRest|NumberCharacter)*;
functionName: NameCharacterFirst (NameCharacterFirst|NameCharacterRest|NumberCharacter)*;
signalName: (NameCharacterFirst|NameCharacterRest|NumberCharacter)+;

NameCharacterFirst: [a-zA-Z];
NameCharacterRest: [A-Z_];
NumberCharacter: [0-9];
TypeName: 'int'|'boolean';

ADD: '+';
SUB: '-';
MUL: '*';
DIV: '/';
MOD: '%';
LSH: '>>';
RSH: '<<';
BAND: '&';
BOR: '|';
BXOR: '^';

AND: '&&';
OR: '||';
XOR: '^';
NOT: '!';

LT: '<';
GT: '>';
LEQ: '<=';
GEQ: '>=';
EQ: '==';
NEQ: '!=';

NETWORK_IN: 'IN';

WS : [ \t\n\r]+ -> skip ;
