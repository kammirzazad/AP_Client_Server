/*
 *  Author      : Kamyar Mirzazad ( kammirzazad@ee.sharif.edu )
 *  Subject     : AP Project Server  
 *  Created  On : May 13, 2014
 *  Modified On : 
*/

import java.net.*;
import java.io.*;
public class AP_Server
{
	public static void main(String[] args) 
        {
		try 
		{
			/* create servers */
			matchmakerServer s0 = new matchmakerServer();
			lspciServer      s1 = new lspciServer();

			/* create threads */
			Thread t0 = new Thread(s0);
			Thread t1 = new Thread(s1);

			/* launch threads */
			t0.start();
			t1.start();


			/* join   threads */
			t0.join();
			t1.join();
		}
               catch (InterruptedException ioe) 
                {
 			System.err.println(ioe);
		}
	}
}

class   department
{



}

class   agent
{

}

class   map 
{
    private char  TA        = 20;
    private char  Student   = 21;
    private char  Professor = 22;

    private int       _max_users;
    private int       _num_users;
    private String [] _users;
    private int       _turn;

    map()
    {
	_max_users = 8;
	_num_users = 0;
	_turn  = 0;
        _users = new String[_max_users];

	for( int i=0; i<_max_users; i++ ) { _users = ""; }
    }

    public int     synchronized add_remove_user    ( String name , boolean add_or_remove )
    {
        if( true ) /* add user */
        {
	    for( int i=0; i<_max_users; i++ )
            {
	         if( !_users[i].equals(  "") ) { _users[i] = name; return  i; } /* allocate "i" th slot to new user and return "i"      */
                 if( !_users[i].equals(name) ) {                   return -1; } /* if there is already user with given name , return -1 */
            }

	    System.out.println(" $ Unable to add new user , limit reached ");
            return -1;
        }

        /* remove user */

        for( int i=0; i<_max_users; i++ ) {  if( _users[i].equals( name ) ) { _users[i] = ""; return 0; } }

	return -1;	/* unable to find given user , return -1 */
    }

    public boolean isMyTurn( int ID ) { return _turn == ID; }

    public    void passTurn() 
    { 
        _turn = (_turn+1)%_max_users;

        while( _users[_turn].equals("") ) { _turn = (_turn+1)%_max_users; }
    }

    /*
    public  move  ( int agent , int x_dst , int y_dst )
    {
	    // move given agent to (x,y) 
    } 
    */

    public     go ( int agent , char dir )
    {
	    /* move given agent in given direction */
    }

    public change ( int building , char to )
    {
	    /* change given building to produce given type */	
    }

}

class   GameServer implements Runnable {
    private ServerSocket sock;
 
    GameServer() {
	try
	{	
		this.sock = new ServerSocket(6060);
	}
	catch (IOException ioe)
	{
		System.err.println(ioe);
	}
    }

    public void run () 
    {
      while(true)
      {
		try
		{
			Socket client = sock.accept();
			GamePort newGame = new GamePort(client);
			Thread t = new Thread(newGame);
			t.start();
		}
		catch (IOException ioe)
		{
			System.err.println(ioe);
		}
      }
    }
}

class   GamePort implements Runnable {

	private char   CONNECT     = 10;
	private char   DISCONNECT  = 11;
	private char   COMMAND     = 12;
	private char   GETMAP      = 13;
	private char   SETMAP      = 14;
	private char   GETRESULT   = 15;
	private char   WAITFORTICK = 16;

	private map    _map;
	private	char   _name[100];
	private Socket client;

	GamePort(Socket client_arg , map _map )
	{
		this.client = client_arg;
	}

	public void run () 
	{

		try
		{
			BufferedReader in   = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter    pout = new PrintWriter(client.getOutputStream(), true);

			/* read request from the socket */
			char request = in.read();

			/* first request should be CONNECT , disconnect client otherwise */
			if( request != CONNECT ) { client.close(); }

						


 							

			/* close the socket and resume */
			client.close();
		}
		catch (IOException ioe)
		{
			System.err.println(ioe);
		}
	}	
}
