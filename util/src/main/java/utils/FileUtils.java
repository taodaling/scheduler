package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class FileUtils {
    public static void createFolder(File file) {
        file.mkdirs();
    }

    public static void createFile(File file) {
        if (file.exists()) {
            return;
        }
        createFolder(file.getParentFile());
        try {
            file.createNewFile();
        } catch (IOException e) {
            ErrorUtils.throwAsRuntimeException(e);
        }
    }

    public static String readFile(File file) {
        if(!file.exists()){
            return "";
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            return IOUtils.readAll(fis);
        } catch (IOException e) {
            ErrorUtils.throwAsRuntimeException(e);
            return null;
        }
    }

    public static String concatPath(String a, String b){
        if(a.endsWith(File.pathSeparator) || a.isEmpty()){
            return a + b;
        }
        return a + "/" + b;
    }

    public static void write(File file, String content) {
        createFile(file);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            ErrorUtils.throwAsRuntimeException(e);
            return;
        }
    }


    public static void write(String file, String content) {
        write(new File(file), content);
    }

    public static void visitFile(File file, Consumer<File> consumer){
        if(file.isDirectory()){
            for(File sub : file.listFiles()){
                visitFile(sub, consumer);
            }
        }
        consumer.accept(file);
    }
}
