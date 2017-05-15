package se.hitta.recruitment.server;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Person 
{
	public Person(Integer id, String name_given, String name_family, String gender, String email, String homepage)
	{
		this.id = id;
		this.name_given = name_given;
		this.name_family = name_family;
		this.gender = gender;
		this.email = email;
		this.homepage = homepage;
		this.hashCode = getHashCode();
	}
	
	@Override
	public String toString()
	{
		return Integer.toString(id) + " " + name_given + " " + name_family + " " + gender + " " + email + " " + homepage;
	}
	
	public boolean equals(Person other)
	{
		if (this.hashCode.equals(other.hashCode))
			return true;
		
		return false;
	}
	
	public int getHashCode()
	{
		return new String(name_given + name_family + gender + email + homepage).hashCode();
	}
	
	public String getFullName()
	{
		return name_given + " " + name_family;
	}
	
	private String getCleanEmail()
	{
		String[] split = email.split(":");
		String cleanEmail = split[0];
		
		if (split[1] != null)
			cleanEmail = split[1];
		
		return cleanEmail;
	}
	
	public JsonObject toJsonObj()
	{		
		return toJsonObjBuilder().build();		
	}
	
	public JsonObjectBuilder toJsonObjBuilder()
	{
		JsonObjectBuilder objBuilder = Json.createObjectBuilder()
				.add(getFullName(), Json.createObjectBuilder()
						.add("name", Json.createObjectBuilder()
								.add("given", name_given)
								.add("family", name_family))
						.add("gender", gender)
						.add("email", Json.createArrayBuilder()
								.add(getCleanEmail()))
						.add("homepage", homepage));
		
		return objBuilder;
	}
	
	public Integer id;
	public Integer hashCode;
	public String name_given;
	public String name_family;
	public String gender;
	public String email;
	public String homepage;
}