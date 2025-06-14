package com.myogoo.extendedterminal.menu;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;

public final class ETSlotSemantics {
    public static final SlotSemantic PLAYER_ARMOR = SlotSemantics.register("PLAYER_ARMOR", true, 1500);
    public static final SlotSemantic PLAYER_CURIOS = SlotSemantics.register("PLAYER_CURIOS", true, -1);

    public static final SlotSemantic BASIC_CRAFTING_GRID = SlotSemantics.register("BASIC_CRAFTING_GRID", true);
    public static final SlotSemantic BASIC_CRAFTING_RESULT = SlotSemantics.register("BASIC_CRAFTING_RESULT", false);

    public static final SlotSemantic ADVANCED_CRAFTING_GRID = SlotSemantics.register("ADVANCED_CRAFTING_GRID", true);
    public static final SlotSemantic ADVANCED_CRAFTING_RESULT = SlotSemantics.register("ADVANCED_CRAFTING_RESULT", false);

    public static final SlotSemantic ELITE_CRAFTING_GRID = SlotSemantics.register("ELITE_CRAFTING_GRID", true);
    public static final SlotSemantic ELITE_CRAFTING_RESULT = SlotSemantics.register("ELITE_CRAFTING_RESULT", false);

    public static final SlotSemantic ULTIMATE_CRAFTING_GRID = SlotSemantics.register("ULTIMATE_CRAFTING_GRID", true);
    public static final SlotSemantic ULTIMATE_CRAFTING_RESULT = SlotSemantics.register("ULTIMATE_CRAFTING_RESULT", false);

    public static final SlotSemantic AVARITIA_CRAFTING_GRID = SlotSemantics.register("AVARITIA_CRAFTING_GRID", true);
    public static final SlotSemantic AVARITIA_CRAFTING_RESULT = SlotSemantics.register("AVARITIA_CRAFTING_RESULT", false);

}
