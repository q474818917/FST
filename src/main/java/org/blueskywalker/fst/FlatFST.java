package org.blueskywalker.fst;

import java.util.ArrayList;

public class FlatFST extends ArrayList<FSTType> {

    /**
     *
     */
    private static final long serialVersionUID = 410343202024410777L;

    public FlatFST() {
    }

    public boolean isUsed(int index) {
        if (index >= size()) {
            return false;
        }
        return this.get(index).next != 0;
    }

    public boolean isFinished(int index) {
        if (index >= size()) {
            return false;
        }

        return this.get(index).next == -1;
    }

    public void put(int index, FSTType fstType) {
        while (index >= size()) {
            add(new FSTType());
        }

        set(index, fstType);
    }

    public void setFinished(int index) {
        put(index, new FSTType((char) -1, -1, -1));
    }

    public char ofChar(int state, int index) {
        if ((state + index) >= size()) {
            return (char) 0;
        }
        return get(state + index).character;
    }

    public int ofValue(int state, int index) {
        if ((state + index) >= size()) {
            return 0;
        }
        return get(state + index).value;
    }

    public int ofNext(int state, int index) {
        if ((state + index) >= size()) {
            return -1;
        }
        return get(state + index).next;
    }

    public int ofNext(int state, char ch) {
        return ofNext(state, (int) ch);
    }

    public boolean ofFinished(int state, int index) {
        return ofNext(state, index) == -1;
    }

    public boolean isValid(int state, int index) {
        return ofChar(state, index) == (char) index && !ofFinished(state, index);
    }

    public boolean isValid(int state, char ch) {
        return isValid(state, (int) ch);
    }
}
