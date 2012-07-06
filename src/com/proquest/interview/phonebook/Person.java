package com.proquest.interview.phonebook;

public class Person {
	public String name;
	public String phoneNumber;
	public String address;

    public Person ( String name, String phoneNumber, String address )
        {
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.address = address;
        }
        
    // making an assumption here to keep things simple and just have a blank person
    // have blank fields so I don't have to worry about checking for nulls in printouts later.
    public Person ()
        {
            this.name = "";
            this.phoneNumber = "";
            this.address = "";
        }
        
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
        
        
}
