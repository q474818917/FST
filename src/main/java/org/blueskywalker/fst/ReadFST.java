/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blueskywalker.fst;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 *
 * @author blueskywalker
 */
public class ReadFST {

    private static final Logger logger = Logger.getLogger(ReadFST.class);
    private FSTFile fst;

    public ReadFST() {
    }


    public static void main(String[] args) {
    
        if(args.length < 1) {
            System.out.printf("ReadFST file.fst\n");
            System.exit(0);
        }
        
        FiniteStateTable fst = new FiniteStateTable();
        try {
            
            fst.readFSTFile(args[0]);
            
            fst.traverseDFA(0, 0, new StringBuilder(), 0, 0);
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
    }
}
