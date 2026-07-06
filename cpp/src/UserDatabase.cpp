#include "UserDatabase.h"
#include <fstream>
#include <sstream>

std::string UserDatabase::getDatabaseFilePath()
{
     std::string paths[] = { "data/users.txt",
};

    for (const auto& path : paths)
    {
        std::ifstream file(path);
        if (file.is_open())
            return path;
    }

    return "data/users.txt";
}

bool UserDatabase::userExists(const User& user)
{
    std::string filePath = getDatabaseFilePath();
    std::ifstream file(filePath);
    std::string line;
    User record;

    if (!file.is_open())
        return false;

    while (std::getline(file, line))
    {
        std::istringstream iss(line);
        if (iss >> record.username >> record.email)
        {
            std::getline(iss >> std::ws, record.password);
            if (record.username == user.username &&
                record.email == user.email &&
                record.password == user.password)
            {
                return true;
            }
        }
    }

    return false;
}

void UserDatabase::addUser(User u)
{
    std::string filePath = getDatabaseFilePath();
    std::ofstream file(filePath, std::ios::app);

    if (!file.is_open())
        return;

    file << u.username << " " << u.email << " " << u.password << std::endl;
}

bool UserDatabase::getUser(std::string username, User& u)
{
    std::string filePath = getDatabaseFilePath();
    std::ifstream file(filePath);
    std::string line;

    if (!file.is_open())
        return false;

    while (std::getline(file, line))
    {
        std::istringstream iss(line);
        if (iss >> u.username >> u.email)
        {
            std::getline(iss >> std::ws, u.password);
            if (u.username == username)
                return true;
        }
    }

    return false;
}