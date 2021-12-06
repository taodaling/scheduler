package scheduler;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import scheduler.config.Config;
import scheduler.config.JobConfig;
import scheduler.config.TriggerConfig;
import scheduler.context.Context;
import scheduler.jobs.Dependency;
import scheduler.jobs.Job;
import scheduler.triggers.AbstractTrigger;
import utils.FileUtils;
import utils.JsonUtils;
import utils.ReflectUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scheduler {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>(System.getenv());
        map.put("_work_directory", System.getProperty("user.dir"));
        String configPath = "scheduler.json";
        if (args.length > 0) {
            configPath = args[0];
        }
        File configFile = new File(configPath);
        Preconditions.checkArgument(configFile.exists(), "config file not exist: pass scheduler.json as the second argument");
        String configContent = FileUtils.readFile(new File(configPath));
        Config config = JsonUtils.parse(configContent, Config.class);

        map.put("schedulerId", config.getSchedulerId());
        map.putAll(config.getEnv());
        Context context = new Context(map);

        for (TriggerConfig triggerConfig : config.getTriggers()) {
            System.out.printf("starting trigger [%s]...", triggerConfig.getName());
            AbstractTrigger trigger = createTrigger(triggerConfig, context);
            Thread t = new Thread(trigger);
            t.setDaemon(false);
            t.start();
            System.out.println("success!");
        }
    }

    public static AbstractTrigger createTrigger(TriggerConfig triggerConfig, Context context) {
        context = Context.inherit(context, triggerConfig.getEnv());
        AbstractTrigger trigger = (AbstractTrigger) ReflectUtils.newInstance(triggerConfig.getType());

        Map<String, Job> jobs = new HashMap<>();
        Map<String, List<Dependency>> dependencies = new HashMap<>();
        for (JobConfig jobConfig : triggerConfig.getJobs()) {
            Preconditions.checkArgument(!jobs.containsKey(jobConfig.getName()), "duplicate job [%s] defined in trigger [%s]", jobConfig.getName(), triggerConfig.getName());
            Job job = new Job();
            job.setName(jobConfig.getName());
            job.setCommands(jobConfig.getCommands());
            job.setFrontend(jobConfig.isFrontend());
            job.setEnv(jobConfig.getEnv());
            job.setTimeout(jobConfig.getTimeout());
            jobs.put(jobConfig.getName(), job);
            dependencies.put(jobConfig.getName(), new ArrayList<>());
        }
        for (JobConfig jobConfig : triggerConfig.getJobs()) {
            Job job = jobs.get(jobConfig.getName());
            for (String dependency : jobConfig.getDependsOn()) {
                Predicate<Boolean> pred;
                if (dependency.endsWith("!")) {
                    dependency = dependency.substring(0, dependency.length() - 1);
                    pred = x -> !x;
                } else if (dependency.endsWith("?")) {
                    dependency = dependency.substring(0, dependency.length() - 1);
                    pred = x -> true;
                } else {
                    pred = x -> x;
                }
                Preconditions.checkArgument(jobs.containsKey(dependency), "can't find dependency [%s] for [%s]", dependency, job.getName());
                Dependency d = new Dependency(jobs.get(dependency), job, pred);
                dependencies.get(d.getA().getName()).add(d);
            }
        }

        trigger.setName(triggerConfig.getName());
        trigger.setDependencies(dependencies);
        trigger.setJobList(new ArrayList<>(jobs.values()));
        trigger.setName(triggerConfig.getName());
        trigger.setContext(context);
        return trigger;
    }
}
