package scheduler.triggers;

import utils.ErrorUtils;

public class Periodical extends AbstractTrigger {
    Long periodically;

    public Long getPeriodically() {
        return periodically;
    }

    public void setPeriodically(Long periodically) {
        this.periodically = periodically;
    }

    @Override
    public void run() {
        try {
            while (true) {
                execute(context, this);
                Thread.sleep(periodically);
            }
        } catch (Exception e) {
            ErrorUtils.throwAsRuntimeException(e);
        }
    }
}
