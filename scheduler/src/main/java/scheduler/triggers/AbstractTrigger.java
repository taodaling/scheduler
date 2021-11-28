package scheduler.triggers;

import scheduler.context.Context;
import scheduler.jobs.Dependency;
import scheduler.jobs.Job;

import java.util.*;

public abstract class AbstractTrigger implements Runnable {
    List<Job> jobList = new ArrayList<>();
    String name;
    Context context;
    Map<String, List<Dependency>> dependencies;

    public Map<String, List<Dependency>> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<String, List<Dependency>> dependencies) {
        this.dependencies = dependencies;
    }

    public List<Job> getJobList() {
        return jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static synchronized void execute(Context context, AbstractTrigger trigger) throws Exception {
        System.out.printf("==============trigger [%s]==============", trigger.name).println();

        //add necessary header
        Map<String, String> map = new HashMap<>();
        map.put("timestamp", Long.toString(System.currentTimeMillis()));
        map.put("_trigger_name", trigger.name);
        context = Context.inherit(context, map);

        Map<String, Integer> deg = new HashMap<>();
        for(Job job : trigger.jobList){
            deg.put(job.getName(), 0);
        }
        for(List<Dependency> dependencies : trigger.dependencies.values()){
            for(Dependency d : dependencies){
                deg.put(d.getB().getName(), deg.get(d.getB().getName()) + 1);
            }
        }

        Deque<Job> pending = new ArrayDeque<>();
        for (Job j : trigger.jobList) {
            if(deg.get(j.getName()).equals(0)){
                pending.addLast(j);
            }
        }
        while (!pending.isEmpty()) {
            Job head = pending.removeFirst();
            System.out.printf("job [%s]...", head.getName()).println();
            boolean res;
            try {
                res = head.invoke(context);
            }catch (Exception e){
                res = false;
                e.printStackTrace();
            }
            for (Dependency d : trigger.dependencies.get(head.getName())) {
                if (!d.getPredicate().test(res)) {
                    continue;
                }
                deg.put(d.getB().getName(), deg.get(d.getB().getName()) - 1);
                if (deg.get(d.getB().getName()).equals(0)) {
                    pending.addLast(d.getB());
                }
            }
        }
    }


}
