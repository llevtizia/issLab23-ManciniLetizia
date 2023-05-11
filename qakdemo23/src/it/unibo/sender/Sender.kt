/* Generated by AN DISI Unibo */ 
package it.unibo.sender

import it.unibo.kactor.*
import alice.tuprolog.*
import unibo.basicomm23.*
import unibo.basicomm23.interfaces.*
import unibo.basicomm23.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Sender ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		 var emitEvents = true   
			   var DT         = 1000L; 
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outblack("$name in ${currentState.stateName} | $currentMsg | ${Thread.currentThread().getName()} n=${Thread.activeCount()}")
						 	   
						CommUtils.outgreen("sender sends ... ")
						forward("msg1", "msg1(1)" ,"demo0" ) 
						delay(300) 
						forward("msg1", "msg1(2)" ,"demo0" ) 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		stateTimer = TimerActor("timer_s0", 
				 	 					  scope, context!!, "local_tout_sender_s0", DT )
					}	 	 
					 transition(edgeName="t06",targetState="sendothermsgs",cond=whenTimeout("local_tout_sender_s0"))   
				}	 
				state("sendothermsgs") { //this:State
					action { //it:State
						CommUtils.outgreen("sender sends again ... ")
						forward("msg2", "msg2(1)" ,"demo0" ) 
						if(  emitEvents  
						 ){emit("alarm", "alarm(fire)" ) 
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
				}	 
			}
		}
}
