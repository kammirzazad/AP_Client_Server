/*
 *  Sample client code for AP Project Clients
 *
 *  Author : Kamyar Mirzazad ( kammirzazad@ee.sharif.edu )
 */

#include <QCoreApplication>

#include <cstdlib>
#include <string>
#include <iostream>
#include "../client_proxy.h"

int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);

    char nmap[10000];

    std::string username , command;

    std::cout<< " Enter username : ";
    std::cin >> username;
    std::getline(std::cin,command);

    client_proxy _client(username);

    std::cout<< " Logging into local game server ... " << std::endl;

    while( !_client.login("127.0.0.1") )  /* use loopback for local game server */
    {
        std::cout << " An error occured : Failed to log into game server " << std::endl;

        std::cout << " Do you want to retry ? (yes/no) " <<std::endl;
        std::cin  >> command;

        if( command == "no" ){ std::cout << " Exiting client app ....  " << std::endl; std::exit(1); }

        _client.logout();
    }

    std::cout << " Successfully logged into game server " << std::endl;   

    _client.print_map(); /* you may also use _client.get_map(nmap);  std::cout << nmap; */

    while( true )
    {
        std::cout << std::endl;
        std::cout << ">>" ;        
        std::getline(std::cin,command);

        if( command == "exit" ) { break; }

        if( command != "wait" )
        {
            if( _client.execute(command) )
            {
                std::cout << " Server returned true  as result of execution " << std::endl;
            }
            else
            {
                std::cout << " Server returned false as result of execution " << std::endl;
            }
        }
        else
        {
            if( _client.wait_for_tick() )
            {
                std::cout << " Game finished " << std::endl;

                if( _client.get_my_id() == _client.get_winner_id() )
                {
                    std::cout << " You won  :) " << std::endl;
                }
                else
                {
                    std::cout << " You lost :( , team#" << _client.get_winner_id() << " won " << std::endl;
                }

                _client.logout();
                return a.exec();
            }

            _client.print_map(); /* you may also use _client.get_map(nmap);  std::cout << nmap; */
        }
    }

    _client.logout();
    exit(0);
    return a.exec();
}
