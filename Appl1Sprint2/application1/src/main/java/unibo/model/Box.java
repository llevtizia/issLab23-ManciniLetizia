package unibo.model;

import unibo.common.IBox;

public class Box implements IBox {
    @Override
    public boolean isFree() {
        return false;
    }

    @Override
    public boolean isRobot() {
        return false;
    }

    @Override
    public boolean isObstacle() {
        return false;
    }

    @Override
    public void setFree() {

    }

    @Override
    public void setRobot() {

    }

    @Override
    public void setObstacle() {

    }
}
