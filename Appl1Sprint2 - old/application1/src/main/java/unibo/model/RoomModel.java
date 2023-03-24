package unibo.model;

public class RoomModel {

    // per misurare la lunghezza dei lati in step del robot
    private int[] boundarySteps = {0, 0, 0, 0};

    public RoomModel() {

    }

    public int getBoundarySteps(int i) {
        return boundarySteps[i];
    }

    public void setBoundarySteps(int i , int step) {
        this.boundarySteps[i] = step;
    }
}
