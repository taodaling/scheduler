import task.Test;
import utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

public class LocalTester {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String location = System.getenv("test-db");
        LocalTester tester = new LocalTester();
        tester.inputSuffix = System.getenv("inputSuffix");
        tester.outputSuffix = System.getenv("outputSuffix");
        File file = new File(location);
        if(!file.exists()) {
            file.mkdirs();
        }
        tester.dfs(new File(location));
        List<Test> tests = new ArrayList<>();
        for (String key : tester.input.keySet()) {
            if (tester.output.containsKey(key)) {
                Test test = new Test();
                test.setInput(tester.input.get(key));
                test.setOutput(tester.output.get(key));
                tests.add(test);
            }
        }
        if (tests.size() == 0) {
            System.exit(1);
        }
        new TestJob().run(tests);
    }

    static Comparator<String> sorter = (a, b) -> {
        if (a.length() != b.length()) {
            return Integer.compare(a.length(), b.length());
        }
        return a.compareTo(b);
    };
    Map<String, String> input = new TreeMap<>(sorter);
    Map<String, String> output = new TreeMap<>(sorter);
    String inputSuffix;
    String outputSuffix;

    public void dfs(File file) {
        if (file.isFile()) {
            if (file.getName().endsWith(inputSuffix)) {
                input.put(file.getName().substring(0, file.getName().length() - inputSuffix.length()),
                        FileUtils.readFile(file));
            }
            if (file.getName().endsWith(outputSuffix)) {
                output.put(file.getName().substring(0, file.getName().length() - outputSuffix.length()),
                        FileUtils.readFile(file));
            }
            return;
        }
        for (File sub : file.listFiles()) {
            dfs(sub);
        }
    }
}
