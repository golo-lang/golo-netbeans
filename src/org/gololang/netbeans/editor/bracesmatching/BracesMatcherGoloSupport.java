/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gololang.netbeans.editor.bracesmatching;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;

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
