package unibo.basicomm23.interfaces;

public interface IAppl1Core {
    public void start() throws Exception;
    public void stop();
    public void resume();

    // sprint 2
    public boolean isRunning();
    public String getCurrentPath();
    public void addObserver(IObserver o);
}
