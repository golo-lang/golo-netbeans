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

import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author David Festal <david.festal@serli.com>
 */

public class IdeJavaCharStream extends JavaCharStream implements GoloParserTokenManager.TokenCompleter {
    private int currentOffset = 0;
    private int tokenStart = 0;

    public IdeJavaCharStream(Reader reader) {
        super(reader);
    }

    @Override
    public char readChar() throws IOException {
        char result = super.readChar();
            currentOffset++;
        return result;
    }

    @Override
    public char BeginToken() throws IOException {
        tokenStart = currentOffset;
        char result = super.BeginToken();
        if (tokenStart == currentOffset) {
            currentOffset++;
        }
        return result;
    }

    @Override
    public void backup(int amount) {
        super.backup(amount);
        currentOffset -= amount;
    }

    @Override
    public void ReInit(Reader dstream) {
        super.ReInit(dstream);
        currentOffset = 0;
    }

    @Override
    public void completeToken(Token token) {
        token.startOffset = tokenStart;
        token.endOffset = currentOffset;
    }

}
