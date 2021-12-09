import task.Task;
import task.Test;
import utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class Tester {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String ignoreInteractive = System.getenv("ignore-interactive");
        List<String> commands = CommandUtils.splitCommands(System.getenv("commands"));
        String input = System.getenv("task-json");
        File inputFile = new File(input);
        if (!inputFile.exists()) {
            return;
        }
        long timeout = System.getenv().containsKey("timeout") ? Long.parseLong(System.getenv("timeout"))
                : 10000L;
        Task task = JsonUtils.parse(FileUtils.readFile(inputFile), Task.class);
        if ("true".equals(ignoreInteractive) && task.isInteractive()) {
            return;
        }
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<Output>> jobs = new ArrayList<>();
        for (Test test : task.getTests()) {
            jobs.add(executor.submit(new Callable<Output>() {
                @Override
                public Output call() throws Exception {
                    Process pb = new ProcessBuilder(commands).start();
                    org.apache.commons.io.IOUtils.write(test.getInput(), pb.getOutputStream());
                    pb.getOutputStream().flush();
                    String s = IOUtils.readAll(pb.getInputStream());
                    String err = IOUtils.readAll(pb.getErrorStream());
                    if (pb.exitValue() != 0) {
                        return null;
                    }
                    return new Output(s, err);
                }
            }));
        }
        executor.shutdown();
        executor.awaitTermination(timeout, TimeUnit.MILLISECONDS);
        executor.shutdownNow();

        Console console = new Console(System.out);
        String failInput = null;
        String failOutput = null;
        String failExpect = null;
        String failErr = null;
        for (int i = 0; i < jobs.size(); i++) {
            Future<Output> future = jobs.get(i);
            if (!future.isDone()) {
                console.blue("" + i + "\t: TIMEOUT");
                if (failInput == null) {
                    failInput = task.getTests().get(i).getInput();
                    failOutput = "";
                    failErr = "";
                    failExpect = task.getTests().get(i).getOutput();
                }

            } else if (future.get() == null) {
                console.blue("" + i + "\t: EXIT BY RUNTIME ERROR");

                if (failInput == null) {
                    failInput = task.getTests().get(i).getInput();
                    failOutput = "";
                    failErr = "";
                    failExpect = task.getTests().get(i).getOutput();
                }
            } else {
                if (match(task.getTests().get(i).getOutput(), future.get().stdout)) {
                    console.green("" + i + "\t: PASSED");
                } else {
                    console.red("" + i + "\t: WRONG ANSWER");
                    failInput = task.getTests().get(i).getInput();
                    failOutput = future.get().stdout;
                    failErr = future.get().stderr;
                    failExpect = task.getTests().get(i).getOutput();
                }
            }
            System.out.println();
        }

        if (failInput != null) {
            System.out.println("Input:");
            System.out.println(failInput);
            System.out.println();
            System.out.println("Expect:");
            System.out.println(failExpect);
            System.out.println();
            System.out.println("Output:");
            System.out.println(failOutput);
            System.out.println();
            System.out.println("Err:");
            System.out.println(failErr);
            System.out.println();
            System.exit(1);
        }
    }

    static Pattern pattern = Pattern.compile("\\s+", Pattern.MULTILINE | Pattern.DOTALL);

    public static boolean match(String a, String b) {
        String[] asplice = pattern.split(a);
        String[] bsplice = pattern.split(b);
        return Arrays.equals(asplice, bsplice);
    }

    static class Output {
        String stdout;
        String stderr;

        public Output(String stdout, String stderr) {
            this.stdout = stdout;
            this.stderr = stderr;
        }
    }
}
