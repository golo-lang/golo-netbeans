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

import org.gololang.netbeans.api.completion.util.CompletionContext;
import fr.insalyon.citi.golo.compiler.ir.GoloFunction;
import java.util.List;
import java.util.Set;
import org.gololang.netbeans.parser.GoloParser;
import org.netbeans.modules.csl.api.CompletionProposal;

/**
 * 
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class FunctionCompletion {

    void complete(List<CompletionProposal> proposals, CompletionContext completionRequest, int anchor) {
        String filter = completionRequest.getPrefix();
        Set<GoloFunction> functions = ((GoloParser.GoloParserResult)completionRequest.getParserResult()).getModule().getFunctions();
        for (GoloFunction fn : functions) {
            if (!fn.getName().startsWith("__$$_")) {
                if (filter != null && fn.getName().startsWith(filter)) {
                    proposals.add(new CompletionItem.FunctionItem(fn, anchor, completionRequest.getParserResult()));
                }
            }
        }

    }

}
