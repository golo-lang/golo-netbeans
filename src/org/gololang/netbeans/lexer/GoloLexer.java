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

import fr.insalyon.citi.golo.compiler.parser.GoloParserTokenManager;
import fr.insalyon.citi.golo.compiler.parser.Token;
import fr.insalyon.citi.golo.compiler.parser.TokenMgrError;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;


/**
 *
 * @author David Festal <david.festal@serli.com>
 */

class GoloLexer implements Lexer<GoloTokenId> {

    private LexerRestartInfo<GoloTokenId> info;
    private GoloParserTokenManager goloParserTokenManager;

    GoloLexer(LexerRestartInfo<GoloTokenId> info) {
        this.info = info;
        JavaCharStream stream = new JavaCharStream(info.input());
        goloParserTokenManager = new GoloParserTokenManager(stream);
    }

    @Override
    public org.netbeans.api.lexer.Token<GoloTokenId> nextToken() {
        Token token = goloParserTokenManager.getNextToken();
        if (info.input().readLength() < 1) {
            return null;
        }
        GoloTokenId tokenId = GoloLanguageHierarchy.getToken(token.kind);
        return info.tokenFactory().createToken( tokenId );     
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
    }

}