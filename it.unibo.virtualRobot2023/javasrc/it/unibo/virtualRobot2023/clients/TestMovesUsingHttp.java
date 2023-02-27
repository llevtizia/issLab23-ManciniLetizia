/*
ClientUsingPost.java
===============================================================
Technology-dependent application
TODO. eliminate the communication details from this level
===============================================================
*/
package it.unibo.virtualRobot2023.clients;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
// import unibo.basicomm23.utils.CommUtils;

import java.net.URI;

public class TestMovesUsingHttp {
	private  final String localHostName    = "localhost"; //"localhost"; 192.168.1.7
	private  final int port                = 8090;
	private  final String URL              = "http://"+localHostName+":"+port+"/api/move";
	private  CloseableHttpClient httpclient ;
	private  JSONParser simpleparser = new JSONParser();	
	 private  String turnrightcmd  = "{\"robotmove\":\"turnRight\"    , \"time\": \"300\"}";
	 private  String turnleftcmd  = "{\"robotmove\":\"turnLeft\"     , \"time\": \"300\"}";
	 private  String forwardcmd   = "{\"robotmove\":\"moveForward\"  , \"time\": \"1500\"}";  //long ...
	 private  String backwardcmd  = "{\"robotmove\":\"moveBackward\" , \"time\": \"2300\"}";
	 private  String haltcmd      = "{\"robotmove\":\"alarm\" ,        \"time\": \"10\"}";


	
	public TestMovesUsingHttp() {
		httpclient = HttpClients.createDefault();
	}


  	protected JSONObject callHTTP(String crilCmd )  {
  		JSONObject jsonEndmove = null;
		try {
 			StringEntity entity = new StringEntity(crilCmd);
			HttpUriRequest httppost = RequestBuilder.post()
					.setUri(new URI(URL))
					.setHeader("Content-Type", "application/json")
					.setHeader("Accept", "application/json")
					.setEntity(entity)
					.build();
			long startTime                 = System.currentTimeMillis() ;
			CloseableHttpResponse response = httpclient.execute(httppost);
			long duration  = System.currentTimeMillis() - startTime;
			String answer  = EntityUtils.toString(response.getEntity());
			System.out.println( Thread.currentThread() + " callHTTP | answer= " + answer + " duration=" + duration );

			jsonEndmove = (JSONObject) simpleparser.parse(answer);
			System.out.println("callHTTP | jsonEndmove=" + jsonEndmove + " duration=" + duration);
		} catch(Exception e){
			System.out.println("callHTTP | " + crilCmd + " ERROR:" + e.getMessage());
		}
		return jsonEndmove;
	}

/*
	BUSINESS LOGIC
*/
	public void doBasicMoves() {
		JSONObject result;
		// CommUtils.waitTheUser("PUT ROBOT in HOME and hit");
		System.out.println("STARTING doBasicMoves ... ");
			result = callHTTP(  turnleftcmd ) ;
			System.out.println("turnLeft endmove=" + result);
			result = callHTTP(  turnrightcmd ) ;
			System.out.println("turnRight endmove=" + result);
		//CommUtils.waitTheUser("hit to forward (time 1500)");
			result = callHTTP(  forwardcmd  );
			System.out.println("moveForward endmove=" + result);
		//CommUtils.waitTheUser("hit to backward (time 2300)");
		    result = callHTTP(  backwardcmd );
			System.out.println("moveBackward endmove=" + result);
	}



	public void doForward() {
		String forwardcmd   = "{\"robotmove\":\"moveForward\"  , \"time\": \"1000\"}";
		//CommUtils.waitTheUser("PUT ROBOT in HOME  and hit");
		JSONObject result = callHTTP(  forwardcmd  );
		System.out.println("moveForward endmove=" + result);
	}

	public void doCollision() {
		String forwardcmd   = "{\"robotmove\":\"moveForward\"  , \"time\": \"3000\"}";
		//CommUtils.waitTheUser("PUT ROBOT near a wall and hit");
		JSONObject result = callHTTP(  forwardcmd  );
		System.out.println("moveForward endmove=" + result);
	}
	public void doHalt() {
		String forwardcmd   = "{\"robotmove\":\"moveForward\"  , \"time\": \"3000\"}";
		//CommUtils.waitTheUser("PUT ROBOT in HOME and hit (forward 3000 and alarm after 1000)");
		sendAlarmAfter(1000);
		JSONObject result = callHTTP(  forwardcmd  );
		System.out.println("moveForward endmove=" + result);
	}

	// doTurnLeft
	public void doTurnLeft() {
		String turnleftcmd   = "{\"robotmove\":\"turnLeft\"     , \"time\": \"300\"}";
		//CommUtils.waitTheUser("PUT ROBOT near a wall and hit");
		JSONObject result = callHTTP(  turnleftcmd  );
		System.out.println("turnLeft endmove=" + result);
	}
	//
	public void doBoundary(){
		int i;
		for (i = 0; i < 4; i++) {
			doCollision();
			doTurnLeft();
		}
	}

	protected void sendAlarmAfter( int time ){
		new Thread(){
		  	protected  JSONObject mycallHTTP(String crilCmd )  {
		  	     System.out.println( Thread.currentThread() + " mycallHTTP starts" );
		  		JSONObject jsonEndmove  = null;
		  		JSONParser mysimpleparser = new JSONParser();
				try {
		 			StringEntity entity = new StringEntity(crilCmd);
					HttpUriRequest httppost = RequestBuilder.post()
							.setUri(new URI(URL))
							.setHeader("Content-Type", "application/json")
							.setHeader("Accept", "application/json")
							.setEntity(entity)
							.build();
					long startTime                 = System.currentTimeMillis() ;
					CloseableHttpResponse response = httpclient.execute(httppost);
					long duration  = System.currentTimeMillis() - startTime;
					String answer  = EntityUtils.toString(response.getEntity());
					//System.out.println( Thread.currentThread() + " mycallHTTP | answer= " + answer + " duration=" + duration );
					jsonEndmove = (JSONObject) mysimpleparser.parse(answer);
					System.out.println(Thread.currentThread() + " mycallHTTP | jsonEndmove=" + jsonEndmove + " duration=" + duration);
				} catch(Exception e){
					System.out.println(Thread.currentThread() + " mycallHTTP | " + crilCmd + " ERROR:" + e.getMessage());
				}
				return jsonEndmove;
			}
			public void run(){
				// CommUtils.delay(time); // posso sostituirlo con una sleep
				System.out.println(Thread.currentThread() + " send alarm"  );
				JSONObject result = mycallHTTP(  haltcmd  );
				//if( result != null ) 
					System.out.println(Thread.currentThread() + " sendAlarmAfter result=" + result);
			}
		}.start();
	}
/*
MAIN
 */
	public static void main(String[] args)   {
		// CommUtils.aboutThreads("Before start - ");
		TestMovesUsingHttp appl = new TestMovesUsingHttp();

		// added by me
		appl.doBoundary();

		System.out.println("boundary done");
		//appl.doHalt();
		// CommUtils.aboutThreads("At end - ");
	}
	
 }
