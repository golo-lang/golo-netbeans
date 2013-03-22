package com.serli.goloide.parser;

import fr.insalyon.citi.golo.compiler.GoloCompilationException;
import fr.insalyon.citi.golo.compiler.GoloCompiler;
import fr.insalyon.citi.golo.compiler.ir.GoloModule;
import fr.insalyon.citi.golo.compiler.parser.ASTCompilationUnit;
import fr.insalyon.citi.golo.compiler.parser.GoloASTNode;
import fr.insalyon.citi.golo.compiler.parser.GoloParserTokenManager;
import fr.insalyon.citi.golo.compiler.parser.IdeJavaCharStream;
import fr.insalyon.citi.golo.compiler.parser.JavaCharStream;
import fr.insalyon.citi.golo.compiler.parser.ParseException;
import fr.insalyon.citi.golo.compiler.parser.Token;
import fr.insalyon.citi.golo.compiler.parser.TokenMgrError;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.csl.spi.DefaultError;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;


/**
 *
 * @author david
 */
public class GoloParser extends Parser {

    private Snapshot snapshot;
    private fr.insalyon.citi.golo.compiler.parser.GoloParser goloParser;
    private ASTCompilationUnit compilationUnit;
    private GoloModule module;
    private List<DefaultError> errors = new ArrayList<DefaultError>();

    @Override
    public void parse (Snapshot snapshot, Task task, SourceModificationEvent event) {
        this.snapshot = snapshot;
        Reader reader = new StringReader(snapshot.getText().toString ());
        errors.clear();

        GoloCompiler compiler = new GoloCompiler() {
            @Override
            protected fr.insalyon.citi.golo.compiler.parser.GoloParser createGoloParser(final Reader sourceReader) {
                final IdeJavaCharStream javaCharStream = new IdeJavaCharStream(sourceReader);
                final GoloParserTokenManager tokenManager = new GoloParserTokenManager(javaCharStream);
                tokenManager.tokenCompleter = javaCharStream;


                class GoloIdeParser extends fr.insalyon.citi.golo.compiler.parser.GoloParser {

                    public GoloIdeParser() {
                        super(tokenManager);
                    }

                    @Override
                    public void ReInit(InputStream stream) {
                        super.ReInit(stream);
                    }
                    @Override
                    public void ReInit(InputStream stream, String encoding) {
                        super.ReInit(stream, encoding);
                    }
                    @Override
                    public void ReInit(Reader stream) {
                        javaCharStream.ReInit(stream);
                        tokenManager.ReInit(javaCharStream);
                        super.ReInit(tokenManager);
                    }
                    @Override
                    public void ReInit(GoloParserTokenManager tm) {
                        super.ReInit(tm);
                    }
                }

                return new GoloIdeParser();
            }
        };
        
        FileObject file = snapshot.getSource().getFileObject();
        String fileDisplayName = file == null ? "source code" : FileUtil.getFileDisplayName(file);
        compiler.setExceptionBuilder(new GoloCompilationException.Builder(fileDisplayName));
        goloParser = compiler.initParser(reader);
        compilationUnit = compiler.parse(fileDisplayName, goloParser);
        module = compiler.check(compilationUnit);

        for (GoloCompilationException.Problem p : compiler.getProblems()) {
          String key = p.getType().name();
          String displayName = p.getType().name().equals(p.getType().toString()) ? p.getDescription(): p.getType().toString();
          String description = p.getDescription();
          int start = 0;
          int end = 0;
              
          Token token = p.getToken();
          if (token != null) {
            start = token.startOffset;
            end = token.endOffset;
          }
          else {
            GoloASTNode node = (GoloASTNode)p.getSource();
            if (node != null) {
              token = node.jjtGetFirstToken();
              if (token != null) {
                start = token.startOffset;
              }
              token = node.jjtGetLastToken();
              if (token != null) {
                end = token.endOffset;
              }
            }
          }
          errors.add(new DefaultError(key, displayName, description, 
              snapshot.getSource().getFileObject(), 
              start, end, 
              Severity.ERROR));
        }
    }

    @Override
    public GoloParserResult getResult (Task task) {
        return new GoloParserResult ();
    }

    @Override
    public void cancel () {
    }

    @Override
    public void addChangeListener (ChangeListener changeListener) {
    }

    @Override
    public void removeChangeListener (ChangeListener changeListener) {
    }

    
    public class GoloParserResult extends ParserResult {

        private boolean valid = true;

        GoloParserResult () {
            super (snapshot);
        }

//        public fr.insalyon.citi.golo.compiler.parser.GoloParser getJavaParser () throws org.netbeans.modules.parsing.spi.ParseException {
//            if (!valid) {
//                throw new org.netbeans.modules.parsing.spi.ParseException ();
//            }
//            return javaParser;
//        }
//
        @Override
        protected void invalidate () {
            valid = false;
        }

        @Override
        public List<? extends Error> getDiagnostics() {
            return errors;
        }

        public ASTCompilationUnit getCompilationUnit() {
            return compilationUnit;
        }

        public GoloModule getModule() {
            return module;
        }
    }
    
}