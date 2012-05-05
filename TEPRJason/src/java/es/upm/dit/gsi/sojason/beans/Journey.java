/**
 * 
 */
package es.upm.dit.gsi.sojason.beans;

import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import es.upm.dit.gsi.jason.utils.NotationUtils;

/**
 * @author miguel
 *
 */
public class Journey implements Perceptable{

	
	/** the functor of the journey literal */
	public final static String JOURNEY_FUNCTOR = "journey";
	/** The functor of the fare literal */
	private static final Object FARE_FUNCTOR = "fare";
	/** from json  */
	private static final String FROM_NAME = "from";
	/** to json */
	private static final String TO_NAME = "to";
	/** departures json */
	private static final String TIME_FROM_NAME = "departures";
	/** arrives json */
	private static final String TIME_TO_NAME = "arrives";
	/** fares json */
	private static final String FARES_NAME = "fares";

	
	/** The departure time of the journey */
	private String departureTime;
	
	/** The arrival time of the journey */
	private String arrivalTime;
	
	/** 
	 * The duration of the journey. This is not simply the difference of the 
	 * departure and arrival time because of timezone considerations.
	 */
	private String duration;
	
	/** The origin */
	private String origin;

	/** The destination */
	private String destination;
	
	/** The fee map that contains the different available fee */
	private Map<String, String> fares;
	
	/** The query id. It is an annotation, it is not mandatory */
	private String queryid = null;
	
	/**
	 * @return the departureTime
	 */
	public String getDepartureTime() {
		return departureTime;
	}

	/**
	 * @param departureTime the departureTime to set
	 */
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	/**
	 * @return the arrivalTime
	 */
	public String getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * @param arrivalTime the arrivalTime to set
	 */
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @param oringin the origin to set
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return the fares
	 */
	public Map<String, String> getFares() {
		return fares;
	}

	/**
	 * @param fares the fares to set
	 */
	public void setFares(Map<String, String> fares) {
		this.fares = fares;
	}
	
	/**
	 * @return the queryid
	 */
	public String getQueryid() {
		return queryid;
	}

	/**
	 * @param queryid the queryid to set
	 */
	public void setQueryid(String queryid) {
		this.queryid = queryid;
	}

	/**
	 * <p>Generates a beliefs representation of the </code>Journey</code>.
	 * In the current implementation of {@link #toPercepts()}, the journey 
	 * is represented by several beliefs, all of them with the same value 
	 * in the from, to, departure and arrival time fields, but different 
	 * fares. The exact representation is presented below:</p>
	 * 
	 * <ul>
	 * 	 <li>journey(madrid, ciudad_real, time(10,15), time(11,5), fare(turista, 22.5))</li>
	 *   <li>journey(madrid, ciudad_real, time(10,15), time(11,5), fare(preferente, 35))</li>
	 * </ul>
	 * 
	 * <p>Notice that in the current implementation the <code>Duration</code> 
	 * attribute is skipped.</p>
	 * 
	 * @return The list with the percepts or an empty list if no fare is given
	 */
	public List<Literal> toPercepts() {
		if (this.fares.size() == 0){ return new LinkedList<Literal>(); }
		return unfoldPercepts();
//		return foldPercepts();
	}

	/**
	 * <p>Generates a beliefs representation of the </code>Journey</code>.
	 * The journey is represented by a single belief, that contains all the 
	 * fares as an array. The exact representation is presented below</p>
	 * 
	 * <ul>
	 * 	 <li>journey(madrid, ciudad_real, time(10,15), time(11,5), [fare(turista, 22.5), fare(preferente, 35)])</li>
	 * </ul>
	 * 
	 * @return	List with literals that represent the journey. With a folded 
	 * 			percept only one percept will be included in the list.
	 */
	@SuppressWarnings("unused")
	private List<Literal> foldPercepts() {
		
		String percept = "journey(";
		percept = percept.concat(NotationUtils.compact(this.origin));
		percept = percept.concat(", ");
		percept = percept.concat(NotationUtils.compact(this.destination));

		percept = percept.concat(", time(");
		String digits[] = this.departureTime.split("[\\x2E\\x3A]"); // [.:]
		percept = percept.concat(digits[0]);
		percept = percept.concat(", ");
		percept = percept.concat(digits[1]);

		percept = percept.concat("), time(");
		digits = this.arrivalTime.split("[\\x2E\\x3A]"); // [.:]
		percept = percept.concat(digits[0]);
		percept = percept.concat(", ");
		percept = percept.concat(digits[1]);

		percept = percept.concat("), [");
		for (String fareName : fares.keySet()) {
			percept = percept.concat("fare(");
			percept = percept.concat(NotationUtils.compact(fareName));
			percept = percept.concat(", ");
			percept = percept.concat(fares.get(fareName));
			percept = percept.concat("), ");
		}
		percept = percept.substring(0, percept.lastIndexOf(","));
		percept = percept.concat("])");
		
		// queryid annotation
		percept = percept.concat("[query(");
		percept = percept.concat(this.getQueryid());
		percept = percept.concat(")]");
		
		LinkedList<Literal> ret = new LinkedList<Literal>();
		ret.add(Literal.parseLiteral(percept));

		return ret;
	}
	
	/**
	 * <p>Generates a beliefs representation of the </code>Journey</code>.
	 * The journey is represented by several beliefs, all of them with the 
	 * same value in the from, to, departure and arrival time fields, but 
	 * different fares. The exact representation is presented below:</p>
	 * 
	 * <ul>
	 * 	 <li>journey(madrid, ciudad_real, time(10,15), time(11,5), fare(turista, 22.5))</li>
	 *   <li>journey(madrid, ciudad_real, time(10,15), time(11,5), fare(preferente, 35))</li>
	 * </ul>
	 * 
	 * TODO: when it is unfloded, it might be usefull if it includes, as 
	 * annotation, an id that represents the journey, to easily join the 
	 * literals.
	 *  
	 * @return	List with literals that represent the journey. All of the 
	 * 			literals will have the same, from, to, departure and arrival 
	 * 			time value, but different fares.
	 */
	private List<Literal> unfoldPercepts() {

		LinkedList<Literal> ret = new LinkedList<Literal>();// returning list
		
		String percept = "journey(";
		percept = percept.concat(NotationUtils.compact(this.origin));
		percept = percept.concat(", ");
		percept = percept.concat(NotationUtils.compact(this.destination));
		
		percept = percept.concat(", time(");
		String digits[] = this.departureTime.split("[\\x2E\\x3A]"); // [.:]
		percept = percept.concat(digits[0]);
		percept = percept.concat(", ");
		percept = percept.concat(digits[1]);
		
		percept = percept.concat("), time(");
		digits = this.arrivalTime.split("[\\x2E\\x3A]"); // [.:]
		percept = percept.concat(digits[0]);
		percept = percept.concat(", ");
		percept = percept.concat(digits[1]);
		
		percept = percept.concat("), ");
		
		for(String fareName : fares.keySet()) {
			String perceptFare = percept.concat("fare(");
			perceptFare = perceptFare.concat(NotationUtils.compact(fareName));
			perceptFare = perceptFare.concat(", ");
			perceptFare = perceptFare.concat(fares.get(fareName));
			perceptFare = perceptFare.concat(") ");
			perceptFare = perceptFare.concat(")"); // close journey
			
			// queryid annotation
			perceptFare = perceptFare.concat("[query(");
			perceptFare = perceptFare.concat(this.getQueryid());
			perceptFare = perceptFare.concat(")]");
			
			ret.add(Literal.parseLiteral(perceptFare));
		}
		
		return ret;
	}
	
	
	/**
	 * <p>This evaluates a literal that represents a journey, in the same 
	 * format given in {@link #toPercepts()} and compiles a <code>Journey</code> 
	 * object.</p>
	 * 
	 * <p>When {@link #toPercepts()} is configured to use unfolded policy, 
	 * {@link #parseJourneyLiteral(String)} is the inverse method.</p>
	 * 
	 * @throws IllegalArgumentException		if the format is not valid
	 * @param literal	the literal that represents a journey
	 * @return			the journey
	 */
	@SuppressWarnings("unchecked")
	public static Journey parseJourneyLiteral (String literal){
		
		Literal lit = Literal.parseLiteral(literal);
		if(!lit.getFunctor().equals(JOURNEY_FUNCTOR)) {
			throw new IllegalArgumentException("The format is not correct: " +
					"functor is not " + JOURNEY_FUNCTOR);
		}
		
		Term[] terms = lit.getTermsArray();
		// Check types
		if (!terms[0].isAtom() ||
			!terms[1].isAtom() ||
			!terms[2].isLiteral() ||
			!terms[3].isLiteral() ){
			
			throw new IllegalArgumentException("The format is not correct. " +
					"Expected journey(<string>,<string>,<structure>," +
					"<structure>,_) but was " + literal);
		}

		// extract fares (either list or single fare)
		Map<String, String> fareMap = new HashMap<String, String>();
		if(terms[4].isList()){ // isntanceof Collection
			for(Term fare : (Collection<Term>)terms[4]){
				Literal fareLiteral = (Literal)fare;
				if(!fareLiteral.getFunctor().equals(FARE_FUNCTOR)){
					throw new IllegalArgumentException("Illegal format. Functor must be: " + FARE_FUNCTOR);
				}
				fareMap.put(fareLiteral.getTerm(0).toString(), 
							fareLiteral.getTerm(1).toString());
			}
		}
		else if (terms[4].isLiteral()){
			Literal fareLiteral = (Literal)terms[4];
			fareMap.put(fareLiteral.getTerm(0).toString(), 
						fareLiteral.getTerm(1).toString());
		}
		else{
			throw new IllegalArgumentException("The format is not correct. " +
					"Expected journey(_,_,_,_, <literal>|[<literal>s]) but " +
					"was " + literal);
		}
		
		
		Journey jor = new Journey();
		jor.setOrigin(terms[0].toString()); 		// location from
		jor.setDestination(terms[1].toString()); 	// location to
		jor.setDepartureTime(NotationUtils.getTimeFromLiteral((Literal)terms[2])); 	// departure time
		jor.setArrivalTime(NotationUtils.getTimeFromLiteral((Literal)terms[3])); 	// arrival time
		jor.setFares(fareMap);
		
		return jor;
	}

	/**
	 * <p>This parses the literal given and returns a json representation of 
	 * the journey.</p>
	 *  
	 * @param literal	The string that represents the literal
	 * @return			Json string that represents the string
	 */
	public static String toJson (String literal){
		Journey jor = parseJourneyLiteral(literal);
		return jor.toJson();
	}
	
	/**
	 * Json representation of the journey
	 * @return	json
	 */
	private String toJson() {

		String json = "{\"";
		json = json.concat(JOURNEY_FUNCTOR);
		json = json.concat("\" :{\"");
		json = json.concat(FROM_NAME);
		json = json.concat("\" : \"");
		json = json.concat(this.origin);
		json = json.concat("\", \"");
		json = json.concat(TO_NAME);
		json = json.concat("\" : \"");
		json = json.concat(this.destination);
		json = json.concat("\", \"");
		json = json.concat(TIME_FROM_NAME);
		json = json.concat("\" : \"");
		json = json.concat(this.departureTime);
		json = json.concat("\", \"");
		json = json.concat(TIME_TO_NAME);
		json = json.concat("\" : \"");
		json = json.concat(this.arrivalTime);
		json = json.concat("\", \"");
		json = json.concat(FARES_NAME);
		json = json.concat("\" : [");
		
		for(String fareName : this.fares.keySet()){
			json = json.concat("{\"");
			json = json.concat(fareName);
			json = json.concat("\" : \"");
			json = json.concat(this.fares.get(fareName));
			json = json.concat("\"}, ");
		}
		json = json.substring(0, json.length()-2);
		
		json = json.concat("]}}");
		
		return json;
	}
	
	
	/** Textual representation of the journey. Use for debuging purposes inly.*/
	public String toString() {
		
		String toString = "From: " +
					origin + " (" + departureTime + ") to: " + destination + 
					" (" + arrivalTime + ") in " + duration + " for "; 
					
		if(fares != null)
			toString = toString.concat(fares.toString());
		else
			toString += null;
		return toString;
	}

	/** try it */
	public static void main(String [] args) {
		Journey jor = new Journey();
		jor.setOrigin("madrid");
		jor.setDepartureTime("15:30");
		jor.setDestination("cuenca");
		jor.setArrivalTime("18:00");
		Map<String, String> fares = new HashMap<String, String>();
		fares.put("turista", "22.5");
		fares.put("preferente", "42.5");
		jor.setFares(fares);
		jor.setQueryid("123");
		
		List<Literal> list = jor.toPercepts();
		for(Literal lit : list){
			System.out.println(toJson(lit.toString()));
		}
		
	}
	
}
