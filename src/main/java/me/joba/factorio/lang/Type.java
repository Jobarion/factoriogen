package me.joba.factorio.lang;

import java.util.Locale;

public interface Type {

    int getSize();

    static Type parseType(LanguageParser.TypeContext ctx) {
        if(ctx.singleType != null) {
            return PrimitiveType.valueOf(ctx.singleType.getText().toUpperCase(Locale.ROOT));
        }
        else {
            throw new UnsupportedOperationException("Unsupported return type");
        }
    }
}
