package themixray.repeating.mod.event.events;

import themixray.repeating.mod.Main;
import themixray.repeating.mod.event.RecordEvent;

public class GuiMouseMoveEvent extends RecordEvent {
    private double mouseX;
    private double mouseY;

    public GuiMouseMoveEvent(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public void replay() {
        if (Main.client.currentScreen != null) {
            Main.client.currentScreen.mouseMoved(mouseX, mouseY);
        }
    }

    protected String[] serializeArgs() {
        return new String[] {
            String.valueOf(mouseX),
            String.valueOf(mouseY)
        };
    }

    public static GuiMouseMoveEvent deserialize(String[] args) {
        return new GuiMouseMoveEvent(
            Double.parseDouble(args[0]),
            Double.parseDouble(args[1])
        );
    }
}
