package themixray.repeating.mod.event.events;

import themixray.repeating.mod.Main;
import themixray.repeating.mod.event.RecordEvent;

public class GuiMouseDragEvent extends RecordEvent {
    private double mouseX;
    private double mouseY;
    private double deltaX;
    private double deltaY;
    private int button;

    public GuiMouseDragEvent(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.button = button;
    }

    public void replay() {
        if (Main.client.currentScreen != null) {
            Main.client.currentScreen.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    protected String[] serializeArgs() {
        return new String[] {
            String.valueOf(mouseX),
            String.valueOf(mouseY),
            String.valueOf(button),
            String.valueOf(deltaX),
            String.valueOf(deltaY)
        };
    }

    public static GuiMouseDragEvent deserialize(String[] args) {
        return new GuiMouseDragEvent(
            Double.parseDouble(args[0]),
            Double.parseDouble(args[1]),
            Integer.parseInt(args[2]),
            Double.parseDouble(args[3]),
            Double.parseDouble(args[4])
        );
    }
}
