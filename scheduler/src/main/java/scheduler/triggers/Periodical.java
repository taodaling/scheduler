package scheduler.triggers;

import utils.ErrorUtils;

public class Periodical extends AbstractTrigger {
    @Override
    public void run() {
        try {
            long interval = Long.parseLong(context.get("interval"));
            while (true) {
                execute(context, this);
                Thread.sleep(interval);
            }
        } catch (Exception e) {
            ErrorUtils.throwAsRuntimeException(e);
        }
    }
}
