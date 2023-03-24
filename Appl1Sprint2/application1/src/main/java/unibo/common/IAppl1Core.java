package unibo.common;

import unibo.basicomm23.interfaces.IObserver;

public interface IAppl1Core {
    // SPRINT 2
    public void start() throws Exception;
    public void stop();
    public void resume();

    // SPRINT 2 REFACTORED
    public boolean isRunning();
    public String getCurrentPath();
    public void addObserver(IObserver o);

}
