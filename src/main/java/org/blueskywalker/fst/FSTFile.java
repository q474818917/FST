/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blueskywalker.fst;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author blueskywalker
 */
public class FSTFile implements Serializable {

    static final long serialVersionUID = -2487357174943342448L;
    private ArrayList<StateType> state;
    private ArrayList<FSTType> fst;
    
    public FSTFile(ArrayList<StateType> state,ArrayList<FSTType> fst) {
        this.state= state;
        this.fst = fst;
    }

    public ArrayList<StateType> getState() {
        return state;
    }

    public void setState(ArrayList<StateType> state) {
        this.state = state;
    }

    public ArrayList<FSTType> getFst() {
        return fst;
    }

    public void setFst(ArrayList<FSTType> fst) {
        this.fst = fst;
    }
    
    
}
