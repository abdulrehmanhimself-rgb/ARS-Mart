// SecurityManager.cpp - CORRECTED
#include "SecurityManager.h"
#include "Utils.h"
#include <cstdlib>
#include <iostream>

using namespace std;

std::string SecurityManager::generateCode() 
{
    return to_string(rand() % 9000 + 1000);
}

void SecurityManager::lockSystem(int seconds)  
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;41m\033[1;97m SYSTEM LOCKED \033[0m\n\n";

    for (int i = seconds; i > 0; i--)
    {
        cout << "\r\033[1;33mWait " << i << " seconds... \033[0m" << flush;
        Utils::sleepSeconds(1);
    }

    cout << "\n\nSystem Unlocked.\n";
}
