package unibo.basicomm23.http;

import unibo.basicomm23.utils.CommUtils;
import unibo.common.CollisionException;
import unibo.common.IVrobotMoves;
import unibo.model.RoomModel;
import unibo.supports.VrobotHLMovesHTTPApache;

public class Appl1HTTPSprint1 {
    private IVrobotMoves vr  ; // per testare la posizione devo avere il virtual robot
    private RoomModel room;

    public Appl1HTTPSprint1(){
        configure();
    }

    protected void configure(){
        String URL = "localhost:8090/api/move";
        //URL potrebbe essere letto da un file di configurazione
        HTTPCommApache httpSupport = new HTTPCommApache(  URL );
        vr = new VrobotHLMovesHTTPApache( httpSupport );
    }

    public static void main( String[] args )  {
        Appl1HTTPSprint1 appl = new Appl1HTTPSprint1();
        try {
            appl.walkAtBoundary();  //ENTRY POINT
        } catch(Exception e) {
            CommUtils.outred("      walkAtBoundary | ERROR:" + e.getMessage());
        }
    }

    public void walkAtBoundary() throws Exception {
        for( int i = 0; i < 4; i++) {
            //walkAheadUntilCollision(i);
            walkByStepping(i);
            vr.turnLeft();
        }
    }

    private void walkAheadUntilCollision(int n)  throws Exception{
        try{
            vr.forward(2300 );
        } catch( CollisionException e) {
            return;
        }
        throw new Exception("no collision");
    }



    public void walkByStepping(int n) throws Exception {
        int step = room.getBoundarySteps(n);
        boolean goon = true;
        while( goon ) {
            goon =  vr.step(350);
            if( goon ) step++;
            CommUtils.delay(300); //to show the steps better
        }
    }

    public boolean checkRobotAtHome() {
        try {
            vr.turnRight();
            boolean res = vr.step(200);
            if (res) return false;
            vr.turnRight();
            res = vr.step(200);
            if (res) return false;
            vr.turnLeft();
            vr.turnLeft();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}