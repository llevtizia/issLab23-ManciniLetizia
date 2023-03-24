package unibo.basicomm23.http;

import unibo.basicomm23.interfaces.IObserver;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.interfaces.IAppl1Core;
import unibo.common.IVrobotMoves;
import unibo.model.RoomModel;
import unibo.observer.Appl1ObserverForPath;
import unibo.supports.VrobotHLMovesHTTPApache;

public class Appl1Core extends java.util.Observable  implements IAppl1Core  {
    protected boolean started    = false;
    protected boolean stopped    = false;
    private boolean isRunning    = false;
    protected IVrobotMoves vr ;
    private Appl1ObserverForPath obsForpath;

    protected RoomModel room ;

    public Appl1Core(){
        stopped = false;
        configure();
    }

    protected void configure(){
        String URL = "localhost:8090/api/move";
        //URL potrebbe essere letto da un file di configurazione
        HTTPCommApache httpSupport = new HTTPCommApache(  URL );
        vr = new VrobotHLMovesHTTPApache( httpSupport );
        //AGGIUNGO OBSERVER
        obsForpath = new Appl1ObserverForPath();
        addObserver(  obsForpath );

    }

    // observer
    private void updateObservers(String msg){
        setChanged(); // setto i cambiamenti
        notifyObservers(msg);
    }

    @Override
    public void start() {
        try {
            if ( ! started ) {
                checkRobotAtHome();
                started = true;
                walkAtBoundary();
            } else
                CommUtils.outred("Application already started");
        } catch (Exception e) {
            CommUtils.outred("      start | ERROR:" + e.getMessage());
        }
    }


    private void walkAtBoundary() {
        try {
            robotMustBeAtHome("START"); //SE PROBLEMI: updateObservers("robot-athomebegin");
            //updateObservers("robot-athomebegin");
            isRunning  = true;
            for( int i=1; i<=4; i++ ) {
                walkBySteppingWithStop(i);
                vr.turnLeft();
                //CommUtils.delay(300);
                updateObservers("robot-turnLeft");
            }
            isRunning  = false;
            //updateObservers("robot-athomeend");
            robotMustBeAtHome("END"); //SE PROBLEMI: updateObservers("robot-athomeend");
        } catch (Exception e) {
            CommUtils.outred("      walkAtBoundary | ERROR:" + e.getMessage());
        }
    }


    public void walkBySteppingWithStop(int n) throws Exception {
        CommUtils.outyellow("walkBySteppingWithStop n:"+ n );

        boolean stepOk = true;
        while ( stepOk  ) {
            if ( stopped ) {
                CommUtils.beep();
                updateObservers("robot-stopped");
                waitResume();
            }

            updateObservers("robot-moving");
            stepOk =  vr.step(350);

            if ( ! stepOk )
                notifyObservers("robot-collision");
            else
                updateObservers("robot-stepdone");
            CommUtils.delay(300); //to show the steps better
        }
        return;
    }

    public synchronized void waitResume(){
        while ( stopped ){
            try {
                wait();
            } catch (InterruptedException e) {
                CommUtils.outred("      waitResume | ERROR: " + e.getMessage());
            }
        }
        updateObservers("robot-resumed");
    }



    @Override
    public  void stop(  ) {
        stopped = true;
        try {
            vr.halt();
        } catch (Exception e) {
            CommUtils.outred("      stop | ERROR: " + e.getMessage());
        }
    }

    @Override
    public synchronized void resume(  ){
        stopped = false;
        notifyAll();  //riattiva waitResume
    }



    private void robotMustBeAtHome(String msg) throws Exception{
        boolean b = checkRobotAtHome();
        CommUtils.outblue("robotMustBeAtHome: " + msg + " " + b);
        if ( b ) {
            if ( msg.equals("START") )
                updateObservers("robot-athomebegin");
            if ( msg.equals("END") )
                updateObservers("robot-athomeend");
        }
        else
            /*
            while ( ! b ) {
            * CommUtils.outblue("Please put robot in HOME");
            * CommUtils.delay(3000);
            * b = checkRobotAtHome();
            * }
            */
            throw new Exception("START: Robot must be at HOME");
    }

    private boolean checkRobotAtHome( ) {
        try {
            vr.turnRight();
            boolean res = vr.step(200); //a little step ...
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
            return false;
        }
    }

    public boolean isRunning(){
        return  isRunning;
    }

    @Override
    public String getCurrentPath() {
        return null;
    }

    @Override
    public void addObserver(IObserver o) {

    }

    public boolean evalBoundaryDone() {
        if ( obsForpath != null )
            return obsForpath.evalBoundaryDone();
        else
            return true;
    }
}
