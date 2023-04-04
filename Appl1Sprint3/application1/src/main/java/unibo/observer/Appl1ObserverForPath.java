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

    private String s;

    public Appl1ObserverForPath( ) { // i messaggi che può ricevere
        moveCmds.add("robot-stepdone");
        moveCmds.add("robot-collision");
        moveCmds.add("robot-turnLeft");
        moveCmds.add("robot-athomeend");
    }

    @Override
    public void update(String string) {
        if ( moveCmds.contains(string) )
            CommUtils.outgreen("         Appl1ObserverForPath: " + string);

        if ( string.contains("robot-stepdone") )
            moveHistory.add("w");
        else if ( string.contains("robot-turnLeft") )
            moveHistory.add("l");
        else if ( string.contains("robot-collision") ) {
            moveHistory.add("|");
            s = getPathAsCompactString();
            CommUtils.outgreen(s);
        }
        else if ( string.equals("robot-athomeend") )
            setTerminated();
       // CommUtils.outgreen( getPath() );

    }

    private void setTerminated( ) {
        applIsTerminated = true;
        notifyAll(); // riattiva getPath
    }

    // getPath
    private String getPathAsCompactString( ) {
        String flatPath = moveHistory.toString()
                .replace("[","")
                .replace("]","")
                .replace(",","")
                .replace(" ","");
        CommUtils.outyellow("Appl1ObserverForpath: flatPath = " + flatPath);
        return flatPath;
    }

    public String getCurrentPath( ){
        return getPathAsCompactString( );
    }

    public synchronized String getPath(){
        CommUtils.outmagenta("Appl1ObserverForpath: getPath applIsTerminated=" + applIsTerminated);
        while ( ! applIsTerminated ) {
            try {
                wait();
            } catch (InterruptedException e) {
                CommUtils.outred("Appl1ObserverForpath: getPath ERROR");
            }
        }
        //CommUtils.outmagenta("Appl1ObserverForpath: getPath RESUMED");
        return getPathAsCompactString();
    }

    // to check the boundary
    public boolean evalBoundaryDone(){
        String hflat      = getPath();  //bloccante
        String[] splitted = hflat.toString().split("l");
        //CommUtils.outyellow("Appl1ObserverForpath: splitted[0]=" + splitted[0]);
        boolean boundaryDone = splitted[0].length()==splitted[2].length()
                && splitted[1].length()==splitted[3].length();
        //the JVM disables assertion validation by default. Insert VM option -enableassertions
        //assert( boundaryDone );
        CommUtils.outmagenta("Appl1ObserverForpath: evalBoundaryDone="+ boundaryDone);
        return boundaryDone;
    }


}
