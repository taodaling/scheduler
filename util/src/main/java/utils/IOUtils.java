package utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class IOUtils {
    public static String readAll(InputStream is) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        readAll(is, bao);
        return bao.toString(StandardCharsets.UTF_8);
    }

    public static long readAll(InputStream is, OutputStream bao) {
        byte[] buf = new byte[1 << 13];
        long ans = 0;
        try {
            while (true) {
                int len = is.read(buf);
                if (len < 0) {
                    break;
                }
                ans += len;
                bao.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //ErrorUtils.throwAsRuntimeException(e);
        }
        return ans;
    }
}
