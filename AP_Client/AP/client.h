#ifndef CLIENT_H
#define CLIENT_H

#include <cstring>
#include <cstdlib>  /* - system       */

#include <string>
#include <sstream>  /* - stringstream */
#include <iomanip>  /* - setw         */
#include <iostream>

#include <QString>
#include <QTcpSocket>
#include <QHostAddress>

class client
{
private:

    int           _port;
    int           _my_id;
    int           _map_size;
    char          _response;
    char*         _buffer;
    char*         _map;
    bool          _isConnected;    
    QTcpSocket    _tcpSocket;

    std::string   _request;
    std::string   _username;

    void   get_raw_map               ( char* map , int mapsize );

public:

    client                           ( std::string username );

    bool   login                     ( QString serverIP );         /* login  into server with given IP */
    void   logout                    ();                           /* logout from server  */

    int    get_my_id                 ();
    int    get_winner_id             ();                           /* returns name of winner */
    bool   wait_for_tick             ();                           /* wait for next tick ( returns false if game is finished , otherwise returns true */

    bool   move_agent                ( int agent_id    , std::string direction    );
    bool   change_department_product ( int building_id , char        desired_type );

    void   get_map                   ( char* map );                /* ask server for most up-to-date map  */

    void   print_map                 ();
    bool   execute                   ( std::string command );
};

#endif // CLIENT_H
