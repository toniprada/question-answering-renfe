/**
 * 
 */
package es.upm.dit.gsi.jason.utils;

import jason.asSyntax.Literal;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * Project: TEPRJason
 * Package: es.upm.dit.gsi.jason.utils
 * Class: CollectionUtils
 *
 * @author Miguel Coronado (miguelcb@dit.upm.es)
 * @version Mar 9, 2012
 *
 */
public abstract class CollectionUtils {

	/**
	 * This wraps a Literal in a collection
	 * @param literal	The literal
	 * @return			Collection containing the literal given
	 */
	public static Collection<Literal> wrapList(Literal literal) {
		Collection<Literal> res = new LinkedList<Literal>();
		res.add(literal);
		return res;
	}
	
	/**
	 * This wraps a Literal in a collection
	 * @param literal	The string that represents a literal
	 * @return			Collection containing the literal given
	 */
	public static Collection<Literal> wrapList(String literal) {
		Collection<Literal> res = new LinkedList<Literal>();
		res.add(Literal.parseLiteral(literal));
		return res;
	}
	
	/**
	 * This
	 * @param collection
	 * @return
	 */
	public static String[] toStringArray (Collection<? extends Object> collection){
		String[] strArray = new String[collection.size()];
		
		int index = 0;
		for(Object obj : collection){
			if(obj == null) { 
				strArray[index] = "null"; 
			}
			else {
				strArray[index] = obj.toString();
			}
			index++;
		}
		
		return strArray;
	}
	
	/**
	 * 
	 * @param items
	 * @return
	 */
	public static String[] toStringArray (Object[] items){
		String[] strArray = new String[items.length];
		
		for(int index = 0; index < items.length; index++){
			Object obj = items[index];
			if(obj == null){
				strArray[index] = "null";
			}
			else{
				strArray[index] = items[index].toString();
			}
		}
		
		return strArray;
	}
}
