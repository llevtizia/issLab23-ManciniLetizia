package unibo.basicomm23.interfaces;

import java.util.Observable;

public interface IObserver extends java.util.Observer {

    @Override
    void update(Observable o, Object arg); // inherited by Observer
    void update(String string);
}
