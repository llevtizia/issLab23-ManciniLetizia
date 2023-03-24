package unibo.basicomm23.http;

import org.json.simple.JSONObject;
import unibo.basicomm23.interfaces.IObserver;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.interfaces.IAppl1Core;
import unibo.common.IVrobotMoves;
import unibo.model.RoomModel;
import unibo.observer.Appl1ObserverForPath;
import unibo.supports.VrobotHLMovesHTTPApache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Appl1CoreSprint2 extends java.util.Observable  implements IAppl1Core  {
    protected boolean started    = false;
    protected boolean stopped    = false;
    private boolean isRunning    = false;

    protected IVrobotMoves vr ;

    private Appl1ObserverForPath obsForpath;
    // private Appl1ObserverForRoomModel obsForroom;

    protected String virtualRobotIp = "";
    protected int stepTime = 350;

    public Appl1CoreSprint2() {

    }

    @Override
    public void addObserver(IObserver o) {
        super.addObserver(o);
    }

    protected void configure( ) throws Exception {
        stopped = false;
        readConfigFile();
    }

    protected void readConfigFile() throws Exception {
        File configFile = new File("D:/Documenti/issLab23-ManciniLetizia/Appl1Sprint2/application1/sprint2config.json");

        BufferedReader br = new BufferedReader(new FileReader(configFile));
        String currentLine = br.readLine();
        CommUtils.outblue("Appl1Core currentLine: " + currentLine);

        JSONObject configObj = CommUtils.parseForJson(currentLine);

        virtualRobotIp = configObj.get("virtualrobotip").toString();
        String virtualRobotConn = configObj.get("virtualrobotconn").toString();
        String pathObs = configObj.get("pathobs").toString();
        String robotStateObs = configObj.get("robotstateobs").toString();
        stepTime = Integer.parseInt(configObj.get("steptime").toString());

        CommUtils.outblue("Appl1Core virtualRobotConn= " + virtualRobotConn + " virtualRobotIp= " + virtualRobotIp
                + " pathObs= " + pathObs + " robotStateObs= " + robotStateObs + " stepTime= " + stepTime );

        if ( virtualRobotConn.equals("http") )
            configureUsingHttp(virtualRobotIp);

        // observer for path
        if ( pathObs.equals("true") ) {
            if ( obsForpath == null ) {
                obsForpath = new Appl1ObserverForPath();
                addObserver(obsForpath);
            }
        } else obsForpath = null;
        // observer for state
        /*
        if ( robotStateObs.equals("true") ) {
            if ( obsForroom == null ) {
                obsForroom = new Appl1ObserverForRoomModel();
                addObserver(obsForrom);
            }
        } else obsForroom = null

         */
    }

    private void configureUsingHttp(String virtualRobotIp) {
        String URL = virtualRobotIp + ":8090/api/move";
        HTTPCommApache httpSupport = new HTTPCommApache( URL );
        vr = new VrobotHLMovesHTTPApache( httpSupport );
    }

    private void walkAtBoundary() {
        try {
            robotMustBeAtHome("START"); //SE PROBLEMI: updateObservers("robot-athomebegin");
            updateObservers("robot-athomebegin");
            isRunning  = true;
            for( int i=1; i<=4; i++ ) {
                walkBySteppingWithStop(i);
                vr.turnLeft();
                //CommUtils.delay(300);
                updateObservers("robot-turnLeft");
            }
            isRunning  = false;
            updateObservers("robot-athomeend");
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
            stepOk =  vr.step(stepTime);

            if ( ! stepOk )
                notifyObservers("robot-collision");
            else
                updateObservers("robot-stepdone");
            CommUtils.delay(300); //to show the steps better
            CommUtils.outgreen("Appl1CoreSprint2 stopped= " + stopped);
        }
    }

    public synchronized void waitResume(){
        while ( stopped ){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                CommUtils.outred("      waitResume | ERROR: " + e.getMessage());
            }
        }
        updateObservers("robot-resumed");
        CommUtils.outyellow(" RESUMED ");
    }

    // obs methods
    public String getCurrentPath() {
        if ( obsForpath != null )
            return obsForpath.getCurrentPath();
        else
            return "no current path yet";
    }

    public String getPath( ) {
        if ( obsForpath != null )
            return obsForpath.getPath();
        else
            return "no path yet";
    }

    public boolean evalBoundaryDone() {
        if ( obsForpath != null )
            return obsForpath.evalBoundaryDone();
        else
            return true;
    }

    public boolean isRunning(){
        return  isRunning;
    }


    @Override
    public void start() throws Exception {
        if ( ! started ) {
            started = true;
            configure();
            walkAtBoundary();
        } else
            CommUtils.outred("Appl1CoreSprint2 already started");
    }

    @Override
    public  void stop(  ) {
        try {
            stopped = true;
            CommUtils.outblue("Appl1CoreSprint2 stopped");
            notifyAll();
        } catch (Exception e) {
            CommUtils.outred("      stop | ERROR: " + e.getMessage());
        }
    }

    @Override
    public synchronized void resume(  ){
        stopped = false;
        notifyAll();  //riattiva waitResume
    }



    // observer
    private void updateObservers(String msg){
        setChanged(); // setto i cambiamenti
        notifyObservers(msg);
    }


    private void robotMustBeAtHome(String msg) throws Exception{
        boolean b = checkRobotAtHome();
        CommUtils.outblue("robotMustBeAtHome: " + msg + " " + b);
        if ( msg.equals("START") ) {
            /*
            while(  ! b ){
                CommUtils.outblue("Please put robot at HOME");
                CommUtils.delay(3000);
                b = checkRobotAtHome( );
            }*/
            if ( !b )
                throw new Exception("Robot NOT in HOME");
            else
                updateObservers("robot-athomebegin");
        }

        if ( msg.equals("END") ) {
            if ( b )
                updateObservers("robot-athomeend");
            else
                updateObservers("robot-NOTathomeend");
        }
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
}
