package org.blueskywalker.fst;

import java.io.Serializable;

public class StateType implements Serializable {
    
    static final long serialVersionUID = -6319568390308878330L;
    
    public boolean finished;
    public int size;
    public int location;

    public StateType(boolean finished, int size, int location) {
        this.finished = finished;
        this.size = size;
        this.location = location;
    }

    public StateType() {
        this(false, 0, 0);
    }
}
