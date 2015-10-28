package org.blueskywalker.fst;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class DecodeFST {
    // private static Logger logger = Logger.getLogger(DecodeFST.class);

    public FlatFST fst;

    public DecodeFST(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));

        fst = (FlatFST) ois.readObject();
        ois.close();
    }

    public FlatFST getFST() {
        return fst;
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("DecodeFST FSTFile");
            return;
        }

        try {
            
             DecodeFST FST = new DecodeFST(args[0]);


             /*
             String input = args[1];

             StringBuilder match = new StringBuilder();

             int hash = FST.getLongestMatch(input, 0, 0, match, 0);

             System.out.println(input + ":" + match + ":" + hash);


             ArrayList<StringBuilder> set = FST.getPossibleStringSet(input, 0, 0);

             if (set != null) {
             for (StringBuilder str : set) {
             System.out.println(str.reverse());
             }
             }
             */
            System.out.println(FST.getNumberOfEntry());

            int hash = 0;
            while (true) {
                String url = FST.hashToString(hash++);

                if (url == null) {
                    break;
                }

                System.out.println(url);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String hashToString(int hashValue) {
        StringBuilder ret = new StringBuilder();

        if (fst == null) {
            return null;
        }

        int currentState = 0;

        final int MAX_VALUE = getMaxValue();

        while (true) {
            // find character 
            // idea :  use hashValue as a tool which can distinguish 
            //         with other character in same state
            char ch = (char) 0;

            for (int i = 1; i < MAX_VALUE; i++) {
                if (fst.isValid(currentState, i)) {
                    if (fst.ofValue(currentState, i) > hashValue) {
                        break;
                    } else {
                        ch = (char) i;
                    }
                }
            }

            // not found
            if ((int) ch == 0) {
                return null;
            }

            ret.append(ch);
            // move to next state
            hashValue -= fst.ofValue(currentState, (int) ch);
            currentState = fst.ofNext(currentState, (int) ch);

            // verify end condition
            if (fst.ofFinished(currentState, 0) && hashValue <= 0) {
                break;
            }
        }
        return ret.toString();
    }

    private int getMaxValue() {
        return Math.min(fst.size(), Character.MAX_VALUE);
    }

    public int getNumberOfEntry() {

        if (fst == null) {
            return 0;
        }

        int currentState = 0;
        int hash = 0;
        int i = 0;

        final int MAX_VALUE = getMaxValue();

        while (true) {
            for (i = MAX_VALUE; i >= 1; i--) {
                if (fst.isValid(currentState, i)) {
                    hash += fst.ofValue(currentState, i);
                    break;
                }
            }
            // Not found 
            if (i == 0) {
                break;
            }

            // move to next state
            currentState = fst.ofNext(currentState, i);
        }
        // check end condition
        if (fst.ofFinished(currentState, 0)) {
            return ++hash;
        }
        return 0;
    }

    public int stringToHash(String input) {

        if (fst == null) {
            return -1;
        }

        int hash = 0;
        int currentState = 0;

        for (int pos = 0; pos < input.length(); pos++) {
            char ch = input.charAt(pos);

            if (!fst.isValid(currentState, ch) // there is no more transition
                    || fst.ofNext(currentState, (int) ch) == 0) // impossible condition
            {
                break;
            }

            hash += fst.ofValue(currentState, (int) ch); // increase hash value
            currentState = fst.ofNext(currentState, (int) ch); // move to next state


            if (fst.ofFinished(currentState, 0)) {
                return hash;
            }

        }

        return -1;
    }

    public ArrayList<StringBuilder> getPossibleStringSet(String input, int pos, int state) {

        if (input == null) {
            return null;
        }

        ArrayList<StringBuilder> ret = new ArrayList<StringBuilder>();
        int match = 0;

        if (pos < input.length()) {
            char ch = input.charAt(pos);
            if (fst.isValid(state, ch)) {
                return getPossibleStringSet(input, pos + 1, fst.ofNext(state, ch));
            } else {
                match = pos;
            }
        } else {
            match = pos;
        }

        if (fst.isFinished(state)) {
            ret.add(new StringBuilder(input.substring(0, match)).reverse());
        } else {
            ArrayList<StringBuilder> rest = getPossibleStringSet(state);
            for (StringBuilder sb : rest) {
                ret.add(sb.append(new StringBuilder(input.substring(0, match)).reverse()));
            }
        }
        return ret;
    }

    public ArrayList<StringBuilder> getPossibleStringSet(int state) {

        if (fst.isFinished(state)) {
            return null;
        }


        final int MAX_VALUE = getMaxValue();

        ArrayList<StringBuilder> ret = new ArrayList<StringBuilder>();

        for (int i = 1; i < MAX_VALUE; i++) {
            if (fst.isValid(state, i)) {
                ArrayList<StringBuilder> rest = getPossibleStringSet(fst.ofNext(state, i));
                if (rest == null) {
                    ret.add(new StringBuilder().append((char) i));
                } else {
                    for (StringBuilder str : rest) {
                        ret.add(str.append((char) i));
                    }
                }
            }
        }
        return ret;
    }

    public int getLongestMatch(String input, int pos, int state, StringBuilder match, int hash) {

        if (pos == input.length()) {
            return hash;
        }

        char ch = input.charAt(pos);
        if (fst.isValid(state, ch)) {
            match.append(ch);
            return getLongestMatch(input, pos + 1, fst.ofNext(state, (int) ch),
                    match, hash + fst.ofValue(state, (int) ch));
        }
        /*
         if(fst.ofFinished(state, 0)) {
         // for debugging, check current state
         int nItem = (int) fst.ofChar(state, 0);			
         return hash;
         }
         */
        return hash;
    }
}
