/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blueskywalker.fst;

import java.net.URL;
import junit.framework.TestCase;

/**
 *
 * @author blueskywalker
 */
public class EntryTableTest extends TestCase {
    
    String fileName;
    
    public EntryTableTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = "test.txt";
        fileName = getClass().getClassLoader().getResource(name).getFile();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of readFromFile method, of class EntryTable.
     */
    public void testReadFromFile() {
        System.out.println("readFromFile");
                
        System.out.println(fileName);
        
        EntryTable instance = new EntryTable();
        instance.readFromFile(fileName);
        
        assertEquals(10, instance.size());
    }

    /**
     * Test of checkUnique method, of class EntryTable.
     */
    public void testCheckUnique() {
        System.out.println("checkUnique");
        EntryTable instance = new EntryTable();
        boolean result = instance.checkUnique();
        assertEquals(true, result);
        instance.readFromFile(fileName);
        result = instance.checkUnique();
        assertEquals(true, result);
    }
}
