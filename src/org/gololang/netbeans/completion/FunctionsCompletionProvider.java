/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gololang.netbeans.completion;

import fr.insalyon.citi.golo.compiler.ir.GoloFunction;
import java.util.Locale;
import java.util.Set;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.gololang.netbeans.parser.FileElementsManager;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.util.Exceptions;

/**
 *
 * @author julienderay
 */
@MimeRegistration(mimeType = "text/x-golo", service = CompletionProvider.class)
public class FunctionsCompletionProvider implements CompletionProvider {
    
    public FunctionsCompletionProvider() {
    }

   @Override
    public CompletionTask createTask(int queryType, JTextComponent jtc) {
    
        if (queryType != CompletionProvider.COMPLETION_QUERY_TYPE) {
            return null;
        }

        return new AsyncCompletionTask(new AsyncCompletionQuery() {

            @Override
            protected void query(CompletionResultSet completionResultSet, Document document, int caretOffset) {

                String filter = null;
                int startOffset = caretOffset - 1;

                try {
                    final StyledDocument bDoc = (StyledDocument) document;
                    final int lineStartOffset = getRowFirstNonWhite(bDoc, caretOffset);
                    final char[] line = bDoc.getText(lineStartOffset, caretOffset - lineStartOffset).toCharArray();
                    final int whiteOffset = indexOfWhite(line);
                    filter = new String(line, whiteOffset + 1, line.length - whiteOffset - 1);
                    if (whiteOffset > 0) {
                        startOffset = lineStartOffset + whiteOffset + 1;
                    } else {
                        startOffset = lineStartOffset;
                    }
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }

                //Iterate through the available locales
                //and assign each country display name
                //to a CompletionResultSet:
                Set<GoloFunction> functions = FileElementsManager.getInstance().getFunctions();
                for (GoloFunction fn : functions) {
                    if ( !fn.getName().startsWith("__$$_") ) {
                        completionResultSet.addItem(new FunctionCompletionItem(fn.getName(), startOffset, caretOffset));
                    }
                }
                completionResultSet.finish();
            }
        }, jtc);
    }

    @Override
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return 0;
    }
    
    static int getRowFirstNonWhite(StyledDocument doc, int offset) throws BadLocationException {
        Element lineElement = doc.getParagraphElement(offset);
        int start = lineElement.getStartOffset();
        while (start + 1 < lineElement.getEndOffset()) {
            try {
                if (doc.getText(start, 1).charAt(0) != ' ') {
                    break;
                }
            } catch (BadLocationException ex) {
                throw (BadLocationException) new BadLocationException(
                        "calling getText(" + start + ", " + (start + 1) +
                        ") on doc of length: " + doc.getLength(), start
                        ).initCause(ex);
            }
            start++;
        }
        return start;
    }
    
    static int indexOfWhite(char[] line){
        int i = line.length;
        while(--i > -1){
            final char c = line[i];
            if(Character.isWhitespace(c)){
                return i;
            }
        }
        return -1;
    }
}