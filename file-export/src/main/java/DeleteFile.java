import com.google.common.base.Preconditions;
import utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public class DeleteFile {
    public static void main(String[] args) throws IOException {
        Preconditions.checkArgument(args.length >= 1, "DeleteFile {file}+");
        for (int i = 0; i < args.length; i++) {
            File src = new File(args[i]);
            System.out.println("Delete [" + src.getAbsolutePath() + "]");
            FileUtils.visitFile(src, file -> {
                if (!file.delete()) {
                    throw new UncheckedIOException(new IOException("Can't delete file [" + file.getAbsolutePath() + "] !"));
                }
            });
        }
    }

}
