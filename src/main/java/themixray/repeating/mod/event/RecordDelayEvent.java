package themixray.repeating.mod.events;

public class RecordDelayEvent extends RecordEvent {
    public long delay;

    public static RecordDelayEvent fromArgs(String[] a) {
        return new RecordDelayEvent(Long.parseLong(a[0]));
    }

    public RecordDelayEvent(long delay) {
        this.delay = delay;
    }

    public void replay() {
        try {
            Thread.sleep(delay / 20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String serialize() {
        return "d=" + delay;
    }

    public String getType() {
        return "delay";
    }
}
