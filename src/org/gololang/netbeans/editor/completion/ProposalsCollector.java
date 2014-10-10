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
    
    
    public ProposalsCollector() {
        proposals = new ArrayList<>();
        keywordCompletion = new KeywordCompletion();
        functionCompletion = new FunctionCompletion();
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
    
    
}
