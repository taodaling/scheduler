import utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class DeleteExpiredFiles {
    public static void main(String[] args) throws IOException {
        Pattern pattern = Pattern.compile(System.getenv("file-pattern"));
        String root = System.getenv("root");
        long now = System.currentTimeMillis();
        long timeSpan = Long.parseLong(System.getenv("expiry"));
        FileUtils.visitFile(new File(root), file -> {
            if (file.isFile() &&
                    file.lastModified() + timeSpan < now &&
                    pattern.matcher(file.getName()).matches()) {
                file.delete();
            }
        });
    }
}
