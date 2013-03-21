package fr.insalyon.citi.golo.compiler.parser;

import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author david
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
