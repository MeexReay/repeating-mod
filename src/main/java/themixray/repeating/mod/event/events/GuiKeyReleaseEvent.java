package themixray.repeating.mod.event.events;

import themixray.repeating.mod.Main;
import themixray.repeating.mod.event.RecordEvent;

public class GuiKeyReleaseEvent extends RecordEvent {
    private int keyCode;
    private int scanCode;
    private int modifiers;

    public GuiKeyReleaseEvent(int keyCode, int scanCode, int modifiers) {
        this.keyCode = keyCode;
        this.scanCode = scanCode;
        this.modifiers = modifiers;
    }

    public void replay() {
        if (Main.client.currentScreen != null) {
            Main.client.currentScreen.keyReleased(keyCode, scanCode, modifiers);
        }
    }

    protected String[] serializeArgs() {
        return new String[] {
            String.valueOf(keyCode),
            String.valueOf(scanCode),
            String.valueOf(modifiers)
        };
    }

    public static GuiKeyReleaseEvent deserialize(String[] args) {
        return new GuiKeyReleaseEvent(
            Integer.parseInt(args[0]),
            Integer.parseInt(args[1]),
            Integer.parseInt(args[2])
        );
    }
}
