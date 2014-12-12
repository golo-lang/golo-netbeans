/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
