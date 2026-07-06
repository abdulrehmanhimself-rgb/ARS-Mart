#ifndef UTILS_H
#define UTILS_H

#include <string>

class Utils
{
public:
    static void clearScreen();
    static void appTheme();
    static void sleepSeconds(int seconds);

    static std::string getStringInput(std::string message);
    static char getCharChoice(std::string message);

    static int getIntInput();
    static double getDoubleInput();
    static double getDoubleInput(std::string message);

    static char getYesNoInput();

    static std::string toLower(std::string str);

    static void pressEnterToContinue();
};

#endif
