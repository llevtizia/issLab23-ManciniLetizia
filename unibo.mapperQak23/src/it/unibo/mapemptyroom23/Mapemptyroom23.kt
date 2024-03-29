/* Generated by AN DISI Unibo */ 
package it.unibo.mapemptyroom23

import it.unibo.kactor.*
import alice.tuprolog.*
import unibo.basicomm23.*
import unibo.basicomm23.interfaces.*
import unibo.basicomm23.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Mapemptyroom23 ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		 var StepTime = 345
			   var NumStep   = 0 
			   val MapName   = "mapEmpty23"
			   val planner   = unibo.planner23.Planner23Util()
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outblack("mapperbuilder starts")
						 planner.initAI()   
						 planner.showMap()   
						request("engage", "engage(mapemptyroom23)" ,"basicrobot" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t00",targetState="doAheadMove",cond=whenReply("engagedone"))
				}	 
				state("doAheadMove") { //this:State
					action { //it:State
						request("step", "step($StepTime)" ,"basicrobot" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t01",targetState="continue",cond=whenReply("stepdone"))
					transition(edgeName="t02",targetState="turn",cond=whenReply("stepfailed"))
				}	 
				state("continue") { //this:State
					action { //it:State
						 planner.updateMap(  "w", "" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="doAheadMove", cond=doswitch() )
				}	 
				state("turn") { //this:State
					action { //it:State
						 NumStep = NumStep + 1  
						forward("cmd", "cmd(l)" ,"basicrobot" ) 
						  planner.updateMap(  "l", "" ) 
									//planner.showMap()	
									planner.showCurrentRobotState();
									//CommUtils.waitTheUser("turn. Please 1CR")	 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="doAheadMove", cond=doswitchGuarded({ NumStep < 4  
					}) )
					transition( edgeName="goto",targetState="endwork", cond=doswitchGuarded({! ( NumStep < 4  
					) }) )
				}	 
				state("endwork") { //this:State
					action { //it:State
						 	//planner.showMap();
						 			planner.showCurrentRobotState();
						 			planner.saveRoomMap("$MapName");
						forward("disengage", "disengage(mapemptyroom23)" ,"basicrobot" ) 
						CommUtils.outblack("mapperbuilder BYE")
						 System.exit(0)  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
				}	 
			}
		}
}
