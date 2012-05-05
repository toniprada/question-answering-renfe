// Agent nluAgent skeleton

// A sample plan has been given. The format of the triggering event is
// optional, since it depends on the message send by the userAgent (defined
// by the programmer)

// In order to develop the multi-agent system before the graphs of the unitex
// module, a plan like the 'demo_msg' may be used. That plan uses the 

// 'sendNLU' external action is provided. It must be used to send data to the
// unitex module. The format of the action is as follows:

//   sendNLU("msg to nlu")

// this external action always successes. When the data is processed it 
// includes the beliefs in the agent belief-base.


/* Initial beliefs and rules */

/* Plans */
@nlu
+!nlu(Msg, Queryid): true 
	<-	sendNLU(Msg);
		+msg(Msg, Queryid);
		.at("now +1 second", "+!find_travel").
		
@find_travel
+!find_travel : not location_from(_)
	<- 	.print("Unknown departure location");
		?expecting(from);
		.print("We asked before, maybe the user answered directly");
		-expecting(from);
		?msg(Msg, Queryid);
		.concat("desde ",Msg,X);
		!nlu(X, Queryid).

+!find_travel : not location_to(_)
	<- 	.print("Unknown arrival location");
		?expecting(to);
		.print("We asked before, maybe the user answered directly");
		-expecting(to);
		?msg(Msg, Queryid);
		.concat("hacia ",Msg,X);
		!nlu(X, Queryid).
		
+!find_travel : true
	<-	?location_from(From); 
		?location_to(To);
		//?people(People);
		?day(Day);
		.send(travelAgent, achieve, find(From, To, when(Day, 5, 2012), 1));
		-location_from(_);
		-location_to(_);
		-expecting(_);
		-msg(_).

/* Asking the user for missed data */
		
+?expecting(from) : true 
	<-	.print("Asking the departure location");
		.send(userAgent, achieve, ask_from);
		+expecting(from);
		-msg(_);
		.fail.
		
+?expecting(to) : true 
	<-	.print("Asking the arrival location");
		.send(userAgent, achieve, ask_to);
		+expecting(to);
		-msg(_);
		.fail.
		
/* Beliefs from the NLU system */

+location_from(City) : true
	<- 	.print("from: ", City);
		+location_from(City).
		
+location_to(City) : true
	<- 	.print("to: ", City);
		+location_to(City).
	
+people(People) : true
	<-	.print("people: ", People);
		+people(People).
		
+day(Day) : true
	<- 	.print("day: ", Day);
		+day(Day).	

