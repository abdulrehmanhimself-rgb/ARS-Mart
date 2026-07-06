#ifndef SECURITYMANAGER_H
#define SECURITYMANAGER_H

#include <string>

class SecurityManager
{
public:
    static std::string generateCode();
    static void lockSystem(int seconds = 10);
};

#endif
