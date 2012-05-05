/**
 * 
 */
package es.upm.dit.gsi.sojason.beans;

import jason.asSyntax.Literal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import es.upm.dit.gsi.jason.utils.NotationUtils;

/**
 * @author miguel
 *
 */
public class ErrorReport extends HashMap<String, String> implements Perceptable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.qa.beans.Perceptable#toPercepts()
	 */
	public List<Literal> toPercepts() {

		List<Literal> res = new LinkedList<Literal>();
		for (String  key : this.keySet()){
			if(!NotationUtils.isCompactable(key)) continue;
			res.add(Literal.parseLiteral("error(" + NotationUtils.compact(key) + ", \"" + get(key) + "\")"));
		}
		return res;
	}

}
