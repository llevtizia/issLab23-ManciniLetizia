package unibo.basicomm23.http;

import unibo.basicomm23.interfaces.IObserver;
import unibo.basicomm23.utils.CommUtils;
import unibo.common.IAppl1Core;
import unibo.common.IVrobotMoves;
import unibo.observer.Appl1ObserverForPath;
import unibo.supports.VrobotHLMovesHTTPApache;

public class Appl1CoreSprint2 extends java.util.Observable implements IAppl1Core {

    protected boolean started = false;
    protected boolean stopped = false;
    protected boolean isRunning = false;

    private Appl1ObserverForPath obsForPath;
    protected IVrobotMoves vr;

    public Appl1CoreSprint2( ) {
        stopped = false;
        configure();
    }

    // ereditato da Observable
    public void addObserver(IObserver o) {
        super.addObserver(o);
    }

    private void configure( ) {
        String URL = "localhost:8090/api/move";
        //String URL = virtualRobotIp+":8090/api/move"; con ip letto da file
        HTTPCommApache httpSupport = new HTTPCommApache(  URL );
        vr         = new VrobotHLMovesHTTPApache( httpSupport );
        //AGGIUNGO OBSERVER
        obsForPath = new Appl1ObserverForPath();
        addObserver(  obsForPath );
    }

    private void updateObservers(String msg){
        setChanged();
        notifyObservers(msg);
    }


    @Override
    public void start() throws Exception {
        if ( ! started ) {
            started = true;
            walkAtBoundary();
        } else
            CommUtils.outred("Application already started! ");
    }

    private void walkAtBoundary() throws Exception {
        /*
        for ( int i = 0; i < 4; i++ ) {
            walkBySteppingWithStop(i);
            vr.turnLeft();
        }*/
        robotMustBeAtHome("START"); //SE PROBLEMI: updateObservers("robot-athomebegin");
        updateObservers("robot-athomebegin");
        isRunning = true;
        for ( int i = 0; i <4 ; i++ ) {
            walkBySteppingWithStop(i);
            vr.turnLeft();
        }
        isRunning = false;
        updateObservers("robot-athomeend");
        robotMustBeAtHome("END"); //SE PROBLEMI: updateObservers("robot-athomeend");
    }

    private void walkBySteppingWithStop(int i) throws Exception {
        CommUtils.outyellow("walkBySteppingWithStop n= "+ i );

        boolean stepOk = true;
        while ( stepOk ) {
            if ( stopped ) {
                CommUtils.beep();
                updateObservers("robot-stopped");
                waitResume();
            }

            updateObservers("robot-moving");
            stepOk = vr.step(350);
            if ( !stepOk )
                notifyObservers("robot-collision");
            else
                updateObservers("robot-stepdone");
            CommUtils.delay(300); // to show the step

        }
        return;
    }

    private synchronized void waitResume() {
        while ( stopped ) {
            try {
                wait();
            } catch ( InterruptedException e ) {
                CommUtils.outred("waitResume interrupted | " + e);
            }
        }
        updateObservers("robot-resumed");
    }

    @Override
    public synchronized void stop() {
        stopped = true;
    }

    @Override
    public synchronized  void resume() {
        stopped = false;
        notifyAll();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public String getCurrentPath() {
        if ( obsForPath != null)
            return obsForPath.getCurrentPath();
        else
            return "no path";
    }

    // robot
    private void robotMustBeAtHome(String msg) throws Exception{
        boolean b = checkRobotAtHome( );
        CommUtils.outblue("robotMustBeAtHome " + msg + " " + b);
        if ( b ) {
            if ( msg.equals("START") )
                updateObservers("robot-athomebegin");
            if ( msg.equals("END") )
                updateObservers("robot-athomeend");
        } else
            throw new Exception("START: robot must be at HOME");
    }

    private boolean checkRobotAtHome( ) {
        try {
            vr.turnRight();
            boolean res = vr.step(200);
            if ( res )
                return false;

            vr.turnRight();
            res = vr.step(200);
            if ( res )
                return false;
            vr.turnLeft();
            vr.turnLeft();
            return true;
        } catch (Exception e) {
            CommUtils.outred("Appl1CoreSprint2 | checkRobotAtHome ERROR:" + e.getMessage());
            return false;
        }
    }

    public boolean evalBoundaryDone(){
        if ( obsForPath != null )
            return obsForPath.evalBoundaryDone();
        else return true;
    }
}
