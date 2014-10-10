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
package org.gololang.netbeans.editor.bracesmatching;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;

/**
 * 
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public final class BracesMatcherGoloSupport {

    public static int matchEqualChar(Document document, int offsetBackward, int limitBackward, int offsetForward, int limitForward, char origin, char matching) throws BadLocationException {
        int lookaheadBackward = offsetBackward - limitBackward;
        int lookaheadForward = limitForward - offsetForward;

        // check the character at the left from the caret
        Segment textBackward = new Segment();
        document.getText(offsetBackward - lookaheadBackward, lookaheadBackward, textBackward);
        int countBackward = 0;
        int offsetMatchBackward = -1;
        for (int i = 0; i < lookaheadBackward; i++) {
            if (MatcherContext.isTaskCanceled()) {
                return -1;
            }
            if (origin == textBackward.array[textBackward.offset + i]) {
                countBackward++;
                if (countBackward > 0 && (countBackward % 2) != 0) {
                    offsetMatchBackward = offsetBackward - (lookaheadBackward - i);
                } else {
                    offsetMatchBackward = -1;
                }
            }
        }
        if (offsetMatchBackward == -1) {
            // check the character at the right from the caret
            Segment textForward = new Segment();
            document.getText(offsetForward, lookaheadForward, textForward);
            for (int i = 0; i < lookaheadForward; i++) {
                if (MatcherContext.isTaskCanceled()) {
                    return -1;
                }
                if (origin == textForward.array[textForward.offset + i]) {
                    return offsetForward + i;
                }
            }
        }

        return offsetMatchBackward;
    }

    // Preventing instantiation
    private BracesMatcherGoloSupport() {
    }
}
