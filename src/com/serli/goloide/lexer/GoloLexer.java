package com.serli.goloide.lexer;

import fr.insalyon.citi.golo.compiler.parser.GoloParserTokenManager;
import fr.insalyon.citi.golo.compiler.parser.Token;
import fr.insalyon.citi.golo.compiler.parser.TokenMgrError;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;


/**
 *
 * @author david
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
      try {
        Token token = goloParserTokenManager.getNextToken();
        if (info.input().readLength() < 1) {
            return null;
        }
        return info.tokenFactory().createToken(GoloLanguageHierarchy.getToken(token.kind));
      }
      catch(TokenMgrError e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
    }

}