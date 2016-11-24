/*
 *	Map.java
 *
 *	- defines and implements "Map" class
 *
 *	Author      : Kamyar Mirzazad ( kammirzazad@ee.sharif.edu )
 *	Created  On : May 17th , 2014
 *	Modified On : May 22th , 2014
 */

import java.util.ArrayList;

public 	class	Map
{
	private	int				_number_of_rows;	
	private	int				_number_of_cols;
	private	ArrayList<ArrayList<Agent>>	_table;	

	public	Map	( int number_of_cols , int number_of_rows , ArrayList<Department> _departments )
	{		
		_number_of_rows   = number_of_rows;
		_number_of_cols	  = number_of_cols;
				       		
		_table = new ArrayList<ArrayList<Agent>>();

		for( int i=0; i<_number_of_rows*_number_of_cols; i++ ) { _table.add( new ArrayList<Agent>() ); }

		for( int i=0; i<_departments.size(); i++ )
		{
			for( int y=0; y<_departments.get(i).get_width(); y++ )
			{
				for( int x=0; x<_departments.get(i).get_length(); x++ )
				{
					int xx = _departments.get(i).get_top_left_x()+x;
					int yy = _departments.get(i).get_top_left_y()+y;					
					_table.set(_number_of_cols*yy+xx,null);
				}			
			}
		}
	}
		
	public  boolean is_valid_place( int x , int y )
	{
		if( ( x < 0 ) || ( y < 0 ) || ( x >= _number_of_cols ) || ( y >= _number_of_rows ) ) { return false; }

		if( _table.get(_number_of_cols*y+x) == null ) { return false; }

		return true;	
	}

	public	int	get_width () { return _number_of_rows; }

	public	int	get_length() { return _number_of_cols; }

	public	int	get_owner_id( int x , int y )
	{	
		if( _table.get(_number_of_cols*y+x) == null ) 
		{ 
			System.out.println(" There is an error : game asked for owner of nonexisitng cell ");
			return 0; 
		} /* entrance of one department can not overlap with another department ? */ 

		if( _table.get(_number_of_cols*y+x).size() == 0 ) { return 0; }

		return _table.get(_number_of_cols*y+x).get(0).get_owner_id();
	}

	public	int	get_power( int x , int y )
	{
		if( _table.get(_number_of_cols*y+x) == null ) 
		{ 
			System.out.println(" There is an error : game asked for power of nonexisitng cell ");
			return 0; 
		} /* entrance of one department can not overlap with another department ? */ 

		int	sum = 0 ;

		for( int i=0; i<_table.get(_number_of_cols*y+x).size(); i++ ) {	sum += _table.get(_number_of_cols*y+x).get(i).get_power();	}

		return sum;
	}

	public	int	get_health( int x , int y )
	{
		if( _table.get(_number_of_cols*y+x) == null ) 
		{ 
			System.out.println(" There is an error : game asked for health of nonexisitng cell ");
			return 0; 
		} /* entrance of one department can not overlap with another department ? */ 

		int	sum = 0 ;

		for( int i=0; i<_table.get(_number_of_cols*y+x).size(); i++ ) {	sum += _table.get(_number_of_cols*y+x).get(i).get_health();	}

		return sum;
	}

	public	void	remove_all_agent_references_from_cell( int x , int y )
	{
		_table.get(_number_of_cols*y+x).clear();
	}

	public	void 	add_agent_reference_to_map ( Agent agent )
	{
		_table.get(_number_of_cols*agent.get_y()+agent.get_x()).add(agent);
	}

	public  boolean	check_conflict( Agent agent , int x , int y )
	{
		if( _table.get(_number_of_cols*y+x).size() == 0 ) { return false; } /* no conflict -> free to move */

		if( _table.get(_number_of_cols*y+x).get(0).get_owner_id() == agent.get_owner_id() ) { return false; }

		for( int i=0; i<_table.get(_number_of_cols*y+x).size(); i++ )
		{
			     if ( _table.get(_number_of_cols*y+x).get(i).get_health() > agent.get_health() )
			{
				  _table.get(_number_of_cols*y+x).get(i).decrease_health( agent.get_health() );
				  agent.die();
				  return true;
			}
			else if ( _table.get(_number_of_cols*y+x).get(i).get_health() == agent.get_health() )
			{
				  _table.get(_number_of_cols*y+x).get(i).die();
				  agent.die();
				  return true;
			}				
			else
			{
				  agent.decrease_health( agent.get_health() );	/* compare agent against next opponent */
			}
		}

		return false;				
	} 

	public	void	remove_agent_reference_from_map ( Agent agent )
	{
		_table.get(_number_of_cols*agent.get_y()+agent.get_x()).remove(agent);
	}

	public	String	print()	/* TODO : check this one more time */
	{
		return ( Integer.toString(_number_of_cols) + " " + Integer.toString(_number_of_rows) + " " );
	}	
}
