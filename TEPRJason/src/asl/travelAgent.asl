// Agent travelAgent skeleton
//
// 'findTravel' external action is provided. The exact format of the action
// is as follows:

//    findTravel(Queryid, From, To, Day, Month, Year)

// where the Queryid is a number that may be used to identify the the results
// of a particular call. this external action always successes. When the data 
// is processed it includes the beliefs in the agent belief-base. The format
// of the beliefs is as follows:

//   journey(<From>, <To>, <Departure>, <Arrival>, <Fare>)[query(Queryid)]
//   journey(madrid, barcelona, time(11,00), time(14,30), fare(FName, FPrice))[query(12345)]


/* Initial beliefs and rules */

/* Initial goals */

/* Plans */

@find
+!find(From, To, when(D, M, Y), People) :true
	<- 	findTravel(12345, From, To, D, M, Y);
		.at("now +1 second", "+!tell_user").
		
/* Store results */
@storejourney1
+journey(From, To, Departure, Arrival, fare(FName, FPrice))[query(Query)]
	: 	true
	<- 	.print(journey(From, To, Departure, Arrival, fare(FName, FPrice)));
		+journey(From, To, Departure, Arrival, fare(FName, FPrice)).
		
@telluser
/* Find the cheapest ticket and tell about it to the user agent */
+!tell_user : true 
	<- 	.findall(journey(fare(FPrice, FName), From, To, Departure, Arrival), 
			journey(From, To, Departure, Arrival, fare(FName, FPrice)), L);
		L \== []; // at least one journey
		.min(L, journey(fare(FPriceMin, FNameMin), FromMin, ToMin, DepartureMin, ArrivalMin));
		.print("Min fare ", FNameMin, " with price " , FPriceMin);
		.send(userAgent, achieve, tell_user(journey(FromMin, ToMin, DepartureMin, ArrivalMin, fare(FNameMin, FPriceMin)))).
		
		
-!tell_user : true 
	<- .send(userAgent, achieve, no_journey).
	

