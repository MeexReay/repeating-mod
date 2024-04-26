package themixray.repeating.mod.event;

import themixray.repeating.mod.event.events.*;

public abstract class RecordEvent {
    public abstract void replay();
    public RecordEventType getType() {
        for (RecordEventType ev : RecordEventType.values()) {
            if (ev.getEventClass().getTypeName().equals(this.getClass().getTypeName())) {
                return ev;
            }
        }
        return null;
    }
    protected abstract String[] serializeArgs();
    public String serialize() {
        return getType().getChar() + "=" + String.join("&", serializeArgs());
    }
    public static RecordEvent deserialize(String t) {
        return RecordEventType.getByChar(t.charAt(0)).deserialize(t.substring(2).split("&"));
    }
}
