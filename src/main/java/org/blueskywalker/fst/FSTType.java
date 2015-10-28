package org.blueskywalker.fst;

import java.io.Serializable;

public class FSTType implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7816333716848443547L;
    public char character;
    public int value;
    public int next;

    public FSTType(char character, int value, int next) {
        this.character = character;
        this.value = value;
        this.next = next;
    }

    public FSTType() {
        this((char) 0, 0, 0);
    }
}
