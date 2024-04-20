package themixray.repeating.mod.events;

public abstract class RecordEvent {
    public abstract void replay();
    public abstract String serialize();
    public abstract String getType();

    public static RecordEvent deserialize(String t) {
        try {
            String type = String.valueOf(t.charAt(0));
            String[] args = t.substring(2).split("&");
            if (type.equals("d")) {
                return RecordDelayEvent.fromArgs(args);
            } else if (type.equals("m")) {
                return RecordMoveEvent.fromArgs(args);
            } else if (type.equals("p")) {
                return RecordInputEvent.fromArgs(args);
            } else if (type.equals("b")) {
                return RecordBlockBreakEvent.fromArgs(args);
            } else if (type.equals("i")) {
                return RecordBlockInteractEvent.fromArgs(args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
