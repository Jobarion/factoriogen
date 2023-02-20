package me.joba.factorio.lang.types;

import me.joba.factorio.lang.LanguageParser;

import java.util.Locale;

public interface Type {

    int getSize();

    static Type parseType(LanguageParser.TypeContext ctx) {
        if(ctx.singleType != null) {
            return PrimitiveType.valueOf(ctx.singleType.getText().toUpperCase(Locale.ROOT));
        }
        else if(ctx.fracbits != null) {
            return new FixedpType(Integer.parseInt(ctx.fracbits.getText()));
        }
        else if(ctx.typeList() != null) {
            Type[] types = new Type[ctx.typeList().type().size()];
            int i = 0;
            for(var tc : ctx.typeList().type()) {
                types[i++] = parseType(tc);
            }
            return new TupleType(types);
        }
        else if(ctx.arrayType != null) {
            return new ArrayType(parseType(ctx.arrayType));
        }
        else {
            throw new UnsupportedOperationException("Unsupported return type");
        }
    }
}
