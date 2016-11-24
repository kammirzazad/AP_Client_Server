/*
 *	Environment.java
 *
 *	- defines and implements "Environment" class
 *
 *	Author      : Kamyar Mirzazad ( kammirzazad@ee.sharif.edu )
 *	Created  On : May 17th , 2014
 *	Modified On : Jun  8th , 2014
 */

import java.io.*;
import java.util.ArrayList;

public 	class 	Environment
{
	private int			_max_users;
    	private int			_num_users;

	private	int			_turns_left;
	private int			_daesh_interval;

	private	boolean			_stop;
	private	boolean			_ongoing_pass;
	private	boolean			_ongoing_move;
	private	boolean			_ongoing_change;
	private boolean			_is_game_started;
	private boolean	[]		_is_turn_finished;

	private	int			_last_rebel_id;
	private	int	[]		_last_agent_id;

	private	String 			_print;
	private String	[]		_users;
	
	private Map			_map;
	private ArrayList<Agent>	_agents;  
	private ArrayList<Department>	_departments;	

	private	boolean	can_rebels_go( int x , int y )
	{
		if( !_map.is_valid_place(x,y) ) { return false; }

		for( int i=0; i<_departments.size(); i++ )
		{
			if( 
			    ( x >= _departments.get(i).get_top_left_x()-1 ) 					&& 
			    ( x <= _departments.get(i).get_top_left_x()+_departments.get(i).get_length()+1 ) 	&&
			    ( y >= _departments.get(i).get_top_left_y()-1 ) 					&& 
			    ( y <= _departments.get(i).get_top_left_y()+_departments.get(i).get_width()+1 )
                          ) 
			  { return false; }							
		}

		return true;
	}

	private void	update_cell( int i , int x , int y )
	{
		if( _map.get_owner_id(x,y) == _departments.get(i).get_owner_id() )
		{
			if( !( ( x == _departments.get(i).get_entrance_x() ) && ( y == _departments.get(i).get_entrance_y() ) ) )
			{
				_departments.get(i).increase_power( _map.get_power(x,y) );	
			}
		}
		else
		{
			     if( _departments.get(i).get_health()  > _map.get_health(x,y) )
			{
				 _departments.get(i).decrease_health( _map.get_health(x,y) );
			}
			else if( _departments.get(i).get_health() == _map.get_health(x,y) )
			{
				 System.out.println( " Team " + Integer.toString(_departments.get(i).get_owner_id()) + " lost department# " + Integer.toString(_departments.get(i).get_id()) ); 
				 _departments.get(i).change_owner(0);		
			}
			else  /* _departments.get(i).get_health()  < _map.get_health(x,y) */
			{	
				 System.out.println( " Team " + Integer.toString(_departments.get(i).get_owner_id()) + " conquered department# " + Integer.toString(_departments.get(i).get_id()) ); 
				 _departments.get(i).change_owner  ( _map.get_owner_id(x,y) );
				 _departments.get(i).increase_power( _map.get_power   (x,y) );
			}
		}

		_map.remove_all_agent_references_from_cell(x,y);
	}

	public  Environment()
	{
		int num_of_cols		= 20;
		int num_of_rows		= 20;
		int department_id	= 0;

		this._max_users		= 4;		/* maximum number of player is limited to 4 */
		this._num_users		= 1;		/* there is always rebel user 		    */
		this._last_rebel_id	= 0;
		this._print		= new String();

		this._stop		= false;
		this._ongoing_move	= false;
		this._ongoing_change	= false;
		this._is_game_started	= false;

		this._agents 		= new ArrayList<Agent>();
		this._departments 	= new ArrayList<Department>();

		try
		{
			BufferedReader reader = new BufferedReader(new FileReader("server.txt"));

			String line = reader.readLine();	/* skip first line */

			if( ( line = reader.readLine() ) == null )
			{
				System.out.println(" server.txt is missing line , initializing environment with default values ");	
			}
			else
			{	
				String [] tokens = line.split(" ");

				num_of_cols	     = Integer.parseInt(tokens[0]);
				num_of_rows	     = Integer.parseInt(tokens[1]);
				this._max_users      = Integer.parseInt(tokens[2]);
				this._daesh_interval = Integer.parseInt(tokens[3]);
			}

			while( ( line = reader.readLine() )!= null )
			{
				String [] tokens = line.split(" ");
				_departments.add( new Department( ++department_id , Integer.parseInt(tokens[0]) , Integer.parseInt(tokens[1]) , Integer.parseInt(tokens[2]) , Integer.parseInt(tokens[3]) , Integer.parseInt(tokens[4]) , Integer.parseInt(tokens[5]) , Integer.parseInt(tokens[6]) , Integer.parseInt(tokens[7]) , Integer.parseInt(tokens[8]) ) );
			}

		}
		catch( FileNotFoundException fe)
		{
			System.out.println(" server.txt not found! , initializing environment with default values ");
		}
		catch( IOException ioe )
		{
			System.err.println( ioe );
		}

		this._last_agent_id	= new int    [_max_users  ];
		this._users		= new String [_max_users  ];
		this._is_turn_finished  = new boolean[_max_users+1];

		for( int i=0; i<_max_users  ; i++ ) { _last_agent_id[i] = 0 ; _users[i] = "";}
		for( int i=0; i<_max_users+1; i++ ) { get_set_reset_turn(i,1); 	     	     }

		this._map		= new Map( num_of_cols , num_of_rows , _departments );
		this.refresh_print();
	}

	public	int	get_user_count	 () { return _num_users;	     }

	public  int 	get_rebel_count	 () { return _last_rebel_id;	     }

	public	int	get_winner_id	 () { return _departments.get(0).get_owner_id(); }

	public	void	start_game	 () {  this._is_game_started = true; }

	public	boolean	is_game_started	 () { return this._is_game_started;  }

	public  synchronized boolean  get_set_reset_turn ( int _user_id , int flag ) {  if(flag==1){_is_turn_finished[_user_id] = true;} if(flag==2){_is_turn_finished[_user_id] = false;} return _is_turn_finished[_user_id];}

	public  void	reset_turn	 () 
	{ 
		refresh_print(); 

		//get_increment_reset_turn(2);
		for( int i=0; i<_max_users+1; i++ ) { get_set_reset_turn(i,2); }

		_stop=false; 
	}	

	public	void	stop_turn	 ()
	{
		_stop = true;
		//get_set_reset(1);
		/* wait for end of all ongoing actions */
		while(  _ongoing_move || _ongoing_change );
	}

	public	String	print		 () { while(_stop); return this._print; }

	public	String	get_winner_name	 () { return _users[get_winner_id()];   }

	public	boolean	is_turn_finished () 
	{
		for( int i=0; i<_max_users; i++ ) { if( !( _users[i].equals("") || get_set_reset_turn(i+1,0) ) ){ return false; } }

		return true;
	}

	public	boolean	wait_for_new_turn( int _user_id ) { while(get_set_reset_turn(_user_id,0)); return is_game_finished(); } /* wait until game server resets _turn */

	public	boolean	is_game_finished () 
	{
		int winner = _departments.get(0).get_owner_id();

		if( winner == 0 ) { return false; }

		for( int i=1; i<_departments.size(); i++ )
		{
			if( winner != _departments.get(i).get_owner_id() ) { return false; }
		}
	
		return	true;
	}

	public 	boolean	can_rebel_agent_go( int agent_id , int x , int y )
	{
	       	for( int i=0 ; i<_agents.size(); i++ )
		{
			if( ( _agents.get(i).get_owner_id() == 0 ) && ( _agents.get(i).get_id() == agent_id ) )
			{
				return can_rebels_go( _agents.get(i).get_x()+x , _agents.get(i).get_y()+y ); 
			}				
		}

		/* return true for dead agents */

		return true;		 
	}
 
	public	void	add_rebel_to_game() 
	{ 
		_turns_left--;

		if( _turns_left != 0 ) { return; }

		_turns_left = _daesh_interval;
 
		int x = (int)( Math.floor( _map.get_length()*Math.random() ) );
		int y = (int)( Math.floor( _map.get_width()*Math.random() ) );

		while( !can_rebels_go( x , y ) )
		{
			x = (int)( Math.floor( _map.get_length()*Math.random() ) );
			y = (int)( Math.floor( _map.get_width()*Math.random() ) );
		}

		Agent daesh;

		switch ( (int)( Math.floor( 2.0*Math.random() ) ) )
		{
			case 0	:	daesh = new Student ( 0 , ++_last_rebel_id , x , y );

			case 1	:	daesh = new TA	    ( 0 , ++_last_rebel_id , x , y );

			default	:	daesh = new Prof    ( 0 , ++_last_rebel_id , x , y );
		}

		_agents.add( daesh );	
		_map.add_agent_reference_to_map( daesh );	
	}

	public  void    ask_all_departments_to_generate_agent ()
	{
		for( int i=0; i<_departments.size(); i++ )
		{
			_last_agent_id[_departments.get(i).get_owner_id()] = 
			_departments.get(i).generate_agent( _last_agent_id[_departments.get(i).get_owner_id()] , _agents  , _map );
		}
	}

	public  void 	refresh_all_agents()
	{
		for( int i=0; i<_agents.size(); i++ ) 
		{	
			if( _agents.get(i).refresh() == false ){ _map.remove_agent_reference_from_map( _agents.get(i) ); }
		}	
	}	

	public	void	refresh_all_departments()
	{
		/* check for agents present at call next to each department's neighbourhood */
		for( int i=0; i<_departments.size(); i++ )
		{
			int x_start = _departments.get(i).get_top_left_x()-1;
			int x_end   = _departments.get(i).get_top_left_x()+_departments.get(i).get_length()+1;

			int y_start = _departments.get(i).get_top_left_y()-1;
			int y_end   = _departments.get(i).get_top_left_y()+_departments.get(i).get_width()+1;

			for( int xx = x_start; xx <x_end; xx++ ) { if(_map.is_valid_place(xx,y_start)){ update_cell( i , xx , y_start ); }	}

			for( int xx = x_start; xx <x_end; xx++ ) { if(_map.is_valid_place(xx,y_end  )){ update_cell( i , xx , y_end   ); }	}			

			for( int yy = y_start; yy <y_end; yy++ ) { if(_map.is_valid_place(x_start,yy)){ update_cell( i , x_start , yy ); }	}

			for( int yy = y_start; yy <y_end; yy++ ) { if(_map.is_valid_place(x_end  ,yy)){ update_cell( i , x_end   , yy ); }	}
		}
	}

	public	boolean	change_department_product ( int owner_id , int building_id , char desired_type )
	{
		while(_stop);	/* stops all users if turn is changing */
		_ongoing_change = true;

		for( int i=0; i<_departments.size(); i++ )
		{
			if( ( _departments.get(i).get_owner_id() == owner_id ) && ( _departments.get(i).get_id() == building_id ) )
			{
				_departments.get(i).change_product( desired_type );
				_ongoing_change = false;
				return true;
			}
		}

		_ongoing_change = false;
		return false;
	} 

	public	void	refresh_print()
	{
		String print_stream = "";
		
		/* print general information */
		
		print_stream += _map.print();
		print_stream += ( Integer.toString( _num_users-1 ) + " " );

		/* print departments */

		print_stream += ( Integer.toString( _departments.size() ) + " " );				

		for( int i=0; i<_departments.size(); i++ )
		{
			print_stream += _departments.get(i).print();
		}

		/* print agents */

		for( int i=0 ;i<=_max_users; i++ )
		{
			int count = 0;

			for( int j=0; j<_agents.size(); j++ ) { if( _agents.get(j).get_owner_id() == i ) { count++; } }
		
			if( i != 0 )
			{
				if( !_users[i-1].equals("") )
				{ 
					print_stream += ( "team "   + Integer.toString(i) + " " + Integer.toString(count) + " " );
				}
			}
			else	/* team zero presents rebels */
			{
				print_stream += ( "team d " + Integer.toString(count) + " " );
			}

			for( int j=0; j<_agents.size(); j++ ) { if( _agents.get(j).get_owner_id() == i ) { print_stream += _agents.get(j).print(); } }
		}

		this._print = print_stream;	
	}
  
	public	synchronized	int	add_remove_user    	( String name , boolean add_or_remove )
    	{
        	if( add_or_remove ) /* add user */
        	{
	    		for( int i=0; i<_max_users; i++ )
            		{
				/* allocate "i" th slot to new user and return "i"      */
	         		if( _users[i].equals(  "") ) 
				{ 
					_users[i] = name; 
					//get_increment_decrement(1); 
					_num_users++;
					_departments.get(i).change_owner(i+1); 
					return  i+1; 
				} 

				/* if there is already user with given name , return 0 */
                 		if( _users[i].equals(name) ) { return 0; } 
            		}

            		return 0;
        	}

        	/* remove user - always returns 0 */

        	for( int i=0; i<_max_users; i++ ) {  if( _users[i].equals( name ) ) { _users[i] = ""; _num_users--;} }

		return 0;
    	}	

	public  synchronized	boolean	move_agent( int owner_id , int agent_id , String direction )
	{
		while(_stop);
		_ongoing_move = true;

		for( int i=0; i<_agents.size(); i++ )
		{
			if( ( _agents.get(i).get_owner_id() == owner_id ) && ( _agents.get(i).get_id() == agent_id ) )
			{
				boolean b = _agents.get(i).move(direction , _map);
				_ongoing_move = false;
				return b;
			}
		}

		_ongoing_move = false;
		return false;
	}
}
