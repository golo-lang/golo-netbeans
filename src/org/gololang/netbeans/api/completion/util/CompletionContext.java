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
package org.gololang.netbeans.api.completion.util;

import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.spi.ParserResult;
import org.openide.filesystems.FileObject;

/**
 * 
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class CompletionContext {
    
     private final ParserResult parserResult;
    private final FileObject sourceFile;
    
    private final String prefix;
    private final int anchor;
    
    public final int lexOffset;
    public final BaseDocument doc;
   
    
    public CompletionContext(
            ParserResult parseResult,
            String prefix,
            int anchor,
            int lexOffset,
            BaseDocument doc) {

        this.parserResult = parseResult;
        this.sourceFile = parseResult.getSnapshot().getSource().getFileObject();
        this.prefix = prefix;
        this.anchor = anchor;
        this.lexOffset = lexOffset;
        this.doc = doc;
        
    }

    public String getPrefix() {
        return prefix;
    }

    public int getAnchor() {
        return anchor;
    }

    public ParserResult getParserResult() {
        return parserResult;
    }

    public FileObject getSourceFile() {
        return sourceFile;
    }
    
    
    
}
