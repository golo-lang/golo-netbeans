package com.serli.goloide.parser;

import fr.insalyon.citi.golo.compiler.GoloCompilationException;
import fr.insalyon.citi.golo.compiler.GoloCompiler;
import fr.insalyon.citi.golo.compiler.ir.GoloModule;
import fr.insalyon.citi.golo.compiler.parser.ASTCompilationUnit;
import fr.insalyon.citi.golo.compiler.parser.GoloASTNode;
import fr.insalyon.citi.golo.compiler.parser.GoloParserTokenManager;
import fr.insalyon.citi.golo.compiler.parser.IdeJavaCharStream;
import fr.insalyon.citi.golo.compiler.parser.Token;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.csl.spi.DefaultError;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;


/**
 *
 * @author david
 */
public class GoloParser extends Parser {

    private Snapshot snapshot;
    private Future<GoloParserResult> parserTaskResult;
    
    private static class InternalGoloCompiler extends GoloCompiler {
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
    }

    @Override
    public void parse (final Snapshot snapshot, Task task, SourceModificationEvent event) {
      cleanup();
      this.snapshot = snapshot;

      ExecutorService es = Executors.newSingleThreadExecutor ();
      
      class Parsing implements Callable<GoloParserResult> {
        private Thread parsingThread;
        private GoloParserResult result;
        @Override
        public GoloParserResult call() throws InterruptedException {
          parsingThread = new Thread("Golo Parsing Thread") {
            @Override
            public void run() {
              final Reader reader = new StringReader(snapshot.getText().toString ());
              FileObject file = snapshot.getSource().getFileObject();
              String fileDisplayName = file == null ? "source code" : FileUtil.getFileDisplayName(file);
              GoloCompiler compiler = new InternalGoloCompiler();
              compiler.setExceptionBuilder(new GoloCompilationException.Builder(fileDisplayName));
              ASTCompilationUnit compilationUnit = compiler.parse(fileDisplayName, compiler.initParser(reader));
              GoloModule module = compiler.check(compilationUnit);
              result = new GoloParserResult(compilationUnit, module, collectErrors(snapshot, compiler));
            }
          };
          parsingThread.start();
          parsingThread.join();
          return result;
        }
        public void cancel() {
          if (parsingThread != null &&
              parsingThread.isAlive()) {
            System.out.println("!!!!! Stopping parsing Thread !!!!!!");
            parsingThread.stop();
          }
        }
      }

      class ParsingTask extends FutureTask<GoloParserResult> {
        
        private Parsing parsing;

        public ParsingTask(Parsing parsing) {
          super(parsing);
          this.parsing = parsing;
        }
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
          boolean result = super.cancel(mayInterruptIfRunning);
          if (mayInterruptIfRunning) {
            parsing.cancel();
          }
          return result;
        }
        @Override
        public GoloParserResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
          try {
            return super.get(timeout, unit);
          }
          catch(TimeoutException te) {
            parsing.cancel();
            throw  te;
          }
        }
      }

      ParsingTask parsingTask = new ParsingTask(new Parsing());
      
      es.execute(parsingTask);
      parserTaskResult = parsingTask;

      try {
        parserTaskResult.get(2, TimeUnit.SECONDS);
      }
      catch (ExecutionException ex) {
      }
      catch (TimeoutException ex) {
        if (task instanceof ParserResultTask) {
          ParserResultTask theTask = (ParserResultTask) task;
          theTask.cancel();
        }
      }
      catch (InterruptedException ex) {
      }
      catch (CancellationException ex) {
      }
    }

    private synchronized void cleanup() {
      parserTaskResult = null;
    }
    
    @Override
    public synchronized GoloParserResult getResult (Task task) {
      if (parserTaskResult != null &&
          parserTaskResult.isDone() &&
          !parserTaskResult.isCancelled()) {
        try {
          return parserTaskResult.get(0, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
        } catch (ExecutionException ex) {
        } catch (CancellationException ex) {
        } catch (TimeoutException ex) {          
        }
      }
      return null;
    }

    @Override
    public synchronized void cancel () {
      if (parserTaskResult != null) {
        parserTaskResult.cancel(true);
      }
    }

    @Override
    public synchronized void cancel(CancelReason cr, SourceModificationEvent sme) {
      if (parserTaskResult != null) {
        parserTaskResult.cancel(true);
      }
    }
    
    @Override
    public void addChangeListener (ChangeListener changeListener) {
    }

    @Override
    public void removeChangeListener (ChangeListener changeListener) {
    }

  private List<? extends Error> collectErrors(Snapshot snapshot, GoloCompiler compiler) {
    List<Error> errors = new LinkedList<>(); 
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
    return errors;
  }


  public class GoloParserResult extends ParserResult {
    private ASTCompilationUnit compilationUnit;
    private GoloModule module;
    private List<? extends Error> errors = new ArrayList<DefaultError>();

    GoloParserResult (ASTCompilationUnit compilationUnit, GoloModule module, List<? extends Error> errors) {
      super (snapshot);
      this.compilationUnit = compilationUnit;
      this.module = module;
      this.errors = errors;
    }

    @Override
    protected void invalidate () {
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