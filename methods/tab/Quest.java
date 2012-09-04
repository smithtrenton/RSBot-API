package org.powerbot.game.api.methods.tab;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.interactive.Player;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

public class Quest {
	private static final int WIDGET_TAB_CONTAINER = 190;
	private static final int WIDGET_SCROLL_CONTAINER = 15;

	public static enum STATUS {
		DONE(65280), IN_PROGRESS(16776960), NOT_STARTED(16711680);
		private int color;

		STATUS(int color) {
			this.color = color;
		}

		static STATUS fromInt(int i) {
			for (STATUS t : values()) {
				if (t.color == i) {
					return t;
				}
			}
			return null;
		}
	}

	public static enum QUEST {
		COOKS_ASSISTANT("Cook's Assistant"), DEMON_SLAYER("Demon Slayer"), DORICS_QUEST("Doric's Quest"),
		DRAGON_SLAYER("Dragon Slayer"), ERNEST_THE_CHICKEN("Ernest the Chicken"), GOBLIN_DIPLOMACY("Goblin Diplomacy"),
		IMP_CATCHER("Imp Catcher"), THE_KNIGHTS_SWORD("The Knight's Sword"), PIRATES_TREASURE("Pirate's Treasure"),
		PRINCE_ALI_RESCUE("Prince Ali Rescue"), THE_RESTLESS_GHOST("The Restless Ghost"),
		THE_PRISONER_OF_GLOUPHRIE("The Prisoner of Glouphrie"), RUNE_MYSTERIES("Rune Mysteries"),
		KING_OF_THE_DWARVES("King of the Dwarves"), SHIELD_OF_ARRAV("Shield of Arrav"),
		VAMPYRE_SLAYER("Vampyre Slayer"), GUNNARS_GROUND("Gunnar's Ground"), ANIMAL_MAGNETISM("Animal Magnetism"),
		BETWEEN_A_ROCK("Between a Rock..."), BIG_CHOMPY_BIRD_HUNTING("Big Chompy Bird Hunting"), BIOHAZARD("Biohazard"),
		CABIN_FEVER("Cabin Fever"), CLOCK_TOWER("Clock Tower"), CONTACT("Contact!"),
		ZOGRE_FLESH_EATERS("Zogre Flesh Eaters"), CREATURE_OF_FENKENSTRAIN("Creature of Fenkenstrain"),
		DARKNESS_OF_HALLOWVALE("Darkness of Hallowvale"), DEATH_TO_THE_DORGESHUUN("Death to the Dorgeshuun"),
		DEATH_PLATEAU("Death Plateau"), DESERT_TREASURE("Desert Treasure"), DEVIOUS_MINDS("Devious Minds"),
		THE_DIG_SITE("The Dig Site"), DRUIDIC_RITUAL("Druidic Ritual"), DWARF_CANNON("Dwarf Cannon"),
		EADGARS_RUSE("Eadgar's Ruse"), EAGLES_PEAK("Eagles' Peak"), ELEMENTAL_WORKSHOP_I("Elemental Workshop I"),
		ELEMENTAL_WORKSHOP_II("Elemental Workshop II"), ENAKHRAS_LAMENT("Enakhra's Lament"),
		ENLIGHTENED_JOURNEY("Enlightened Journey"), THE_EYES_OF_GLOUPHRIE("The Eyes of Glouphrie"),
		FAIRY_TALE_I_GROWING_PAINS("Fairy Tale I - Growing Pains"),
		FAIRY_TALE_II_CURE_A_QUEEN("Fairy Tale II - Cure a Queen"), FAMILY_CREST("Family Crest"), THE_FEUD("The Feud"),
		FIGHT_ARENA("Fight Arena"), FISHING_CONTEST("Fishing Contest"), FORGETTABLE_TALE("Forgettable Tale..."),
		THE_FREMENNIK_TRIALS("The Fremennik Trials"), WATERFALL_QUEST("Waterfall Quest"),
		GARDEN_OF_TRANQUILLITY("Garden of Tranquillity"), GERTRUDES_CAT("Gertrude's Cat"), GHOSTS_AHOY("Ghosts Ahoy"),
		THE_GIANT_DWARF("The Giant Dwarf"), THE_GOLEM("The Golem"), THE_GRAND_TREE("The Grand Tree"),
		THE_HAND_IN_THE_SAND("The Hand in the Sand"), HAUNTED_MINE("Haunted Mine"), HAZEEL_CULT("Hazeel Cult"),
		HEROES_QUEST("Heroes' Quest"), HOLY_GRAIL("Holy Grail"), HORROR_FROM_THE_DEEP("Horror fromthe Deep"),
		ICTHLARINS_LITTLE_HELPER("Icthlarin's Little Helper"), IN_AID_OF_THE_MYREQUE("In Aid of the Myreque"),
		IN_SEARCH_OF_THE_MYREQUE("In Search of the Myreque"), JUNGLE_POTION("Jungle Potion"),
		LEGENDS_QUEST("Legends' Quest"), LOST_CITY("Lost City"), THE_LOST_TRIBE("The Lost Tribe"),
		LUNAR_DIPLOMACY("Lunar Diplomacy"), MAKING_HISTORY("Making History"), MERLINS_CRYSTAL("Merlin's Crystal"),
		MONKEY_MADNESS("Monkey Madness"), MONKS_FRIEND("Monk's Friend"), MOUNTAIN_DAUGHTER("Mountain Daughter"),
		MOURNINGS_ENDS_PART_I("Mourning's Ends Part I"), MOURNINGS_ENDS_PART_II("Mourning's Ends Part II"),
		MURDER_MYSTERY("Murder Mystery"), MY_ARMS_BIG_ADVENTURE("My Arm's Big Adventure"),
		NATURE_SPIRIT("Nature Spirit"), OBSERVATORY_QUEST("Observatory Quest"), ONE_SMALL_FAVOUR("One Small Favour"),
		PLAGUE_CITY("Plague City"), PRIEST_IN_PERIL("Priest in Peril"), RAG_AND_BONE_MAN("Rag and Bone Man"),
		RATCATCHERS("Ratcatchers"), RECIPE_FOR_DISASTER("Recipe for Disaster"), RECRUITMENT_DRIVE("Recruitment Drive"),
		REGICIDE("Regicide"), ROVING_ELVES("Roving Elves"), ROYAL_TROUBLE("Royal Trouble"), RUM_DEAL("Rum Deal"),
		SCORPION_CATCHER("Scorpion Catcher"), SEA_SLUG("Sea Slug"), THE_SLUG_MENACE("The Slug Menace"),
		SHADES_OF_MORTTON("Shades of Mort'ton"), SHADOW_OF_THE_STORM("Shadow of the Storm"),
		SHEEP_HERDER("Sheep Herder"), SHILO_VILLAGE("Shilo Village"), A_SOULS_BANE("A Soul's Bane"),
		SPIRITS_OF_THE_ELID("Spirits of the Elid"), SWAN_SONG("Swan Song"), TAI_BWO_WANNAI_TRIO("Tai Bwo Wannai Trio"),
		A_TAIL_OF_TWO_CATS("A Tail of Two Cats"), TEARS_OF_GUTHIX("Tears of Guthix"), TEMPLE_OF_IKOV("Temple of Ikov"),
		THRONE_OF_MISCELLANIA("Throneof Miscellania"), THE_TOURIST_TRAP("The Tourist Trap"),
		WITCHS_HOUSE("Witch's House"), TREE_GNOME_VILLAGE("Tree Gnome Village"), TRIBAL_TOTEM("Tribal Totem"),
		TROLL_ROMANCE("Troll Romance"), TROLL_STRONGHOLD("Troll Stronghold"), UNDERGROUND_PASS("Underground Pass"),
		WANTED("Wanted!"), WATCHTOWER("Watchtower"), COLD_WAR("Cold War"), THE_FREMENNIK_ISLES("The Fremennik Isles"),
		TOWER_OF_LIFE("Tower of Life"), THE_GREAT_BRAIN_ROBBERY("The Great Brain Robbery"),
		WHAT_LIES_BELOW("What Lies Below"), OLAFS_QUEST("Olaf's Quest"),
		ANOTHER_SLICE_OF_H_A_M("Another Slice of H.A.M."), DREAM_MENTOR("Dream Mentor"), GRIM_TALES("Grim Tales"),
		KINGS_RANSOM("King's Ransom"), THE_PATH_OF_GLOUPHRIE("The Path of Glouphrie"),
		BACK_TO_MY_ROOTS("Back to my Roots"), LAND_OF_THE_GOBLINS("Land of the Goblins"),
		DEALING_WITH_SCABARAS("Dealing with Scabaras"), WOLF_WHISTLE("Wolf Whistle"),
		AS_A_FIRST_RESORT("As a First Resort"), CATAPULT_CONSTRUCTION("Catapult Construction"),
		KENNITHS_CONCERNS("Kennith's Concerns"), LEGACY_OF_SEERGAZE("Legacy of Seergaze"),
		PERILS_OF_ICE_MOUNTAIN("Perils of Ice Mountain"), TOKTZ_KET_DILL("TokTz-Ket-Dill"),
		SMOKING_KILLS("SmokingKills"), ROCKING_OUT("Rocking Out"), SPIRIT_OF_SUMMER("Spirit of Summer"),
		MEETING_HISTORY("Meeting History"), ALL_FIRED_UP("All Fired Up"), SUMMERS_END("Summer's End"),
		DEFENDER_OF_VARROCK("Defender of Varrock"), WHILE_GUTHIX_SLEEPS("While Guthix Sleeps"),
		IN_PYRE_NEED("In Pyre Need"), MYTHS_OF_THE_WHITE_LANDS("Myths of the White Lands"),
		GLORIOUS_MEMORIES("Glorious Memories"), THE_TALE_OF_THE_MUSPAH("The Tale of the Muspah"),
		HUNT_FOR_RED_RAKTUBER("Hunt for Red Raktuber"), THE_CHOSEN_COMMANDER("The Chosen Commander"),
		SWEPT_AWAY("Swept Away"), FUR_N_SEEK("Fur 'n' Seek"), MISSING_MY_MUMMY("Missing My Mummy"),
		THE_CURSE_OF_ARRAV("The Curse of Arrav"), THE_TEMPLE_AT_SENNTISTEN("The Temple at Senntisten"),
		FAIRY_TALE_III_ORKS_RIFT("Fairy Tale III - Orks Rift"), BLACK_KNIGHTS_FORTRESS("Black Knights' Fortress"),
		FORGIVENESS_OF_A_CHAOS_DWARF("Forgiveness of a Chaos Dwarf"), WITHIN_THE_LIGHT("Within the Light"),
		NOMADS_REQUIEM("Nomad's Requiem"), BLOOD_RUNS_DEEP("Blood RunsDeep"), RUNE_MECHANICS("Rune Mechanics"),
		BUYERS_AND_CELLARS("Buyers and Cellars"), LOVE_STORY("Love Story"), THE_BLOOD_PACT("The Blood Pact"),
		QUIET_BEFORE_THE_SWARM("Quiet Before the Swarm"), ELEMENTAL_WORKSHOP_III("Elemental Workshop III"),
		A_VOID_DANCE("A Void Dance"), THE_VOID_STARES_BACK("The Void Stares Back"),
		RITUAL_OF_THE_MAHJARRAT("Ritual of the Mahjarrat"), DO_NO_EVIL("Do No Evil"),
		ELEMENTAL_WORKSHOP_IV("Elemental Workshop IV"), A_CLOCKWORK_SYRINGE("A Clockwork Syringe"),
		DEADLIEST_CATCH("Deadliest Catch"), SALT_IN_THE_WOUND("Salt in the Wound"),
		THE_BRANCHES_OF_DARKMEYER("The Branches of Darkmeyer"), ONE_PIERCING_NOTE("One Piercing Note"),
		LET_THEM_EAT_PIE("Let Them Eat Pie"), THE_ELDER_KILN("The Elder Kiln"),
		THE_FIREMAKERS_CURSE("The Firemaker's Curse"), SONG_FROM_THE_DEPTHS("Song from the Depths");
		private String tabText;

		QUEST(String tabText) {
			this.tabText = tabText;
		}
	}

	private static final HashMap<String, EnumMap<QUEST, STATUS>> questStatusCache =
			new HashMap<String, EnumMap<QUEST, STATUS>>();
	private static final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();

	/**
	 * @param quest The quest you are checking
	 * @return Returns the STATUS of the QUEST or NULL if the status is currently unavailable.
	 */
	public static STATUS getStatus(final QUEST quest) {
		STATUS statusFromCache = getStatusFromCache(quest);
		if (statusFromCache == null) {
			statusFromCache = buildCacheWithGoal(quest);
		}
		return statusFromCache;
	}

	/**
	 * Will RETURN true if and only if the STATUS is available and it equals {@link org.powerbot.game.api.methods.tab.Quest.STATUS DONE}.
	 * <p/>
	 * {See @see org.powerbot.game.api.methods.tab.Quest#getStatus(org.powerbot.game.api.methods.tab.Quest.QUEST) getStatus}
	 *
	 * @param quest The quest you are checking
	 * @return See description.
	 */
	public static boolean isDoneWithQuest(final QUEST quest) {
		return STATUS.DONE.equals(getStatus(quest));
	}

	/**
	 * Will RETURN true if and only if the STATUS is available and it equals {@link org.powerbot.game.api.methods.tab.Quest.STATUS IN_PROGRESS}.
	 * <p/>
	 * {See @see org.powerbot.game.api.methods.tab.Quest#getStatus(org.powerbot.game.api.methods.tab.Quest.QUEST) getStatus}
	 *
	 * @param quest The quest you are checking
	 * @return See description.
	 */
	public static boolean isWorkingOnQuest(final QUEST quest) {
		return STATUS.IN_PROGRESS.equals(getStatus(quest));
	}

	/**
	 * Will RETURN true if and only if the STATUS is available and it equals {@link org.powerbot.game.api.methods.tab.Quest.STATUS NOT_STARTED}.
	 * <p/>
	 * {See @see org.powerbot.game.api.methods.tab.Quest#getStatus(org.powerbot.game.api.methods.tab.Quest.QUEST) getStatus}
	 *
	 * @param quest The quest you are checking
	 * @return See description.
	 */
	public static boolean isNotStartedWithQuest(final QUEST quest) {
		return STATUS.NOT_STARTED.equals(getStatus(quest));
	}

	public boolean invalidateCacheForCurrentUser() {
		Player player = Players.getLocal();
		String name;
		if (player != null && (name = player.getName()) != null) {
			invalidateCacheForUser(name);
			return true;
		}
		return false;
	}

	public static void invalidateCacheForUser(String username) {
		cacheLock.writeLock().lock();
		questStatusCache.remove(username);
		cacheLock.writeLock().unlock();
	}

	private static STATUS buildCacheWithGoal(final QUEST goal) {
		Player p = Players.getLocal();
		String playerName = (p != null) ? p.getName() : null;
		if (playerName != null) {
			STATUS goalStatus;
			EnumMap<QUEST, STATUS> cacheBuild = buildStatusMapForCurrentUser();
			if (cacheBuild == null) {
				return null;
			}
			goalStatus = cacheBuild.get(goal);
			cacheLock.writeLock().lock();
			questStatusCache.put(playerName, cacheBuild);
			cacheLock.writeLock().unlock();
			return goalStatus;
		}
		return null;
	}

	private static STATUS getStatusFromCache(final QUEST quest) {
		String name = Players.getLocal().getName();
		cacheLock.readLock().lock();
		EnumMap<QUEST, STATUS> playerCache = questStatusCache.get(name);
		if (playerCache != null) {
			STATUS status = playerCache.get(quest);
			cacheLock.readLock().unlock();
			return status;
		}
		cacheLock.readLock().unlock();
		return null;
	}

	private static EnumMap<QUEST, STATUS> buildStatusMapForCurrentUser() {
		if (Tabs.QUESTS.isOpen()) {
			Widget tabWidget = Widgets.get(WIDGET_TAB_CONTAINER);
			if (tabWidget != null) {
				WidgetChild scrollInTab = tabWidget.getChild(WIDGET_SCROLL_CONTAINER);
				if (scrollInTab != null) {
					if (scrollInTab.validate()) {
						HashMap<String, QUEST> questNameToQuestMap = new HashMap<String, QUEST>();
						EnumMap<QUEST, STATUS> questStatusEnumMap = new EnumMap<QUEST, STATUS>(QUEST.class);
						for (QUEST q : QUEST.values()) {
							questNameToQuestMap.put(q.tabText, q);
						}
						for (WidgetChild widgetChild : scrollInTab.getChildren()) {
							String text;
							if ((text = widgetChild.getText()) != null) {
								QUEST matchingQuest = questNameToQuestMap.get(text);
								if (matchingQuest != null) {
									STATUS t = STATUS.fromInt(widgetChild.getTextColor());
									if (t != null) {
										questStatusEnumMap.put(matchingQuest, t);
									} else {
										return null;
									}
								}
							}
						}
						for (QUEST quest : QUEST.values()) {
							if (questStatusEnumMap.get(quest) == null) {
								return null;
							}
						}
						return questStatusEnumMap;
					}
				}
			}
		} else {
			if (Tabs.QUESTS.open()) {
				return buildStatusMapForCurrentUser();
			}
		}
		return null;
	}
}

