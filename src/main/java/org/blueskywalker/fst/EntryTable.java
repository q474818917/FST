package org.blueskywalker.fst;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

public class EntryTable extends ArrayList<String> {

    /**
     *
     */
    private static final long serialVersionUID = 35211399975284427L;
    private final static Logger logger = Logger.getLogger(EntryTable.class);

    public EntryTable() {
    }

    public void readFromFile(String name) {

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    new FileInputStream(name), "UTF-8"));

            clear();

            String entry;
            while ((entry = in.readLine()) != null) {
                add(entry);
//				System.out.println(entry);
            }

            logger.info("Sorting Entries in Memory");
            Collections.sort(this);
            logger.info("Sorting is done!!!");


        } catch (IOException e) {
            logger.error(e);
        }
    }

    public boolean checkUnique() {
        int size = this.size();
        String lastString = "";

        for (int i = 0; i < size; i++) {
            if (this.get(i).compareTo(lastString) == 0) {
                logger.warn("You need to make data unique!!!");
                return false;
            }
            lastString = this.get(i);
        }
        return true;
    }
}
