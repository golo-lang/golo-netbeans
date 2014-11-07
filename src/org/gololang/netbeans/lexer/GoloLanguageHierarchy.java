/*
 *  Copyright 2013 SERLI (www.serli.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */

package org.gololang.netbeans.lexer;

import fr.insalyon.citi.golo.compiler.parser.GoloParserConstants;
import java.lang.reflect.Field;
import java.util.*;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.openide.util.Exceptions;

/**
 *
 * @author David Festal <david.festal@serli.com>
 */

public class GoloLanguageHierarchy extends LanguageHierarchy<GoloTokenId> {

    public static final String KEYWORD_CATGEORY = "keyword";
    public static final String IDENTIFIER_CATEGORY = "identifier";
    
    private static Collection<GoloTokenId> tokens;
    private static TreeMap<Integer, GoloTokenId> idToToken;

    private static void init() {
        ArrayList<GoloTokenId> theTokens = new ArrayList<GoloTokenId>();
        for (Field field : GoloParserConstants.class.getFields()) {
            if (! field.getType().equals(Integer.TYPE)) {
                continue;
            }

            if (field.getName().equals("DEFAULT") ||
                    field.getName().equals("ESCAPED")) {
                continue;
            }
            
            String category;
            switch (field.getName()) {
                case "EOF" : 
                case "NEWLINE" : 
                    category = "whitespace";
                    break;
                    
                case "ESCAPE" :
                case "MODULE" :
                case "IMPORT" : 
                case "FUNCTION" : 
                case "LOCAL" :
                case "RETURN" :
                case "IF" :
                case "ELSE" :
                case "WHILE" :
                case "FOR" :
                case "FOREACH" :
                case "IN" :
                case "THROW" :
                case "TRY" :
                case "CATCH" :
                case "FINALLY" :
                case "CASE" :
                case "WHEN" :
                case "MATCH" :
                case "THEN" :
                case "OTHERWISE" :
                case "AUGMENT" :
                case "COLL_START" :                
                case "NULL" :
                case "BREAK" :
                case "CONTINUE" :
                case "STRUCT" :
                case "INVOCATION" : 
                case "DECORATOR" :
                case "TRUE" :
                case "FALSE" :
                case "VAR" :
                case "LET" :
                    category = KEYWORD_CATGEORY;
                    break;

                case "NUMBER" :
                case "LONG_NUMBER" : 
                case "FLOATING_NUMBER" :
                case "FLOAT" : 
                    category = "number";
                    break;

                case "IDENTIFIER" :
                case "FUNREF" :
                case "CLASSREF" :
                    category = IDENTIFIER_CATEGORY;
                    break;

                case "ASSOCIATIVE_OPERATOR" :
                case "COMMUTATIVE_OPERATOR" : 
                case "UNARY_OPERATOR" : 
                    category = "operator";
                    break;

                case "MULTI_STRING" :                    
                case "STRING" :
                    category = "string";
                    break;

                case "LETTER" :
                case "ID_REST" :
                case "CHAR" :
                    category = "character";
                    break;

                case "DOCUMENTATION" :
                case "COMMENT" :
                    category = "comment";
                    break;

                default :
                    category = "defaut";
                    break;
            }
            try {
                theTokens.add(new GoloTokenId(field.getName(), category, field.getInt(null)));
            } catch (IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            }
        }    

        idToToken = new TreeMap<Integer, GoloTokenId>();
        for (GoloTokenId token : theTokens) {
            idToToken.put(token.ordinal(), token);
        }
        
        int index = 0;
        for (String tokenImageElement : GoloParserConstants.tokenImage) {
            Integer indexObject = new Integer(index);
            if (! idToToken.containsKey(indexObject)) {
                String category = "default";
                if (index >= 1 && index <= 5) {
                    category = "whitespace";
                }
                idToToken.put(indexObject, new GoloTokenId(GoloParserConstants.tokenImage[index], category, index));
            }
            index ++;
        }
        
        tokens = idToToken.values();
        
    }

    static synchronized GoloTokenId getToken(int id) {
        if (idToToken == null) {
            init();
        }
        return idToToken.get(id);
    }

    @Override
    protected synchronized Collection<GoloTokenId> createTokenIds() {
        return getTokens();
    }

    public static synchronized Collection<GoloTokenId> getTokens() {
        if (tokens == null) {
            init();
        }
        return tokens;
    }

    
    
    @Override
    protected synchronized Lexer<GoloTokenId> createLexer(LexerRestartInfo<GoloTokenId> info) {
        return new GoloLexer(info);
    }

    @Override
    protected String mimeType() {
        return "text/x-golo";
    }

}