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

import fr.insalyon.citi.golo.compiler.ir.GoloFunction;
import fr.insalyon.citi.golo.compiler.ir.GoloModule;
import fr.insalyon.citi.golo.compiler.parser.GoloASTNode;
import fr.insalyon.citi.golo.compiler.parser.GoloASTUtils;
import java.util.List;
import java.util.Set;
import org.gololang.netbeans.api.completion.util.CompletionContext;
import org.gololang.netbeans.parser.GoloParser;
import org.gololang.netbeans.structure.GoloParameterElementHandle;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.OffsetRange;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class ParameterCompletion {

    void complete(List<CompletionProposal> proposals, CompletionContext completionRequest, int anchor) {
        String filter = completionRequest.getPrefix();
        GoloModule module = ((GoloParser.GoloParserResult) completionRequest.getParserResult()).getModule();
        Set<GoloFunction> functions = module.getFunctions();
        BaseDocument doc = (BaseDocument) completionRequest.getParserResult().getSnapshot().getSource().getDocument(true);
        FileObject fo = completionRequest.getSourceFile();
        for (GoloFunction fn : functions) {
            if (!fn.getName().startsWith("__$$_") && fn.hasASTNode()) {
                GoloASTNode astNode = fn.getASTNode();
                OffsetRange range = GoloASTUtils.getRange(astNode, doc);
                if (range.containsInclusive(anchor)) {
                    List<String> parameterNames = fn.getParameterNames();
                    if (parameterNames != null && parameterNames.size() > 0) {
                        for (String parameterName : parameterNames) {
                            if (filter != null && parameterName.startsWith(filter)) {
                                proposals.add(new CompletionItem.SimpleParameterElementItem(new GoloParameterElementHandle(fo, module.getPackageAndClass().toString(), fn.getName(), parameterName), anchor));
                            }
                        }
                    }
                }

            }
        }

    }
}
