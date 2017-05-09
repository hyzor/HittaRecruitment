package se.hitta.recruitment.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.json.*;

public class HelloWorld extends HttpServlet
{	
    /**
	 * 
	 */
	private static final long serialVersionUID = -718068643288335974L;
	
	private volatile boolean lastWasPut = false;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        resp.setCharacterEncoding("utf-8");
        if(this.lastWasPut)
        {
            resp.getWriter()
                    .print("{\"Andrea Räksmörgås\": {\"name\": {\"given\": \"Andrea\",\"family\": \"Räksmörgås\"},\"gender\": \"female\",\"email\": [\"a.r@foo.hitta.se\"],\"homepage\": \"http://ar.foo.hitta.se\"}}");

        }
        else
        {
            resp.getWriter()
                    .print("{\"Anders Svensson\": {\"name\": {\"given\": \"Anders\",\"family\": \"Svensson\"},\"gender\": \"male\",\"email\": [\"a.s@foo.hitta.se\"],\"homepage\": \"http://as.foo.hitta.se\"}}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        this.lastWasPut = false;
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.addHeader("Location", "http://127.0.0.1:8080/person/123");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        this.lastWasPut = true;
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
    }
    
    private void createPerson(String givenName, String familyName, String email, String homepage)
    {
    }
}