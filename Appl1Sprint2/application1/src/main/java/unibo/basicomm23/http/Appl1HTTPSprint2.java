package unibo.basicomm23.http;

import unibo.basicomm23.utils.CommUtils;
import unibo.console.CmdConsoleSimulator;

public class Appl1HTTPSprint2 {
    private Appl1CoreSprint2 appl1Core;
    private CmdConsoleSimulator cmdConsole;

    public Appl1HTTPSprint2(){

        configureTheSystem();
    }

    private void configureTheSystem(){
        appl1Core  = new Appl1CoreSprint2();
        cmdConsole = new CmdConsoleSimulator(appl1Core);
    }
    public void doJob() throws Exception{
        cmdConsole.activate(  );   //invoca start/stop/resume
    }

    public static void main( String[] args ) throws Exception {
        CommUtils.aboutThreads("Before start - ");
        Appl1HTTPSprint2 appl = new Appl1HTTPSprint2();
        appl.doJob();
        CommUtils.aboutThreads("At end - ");
    }
}
