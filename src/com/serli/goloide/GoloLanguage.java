package com.serli.goloide;

import com.serli.goloide.lexer.GoloTokenId;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;

/**
 *
 * @author david
 */

@LanguageRegistration(mimeType = "text/x-golo")
public class GoloLanguage extends DefaultLanguageConfig {

    @Override
    public Language getLexerLanguage() {
        return GoloTokenId.getLanguage();
    }

    @Override
    public String getDisplayName() {
        return "SJ";
    }

}