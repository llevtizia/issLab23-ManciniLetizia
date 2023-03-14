package unibo.basicomm23.http;

import unibo.basicomm23.utils.CommUtils;
import unibo.common.IAppl1Core;
import unibo.common.IVrobotMoves;
import unibo.model.RoomModel;
import unibo.supports.VrobotHLMovesHTTPApache;

public class Appl1Core implements IAppl1Core {
    protected boolean started    = false;
    protected boolean stopped    = false;
    protected IVrobotMoves vr ;
    private RoomModel room;

    public Appl1Core(){
        stopped = false;
        configure();
    }

    protected void configure(){
        String URL = "localhost:8090/api/move";
        //URL potrebbe essere letto da un file di configurazione
        HTTPCommApache httpSupport = new HTTPCommApache(  URL );
        vr = new VrobotHLMovesHTTPApache( httpSupport );
    }

    @Override
    public void start() {
        try {
            if ( ! started ) {
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
            for( int i=1; i<=4;i++) {
                walkBySteppingWithStop(i);
                vr.turnLeft();
            }
        } catch (Exception e) {
            CommUtils.outred("      walkAtBoundary | ERROR:" + e.getMessage());
        }
    }

    public void walkBySteppingWithStop(int n) throws Exception {
        boolean stepOk = true;
        while( stepOk  ) {
            stepOk =  vr.step(350);
            CommUtils.delay(300); //to show the steps better
            if ( stopped ) {
                waitResume();
            }
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
}
