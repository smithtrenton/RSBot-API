package org.powerbot.game.api.wrappers.node;

import org.powerbot.game.api.wrappers.Identifiable;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.RSObjectDef;

/**
 * @author Timer
 */
public class SceneObjectDefinition implements Identifiable {
	private final RSObjectDef def;
	private final int id_multiplier;

	public SceneObjectDefinition(final RSObjectDef def) {
		this.def = def;
		this.id_multiplier = Context.multipliers().OBJECTDEF_ID;
	}

	public String getName() {
		return (String) def.getName();
	}

	public String[] getActions() {
		return (String[]) def.getActions();
	}

	public int getId() {
		return def.getID() * id_multiplier;
	}
}
