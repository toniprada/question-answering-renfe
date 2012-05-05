/**
 * 
 */
package es.upm.dit.gsi.httpserver.handlers;

import jason.asSyntax.Literal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import es.upm.dit.gsi.sojason.TEPRModel;

/**
 *
 * Project: TEPRJason
 * Package: es.upm.dit.gsi.httpserver.handlers
 * Class: QAHandler
 *
 * @author Miguel Coronado (miguelcb@dit.upm.es)
 * @version Mar 27, 2012
 *
 */
public class QAHandler implements HttpHandler {

	/** The model where we put the incoming message */
	private TEPRModel model;
	/** the logger */
	private Logger logger = Logger.getLogger("TEPRJason." + QAHandler.class.getName());
	
	/**
	 * Constructor
	 * @param model
	 */
	public QAHandler(TEPRModel model) {
		this.model = model;
	}

	/* (non-Javadoc)
	 * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
	 */
	public void handle(HttpExchange exchange) throws IOException {

		Headers headers = exchange.getResponseHeaders();
		headers.add("Connection", "Keep-Alive");
		headers.add("Content-Type", "application/json");
		headers.add("Server", "simpleHttpServer");
		headers.add("Access-Control-Allow-Origin", "*"); // to accept ajax 
										// calls -even from chrome extension

		// Get the question
		@SuppressWarnings("unchecked")
		Map<String, Object> params = (Map<String, Object>)exchange.getAttribute("parameters");
		String question = (String)params.get("q");
		logger.info("Message received:\'" + question + "\'");
		
		Literal qLiteral = Literal.parseLiteral("user_msg(\"" + question + "\")");
		model.setDataInbox("userAgent", qLiteral);	// tell the agent
		
		// Wait for response
		while(!this.model.getServerMessage().hasChanged()){
			try { Thread.sleep(500); } catch (InterruptedException e) {};
		}
		
		String response = "";
		synchronized (this.model.getServerMessage()) {
			response = this.model.getServerMessage().getMsg();
		}
		
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();

	}

}
