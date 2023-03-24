package unibo.basicomm23.http;

import unibo.basicomm23.utils.CommUtils;
import unibo.console.CmdConsoleSimulator;
import unibo.console.gui.Appl1HttpSprint2CmdConsole;

public class Appl1HTTPSprint2 {
    private Appl1CoreSprint2 appl1Core;
    private CmdConsoleSimulator cmdConsole;

    public Appl1HTTPSprint2() throws Exception{
        configureTheSystem();
    }

    private void configureTheSystem() throws Exception{
        appl1Core  = new Appl1CoreSprint2();
        cmdConsole = new CmdConsoleSimulator(appl1Core);
    }

    public void doJob() throws Exception{
        CommUtils.outmagenta("Activate Appl1HttpSprint3CmdConsole ");
        //new Appl1HttpSprint2CmdConsole();
        cmdConsole.activate();
    }

    public static void main( String[] args ) throws Exception {
        CommUtils.aboutThreads("Before start - ");
        Appl1HTTPSprint2 appl = new Appl1HTTPSprint2();
        appl.doJob();
        CommUtils.aboutThreads("At end - ");
    }
}
