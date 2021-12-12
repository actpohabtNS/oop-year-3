package task11;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class EncryptStream extends FilterOutputStream {

    public EncryptStream(OutputStream o) {
        super(o);
    }

    public void write(int c, int z) throws IOException {
        super.write(c ^ z);
    }
}
