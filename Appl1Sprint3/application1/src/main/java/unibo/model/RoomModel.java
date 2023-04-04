package unibo.model;

public class RoomModel {

    // per misurare la lunghezza dei lati in step del robot
    private int nStepLeft;
    private int nStepRight;
    private int nStepUp;
    private int nStepDown;

    public RoomModel(int nStepLeft, int nStepRight, int nStepUp, int nStepDown) {
        this.nStepLeft = nStepLeft;
        this.nStepRight = nStepRight;
        this.nStepUp = nStepUp;
        this.nStepDown = nStepDown;
    }

    public int getnStepLeft() { return nStepLeft; }
    public void setnStepLeft(int nStepLeft) { this.nStepLeft = nStepLeft; }

    public int getnStepRight() { return nStepRight; }
    public void setnStepRight(int nStepRight) { this.nStepRight = nStepRight; }

    public int getnStepUp() { return nStepUp; }
    public void setnStepUp(int nStepUp) { this.nStepUp = nStepUp; }

    public int getnStepDown() { return nStepDown; }
    public void setnStepDown(int nStepDown) { this.nStepDown = nStepDown; }
}
