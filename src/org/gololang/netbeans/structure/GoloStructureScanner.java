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

package org.gololang.netbeans.structure;

import fr.insalyon.citi.golo.compiler.parser.GoloParserConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.Document;
import org.gololang.netbeans.lexer.GoloTokenId;
import org.gololang.netbeans.parser.GoloParser;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;

/**
 *
 * @author David Festal <david.festal@serli.com>
 */
public class GoloStructureScanner implements StructureScanner {

    public GoloStructureScanner() {
    }
    

    @Override
    public List<? extends StructureItem> scan(ParserResult pr) {
      List<? extends StructureItem>  list = new ArrayList<>();
      GenerateStructureVisitor visitor = new GenerateStructureVisitor(pr.getSnapshot().getSource());
      GoloParser.GoloParserResult result = (GoloParser.GoloParserResult) pr;
      visitor.visit(result.getCompilationUnit(), list);
      return list;
    }

    @Override
    public Map<String, List<OffsetRange>> folds(ParserResult pr) {
        Map<String, List<OffsetRange>> folds = new HashMap<>();
        
        // Comments
        if (pr instanceof GoloParser.GoloParserResult) {
            
            final Document doc = pr.getSnapshot().getSource().getDocument(true);
            ((BaseDocument)doc).readLock();
            TokenHierarchy<Document> hi = TokenHierarchy.get(doc);
            try {
            TokenSequence<GoloTokenId> ts = (TokenSequence<GoloTokenId>) hi.tokenSequence();
            int startComment = -1;
            int stopComment = -1;
            int offset = 0;
            GoloTokenId lastTokenId = null;
            boolean initialComment = true;
            while (ts.moveNext()) {
                offset = ts.offset();
                Token<GoloTokenId> token = ts.token();
                GoloTokenId id = token.id();
                if (id.ordinal() == GoloParserConstants.COMMENT) {
                    if (lastTokenId == null 
                            || lastTokenId.ordinal() != GoloParserConstants.COMMENT) {
                        startComment = offset;
                    }
                    else {
                        stopComment = offset + token.length() - 1;
                    }

                }
                else {
                    if (startComment >= 0 && stopComment >= 0) {
                        String foldTypeName = initialComment ? "initial-comment" : "comments";
                        List<OffsetRange> commentsFolds = folds.get(foldTypeName);
                        if (commentsFolds == null) {
                            commentsFolds = new ArrayList<>();
                            folds.put(foldTypeName, commentsFolds);
                        }
                        commentsFolds.add(new OffsetRange(startComment + 1, stopComment));
                            
                        startComment = -1;
                        stopComment = -1;
                        initialComment = false;
                    }
                    
                    if (id.ordinal() != GoloParserConstants.NEWLINE) {
                        initialComment = false;
                    }
                }
                lastTokenId = id;
            }
            } finally {
                ((BaseDocument)doc).readUnlock();
            }
            
            // Code blocks and imports
            GoloParser.GoloParserResult result = (GoloParser.GoloParserResult) pr;
            GenerateFoldsVisitor visitor = new GenerateFoldsVisitor();
            visitor.visit(result.getCompilationUnit(), folds);
        }
        return folds;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }
        
}
