/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blueskywalker.fst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author blueskywalker
 */
public class FiniteStateTableTest {

    String fileName;
    EntryTable entries;
    FiniteStateTable instance;
    private int[] DFAGeneratedNumber = null;

    public FiniteStateTableTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        fileName = getClass().getClassLoader().getResource("test.txt").getFile();
        entries = new EntryTable();
        entries.readFromFile(fileName);
        instance = new FiniteStateTable(entries);
        if (!entries.checkUnique()) {
            fail("file content is wrong");
        }

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of build method, of class FiniteStateTable.
     */
    @Test
    public void testBuild() {
        System.out.println("build");
        String fileName = "test.fst";
        try {
            instance.build(fileName);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();

        instance.traverseDFA(0, 0, sb, 0, 0);

    }

    @Test
    public void testBuildDFA() {

        instance.buildDFA(0, 0, entries.size(), 0);
        
        int i = 0;
        System.out.println("DFA");
        for (StateType s : instance.getDFA()) {
            System.out.printf("[%d]%d:%b:%d\n", i++, s.size, s.finished, s.location);
        }
        i = 0;
        System.out.println("FST");
        for (FSTType f : instance.getTransitionTable()) {
            System.out.printf("[%d]%c:%d:%d\n", i++, f.character, f.value, f.next);
        }

    }
}
