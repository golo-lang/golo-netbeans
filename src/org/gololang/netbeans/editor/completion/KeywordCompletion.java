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

import fr.insalyon.citi.golo.compiler.ir.CollectionLiteral;
import fr.insalyon.citi.golo.compiler.parser.GoloParserConstants;
import org.gololang.netbeans.api.completion.util.CompletionContext;
import java.util.Collection;
import java.util.List;
import org.gololang.netbeans.lexer.GoloLanguageHierarchy;
import org.gololang.netbeans.lexer.GoloTokenId;
import org.netbeans.modules.csl.api.CompletionProposal;

/**
 * 
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class KeywordCompletion {

    void complete(List<CompletionProposal> proposals, CompletionContext completionRequest, int anchor) {
        String filter = completionRequest.getPrefix();
        Collection<GoloTokenId> tokens = GoloLanguageHierarchy.getTokens();
        for (GoloTokenId token : tokens) {
            if (GoloLanguageHierarchy.KEYWORD_CATGEORY.equalsIgnoreCase(token.primaryCategory())) {
                if (token.ordinal() == GoloParserConstants.COLL_START) {
                    CollectionLiteral.Type[] values = CollectionLiteral.Type.values();
                    for (CollectionLiteral.Type value : values) {
                        if (value.toString().toLowerCase().startsWith(filter.toLowerCase())) {
                            proposals.add(new CompletionItem.KeywordItem(value.toString().toLowerCase(), token.ordinal(), null, anchor, completionRequest.getParserResult()));
                        }
                    }
                } else if (token.name().toLowerCase().startsWith(filter.toLowerCase())) {
                    proposals.add(new CompletionItem.KeywordItem(token.name().toLowerCase(), token.ordinal(), null, anchor, completionRequest.getParserResult()));
                }
            }
        }
    }
    

}
