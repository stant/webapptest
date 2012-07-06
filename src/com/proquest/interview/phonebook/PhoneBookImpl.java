package com.proquest.interview.phonebook;

import java.util.List;

import com.proquest.interview.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PhoneBookImpl implements PhoneBook {
	public List people;
	
        static String spaces14 = "              ";
        static String spaces30 = "                              ";
        static String spaces50 = "                                                  ";
        
        PreparedStatement addPersonPstmt = null;
        PreparedStatement findPersonPstmt = null;
        int rc = 0;
        
        String addPersonSql =
            "INSERT INTO PHONEBOOK (NAME, PHONENUMBER, ADDRESS) VALUES( ?, ?, ? )";

        String findPersonSql =
            "SELECT * FROM PHONEBOOK where NAME = ?";

        String findLikePersonSql =
            "SELECT * FROM PHONEBOOK where NAME like ?";

	@Override
	public void addPerson(Person newPerson) {
                Connection conn = null;

                try {
                    conn = DatabaseUtil.getConnection();
                    conn.setAutoCommit(false);
                    addPersonPstmt = conn.prepareStatement( addPersonSql );

                    addPersonPstmt.setString( 1, newPerson.getName() );
                    addPersonPstmt.setString( 2, newPerson.getPhoneNumber() );
                    addPersonPstmt.setString( 3, newPerson.getAddress() );
                    rc = addPersonPstmt.executeUpdate();
                    if ( rc != 1 )
                        {
                        System.err.print( "*** Error: could not add person >" + newPerson.getName() + "<" );
                        conn.rollback();
                        }
                    addPersonPstmt.close();
                    conn.commit();
                    } 
                catch (SQLException e ) 
                    {
                    if (conn != null) 
                        {
                        try {
                            System.err.print("Transaction is being rolled back");
                            conn.rollback();
                            }
                        catch(SQLException excep) 
                            {
                            System.err.print( "*** Error: on rollback !" );
                            }
                        }
                    }
                catch (ClassNotFoundException ec ) 
                    {
                    System.err.print( "*** Error: Class not found getting db connection !" );
                    }
                finally 
                    {
                    try {
                        if (addPersonPstmt != null) 
                            {
                            addPersonPstmt.close();
                            }
                        if (conn != null) 
                            {
                            conn.close();
                            }
//                        conn.setAutoCommit(true);
                        }
                    catch(SQLException excep) 
                        {
                        System.err.print( "*** Error: on finally clean up !" );
                        }                        
                    }
	}
	
	@Override
	public Person findPerson( String firstName, String lastName ) 
            {
            Connection conn = null;
            ResultSet rset = null;
            Person person = new Person();
            
            try {
                conn = DatabaseUtil.getConnection();

                if ( firstName == null && lastName == null )
                    {
                    return new Person();
                    }
                
                if ( ( firstName.indexOf( '%' ) >= 0 )  ||
                     ( firstName.indexOf( '_' ) >= 0 )  ||
                     ( lastName.indexOf( '%' ) >= 0 )  ||
                     ( lastName.indexOf( '_' ) >= 0 ) )
                    {
                    findPersonPstmt = conn.prepareStatement( findLikePersonSql );
                    //System.out.print( "using sql >" + findLikePersonSql + "<" );
                    }
                else
                    {
                    findPersonPstmt = conn.prepareStatement( findPersonSql );
                    //System.out.print( "using sql >" + findPersonSql + "<" );
                    }
                //System.out.print( "find person >" + (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName) + "<" );
                findPersonPstmt.setString( 1, (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName) );
                rset = findPersonPstmt.executeQuery();
                if ( rset == null )
                    {
                    System.out.print( "*** Error: could not find person >" + firstName + " " + lastName + "<" );
                    }
                else
                    {  // only return 1st person if there are more
                    rset.next();
                    person.setName( rset.getString( DatabaseUtil.PHONEBOOK_TABLE_NAME ) );
                    person.setPhoneNumber( rset.getString( DatabaseUtil.PHONEBOOK_TABLE_PHONENUMBER ) );
                    person.setAddress( rset.getString( DatabaseUtil.PHONEBOOK_TABLE_ADDRESS ) );
                    }
                findPersonPstmt.close();
                } 
            catch (SQLException e ) 
                {
                System.err.print("error in findPerson() sql:\n" + e.getMessage());
                }
            catch (ClassNotFoundException ec ) 
                {
                System.err.print( "*** Error: Class not found getting db connection !" );
                }
            finally 
                {
                try {
                    if (findPersonPstmt != null) 
                        {
                        findPersonPstmt.close();
                        }
                    if (conn != null) 
                        {
                        conn.close();
                        }
                    }
                catch(SQLException excep) 
                    {
                    System.err.print( "*** Error: on finally clean up !" );
                    }                        
                }
            return person;
            }
	
	public ArrayList<Person> findPeople( String name ) 
            {
            Connection conn = null;
            ResultSet rset = null;
            ArrayList<Person> personList = new ArrayList();
            Person person = null;
            
            try {
                conn = DatabaseUtil.getConnection();

                if ( name == null )
                    {
                    return personList;
                    }
                
                //System.out.print( "find person >" + name + "<" );
                if ( ( name.indexOf( '%' ) >= 0 )  ||
                     ( name.indexOf( '_' ) >= 0 )  )
                    {
                    findPersonPstmt = conn.prepareStatement( findLikePersonSql );
                    }
                else
                    {
                    findPersonPstmt = conn.prepareStatement( findPersonSql );
                    }
                findPersonPstmt.setString( 1, name );
                rset = findPersonPstmt.executeQuery();
                if ( rset == null )
                    {
                    System.out.print( "*** Error: could not find people >" + name + "<" );
                    }
                else
                    {
                    while ( rset.next() ) 
                        {
                        person = new Person();
                        person.setName( rset.getString( DatabaseUtil.PHONEBOOK_TABLE_NAME ) );
                        person.setPhoneNumber( rset.getString( DatabaseUtil.PHONEBOOK_TABLE_PHONENUMBER ) );
                        person.setAddress( rset.getString( DatabaseUtil.PHONEBOOK_TABLE_ADDRESS ) );
                        personList.add( person );
                        }
                    }
                findPersonPstmt.close();
                } 
            catch (SQLException e ) 
                {
                System.err.print("error in findPeople()");
                }
            catch (ClassNotFoundException ec ) 
                {
                System.err.print( "*** Error: Class not found getting db connection !" );
                }
            finally 
                {
                try {
                    if (findPersonPstmt != null) 
                        {
                        findPersonPstmt.close();
                        }
                    if (conn != null) 
                        {
                        conn.close();
                        }
                    }
                catch(SQLException excep) 
                    {
                    System.err.print( "*** Error: on finally clean up !" );
                    }                        
                }
            return personList;
            }
	
	public static void main(String[] args) {
		DatabaseUtil.initDB();  //You should not remove this line, it creates the in-memory database

		/* TODO: create person objects and put them in the PhoneBook and database
		 * John Smith, (248) 123-4567, 1234 Sand Hill Dr, Royal Oak, MI
		 * Cynthia Smith, (824) 128-8758, 875 Main St, Ann Arbor, MI
		*/ 
                PhoneBook pbook = new PhoneBookImpl();
                
                pbook.addPerson( new Person( "John Smith", "(248) 123-4567", "1234 Sand Hill Dr, Royal Oak, MI" ) );
                pbook.addPerson( new Person( "Cynthia Smith", "(824) 128-8758", "875 Main St, Ann Arbor, MI" ) );
                
		// TODO: print the phone book out to System.out
                System.out.println( "whole phonebook:" );
                ArrayList<Person> personList = pbook.findPeople( "%" );
                // not the best performance but for quick and dirty it makes the output look pretty.
                for ( Person person : personList )
                    {
                    System.out.printf( "%-30s  %-14s  %-50s\n"
                            , person.getName() + spaces30.substring( person.getName().length() )
                            , person.getPhoneNumber() + spaces14.substring( person.getPhoneNumber().length() )
                            , person.getAddress() + spaces50.substring( person.getAddress().length() ) );
                    }
                
		// TODO: find Cynthia Smith and print out just her entry
                System.out.println( "\nfind Cynthia Smith and print out just her entry:" );
                Person person = pbook.findPerson( "Cynthia", "Smith%" );
                System.out.printf( "%-30s  %-14s  %-50s\n"
                            , person.getName() + spaces30.substring( person.getName().length() )
                            , person.getPhoneNumber() + spaces14.substring( person.getPhoneNumber().length() )
                            , person.getAddress() + spaces50.substring( person.getAddress().length() ) );
                
		// TODO: insert the new person objects into the database
                
                // I already added them to the database when I created them.
                //TODO: create person objects and put them in the PhoneBook and database
                // and TODO: insert the new person objects into the database
                // was confusing. If they are both going to the db, what's the difference.
                //I would ask the business person to clarify that for me.
                // Stan Towianski
                
	}
}
