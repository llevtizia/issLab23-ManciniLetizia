package unibo.basicomm23.interfaces;

public interface IObserver extends java.util.Observer {
    void update(String var1);
    //void update(Observable o, Object arg);
    // inherited by Observer --> observable = chi ha emesso l'informazione, object = l'informazione emessa
}