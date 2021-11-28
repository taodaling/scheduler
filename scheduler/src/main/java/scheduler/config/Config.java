package scheduler.config;

import java.util.List;
import java.util.Map;

public class Config {
    private String schedulerId;
    private Map<String, String> env;
    private List<TriggerConfig> triggers;

    public String getSchedulerId() {
        return schedulerId;
    }

    public void setSchedulerId(String schedulerId) {
        this.schedulerId = schedulerId;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public List<TriggerConfig> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<TriggerConfig> triggers) {
        this.triggers = triggers;
    }
}
