/**
 * 
 */
package es.upm.dit.gsi.sojason.services.travel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import es.upm.dit.gsi.jason.utils.NotationUtils;
import es.upm.dit.gsi.sojason.beans.ErrorReport;

/**
 *
 * Project: TEPRJason
 * Package: es.upm.dit.gsi.sojason.services.travel
 * Class: RenfeServiceConvenion
 *
 * @author Miguel Coronado (miguelcb@dit.upm.es)
 * @version fEB 27, 2012
 *
 */
public class RenfeServiceConvenion {

	public final static String SERVICE_URL = "http://horarios.renfe.com/HIRRenfeWeb/buscar.do";
	public final static String ORIGIN_PARAM = "O";
	public final static String DESTINATION_PARAM = "D";
	public final static String YEAR_PARAM = "AF";
	public final static String MONTH_PARAM = "MF";
	public final static String DAY_PARAM = "DF";
	public final static String DEFAULT_PATH_TO_CITY_CODES_FILE = "conf/cities.xml";
	
	private Properties cityCodes;
	
	/**
	 * 
	 * @param cityCodesFile
	 * @throws IOException 
	 */
	public RenfeServiceConvenion (File cityCodesFile) throws IOException {
		this.cityCodes = new Properties();
		this.cityCodes.loadFromXML(new FileInputStream(cityCodesFile));
	}
	
	/**
	 * Default constructor
	 * @throws IOException 
	 */
	public RenfeServiceConvenion () throws IOException {
		this(new File(DEFAULT_PATH_TO_CITY_CODES_FILE));
	}
	
	
	/**
	 * 
	 * @param origin
	 * @param destination
	 * @param day
	 * @param month
	 * @param year
	 * @return
	 */
	public String generateQuery (String origin, String destination, 
								  String day, String month, String year){

		origin = NotationUtils.uncompact(origin);
		destination = NotationUtils.uncompact(destination);
		
		if(!validateParams(origin, destination, day, month, year)){
			throw new IllegalArgumentException();
		}
		
		String res = SERVICE_URL;
		res = res.concat("?");
		res = res.concat(ORIGIN_PARAM);
		res = res.concat("=");
		res = res.concat(this.cityCodes.getProperty(origin.toLowerCase()));
		res = res.concat("&");
		res = res.concat(DESTINATION_PARAM);
		res = res.concat("=");
		res = res.concat(this.cityCodes.getProperty(destination.toLowerCase()));
		res = res.concat("&");
		res = res.concat(YEAR_PARAM);
		res = res.concat("=");
		res = res.concat(year);
		res = res.concat("&");
		res = res.concat(MONTH_PARAM);
		res = res.concat("=");
		res = res.concat(month);
		res = res.concat("&");
		res = res.concat(DAY_PARAM);
		res = res.concat("=");
		res = res.concat(day);
		// 'concat' is faster than '+' operator
		
		return res;
	}

	/**
	 * 
	 * @param origin
	 * @param destination
	 * @param day
	 * @param month
	 * @param year
	 * @throws IllegalArgumentException
	 */
	protected boolean validateParams(String origin, String destination, String day,
			String month, String year) {

		if(!Pattern.matches("\\d{4}", year)){
			return false;
		}
		if(!Pattern.matches("\\d{1,2}", month)){
			return false;
		}
		if(!Pattern.matches("\\d{1,2}", day)){
			return false;
		}
		
		int monthI = Integer.parseInt(month);
		int dayI = Integer.parseInt(day);
		int monthNumberOfDays[] = {31,29,31,30,31,30,31,31,30,31,30,31};
		if(monthI < 1 || monthI > 12){
			return false;
		}
		if(dayI < 1 || dayI > monthNumberOfDays[monthI-1]){
			return false;
		}
		
		if(origin == null || !this.cityCodes.containsKey(origin.toLowerCase())) {
			return false;
		}
		
		if(destination == null || !this.cityCodes.containsKey(destination.toLowerCase())) {
			return false;
		}
		
		return true;
	}
	
	protected ErrorReport reportParamErrors (String origin, String destination, String day,
			String month, String year) {
		
		ErrorReport errors = new ErrorReport();
		
		if(!Pattern.matches("\\d{4}", year)){
			errors.put("year", "invalid format");
		}
		if(!Pattern.matches("\\d{1,2}", month)){
			errors.put("month", "invalid format");
		}
		if(!Pattern.matches("\\d{1,2}", day)){
			errors.put("day", "invalid format");
		}
		
		int monthI = Integer.parseInt(month);
		int dayI = Integer.parseInt(day);
		int monthNumberOfDays[] = {31,29,31,30,31,30,31,31,30,31,30,31};
		if(monthI < 1 || monthI > 12){
			errors.put("month", "out of range");
			monthI = 1; // this lets check the day
		}
		if(dayI < 1 || dayI > monthNumberOfDays[monthI-1]){
			errors.put("day", "out of range");
		}
		
		if(origin == null || !this.cityCodes.containsKey(origin.toLowerCase())) {
			errors.put("origin", "no such location");
		}
		
		if(destination == null || !this.cityCodes.containsKey(destination.toLowerCase())) {
			errors.put("destination", "no such location");
		}
		
		return errors;
		
	}
	
	/**
	 * 
	 * @param arga
	 * @throws IOException
	 */
	public static void main(String [] arga) throws IOException{
		
//		RenfeServiceConvenion rsc = new RenfeServiceConvenion();
//		Properties newProperties = new Properties();
//
//		for (Object key : rsc.cityCodes.keySet() ){
//			String keyStr = (String)key;
//			keyStr =  keyStr.toLowerCase();
//			newProperties.put(keyStr, rsc.cityCodes.get(key));
//			if (keyStr.contains("á") || 
//				keyStr.contains("é") || 
//				keyStr.contains("í") || 
//				keyStr.contains("ó") || 
//				keyStr.contains("ú")) {
//				
//				keyStr = keyStr.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u");
//				newProperties.put(keyStr, rsc.cityCodes.get(key));
//			}
//		}
//		
//		newProperties.storeToXML(new FileOutputStream(DEFAULT_PATH_TO_CITY_CODES_FILE), "comment");
		
		RenfeServiceConvenion rsc = new RenfeServiceConvenion();
		String res = rsc.generateQuery("Madrid", "Ciudad Real", "15", "02", "2012");
		System.out.println(res);
		
	}
	
}
