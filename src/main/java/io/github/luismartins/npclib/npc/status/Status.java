package io.github.luismartins.npclib.npc.status;

public enum Status {

    HURT((byte) 2),
    DEAD((byte) 3);

    private Byte value;

    Status(Byte value) {
        this.value = value;
    }

    public Byte getValue() {
        return value;
    }
}
