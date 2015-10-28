package org.blueskywalker.fst;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class FiniteStateTable {

    private static Logger logger = Logger.getLogger(FiniteStateTable.class);
    public static final int DEFAULT_SIZE = 1024 * 1024;
    private ArrayList<FSTType> TransitionTable;
    private ArrayList<StateType> DFA;
    private EntryTable entries;
    private int[] DFAGeneratedNumber = null;
    private FSTFile fstFile;
    
    public FiniteStateTable() {
        TransitionTable = new ArrayList<FSTType>(DEFAULT_SIZE);
        DFA = new ArrayList<StateType>(DEFAULT_SIZE);       
    }
    
    public FiniteStateTable(EntryTable entries) {
        this();
        this.entries = entries;
    }

    void build(String fileName) throws FileNotFoundException, IOException {
        // DFA Deterministic Finite Automaton,确定性有限自动机
        logger.info("Building DFA...");
        buildDFA(0, 0, entries.size(), 0);
        logger.info("Total States are " + DFA.size());
        logger.info("Total Trnasitions are " + TransitionTable.size());


        //// Hash Calculation
        ///
        logger.info("Hash Value Calculating....");
        DFAGeneratedNumber = new int[DFA.size()];
        // initialize
        for (int i = 0; i < DFAGeneratedNumber.length; i++) {
            DFAGeneratedNumber[i] = 0;
        }
        // generate
        generateDFANumber(0);

        // add Hash Number
        addHashNumber(0);
        logger.info("Hash Value Calculation is done.");

        //// minimizing DFA
        logger.info("minimizing DFA .....");
        DFAGroup minimizing = new DFAGroup(this);

        minimizing.initialize();
        minimizing.initializeGroup(0);
        minimizing.compress();


        logger.info("minimizing is done");
        logger.info("Total States are " + minimizing.getmDFA().size());
        logger.info("Total Transitions are " + minimizing.getmTransitionTable().size());

        // free memory and reinitialize variables with minimized data 
        DFA = minimizing.getmDFA();
        TransitionTable = minimizing.getmTransitionTable();
        minimizing = null;

//		StringBuffer str = new StringBuffer();		
//		traverseDFA(0, 0,str, 0, 0);

        /// flattening and
        /// make FST file
        /*
         logger.info("flattening ....");
         Transformer transformer = new Transformer(this);
         transformer.transform();
         logger.info("flattening is done.");
         */

        // save
        logger.info("save to FST file..");
        //saveFST2File(fileName, transformer.getFlatFST());
        saveFST2File(fileName);
        
        logger.info("All jobs are completed!!!");
    }

    public int traverseDFA(int root, int value, StringBuilder str, int pos, int n) {
        if (ofFinished(root)) {
            n += ofFinished(root) ? 1 : 0;
            str.setLength(pos);
            System.out.println(str.toString() + ":" + value);
        }
        for (int i = 0; i < ofSize(root); i++) {
            if (ofNext(root, i) == root) {
                continue;
            }

            while (pos >= str.length()) {
                str.append(' ');
            }

            str.setCharAt(pos, ofChar(root, i));
            n = traverseDFA(ofNext(root, i), value + ofValue(root, i), str, pos + 1, n);
        }
        return n;
    }

    private int generateDFANumber(int state) {
        // TODO Auto-generated method stub
        int currentValue = 0, nextValue = 0;

        for (int i = 0; i < DFA.get(state).size; i++) {

            if (ofNext(state, i) == state) {
                continue;
            }

            nextValue = DFAGeneratedNumber[ofNext(state, i)];

            if (nextValue != 0) {
                currentValue += nextValue;
            } else {
                currentValue += generateDFANumber(ofNext(state, i));
            }
        }

        currentValue += DFA.get(state).finished ? 1 : 0;
        DFAGeneratedNumber[state] = currentValue;
        return currentValue;
    }

    private void addHashNumber(int state) {

        for (int i = 0; i < DFA.get(state).size; i++) {
            if (ofNext(state, i) == state) {
                ofValueSet(state, i, 0);
            } else if (i == 0) {
                ofValueSet(state, i, DFA.get(state).finished ? 1 : 0);
            } else {
                ofValueSet(state, i,
                        ofValue(state, i - 1) + DFAGeneratedNumber[ofNext(state, i - 1)]);
            }

            if (ofNext(state, i) != state) {
                addHashNumber(ofNext(state, i));
            }
        }
    }

    void buildDFA(int root, int start, int size, int stringPos) {

        ArrayList<TransitionData> subSet = null;

        while (DFA.size() <= root) {
            DFA.add(new StateType());
        }

        // check END State		
        if (entries.get(start).length() == stringPos) {
            DFA.get(root).finished = true;
            DFA.get(root).size = 0;
            DFA.get(root).location = 0;

        }

        // count transition character			
        subSet = countTransitionChar(start, size, stringPos);

        DFA.get(root).size = subSet.size();
        DFA.get(root).location = TransitionTable.size();

        // add TransitionTable
        for (int i = 0; i < subSet.size(); i++) {
            TransitionTable.add(
                    new FSTType(subSet.get(i).transition, 0, DFA.size()));
            DFA.add(new StateType());
        }


        // follow all transition
        for (int i = 0; i < DFA.get(root).size; i++) {
            buildDFA(ofNext(root, i), subSet.get(i).begin,
                    subSet.get(i).size, stringPos + 1);
        }
    }

    private ArrayList<TransitionData> countTransitionChar(int start, int size, int stringPos) {
        ArrayList<TransitionData> data = new ArrayList<TransitionData>();

        char lastChar = (char) -1;
        for (int i = 0; i < size; i++) {
            String entry = entries.get(start + i);

            if (entry.length() <= stringPos) {
                continue;
            }

            char currentChar = entry.charAt(stringPos);

            if (currentChar != lastChar) {
                data.add(new TransitionData(currentChar, start + i, 0));
                lastChar = currentChar;
            }
            if (data.size() > 0) {
                data.get(data.size() - 1).size++;
            }
        }

        return data;
    }

    void saveFST2File(String fileName) throws FileNotFoundException, IOException {

        
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        fstFile = new FSTFile(DFA, TransitionTable);
        oos.writeObject(fstFile);
        oos.close();

    }

    void readFSTFile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
        fstFile = (FSTFile) ois.readObject();
        ois.close();
        DFA = fstFile.getState();
        TransitionTable = fstFile.getFst();
    }

    int ofSize(int state) {
        return DFA.get(state).size;
    }

    char ofChar(int state, int n) {
        return TransitionTable.get(DFA.get(state).location + n).character;
    }

    int ofValue(int state, int n) {
        return TransitionTable.get(DFA.get(state).location + n).value;
    }

    void ofValueSet(int state, int n, int newValue) {
        TransitionTable.get(DFA.get(state).location + n).value = newValue;
    }

    int ofNext(int state, int n) {
        return TransitionTable.get(DFA.get(state).location + n).next;
    }

    public ArrayList<FSTType> getTransitionTable() {
        return TransitionTable;
    }

    public ArrayList<StateType> getDFA() {
        return DFA;
    }

    boolean ofFinished(int state) {
        return DFA.get(state).finished;
    }
}
