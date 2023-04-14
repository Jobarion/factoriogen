grammar Language;

file: arrayDeclaration* function+;

function
    : functionHeader block
    | functionHeader ';';

functionHeader: 'function ' functionModifiers? name=functionName '(' functionParams ')' '->' returnType=type;
functionModifiers
    :
    | '[' functionModifier (',' functionModifier)* ']';

functionModifier
    : key='native'
    | key='fixed_delay' ('=' intLiteral)?;

functionParams: (functionParam (',' functionParam)*)?;
functionParam: varName ':' type ('<' signalName '>')?;

block: '{' statement+ '}';

statement
    : block
    | assignment ';'
    | arrayAssignment ';'
    | ifExpr
    | whileExpr
    | returnStatement ';'
    | functionCall ';'
    ;

assignment: var=varName '=' x=expr;
arrayAssignment: array=expr '[' index=expr ']' '=' x=expr;

ifExpr: 'if' '(' ifCond=boolExpr ')' ifPart=ifStatement ('else' elsePart=elseStatement)?;
elseStatement: statement;
ifStatement: statement;

whileExpr: 'while' '(' loopCond=boolExpr ')' loopStatement=loopBody;
loopBody: statement;

returnStatement: 'return ' expr;

functionCall: functionName '(' argumentList ')';
argumentList: (expr (',' expr)*)?;

expr
    : '(' wrapped=expr ')'
    | '(' tupleValues=exprList ')'
    | var=varName
    | array=expr '[' index=expr ']'
    | call=functionCall
    | tuple=expr (op=ACCESS) propertyId=intLiteral
    | left=expr (op=MUL | op=DIV | op=MOD) (targetFractBits=fractBits)? right=expr
    | left=expr (op=ADD | op=SUB) (targetFractBits=fractBits)? right=expr
    | left=expr (op=LSH | op=RSH) (targetFractBits=fractBits)? right=expr
    | left=expr op=BAND right=expr
    | left=expr op=BOR right=expr
    | left=expr op=BXOR right=expr
    | typeCast
    | simpleExpr
    ;

simpleExpr
    : fixedpLit=fixedpLiteral
    | numberLit=intLiteral
    | boolLit=boolLiteral
    ;

exprList: expr (',' expr)+;

boolExpr
    : '(' boolExpr ')'
    | NOT negated=boolExpr
    | left=boolExpr op=(AND | OR | XOR) right=boolExpr
    | leftComponent=expr op=(LT | GT | LEQ | GEQ | EQ | NEQ) rightComponent=expr;

type
    : singleType=TypeName
    | 'fixedp' fractBits
    | '(' typeList ')'
    | arrayType=type '[]'
    ;

fractBits: '<' fracbits=intLiteral '>';

typeList: type (',' type)+;

typeCast: '(' targetType=type ')' expr;

arrayDeclaration: type '[' intLiteral ']' varName ';';

fixedpLiteral: decimalLiteral '.' NumberCharacter+;
intLiteral
          : decimal=decimalLiteral
          | hex=hexLiteral
          ;
decimalLiteral: '-'? NumberCharacter+;
hexLiteral: '-'? HexCharacter+;
boolLiteral: BoolLiteral;
varName: NameCharacterFirst (NameCharacterFirst|NameCharacterRest|NumberCharacter)*;
functionName: NameCharacterFirst (NameCharacterFirst|NameCharacterRest|NumberCharacter)*;
signalName: (NameCharacterFirst|NameCharacterRest|NumberCharacter)+;

NameCharacterFirst: [a-zA-Z];
NameCharacterRest: [A-Z_];
NumberCharacter: [0-9];
HexCharacter: [0-9a-fA-F];
TypeName: 'int'|'boolean'|'void';
BoolLiteral: 'true'|'false';

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
ACCESS: '.';

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

WS : [ \t\n\r]+ -> skip ;
