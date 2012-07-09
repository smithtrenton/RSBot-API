package org.powerbot.game.api.methods.tab;

import java.util.LinkedHashSet;
import java.util.Set;

import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.util.Time;

/**
 * @author ArcaneSanity
 */
public class Prayer {

	public interface PrayerBook {

		public int getId();

		public int getBook();

		public int getRequiredLevel();

		public boolean isActive();

		public boolean isSetQuick();

	}

	public enum CURSES implements PrayerBook {
		PROTECT_ITEM_CURSE(0, 0, 50),
		SAP_WARRIOR(1, 1, 50),
		SAP_RANGER(2, 2, 52),
		SAP_MAGE(3, 3, 54),
		SAP_SPIRIT(4, 4, 56),
		BERSERKER(5, 5, 59),
		DEFLECT_SUMMONING(6, 6, 62),
		DEFLECT_MAGIC(7, 7, 65),
		DEFLECT_MISSILE(8, 8, 68),
		DEFLECT_MELEE(9, 9, 71),
		LEECH_ATTACK(10, 10, 74),
		LEECH_RANGE(11, 11, 76),
		LEECH_MAGIC(12, 12, 78),
		LEECH_DEFENCE(13, 13, 80),
		LEECH_STRENGTH(14, 14, 82),
		LEECH_ENERGY(15, 15, 84),
		LEECH_SPECIAL_ATTACK(16, 16, 86),
		WRATH(17, 17, 89),
		SOUL_SPLIT(18, 18, 92),
		TURMOIL(19, 19, 95);

		private final int id, shift, level;

		CURSES(final int id, final int shift, final int level) {
			this.id = id;
			this.shift = shift;
			this.level = level;
		}

		public int getId() {
			return this.id;
		}

		public int getBook() {
			return PRAYER_BOOK_CURSES;
		}

		public int getRequiredLevel() {
			return this.level;
		}

		public boolean isActive() {
			return Settings.get(1582, this.shift, 0x1) == 1;
		}

		public boolean isSetQuick() {
			return Settings.get(1587, this.shift, 0x1) == 1;
		}

	}

	public enum NORMAL implements PrayerBook {
		THICK_SKIN(0, 0, 1),
		BURST_OF_STRENGTH(1, 1, 4),
		CLARITY_OF_THOUGHT(2, 2, 7),
		SHARP_EYE(3, 18, 8),
		MYSTIC_WILL(4, 19, 9),
		ROCK_SKIN(5, 3, 10),
		SUPERHUMAN_STRENGTH(6, 4, 13),
		IMPROVED_REFLEXES(7, 5, 16),
		RAPID_RESTORE(8, 6, 19),
		RAPID_HEAL(9, 7, 22),
		PROTECT_ITEM_REGULAR(10, 8, 25),
		HAWK_EYE(11, 20, 26),
		MYSTIC_LORE(12, 21, 27),
		STEEL_SKIN(13, 9, 28),
		ULTIMATE_STRENGTH(14, 10, 31),
		INCREDIBLE_REFLEXES(15, 11, 34),
		PROTECT_FROM_SUMMONING(16, 24, 35),
		PROTECT_FROM_MAGIC(17, 12, 37),
		PROTECT_FROM_MISSILES(18, 13, 40),
		PROTECT_FROM_MELEE(19, 14, 43),
		EAGLE_EYE(20, 22, 44),
		MYSTIC_MIGHT(21, 23, 45),
		RETRIBUTION(22, 15, 46),
		REDEMPTION(23, 16, 49),
		SMITE(24, 17, 52),
		CHIVALRY(25, 25, 60),
		RAPID_RENEWAL(26, 27, 65),
		PIETY(27, 26, 70),
		RIGOUR(28, 29, 74),
		AUGURY(29, 28, 77);

		private final int id, shift, level;

		private NORMAL(final int id, final int shift, final int level) {
			this.id = id;
			this.shift = shift;
			this.level = level;
		}

		public int getId() {
			return this.id;
		}

		public int getBook() {
			return PRAYER_BOOK_NORMAL;
		}

		public int getRequiredLevel() {
			return this.level;
		}

		public boolean isActive() {
			return Settings.get(1395, this.shift, 0x1) == 1;
		}

		public boolean isSetQuick() {
			return Settings.get(1397, this.shift, 0x1) == 1;
		}

	}

	public static final int WIDGET_PRAYER = 271;
	public static final int WIDGET_PRAYER_ORB = 749;
	public static final int PRAYER_BOOK_CURSES = 0x17;
	public static final int PRAYER_BOOK_NORMAL = 0x16;

	public static int getPoints() {
		return Settings.get(2382, 0x3ff);
	}

	public static boolean isCursesOn() {
		return Settings.get(1584) % 2 != 0;
	}

	public static boolean isQuickOn() {
		return Settings.get(1396) == 0x2;
	}

	/**
	 * @return An array of currently active prayers/curses.
	 */
	public static PrayerBook[] getActive() {
		Set<PrayerBook> active = new LinkedHashSet<PrayerBook>();
		for (PrayerBook p : isCursesOn() ? CURSES.values() : NORMAL.values()) {
			if (p.isActive()) {
				active.add(p);
			}
		}
		return active.toArray(new PrayerBook[active.size()]);
	}

	/**
	 * @return An array of currently set prayers/curses to quick use.
	 */
	public static PrayerBook[] getQuick() {
		Set<PrayerBook> setquick = new LinkedHashSet<PrayerBook>();
		for (PrayerBook p : isCursesOn() ? CURSES.values() : NORMAL.values()) {
			if (p.isSetQuick()) {
				setquick.add(p);
			}
		}
		return setquick.toArray(new PrayerBook[setquick.size()]);
	}

	/**
	 * @param active <tt>true</tt> to turn quick prayers/curses on, <tt>false</tt> to turn quick prayers/curses off.
	 */
	public static boolean toggleQuick(final boolean activate) {
		if (isQuickOn() == activate) {
			return true;
		}
		return Widgets.get(WIDGET_PRAYER_ORB, 2).interact("Turn");
	}

	/**
	 * @param prayers Prayers/Curses to set to quick use
	 */
	public static boolean setQuick(final PrayerBook... prayers) {
		for (PrayerBook p : prayers) {
			if (p.getBook() != (isCursesOn() ? PRAYER_BOOK_CURSES : PRAYER_BOOK_NORMAL)
					|| p.getRequiredLevel() > Skills.getRealLevel(Skills.PRAYER)) {
				return false;
			}
		}
		if (Widgets.get(WIDGET_PRAYER_ORB, 2).interact("Select quick")) {
			for (int i = 0; i < 20 && Settings.get(1396) != 0x1; i++) {
				Time.sleep(50);
			}
			Time.sleep(200);
			for (PrayerBook p : prayers) {
				if (p.isSetQuick()) {
					continue;
				}
				if (Widgets.get(WIDGET_PRAYER, 42).getChild(p.getId()).interact("Select")) {
					for (int i = 0; i < 10 && !p.isSetQuick(); i++) {
						Time.sleep(50);
					}
				} else {
					Widgets.get(WIDGET_PRAYER, 43).interact("Confirm");
					return false;
				}
			}
			return Widgets.get(WIDGET_PRAYER, 43).interact("Confirm");
		}
		return false;
	}

	/**
	 * @param prayer Desired prayer/curse
	 * @param active <tt>true</tt> to activate, <tt>false</tt> to deactivate
	 */
	public static boolean togglePrayer(final PrayerBook prayer, final boolean active) {
		if (prayer.getBook() != (isCursesOn() ? PRAYER_BOOK_CURSES : PRAYER_BOOK_NORMAL)
				|| prayer.getRequiredLevel() > Skills.getRealLevel(Skills.PRAYER)) {
			return false;
		}
		if (prayer.isActive() == active) {
			return true;
		}
		if (!Tabs.getCurrent().equals(Tabs.PRAYER)) {
			Tabs.PRAYER.open(false);
			for (int i = 0; i < 20 && !Tabs.getCurrent().equals(Tabs.PRAYER); i++) {
				Time.sleep(50);
			}
		}
		return Widgets.get(WIDGET_PRAYER, 8).getChild(prayer.getId()).interact(active ? "Activate" : "Deactivate");
	}

	public static boolean deactivateAll() {
		if (getActive().length == 0) {
			return true;
		}
		for (PrayerBook p : getActive()) {
			if (togglePrayer(p, false)) {
				for (int i = 0; i < 10 && p.isActive(); i++) {
					Time.sleep(50);
				}
			}
		}
		return getActive().length == 0;
	}
}
