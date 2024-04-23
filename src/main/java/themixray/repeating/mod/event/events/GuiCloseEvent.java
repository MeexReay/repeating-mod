package themixray.repeating.mod.event.events;

import themixray.repeating.mod.Main;
import themixray.repeating.mod.event.RecordEvent;

public class GuiCloseEvent extends RecordEvent {
    public GuiCloseEvent() {}

    public void replay() {
        if (Main.client.currentScreen != null) {
            Main.client.setScreen(null);
        }
    }

    protected String[] serializeArgs() {
        return new String[] {};
    }

    public static GuiCloseEvent deserialize(String[] args) {
        return new GuiCloseEvent();
    }
}
