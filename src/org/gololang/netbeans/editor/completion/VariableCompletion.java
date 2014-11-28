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
import fr.insalyon.citi.golo.compiler.parser.ASTLetOrVar;
import fr.insalyon.citi.golo.compiler.parser.GoloASTNode;
import fr.insalyon.citi.golo.compiler.parser.GoloASTUtils;
import fr.insalyon.citi.golo.compiler.parser.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.gololang.netbeans.api.completion.util.CompletionContext;
import org.gololang.netbeans.parser.GoloParser;
import org.gololang.netbeans.structure.VariableElementHandle;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.parsing.api.Source;

/**
 *
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class VariableCompletion {

    void complete(List<CompletionProposal> proposals, CompletionContext completionRequest, int anchor) {
        String filter = completionRequest.getPrefix();
        GoloModule module = ((GoloParser.GoloParserResult) completionRequest.getParserResult()).getModule();
        Set<GoloFunction> functions = module.getFunctions();
        Source source = completionRequest.getParserResult().getSnapshot().getSource();
        BaseDocument doc = (BaseDocument) source.getDocument(true);
        for (GoloFunction fn : functions) {
            if (!fn.getName().startsWith("__$$_")) {
                GoloASTNode astNode = fn.getASTNode();
                OffsetRange range = GoloASTUtils.getRange(astNode, doc);
                if (range.containsInclusive(anchor)) {
                    List<ASTLetOrVar> variablesNodes = getVariablesNodes(astNode);
                    for (ASTLetOrVar variablesNode : variablesNodes) {
                        OffsetRange rangeVarLet = GoloASTUtils.getRange(variablesNode, doc);
                        if (rangeVarLet.getEnd() <= anchor) {
                            if (filter != null && variablesNode.getName().startsWith(filter)) {
                                proposals.add(new CompletionItem.VariableElementItem(new VariableElementHandle(variablesNode, source), anchor, module.getPackageAndClass().toString(), fn.getName()));
                            }
                        }
                    }
                }

            }
        }

    }

    private List<ASTLetOrVar> getVariablesNodes(Node node) {
        List<ASTLetOrVar> variablesNodes = new ArrayList<>();
        int numChildren = node.jjtGetNumChildren();
        if (numChildren > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = node.jjtGetChild(i);
                if (child instanceof ASTLetOrVar) {
                    variablesNodes.add((ASTLetOrVar) child);
                } else {
                    variablesNodes.addAll(getVariablesNodes(child));
                }
            }
        }
        return variablesNodes;
    }
}
