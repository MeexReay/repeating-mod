package themixray.repeating.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import themixray.repeating.mod.event.events.DelayEvent;
import themixray.repeating.mod.event.RecordEvent;
import themixray.repeating.mod.event.events.InputEvent;
import themixray.repeating.mod.event.events.MoveEvent;
import themixray.repeating.mod.render.RenderHelper;
import themixray.repeating.mod.render.RenderSystem;
import themixray.repeating.mod.render.buffer.WorldBuffer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Main implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("repeating-mod");
	public static final MinecraftClient client = MinecraftClient.getInstance();
	public static final FabricLoader loader = FabricLoader.getInstance();
	public static Main me;

	public RecordList record_list;
	public RecordState now_record;

	public boolean is_recording = false;
	public long last_record = -1;
	public TickTask move_tick = null;

	public TickTask replay_tick = null;
	public boolean is_replaying = false;
	public boolean loop_replay = false;
	public static InputEvent input_replay = null;

	public long living_ticks = 0;

	public static RepeatingScreen menu;
	private static KeyBinding menu_key;
	private static KeyBinding toggle_replay_key;
	private static KeyBinding toggle_record_key;

	public long record_pos_delay = 20;

	public static Random rand = new Random();

	public EasyConfig conf;
	public File records_folder;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Repeating mod initialized");
		me = this;

		now_record = null;

		records_folder = new File(FabricLoader.getInstance().getGameDir().toFile(),"repeating_mod_records");
		if (!records_folder.exists()) records_folder.mkdir();

		record_list = new RecordList(records_folder);
		record_list.loadRecords();

		RenderSystem.init();
		WorldRenderEvents.LAST.register(context -> {
			WorldBuffer buffer = RenderHelper.startTri(context);
			if (now_record != null) {
				Vec3d start_pos = now_record.getStartRecordPos();
				Vec3d finish_pos = now_record.getFinishRecordPos();

				if (start_pos != null) drawRecordPos(buffer, start_pos, new Color(70, 230, 70, 128));
				if (finish_pos != null) drawRecordPos(buffer, finish_pos, new Color(230, 70, 70, 128));
			}
			RenderHelper.endTri(buffer);
		});

		Map<String,String> def = new HashMap<>();
		def.put("record_pos_delay", String.valueOf(record_pos_delay));

		conf = new EasyConfig(loader.getConfigDir(),"repeating-mod",def);

		record_pos_delay = Long.parseLong(conf.data.get("record_pos_delay"));

		menu_key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.repeating-mod.menu",InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_J,"text.repeating-mod.name"));
		toggle_replay_key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.repeating-mod.toggle_replay",InputUtil.Type.KEYSYM,
				-1,"text.repeating-mod.name"));
		toggle_record_key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.repeating-mod.toggle_record",InputUtil.Type.KEYSYM,
				-1,"text.repeating-mod.name"));

		menu = new RepeatingScreen();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (menu_key.wasPressed())
				client.setScreen(menu);
			if (toggle_replay_key.wasPressed()) {
				if (now_record != null) {
					if (!is_recording) {
						if (is_replaying)
							stopReplay();
						else startReplay();
						menu.updateButtons();
					}
				}
			}
			if (toggle_record_key.wasPressed()) {
				if (!is_replaying) {
					if (is_recording)
						stopRecording();
					else startRecording();
					menu.updateButtons();
				}
			}
		});

		new TickTask(0,0) {
			@Override
			public void run() {
				living_ticks++;
			}
		};

		System.setProperty("java.awt.headless", "false");
	}

	public void setNowRecord(RecordState record) {
		now_record = record;
	}

	public void drawRecordPos(WorldBuffer buffer, Vec3d pos, Color color) {
		RenderHelper.drawRectFromTri(buffer,
				(float) pos.getX() - 0.25F,
				(float) pos.getY() + 0.01F,
				(float) pos.getZ() - 0.25F,

				(float) pos.getX() + 0.25F,
				(float) pos.getY() + 0.01F,
				(float) pos.getZ() - 0.25F,

				(float) pos.getX() + 0.25F,
				(float) pos.getY() + 0.01F,
				(float) pos.getZ() + 0.25F,

				(float) pos.getX() - 0.25F,
				(float) pos.getY() + 0.01F,
				(float) pos.getZ() + 0.25F,
				color);
	}

	public void startRecording() {
		is_recording = true;
		menu.updateButtons();

		now_record = record_list.newRecord();

		Vec3d start_pos = client.player.getPos();
		now_record.addEvent(new MoveEvent(start_pos,client.player.getHeadYaw(),client.player.getPitch()));
		now_record.setStartRecordPos(start_pos);

		if (record_pos_delay > 0) {
			move_tick = new TickTask(
					record_pos_delay,
					record_pos_delay) {
				@Override
				public void run() {
					now_record.addEvent(new MoveEvent(client.player.getPos(),
							client.player.getHeadYaw(), client.player.getPitch()));
				}
			};
		}

		sendMessage(Text.translatable("message.repeating-mod.record_start"));
	}

	public void recordTick(RecordEvent e) {
		if (is_recording) {
			long now = living_ticks;
			if (last_record != -1) {
				long diff = now - last_record - 2;
				if (diff > 0) now_record.addEvent(new DelayEvent(diff));
			}
			now_record.addEvent(e);
			last_record = now;
		}
	}

	public void recordAllInput() {
		if (client.player == null) {
			stopRecording();
			return;
		}

		InputEvent l = ((InputEvent) now_record.getLastEvent("input"));
		if (l == null) {
			InputEvent e = new InputEvent(
				client.player.input.sneaking,
				client.player.input.jumping,
				client.player.input.movementSideways,
				client.player.input.movementForward,
				client.player.input.pressingForward,
				client.player.input.pressingBack,
				client.player.input.pressingLeft,
				client.player.input.pressingRight,
				client.player.getHeadYaw(),
				client.player.getBodyYaw(),
				client.player.getPitch(),
				client.player.isSprinting(),
				client.player.getYaw(),
				client.player.getMovementSpeed());
			recordTick(e);
		} else {
			InputEvent e = new InputEvent(
				((Boolean) client.player.input.sneaking == l.sneaking) ? null : client.player.input.sneaking,
				((Boolean) client.player.input.jumping == l.jumping) ? null : client.player.input.jumping,
				(((Float) client.player.input.movementSideways).equals(l.movementSideways)) ? null : client.player.input.movementSideways,
				(((Float) client.player.input.movementForward).equals(l.movementForward)) ? null : client.player.input.movementForward,
				((Boolean) client.player.input.pressingForward == l.pressingForward) ? null : client.player.input.pressingForward,
				((Boolean) client.player.input.pressingBack == l.pressingBack) ? null : client.player.input.pressingBack,
				((Boolean) client.player.input.pressingLeft == l.pressingLeft) ? null : client.player.input.pressingLeft,
				((Boolean) client.player.input.pressingRight == l.pressingRight) ? null : client.player.input.pressingRight,
				client.player.getHeadYaw(), Main.client.player.getBodyYaw(),client.player.getPitch(),
				((Boolean) client.player.isSprinting() == l.sprinting) ? null : client.player.isSprinting(),
				client.player.getYaw(),client.player.getMovementSpeed());

			if (!(e.isEmpty() &&
					e.yaw == l.yaw &&
					e.head_yaw == l.head_yaw &&
					e.pitch == l.pitch &&
					e.body_yaw == l.body_yaw)) {
				e.fillEmpty(l);
				recordTick(e);
			}
		}
	}

	public void stopRecording() {
		is_recording = false;
		now_record.setFinishRecordPos(client.player.getPos());
        try {
            now_record.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (move_tick != null) {
			move_tick.cancel();
			move_tick = null;
		}
		menu.updateButtons();
		last_record = -1;
		sendMessage(Text.translatable("message.repeating-mod.record_stop"));
	}


	public void startReplay() {
		is_recording = false;
		is_replaying = true;
		menu.updateButtons();

		List<RecordEvent> events = now_record.getEvents();

		replay_tick = new TickTask(0,0, TickTask.TickAt.CLIENT_TAIL) {
			public int replay_index = 0;

			@Override
			public void run() {
				if (!is_replaying) cancel();
				RecordEvent e = events.get(replay_index);
				if (e instanceof DelayEvent) {
					setDelay(((DelayEvent) e).delay);
				} else {
					e.replay();
				}

				replay_index++;
				if (!loop_replay) {
					if (replay_index == events.size()) {
						stopReplay();
						cancel();
					}
				} else if (replay_index == events.size()) {
					replay_index = 0;
				}
			}
		};

		sendMessage(Text.translatable("message.repeating-mod.replay_start"));
	}

	public void stopReplay() {
		is_recording = false;
		is_replaying = false;
		if (replay_tick != null) {
			replay_tick.cancel();
			replay_tick = null;
		}
		menu.updateButtons();
		record_list.getWidget().getWidget(now_record).getChildren().get(3).setMessage(Text.translatable("text.repeating-mod.start"));
		sendMessage(Text.translatable("message.repeating-mod.replay_stop"));
	}

	public static void sendMessage(MutableText text) {
		client.player.sendMessage(Text.literal("[")
			.append(Text.translatable("text.repeating-mod.name"))
			.append("] ").formatted(Formatting.BOLD,Formatting.DARK_GRAY)
			.append(text.formatted(Formatting.RESET).formatted(Formatting.GRAY)));
	}

	public static void sendDebug(String s) {
		client.player.sendMessage(Text.literal("[DEBUG] ").append(Text.of(s)));
	}
}
