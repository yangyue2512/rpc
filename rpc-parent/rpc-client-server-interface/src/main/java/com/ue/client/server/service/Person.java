package com.ue.client.server.service;

/**
 * 实体类
 * @ClassName: Person 
 * @author yangyue
 * @date 2017年10月12日 下午1:48:17 
 *
 */
public class Person {
	
	    private String firstName;
	    private String lastName;

	    public Person() {
	    }

	    public Person(String firstName, String lastName) {
	        this.firstName = firstName;
	        this.lastName = lastName;
	    }

	    public String getFirstName() {
	        return firstName;
	    }

	    public void setFirstName(String firstName) {
	        this.firstName = firstName;
	    }

	    public String getLastName() {
	        return lastName;
	    }

	    public void setLastName(String lastName) {
	        this.lastName = lastName;
	    }
}
