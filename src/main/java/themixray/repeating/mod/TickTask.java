package themixray.repeating.mod;

import java.util.ArrayList;
import java.util.List;

public abstract class TickTask implements Runnable {
    public static List<TickTask> tasks = new ArrayList<>();

    public static void tickTasks(TickAt at) {
        for (TickTask t:new ArrayList<>(tasks))
            if (t.getAt() == at) t.tick();
    }

    private long living;
    private long delay;

    private boolean is_repeating;
    private long period;

    private boolean is_cancelled;
    private TickAt at;

    public enum TickAt {
        CLIENT_HEAD, CLIENT_TAIL,
        MOVEMENT_HEAD, MOVEMENT_TAIL,
        RENDER_HEAD, RENDER_TAIL
    }

    public TickTask(long delay, TickAt at) {
        this.is_cancelled = false;
        this.is_repeating = false;
        this.delay = delay;
        this.living = 0;
        this.period = 0;
        this.at = at;
        tasks.add(this);
    }

    public TickTask(long delay, long period, TickAt at) {
        this.is_cancelled = false;
        this.is_repeating = true;
        this.delay = delay;
        this.period = period;
        this.living = 0;
        this.at = at;
        tasks.add(this);
    }

    public TickTask(long delay) {
        this(delay,TickAt.CLIENT_HEAD);
    }

    public TickTask(long delay, long period) {
        this(delay,period,TickAt.CLIENT_HEAD);
    }

    public void cancel() {
        if (!is_cancelled) {
            is_cancelled = true;
            tasks.remove(this);
        }
    }

    public boolean isCancelled() {
        return is_cancelled;
    }

    public TickAt getAt() {
        return at;
    }

    public void setDelay(long delay) {
        if (is_repeating) {
            this.delay = delay;
        }
    }
    public long getDelay() {
        return this.delay;
    }

    public void tick() {
        if (living >= delay) {
            if (is_repeating) {
                delay = period;
                run();
                living = -1;
            } else {
                run();
                cancel();
            }
        }
        living++;
    }
}
