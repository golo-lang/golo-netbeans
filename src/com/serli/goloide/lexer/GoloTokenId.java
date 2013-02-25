package com.serli.goloide.lexer;

/**
 *
 * @author david
 */

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

public class GoloTokenId implements TokenId {

    private final String name;
    private final String primaryCategory;
    private final int id;

    public static Language<GoloTokenId> getLanguage() {
        return new GoloLanguageHierarchy().language();
    }
    
    GoloTokenId(
            String name,
            String primaryCategory,
            int id) {
        this.name = name;
        this.primaryCategory = primaryCategory;
        this.id = id;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory;
    }

    @Override
    public int ordinal() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

}