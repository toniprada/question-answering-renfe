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
		
+!find_travel : not day(_)
	<- 	.print("Unknown day");
		?expecting(day);
		.print("We asked before, maybe the user answered directly");
		-expecting(day);
		?msg(Msg, Queryid);
		.concat("el dia ",Msg,X);
		!nlu(X, Queryid).
		
+!find_travel : not month(_)
	<- 	.print("Unknown month");
		?expecting(month);
		.print("We asked before, maybe the user answered directly");
		-expecting(month);
		?msg(Msg, Queryid);
		.concat("del mes ",Msg,X);
		!nlu(X, Queryid).
		
+!find_travel : not year(_)
	<- 	.print("Unknown year");
		?expecting(year);
		.print("We asked before, maybe the user answered directly");
		-expecting(year);
		?msg(Msg, Queryid);
		.concat("del",Msg,X);
		!nlu(X, Queryid).
		
+!find_travel : true
	<-	?location_from(From); 
		?location_to(To);
		?day(Day);
		?month(Month);
		?year(Year);
		//?people(People);
		.send(travelAgent, achieve, find(From, To, when(Day, Month, Year), 1));
		-location_from(_);
		-location_to(_);
		-day(_);
		-month(_);
		-year(_);
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
		
+?expecting(day) : true 
	<-	.print("Asking the day");
		.send(userAgent, achieve, ask_day);
		+expecting(day);
		-msg(_);
		.fail.
		
+?expecting(month) : true 
	<-	.print("Asking the month");
		.send(userAgent, achieve, ask_month);
		+expecting(month);
		-msg(_);
		.fail.
		
+?expecting(year) : true 
	<-	.print("Asking the year");
		.send(userAgent, achieve, ask_year);
		+expecting(year);
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
		
+month(Month) : true
	<- 	.print("month: ", Month);
		+month(Month).	
		
+year(Year) : true
	<- 	.print("year: ", Year);
		+year(Year).


