package org.powerbot.game.api.wrappers.interactive;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.internal.Multipliers;
import org.powerbot.game.api.util.node.LinkedList;
import org.powerbot.game.api.util.node.Nodes;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Identifiable;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.RegionOffset;
import org.powerbot.game.api.wrappers.Rotatable;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.api.wrappers.graphics.model.CharacterModel;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.CombatStatus;
import org.powerbot.game.client.CombatStatusData;
import org.powerbot.game.client.HashTable;
import org.powerbot.game.client.Model;
import org.powerbot.game.client.RSAnimator;
import org.powerbot.game.client.RSCharacter;
import org.powerbot.game.client.RSInteractable;
import org.powerbot.game.client.RSInteractableData;
import org.powerbot.game.client.RSMessageData;
import org.powerbot.game.client.RSNPC;
import org.powerbot.game.client.RSNPCNode;
import org.powerbot.game.client.RSPlayer;
import org.powerbot.game.client.Sequence;

/**
 * @author Timer
 */
public abstract class Character implements Entity, Locatable, Rotatable, Identifiable {
	private final Client client;
	private final Multipliers multipliers;

	public Character() {
		this.client = Context.client();
		this.multipliers = Context.multipliers();
	}

	public abstract int getLevel();

	public abstract String getName();

	public RegionOffset getRegionOffset() {
		final RSInteractable location = get();
		final RSInteractableData data = (RSInteractableData) location.getData();
		return new RegionOffset((int) data.getLocation().getX() >> 9, (int) data.getLocation().getY() >> 9, getPlane());
	}

	public Tile getLocation() {
		final RegionOffset regionTile = getRegionOffset();
		return new Tile(Game.getBaseX() + regionTile.getX(), Game.getBaseY() + regionTile.getY(), regionTile.getPlane());
	}

	public int getPlane() {
		return get().getPlane();
	}

	public Character getInteracting() {
		final int index = get().getInteracting() * multipliers.CHARACTER_INTERACTING;
		if (index == -1) {
			return null;
		}
		if (index < 0x8000) {
			return new NPC((RSNPC) ((RSNPCNode) Nodes.lookup((HashTable) client.getRSNPCNC(), index)).getRSNPC());
		} else {
			return new Player((RSPlayer) client.getRSPlayerArray()[index - 0x8000]);
		}
	}

	public int getAnimation() {
		final RSAnimator animation = (RSAnimator) get().getAnimation();
		if (animation != null) {
			final Sequence sequence = (Sequence) animation.getSequence();
			if (sequence != null) {
				return sequence.getID() * multipliers.SEQUENCE_ID;
			}
		}
		return -1;
	}

	public int getPassiveAnimation() {
		try {
			final RSAnimator animation = (RSAnimator) get().getPassiveAnimation();
			if (animation != null) {
				final Sequence sequence = (Sequence) animation.getSequence();
				if (sequence != null) {
					return sequence.getID() * multipliers.SEQUENCE_ID;
				}
			}
		} catch (final AbstractMethodError ignored) {
		} catch (final ClassCastException ignored) {
		}
		return -1;
	}

	public int getHeight() {
		return get().getHeight() * multipliers.CHARACTER_HEIGHT;
	}

	public int getRotation() {
		return get().getOrientation() * multipliers.CHARACTER_ORIENTATION;
	}

	public int getOrientation() {
		return (630 - getRotation() * 45 / 0x800) % 360;
	}

	private CombatStatusData getCombatInfoData() {
		final RSCharacter accessor = get();
		if (accessor == null) {
			return null;
		}

		final int global_loopCycle = client.getLoopCycle() * multipliers.GLOBAL_LOOPCYCLE;

		final Object combatStatusList = accessor.getCombatStatusList();
		if (combatStatusList == null) {
			return null;
		}

		final LinkedList<Object> linkedCombatStatus = new LinkedList<Object>((org.powerbot.game.client.LinkedList) combatStatusList);
		for (Object combatStatus = linkedCombatStatus.getHead(); combatStatus != null; combatStatus = linkedCombatStatus.getNext()) {
			final Object dataList = ((CombatStatus) combatStatus).getData();
			if (dataList == null) {
				continue;
			}

			final LinkedList<Object> linkedDataList = new LinkedList<Object>((org.powerbot.game.client.LinkedList) dataList);
			final Object headData = linkedDataList.getHead();
			if (headData == null || ((CombatStatusData) headData).getLoopCycleStatus() * multipliers.CHARACTER_LOOPCYCLESTATUS > global_loopCycle) {
				continue;
			}

			return (CombatStatusData) headData;
		}

		return null;
	}

	public int getHpPercent() {
		final RSCharacter c = get();
		if (c != null) {
			final CombatStatusData combatInfoData = getCombatInfoData();
			if (combatInfoData == null) {
				return 100;
			}

			return (int) Math.ceil(combatInfoData.getHPRatio() * multipliers.CHARACTER_HPRATIO * 100 / 255);
		}

		return -1;
	}
	
	public int getHpRatio() {
		final RSCharacter c = get();
		if (c != null) {
			final CombatStatusData combatInfoData = getCombatInfoData();
			if (combatInfoData == null) {
				return 255;
			}

			return combatInfoData.getHPRatio() * multipliers.CHARACTER_HPRATIO;
		}

		return -1;
	}

	public boolean isInCombat() {
		final RSCharacter c = get();
		return c != null && getCombatInfoData() != null;
	}

	public String getMessage() {
		try {
			final RSMessageData message_data = (RSMessageData) get().getMessageData();
			if (message_data != null) {
				return (String) message_data.getMessage();
			}
		} catch (final AbstractMethodError ignored) {
		} catch (final ClassCastException ignored) {
		}
		return null;
	}

	public int getSpeed() {
		return get().isMoving() * multipliers.CHARACTER_ISMOVING;
	}

	public boolean isMoving() {
		return getSpeed() != 0;
	}

	public CapturedModel getModel() {
		final RSCharacter ref = get();
		if (ref != null) {
			final Model model = ref.getModel();
			if (model != null) {
				return new CharacterModel(model, this);
			}
		}
		return null;
	}

	public abstract RSCharacter get();

	public boolean validate() {
		final RSCharacter this_ref;
		if ((this_ref = get()) != null) {
			if (this instanceof Player) {
				return Players.getNearest(new Filter<Player>() {
					@Override
					public boolean accept(final Player character) {
						return character.get() == this_ref;
					}
				}) != null;
			} else if (this instanceof NPC) {
				return NPCs.getNearest(new Filter<NPC>() {
					@Override
					public boolean accept(final NPC character) {
						return character.get() == this_ref;
					}
				}) != null;
			}
			return true;
		}
		return false;
	}

	public Point getCentralPoint() {
		final CapturedModel model = getModel();
		if (model != null) {
			return model.getCentralPoint();
		}
		final RSCharacter character = get();
		final RSInteractableData data = (RSInteractableData) character.getData();
		return Calculations.groundToScreen((int) data.getLocation().getX(), (int) data.getLocation().getY(), character.getPlane(), -getHeight() / 2);
	}

	public Point getNextViewportPoint() {
		final CapturedModel model = getModel();
		if (model != null) {
			return model.getNextViewportPoint();
		}
		return getCentralPoint();
	}

	public boolean contains(final Point point) {
		final CapturedModel model = getModel();
		if (model != null) {
			return model.contains(point);
		}
		return getLocation().contains(point);
	}

	public boolean isOnScreen() {
		final CapturedModel model = getModel();
		return model != null ? model.isOnScreen() : Calculations.isOnScreen(getCentralPoint());
	}

	public Polygon[] getBounds() {
		final CapturedModel model = getModel();
		if (model != null) {
			return model.getBounds();
		}
		return getLocation().getBounds();
	}

	public boolean hover() {
		final CapturedModel model = getModel();
		if (model != null) {
			return model.hover();
		}
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return true;
			}
		});
	}

	public boolean click(final boolean left) {
		final CapturedModel model = getModel();
		if (model != null) {
			return model.click(left);
		}
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				Mouse.click(true);
				return true;
			}
		});
	}

	public boolean interact(final String action) {
		final CapturedModel model = getModel();
		if (model != null) {
			return model.interact(action);
		}
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return Menu.select(action);
			}
		});
	}

	public boolean interact(final String action, final String option) {
		final CapturedModel model = getModel();
		if (model != null) {
			return model.interact(action, option);
		}
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return Menu.select(action, option);
			}
		});
	}

	public void draw(final Graphics render) {
		//TODO
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Character) {
			final Character cha = (Character) obj;
			return cha.get() == get();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(get());
	}
}
