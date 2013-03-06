package com.serli.goloide;

import com.serli.goloide.structure.GoloStructureScanner;
import com.serli.goloide.lexer.GoloTokenId;
import com.serli.goloide.parser.GoloParser;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;

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
        return "Golo";
    }

    @Override
    public String getPreferredExtension() {
        return "golo";
    }
    
    @Override
    public Parser getParser() {
        return new GoloParser();
    }
    
    @Override
    public StructureScanner getStructureScanner() {
        return new GoloStructureScanner();
    }

    @Override
    public boolean hasStructureScanner() {
        return true;
    }
}