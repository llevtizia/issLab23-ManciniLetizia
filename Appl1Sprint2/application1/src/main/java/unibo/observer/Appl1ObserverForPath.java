package unibo.observer;

import unibo.basicomm23.utils.ApplAbstractObserver;
import unibo.basicomm23.utils.CommUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class Appl1ObserverForPath extends ApplAbstractObserver {

    private boolean applIsTerminated ;
    private Vector<String> moveHistory = new Vector<String>();
    private Set<String> moveCmds       = new HashSet<String>();

    public Appl1ObserverForPath() {
        moveCmds.add("robot-stepdone");
        moveCmds.add("robot-turnLeft");
        moveCmds.add("robot-collision");
        moveCmds.add("robot-athomebegin");
        moveCmds.add("robot-athomeend");
    }

    @Override
    public void update(String msg) {
        String s;

        if (moveCmds.contains(msg))
            CommUtils.outgreen("         Appl1ObserverForpath:" + msg);

        if (msg.contains("robot-stepdone"))
            moveHistory.add("w");
        if (msg.contains("robot-turnLeft"))
            moveHistory.add("l");
        if (msg.equals("robot-collision")) {
            moveHistory.add("|");

            s = getPathAsCompactString();
            //if( s1.length() > 5) CommUtils.outred(s1); else
            CommUtils.outgreen(s);
        }
        if (msg.equals("robot-athomeend"))
            setTerminated();

        CommUtils.outgreen( getPath() );
    }

    private synchronized void setTerminated(){
        CommUtils.outmagenta("         Appl1ObserverForPath: Application TERMINATED");
        applIsTerminated = true;
        notifyAll(); //riattiva getPath
    }

    // valore corrente del path
    public String getCurrentPath( ) {
        return getPathAsCompactString();
    }

    // blocca il chiamante se la computazione è in corso
    public synchronized String getPath(){
        while( ! applIsTerminated ) {
            try {
                wait();
            } catch (InterruptedException e) {
                CommUtils.outred("      getPath | ERROR:" + e.getMessage());
            }
        }
        return getPathAsCompactString();
    }

    private String getPathAsCompactString(){
        String hflat = moveHistory.toString()
                .replace("[","")
                .replace("]","")
                .replace(",","")
                .replace(" ","");
        return hflat;
    }

    public boolean evalBoundaryDone(){
        String hflat      = getPath();  //bloccante
        String[] splitted = hflat.toString().split("l");
        boolean boundaryDone = splitted[0].length() == splitted[2].length()
                && splitted[1].length() == splitted[3].length();
        CommUtils.outmagenta("Appl1ObserverForpath: evalBoundaryDone= " + boundaryDone);
        return boundaryDone;
    }

}
