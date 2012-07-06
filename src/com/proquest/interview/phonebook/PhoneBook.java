package com.proquest.interview.phonebook;

import java.util.ArrayList;

public interface PhoneBook {
	public Person findPerson(String firstName, String lastName);
        public ArrayList<Person> findPeople( String name );
	public void addPerson(Person newPerson);
}
