package org.powerbot.game.api.util.node;

import org.powerbot.game.api.util.internal.Multipliers;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.Node;

/**
 * @author Timer
 */
public class Nodes {
	/**
	 * @param nc The node cache to check
	 * @param id The id of the node
	 * @return A <tt>Node</tt> object corresponding to the ID in the nodecache.
	 */
	public static Node lookup(final org.powerbot.game.client.HashTable nc, final long id) {
		try {
			if (nc == null || nc.getBuckets() == null || id < 0) {
				return null;
			}

			final Multipliers multipliers = Context.multipliers();
			final long multiplier = (((long) multipliers.NODE_ID) << 32) + ((multipliers.NODE_ID_p2 & 0xFFFFFFFFL));

			final Node n = ((Node[]) nc.getBuckets())[(int) (id & ((Node[]) nc.getBuckets()).length - 1)];
			for (Node node = n.getPrevious(); node != n; node = node.getPrevious()) {
				if (node.getID() * multiplier == id) {
					return node;
				}
			}
		} catch (final Exception ignored) {
		}
		return null;
	}
}