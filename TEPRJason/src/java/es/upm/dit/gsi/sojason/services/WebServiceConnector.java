/**
 * 
 */
package es.upm.dit.gsi.sojason.services;

import jason.asSyntax.Literal;

import java.util.Collection;

/**
 * This interface defines a standard way to connect to a web service in
 * the definition of an external action in Jason.
 *
 * Project: Web40SOJason
 * Package: es.upm.dit.gsi.sojason.services
 * Class: WebServiceConnector
 *
 * @author Miguel Coronado (miguelcb@dit.upm.es)
 * @version Feb 27, 2012
 *
 */
public interface WebServiceConnector {

	/**
	 * This calls the service including in the request the parameters given.
	 * The URL of the service, the method to use and any other particularity
	 * of the transaction to connect to the service must be determined in the
	 * implementation of the method.
	 * 
	 * @param params	The list of parameters to include in the service 
	 * 					request. Due to this is not a <code>Dictionary</code> 
	 * 					the order of the parameters it is important and will be
	 * 					determined by the implementation of the extendee 
	 * 					classes.
	 */
	public Collection<Literal> call(String... params);
	
	/**
	 * This validates the set of parameters provided. Typically, this method
	 * should use some regex exprsesions to check whether a parameter is valid 
	 * or not, due to the nature of the parameter cannot be checked because of
	 * the type of the parameters has been unified to String.
	 * 
	 * @param params	The list of parameters to validate
	 */
	public boolean validateParams(String... params);
	
	/**
	 * This generates a set of error <code>Literal</code>s that describes the
	 * errors committed when trying to call the given service with the set of 
	 * parameters given.
	 * 
	 * @param params	The list of parameters
	 */
//	public Set<Literal> checkForErrors(String... params);
	
}
