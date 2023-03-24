package unibo.appl1.http;

import org.junit.Before;
import org.junit.Test;
import unibo.basicomm23.http.Appl1Core;
import unibo.basicomm23.utils.CommUtils;
import unibo.observer.Appl1CoreTestStartStopObserver;

import java.util.Vector;

import static org.junit.Assert.fail;

public class TestAppl1HTTPSprint2 {
    private Appl1Core appl;
    private Appl1CoreTestStartStopObserver obsStartStop;
    //private Appl1ObserverForpath obsPath; //Già creato nel POJO

    // metodi di utilità
    @Before
    public void initSystemToTest() {
        try {
            appl         = new Appl1Core(); // il robot viene verificato in HOME
            obsStartStop = new Appl1CoreTestStartStopObserver();
            // obsStartStop.init();
            // aggiungo osservatore per start/stop
            appl.addObserver(obsStartStop);
            CommUtils.outmagenta("initSystem for testing done ");
        } catch( Exception e){
            fail("initSystemToTest " +e.getMessage());
        }
    }

    protected void startTheApplication( ) {
        new Thread() {
            public void run() {
                try {
                    appl.start();
                } catch ( Exception e ) {
                    fail( "startTheApplication " + e.getMessage() );
                }
            }
        }.start();
    }

    protected void checkHistoryAfterStop( ) {
        //CommUtils.delay(1500); //Per permettere elaborazione di ultime stringhe dall'observer
        Vector<String> h = obsStartStop.getMoveHistoryAfterStop();
        //h.forEach( item -> CommUtils.outyellow(item) );

        assert ( h.elementAt(0).equals("robot-athomebegin") );
        assert ( h.elementAt(1).equals("robot-moving") );

        if ( h.size() > 3 )
            assert( h.elementAt(2).equals("robot-stepdone"));

        //Dopo il secondo item ci possono essere altre coppie robot-moving / robot-stepdone
        assert ( h.elementAt(h.size()-1 ).equals("robot-stopped") );

        CommUtils.outmagenta("checkHistoryAfterStop done") ;
    }

    private void checkBoundaryDone( ) {
        boolean b = appl.evalBoundaryDone(); //bloccante
        CommUtils.outblue("checkBoundaryDone: " + b);
        assert( b );

    }

    protected void checkResumed(){
        obsStartStop.waitUntilResume();
    }


    // Test
    @Test
    public void testStartNoStop( ) {
        CommUtils.outmagenta("testStartNoStop");
        startTheApplication();
        checkBoundaryDone(); // wait
    }

    @Test
    public void testStop( ) {
        startTheApplication();

        for ( int i = 1; i <= 4; i++ ) {
            CommUtils.delay(3000);
            appl.stop();
            checkHistoryAfterStop();
            CommUtils.delay(3000);
            // test resume
            appl.resume();
            checkResumed();
        }
        appl.evalBoundaryDone();
    }

}
