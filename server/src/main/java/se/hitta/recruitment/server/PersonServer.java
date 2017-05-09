package se.hitta.recruitment.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class PersonServer 
{
	public static void main(String[] args) throws Exception
	{
		Server server = new Server(8080);
		
		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
		context.addServlet(PersonServlet.class, "/person/*");
		
		HandlerCollection handlers = new HandlerCollection();
		handlers.setHandlers(new Handler[] { context, new DefaultHandler() });
		server.setHandler(handlers);
				
		server.start();
		
		System.out.println("Person server started!");

		server.join();
	}
}