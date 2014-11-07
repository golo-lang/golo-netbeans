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
package org.gololang.netbeans.api.completion;

import fr.insalyon.citi.golo.compiler.ir.GoloFunction;
import fr.insalyon.citi.golo.compiler.parser.GoloASTNode;
import fr.insalyon.citi.golo.compiler.parser.GoloASTUtils;
import java.util.ArrayList;
import org.gololang.netbeans.editor.completion.ProposalsCollector;
import org.gololang.netbeans.api.completion.util.CompletionContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.gololang.netbeans.parser.GoloParser;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.CodeCompletionContext;
import org.netbeans.modules.csl.api.CodeCompletionHandler;
import org.netbeans.modules.csl.api.CodeCompletionResult;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.ParameterInfo;
import org.netbeans.modules.csl.spi.DefaultCompletionResult;
import org.netbeans.modules.csl.spi.ParserResult;

/**
 *
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class CompletionHandler implements CodeCompletionHandler {

    @Override
    public CodeCompletionResult complete(CodeCompletionContext completionContext) {
        ParserResult parserResult = completionContext.getParserResult();
        String prefix = completionContext.getPrefix();

        // Documentation says that @NonNull is return from getPrefix() but it's not true
        // Invoking "this.^" makes the return value null
        if (prefix == null) {
            prefix = "";
        }
            if (completionContext.getQueryType() == QueryType.NONE) {
            return CodeCompletionResult.NONE;
        }
        int lexOffset = completionContext.getCaretOffset();
//        int astOffset = ASTUtils.getAstOffset(parserResult, lexOffset);
        int anchor = lexOffset - prefix.length();

        final Document document = parserResult.getSnapshot().getSource().getDocument(false);
        if (document == null) {
            return CodeCompletionResult.NONE;
        }
        final BaseDocument doc = (BaseDocument) document;
        doc.readLock(); // Read-lock due to Token hierarchy use
        try {
            ProposalsCollector collector = new ProposalsCollector();
            CompletionContext context = new CompletionContext(parserResult, prefix, anchor, lexOffset, doc);
            collector.completeKeywords(context);
            collector.completeMethods(context);
            collector.completeMethodsFromImports(context);
            collector.completeParameters(context);
            List<CompletionProposal> listCompletionProposal = collector.getProposals();
            return new DefaultCompletionResult(listCompletionProposal, false);
        } finally {
            doc.readUnlock();
        }
    }

    @Override
    public String document(ParserResult pr, ElementHandle eh) {
        return null;
    }

    @Override
    public ElementHandle resolveLink(String string, ElementHandle eh) {
        return eh;
    }

    @Override
    public String getPrefix(ParserResult pr, int i, boolean bln) {
        return null;
    }

    @Override
    public QueryType getAutoQuery(JTextComponent jtc, String typedText) {
        char c = typedText.charAt(0);

        if (c == '.' || Character.isWhitespace(c)) {
            // no proposals after '.', 'and whitespaces because it's not smart for instance
            return QueryType.NONE;
        }

        // proposals just for starting word (ctrl-space is not managed here)
        return QueryType.COMPLETION;
    }

    @Override
    public String resolveTemplateVariable(String string, ParserResult pr, int i, String string1, Map map) {
        return null;
    }

    @Override
    public Set<String> getApplicableTemplates(Document dcmnt, int i, int i1) {
        return Collections.emptySet();
    }

    @Override
    public ParameterInfo parameters(ParserResult pr, int i, CompletionProposal cp) {
        return ParameterInfo.NONE;
    }
//    @Override
//    public ParameterInfo parameters(ParserResult pr, int caretOffset, CompletionProposal info) {
//        // here we need to calculate the list of parameters for the methods under the caret.
//        // proposal seems to be null all the time.
//
//        List<String> paramList = new ArrayList<>();
//
//        BaseDocument doc = (BaseDocument) pr.getSnapshot().getSource().getDocument(true);
//        Set<GoloFunction> functions = ((GoloParser.GoloParserResult) pr).getModule().getFunctions();
//        int idx = 1;
//        int index = -1;
//        int offset = -1;
//
//        for (GoloFunction function : functions) {
//            GoloASTNode astNode = function.getASTNode();
//            OffsetRange range = GoloASTUtils.getRange(astNode, doc);
//            if (range.containsInclusive(caretOffset)) {
//                paramList = function.getParameterNames();
//                offset = range.getStart();
//                index = idx;
//            }
//            idx++;
//        }
//
//        if (paramList != null && !paramList.isEmpty()) {
//            return new ParameterInfo(paramList, index, offset);
//        }
//        
//        return ParameterInfo.NONE;

//        AstPath path = getPathFromInfo(caretOffset, info);
//
//        if (path != null) {
//
//            ArgumentListExpression ael = getSurroundingArgumentList(path);
//
//            if (ael != null) {
//
//                List<ASTNode> children = ASTUtils.children(ael);
//
//                // populate list with *all* parameters, but let index and offset
//                // point to a specific parameter.
//                int idx = 1;
//                int index = -1;
//                int offset = -1;
//
//                for (ASTNode node : children) {
//                    OffsetRange range = GoloASTUtils.getRange(node, doc);
//                    paramList.add(node.getText());
//
//                    if (range.containsInclusive(caretOffset)) {
//                        offset = range.getStart();
//                        index = idx;
//                    }
//
//                    idx++;
//                }
//
//                // calculate the parameter we are dealing with
//                if (paramList != null && !paramList.isEmpty()) {
//                    return new ParameterInfo(paramList, index, offset);
//                }
//            } else {
//                LOG.log(Level.FINEST, "ArgumentListExpression ==  null"); // NOI18N
//                return ParameterInfo.NONE;
//            }
//
//        } else {
//            LOG.log(Level.FINEST, "path ==  null"); // NOI18N
//            return ParameterInfo.NONE;
//        }
//        return ParameterInfo.NONE;
//    }

}
