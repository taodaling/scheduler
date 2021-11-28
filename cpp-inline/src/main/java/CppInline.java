import utils.FileUtils;
import utils.RegexUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class CppInline {
    public static void main(String[] args) {
        String path = System.getenv("main-file");
        String lib = System.getenv("CPLUS_INCLUDE_PATH");
        String inlineFile = System.getenv("inline-file");
        List<String> libs = new ArrayList<>();
        String splitBy = System.getProperty("os.name").contains("win") ? ";" : ":";
        for (String s : lib.split(splitBy)) {
            libs.add(s);
        }
        String inlineContent = include(new File(path), libs, new HashSet<>());
        FileUtils.write(inlineFile, inlineContent);
    }

    private static Pattern pattern = Pattern.compile("#include\\s*?\"(.*?)\"");

    public static String include(File file, List<String> libs, Set<File> set) {
        if (set.contains(file)) {
            return "";
        }
        set.add(file);
        String content = FileUtils.readFile(file).replaceAll("#pragma\\s+once", "");
        return RegexUtils.replace(content, pattern, m -> {
            String header = m.group(1);
            List<String> paths = new ArrayList<>();
            paths.add(FileUtils.concatPath(file.getParent(), header));
            for (String lib : libs) {
                paths.add(FileUtils.concatPath(lib, header));
            }
            for (String path : paths) {
                File cand = new File(path);
                if (cand.exists()) {
                    return include(cand, libs, set);
                }
            }
            throw new UncheckedIOException(new FileNotFoundException(String.format("can't find header [%s]", header)));
        });
    }
}
