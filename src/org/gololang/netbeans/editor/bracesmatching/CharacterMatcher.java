/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gololang.netbeans.editor.bracesmatching;

import java.util.logging.Logger;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;
import org.netbeans.spi.editor.bracesmatching.support.BracesMatcherSupport;

/**
 *
 * @author Vita Stejskal
 */
/* package */ final class CharacterMatcher implements BracesMatcher {

    private static final Logger LOG = Logger.getLogger(CharacterMatcher.class.getName());

    private final MatcherContext context;
    private final char[] matchingPairs;
    private final int lowerBound;
    private final int upperBound;

    private int originOffset;
    private char originalChar;
    private char matchingChar;
    private boolean backward;

    public CharacterMatcher(MatcherContext context, int lowerBound, int upperBound, char... matchingPairs) {
        this.context = context;
        this.lowerBound = lowerBound == -1 ? Integer.MIN_VALUE : lowerBound;
        this.upperBound = upperBound == -1 ? Integer.MAX_VALUE : upperBound;

        assert matchingPairs.length % 2 == 0 : "The matchingPairs parameter must contain even number of characters."; //NOI18N
        this.matchingPairs = matchingPairs;
    }

    // -----------------------------------------------------
    // BracesMatcher implementation
    // -----------------------------------------------------
    public int[] findOrigin() throws BadLocationException {
        ((AbstractDocument) context.getDocument()).readLock();
        try {
            int result[] = BracesMatcherSupport.findChar(
                    context.getDocument(),
                    context.getSearchOffset(),
                    context.isSearchingBackward()
                            ? Math.max(context.getLimitOffset(), lowerBound)
                            : Math.min(context.getLimitOffset(), upperBound),
                    matchingPairs
            );

            if (result != null) {
                originOffset = result[0];
                originalChar = matchingPairs[result[1]];
                matchingChar = matchingPairs[result[1] + result[2]];
                backward = result[2] < 0;
                return new int[]{originOffset, originOffset + 1};
            } else {
                return null;
            }
        } finally {
            ((AbstractDocument) context.getDocument()).readUnlock();
        }
    }

    public int[] findMatches() throws BadLocationException {
        ((AbstractDocument) context.getDocument()).readLock();
        try {
            int offset = -1;
            if (originalChar == matchingChar) {
                offset = BracesMatcherGoloSupport.matchEqualChar(
                        context.getDocument(),
                        originOffset,
                        Math.max(0, lowerBound),
                        originOffset + 1,
                        Math.min(context.getDocument().getLength(), upperBound),
                        originalChar,
                        matchingChar
                );
            } else {
                offset = BracesMatcherSupport.matchChar(
                        context.getDocument(),
                        backward ? originOffset : originOffset + 1,
                        backward
                                ? Math.max(0, lowerBound)
                                : Math.min(context.getDocument().getLength(), upperBound),
                        originalChar,
                        matchingChar
                );
            }
            return offset != -1 ? new int[]{offset, offset + 1} : null;
        } finally {
            ((AbstractDocument) context.getDocument()).readUnlock();
        }
    }
}
