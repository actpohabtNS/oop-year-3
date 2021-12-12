package task11;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DecryptStream extends FilterInputStream {

    protected DecryptStream(InputStream in) {
        super(in);
    }

    public int read(int z) throws IOException {
        int c = super.read();
        if (c == -1) return -1;
        return (c ^ z) & 0xFF;
    }
}
