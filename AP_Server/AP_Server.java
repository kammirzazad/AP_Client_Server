/*
 *	AP_Server.java
 *
 *	- Advanced Programming course final project's server side code 
 *
 *	Author      : Kamyar Mirzazad ( kammirzazad@ee.sharif.edu )
 *	Created  On : May 17th , 2014
 *	Modified On : Jun  8th , 2014
 */

import java.net.*;
import java.io.*;

public class AP_Server
{
		public static void main(String[] args) 
		{
			try
			{
				Environment	_environment	= new Environment();
				
				System.out.println( " Opening TCP port ..." );
				ServerSocket	_serversock	= new ServerSocket(6060);

				System.out.println( " Launching game server ..." );
				Thread	_thread_game = new Thread( new Game_Server( _environment ) );
				_thread_game.start();

				System.out.println( " Launching rebel user ..." );
				Thread _thread_rebel = new Thread( new      Rebels( _environment ) );
				_thread_rebel.start();

				while( true )
				{
					Socket	_sock 	= _serversock.accept();

					if( _environment.is_game_started() ) { break; } 

					System.out.println( " Connection to client established " );
					
					Thread	__thread =  new Thread( new Client_Interface( _sock , _environment ) );
					__thread.start();
				}				

				_thread_game.join();	/* wait till game finishes */

			}
			catch (IOException | InterruptedException ioe ) 
			{
				System.err.println(ioe);
			}
		}
}

class	Game_Server	implements Runnable
{
	private	int		_timeout;
	private	int		_max_turn;
	private	int		_turn_count;
	private	Environment	_environment;

	public	Game_Server( Environment environment )
	{
		this._timeout	  = 10000;
		this._max_turn	  = 30;
		this._turn_count  = 0;
		this._environment = environment;

		try
		{
			BufferedReader reader = new BufferedReader(new FileReader("server.txt"));

			String	  line   = reader.readLine();

			if( line == null )
			{
				System.out.println(" server.txt is missing line , initializing game server with default values ");	
			}
			else
			{	
				String [] tokens = line.split(" ");

				this._timeout	  = Integer.parseInt(tokens[1]);
				this._max_turn	  = Integer.parseInt(tokens[0]);
			}

		}
		catch( FileNotFoundException fe)
		{
			System.out.println(" server.txt not found! , initializing game server with default values ");
		}
		catch( IOException ioe )
		{
			System.err.println( ioe );
		}
		
	}

	public	void run()
	{
		System.out.println(" Press enter to start the game ");
		try
                {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        System.out.println(reader.readLine());
                }
                catch (IOException ioe)
                {
                        System.err.println(ioe);
                }

		_environment.start_game();

		//while(!_environment.is_turn_finished());	/* wait till first time that every user calls wat_for_new_turn */		
		_environment.reset_turn();

		System.out.println(" Starting turn #0 with " + Integer.toString(_environment.get_user_count()) + " users ... " );		

		try{

		while(true)
		{
			Thread.sleep(_timeout);

			_environment.stop_turn();	
			if( !_environment.is_turn_finished() ) { System.out.println( " Some users did not finished their turn " ); }					
			if( _turn_count == _max_turn ) { System.out.println(" Game is finished : time limit reached "); return; }
			_environment.refresh_all_departments();
			_environment.ask_all_departments_to_generate_agent();
			_environment.refresh_all_agents();
			_environment.add_rebel_to_game();

			if( _environment.is_game_finished() ) 
			{ 
				System.out.println(" Game is finished : user# " + Integer.toString(_environment.get_winner_id()) + " [ " + _environment.get_winner_name() + " ] " ); 
				return; 
			}

			_environment.reset_turn();	
			System.out.println(" Starting turn #"  + Integer.toString(++_turn_count) + " with " + Integer.toString(_environment.get_user_count()) + " users ... ");	
		}

		}
		catch( InterruptedException ioe )
		{
			System.err.println(ioe);
		}
	}
}

class   Rebels		 implements Runnable
{
	private	int		_last_agent_id;
	private	Environment	_environment;

	private	String	select_random_direction() 
	{ 
		switch( (int)( Math.floor( 7.0*Math.random() ) ) )
		{
			case 0 :	return "L";
			case 1 :	return "U";
			case 2 :	return "R";
			case 3 :	return "D";
			case 4 :	return "UL";
			case 5 : 	return "UR";
			case 6 :	return "DL";
			default:	return "DR";
		}
	}

	private int     get_x ( String dir ) 
	{
		switch( dir )
		{
			case "L"  :	return -1;
			case "U"  :	return  0;
			case "R"  :	return  1;
			case "D"  :	return 	0;
			case "UL" :	return -1;
			case "UR" :	return  1;
			case "DL" :	return -1;
			default   :	return  1;
		}		
	}

	private int     get_y ( String dir ) 
	{
		switch( dir )
		{
			case "L"  :	return  0;
			case "U"  :	return  1;
			case "R"  :	return  0;
			case "D"  :	return -1;
			case "UL" :	return  1;
			case "UR" :	return  1;
			case "DL" :	return -1;
			default   :	return -1;
		}		
	}

	public	Rebels( Environment environment )	{	this._environment = environment;	}

	public	void	run ()
	{
		while( !_environment.is_game_finished() )
		{
			_environment.wait_for_new_turn(0);

			for( int i=1; i<_environment.get_rebel_count(); i++ )	  /* agent ids are between 1 and _count */
			{
				String rand = select_random_direction();
					
				while( !_environment.can_rebel_agent_go( i , get_x(rand) , get_y(rand) ) ) { rand = select_random_direction(); }

				_environment.move_agent( 0 , i , rand );
			}

			_environment.get_set_reset_turn(0,1);			
		}
	}

}

class	Client_Interface implements Runnable 
{
	private	int		_my_id;
	private Socket 		_client;
	private	PrintWriter	_pout;
	private	Environment	_environment;
	private BufferedReader	_bin;

	public	Client_Interface( Socket client , Environment environment )
	{
		this._client		= client;
		this._environment	= environment;
	}

	public	void run () 
    	{
		int    _my_id = 0;
		String   user = new String();

		while(true)
		{
			try
			{
	
			this._pout = new PrintWriter(_client.getOutputStream(), true);
			this._bin  = new BufferedReader(new InputStreamReader(_client.getInputStream()));

			user 	   = _bin.readLine();		/* get username */

			_my_id 	   = _environment.add_remove_user( user , true );

			if( _my_id == 0 )
			{
				System.out.println( " User limit reached. Rejecting \"" + user + "\"");
				_pout.println( Integer.toString(_my_id) ); 
				_client.close();
				return;									
			}
			else
			{				
				System.out.println( " \"" + user + "\" has logged in " + Integer.toString(_my_id) );
				_environment.get_set_reset_turn(_my_id,1);
				_environment.wait_for_new_turn(_my_id);
				_pout.println( Integer.toString(_my_id) );
			}										

			boolean result;
			String [] _tokens;

			while(true)
			{
			switch( _bin.readLine() )
			{
				case "DISCONNECT" :	
					_environment.add_remove_user( user , false );
					_pout.println("1");	
					_client.close();
					return; 

				case "MOVE" :
					_tokens = _bin.readLine().split(" ");
					result  = _environment.move_agent( _my_id , Integer.parseInt(_tokens[0]) , _tokens[1] );					
					_pout.println( result ? "1" : "0" );	/* Allow next request */
					break;

				case "CHANGE" :
					_tokens = _bin.readLine().split(" ");
					result  = _environment.change_department_product( _my_id , Integer.parseInt(_tokens[0]) , _tokens[1].charAt(0) );
					_pout.println( result ? "1" : "0" );	/* Allow next request */
					break;

				case "GETMAP" :
					_pout.println( _environment.print());
					break;
		
				case "RESULT" :
					_pout.println( Integer.toString( _environment.get_winner_id() ) );	
					break;

				case "TICK" :	
					_environment.get_set_reset_turn(_my_id,1);
					result = _environment.wait_for_new_turn(_my_id);
					_pout.println( result ? "1" : "0" );	/* Allow next request */
					break;

				default:
					_pout.println("-UNKNOWN-");
					break;
				}

				}
			}
			catch (IOException ioe)
			{				
				System.err.println(ioe);
			}
			catch (NullPointerException s)
			{
				System.out.println(" Connection to user# " + Integer.toString(_my_id) + " [ " + user + " ] lost ");
				_environment.add_remove_user( user , false );
				return;
			}
      		}
    	}	
}

