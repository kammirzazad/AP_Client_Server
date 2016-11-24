#include "client.h"
#include "../client_proxy.h"

           client_proxy::client_proxy              ( std::string username )                           { _client = new client(username);   }

    bool   client_proxy::login                     ( QString     serverIP )                           { return _client->login (serverIP); }   /* login  into server : arguement maybe LOCAL or MAIN */
    void   client_proxy::logout                    ()                                                 { return _client->logout();         }   /* logout from server  */

    int    client_proxy::get_my_id                 ()                                                 { return _client->get_my_id();      }
    int    client_proxy::get_winner_id             ()                                                 { return _client->get_winner_id();  }   /* returns id of winner */
    bool   client_proxy::wait_for_tick             ()                                                 { return _client->wait_for_tick();  }   /* wait for next tick ( returns false if game is finished , otherwise returns true */

    bool   client_proxy::move_agent                ( int agent_id    , std::string direction    )     { return _client->move_agent(agent_id,direction);                      }
    bool   client_proxy::change_department_product ( int building_id , char        desired_type )     { return _client->change_department_product(building_id,desired_type); }

    void   client_proxy::get_map                   ( char* map )                                      { _client->get_map(map);            }  /* ask server for most up-to-date map  */

    void   client_proxy::print_map                 ()                                                 { _client->print_map();             }
    bool   client_proxy::execute                   ( std::string command )                            { return _client->execute(command); }
