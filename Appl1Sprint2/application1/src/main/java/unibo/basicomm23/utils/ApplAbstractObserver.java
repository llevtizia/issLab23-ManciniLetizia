package unibo.basicomm23.utils;

import unibo.basicomm23.interfaces.IObserver;

import java.util.Observable;

public abstract class ApplAbstractObserver implements IObserver {
    @Override
    public void update(Observable o, Object arg) {

        this.update(arg.toString());
    }

    @Override
    public abstract void update(String string);
}
