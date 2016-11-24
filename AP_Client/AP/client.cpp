#include "client.h"

        client::client( std::string username )
{
        _port           = 6060;
        _map_size       = 1000;
        _isConnected    = false;

        _buffer         = new char[100];
        _map            = new char[10000];
        _username       = username+'\n';
}

bool    client::login( QString serverIP )
{
        if( _isConnected ) { return false; } /* client is already connected to server */

        _tcpSocket.connectToHost( serverIP , _port );
        _tcpSocket.waitForConnected();

        _tcpSocket.write( _username.c_str() , _username.length());
        _tcpSocket.flush();

        _tcpSocket.waitForReadyRead(120*1000);  /* 2 min wait time */
        _tcpSocket.readLine(_buffer,100);

        if( _buffer[0] == '\0' )  { std::cout << " Error : Connection timed-out " << std::endl; exit(1);}

        _my_id = atoi(_buffer);

        if( _my_id == 0 ) {  return false; }

        _isConnected = true;
        return true;
}

void    client::logout()
{
        _request = "DISCONNECT\n" ;

        if( _isConnected )
        {
            _isConnected = false;

            _tcpSocket.write( _request.c_str() , _request.length() ); /* inform server that you are leaving game */
            _tcpSocket.flush();

            _tcpSocket.waitForReadyRead();
            _tcpSocket.readLine(_buffer,100);

            if( _buffer[0] == '\0' )  { std::cout << " Error : Connection timed-out " << std::endl; exit(1);}

            _tcpSocket.disconnectFromHost();
        }
}

bool    client::wait_for_tick()
{
        _request = "TICK\n";

        if( _isConnected )
        {
            _tcpSocket.write( _request.c_str() , _request.length() );
            _tcpSocket.flush();

            _tcpSocket.waitForReadyRead();
            _tcpSocket.readLine(_buffer,100);

            if( _buffer[0] == '\0' )  { std::cout << " Error : Connection timed-out " << std::endl; exit(1);}

            if( _buffer[0] == '0' ) { return false; }

            return true;
        }

        return false;
}

bool    client::move_agent(int agent_id, std::string direction)
{
        _request = "MOVE\n";

        if( _isConnected )
        {
            _tcpSocket.write( _request.c_str() , _request.length() );
            _tcpSocket.flush();

            _request =  std::to_string(agent_id);
            _request += " ";
            _request += direction;
            _request += '\n';

            _tcpSocket.write( _request.c_str() , _request.length() );
            _tcpSocket.flush();

            _tcpSocket.waitForReadyRead();
            _tcpSocket.readLine(_buffer,100);

            if( _buffer[0] == '\0' )  { std::cout << " Error : Connection timed-out " << std::endl; exit(1);}

            if( _buffer[0] == '0' ) { return false; }

            return true;

        }

        return false;
}

bool    client::change_department_product(int building_id, char desired_type)
{
        _request = "CHANGE\n";

        if( _isConnected )
        {
            _tcpSocket.write( _request.c_str() , _request.length() );
            _tcpSocket.flush();

            _request =  std::to_string(building_id);
            _request += " ";
            _request += desired_type;
            _request += '\n';

            _tcpSocket.write( _request.c_str() , _request.length() );
            _tcpSocket.flush();

            _tcpSocket.waitForReadyRead();
            _tcpSocket.readLine(_buffer,100);

            if( _buffer[0] == '\0' )  { std::cout << " Error : Connection timed-out " << std::endl; exit(1);}

            if( _buffer[0] == '0' ) { return false; }

            return true;
        }

        return false;
}

void    client::get_raw_map(char *map, int mapsize)
{
        _request = "GETMAP\n";

        if( _isConnected )
        {
            _tcpSocket.write( _request.c_str() , _request.length() );
            _tcpSocket.flush();

            _tcpSocket.waitForReadyRead();
            _tcpSocket.readLine(map,mapsize);

            if( _map[0] == '\0' )  { std::cout << " Error : Connection timed-out " << std::endl; exit(1);}
        }
}

void    client::get_map( char* map )
{
        this->get_raw_map(this->_map,10000);

        std::string nmap  = "";
        std::string token = "";
        std::string num_of_teams , num_of_departments , num_of_agents;
        std::stringstream smap(this->_map);

        smap >> token;
        nmap += ( token + ' ' );

        smap >> token;
        nmap += ( token + ' ' );

        smap >> num_of_teams;
        nmap += ( num_of_teams + '\n');

        smap >> num_of_departments;
        nmap += ( num_of_departments + '\n');

        for( int i=0; i<std::stoi(num_of_departments); i++)
        {
            //smap >> id;
            smap >> token;
            nmap += ( token + ' ' );

            //smap >> x_start;
            smap >> token;
            nmap += ( token + ' ' );

            //smap >> y_start;
            smap >> token;
            nmap += ( token + ' ' );

            //smap >> length;
            smap >> token;
            nmap += ( token + ' ' );

            //smap >> width;
            smap >> token;
            nmap += ( token + ' ' );

            //smap >> owner;
            smap >> token;
            nmap += ( token + ' ' );

            //smap >> x_entrance;
            smap >> token;
            nmap += ( token + ' ' );

            //smap >> y_entrance;
            smap >> token;
            nmap += ( token + ' ' );

            //smap >> product;
            smap >> token;
            nmap += ( token + ' ' );

            //smap >> power;
            smap >> token;
            nmap += ( token + ' ' );

            //smap >> level;
            smap >> token;
            nmap += ( token + ' ' );

            //smap >> health;
            smap >> token;
            nmap += ( token + '\n');
        }

        for( int i=0; i<std::stoi(num_of_teams)+1; i++ )
        {
            smap >> token;
            nmap += ( token + ' ' );

            smap >> token;
            nmap += ( token + ' ' );

            smap >> num_of_agents;
            nmap += ( num_of_agents + '\n' );

            for( int j=0; j<std::stoi(num_of_agents); j++ )
            {
                //smap >> agent_id;
                smap >> token;
                nmap += ( token + ' ' );

                //smap >> x_start;
                smap >> token;
                nmap += ( token + ' ' );

                //smap >> y_start;
                smap >> token;
                nmap += ( token + ' ' );

                //smap >> product;
                smap >> token;
                nmap += ( token + ' ' );

                //smap >> health;
                smap >> token;
                nmap += ( token + '\n');
            }
        }

        std::strcpy(map,nmap.c_str());
}

int     client::get_my_id()
{
        return _my_id;
}

int     client::get_winner_id()
{
        _request = "RESULT\n";

        if( _isConnected )
        {
            _tcpSocket.write( _request.c_str() , _request.length() );
            _tcpSocket.flush();

            _tcpSocket.waitForReadyRead();
            _tcpSocket.readLine(_buffer,100);

            if( _buffer[0] == '\0' )  { std::cout << " Error : Connection timed-out " << std::endl; exit(1);}

            return atoi(_buffer);
        }

        return 0;
}

bool    client::execute(std::string command)
{
        std::stringstream scommand(command);

        std::string _token;

        scommand >> _token;

        int            _id;
        char           _selected_value;
        std::string    _direction;

        if( std::strcmp(_token.c_str(),"change" ) == 0 )
        {
            scommand >> _id;
            scommand >> _selected_value;
            return this->change_department_product(_id,_selected_value);
        }

        if( std::strcmp(_token.c_str(),"move"   ) == 0 )
        {
            scommand >> _id;
            scommand >> _direction;
            return this->move_agent(_id,_direction);
        }

        std::cout << "#" << _token << " Unknown command \"" << command << "\"" << std::endl;
        return false;
}

void    client::print_map()
{
        #ifdef WINDOWS
            std::system("cls");
        #else // POSIX
            std::system ("clear");
        #endif

        this->get_raw_map(this->_map,10000);

        std::stringstream smap(this->_map);

        int x , y;
        int num_of_teams, num_of_agents, num_of_departments;

        smap >> x;
        smap >> y;

        std::cout << " Map is size of ( " << std::setw(3) << x << "," << std::setw(3) << y << " )" << std::endl;

        smap >> num_of_teams;
        smap >> num_of_departments;        

        std::string id , owner , product , power , level, health;
        int x_start,y_start,length ,width , x_entrance , y_entrance;

        for( int i=0; i<num_of_departments; i++)
        {                
                smap >> id;

                smap >> x_start;

                smap >> y_start;

                smap >> length;

                smap >> width;

                smap >> owner;

                //owner = owner.substr(5);

                smap >> x_entrance;

                smap >> y_entrance;

                smap >> product;

                //product = product.substr(2);

                smap >> power;

                smap >> level;

                smap >> health;

                std::cout << " Department#" << std::setw(2) << id << " : " <<  std::setw(2) << owner << " @( " <<  std::setw(2) << x_entrance << "," <<  std::setw(2) << y_entrance << ") : ( " <<  std::setw(3) << x_start << " - " <<   std::setw(3) << x_start+length << "," <<  std::setw(3) << y_start << " - " <<  std::setw(3) << y_start+width << " ) : [" <<  product << "|" << std::setw(2) << health << "|" <<  std::setw(2) << power << "|" <<  std::setw(2) << level << "]" << std::endl;
        }

        std::string agent_id;

        std::cout << std::endl;

        for( int i=0; i<num_of_teams+1; i++ )
        {
                smap >> id;
                smap >> id;
                smap >> num_of_agents;

                if(num_of_agents!=0)
                {
                    if(id=="d")
                        std::cout << " Daesh's agents : " <<std::endl;
                    else
                        std::cout << " Team#" << i << "'s agents : " << std::endl;
                }

                for( int j=0; j<num_of_agents; j++ )
                {
                    smap >> agent_id;
                    smap >> x_start;
                    smap >> y_start;
                    smap >> product;
                    smap >> health;

                    std::cout << " Agent#" << std::setw(2) << agent_id << " : [ " << product << " ] : ( " <<  std::setw(3) << x_start << "," <<  std::setw(3) << y_start <<  " ) : " << health << std::endl;
                }
        }       
}
