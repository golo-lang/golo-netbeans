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
package org.gololang.netbeans.editor.completion;

import fr.insalyon.citi.golo.compiler.parser.GoloParserConstants;
import java.util.Arrays;
import java.util.List;
import org.gololang.netbeans.api.completion.util.CompletionContext;
import org.gololang.netbeans.lexer.GoloLexerUtils;
import org.gololang.netbeans.lexer.GoloTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.CompletionProposal;

/**
 *
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class DocumentationCompletion {

    void complete(List<CompletionProposal> proposals, CompletionContext completionRequest, int anchor) {
        BaseDocument doc = (BaseDocument) completionRequest.getParserResult().getSnapshot().getSource().getDocument(true);
        if (!completionRequest.isAnchorInFunction()) {
            // documentation blocks are on modules, functions, augmentations and structs
            Token<GoloTokenId> nextToken = GoloLexerUtils.getNextToken(doc, completionRequest.lexOffset);
            if (GoloLexerUtils.isOfType(nextToken, Arrays.asList(GoloParserConstants.FUNCTION, GoloParserConstants.MODULE, GoloParserConstants.AUGMENT, GoloParserConstants.STRUCT, GoloParserConstants.LOCAL))) {
                proposals.add(new CompletionItem.DocumentationItem(nextToken, anchor));
            }
        }
    }


}
