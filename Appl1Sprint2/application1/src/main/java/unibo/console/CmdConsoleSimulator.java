package unibo.console;

import unibo.basicomm23.http.Appl1CoreSprint2;
import unibo.basicomm23.utils.CommUtils;

public class CmdConsoleSimulator {
    private Appl1CoreSprint2 appl;

    public CmdConsoleSimulator( Appl1CoreSprint2 appl ) {

        this.appl = appl;
    }

    public void activate( ) {
        try {
            cmdConsoleSim.start();
            appl.start();
        } catch ( Exception e ) {

        }
    }

    private Thread cmdConsoleSim = new Thread("cmdConsole") {
        public void run() {
            for (int i = 0; i < 5; i++) {
                CommUtils.delay(3000);
                CommUtils.outmagenta("cmdConsoleSimulator send STOP " + i);
                appl.stop();
                CommUtils.delay(1500);
                appl.resume();
            }
        }
    };
}
