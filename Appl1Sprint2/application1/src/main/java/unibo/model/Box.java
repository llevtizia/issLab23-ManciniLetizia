package unibo.model;

import unibo.common.IBox;

public class Box implements IBox {

    private boolean isObstacle;
    private boolean isFree;
    private boolean isRobot;

    public Box( boolean isObstacle, boolean isFree, boolean isRobot ){
        this.isObstacle = isObstacle;
        this.isFree = isFree;
        this.isRobot = isRobot;
    }

    public Box() {
        this(false, false, false);
    }
    @Override
    public boolean isFree() {
        return this.isFree;
    }
    @Override
    public boolean isRobot() {
        return this.isRobot;
    }
    @Override
    public boolean isObstacle() {
        return this.isObstacle;
    }


    @Override
    public void setFree() {
        this.isFree = true;
    }
    @Override
    public void setRobot() {
        this.isRobot = true;
    }
    @Override
    public void setObstacle() {
        this.isObstacle = true;
    }
}
