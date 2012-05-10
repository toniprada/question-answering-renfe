// Agent userAgent skeleton
//
// Messages from the user are expressed as beliefs with the format

//    user_msg("message string here")

// 'sendUser' external action is provided. It can be used to send a literal 
// or a string to the user.

//    sendUser(<literal>|<string>)

// When the literal has the specific format

//    journey(From, To, Departure, Arrival, fare(FName, FPrice))

// the client prints me data in a special format, as it recognizes
// it as a journey.

/* Initial beliefs and rules */

queryId(0).

/* Initial goals */

/* Plans*/

@msg
+user_msg(Msg) : true <- 
	?queryId(Queryid);
	-queryId(_);
	+queryId(Queryid+1);
	.send(nluAgent, achieve, nlu(Msg, Queryid + 1)).
	
+!tell_user(journey(From, To, Departure, Arrival, fare(FName, FPrice)))
	: 	true
	<-	sendUser(journey(From, To, Departure, Arrival, fare(FName, FPrice)));
		-journey(_, _, _, _, fare(_, _)).
	
+!no_journey : true
	<- sendUser("No he encontrado billetes").
	
+!ask_from : true
	<- sendUser("&#191;Desde donde desea partir?").
	
+!ask_to : true
	<- sendUser("&#191;Hacia donde desea ir?").
