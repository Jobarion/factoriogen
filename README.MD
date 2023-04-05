# Factorio Combinator Compiler

### Syntax
See [examples](examples/)

### Types
The only supported types right now are:
- `int` a 32 bit integer, the same type that factorio uses for signals.
- `boolean` either true or false. 
- n-tuple. Tuples are a typed, fixed-size, ordered list of elements. E.g. `(int, boolean, (int, int))` is a tuple with 3 values. First, an `int`, second a `boolean`, third another tuple `(int, int)`. 
- `fixedp<N>` is a 32 bit binary fixed point number in the `Q(32-N).N` format (https://en.wikipedia.org/wiki/Q_(number_format))
- Arrays of types that are 32bit wide. I.e. `int[]` or `fixedp<N>[]`.

### Known issues
 - The return statement must the last statement in a function. Right now that's not enforced, but having multiple return statements, or returning in a `while` loop _will_ break things.
 - Function calls to void functions don't work correctly
 
## ToDo
- Lots of optimizations
- Named tuple types
- Better error messages
- Inlining