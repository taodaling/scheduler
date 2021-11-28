package scheduler.config;

import java.util.List;
import java.util.Map;

public class JobConfig {
    private String name;
    private List<String> commands;
    private List<String> dependsOn;
    private Map<String, String> env;
    private boolean frontend = true;
    private Long timeout = 10000L;

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

    public boolean isFrontend() {
        return frontend;
    }

    public void setFrontend(boolean frontend) {
        this.frontend = frontend;
    }

    public String getName() {
        return name;
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

    public List<String> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(List<String> dependsOn) {
        this.dependsOn = dependsOn;
    }
}
