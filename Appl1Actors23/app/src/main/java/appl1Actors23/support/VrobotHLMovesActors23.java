package appl1Actors23.support;

import appl1Actors23.VrobotMsgs;
import appl1Actors23.interfaces.IVrobotMovesAsynch;
import org.json.simple.JSONObject;
import unibo.actors23.Actor23Utils;
import unibo.actors23.ActorBasic23;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.ApplAbstractObserver;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.ws.WsConnection;

public class VrobotHLMovesActors23 extends ApplAbstractObserver implements IVrobotMovesAsynch {
    protected Interaction wsCommSupport;
    protected int elapsed       = 0;     //modified by update
    protected String moveResult = null;  //for observer part
    protected int threadCount = 1;
    protected ActorBasic23 appl;
    protected String toApplMsg   ;
    protected boolean tracing = false;

    public VrobotHLMovesActors23(Interaction wsCommSupport, ActorBasic23 appl) {
        this.wsCommSupport = wsCommSupport;
        this.appl          = appl;
        ((WsConnection) wsCommSupport).addObserver(this);
        toApplMsg = "msg(wenvinfo, dispatch, support, RECEIVER, CONTENT, 0)"
                .replace("RECEIVER",appl.getName());

        CommUtils.aboutThreads("     VrobotHLMovesActors23 |");
        CommUtils.outblue(
                "     VrobotHLMovesActors23 | CREATED in " + Thread.currentThread().getName());
    }

    public void setTrace(boolean v){
        tracing = v;
    }
    public Interaction getConn() {
        return wsCommSupport;
    }



    @Override
    public void turnLeft() throws Exception {
        sendSynchToWenv(VrobotMsgs.turnleftcmd);
    }

    @Override
    public void turnRight() throws Exception {
        sendSynchToWenv(VrobotMsgs.turnrightcmd);
    }

    @Override
    public void forward(int time) throws Exception {
        startTimer();
        wsCommSupport.forward(VrobotMsgs.forwardcmd.replace("TIME", "" + time));
    }

    @Override
    public void backward(int time) throws Exception {
        startTimer();
        wsCommSupport.forward(VrobotMsgs.forwardcmd.replace("TIME", "" + time));
    }

    @Override
    public void halt() throws Exception {
        CommUtils.outgreen("     VrobotHLMovesActors23 | halt");
        wsCommSupport.forward(VrobotMsgs.haltcmd);
        CommUtils.delay(150); //wait for halt completion since halt on ws does not send answer
        //CommUtils.outgreen("     VrobotHLMovesActors23 | halt done " + moveResult );
    }
// Observer part

    protected String sendSynchToWenv(String msg) throws Exception {
        moveResult = null;
        //Invio fire-and.forget e attendo modifica di  moveResult da update
        startTimer();
        //CommUtils.outgreen("     VrobotHLMovesActors23 | sendSynchToWenv " + msg);
        wsCommSupport.forward(msg);
        return waitForResult();
    }
    protected String waitForResult() throws Exception {
        synchronized (this) {
            while (moveResult == null) {
                wait();
            }
            if( tracing ) CommUtils.outblack("     VrobotHLMovesActors23 | sendSynchToWenv RESUMES moveResult=" + moveResult);
            return moveResult;
        }
    }
    protected void activateWaiting(String endmove){
        synchronized (this) {  //sblocca request sincrona per checkRobotAtHome
            moveResult = endmove;
            notifyAll();
        }
    }
/*
    protected void emitEvent(String evid, String payload){
        IApplMessage sonarevent = CommUtils.buildEvent(
                "vrhlsupport",evid, payload  );
        CommUtils.outred( "VrobotHLMovesActors23 | emitEvent " + sonarevent);
        appl.g.emit(sonarevent);  //tranne a sè stesso

    }*/
    @Override
    public void update(String info) {
         try {
            elapsed = getDuration();
            if( tracing ) CommUtils.outblack(
                    "     VrobotHLMovesActors23 | update:" + info + " elapsed=" + elapsed +
                            " " + Thread.currentThread().getName());

             JSONObject jsonObj = CommUtils.parseForJson(info);
            if (jsonObj == null) {
                CommUtils.outred("     VrobotHLMovesActors23 | update ERROR Json:" + info);
                return;
            }
            if (info.contains("_notallowed")) {
                CommUtils.outred("     VrobotHLMovesActors23 | update WARNING!!! _notallowed unexpected in " + info);
                return;
            }
            if (jsonObj.get("sonarName") != null) {
                long d = (long) jsonObj.get("distance") ;
                IApplMessage sonarEvent = CommUtils.buildEvent(
                        "vrhlsprt","sonardata","'"+"sonar(" +d + " )"+"'");
                //Imviare un msg ad appl perchè generi un evento a favore di SonarObserverActor23
                //è ridondante
                /*
                String wenvInfo = toApplMsg.replace("wenvinfo","sonardata")
                        .replace("CONTENT",  "'"+"sonar(" +d + " )"+"'");
                IApplMessage msg = new ApplMessage(wenvInfo);
                Actor23Utils.sendMsg(msg,appl);
                Invio l'evento a tutti gli attori locali -> gestito da SonarObserverActor23
                 */
                Actor23Utils.emitLocalEvent( sonarEvent,appl );
                //appl.emitLocalStreamEvent(sonarEvent);  //appl funge da emitter
                return;
            }
            if (jsonObj.get("collision") != null) {
                /*
                String wenvInfo = toApplMsg.replace("CONTENT",
                        "collision(" + elapsed + " )");
                IApplMessage msg = new ApplMessage(wenvInfo);
                Actor23Utils.sendMsg(msg,appl);  //non viene gestito
                 */
                return;
            }
            if (jsonObj.get("endmove") != null) {
                //{"endmove":"true/false ","move":"..."}
                boolean endmove = jsonObj.get("endmove").toString().contains("true");
                String move     = jsonObj.get("move").toString();
                //CommUtils.outred("     VrobotHLMovesActors23 | update move=" + move);
                //move moveForward-collision or moveBackward-collision
                if (endmove) {
                    //msgQueue.put("stepdone(" + elapsed + ")");
                    if( ( move.equals("turnLeft") || move.equals("turnRight")) ){
                        activateWaiting("" + endmove);
                        return;
                    }
                    String wenvInfo = toApplMsg.replace("wenvinfo","stepdone") //"stepdone"
                            .replace("CONTENT", "stepdone(" + elapsed + ")");
                    IApplMessage msg = new ApplMessage(wenvInfo);
                    if( ! doingStep )  Actor23Utils.sendMsg(msg,appl);
                    else{
                        activateWaiting("" + endmove);
                    }
                } else if (move.contains("interrupted")) {
                    String wenvInfo = toApplMsg.replace("wenvinfo","stepfailed") //stepfailed
                            .replace("CONTENT", "stepfailed(" + elapsed + ", interrupt )");
                    IApplMessage msg = new ApplMessage(wenvInfo);
                    Actor23Utils.sendMsg(msg, appl);
                 }else if (move.contains("collision")) {
                    String wenvInfo = toApplMsg.replace("wenvinfo","stepfailed") //stepfailed
                            .replace("CONTENT","stepfailed(" + elapsed + ", collision )");
                    IApplMessage msg = new ApplMessage(wenvInfo);
                    if( ! doingStep ) Actor23Utils.sendMsg(msg, appl);
                    activateWaiting("false"  );
                    //CommUtils.outred("     VrobotHLMovesActors23 | update END move=" + move);
                }
                return;
            }
        } catch (Exception e) {
            CommUtils.outred("     VrobotHLMovesActors23 | update ERROR:" + e.getMessage());
        }
    }


    //  Timer part
    private Long timeStart = 0L;

    public void startTimer() {
        elapsed = 0;
        timeStart = System.currentTimeMillis();
    }

    public int getDuration() {
        long duration = (System.currentTimeMillis() - timeStart);
        return (int) duration;
    }


     boolean doingStep = false;
    @Override
    public boolean step(int time) throws Exception {
        doingStep = true;
        if( tracing ) CommUtils.outblack("     VrobotHLMovesActors23 | step time=" + time);
        String cmd    = VrobotMsgs.forwardcmd.replace("TIME", "" + time);
        String result = sendSynchToWenv(cmd);
        if( tracing ) CommUtils.outblack("     VrobotHLMovesActors23 | step result="+result);
        //result=true elapsed=... OPPURE collision elapsed=...
        doingStep = false;
        return result.contains("true");
    }

    @Override
    public void stepAsynch(int time) {
        try {
            startTimer(); //per getDuration()
            //if( tracing ) CommUtils.outblack("     VrobotHLMovesActors23 | stepAsynch" );
            wsCommSupport.forward(VrobotMsgs.forwardcmd.replace("TIME", "" + time));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

