import task.Test;
import utils.CommandUtils;
import utils.Console;
import utils.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class TestJob {
    static List<Process> processes = new CopyOnWriteArrayList<>();

    public void run(List<Test> tests) throws InterruptedException, ExecutionException {
        List<String> commands = CommandUtils.splitCommands(System.getenv("commands"));
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
                (x) -> {
                    Thread t = new Thread(x);
                    t.setDaemon(true);
                    return t;
                });
        ExecutorService ioExecutor = Executors.newCachedThreadPool();
        long timeout = System.getenv().containsKey("timeout") ? Long.parseLong(System.getenv("timeout"))
                : 10000L;
        List<Future<Output>> jobs = new ArrayList<>();
        for (Test test : tests) {
            jobs.add(executor.submit(new Callable<Output>() {
                @Override
                public Output call() throws Exception {
                    Process pb = new ProcessBuilder(commands).start();
                    processes.add(pb);
                    Future<String> stdout = ioExecutor.submit(() -> {
                        String s = IOUtils.readAll(pb.getInputStream());
                        return s;
                    });
                    Future<String> stderr = ioExecutor.submit(() -> {
                        String s = IOUtils.readAll(pb.getErrorStream());
                        return s;
                    });
                    //allow pb exits before reading all input
                    try {
                        org.apache.commons.io.IOUtils.write(test.getInput(), pb.getOutputStream());
                        pb.getOutputStream().close();
                    } catch (IOException e) {
                    }
                    Output output = new Output(stdout.get(), stderr.get());
                    if (pb.exitValue() != 0) {
                        return null;
                    }
                    return output;
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
                    failInput = tests.get(i).getInput();
                    failOutput = "";
                    failErr = "";
                    failExpect = tests.get(i).getOutput();
                }

            } else if (future.get() == null) {
                console.blue("" + i + "\t: EXIT BY RUNTIME ERROR");

                if (failInput == null) {
                    failInput = tests.get(i).getInput();
                    failOutput = "";
                    failErr = "";
                    failExpect = tests.get(i).getOutput();
                }
            } else {
                if (match(tests.get(i).getOutput(), future.get().stdout)) {
                    console.green("" + i + "\t: PASSED");
                } else {
                    console.red("" + i + "\t: WRONG ANSWER");
                    if (failInput == null) {
                        failInput = tests.get(i).getInput();
                        failOutput = future.get().stdout;
                        failErr = future.get().stderr;
                        failExpect = tests.get(i).getOutput();
                    }
                }
            }
            System.out.println();
        }
        if (failInput != null) {
            System.out.println("Input:");
            System.out.println(truncate(failInput, 256));
            System.out.println();
            System.out.println("Expect:");
            System.out.println(truncate(failExpect, 256));
            System.out.println();
            System.out.println("Output:");
            System.out.println(truncate(failOutput, 256));
            System.out.println();
            System.out.println("Err:");
            System.out.println(failErr);
            System.out.println();
            //
        }
        for (Process p : processes) {
            if (p.isAlive()) {
                p.destroyForcibly();
            }
        }
        System.exit(0);
    }

    static Pattern pattern = Pattern.compile("\\s+", Pattern.MULTILINE | Pattern.DOTALL);

    public static String truncate(String s, int maxLength) {
        if (s.length() <= maxLength) {
            return s;
        }
        return s.substring(0, maxLength) + "\n...";
    }

    public static boolean match(String a, String b) {
        String[] asplice = Arrays.stream(pattern.split(a)).filter(x -> x.length() > 0).toArray(i -> new String[i]);
        String[] bsplice = Arrays.stream(pattern.split(b)).filter(x -> x.length() > 0).toArray(i -> new String[i]);
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
