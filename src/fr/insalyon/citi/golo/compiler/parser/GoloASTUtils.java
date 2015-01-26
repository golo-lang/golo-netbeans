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
package fr.insalyon.citi.golo.compiler.parser;

import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public final class GoloASTUtils {
    
    private GoloASTUtils(){}

    public static OffsetRange getRange(GoloASTNode node, BaseDocument doc) {

        // Warning! The implicit class and some other nodes has line/column numbers below 1
        // if line is wrong, let's invalidate also column and vice versa
        int startLineNumber = node.getLineInSourceCode();
        int startColumnNumber = node.getColumnInSourceCode();
        if (startLineNumber < 1 || startColumnNumber < 1) {
            return OffsetRange.NONE;
        }
        int endLineNumber = node.jjtGetLastToken().endLine;
        int endColumnNumber = node.jjtGetLastToken().endColumn;
        if (endLineNumber < 1 || endColumnNumber < 1) {
            return OffsetRange.NONE;
        }
        if (doc == null) {
            // Null document in getRange()
            return OffsetRange.NONE;
        }
        
        int start = getOffset(doc, startLineNumber, startColumnNumber);
        int end = getOffset(doc, endLineNumber, endColumnNumber);
        return new OffsetRange(start, end);
    }
    
    /**
     * Find offset in text for given line and column
     * Never returns negative number
     */
    public static int getOffset(BaseDocument doc, int lineNumber, int columnNumber) {
        assert lineNumber > 0 : "Line number must be at least 1 and was: " + lineNumber;
        assert columnNumber > 0 : "Column number must be at least 1 ans was: " + columnNumber;

        int offset = Utilities.getRowStartFromLineOffset(doc, lineNumber - 1);
        offset += (columnNumber - 1);

        // some sanity checks
        if (offset < 0){
            offset = 0;
        }

        return offset;
    }
}
