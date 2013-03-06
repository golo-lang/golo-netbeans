package com.serli.goloide.structure;

import com.serli.goloide.lexer.GoloTokenId;
import com.serli.goloide.parser.GoloParser;
import fr.insalyon.citi.golo.compiler.ir.AssignmentStatement;
import fr.insalyon.citi.golo.compiler.ir.BinaryOperation;
import fr.insalyon.citi.golo.compiler.ir.Block;
import fr.insalyon.citi.golo.compiler.ir.ClosureReference;
import fr.insalyon.citi.golo.compiler.ir.ConditionalBranching;
import fr.insalyon.citi.golo.compiler.ir.ConstantStatement;
import fr.insalyon.citi.golo.compiler.ir.FunctionInvocation;
import fr.insalyon.citi.golo.compiler.ir.GoloFunction;
import fr.insalyon.citi.golo.compiler.ir.GoloIrVisitor;
import fr.insalyon.citi.golo.compiler.ir.GoloModule;
import fr.insalyon.citi.golo.compiler.ir.LoopStatement;
import fr.insalyon.citi.golo.compiler.ir.MethodInvocation;
import fr.insalyon.citi.golo.compiler.ir.ReferenceLookup;
import fr.insalyon.citi.golo.compiler.ir.ReturnStatement;
import fr.insalyon.citi.golo.compiler.ir.ThrowStatement;
import fr.insalyon.citi.golo.compiler.ir.TryCatchFinally;
import fr.insalyon.citi.golo.compiler.ir.UnaryOperation;
import fr.insalyon.citi.golo.compiler.parser.GoloParserConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;
import org.openide.util.Exceptions;

/**
 *
 * @author david
 */
public class GoloStructureScanner implements StructureScanner {

    public GoloStructureScanner() {
    }
    

    @Override
    public List<? extends StructureItem> scan(ParserResult pr) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, List<OffsetRange>> folds(ParserResult pr) {
        Map<String, List<OffsetRange>> folds = new HashMap<>();
        
        // Comments
        if (pr instanceof GoloParser.GoloParserResult) {
            TokenHierarchy<Document> hi = TokenHierarchy.get(pr.getSnapshot().getSource().getDocument(true));
            TokenSequence<GoloTokenId> ts = (TokenSequence<GoloTokenId>) hi.tokenSequence();
            int startComment = -1;
            int stopComment = -1;
            int offset = 0;
            GoloTokenId lastTokenId = null;
            boolean initialComment = true;
            while (ts.moveNext()) {
                offset = ts.offset();
                Token<GoloTokenId> token = ts.token();
                GoloTokenId id = token.id();
                if (id.ordinal() == GoloParserConstants.COMMENT) {
                    if (lastTokenId == null 
                            || lastTokenId.ordinal() != GoloParserConstants.COMMENT) {
                        startComment = offset;
                    }
                    else {
                        stopComment = offset + token.length() - 1;
                    }

                }
                else {
                    if (startComment >= 0 && stopComment >= 0) {
                        String foldTypeName = initialComment ? "initial-comment" : "comments";
                        List<OffsetRange> commentsFolds = folds.get(foldTypeName);
                        if (commentsFolds == null) {
                            commentsFolds = new ArrayList<>();
                            folds.put(foldTypeName, commentsFolds);
                        }
                        commentsFolds.add(new OffsetRange(startComment + 1, stopComment));
                            
                        startComment = -1;
                        stopComment = -1;
                        initialComment = false;
                    }
                    
                    if (id.ordinal() != GoloParserConstants.NEWLINE) {
                        initialComment = false;
                    }
                }
                lastTokenId = id;
            }
            
            // Code blocks and imports
            GoloParser.GoloParserResult result = (GoloParser.GoloParserResult) pr;
            GenerateFoldsVisitor visitor = new GenerateFoldsVisitor();
            visitor.visit(result.getCompilationUnit(), folds);
        }
        return folds;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }
        
}
