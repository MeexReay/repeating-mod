package themixray.repeating.mod.event.events;

import themixray.repeating.mod.event.RecordEvent;

public class DelayEvent extends RecordEvent {
    public long delay;

    public static DelayEvent deserialize(String[] a) {
        return new DelayEvent(Long.parseLong(a[0]));
    }

    public DelayEvent(long delay) {
        this.delay = delay;
    }

    public void replay() {
        try {
            Thread.sleep(delay / 20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected String[] serializeArgs() {
        return new String[]{
                String.valueOf(delay)
        };
    }
}
