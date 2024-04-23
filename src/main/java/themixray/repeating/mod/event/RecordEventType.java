package themixray.repeating.mod.event;

import themixray.repeating.mod.event.events.*;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public enum RecordEventType {
    BLOCK_BREAK('b',"block_break",BlockBreakEvent.class),
    BLOCK_INTERACT('i',"block_interact",BlockInteractEvent.class),
    DELAY('d',"delay",DelayEvent.class),
    INPUT('p',"input",InputEvent.class),
    MOVE('m',"move",MoveEvent.class),
    GUI_KEY_PRESS('r',"key_press",BlockBreakEvent.class),
    GUI_KEY_RELEASE('s',"key_release",BlockBreakEvent.class),
    GUI_CHAR_TYPE('h',"char_type",BlockBreakEvent.class),
    GUI_MOUSE_CLICK('c',"mouse_click",BlockBreakEvent.class),
    GUI_MOUSE_RELEASE('l',"mouse_release",BlockBreakEvent.class),
    GUI_MOUSE_DRAG('g',"mouse_drag",BlockBreakEvent.class),
    GUI_MOUSE_SCROLL('o',"mouse_scroll",BlockBreakEvent.class),
    GUI_CLOSE('e',"close",BlockBreakEvent.class);

    private Class<? extends RecordEvent> ev;
    private char ch;
    private String name;

    RecordEventType(char ch, String name, Class<? extends RecordEvent> ev) {
        this.ev = ev;
        this.ch = ch;
        this.name = name;
    }

    public Class<? extends RecordEvent> getEventClass() {
        return ev;
    }

    public char getChar() {
        return ch;
    }

    public String getName() {
        return name;
    }

    public RecordEvent deserialize(String[] args) {
        try {
            return (RecordEvent) ev
                .getMethod("deserialize", String[].class)
                .invoke(null, (Object) args);
        } catch (Throwable e) {
            return null;
        }
    }

    public String serialize(RecordEvent event) {
        return event.serialize();
    }

    public static RecordEventType getByChar(String type) {
        return getByChar(type.charAt(0));
    }

    public static RecordEventType getByChar(char ch) {
        for (RecordEventType t : values()) {
            if (t.getChar() == ch) {
                return t;
            }
        }
        return null;
    }
}
