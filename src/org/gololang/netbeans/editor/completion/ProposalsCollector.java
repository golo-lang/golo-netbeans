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
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.csl.api.CompletionProposal;

/**
 * 
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class ProposalsCollector {
    private List<CompletionProposal> proposals;
    private final KeywordCompletion keywordCompletion;
    private final FunctionCompletion functionCompletion;
    private final ImportCompletion importMethodCompletion;
    private final ParameterCompletion parameterCompletion;
    private final VariableCompletion variableCompletion;
    private final DocumentationCompletion documentationCompletion;
    private final ModuleCompletion moduleCompletion;
    private final NamedAugmentationCompletion namedAugmentationCompletion;
    
    public ProposalsCollector() {
        proposals = new ArrayList<>();
        keywordCompletion = new KeywordCompletion();
        functionCompletion = new FunctionCompletion();
        importMethodCompletion = new ImportCompletion();
        parameterCompletion = new ParameterCompletion();
        variableCompletion = new VariableCompletion();
        documentationCompletion = new DocumentationCompletion();
        moduleCompletion = new ModuleCompletion();
        namedAugmentationCompletion = new NamedAugmentationCompletion();
    }
    
    public void completeKeywords(CompletionContext completionRequest) {
        keywordCompletion.complete(proposals, completionRequest, completionRequest.getAnchor());
    }

    public void completeMethods(CompletionContext completionRequest) {
        functionCompletion.complete(proposals, completionRequest, completionRequest.getAnchor());
    }
    
    public List<CompletionProposal> getProposals() {
        return proposals;
    }

    public void completeMethodsFromImports(CompletionContext context) {
        importMethodCompletion.complete(proposals, context, context.getAnchor());
    }
    
    public void completeParameters(CompletionContext context) {
        parameterCompletion.complete(proposals, context, context.getAnchor());
    }
    
    public void completeVariable(CompletionContext context) {
        variableCompletion.complete(proposals, context, context.getAnchor());
    }

    public void completeDocumentation(CompletionContext context) {
        documentationCompletion.complete(proposals, context, context.getAnchor());
    }
    
    public void completeImportModule(CompletionContext context) {
        moduleCompletion.complete(proposals, context, context.getAnchor());
    }

    public void completeAugmentation(CompletionContext context) {
        namedAugmentationCompletion.complete(proposals, context, context.getAnchor());
    }
}
