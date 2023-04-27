package unibo.supports;

import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.utils.CommUtils;
import unibo.common.CollisionException;
import unibo.common.IVrobotMoves;
import unibo.common.VrobotMsgs;

public class VrobotHLMovesHTTP implements IVrobotMoves {
    private Interaction httpSupport;

    public VrobotHLMovesHTTP(Interaction httpSupport) {
        this.httpSupport = httpSupport;
    }

    @Override
    public boolean step(int time) throws Exception {
        String cmd        = VrobotMsgs.forwardcmd.replace("TIME", ""+time);
        String result = httpSupport.request(   cmd  );
        //{"endmove":true,"move":"moveForward"}  OPPURE:
        //{"endmove":"false","move":"moveForward-collision"}
        boolean collision = result.toString().contains("collision");
        return ! collision;
    }

    @Override
    public void turnLeft() throws Exception {
        httpSupport.request(  VrobotMsgs.turnleftcmd  );
    }

    @Override
    public void turnRight() throws Exception {
        httpSupport.request(  VrobotMsgs.turnrightcmd  );
    }

    @Override
    public void forward(int time) throws Exception {
        //CommUtils.outyellow("VrobotHLMovesHTTP | forward time=" + time);
        long timeStart = System.currentTimeMillis();
        String result = httpSupport.request(  VrobotMsgs.forwardcmd.replace("TIME",""+time)  );
        long timeend = System.currentTimeMillis();
        long elapsed = timeend - timeStart;
        CommUtils.outyellow("VrobotHLMovesHTTP | forward result=" + result + " elapsed=" + elapsed);
        if( result.contains("collision")  ){
            throw new CollisionException(  ) ;
        }
    }

    @Override
    public void backward(int time) throws Exception {
        long timeStart = System.currentTimeMillis();
        String result = httpSupport.request(  VrobotMsgs.backwardcmd.replace("TIME",""+time)  );
        long timeend = System.currentTimeMillis();
        long elapsed = timeend - timeStart;
        CommUtils.outyellow("VrobotHLMovesHTTP | backward result=" + result + " elapsed=" + elapsed);
        if( result.contains("collision")  ){
            throw new CollisionException(  ) ;
        }
    }

    @Override
    public void halt() throws Exception {
        String result = httpSupport.request(  VrobotMsgs.haltcmd  );
        CommUtils.outyellow("VrobotHLMovesHTTP | halt result=" + result);
    }
}
