package unibo.console;

import unibo.basicomm23.http.Appl1Core;
import unibo.basicomm23.utils.CommUtils;

public class CmdConsoleSimulator {
    private Appl1Core appl;

    public CmdConsoleSimulator( Appl1Core appl ){
        this.appl = appl;
    }

    public void activate(   )   {
        try {
            cmdConsoleSimul.start();
            appl.start();
        } catch (Exception e) {
            CommUtils.outred("      activate | ERROR: " + e.getMessage());
        }
    }
    private  Thread cmdConsoleSimul = new Thread("cmdConsole") {
        public void run() {
            for (int i = 1; i <= 5; i++) {
                CommUtils.delay(3000);
                CommUtils.outmagenta("cmdConsoleSimul send STOP " + i);
                appl.stop();
                CommUtils.delay(3000);
                appl.resume();
            }
        }
    };
}