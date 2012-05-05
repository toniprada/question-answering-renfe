package es.upm.dit.gsi.sojason;
// Environment code for project Web40SOJason

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.Environment;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * This represents a Software Oriented Environment.
 * It overrides the getPercepts method so every time the agent perceives
 * it checks the model to update the percepts for the particular agent.
 *
 * Project: TEPRJason
 * Package: es.upm.dit.gsi.sojason
 * Class: SOEnvironment
 *
 * @author Miguel Coronado (miguelcb@dit.upm.es)
 * @version Mar 9, 2012
 *
 */
public class SOEnvironment extends Environment {

	/** The logger */
    private Logger logger = Logger.getLogger("TEPRJason." + SOEnvironment.class.getName());

    /** The model */
    public TEPRModel model;
    
    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);

        logger.info("Java version:" + System.getProperty("java.version"));
        logger.info("Java architecture:" + System.getProperty("sun.arch.data.model"));
        
        try {
			this.model = new TEPRModel();
		} catch (IOException e) {
			addPercept(Literal.parseLiteral("error(\"Could not inatantiate the model\")"));
			e.printStackTrace();
		}
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        logger.info("executing: " + action + " (" + agName + ")");

        // select the external action
    	boolean result = false;
    	String functor = action.getFunctor();
    	List<Term> terms = action.getTerms();
    	
    	if (functor.equals("sendNLU")) {
    		result = this.model.sendNlu(agName, terms);
    	}
    	else if (functor.equals("sendUser")) {
    		result = this.model.sendUser(terms.get(0).toString());
    	}
    	else if (functor.equals("findTravel")) {
    		result = this.model.findTravel(agName, terms);
    		return true;
    	}
        else {
        	logger.info(action + " was not implemented.");
        }
    	
        return result;
    }

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
    
    @Override
    public List<Literal> getPercepts(String agName) {
    	updatePerceptsForAg(agName);
    	return super.getPercepts(agName);
    }
    
    /**
     * <p>This updates the percepts for the given agent.</p>
     * @param agName The name of the agent
     */
    protected void updatePerceptsForAg (String agName) {
    	clearPercepts(agName);
    	Collection<Literal> literals = model.getDataFromInbox(agName);
    	for(Literal literal : literals){
    		addPercept(agName, literal);
    	}
    }
}
