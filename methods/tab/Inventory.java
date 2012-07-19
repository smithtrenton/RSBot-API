package org.powerbot.game.api.methods.tab;

import java.util.LinkedList;
import java.util.List;

import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

public class Inventory {
	public static final int WIDGET = 679;
	public static final int WIDGET_BANK = 763;
	public static final int WIDGET_PRICE_CHECK = 204;
	public static final int WIDGET_EQUIPMENT_BONUSES = 670;
	public static final int WIDGET_EXCHANGE = 644;
	public static final int WIDGET_SHOP = 621;
	public static final int WIDGET_DUNGEONEERING_SHOP = 957;
	public static final int WIDGET_BEAST_OF_BURDEN_STORAGE = 665;
	public static final int WIDGET_STORE = 1266;

	public static final int[] ALT_WIDGETS = {
			WIDGET_BANK,
			WIDGET_PRICE_CHECK, WIDGET_EQUIPMENT_BONUSES,
			WIDGET_EXCHANGE, WIDGET_SHOP, WIDGET_DUNGEONEERING_SHOP,
			WIDGET_BEAST_OF_BURDEN_STORAGE, WIDGET_STORE
	};

	public static final Filter<Item> ALL_FILTER = new Filter<Item>() {
		public boolean accept(final Item item) {
			return true;
		}
	};

	public static Item getItem(final int... ids) {
		final Item[] items = getItems(false);
		for (final Item item : items) {
			final int item_id = item.getId();
			for (final int id : ids) {
				if (item_id == id) {
					return item;
				}
			}
		}
		return null;
	}

	public static Item[] getItems() {
		return getItems(false);
	}

	public static Item[] getItems(final boolean cached) {
		return getItems(cached, ALL_FILTER);
	}

	public static Item[] getItems(final Filter<Item> itemFilter) {
		return getItems(false, itemFilter);
	}

	/**
	 * Returns the items matching a set filter.
	 *
	 * @param cached     If true opens the inventory tab, if false it uses the last seen representation of the items
	 * @param itemFilter The filter to compare against
	 * @return The items matching the filter
	 */
	public static Item[] getItems(final boolean cached, final Filter<Item> itemFilter) {
		final WidgetChild inventoryWidget = getWidget(cached);
		if (inventoryWidget != null) {
			final WidgetChild[] inventoryChildren = inventoryWidget.getChildren();
			if (inventoryChildren.length > 27) {
				final List<Item> items = new LinkedList<Item>();
				for (int i = 0; i < 28; ++i) {
					if (inventoryChildren[i].getChildId() != -1) {
						final Item inventoryItem = new Item(inventoryChildren[i]);
						if (itemFilter.accept(inventoryItem)) {
							items.add(new Item(inventoryChildren[i]));
						}
					}
				}
				return items.toArray(new Item[items.size()]);
			}
		}
		return new Item[0];
	}

	/**
	 * Returns an array representing the state of the inventory.<br>
	 * This function will always return an array of length 28.
	 *
	 * @param cached if true opens the inventory tab, if false it uses the last seen representation of the inventory.
	 * @return an array representing the inventory.
	 */
	public static Item[] getAllItems(final boolean cached) {
		final Item[] items = new Item[28];
		final WidgetChild inventoryWidget = getWidget(cached);
		if (inventoryWidget != null) {
			final WidgetChild[] inventoryChildren = inventoryWidget.getChildren();
			if (inventoryChildren.length >= items.length) {
				for (int i = 0; i < items.length; i++) {
					final WidgetChild wc = inventoryChildren[i];
					items[i] = (wc == null || wc.getChildId() == -1) ? null : new Item(wc);
				}
			}
		}
		return items;
	}

	public static int getCount() {
		return getItems().length;
	}

	public static int getCount(final boolean countStack) {
		return getCount(countStack, ALL_FILTER);
	}

	public static int getCount(final Filter<Item> itemFilter) {
		return getCount(false, itemFilter);
	}

	public static int getCount(final int... ids) {
		return getCount(false, ids);
	}

	public static int getCount(final boolean countStack, final int id) {
		return getCount(countStack, new Filter<Item>() {
			public boolean accept(final Item item) {
				return item.getId() == id;
			}
		});
	}

	public static int getCount(final boolean countStacks, final int... ids) {
		return getCount(countStacks, new Filter<Item>() {
			public boolean accept(final Item item) {
				for (final int ID : ids) {
					if (item.getId() == ID) {
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * Gets the count of a set of items
	 *
	 * @param countStack Should the method count item stacks?
	 * @param itemFilter The filter to compare against
	 * @return The amount of items matching the filter
	 */
	public static int getCount(final boolean countStack, final Filter<Item> itemFilter) {
		final Item[] items = getItems();
		int count = 0;
		for (final Item item : items) {
			if (item != null && itemFilter.accept(item)) {
				count += countStack ? item.getStackSize() : 1;
			}
		}
		return count;
	}

	public static boolean isFull() {
		return getCount() == 28;
	}

	public static boolean selectItem(final int itemId) {
		final Item item = getItem(itemId);
		return item != null && selectItem(item);
	}

	/**
	 * Selects the specified item in the inventory
	 *
	 * @param item The item to select.
	 * @return <tt>true</tt> if the item was selected; otherwise <tt>false</tt>.
	 */
	public static boolean selectItem(final Item item) {//TODO fix index 0
		final int itemID = item.getId();
		Item selItem = getSelectedItem();
		if (selItem != null && selItem.getId() == itemID) {
			return true;
		}
		if (selItem != null) {
			selItem.getWidgetChild().interact("Use");
			Time.sleep(Random.nextInt(500, 700));
		}
		if (!item.getWidgetChild().interact("Use")) {
			return false;
		}
		for (int c = 0; c < 5 && (selItem = getSelectedItem()) == null; c++) {
			Time.sleep(Random.nextInt(500, 700));
		}
		return selItem != null && selItem.getId() == itemID;
	}

	public static Item getItemAt(final int index) {
		final WidgetChild child = getWidget(false).getChild(index);
		return index >= 0 && index < 28 && child != null ? new Item(child) : null;
	}


	public static Item getSelectedItem() {
		final int index = getSelectedItemIndex();
		return index == -1 ? null : getItemAt(index);
	}

	/**
	 * Gets the selected item index.
	 *
	 * @return The index of current selected item, or -1 if none is selected.
	 */
	public static int getSelectedItemIndex() {
		final WidgetChild[] children = getWidget(false).getChildren();
		for (int i = 0; i < Math.min(28, children.length); i++) {
			if (children[i].getBorderThickness() == 2) {
				return i;
			}
		}
		return -1;
	}

	public static WidgetChild getWidget(final boolean cached) {
		for (final int widget : ALT_WIDGETS) {
			WidgetChild inventory = Widgets.get(widget, 0);
			if (inventory != null && inventory.getAbsoluteX() > 50) {
				return inventory;
			}
		}
		if (!cached) {
			Tabs.INVENTORY.open(false);
		}
		return Widgets.get(WIDGET, 0);
	}
}
