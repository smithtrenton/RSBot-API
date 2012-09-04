package org.powerbot.game.api.wrappers.interactive;

import java.lang.ref.SoftReference;

import org.powerbot.game.api.util.internal.Multipliers;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.RSNPC;
import org.powerbot.game.client.RSNPCDef;

/**
 * @author Timer
 */
public class NPC extends Character {
	private final SoftReference<RSNPC> n;
	private final Multipliers multipliers;

	public NPC(final RSNPC n) {
		this.n = new SoftReference<RSNPC>(n);
		this.multipliers = Context.multipliers();
	}

	public int getLevel() {
		return get().getLevel() * multipliers.NPC_LEVEL;
	}

	public String getName() {
		return (String) ((RSNPCDef) get().getRSNPCDef()).getName();
	}

	public int getId() {
		return ((RSNPCDef) get().getRSNPCDef()).getID() * multipliers.NPCDEF_ID;
	}

	public String[] getActions() {
		return (String[]) ((RSNPCDef) get().getRSNPCDef()).getActions();
	}

	public RSNPC get() {
		return n.get();
	}
}
