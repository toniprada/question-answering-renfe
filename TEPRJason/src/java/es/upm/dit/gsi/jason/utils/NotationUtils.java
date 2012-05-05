package es.upm.dit.gsi.jason.utils;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;

import java.util.List;


/**
 * This Utils class is used to validate string according to the Jason atom 
 * notation criteria, and transform an invalid notation into a valid one
 * and vice versa.
 * 
 * This is useful in some context where the agents need to interact with an
 * uncontrollable environment such as the Web. 
 * 
 * In Jason notation, white-spaces are not allowed, neither, words that starts
 * with capital letter.
 *
 * Project: TEPRJason
 * Package: es.upm.dit.gsi.jason.utils
 * Class: NotationUtils
 *
 * @author Miguel Coronado (miguelcb@dit.upm.es)
 *
 */
public class NotationUtils {

	/** The functor of time literals */
	private static final Object TIME_FUNCTOR = "time";

	/**
	 * If there are more than one annotation it return null
	 * @param functor
	 * @return
	 */
	public static String getAnnotationValue (String functor, Structure action){
		
		List<Term> annots = action.getAnnots(functor);
		for(Term annot : annots){
			System.out.print(annot);
		}
		
		return null;
	}
	
	/**
	 *  <p>This checks if the string given is a valid literal. When trying 
	 *  to parse it, {@link jason.asSyntax.Literal#parseLiteral(String)} may 
	 *  throw a <code>ParseLiteralException</code>. If so happens it is 
	 *  assumed, the content given is not a literal. For instance, "value()" 
	 *  cannot be parsed. The same happens with strings.
	 *  
	 *  @return		true if the string can be parsed into a literal, it is a valid structure
	 *  			but a list.
	 */
	public static boolean isLiteral(String content) {
		System.out.println(content);
		try{
			Literal aux = Literal.parseLiteral(content);
			return aux.isStructure() && !aux.isList();
		}
		catch(Exception e){
			return false;
		}
	}
	
	/**
	 * @param toCheck
	 * @return
	 */
	public static boolean isValidAtom (String toCheck) {
		String lowerCase = toCheck.toLowerCase();
		return !toCheck.contains(" ") && !toCheck.contains(",") && toCheck.equals(lowerCase);
	}
	
	/**
	 * 
	 * @param toCheck
	 * @return
	 */
	public static boolean isCompactable (String toCheck) {
		return true;
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String compact(String str) {
		
		if (isValidAtom (str)) {return str;}
		if (!isCompactable(str)) {return null;}
		
		str = str.replace("_", "___");
		str = str.replace(" ", "_");
		str = str.replace("ñ", "n");
		return str.toLowerCase();
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String uncompact(String str) {
		str = str.replace("___", "#");
		str = str.replace("_", " ");
		str = str.replace("#", " ");
		return str;
	}
	
	/**
	 * <p>This removes the quotation mark from the string given. If that
	 * string has no quotation marks it returned trimmed.</p>
	 * 
	 * <p>The quotation marks are only removed from the beginning and the
	 * end of the string, so any quotation mark inserted in the middle of
	 * the string will be kept.</p>
	 * 
	 * @return 	the string without the quotation marks
	 */
	public static String removeQuotation (String str) {
		String message = str.trim();
		if(message.startsWith("\"")) message = message.substring(1);
		if(message.endsWith("\"")) message = message.substring(0, message.length()-1);
		return message;
	}
	
	/**
	 * TODO: document this
	 * @param timeLiteral
	 * @return
	 */
	public static String parseTime (String timeLiteral) {
		Literal lit = Literal.parseLiteral(timeLiteral);
		return getTimeFromLiteral(lit);
	}
	
	/**
	 * TODO: document this
	 * @param timeLiteral
	 * @return
	 */
	public static String getTimeFromLiteral (Literal timeLiteral) {
		if(!timeLiteral.getFunctor().equals(TIME_FUNCTOR)){
			throw new IllegalArgumentException("Illegal format. Functor must be: " + TIME_FUNCTOR);
		}
		return timeLiteral.getTerm(0) + ":" + timeLiteral.getTerm(1);
	}
	
	public static void main(String[] a){
//		getAnnotationValue("query", new Structure("journey(from, to)[query(123)]", 2));
		
		System.out.println(NotationUtils.isLiteral("this is a message"));
		System.out.println(NotationUtils.isLiteral("This is a message"));
		System.out.println(NotationUtils.isLiteral("fare(12,39))"));
		System.out.println(NotationUtils.isLiteral("fare(hola, time(12) ,39))"));
		System.out.println(NotationUtils.isLiteral("fare"));
//		System.out.println(NotationUtils.isLiteral("fare()"));
//		System.out.println(NotationUtils.isLiteral("[fare]"));
		System.out.println(NotationUtils.isLiteral("fare(12), fare(2)"));
//		System.out.println(NotationUtils.isLiteral("[fare(13), fare(12)]"));
		System.out.println(NotationUtils.isLiteral("list([fare(13), fare(12)])"));
		
	}
	
}
