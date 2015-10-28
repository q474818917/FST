package org.blueskywalker.fst;

import java.util.BitSet;

public class Transformer {

    private FlatFST flatFST;
    private BitSet used;
    private FiniteStateTable dfa;
    private int[] statePosition;

    public Transformer(FiniteStateTable dfa) {
        this.dfa = dfa;
        flatFST = new FlatFST();
        used = new BitSet(dfa.getTransitionTable().size());
        used.clear();
        statePosition = new int[dfa.getDFA().size()];
    }

    //
    // transform data structure from DFA+TransitionTable to FlatFST
    //
    void transform() {

        int current = 0;
        int beginning = 0;
        int i;

        // iteration through all states
        for (int state = 0; state < dfa.getDFA().size(); state++) {

            // check all transition space
            current = beginning - 1;
            do {
                do {// find unused space
                    for (++current; used.get(current); current++)
						;
                    // make sure for unused space
                } while ((dfa.ofFinished(state) == true)
                        && flatFST.isUsed(current));

                // check all transition space
                for (i = 0; i < dfa.ofSize(state); i++) {
                    if (flatFST.isUsed(current + dfa.ofChar(state, i))) {
                        break;
                    }
                }
            } while (i < dfa.ofSize(state));

            // copy all transitions into flatfst position 
            for (i = 0; i < dfa.ofSize(state); i++) {
                flatFST.put(current + dfa.ofChar(state, i),
                        new FSTType(
                        dfa.ofChar(state, i),
                        dfa.ofValue(state, i),
                        dfa.ofNext(state, i)));
            }

            if (dfa.ofFinished(state)) {
                flatFST.setFinished(current);
            }

            // set state position
            statePosition[state] = current;
            used.set(current);

            // move beginning position
            for (; used.get(beginning); beginning++)
				;
        }
        // free memory
        used = null;

        // most import concept of flatFST 
        // combine state(position of FST) and transition
        for (i = 0; i < flatFST.size(); i++) {
            if (flatFST.isUsed(i) && !flatFST.isFinished(i)) {
                flatFST.get(i).next = statePosition[flatFST.get(i).next];
            }
        }
    }

    public FlatFST getFlatFST() {
        return flatFST;
    }
}
