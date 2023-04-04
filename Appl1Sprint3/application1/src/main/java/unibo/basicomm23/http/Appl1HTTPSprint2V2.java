package unibo.basicomm23.http;

import unibo.basicomm23.utils.CommUtils;
import unibo.console.CmdConsoleSimulator;
import unibo.console.gui.Appl1HttpSprint2CmdConsole;

public class Appl1HTTPSprint2V2 {
    private Appl1CoreSprint2 appl1Core;

    public Appl1HTTPSprint2V2(){
        configureTheSystem();
    }

    private void configureTheSystem(){
        appl1Core  = new Appl1CoreSprint2();
    }

    public void doJob() throws Exception{
        CommUtils.outmagenta("Activate Appl1HttpSprint3CmdConsole ");
        new Appl1HttpSprint2CmdConsole();
    }

    public static void main( String[] args ) throws Exception {
        CommUtils.aboutThreads("Before start - ");
        Appl1HTTPSprint2V2 appl = new Appl1HTTPSprint2V2();
        appl.doJob();
        CommUtils.aboutThreads("At end - ");
    }
}
