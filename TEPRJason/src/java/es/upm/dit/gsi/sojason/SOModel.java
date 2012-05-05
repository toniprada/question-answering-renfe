/**
 * 
 */
package es.upm.dit.gsi.sojason;

import jason.asSyntax.Literal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Project: TEPRJason
 * Package: es.upm.dit.gsi.sojason
 * Class: SOModel
 *
 * @author Miguel Coronado (miguelcb@dit.upm.es)
 * @version Mar 9, 2012
 *
 */
abstract public class SOModel {
	
	/**
	 * This contains the data that will be 
	 */
	private Map<String, Set<Literal>> serviceDataInbox;

	/**
	 * Constructor. Just initializes attributes.
	 */
	public SOModel(){
		this.serviceDataInbox = Collections.synchronizedMap(new HashMap<String, Set<Literal>>());
	}
	
	/**
	 * <p>This puts data into the <code>serviceDataInbox</code> for a 
	 * particular agent. The data loaded will be appended into the already
	 * existing data if any.</p>
	 * 
	 * <p>The data from the <code>serviceDataInbox</code> is removed as 
	 * described in the documentation of {@link #getDataFromInbox(String)}</p>
	 * 
	 * @param agName		The name of the agent.
	 * @param serviceData	The service-data.
	 */
	public void setDataInbox (String agName, Collection<Literal> serviceData){
		
		synchronized (serviceDataInbox) {
			if (!this.serviceDataInbox.containsKey(agName)) {
				Set<Literal> set = new HashSet<Literal>();
				set.addAll(serviceData); // create a set and add all the collection
				this.serviceDataInbox.put(agName, set);
				return;
			}
			
			// There is no data in the inbox for the agent given
			Set<Literal> set = this.serviceDataInbox.get(agName);
			set.addAll(serviceData);
			this.serviceDataInbox.put(agName, set);
		}
		
	}
	
	/**
	 * <p>This provides a different way to call the method 
	 * {@linkplain #setDataInbox(String, Collection)} with a single literal
	 * instead of a collection of literals.</p>
	 * 
	 * @param agName	the name of the agent.
	 * @param literal	the literal.
	 */
	public void setDataInbox (String agName, Literal literal) {
		Set <Literal> set = new HashSet<Literal>();
		set.add(literal);
		setDataInbox(agName, set);		
	}
	
	/**
	 * <p>This checks if there are new data for the agent given.</p>
	 * 
	 * @param 	agName	the agent name
	 * @return	true if the given agent name is an existing key in the 
	 * 			{@link #serviceDataInbox} <code>Map</code>
	 */
	public boolean hasNewData (String agName){
		return this.serviceDataInbox.containsKey(agName);
	}
	
	/**
	 * <p>This gets from the <code>serviceDataInbox</code> the data stored for
	 * the agent given. It will remove the data from the inbox, so two consequent 
	 * invocations of this method will return different results, actually, if no 
	 * new data is put, the second invocation will return no data.</p>
	 * 
	 * <p>So, it is important to point out this method empties the 
	 * <code>serviceDataInbox</code>.</p>
	 * 
	 * <p>This method never returns null to avoid null pointer</p>
	 * 
	 * @param agName 	the name of the agent who data will be retrieved from 
	 * 					the inbox
	 * @return			The data retrieved
	 */
	public Collection<Literal> getDataFromInbox (String agName) {
		if (!this.serviceDataInbox.containsKey(agName)){
			return new HashSet<Literal>();
		}
//		return this.serviceDataInbox.get(agName);
		return this.serviceDataInbox.remove(agName);
	}
		
}
