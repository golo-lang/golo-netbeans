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

import fr.insalyon.citi.golo.compiler.parser.GoloParserConstants;
import java.util.Arrays;
import org.gololang.netbeans.editor.completion.ProposalsCollector;
import org.gololang.netbeans.api.completion.util.CompletionContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.gololang.netbeans.lexer.GoloLanguageHierarchy;
import org.gololang.netbeans.lexer.GoloLexerUtils;
import org.gololang.netbeans.lexer.GoloTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.CodeCompletionContext;
import org.netbeans.modules.csl.api.CodeCompletionHandler;
import org.netbeans.modules.csl.api.CodeCompletionResult;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementHandle;
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
            if (noNeedToPropose(doc, lexOffset)) {
                return CodeCompletionResult.NONE;
            }
            collector.completeKeywords(context);
            collector.completeMethods(context);
            collector.completeMethodsFromImports(context);
            collector.completeParameters(context);
            collector.completeVariable(context);
            List<CompletionProposal> listCompletionProposal = collector.getProposals();
            return new DefaultCompletionResult(listCompletionProposal, false);
        } finally {
            doc.readUnlock();
        }
    }

    private boolean noNeedToPropose(BaseDocument doc, int lexOffset) {
        TokenSequence<GoloTokenId> sequence = GoloLexerUtils.getPositionedSequence(doc, lexOffset);
        Token<GoloTokenId> token = sequence.token();
        
        if (GoloLexerUtils.isInCategories(token, Arrays.asList(GoloLanguageHierarchy.COMMENT_CATEGORY, GoloLanguageHierarchy.STRING_CATEGORY, GoloLanguageHierarchy.IDENTIFIER_CATEGORY))) {
            return true;
        }
        
        // previous token == LET or VAR ?
        if (GoloLexerUtils.isJustAfterTokenOfType(sequence, Arrays.asList(GoloParserConstants.LET, GoloParserConstants.VAR))) {
            // new variable or constant declaration, so nothing to propose
            return true;
        }
        return false;
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
}
