import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;

public class MoveFile {
    public static void main(String[] args) throws IOException {
        Preconditions.checkArgument(args.length >= 2, "MoveFile {src} {dst}");
        File src = new File(args[0]);
        File dst = new File(args[1]);
        System.out.println("Move [" + src.getAbsolutePath() + "] to [" + dst.getAbsolutePath() + "]");
        if (dst.exists()) {
            if (!dst.delete()) {
                throw new IOException("Can't delete existing [" + dst.getAbsolutePath() + "]");
            }
        }
        if (!src.renameTo(dst)) {
            throw new IOException("Move failed!");
        }
    }
}
