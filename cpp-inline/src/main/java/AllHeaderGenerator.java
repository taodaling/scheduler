import utils.FileUtils;
import utils.IOUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AllHeaderGenerator {
    public static void main(String[] args) {
        File dir = new File(args[0]);
        List<String> fileNames = new ArrayList<>();
        generateHeader(dir, "", path -> {
            if (path.equals("/all")) {
                return;
            }
            fileNames.add(path);
        });
        String body = fileNames.stream().map(x -> "#include \"" + x.substring(1) + "\"\n").collect(Collectors.joining());
        FileUtils.write(new File(dir, "all"), body);
    }

    public static void generateHeader(File root, String path, Consumer<String> headerConsumer) {
        for (File file : root.listFiles()) {
            if (file.isDirectory()) {
                generateHeader(file, path + "/" + file.getName(), headerConsumer);
            } else {
                headerConsumer.accept(path + "/" + file.getName());
            }
        }
    }
}
