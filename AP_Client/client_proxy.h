#ifndef CLIENT_PROXY_H
#define CLIENT_PROXY_H

class client;

class client_proxy
{
    private:

    client* _client;

    public :

    client_proxy                     ( std::string username );

    bool   login                     ( QString serverIP );          /* login  into server : arguement maybe LOCAL or MAIN */
    void   logout                    ();                            /* logout from server  */

    int    get_my_id                 ();
    int    get_winner_id             ();                            /* returns id of winner */
    bool   wait_for_tick             ();                            /* wait for next tick ( returns false if game is finished , otherwise returns true */

    bool   move_agent                ( int agent_id    , std::string direction    );
    bool   change_department_product ( int building_id , char        desired_type );

    void   get_map                   ( char* map );                 /* ask server for most up-to-date map  */

    void   print_map                 ();
    bool   execute                   ( std::string command );
};

#endif // CLIENT_PROXY_H
