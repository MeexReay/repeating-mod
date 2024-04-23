package themixray.repeating.mod.event.events;

import themixray.repeating.mod.Main;
import themixray.repeating.mod.event.RecordEvent;

public class GuiCharTypeEvent extends RecordEvent {
    private char chr;
    private int modifiers;

    public GuiCharTypeEvent(char chr, int modifiers) {
        this.chr = chr;
        this.modifiers = modifiers;
    }

    public void replay() {
        if (Main.client.currentScreen != null) {
            Main.client.currentScreen.charTyped(chr, modifiers);
        }
    }

    protected String[] serializeArgs() {
        return new String[] {
            String.valueOf((int) chr),
            String.valueOf(modifiers)
        };
    }

    public static GuiCharTypeEvent deserialize(String[] args) {
        return new GuiCharTypeEvent(
            (char) Integer.parseInt(args[0]),
            Integer.parseInt(args[1])
        );
    }
}
