package scheduler.jobs;

import scheduler.context.Context;
import utils.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Job {
    String name;
    List<String> commands;
    boolean frontend;
    Map<String, String> env;
    Long timeout;

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public boolean isFrontend() {
        return frontend;
    }

    public void setFrontend(boolean frontend) {
        this.frontend = frontend;
    }

    public boolean invoke(Context context) throws Exception {
        List<String> commands = this.commands.stream().map(context::translate).collect(Collectors.toList());
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File(context.get("_work_directory")));
        if (env != null) {
            for (Map.Entry<String, String> kv : env.entrySet()) {
                pb.environment().put(context.translate(kv.getKey()), context.translate(kv.getValue()));
            }
        }
        if (frontend) {
            pb.inheritIO();
            Process process = pb.start();
            boolean exited = process.waitFor(timeout, TimeUnit.MILLISECONDS);
            if (!exited) {
                process.destroyForcibly();
                return false;
            }
            return process.exitValue() == 0;
        } else {
            String parent = context.get("_work_directory") + "/logs/" + context.get("schedulerId") + "/" + context.get("_trigger_name") + "/" + name;
            File stdout = new File(parent + "/" + "stdout");
            File stderr = new File(parent + "/" + "stderr");
            FileUtils.createFile(stdout);
            FileUtils.createFile(stderr);
            pb.redirectError(ProcessBuilder.Redirect.appendTo(stderr));
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(stdout));
            pb.start();
            return true;
        }
    }

    public String getName() {
        return name;
    }

}
