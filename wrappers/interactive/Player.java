package org.powerbot.game.api.wrappers.interactive;

import java.lang.ref.SoftReference;

import org.powerbot.game.api.util.internal.Multipliers;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.RSPlayer;
import org.powerbot.game.client.RSPlayerComposite;

/**
 * @author Timer
 */
public class Player extends Character {
	private final SoftReference<RSPlayer> p;
	private final Multipliers multipliers;

	public Player(final RSPlayer p) {
		this.p = new SoftReference<RSPlayer>(p);
		this.multipliers = Context.multipliers();
	}

	public int getLevel() {
		return get().getLevel() * multipliers.PLAYER_LEVEL;
	}

	public String getName() {
		return (String) get().getName();
	}

	public int getTeam() {
		return get().getTeam() * multipliers.PLAYER_TEAM;
	}

	public int getPrayerIcon() {
		return get().getPrayerIcon() * multipliers.PLAYER_PRAYERICON;
	}

	public int getSkullIcon() {
		return get().getSkullIcon() * multipliers.PLAYER_SKULLICON;
	}

	public int getNpcId() {
		final RSPlayerComposite composite = (RSPlayerComposite) get().getComposite();
		return composite == null ? -1 : composite.getNPCID() * multipliers.PLAYERCOMPOSITE_NPCID;
	}

	public int getId() {
		return getName().hashCode();
	}

	public int[] getAppearance() {
		final RSPlayerComposite composite = (RSPlayerComposite) get().getComposite();
		if (composite != null) {
			final int[] appearance = ((int[]) composite.getEquipment()).clone();
			for (int i = 0; i < appearance.length; i++) {
				if ((appearance[i] & 0x40000000) > 0) {
					appearance[i] &= 0x3fffffff;
				} else {
					appearance[i] = -1;
				}
			}
			return appearance;
		}
		return null;
	}

	public RSPlayer get() {
		return p.get();
	}
}
