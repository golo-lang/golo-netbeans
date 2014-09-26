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

package org.gololang.netbeans.completion;

import fr.insalyon.citi.golo.compiler.ir.GoloFunction;
import java.util.Set;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.gololang.netbeans.parser.FileElementsManager;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

/**
 *
 * @author Julien Déray
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

                //Iterate through the available locales
                //and assign each country display name
                //to a CompletionResultSet:
                Set<GoloFunction> functions = FileElementsManager.getInstance().getFunctions();
                for (GoloFunction fn : functions) {
                    if ( !fn.getName().startsWith("__$$_") ) {
                        completionResultSet.addItem(new FunctionCompletionItem(fn.getName(), caretOffset));
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
    
}