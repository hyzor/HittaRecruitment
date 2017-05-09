package se.hitta.recruitment.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

public class PersonServlet extends HttpServlet 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3330161663073409678L;
	
	// Thread safe concurrent hash map
	private final Map<Integer, Person> personsMap = new ConcurrentHashMap<Integer, Person>();
	
	// Thread safe atomic integer (for ID generation)
	private final AtomicInteger idGen = new AtomicInteger();
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    	final PrintWriter out = resp.getWriter();
    	
    	Integer id = null;
    	String pathInfo = req.getPathInfo();
    	String gender = req.getParameter("gender");
    	boolean success = false;
    	
    	if (pathInfo != null)
    	{
    		id = getIdFromPath(req.getPathInfo());
    	}
    	
    	// User has requested to fetch a specific person ID
    	if (id != null)
    	{
    		success = true;
    		
        	if (personsMap.containsKey(id))
        	{
        		out.write(personsMap.get(id).toString() + "\n");
                resp.setStatus(HttpServletResponse.SC_OK);
        	}
        	else
        	{
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            	out.write("GET: Person was not found!\n");
        	}
    	}
    	
    	// User has requested to fetch persons with a specific gender
    	else if (gender != null)
    	{
    		success = true;
    		
    		Iterator<Entry<Integer, Person>> it = personsMap.entrySet().iterator();
    		boolean genderFound = false;
    		
    		while (it.hasNext())
    		{
    			Map.Entry<Integer, Person> pair = (Map.Entry<Integer, Person>)it.next();
    			Person curPerson = pair.getValue();
    			
    			if (curPerson.gender.equalsIgnoreCase(gender))
    			{
    				out.write(curPerson.toJsonObj().toString() + "\n");
    				genderFound = true;
    			}
    			
    		}
    		
    		if (!genderFound)
    		{
    			out.write("No person with gender " + gender + " was found!\n");
    		}
    		
    	}
    	
    	// Fetch every person
    	else
    	{
    		Iterator<Entry<Integer, Person>> it = personsMap.entrySet().iterator();
    		
    		while (it.hasNext())
    		{
    			Map.Entry<Integer, Person> pair = (Map.Entry<Integer, Person>)it.next();
    			Person curPerson = pair.getValue();
    			out.write(curPerson.toJsonObj().toString() + "\n");    			
    		}
    	}
    	
    	if (success)
    	{
        	out.write("GET: Successfully invoked!\n");
    	}
    	else
    	{
        	out.write("GET: Unsuccessful!\n");
    	}
    	
    }
    
    private Integer getIdFromPath(String pathInfo)
    {
    	String val = pathInfo.replaceAll("[^\\d]", "");
    	
    	if (val.equals(""))
    		return null;
    	
    	return Integer.valueOf(val);
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
    	final PrintWriter out = resp.getWriter();
    	
    	Model rdfModel = getInputRdfModel(req);
    	Person curPerson = null;
    	
    	Integer id = getIdFromPath(req.getPathInfo());
    	
    	if (personsMap.containsKey(id))
    	{
        	for (Resource personRes: rdfModel.filter(null, RDF.TYPE, FOAF.PERSON).subjects())
        	{
        		curPerson = createPerson(rdfModel, personRes);
        		personsMap.put(id, curPerson);
        	}
        	
	    	out.write("PUT: Person with id " + Integer.toString(id) + " updated!\n");
            resp.setStatus(HttpServletResponse.SC_OK);
    	}
    	else
    	{
	    	out.write("PUT: No person with id " + Integer.toString(id) + " was found! Skipping...\n");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    	}
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    	final PrintWriter out = resp.getWriter();
    	
    	Model rdfModel = getInputRdfModel(req);
    	
    	Person lastSuccessfulPerson = null;
    	Person curPerson = null;
    	boolean personFound = false;
    	
    	// Process each person in the Model object
    	for (Resource personRes: rdfModel.filter(null, RDF.TYPE, FOAF.PERSON).subjects())
    	{
    		curPerson = createPerson(rdfModel, personRes);
    		personFound = false;
    		
    		Iterator<Entry<Integer, Person>> it = personsMap.entrySet().iterator();
    		
    		// Does the person already exist?
    		while (it.hasNext())
    		{
    			Map.Entry<Integer, Person> pair = (Map.Entry<Integer, Person>)it.next();
    			
    			if (pair.getValue().equals(curPerson))
    			{
        			out.write(curPerson.getFullName() + " already added! Skipping...\n");
        			personFound = true;
        			break;
    			}
    		}
    		
    		// If it doesn't already exist, add it to our hash map
    		if (!personFound)
    		{
        		lastSuccessfulPerson = curPerson;
        		personsMap.put(curPerson.id, curPerson);
    		}
    	}
    	
    	// Assuming that we can create multiple persons in one go, append the last person created
    	// to the Location header
    	if (lastSuccessfulPerson != null)
    	{
            resp.setStatus(HttpServletResponse.SC_CREATED);
	        resp.addHeader("Location", "http://127.0.0.1:8080/person/" + lastSuccessfulPerson.id);
	    	out.write("POST: One or more Persons successfully created!\n");
    	}
    	else
    	{
    		resp.setStatus(HttpServletResponse.SC_CONFLICT);
	    	out.write("POST: No new person was created!\n");
    	}
    }
    
    private Model getInputRdfModel(HttpServletRequest req) throws RDFParseException, UnsupportedRDFormatException, IOException
    {
    	// Get RDF XML input stream
    	InputStream rdfXmlInput = req.getInputStream();
    	
    	// Parse RDF XML stream into a rdf4j Model object
    	return Rio.parse(rdfXmlInput, "", RDFFormat.RDFXML);
    }
    
    private Person createPerson(Model rdfModel, Resource personRes)
    {
		// Get all relevant FOAF IRI:s
		Optional<Literal> gender = Models.objectLiteral(rdfModel.filter(personRes, FOAF.GENDER, null));
		Optional<Literal> given_name = Models.objectLiteral(rdfModel.filter(personRes, FOAF.GIVENNAME, null));
		Optional<Literal> family_name = Models.objectLiteral(rdfModel.filter(personRes, FOAF.FAMILYNAME, null));
		Optional<Resource> email = Models.objectResource(rdfModel.filter(personRes, FOAF.MBOX, null));
		Optional<Resource> homepage = Models.objectResource(rdfModel.filter(personRes, FOAF.HOMEPAGE, null));
		
		Integer id = idGen.incrementAndGet();
		
		return new Person(id, given_name.get().stringValue(), family_name.get().stringValue(),
				gender.get().stringValue(), email.get().stringValue(), homepage.get().stringValue());
    }
}