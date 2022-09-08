import task.Task;
import task.Test;
import utils.*;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class Tester {
    static List<Process> processes = new CopyOnWriteArrayList<>();
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String ignoreInteractive = System.getenv("ignore-interactive");
        String input = System.getenv("task-json");
        File inputFile = new File(input);
        if (!inputFile.exists()) {
            return;
        }
        Task task = JsonUtils.parse(FileUtils.readFile(inputFile), Task.class);
        if ("true".equals(ignoreInteractive) && task.isInteractive()) {
            System.out.println("skip interactive task");
            return;
        }
        new TestJob().run(task.getTests());
    }


}
