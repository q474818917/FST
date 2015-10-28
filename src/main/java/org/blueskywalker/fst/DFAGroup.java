package org.blueskywalker.fst;

import java.util.ArrayList;

public class DFAGroup {

    private FiniteStateTable dfa;
    private ArrayList<DFAGroupItem> groups;
    private int[] dfaNext;
    private int[] dfaGroup;
    // minimized structure;
    private ArrayList<StateType> mDFA;
    private ArrayList<FSTType> mTransitionTable;

    public DFAGroup(FiniteStateTable dfa) {
        this.dfa = dfa;
        groups = new ArrayList<DFAGroupItem>();
        dfaNext = new int[dfa.getDFA().size()];
        dfaGroup = new int[dfa.getDFA().size()];
    }

    public void initialize() {
        for (int i = 0; i < dfa.getDFA().size(); i++) {
            dfaGroup[i] = dfa.getDFA().get(i).finished ? 1 : 0;
            dfaNext[i] = 0;
        }

    }

    public void addState(int group, int newState) {

        while (groups.size() <= group) {
            groups.add(new DFAGroupItem(0, 0));
        }

        dfaNext[newState] = groups.get(group).head;
        dfaGroup[newState] = group;
        groups.get(group).head = newState;
        groups.get(group).size++;
    }

    public int deleteState(int group, int prev) {
        int stateToDelete = dfaNext[prev];
        dfaNext[prev] = dfaNext[stateToDelete];
        groups.get(group).size--;

        return stateToDelete;
    }

    public void initializeGroup(int state) {

        for (int i = 0; i < dfa.ofSize(state); i++) {
            if (dfa.ofNext(state, i) == state) {
                continue;
            }

            initializeGroup(dfa.ofNext(state, i));
        }

        dfaNext[state] = 0;
        addState(dfaGroup[state], state);
    }

    // To reduce redundant states
    // 
    public void compress() {
        // TODO Auto-generated method stub

        // lookup all states of a group to see if the state is not the same as head state 
        // do it until there is no new group
        int lastSize;
        int nGroup = groups.size();
        do {
            lastSize = nGroup;

            for (int i = 0; i < nGroup; i++) {
                if (groups.get(i).size == 1) {
                    continue;
                }

                // lookup a group
                int head = groups.get(i).head;
                int last = head;
                do {
                    int current = dfaNext[last];
                    // check if the state is the same as head state
                    int j = 0;

                    if (dfa.ofSize(head) == dfa.ofSize(current)) {
                        for (j = 0; j < dfa.ofSize(head); j++) {
                            if (dfa.ofChar(head, j) != dfa.ofChar(current, j)) {
                                break;
                            }
                            if (dfa.ofValue(head, j) != dfa.ofValue(current, j)) {
                                break;
                            }
                            if (dfaGroup[dfa.ofNext(head, j)] != dfaGroup[dfa.ofNext(current, j)]) {
                                break;
                            }
                        }
                    }
                    // second check
                    // if two states are different, 
                    // remove the state from group to which state belong
                    // add the state into new group
                    //
                    if ((dfa.ofSize(head) != dfa.ofSize(current))
                            || (j < dfa.ofSize(head))) {
                        addState(nGroup, deleteState(i, last));
                    } else {
                        last = dfaNext[last];
                    }
                } while (dfaNext[last] != 0);

                //if(nGroup > 4 ) break;
                if (groups.size() > nGroup) {
                    nGroup++;
                }
            }
        } while (nGroup != lastSize);
        dfaNext = null; // free memory

//		for(int i=0;i<groups.size();i++) 
//			System.out.println("["+i+"] size:"+groups.get(i).size+" head:"+groups.get(i).head);

        // make a compressed data (DFA, TransitionTable)
        arrange();
    }

    /// make minimized DFA and TransitionTable
    //  
    void arrange() {

        mDFA = new ArrayList<StateType>();
        mTransitionTable = new ArrayList<FSTType>();

        int location = 0;
        for (int group = 0; group < groups.size(); group++) {
            location = mTransitionTable.size();
            int head = groups.get(group).head;
            for (int i = 0; i < dfa.ofSize(head); i++) {
                mTransitionTable.add(
                        new FSTType(
                        dfa.ofChar(head, i),
                        dfa.ofValue(head, i),
                        dfaGroup[dfa.ofNext(head, i)]));
            }
            mDFA.add(new StateType(dfa.getDFA().get(head).finished,
                    dfa.ofSize(head), location));
        }
    }

    public ArrayList<StateType> getmDFA() {
        return mDFA;
    }

    public ArrayList<FSTType> getmTransitionTable() {
        return mTransitionTable;
    }
}
