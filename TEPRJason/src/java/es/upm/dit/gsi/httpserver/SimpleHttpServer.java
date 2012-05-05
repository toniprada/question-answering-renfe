package es.upm.dit.gsi.httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * <p>The implementation of the server is not concurrent, so calls that
 * are not terminated may block the server permanently.</p> 
 *
 * Project: TEPRJason
 * Package: es.upm.dit.gsi.httpserver
 * Class: SimpleHttpServer
 *
 * @author Miguel Coronado (miguelcb@dit.upm.es)
 * @version Mar 13, 2012
 *
 */
public class SimpleHttpServer {

	/** integer */
	public final static int DEFAULT_PORT = 8000;
	/** the server */
	private HttpServer server;
	/** the logger */
	private Logger logger = Logger.getLogger("TEPRJason." + SimpleHttpServer.class.getName());
	
	/**
	 * <p>This creates the server but does not starts it</p>
	 *   
	 * @param port				the port
	 * @param defaultContext	the path to which the default handler should 
	 * 							be bind. If the <code>defaultContext</code> if 
	 * 							<tt>null</tt> no default handler is set.
	 */
	public SimpleHttpServer (int port, String defaultContext) {
		
		try{
	    	if (port > 0) {
	    		this.server = HttpServer.create(new InetSocketAddress(port), 0);
	    	}
	    	else {
	    		this.server = HttpServer.create(new InetSocketAddress(DEFAULT_PORT), 0);
	    	}
	        server.createContext(defaultContext, new DefaultHandler());

		}catch(Exception e){
			e.printStackTrace();
		}
    	
    }

	/**
	 * This starts the server
	 */
	public void start(){
		this.server.setExecutor(null); // creates a default executor
		logger.info("Running server...");
		server.start(); // start the server
	}

	/**
	 * <p>This creates a new context with the path given and handled by the
	 * <code>HttpHandler</code> given. By default it adds the 
	 * {@link es.upm.dit.gsi.httpserver.ParameterFilter}. If you need to add 
	 * other filters use the {@link #addContext(String, HttpHandler, Filter...)}
	 * method.</p>
	 * 
	 * <p>If the context path is null or empty, or the handler is null nothing 
	 * is done.</p>
	 * 
	 * <p>If a path has already be bind to a different handler calling this method
	 * with that path has no effect.</p>
	 * 
	 * @param contextPath	the path
	 * @param handler		The handler
	 */
	public void addContext(String contextPath, HttpHandler handler){
		addContext(contextPath, handler, new ParameterFilter());
	}
	
	/**
	 * <p>This creates a new context with the path given and handled by the
	 * <code>HttpHandler</code> given. 
	 * 
	 * <p>If the context path is null or empty, or the handler is null nothing 
	 * is done. If any of the filters is null it is skipped</p>
	 * 
	 * <p>If a path has already be bind to a different handler calling this method
	 * with that path has no effect.</p>
	 * 
	 * @param contextPath	the path
	 * @param handler		the handler
	 * @param filters		array of filters
	 */
	public void addContext(String contextPath, HttpHandler handler, Filter... filters){
		if(contextPath == null || contextPath.equals("") || handler == null){
			return;
		}
		HttpContext context = this.server.createContext(contextPath, handler);
		// Add the filters
		for(Filter filter : filters){
			if(filter != null){
				context.getFilters().add(filter);
			}
		}
	}
	
	/**
	 *
	 * Project: HttpServer
	 * Package: es.upm.dit.gsi.httpserver
	 * Class: MyHandler
	 *
	 * @author Miguel Coronado (miguelcb@dit.upm.es)
	 * @version Mar 13, 2012
	 *
	 */
    static class DefaultHandler implements HttpHandler {
    	
        public void handle(HttpExchange exchange) throws IOException {

            Headers headers = exchange.getResponseHeaders();
            headers.add("Connection", "Keep-Alive");
            headers.add("Content-Type", "text/html");
            headers.add("Server", "simpleHttpServer");
            
            String response = "<html><head><title>Simpleserver default page</title></head><body><h1>It worked!</h1></body></html>";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            
        }
        
    }
	
    /**
     * Try it
     * @param args nothing to be done with them
     */
    public static void main (String [] args){
    	SimpleHttpServer server = new SimpleHttpServer(8000, "/");
    	server.start();
    }

}
