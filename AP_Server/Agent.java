/*
 *	Agent.java
 *
 *	- defines and implements "Agent" superclass and its subclasses "Student" , "TA", "Prof"
 *
 *	Author      : Kamyar Mirzazad ( kammirzazad@ee.sharif.edu )
 *	Created  On : May 17th , 2014
 *	Modified On : Jun  2nd , 2014
 */

import java.util.ArrayList;

public class Agent
{
	private final	int 	_speed;
	private final	int 	_power;
	private	final	String  _type;

	private	int 	_health;
	private int     _turns_left;
	private	int     _id , _x, _y;
	private	int	_owner_id;
	private	boolean _is_agent_alive;

	public		Agent	     ( int owner_id , int id , int x , int y , int speed , int power , int health , String type )
	{
		/* constant attributes */

		this._speed	      = speed;
		this._power	      = power;
		this._health	      = health;
		this._type	      = type;

		/* other attributes */

		this._x		      = x;
		this._y       	      = y;
		this._id	      = id;
		this._is_agent_alive  = true;
		this._owner_id	      = owner_id;
		this._turns_left      = _speed;
	}

	public	int  	get_x	     ()				{ return _x;		}

	public	int  	get_y	     ()				{ return _y;		}	

	public	int  	get_id	     ()				{ return _id;		}

	public	int	get_power    ()				{ return _power;	}

	public 	int  	get_health   ()	       			{ return _health;	}

	public  int  	get_owner_id () 			{ return _owner_id;	}

	public 	void  	decrease_health ( int _arg ) 		{ _health -= _arg;	}

	public 	void 	die	     ()				{ _health = 0; _is_agent_alive = false;   }

	public  boolean move	     ( String direction , Map map )
	{
		if( _turns_left == 0 ) 
		{ 
			if( direction.equals("U") )
			{ 
				if( map.is_valid_place( _x , _y+1 ) ) 
				{ 
					map.remove_agent_reference_from_map(this);

					if ( map.check_conflict( this, _x , _y+1 ) == false ) /* move only if no conflict */
					{
						_y++;
						map.add_agent_reference_to_map(this); 	
					}
				
					_turns_left--; 
				} 

				return true; 
			}

			if( direction.equals("L") )
			{ 
				if( map.is_valid_place( _x-1 , _y ) ) 
				{ 
					map.remove_agent_reference_from_map(this);

					if ( map.check_conflict( this, _x-1 , _y ) == false ) /* move only if no conflict */
					{
						_x--;
						map.add_agent_reference_to_map(this); 	
					}
				
					_turns_left--; 
				} 

				return true; 
			}

			if( direction.equals("R") )
			{ 
				if( map.is_valid_place( _x+1 , _y ) ) 
				{ 
					map.remove_agent_reference_from_map(this);

					if ( map.check_conflict( this, _x+1 , _y ) == false ) /* move only if no conflict */
					{
						_x++;
						map.add_agent_reference_to_map(this); 	
					}
				
					_turns_left--; 
				} 

				return true; 
			}

			if( direction.equals("D") )
			{ 
				if( map.is_valid_place( _x , _y-1 ) ) 
				{ 
					map.remove_agent_reference_from_map(this);

					if ( map.check_conflict( this, _x , _y-1 ) == false ) /* move only if no conflict */
					{
						_y--;
						map.add_agent_reference_to_map(this); 	
					}
				
					_turns_left--; 
				} 

				return true; 
			}			

			if( direction.equals("UL") )
			{ 
				if( map.is_valid_place( _x-1 , _y+1 ) ) 
				{ 
					map.remove_agent_reference_from_map(this);

					if ( map.check_conflict( this, _x-1 , _y+1 ) == false ) /* move only if no conflict */
					{
						_x--;
						_y++;
						map.add_agent_reference_to_map(this); 	
					}
				
					_turns_left--; 
				} 

				return true; 
			}

			if( direction.equals("UR") )
			{ 
				if( map.is_valid_place( _x+1 , _y+1 ) ) 
				{ 
					map.remove_agent_reference_from_map(this);

					if ( map.check_conflict( this, _x+1 , _y+1 ) == false ) /* move only if no conflict */
					{
						_x++;
						_y++;
						map.add_agent_reference_to_map(this); 	
					}
				
					_turns_left--; 
				} 

				return true; 
			}

			if( direction.equals("DL") )
			{ 
				if( map.is_valid_place( _x-1 , _y-1 ) ) 
				{ 
					map.remove_agent_reference_from_map(this);

					if ( map.check_conflict( this, _x-1 , _y-1 ) == false ) /* move only if no conflict */
					{
						_x--;
						_y--;
						map.add_agent_reference_to_map(this); 	
					}
				
					_turns_left--; 
				} 

				return true; 
			}

			if( direction.equals("DR") )
			{ 
				if( map.is_valid_place( _x+1 , _y-1 ) ) 
				{ 
					map.remove_agent_reference_from_map(this);

					if ( map.check_conflict( this, _x+1 , _y-1 ) == false ) /* move only if no conflict */
					{
						_x++;
						_y--;
						map.add_agent_reference_to_map(this); 	
					}
				
					_turns_left--; 
				} 

				return true; 
			}

			System.out.println(" Error : Unknown direction : " + direction );
			return false;
		}

		return false;
	}

	public 	String 	print	     ()	{ return ( Integer.toString(_id) + " " + Integer.toString(_x) + " " + Integer.toString(_y) + _type + Integer.toString(_health) + " " ); }	

	public	boolean refresh	     ()	{ if(_turns_left == 0){ _turns_left=_speed; } _turns_left--; return _is_agent_alive; }	
}

class  TA       extends Agent	{	public TA     ( int owner_id , int id , int x , int y )	{  super( owner_id , id , x , y , 3 , 2 , 1 , " T " );	}	}

class  Prof     extends Agent	{	public Prof   ( int owner_id , int id , int x , int y ) {  super( owner_id , id , x , y , 1 , 3 , 2 , " P " );	}	}

class  Student  extends Agent	{	public Student( int owner_id , int id , int x , int y )	{  super( owner_id , id , x , y , 2 , 1 , 3 , " S " );  }	}

