package unibo.actors23.fsm;
import unibo.actors23.Actor23Utils;
import unibo.actors23.ActorBasic23;
import unibo.actors23.ActorContext23;
import unibo.actors23.annotations.State;
import unibo.actors23.annotations.Transition;
import unibo.actors23.annotations.TransitionGuard;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.utils.CommUtils;
import java.lang.reflect.Method;
import java.util.*;

public   class ActorBasicFsm23 extends ActorBasic23 {
    public static final String emptyMoveId   = "emptyMove";
    public static final String startSysCmdId = "startcmd";

    public static final IApplMessage emptyMoveCmd(String sender, String receiver)   {
        return CommUtils.buildDispatch(sender, emptyMoveId, "do", receiver );
    }

    protected String initialState = null;
    protected HashMap<String, StateActionFun> stateMap = new HashMap<String,StateActionFun>();
    protected HashMap<String,String> nextMsgMap     = new HashMap<String,String>();
    protected Vector<IApplMessage> OldMsgQueue      = new Vector< IApplMessage>();
    protected Vector<Pair<String, String>> transTab     = new Vector< Pair<String, String> >();
    protected Vector<Pair<String, Boolean> >interruptTab = new Vector< Pair<String, Boolean> >();
    protected String curState = "";
    protected IApplMessage currentMsg = null;

    protected String stateWithInterrupt = "";
    protected Vector< Pair<String, String> >  memoTransTab     = null;
    protected Vector< Pair<String, Boolean> > memoInterruptTab = null;


    public ActorBasicFsm23(String name, ActorContext23 ctx) {
        super(name, ctx);
        declareTheStates( );
           addExpectedMsg(curState,  startSysCmdId );
        autostart = true;
    }
    
    protected void elabStateMethod(Method m, String stateName, Method[] allguards) { //Guards by Lenzi
        if( ! m.getName().equals(stateName)) {
            CommUtils.outred(getName() + " | ActorBasicFsm23  Method name must be the same as state name" );
        }
        Vector<String> nextStates = new Vector<String>();
        Vector<String> msgIds     = new Vector<String>();
        //Vector<Class> guards      = new Vector<Class>();
        Vector<Boolean> interrupts= new Vector<Boolean>();
        Vector<Method> guards     = new Vector<Method>(); //From Lenzi

        Transition[] ta        = m.getAnnotationsByType(Transition.class);

        for ( Transition t : ta ) {
            CommUtils.outgray("Transition simple: "+ t.msgId() + " -> " + t.state() + " guard=" + t.guard());
            nextStates.add(t.state());
            msgIds.add(t.msgId());
            interrupts.add(t.interrupt());

            String guardName = t.guard();
            if ( guardName.isEmpty() ) guards.add(null);
            else {
                Method guard = getGuard(allguards, guardName);
                CommUtils.outmagenta(getName() + " | ActorBasicFsm23 guard=" + guard);
                if (guard == null) {
                    throw new IllegalArgumentException("Non existent guard:" + guardName);
                }
                guards.add( guard );
            }
        }
        doDeclareState(this, m,stateName,nextStates,msgIds,guards,interrupts );

    }

    protected Method getGuard(Method[] guards, String guardName) { //From Lenzi
        return Arrays.stream(guards)
                .filter(m -> m.getName().equals(guardName))
                .findFirst()
                .orElse(null);
    }

    protected boolean guardForTransition(String stateName, String transName ) {
        return false;
    }
    protected void stateTransition(String stateName, IApplMessage msg ) {
        curState   = stateName;
        currentMsg = msg;
        transTab.removeAllElements();
        interruptTab.removeAllElements();
        stateWithInterrupt = null;
        StateActionFun a = stateMap.get(curState);
        if( a != null ) {
            a.run( msg );
        }
        else CommUtils.outred(getName() + " | ActorBasicFsm23 TERMINATED since no body");
    }

    protected void addTransition(String state, String msgId, boolean withInterrupt) {
        if(Actor23Utils.trace)  CommUtils.outgray( getName() + " ActorBasicFsm23 | in " + curState + ": transition to " + state + " for " +  msgId );
        transTab.add(     new Pair<>(state, msgId) );
        interruptTab.add( new Pair<>(state, withInterrupt) );
    }
    protected void addExpectedMsg(String state, String msgId) {
        nextMsgMap.put(msgId, state);
    }
    protected void clearExpectedMsgs( ) {
        nextMsgMap.clear();
    }
    protected String checkIfExpected(IApplMessage msg) {
        return nextMsgMap.get( msg.msgId() );
    }

    protected void nextState(String currentState ) {
        clearExpectedMsgs();
        Iterator< Pair<String, String> >  iterOnTrans  = transTab.iterator();
        Iterator< Pair<String, Boolean> > iterOnIntrpt = interruptTab.iterator();
        boolean hasInterruptTransition = false;

        while( iterOnTrans.hasNext() ) {
            Pair<String, String> curTrans   = iterOnTrans.next();
            Pair<String, Boolean> curIntrpt = iterOnIntrpt.next();

            String nextState         		= curTrans.getFirst();
            String msgId            		= curTrans.getSecond();
            boolean withInterrupt    		= curIntrpt.getSecond();

            if( msgId.equals( emptyMoveId) ) {
                stateTransition(nextState,  emptyMoveCmd(getName(),getName() ));
                return;
            }
            if( withInterrupt && ! hasInterruptTransition && ! curState.equals("resuming")  ) { //
                memoTheState(currentState);
                hasInterruptTransition = true;
            }

            IApplMessage oldMsg = searchInOldMsgQueue( msgId );
            if( oldMsg != null ) {
                stateTransition(nextState,oldMsg);
                break;
            }
            else addExpectedMsg(nextState, msgId);
        }
    }

    protected void doDeclareState( ActorBasicFsm23 castMyself,	//Since Lenzi guards as methods
                                   Method curMethod, String stateName, Vector<String> nextStates,
                                   Vector<String> msgIds, Vector<Method> guards, Vector<Boolean> interrupts) {
        declareState( stateName, new StateActionFun() {
            @Override
            public void run( IApplMessage msg ) {
                try {
                    //Esegue il body  					
                    curMethod.invoke(  castMyself, msg   );  //I metodi hanno this come arg implicito

                    boolean stateWithInterrupt=false;

                    for( int j=0; j<nextStates.size();j++ ) {
                        Method  g   =  guards.elementAt(j);
                        boolean hasInterrupt = interrupts.elementAt(j);
                        if( hasInterrupt  ) {
                            if( ! stateWithInterrupt  ) stateWithInterrupt = true;
                            else {CommUtils.outred(stateName + ": multiple interrupt not allowed");}
                        }
                        //Object og = g.newInstance();  //Guard as class: deprecated
                        //Boolean result = (Boolean) g.getMethod("eval").invoke( og );
                        Boolean result = (g != null) ? (Boolean) g.invoke(castMyself) : true;

                        if( result ) {
                            //CommUtils.outgray("g:"+ g + " result=" + result.getClass().getName(), ColorsOut.GREEN);
                            addTransition( nextStates.elementAt(j), msgIds.elementAt(j), stateWithInterrupt );
                        }
                    }
                    nextState(stateName );
                } catch ( Exception e) {
                    CommUtils.outred("wrong execution for:"+ stateName + " - " + e.getMessage());
                }
            }
        });//declareState		
    }

    protected void declareTheStates( ) {
        try {
            Method[] methods  = this.getClass().getDeclaredMethods( );
            CommUtils.outgray("declareTheStates: "+ methods.length   );

            Method[] guards = Arrays.stream(methods)
                    .filter(m -> m.isAnnotationPresent(TransitionGuard.class))
                    .toArray(Method[]::new);


            List<Method> annotatedMethods = new ArrayList<>( methods.length  ); //  /2 ???

            for (Method method : methods) {
                method.setAccessible(true);	//anche per ogni guardia-Lenzi
                if (method.isAnnotationPresent(State.class)) annotatedMethods.add(method);
                //if( methods [i].isAnnotationPresent(State.class)) elabAnnotatedMethod(methods [i]);
            }
            for (Method method : annotatedMethods) elabAnnotatedMethod(method, guards);

        } catch (Exception e) {
            CommUtils.outred("readAnnots ERROR:" + e.getMessage() )   ;
        }
    }

    protected void memoTheState(String currentState) {
        CommUtils.outgray(getName() + " | ActorBasicFsm23 memoTheState " +  currentState );
        stateWithInterrupt = currentState;
        memoTransTab     = transTabCopy(transTab);
        memoInterruptTab = interruptTabCopy(interruptTab);
    }
    protected void elabAnnotatedMethod(Method m, Method[] guards) { //guards by Lenzi
        String functor =  m.getName();
        Class<?>[] p   =  m.getParameterTypes();
//    		  CommUtils.outgray("state ANNOT functor="+ functor + " p.length=" + p.length , ColorsOut.CYAN);
        if( p.length==0 || p.length>1 ||
                ! p[0].getCanonicalName().equals("unibo.basicomm23.interfaces.IApplMessage") ) {
            CommUtils.outred("wrong arguments for state:"+ functor);
        }else {
            State stateAnnot=m.getAnnotation(State.class);
            if( stateAnnot.initial() )  setTheInitialState(stateAnnot.name());
            elabStateMethod( m, stateAnnot.name(), guards);
        }
    }
    
    
    protected void setTheInitialState( String stateName ) {
        if( initialState == null  ) {
            initialState= stateName;
            declareAsInitialState( initialState );
        } else CommUtils.outred("Multiple intial states not allowed" );
    }
    protected void declareAsInitialState( String stateName ) {
        if(Actor23Utils.trace)  CommUtils.outgray( getName() + " ActorBasicFsm23 | declareAsInitialState " + stateName );
        curState = stateName;
    };
    protected Vector<Pair<String,String>> transTabCopy( Vector<Pair<String,String>> tab){
        Vector< Pair<String, String> > copied = new Vector<Pair<String,String>>();
        Iterator< Pair<String, String> >  iter  = tab.iterator();
        while( iter.hasNext() ) {
            copied.add( iter.next() );
        }
        return copied;
    }
    protected Vector<Pair<String,Boolean>> interruptTabCopy( Vector<Pair<String,Boolean>> tab){
        Vector< Pair<String, Boolean> > copied = new Vector<Pair<String,Boolean>>();
        Iterator< Pair<String, Boolean> >  iter  = tab.iterator();
        while( iter.hasNext() ) {
            copied.add( iter.next() );
        }
        return copied;
    }
    protected void declareState(String stateName, StateActionFun action) {
        if(Actor23Utils.trace) CommUtils.outblue( getName() + " ActorBasicFsm23 | declareState " + stateName + " action=" + action );
        if( action == null ) CommUtils.outred(getName() + " ActorBasicFsm23 | action null");
        else stateMap.put( stateName, action );
    }
    protected IApplMessage searchInOldMsgQueue(String msgId) {
        Iterator<IApplMessage> iter = OldMsgQueue.iterator();
        while( iter.hasNext() ) {
            IApplMessage msg = iter.next();
            if( msg.msgId().equals(msgId)) {
                OldMsgQueue.remove(msg);
                return msg;
            }
        }
        return null;
    }


    @Override
    protected void elabMsg(IApplMessage msg) throws Exception {
        if(Actor23Utils.trace) CommUtils.outgray(getName() + " | ActorBasicFsm23 in " + this.curState + " elabMsg:" +  msg);
        String state = checkIfExpected(msg);
        if ( state != null ) stateTransition(state,msg);
        else memoTheMessage(msg);
    }
    protected void memoTheMessage(IApplMessage msg) {
        CommUtils.outgray(getName() + " | ActorBasicFsm23 in " + this.curState + " memoTheMessage not yet:" +  msg);
        OldMsgQueue.add(msg);
        currentMsg=null;
    }
}
