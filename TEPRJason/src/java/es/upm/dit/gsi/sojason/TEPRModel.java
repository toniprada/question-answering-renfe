package es.upm.dit.gsi.sojason;

import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import es.upm.dit.gsi.httpserver.SimpleHttpServer;
import es.upm.dit.gsi.httpserver.handlers.QAHandler;
import es.upm.dit.gsi.jason.utils.CollectionUtils;
import es.upm.dit.gsi.jason.utils.NotationUtils;
import es.upm.dit.gsi.sojason.beans.Journey;
import es.upm.dit.gsi.sojason.nlu.unitex.UnitexWrapper;
import es.upm.dit.gsi.sojason.services.WebServiceConnector;
import es.upm.dit.gsi.sojason.services.travel.RenfeScrapper;

/**
 *
 * Project: TEPRJason
 * Package: es.upm.dit.gsi.qa
 * Class: Web40Model
 *
 * @author Miguel Coronado (miguelcb@dit.upm.es)
 * @version Feb 29, 2012
 *
 */
public class TEPRModel extends SOModel{

	/** The Natural Language Service Connector */
	private WebServiceConnector nluConnector;
	
	/** The RENFE Scraper */
	private WebServiceConnector renfeScrapper;
	
	/** The http server to connect the client */
	private SimpleHttpServer httpServer;
	
	/** exchange messages with the server */
	private MessageSignal serverMessage;

	/** the Logger */
	private Logger logger = Logger.getLogger("TEPRJason." + TEPRModel.class.getName());
	
	/** 
	 * Constructor 
	 * @throws IOException 
	 */
	public TEPRModel () throws IOException {
		super();
		this.nluConnector = new UnitexWrapper();
		this.renfeScrapper = new RenfeScrapper();
		this.serverMessage = new MessageSignal();
		
		this.httpServer = new SimpleHttpServer(8000, "/"); // creates the server
		this.httpServer.addContext("/qasystem", new QAHandler(this));
		this.httpServer.start(); // lunches the server
	}
	
	/**
	 * <p>This calls the NLU service</p>
	 * 
	 * <p>Internally this modifies the model so it reports to the agent</p>
	 *  
	 * @param agName 	the name of the agent that will be reported with the 
	 * 					results of the call.
	 * @param terms		The parameters
	 * @return			
	 */
	public boolean sendNlu (String agName, Collection<Term> params) {

		logger.info("Entering sendNLU...");
		try{
			String[] strParams = CollectionUtils.toStringArray(params);
			Collection<Literal> serviceData = nluConnector.call(strParams);
			if(serviceData == null){ 
				logger.info("Could not complete action sendNLU: no service data found");
				return false; 
			}
			
			// put data into mailbox
			this.setDataInbox(agName, serviceData);
		} 
		catch (Exception e){
			logger.info("Could not complete action sendNLU:" + e.getMessage());
			return false;	
		}
		
		logger.info("NLU call completed successfully");
		return true;
	}
	
	/**
	 * <p>This calls the Renfe Scraper</p>
	 * 
	 * @param agName
	 * @param terms
	 * @return
	 */
	public boolean findTravel (String agName, Collection<Term> params) {
		
		logger.info("Entering findTravel...");
		try{
			String[] strParams = CollectionUtils.toStringArray(params);
			Collection<Literal> serviceData = renfeScrapper.call(strParams);
			if(serviceData == null){ return false; }
			
			// put data into mailbox
			this.setDataInbox(agName, serviceData);
		} 
		catch (Exception e){ return false; }
		
		logger.info("findTravel call completed successfully");
		return true;
		
	}
	
	/**
	 * 
	 * @param content
	 * @return
	 */
	public boolean sendUser (String content){

		content = NotationUtils.removeQuotation(content);
		
		logger.info("Entering sendUser...");
		if(!NotationUtils.isLiteral(content)) {
			// send the message as it is
			this.serverMessage.setMsg(content);
			return true;
		}
		
		// then it is a literal
		try{
			String jsonMessage = Journey.toJson(content);
			this.serverMessage.setMsg(jsonMessage);
			return true;
		} 
		catch (Exception e) {/* it is not a journey */}
		
		// then send it as it is
		this.serverMessage.setMsg(content);
		return true;
	}
	
	

	/**
	 * @return the serverMessage
	 */
	public MessageSignal getServerMessage() {
		return serverMessage;
	}

	/**
	 * Auxiliar class, acting as buffer, to perform the exchange of data 
	 * between the server and the model, in a safe way. 
	 *
	 * Project: TEPRJason
	 * Package: es.upm.dit.gsi.sojason
	 * Class: MessageSignal
	 *
	 * @author Miguel Coronado (miguelcb@dit.upm.es)
	 *
	 */
	public class MessageSignal {
		
		private String msg = null;
		private boolean change = false;
		
		public synchronized String getMsg(){
			this.change = false;			
			return msg;
		}
		
		public synchronized void setMsg(String msg){
			this.change = true;
			this.msg = msg;
		}
		
		public synchronized boolean hasChanged(){
			return change;
		}
	}
	
}
