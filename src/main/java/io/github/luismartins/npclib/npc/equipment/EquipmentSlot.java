package io.github.luismartins.npclib.npc.equipment;

public enum EquipmentSlot {

    HAND(0),
    BOOTS(1),
    LEGGINGS(2),
    CHESTPLATE(3),
    HELMET(4);

    private Integer value;

    EquipmentSlot(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
