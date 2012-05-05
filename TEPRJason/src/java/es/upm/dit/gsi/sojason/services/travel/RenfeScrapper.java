package es.upm.dit.gsi.sojason.services.travel;

import jason.asSyntax.Literal;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.upm.dit.gsi.sojason.beans.Journey;
import es.upm.dit.gsi.sojason.beans.Perceptable;
import es.upm.dit.gsi.sojason.services.WebServiceConnector;

/**
 *
 * Project: TEPRJason
 * Package: es.upm.dit.gsi.sojason.services.travel
 * Class: RenfeScrapper
 *
 * @author Miguel Coronado (miguelcb@dit.upm.es)
 * @version Feb 27, 2012
 *
 */
public class RenfeScrapper implements WebServiceConnector {

	/** */
	private Logger logger = Logger.getLogger("TEPRJason." + RenfeScrapper.class.getName());
	
	/** 
	 * It contains all the information about the <i>Renfe</i> 
	 * web service URL convenion. 
	 */
	RenfeServiceConvenion queryGenerator;
	
	/**
	 * Constructor
	 * @throws IOException if there is any error while loading the station ids.
	 */
	public RenfeScrapper() throws IOException {
		this.queryGenerator = new RenfeServiceConvenion();
	}
	

	public Collection<Literal> call(String... params) {
		
		if(!validateParams(params)){
			return null;
		}
		try {
			String queryid = params[0].toString();
			List<Perceptable> schedule = getSchedule  ( params[1].toString(), 
														params[2].toString(), 
														params[3].toString(), 
														params[4].toString(), 
														params[5].toString());
			
			// prepare response
			Collection<Literal> res =  new LinkedList<Literal>();
			for (Perceptable travel : schedule){
				if(travel instanceof Journey){
					((Journey)travel).setQueryid(queryid); // add the query id
				}
				res.addAll(travel.toPercepts());
			}
			return res;
			
		} catch (IOException e) {
//			return CollectionUtils.wrapList("error(io_exception, \"Some io exception ocurr\")");
			return null;
		}
		
	}

	public boolean validateParams(String... params) {
		if(params.length != 6){
			return false;
		}
		return true;
	}
	
	/**
	 * TODO: filter by time
	 * 
	 * 
	 * @param origin
	 * @param destination
	 * @param day
	 * @param month
	 * @param year
	 * @return
	 * @throws IOException 
	 */
	public List<Perceptable> getSchedule (String origin, String destination, 
							  String day, String month, String year) throws IOException {
		
		// The list with the journeys that matches the given criteria
		List<Perceptable> retList = new LinkedList<Perceptable>();
		
		// Get the html
		String queryUrl = "";
		try{
			queryUrl = queryGenerator.generateQuery(origin, destination, day, month, year);
			logger.info(queryUrl);
		}
		catch(IllegalArgumentException iae){
			retList.add( queryGenerator.reportParamErrors(origin, destination, day, month, year) );
			return retList;
		}
		Document doc = Jsoup.connect(queryUrl).get();
		
		// Get the rows of the schedule table
		Elements rows = doc.select("table#row > tbody > tr");
		// Each row has the information of a different journey
		for (Element row : rows) {
			Elements cells = row.getElementsByTag("td");
			if(cells.size() > 2){
				
				// get and fill the journey information
				Journey journey = new Journey();
				journey.setOrigin(origin);
				journey.setDestination(destination);
				journey.setDepartureTime(cells.get(1).text());
				journey.setArrivalTime(cells.get(2).text());
				journey.setDuration(cells.get(3).text());
				
				// the fee map for the particular journey
				Map<String, String> feeMap = new HashMap<String,String>();
				// get the fares
				Elements feeRows = cells.get(4).select("tbody tr");
				
				/* According to Renfe's website we select the following sublist */
				int fromIndex = 1;                    // skip the header row
				/* They present 2 set of fares (Internet and station) so we skip
				 * the header rows and divide by 2 to get the amount of fares to 
				 * parse */
				int toIndex = 1+(feeRows.size()-2)/2; 
				
				for(Element feeRow : feeRows.subList(fromIndex, toIndex)) {
					Elements feeCells = feeRow.getElementsByTag("td");
//					String feeName = feeCells.get(1).text().toLowerCase().replace(" ", "").replace(":", "").replace("ñ", "n");
					String feeName = feeCells.get(1).text().replace(":", "");
					String price = feeCells.get(2).text().replace(",", ".");
					// Set the fee
					feeMap.put(feeName, price);
				}
				
				// Set the fares
				journey.setFares(feeMap);
				retList.add(journey);
			}
		}

		return retList;
	}

	/**
	 * try it
	 * @param args
	 * @throws IOException
	 */
	public static void main(String [] args) throws IOException{
		RenfeScrapper rs = new RenfeScrapper();
		List<Perceptable> list = rs.getSchedule ("Madrid", "ciudad real", "16", "04", "2012");
		for(Perceptable journey : list){
			System.out.println(journey);
		}
	}
	
}
