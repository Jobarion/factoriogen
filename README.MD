# Factorio Combinator Compiler

The Factorio Combinator Compiler (FCC) is 

## Language
Every FCL program starts 

### Expressions
An expression is either
- A variable (in `tmp = a`, `a` is an expression).
- A literal (`4`, `-152`)  
- A mathematical expression (e.g. `a + (b / -4)`). Supported operators: `+`,`-`,`*`,`/`,`%`,`<<`,`>>`,`|`,`&`,`^`
- A boolean expression (e.g. `a && !(b || c)`). Supported operators: `&&`, `||`, `^`, `!`
- A function call (e.g. `max(a, b)`)

For a more formal definition, please take a look at the [grammar](src/main/antlr4/me/joba/factorio/lang/Language.g4).

### Variables
To create a variable, simply assign an expression to a name: `a = 3;`, `a = b * 2;`, `a = max(b, c);`.
The variables will infer the type based on the expression on the right side. In contrast to many other languages, assignments are _not_ expressions. `a = b = 1` is illegal.

### Conditionals
Conditionals are structured like this:

    if(<condition>) <if block>
    else <else block> //optional

For example:

    if(a < 3) {
        if(a < 0) {
            a = -a;
        }
    }
    else {
        b = 5;
    }

The `if` is mandatory, the `else` part is optional. `else if` is not supported (yet), nested conditionals must be used instead.

### Loops
At the moment only `while` loops are supported:

    while(<condition>) <while block>

For example:

    sum = 0;
    i = 100;
    while(i > 0) {
        sum = sum + i;
        i = i - 1;
    }


### Functions
Every function consists of a function signature and a function body.

In this example function header `function add(a: int<red>, b: int) -> int`
1. `function` is a reserved keyword.
2. `add` is the function name.
3. `(a: int<red>, b: int)` is a list of two function parameters.
    1. `a: int<red>` defines the first function parameter
        1. `a` is the name.
        2. `int` is the type.
        3. `<blue>` specifies the signal that is used in Factorio for this parameter.
    2. `b: int` defines the second function parameter
        1. `b` is the name.
        2. `int` is the type.
        3. In this case the signal that is used in Factorio to represent this parameter is not specified. The FCC will use a random available signal.
4. `-> int` specifies the return type.

Functions must not call each other recursively (Function calls are a directed acyclic graph). The compiler will not prevent recursive calls, but if a recursive call is performed, stuff will break.

### Types
The only supported types right now are:
- `int` a 32 bit integer, the same type that factorio uses for signals.
- `boolean` either true or false. 

## ToDo
- Support more types: structs/tuples, arrays