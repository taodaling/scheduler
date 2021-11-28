import utils.FileUtils;

public class ExportEnv {
    public static void main(String[] args) {
        String path = System.getenv("output");
        String content = System.getenv("body");
        System.out.println("path = " + path);
        System.out.println("content = " + content);
        FileUtils.write(path, content);
    }
}
