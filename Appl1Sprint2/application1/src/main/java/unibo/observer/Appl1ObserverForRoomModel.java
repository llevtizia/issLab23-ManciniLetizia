package unibo.observer;

import unibo.basicomm23.utils.ApplAbstractObserver;
import unibo.basicomm23.utils.CommUtils;
import unibo.common.IRobotState;
import unibo.model.RobotState;

import java.util.HashSet;
import java.util.Set;

public class Appl1ObserverForRoomModel extends ApplAbstractObserver {
    private Set<String> moveCmds = new HashSet<String>();
    private RobotState robotState;

    public Appl1ObserverForRoomModel() {
        moveCmds.add("robot-athomebegin");
        moveCmds.add("robot-stepdone");
        moveCmds.add("robot-turnLeft");
        moveCmds.add("robot-athomeend");
    }

    public void update(String msg) {
        if (msg.contains("robot-athomebegin"))
            robotState = new RobotState(0, 0, IRobotState.Direction.DOWN);
        else if (msg.contains("robot-stepdone"))
            robotState.forward();
        else if (msg.contains("robot-turnLeft"))
            robotState.turnLeft();
        else if (msg.contains("robot-athomeend")) {
            String path = robotState.toString();
            CommUtils.outblue(path);
        }
    }

}
