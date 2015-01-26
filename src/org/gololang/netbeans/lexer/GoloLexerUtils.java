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
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public final class GoloLexerUtils {

    public static TokenSequence<GoloTokenId> getPositionedSequence(BaseDocument doc, int offset) {
        return getPositionedSequence(doc, offset, true);
    }

    public static TokenSequence<GoloTokenId> getPositionedSequence(BaseDocument doc, int offset, boolean lookBack) {
        TokenSequence<GoloTokenId> ts = GoloLexerUtils.getGoloTokenSequence(doc, offset);

        if (ts != null) {
            try {
                ts.move(offset);
            } catch (AssertionError e) {
                DataObject dobj = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);

                if (dobj != null) {
                    Exceptions.attachMessage(e, FileUtil.getFileDisplayName(dobj.getPrimaryFile()));
                }

                throw e;
            }

            if (!lookBack && !ts.moveNext()) {
                return null;
            } else if (lookBack && !ts.moveNext() && !ts.movePrevious()) {
                return null;
            }

            return ts;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static TokenSequence<GoloTokenId> getGoloTokenSequence(Document doc, int offset) {
        final BaseDocument baseDocument = (BaseDocument) doc;
        try {
            baseDocument.readLock();
            return getGoloTokenSequence(TokenHierarchy.get(doc), offset);
        } finally {
            baseDocument.readUnlock();
        }
    }

    @SuppressWarnings("unchecked")
    public static TokenSequence<GoloTokenId> getGoloTokenSequence(TokenHierarchy<Document> th, int offset) {
        TokenSequence<GoloTokenId> ts = th.tokenSequence(GoloTokenId.getLanguage());
//        TokenSequence<?> ts = th.tokenSequence();
        if (ts == null) {
            List<TokenSequence<?>> list = th.embeddedTokenSequences(offset, true);

            for (TokenSequence t : list) {
                if (t.language().mimeType().equals(GoloTokenId.getLanguage().mimeType())) {
                    ts = t;

                    break;
                }
            }

            if (ts == null) {
                list = th.embeddedTokenSequences(offset, false);

                for (TokenSequence t : list) {
                    if (t.language().mimeType().equals(GoloTokenId.getLanguage().mimeType())) {
                        ts = t;

                        break;
                    }
                }
            }
        }

        return ts;
    }

    
    public static Token<GoloTokenId> getPreviousToken(BaseDocument doc, int offset) {
        TokenSequence<GoloTokenId> positionedSequence = GoloLexerUtils.getPositionedSequence(doc, offset);
        return getPreviousToken(positionedSequence);
    }

    public static Token<GoloTokenId> getPreviousToken(TokenSequence<GoloTokenId> tokenSequence) {
        if (tokenSequence != null) {
            Token<GoloTokenId> tokenValue = tokenSequence.token();
            if (tokenValue != null && tokenSequence.movePrevious()) {
                return tokenSequence.token();
            }
        }
        return null;
    }
    
    public static Token<GoloTokenId> getNextToken(BaseDocument doc, int offset) {
        TokenSequence<GoloTokenId> positionedSequence = GoloLexerUtils.getPositionedSequence(doc, offset);
        return getNextToken(positionedSequence);
    }

    public static Token<GoloTokenId> getNextToken(TokenSequence<GoloTokenId> tokenSequence) {
        if (tokenSequence != null) {
            Token<GoloTokenId> tokenValue = tokenSequence.token();
            if (tokenValue != null && tokenSequence.moveNext()) {
                return tokenSequence.token();
            }
        }
        return null;
    }
    
    public static boolean isVariableOrConstantDeclarationToken(Token<GoloTokenId> token) {
        if (token != null) {
            return (token.id().ordinal() == GoloParserConstants.LET || token.id().ordinal() == GoloParserConstants.VAR);
        }
        return false;
    }
    
    public static boolean isInCategories(Token<GoloTokenId> token, List<String> categories) {
        if (token != null) {
            return categories.contains(token.id().primaryCategory());
        }
        return false;
    }

    public static boolean isOfType(Token<GoloTokenId> token, List<Integer> tokenOrdinals) {
        if (token != null) {
            return tokenOrdinals.contains(token.id().ordinal());
        }
        return false;
    }
    
    
    public static boolean isJustAfterTokenOfType(TokenSequence<GoloTokenId> sequence, List<Integer> tokenOrdinals) {
        Token<GoloTokenId> previousToken = GoloLexerUtils.getPreviousToken(sequence);
        return isOfType(previousToken, tokenOrdinals);
    }
    
}
