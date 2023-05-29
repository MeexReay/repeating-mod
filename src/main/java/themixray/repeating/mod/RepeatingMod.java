package themixray.repeating.mod;

import com.google.common.collect.Lists;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.MovementType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.*;

public class RepeatingMod implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("repeating-mod");
	public static final MinecraftClient client = MinecraftClient.getInstance();
	public static final FabricLoader loader = FabricLoader.getInstance();
	public static RepeatingMod me;

	public List<RecordEvent> record = new ArrayList<>();
	public boolean is_recording = false;
	public Date last_record = null;

	public Thread replay = null;
	public boolean is_replaying = false;
	public boolean loop_replay = false;
	public static boolean replay_sneaking = false;

	public static RepeatingScreen menu;
	private static KeyBinding menu_key;
	private static KeyBinding toggle_replay_key;
	private static KeyBinding toggle_record_key;

	public double record_blocks_limit = 2;
	public long record_time_limit = 50;

	public EasyConfig conf;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Repeating mod initialized");
		me = this;

		Map<String,Object> def = new HashMap<>();
		def.put("record_blocks_limit", record_blocks_limit);
		def.put("record_time_limit", record_time_limit);
		conf = new EasyConfig(new File(loader.getConfigDir().toFile(),"repeating-mod.yml").toPath(),def);

		record_blocks_limit = (double) conf.data.get("record_blocks_limit");
		record_time_limit = (Integer) conf.data.get("record_time_limit");

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
			if (menu_key.wasPressed()) {
				client.setScreen(menu);
			}
			if (toggle_replay_key.wasPressed()) {
				if (!is_recording) {
					if (is_replaying)
						stopReplay();
					else startReplay();
					menu.update_btns();
				}
			}
			if (toggle_record_key.wasPressed()) {
				if (!is_replaying) {
					if (is_recording)
						stopRecording();
					else startRecording();
					menu.update_btns();
				}
			}
		});
	}

	public RecordEvent getLastRecord(String t) {
		for (RecordEvent r:Lists.reverse(new ArrayList<>(record))) {
			if (r.getType().equals(t)) {
				return r;
			}
		}
		return null;
	}


	public void startRecording() {
		is_recording = true;
		menu.update_btns();
		record.clear();
		sendMessage(Text.translatable("message.repeating-mod.record_start"));
	}

	public void recordTick(RecordEvent e) {
		Date now = new Date();
		if (last_record != null) {
			long diff = now.getTime() - last_record.getTime();
			if (diff >= 0) record.add(new RecordDelayEvent(diff));
		}
		record.add(e);
		last_record = now;
	}

	public void stopRecording() {
		is_recording = false;
		menu.update_btns();
		last_record = null;
		sendMessage(Text.translatable("message.repeating-mod.record_stop"));
	}


	public void startReplay() {
		is_recording = false;
		is_replaying = true;
		menu.update_btns();
		client.player.setNoGravity(true);
		replay = new Thread(() -> {
			while (true) {
				for (RecordEvent e : record)
					if (is_replaying)
						e.callback();
				if (!loop_replay || !is_replaying) break;
			}
			stopReplay();
		});
		replay.start();
		sendMessage(Text.translatable("message.repeating-mod.replay_start"));
	}

	public void stopReplay() {
		is_recording = false;
		is_replaying = false;
		menu.update_btns();
		client.player.setNoGravity(false);
		sendMessage(Text.translatable("message.repeating-mod.replay_stop"));
	}

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		long factor = (long) Math.pow(10, places);
		return (double) Math.round(value * factor) / factor;
	}

	public static void sendMessage(Text text) {
		client.player.sendMessage(Text.literal("[")
				.append(Text.translatable("text.repeating-mod.name"))
				.append("] ").append(text));
	}

	public static abstract class RecordEvent {
		abstract void callback();
		abstract String toText();
		abstract String getType();

		public static RecordEvent fromText(String t) {
			try {
				String type = String.valueOf(t.charAt(0));
				String[] args = t.substring(2).split("&");
				if (type.equals("d")) {
					return new RecordDelayEvent(
						Long.parseLong(args[0]));
				} else if (type.equals("m")) {
					return new RecordMoveEvent(new Vec3d(
							Double.parseDouble(args[0]),
							Double.parseDouble(args[1]),
							Double.parseDouble(args[2])),
						Float.parseFloat(args[3]),
						Float.parseFloat(args[4]));
				} else if (type.equals("s")) {
					return new RecordSneakEvent(
						args[0].equals("1"));
				} else if (type.equals("b")) {
					return new RecordBlockBreakEvent(new BlockPos(
							Integer.parseInt(args[0]),
							Integer.parseInt(args[1]),
							Integer.parseInt(args[2])));
				} else if (type.equals("i")) {
					return new RecordBlockInteractEvent(
							Hand.valueOf(args[5]),
							new BlockHitResult(new Vec3d(
									Double.parseDouble(args[0]),
									Double.parseDouble(args[1]),
									Double.parseDouble(args[2])),
								Direction.byId(Integer.parseInt(args[4])),
								new BlockPos(
									Integer.parseInt(args[0]),
									Integer.parseInt(args[1]),
									Integer.parseInt(args[2])),
								args[3].equals("1")));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static class RecordDelayEvent extends RecordEvent {
		public long delay;

		public RecordDelayEvent(long delay) {
			this.delay = delay;
		}

		public void callback() {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public String toText() {
			return "d="+delay;
		}
		public String getType() {
			return "delay";
		}
	}

	public static class RecordMoveEvent extends RecordEvent {
		public Vec3d vec;
		public float yaw;
		public float pitch;

		public RecordMoveEvent(Vec3d vec,float yaw,float pitch) {
			this.vec = vec;
			this.yaw = yaw;
			this.pitch = pitch;
		}

		public void callback() {
			Vec3d p = client.player.getPos();
			Vec3d v = new Vec3d(vec.getX()-p.getX(),vec.getY()-p.getY(),vec.getZ()-p.getZ());
			client.player.move(MovementType.SELF,v);
			client.player.setYaw(yaw);
			client.player.setPitch(pitch);
		}

		public String toText() {
			return "m="+vec.getX()+"&"+vec.getY()+"&"+vec.getZ()+"&"+yaw+"&"+pitch;
		}
		public String getType() {
			return "move";
		}
	}

	public static class RecordSneakEvent extends RecordEvent {
		public boolean sneaking;

		public RecordSneakEvent(boolean sneaking) {
			this.sneaking = sneaking;
		}

		public void callback() {
			RepeatingMod.replay_sneaking = sneaking;
		}

		public String toText() {
			return "s="+(sneaking?"1":"0");
		}
		public String getType() {
			return "sneak";
		}
	}

	public static class RecordBlockBreakEvent extends RecordEvent {
		public BlockPos pos;

		public RecordBlockBreakEvent(
				BlockPos pos) {
			this.pos = pos;
		}

		public void callback() {
			client.interactionManager.breakBlock(pos);
		}

		public String toText() {
			return "b="+pos.getX()+"&"+pos.getY()+"&"+pos.getZ();
		}
		public String getType() {
			return "block_break";
		}
	}

	public static class RecordBlockInteractEvent extends RecordEvent {
		public Hand hand;
		public BlockHitResult hitResult;

		public RecordBlockInteractEvent(Hand hand, BlockHitResult hitResult) {
			this.hand = hand;
			this.hitResult = hitResult;
		}

		public void callback() {
			client.interactionManager.interactBlock(client.player,hand,hitResult);
		}

		public String toText() {
			return "i="+hitResult.getBlockPos().getX()+"&"+hitResult.getBlockPos().getY()+"&"+hitResult.getBlockPos().getZ()+
					"&"+(hitResult.isInsideBlock()?"1":"0")+"&"+hitResult.getSide().getId()+"&"+hand.name();
		}
		public String getType() {
			return "block_interact";
		}
	}
}
