package unibo.appl1.http;

import org.junit.Before;
import org.junit.Test;
import unibo.basicomm23.http.Appl1CoreSprint2;
import unibo.basicomm23.utils.CommUtils;
import unibo.observer.Appl1CoreTestStartStopObserver;

import java.util.Vector;

import static org.junit.Assert.fail;

public class TestAppl1HTTPSprint2 {
    private Appl1CoreSprint2 appl;
    private Appl1CoreTestStartStopObserver obsStartStop;
    // private Appl1ObserverForPath obsPath // già aggiunto nel POJO

    // utility methods
    @Before
    public void initSystemToTest( ) {
        try {
            appl = new Appl1CoreSprint2();
            obsStartStop = new Appl1CoreTestStartStopObserver();
            obsStartStop.init();
            // aggiungo osservatore per start/stop
            appl.addObserver(obsStartStop);
            CommUtils.outmagenta("initSystem for testing OK ");
        } catch ( Exception e ) {
            fail("initSystemToTest " + e.getMessage());
        }
    }

    protected void startTheApplication( ) {
        new Thread(){
            public void run(){
                try {
                    appl.start();
                } catch ( Exception e ) {
                    fail("startTheApplication " + e.getMessage());
                }
            }
        }.start();
    }

    protected void checkHistoryAfterStop( ) {
        CommUtils.delay(1500);

        Vector<String> h = obsStartStop.getMoveHistoryAfterStop();
        assert( h.elementAt(0).equals("robot-athomebegin") );
        assert( h.elementAt(1).equals("robot-moving") );
        if ( h.size() > 3 )
            assert( h.elementAt(2).equals("robot-stepdone") );
        // dopo il secondo item ci possono essere altre coppie robot-moving/robot-stepdone
        assert( h.elementAt(h.size() - 1).equals("robot-stopped") );
        CommUtils.outmagenta("checkHistoryAfterStop OK") ;
    }

    private void checkBoundaryDone( ) {
        assert( appl.evalBoundaryDone() );
        CommUtils.outblue("checkBoundaryDone");
    }

    // realizzazione dei test
    @Test
    public void testStartNoStop(){
        CommUtils.outmagenta("testStartNoStop");
        startTheApplication();
        checkBoundaryDone();  //wait
    }

    @Test
    public void testStop(){
        CommUtils.outmagenta("testStop");
        startTheApplication();
        for ( int i = 0; i < 4; i++ ) {
            CommUtils.delay(5000);
            appl.stop();
            checkHistoryAfterStop();
            CommUtils.delay(1500);
            appl.resume();
            checkResumed();
        }
        //while( appl.isRunning){  CommUtils.delay(1000);  }
        checkBoundaryDone();
    }

    protected void checkResumed() {
        obsStartStop.waitUntilResume();
    }

}
