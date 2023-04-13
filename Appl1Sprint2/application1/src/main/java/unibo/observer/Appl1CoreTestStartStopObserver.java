package unibo.observer;

import unibo.basicomm23.utils.ApplAbstractObserver;
import unibo.basicomm23.utils.CommUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class Appl1CoreTestStartStopObserver extends ApplAbstractObserver  {

    private boolean stopped = false;
    private boolean resumed = true;
    private Vector<String> moveHistory = new Vector<String>();
    private Set<String> moveCmds       = new HashSet<String>();

    public Appl1CoreTestStartStopObserver(){ // i messaggi che può ricevere
        moveCmds.add("robot-athomebegin");
        moveCmds.add("robot-moving");
        moveCmds.add("robot-stepdone");
        moveCmds.add("robot-stopped");
        moveCmds.add("robot-resumed");
        moveCmds.add("robot-athomeend");
    }

    // per riusare l'observer in fase di testing
    public void init( ) {

        moveHistory = new Vector<String>();
    }

    @Override
    public void update(String string) {
        CommUtils.outgreen("         Appl1CoreTestStartStopObserver | " + string  );

        if ( moveCmds.contains(string) ) {
            moveHistory.add(string);
            if ( string.equals("robot-stopped") )
                gotStopped();
            if ( string.equals("robot-resumed") )
                gotResumed();
        }
    }

    private synchronized void gotResumed() {
        resumed = true;
        stopped = false;
        notifyAll();
    }

    private synchronized void gotStopped() {
        stopped = true;
        resumed = false;
        notifyAll();
    }

    // utility methods
    public Vector<String> getMoveHistory() {

        return moveHistory;
    }

    public synchronized Vector<String> getMoveHistoryAfterStop( ) {
        while ( !stopped ) {
            CommUtils.outmagenta("         Appl1CoreTestStartStopObserver | wait for stop");
            try {
                wait();
            } catch ( InterruptedException e ) {
                CommUtils.outred("Appl1CoreTestStartStopObserver | get move history after stop error " + e);
            }
        }
        return moveHistory;
    }

    public synchronized void waitUntilResume( ){
        while ( !resumed ) {
            CommUtils.outmagenta("         Appl1CoreTestStartStopObserver | wait for resume");
            try {
                wait();
            } catch (InterruptedException e) {
                CommUtils.outred("Appl1CoreTestStartStopObserver | wait for resume error " + e);
            }
        }
    }

}
