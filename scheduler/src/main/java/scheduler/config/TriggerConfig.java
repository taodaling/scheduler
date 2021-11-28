package scheduler.config;

import java.util.List;
import java.util.Map;

public class TriggerConfig {
    private List<JobConfig> jobs;
    private Map<String, String> env;
    private String name;
    private String type;

    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JobConfig> getJobs() {
        return jobs;
    }


    public void setJobs(List<JobConfig> jobs) {
        this.jobs = jobs;
    }

}
