package unibo.basicomm23.http;

import unibo.basicomm23.utils.CommUtils;
import unibo.common.IAppl1Core;
import unibo.common.IVrobotMoves;
import unibo.supports.VrobotHLMovesHTTPApache;

public class Appl1CoreSprint2 implements IAppl1Core {

    protected boolean started = false;
    protected boolean stopped = false;

    // private Appl1ObserverForPath obsForPath;
    protected IVrobotMoves vr;

    public Appl1CoreSprint2( ) {
        stopped = false;
        configure();
    }

    private void configure( ) {
        String URL = "localhost:8090/api/move";
        //String URL = virtualRobotIp+":8090/api/move"; con ip letto da file
        HTTPCommApache httpSupport = new HTTPCommApache(  URL );
        vr         = new VrobotHLMovesHTTPApache( httpSupport );
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
        for ( int i = 0; i < 4; i++ ) {
            walkBySteppingWithStop(i);
            vr.turnLeft();
        }
    }

    private void walkBySteppingWithStop(int i) throws Exception {
        boolean stepOk = true;
        while ( stepOk ) {
            stepOk = vr.step(350);
            CommUtils.delay(300); // to show the step
            if ( stopped )
                waitResume();
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
}
