package scheduler.triggers;

import utils.ErrorUtils;

import java.io.File;

public class FileEvent extends AbstractTrigger {

    @Override
    public void run() {
        try {
            File file = new File(context.get("filename"));
            Long interval = context.get("interval") == null ? 1000 : Long.parseLong(context.get("interval"));
            long lastTimeStamp = Long.MIN_VALUE;
            while (true) {
                if(file.exists()) {
                    long updateTime = file.lastModified();
                    if (updateTime != lastTimeStamp) {
                        lastTimeStamp = updateTime;
                        execute(context, this);
                    }
                }
                Thread.sleep(interval);
            }
        } catch (Exception e) {
            ErrorUtils.throwAsRuntimeException(e);
        }
    }
}
