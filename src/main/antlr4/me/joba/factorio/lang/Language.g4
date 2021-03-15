grammar Language;

//declaration: Type assignment;
assignment: var=VarName '=' x=completeExpression;

completeExpression: expr;

expr
    : '(' wrapped=expr ')'
    | left=expr (op=MUL | op=DIV) right=expr
    | left=expr (op=ADD | op=SUB) right=expr
    | numberLit=IntLiteral
    | var=VarName ('(' vecAccessor=StringLiteral ')')?
    | 'sum(' sumExpr=expr ')'
    | 'count(' countExpr=expr ')'
    | NETWORK_IN
    ;

ifExpr: 'if' '(' ifCond=boolExpr ')' ifStatement=statement ('else' elseStatement=statement)?;
whileExpr: 'while' '(' loopCond=boolExpr ')' loopStatement=statement;

boolExprComponent
    : completeExpression
    | 'any(' anyExpr=completeExpression ')'
    | 'all(' allExpr=completeExpression ')';

boolExpr
    : '(' boolExpr ')'
    | NOT boolExpr
    | boolExpr (AND | OR | XOR) boolExpr
    | boolExprComponent (LT | GT | LEQ | GEQ | EQ | NEQ) boolExprComponent;

block: '{' blockStatement+ '}';

blockStatement: statement;

statement
    : block ';'
    | assignment ';'
    | ifExpr
    | whileExpr;

VarName: [a-zA-Z][a-zA-Z0-9_]*;
Type: 'int';
IntLiteral: '-'?[0-9]+;
StringLiteral: '"'[-a-z]'"';

ADD: '+';
SUB: '-';
MUL: '*';
DIV: '/';

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

WS : [ \t]+ -> skip ;
WS_OPT: (' ' | '\t')+;

