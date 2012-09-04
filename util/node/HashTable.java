package org.powerbot.game.api.util.node;

import org.powerbot.game.client.Node;

/**
 * @author Timer
 */
public class HashTable {
	private org.powerbot.game.client.HashTable nc;
	private org.powerbot.game.client.Node current;
	private int c_index = 0;

	public HashTable(org.powerbot.game.client.HashTable hashTable) {
		nc = hashTable;
	}

	public org.powerbot.game.client.Node getFirst() {
		c_index = 0;
		return getNext();
	}

	public org.powerbot.game.client.Node getNext() {
		if (c_index > 0 && ((Node[]) nc.getBuckets())[c_index - 1] != current) {
			org.powerbot.game.client.Node node = current;
			current = node.getPrevious();
			return node;
		}
		while (c_index < ((Node[]) nc.getBuckets()).length) {
			org.powerbot.game.client.Node node = ((Node[]) nc.getBuckets())[c_index++].getPrevious();
			if (((Node[]) nc.getBuckets())[c_index - 1] != node) {
				current = node.getPrevious();
				return node;
			}
		}
		return null;
	}
}
