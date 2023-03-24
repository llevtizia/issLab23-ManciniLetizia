package unibo.observer;

import unibo.basicomm23.utils.ApplAbstractObserver;
import unibo.basicomm23.utils.CommUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class Appl1CoreTestStartStopObserver extends ApplAbstractObserver {

    private Vector<String> moveHistory = new Vector<String>();
    private Set<String> moveCmds = new HashSet<String>();

    private boolean stopped = false;
    private boolean resumed = true;

    public Appl1CoreTestStartStopObserver() {
        moveCmds.add("robot-athomebegin");
        moveCmds.add("robot-moving");
        moveCmds.add("robot-stepdone");
        moveCmds.add("robot-stopped");
        moveCmds.add("robot-resumed");
        moveCmds.add("robot-athomeend");
    }

    // per riusare l'observer rinizializzo moveHistory
    public void init( ) {
        moveHistory = new Vector<String>();
    }

    @Override
    public void update(String msg) {
        if ( moveCmds.contains(msg) ) {
            moveHistory.add(msg);
            if ( msg.equals("robot-stopped") )
                gotStopped();
            if ( msg.equals("robot-resumed") )
                gotResumed();
        }
    }


    private void gotResumed() {
        resumed = true;
        stopped = false;
        notifyAll();
    }

    private void gotStopped() {
        stopped = true;
        resumed = false;
        notifyAll();
    }

    public Vector<String> getMoveHistory( ) {
        return moveHistory;
    }

    public synchronized Vector<String> getMoveHistoryAfterStop(){
        while ( ! stopped    ) {
            try {
                wait();
            } catch (InterruptedException e) {
                CommUtils.outred("      getMoveHistoryAfterStop | ERROR:" + e.getMessage());
            }
        }
        return moveHistory;
    }

    public synchronized void waitUntilResume(){
        while ( ! (resumed )  ) {
            try {
                wait();
            } catch (InterruptedException e) {
                CommUtils.outred("      waitUntilResume | ERROR:" + e.getMessage());
            }
        }
    }

}
