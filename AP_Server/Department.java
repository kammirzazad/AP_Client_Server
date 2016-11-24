/*
 *	Department.java
 *
 *	- defines and implements "Department" class
 *
 *	Author      : Kamyar Mirzazad ( kammirzazad@ee.sharif.edu )
 *	Created  On : May 17th , 2014
 *	Modified On : Jun  8th , 2014
 */

import java.util.ArrayList;

public 	class 	Department
{
	private	int	_id;

	private final	int	_x_start;
	private	final	int	_y_start;

	private final	int	_x_entrance;
	private	final	int	_y_entrance;

	private	final	int	_number_of_cols;
	private	final	int	_number_of_rows;

	private	final	int	_health;
	private	final	int	_interval;
	private	final	int	_upgrade_cost;

	private	int 	_power;
	private int	_level;
	private int 	_owner_id;
	private int	_invader_id;
	private	int	_turn_left;
	private	int	_current_health;
	private	char	_product_type;

	public 	Department(	int id			,
				int x_start		, int y_start		,
				int x_entrance		, int y_entrance	,	 
				int number_of_cols	, int number_of_rows	,
				int health		,
				int interval		,
				int upgrade_cost	
			  )
	{
		/* constant attributes */

		this._id		= id;

		this._x_start		= x_start;
		this._y_start		= y_start;
		this._x_entrance	= x_entrance;
		this._y_entrance	= y_entrance;
		this._number_of_cols	= number_of_cols;
		this._number_of_rows	= number_of_rows;

		this._health		= health;
		this._interval		= interval;
		this._upgrade_cost	= upgrade_cost;

		/* other attributes */

		this._level		= 1;
		this._owner_id		= 0;				
		this._turn_left		= _interval;
		this._product_type	= 'S';	/* Departmens generate student by default */
		this._current_health	= _health;
		this._power		= 0;
	}

	public	int	get_id	       (){ return this._id;	   }

	public  int     get_owner_id   (){ return this._owner_id;  }

	public	int	get_health     (){ return this._health;	   }

	public	int	get_power      (){ return this._power; }

	public	int	get_width      (){ return this._number_of_rows; }
	
	public	int	get_length     (){ return this._number_of_cols; }

	public	int	get_top_left_x (){ return this._x_start;    }
	
	public	int	get_top_left_y (){ return this._y_start;    }

	public	int	get_entrance_x (){ return this._x_entrance; }
	
	public	int	get_entrance_y (){ return this._y_entrance; }	

	public	void	increase_power ( int power    ) 
	{ 
		this._power += power; 

		if( _power > _upgrade_cost ) { _power -= _upgrade_cost; _level++; }
	}

	public  void	decrease_health( int health   ) { this._current_health -= health; }

	public	void	change_product ( char type    ) { this._product_type = type;	  }

	public	void    change_owner   ( int owner_id ) 
	{
		this._power		= 0;  
		this._owner_id 		= owner_id;
		this._turn_left		= _interval; 
		this._product_type	= 'S'; 
		this._current_health	= _health;
	}

	public	int	generate_agent ( int _last_agent_id , ArrayList<Agent> _agents , Map _map )
	{
		if( _owner_id == 0 )	{ return _last_agent_id;  }	/* departments without owner can not generate agent */		

		_turn_left--;

		if( _turn_left == 0 )
		{ 
			_turn_left = _interval; 

			if( _product_type == 'T' ) 
			{ 
				for( int i=0; i<_level; i++ )
				{
					Agent _a = new TA      ( _owner_id , ++_last_agent_id , _x_entrance , _y_entrance );
	
					_agents.add( _a );	
					_map.add_agent_reference_to_map( _a );
				}
			}	

			if( _product_type == 'P' ) 
			{ 
				for( int i=0; i<_level; i++ )
				{
					Agent _a = new Prof    ( _owner_id , ++_last_agent_id , _x_entrance , _y_entrance );

					_agents.add( _a );	
					_map.add_agent_reference_to_map( _a );
				}
			}	

			if( _product_type == 'S' ) 
			{ 
				for( int i=0; i<_level; i++ )
				{
					Agent _a = new Student ( _owner_id , ++_last_agent_id , _x_entrance , _y_entrance );

					_agents.add( _a );	
					_map.add_agent_reference_to_map( _a );
				}
			}		
		}

		return _last_agent_id;
	}        		

	public	String	print	    ()
	{
		String owner = " team=" + ( ( _owner_id == 0 )? "x" :  Integer.toString(_owner_id) );

		return  
		( Integer.toString(            _id) + " " 
		+ Integer.toString(       _x_start) + " " + Integer.toString(       _y_start) + " " 
		+ Integer.toString(_number_of_cols) + " " + Integer.toString(_number_of_rows) + " "
	 	+ 			      owner + " " 
		+ Integer.toString(    _x_entrance) + " " + Integer.toString(    _y_entrance) 
		+ " p=" + 	     _product_type  + " " 
		+ Integer.toString(	    _power) + " " 
		+ Integer.toString(         _level) + " " 
		+ Integer.toString(_current_health) + " "
		);
	}
}
