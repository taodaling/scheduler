package scheduler.triggers;

import scheduler.context.Context;
import utils.ErrorUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FileEvent extends AbstractTrigger {

    @Override
    public void run() {
        try {
            File[] files = Arrays.stream(context.get("filenames").split(","))
                    .map(String::trim)
                    .map(File::new).toArray(k -> new File[k]);
            long[] lastModified = Arrays.stream(files).mapToLong(File::lastModified).toArray();

            Long interval = context.get("interval") == null ? 1000 : Long.parseLong(context.get("interval"));
            while (true) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.exists()) {
                        long updateTime = file.lastModified();
                        if (updateTime != lastModified[i]) {
                            lastModified[i] = updateTime;
                            Map<String, String> override = new HashMap<>();
                            override.put("_changed_filepath", file.getAbsolutePath());
                            override.put("_changed_filename", file.getName());
                            execute(Context.inherit(context, override), this);
                        }
                    }
                }
                Thread.sleep(interval);
            }
        } catch (Exception e) {
            ErrorUtils.throwAsRuntimeException(e);
        }
    }
}
