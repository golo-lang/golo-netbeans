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
        if (positionedSequence != null) {
            Token<GoloTokenId> tokenValue = positionedSequence.token();
            if (tokenValue != null && positionedSequence.movePrevious()) {
                return positionedSequence.token();
            }
        }
        return null;
    }

    public static Token<GoloTokenId> getToken(BaseDocument doc, int offset) {
        TokenSequence<GoloTokenId> ts = getGoloTokenSequence(doc, offset);

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

            if (!ts.moveNext() && !ts.movePrevious()) {
                return null;
            }

            Token<GoloTokenId> token = ts.token();

            return token;
        }

        return null;
    }
    
    public static boolean isVariableOrConstantDeclaration(BaseDocument doc, int offset) {
        Token<GoloTokenId> previousToken = GoloLexerUtils.getPreviousToken(doc, offset);
        return isVariableOrConstantDeclarationToken(previousToken);
    }
    
    public static boolean isVariableOrConstantDeclarationToken(Token<GoloTokenId> token) {
        if (token != null) {
            return (token.id().ordinal() == GoloParserConstants.LET || token.id().ordinal() == GoloParserConstants.VAR);
        }
        return false;
    }

}
