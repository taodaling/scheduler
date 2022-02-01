import utils.FileUtils;
import utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CppInline {
    public static void main(String[] args) throws IOException {
        String path = System.getenv("main-file");
        String lib = System.getenv("CPLUS_INCLUDE_PATH");
        String ignoreList = System.getenv("ignore-list");
        if (ignoreList == null) {
            ignoreList = "";
        }

        String inlineFile = System.getenv("inline-file");
        List<String> libs = new ArrayList<>();
        String splitBy = System.getProperty("os.name").toLowerCase().contains("win") ? ";" : ":";
        for (String s : lib.split(splitBy)) {
            libs.add(s);
        }
        libs.add("");

        Set<File> included = new HashSet<>();
        for(String s : ignoreList.split(",")) {
            included.add(new File(s).getCanonicalFile());
        }
        String inlineContent = "//Timestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n" +
                include(new File(path), libs, included);

        //compress
        inlineContent = Stream.of(inlineContent.split("\\n")).filter(x -> !x.isBlank()).collect(Collectors.joining("\n"));
        FileUtils.write(inlineFile, inlineContent);
    }

    private static Pattern pattern = Pattern.compile("^\\s*#include\\s*?\"(.*?)\"", Pattern.MULTILINE | Pattern.DOTALL);

    public static String include(File file, List<String> libs, Set<File> set) throws IOException {
        file = file.getCanonicalFile();
        if (set.contains(file)) {
            return "";
        }
        set.add(file);
        String content = FileUtils.readFile(file).replaceAll("#pragma\\s+once", "");
        File finalFile = file;
        return StringUtils.replace(content, pattern, m -> {
            String header = m.group(1);
            List<String> paths = new ArrayList<>();
            paths.add(FileUtils.concatPath(finalFile.getParent(), header));
            for (String lib : libs) {
                paths.add(FileUtils.concatPath(lib, header));
            }
            for (String path : paths) {
                File cand = new File(path);
                if (cand.exists()) {
                    try {
                        return include(cand, libs, set);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }
            throw new UncheckedIOException(new FileNotFoundException(String.format("can't find header [%s]", header)));
        });
    }
}
