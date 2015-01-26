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

package org.gololang.netbeans;

import org.gololang.netbeans.api.completion.CompletionHandler;
import org.gololang.netbeans.lexer.GoloTokenId;
import org.gololang.netbeans.parser.GoloParser;
import org.gololang.netbeans.structure.GoloStructureScanner;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.api.CodeCompletionHandler;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;

/**
 *
 * @author David Festal <david.festal@serli.com>
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

    @Override
    public CodeCompletionHandler getCompletionHandler() {
        return new CompletionHandler();
    }

    @Override
    public String getLineCommentPrefix() {
        return "#";
    }

}