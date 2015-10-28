package org.blueskywalker.fst;

public class TransitionData {

    public char transition;
    public int begin;
    public int size;

    TransitionData(char transition, int begin, int size) {
        this.transition = transition;
        this.begin = begin;
        this.size = size;
    }

    TransitionData() {
        this((char) 0, 0, 0);
    }
}
