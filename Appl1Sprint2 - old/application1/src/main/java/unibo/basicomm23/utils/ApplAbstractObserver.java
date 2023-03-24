package unibo.basicomm23.utils;

import unibo.basicomm23.interfaces.IObserver;
import java.util.Observable;

/* scheletro di un observer che lascia non specificato il metodo public void update(String msg) */

public abstract class ApplAbstractObserver implements IObserver {

    public void update(Observable o, Object arg) { // trasformo ogni informazione in stringa
        this.update(arg.toString());
    }

    public abstract void update(String var1); // tutti gli observer avranno una stringa da interpretare
}