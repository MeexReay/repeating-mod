package themixray.repeating.mod;

import com.google.common.collect.Lists;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.MovementType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class RepeatingMod implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("repeating-mod");
	public static final MinecraftClient client = MinecraftClient.getInstance();
	public static final FabricLoader loader = FabricLoader.getInstance();
	public static RepeatingMod me;

	public Thread move_tick = null;
	public List<RecordEvent> record = new ArrayList<>();
	public boolean is_recording = false;
	public Date last_record = null;

	public Thread replay = null;
	public boolean is_replaying = false;
	public boolean loop_replay = false;
	public static RecordInputEvent input_replay = null;

	public static RepeatingScreen menu;
	private static KeyBinding menu_key;
	private static KeyBinding toggle_replay_key;
	private static KeyBinding toggle_record_key;

	public long record_pos_delay = 1000;

	public EasyConfig conf;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Repeating mod initialized");
		me = this;

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

		record.add(new RecordMoveEvent(client.player.getPos(),
				client.player.getHeadYaw(), client.player.getPitch()));

		if (record_pos_delay > 0) {
			move_tick = new Thread(() -> {
				while (is_recording) {
					try {
						Thread.sleep(record_pos_delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					record.add(new RecordMoveEvent(client.player.getPos(),
							client.player.getHeadYaw(), client.player.getPitch()));
				}
			});
			move_tick.start();
		}

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

	public void recordAllInput() {
		RecordInputEvent l = ((RecordInputEvent)getLastRecord("input"));
		if (l == null) {
			RecordInputEvent e = new RecordInputEvent(
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
			RecordInputEvent e = new RecordInputEvent(
				((Boolean) client.player.input.sneaking == l.sneaking) ? null : client.player.input.sneaking,
				((Boolean) client.player.input.jumping == l.jumping) ? null : client.player.input.jumping,
				(((Float) client.player.input.movementSideways).equals(l.movementSideways)) ? null : client.player.input.movementSideways,
				(((Float) client.player.input.movementForward).equals(l.movementForward)) ? null : client.player.input.movementForward,
				((Boolean) client.player.input.pressingForward == l.pressingForward) ? null : client.player.input.pressingForward,
				((Boolean) client.player.input.pressingBack == l.pressingBack) ? null : client.player.input.pressingBack,
				((Boolean) client.player.input.pressingLeft == l.pressingLeft) ? null : client.player.input.pressingLeft,
				((Boolean) client.player.input.pressingRight == l.pressingRight) ? null : client.player.input.pressingRight,
				client.player.getHeadYaw(),RepeatingMod.client.player.getBodyYaw(),client.player.getPitch(),
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

	public void recordCameraInput() {
		RecordInputEvent l = ((RecordInputEvent)getLastRecord("input"));
		if (l == null) {
			RecordInputEvent e = new RecordInputEvent(
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
			RecordInputEvent e = new RecordInputEvent(null,null,null,
					null,null,null,null,null,
					client.player.getHeadYaw(),RepeatingMod.client.player.getBodyYaw(),client.player.getPitch(),
					null,client.player.getYaw(),client.player.getMovementSpeed());

			if (!(e.yaw == l.yaw &&
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
		move_tick = null;
		menu.update_btns();
		last_record = null;
		sendMessage(Text.translatable("message.repeating-mod.record_stop"));
	}


	public void startReplay() {
		is_recording = false;
		is_replaying = true;
		menu.update_btns();
		replay = new Thread(() -> {
			while (true) {
				for (RecordEvent e : record) {
					if (is_replaying) {
						e.callback();
					}
				}
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
		replay = null;
		menu.update_btns();
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

	public static class RecordDelayEvent extends RecordEvent {
		public long delay;

		public static RecordDelayEvent fromArgs(String[] a) {
			return new RecordDelayEvent(Long.parseLong(a[0]));
		}

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

		public static RecordMoveEvent fromArgs(String[] a) {
			return new RecordMoveEvent(new Vec3d(
					Double.parseDouble(a[0]),
					Double.parseDouble(a[1]),
					Double.parseDouble(a[2])),
					Float.parseFloat(a[3]),
					Float.parseFloat(a[4]));
		}

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

	public static class RecordInputEvent extends RecordEvent {
		public Boolean sneaking;
		public Boolean jumping;
		public Boolean pressingForward;
		public Boolean pressingBack;
		public Boolean pressingLeft;
		public Boolean pressingRight;
		public Boolean sprinting;

		public Float movementSideways;
		public Float movementForward;

		public float yaw;
		public float head_yaw;
		public float body_yaw;
		public float pitch;
		public float speed;

		public static RecordInputEvent fromArgs(String[] a) {
			return new RecordInputEvent(
					(a[0].equals("n")?null:a[0].equals("1")),
					(a[1].equals("n")?null:a[1].equals("1")),
					(a[2].equals("n")?null:Float.parseFloat(a[2])),
					(a[3].equals("n")?null:Float.parseFloat(a[3])),
					(a[4].equals("n")?null:a[4].equals("1")),
					(a[5].equals("n")?null:a[5].equals("1")),
					(a[6].equals("n")?null:a[6].equals("1")),
					(a[7].equals("n")?null:a[7].equals("1")),
					Float.parseFloat(a[8]),Float.parseFloat(a[9]),
					Float.parseFloat(a[10]),
					(a[11].equals("n")?null:a[11].equals("1")),
					Float.parseFloat(a[12]),
					Float.parseFloat(a[13]));
		}

		public RecordInputEvent(Boolean sneaking,
								Boolean jumping,
								Float movementSideways,
								Float movementForward,
								Boolean pressingForward,
								Boolean pressingBack,
								Boolean pressingLeft,
								Boolean pressingRight,
								float head_yaw,
								float body_yaw,
								float head_pitch,
								Boolean sprinting,
								float yaw,
								float speed) {
			this.sneaking = sneaking;
			this.jumping = jumping;
			this.movementSideways = movementSideways;
			this.movementForward = movementForward;
			this.pressingForward = pressingForward;
			this.pressingBack = pressingBack;
			this.pressingLeft = pressingLeft;
			this.pressingRight = pressingRight;
			this.head_yaw = head_yaw;
			this.body_yaw = body_yaw;
			this.pitch = head_pitch;
			this.sprinting = sprinting;
			this.yaw = yaw;
			this.speed = speed;
		}

		public void fillEmpty(RecordInputEvent e) {
			if (sneaking == null) sneaking = e.sneaking;
			if (jumping == null) jumping = e.jumping;
			if (movementSideways == null) movementSideways = e.movementSideways;
			if (movementForward == null) movementForward = e.movementForward;
			if (pressingForward == null) pressingForward = e.pressingForward;
			if (pressingBack == null) pressingBack = e.pressingBack;
			if (pressingLeft == null) pressingLeft = e.pressingLeft;
			if (pressingRight == null) pressingRight = e.pressingRight;
			if (sprinting == null) sprinting = e.sprinting;
		}

		public boolean isEmpty() {
			return sneaking == null &&
				jumping == null &&
				movementSideways == null &&
				movementForward == null &&
				pressingForward == null &&
				pressingBack == null &&
				pressingLeft == null &&
				pressingRight == null &&
				sprinting == null;
		}

		public void callback() {
			input_replay = this;
		}

		public void inputCallback() {
			if (sprinting != null && client.player.isSprinting() != sprinting)
				client.player.setSprinting(sprinting);
			if (client.player.getYaw() != yaw)
				client.player.setYaw(yaw);
			if (client.player.getHeadYaw() != head_yaw)
				client.player.setHeadYaw(head_yaw);
			if (client.player.getBodyYaw() != body_yaw)
				client.player.setBodyYaw(body_yaw);
			if (client.player.getPitch() != pitch)
				client.player.setPitch(pitch);
			if (client.player.getMovementSpeed() != speed)
				client.player.setMovementSpeed(speed);
			if (sneaking != null && client.player.input.sneaking != sneaking)
				client.player.input.sneaking = sneaking;
			if (jumping != null && client.player.input.jumping != jumping)
				client.player.input.jumping = jumping;
			if (movementSideways != null && client.player.input.movementSideways != movementSideways)
				client.player.input.movementSideways = movementSideways;
			if (movementForward != null && client.player.input.movementForward != movementForward)
				client.player.input.movementForward = movementForward;
			if (pressingForward != null && client.player.input.pressingForward != pressingForward)
				client.player.input.pressingForward = pressingForward;
			if (pressingBack != null && client.player.input.pressingBack != pressingBack)
				client.player.input.pressingBack = pressingBack;
			if (pressingLeft != null && client.player.input.pressingLeft != pressingLeft)
				client.player.input.pressingLeft = pressingLeft;
			if (pressingRight != null && client.player.input.pressingRight != pressingRight)
				client.player.input.pressingRight = pressingRight;
		}

		public String toText() {
			return "p="+
				((sneaking==null)?"n":(sneaking?"1":"0"))+"&"+
				((jumping==null)?"n":(jumping?"1":"0"))+"&"+
				((movementSideways==null)?"n":movementSideways)+"&"+
				((movementForward==null)?"n":movementForward)+"&"+
				((pressingForward==null)?"n":(pressingForward?"1":"0"))+"&"+
				((pressingBack==null)?"n":(pressingBack?"1":"0"))+"&"+
				((pressingLeft==null)?"n":(pressingLeft?"1":"0"))+"&"+
				((pressingRight==null)?"n":(pressingRight?"1":"0"))+"&"+
				head_yaw+"&"+body_yaw+"&"+ pitch +"&"+
				((sprinting==null)?"n":(sprinting?"1":"0")+
				"&"+yaw+"&"+speed);
		}

		public String getType() {
			return "input";
		}
	}

	public static class RecordBlockBreakEvent extends RecordEvent {
		public BlockPos pos;

		public static RecordBlockBreakEvent fromArgs(String[] a) {
			return new RecordBlockBreakEvent(new BlockPos(
					Integer.parseInt(a[0]),
					Integer.parseInt(a[1]),
					Integer.parseInt(a[2])));
		}

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

		public static RecordBlockInteractEvent fromArgs(String[] a) {
			return new RecordBlockInteractEvent(
					Hand.valueOf(a[5]),
					new BlockHitResult(new Vec3d(
							Double.parseDouble(a[0]),
							Double.parseDouble(a[1]),
							Double.parseDouble(a[2])),
							Direction.byId(Integer.parseInt(a[4])),
							new BlockPos(
									Integer.parseInt(a[0]),
									Integer.parseInt(a[1]),
									Integer.parseInt(a[2])),
							a[3].equals("1")));
		}

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
