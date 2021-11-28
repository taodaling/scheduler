package scheduler.triggers;

import utils.ErrorUtils;

public class Once extends AbstractTrigger {
    @Override
    public void run() {
        try {
            execute(context, this);
        } catch (Exception e) {
            ErrorUtils.throwAsRuntimeException(e);
        }
    }
}
