#include "Utils.h"
#include <iostream>
#include <limits>
#include <algorithm>
#include <cctype>
#include <cstdlib>
#define NOMINMAX
#ifdef _WIN32
#include <windows.h>
#else
#include <unistd.h>
#endif

using namespace std;


void Utils::sleepSeconds(int seconds)
{
#ifdef _WIN32
    Sleep(seconds * 1000);
#else
    sleep(seconds);
#endif
}


string Utils::getStringInput(string message)
{
    string input;

    while (true)
    {
        cout << message;
        getline(cin >> ws, input);

        if (!input.empty())
            return input;

        cout << "\033[1;31mInvalid input! Please try again.\033[0m\n";
    }
}


char Utils::getCharChoice(string message)
{
    char ch;
    cout << message;

    while (true)
    {
        cin >> ch;
        cin.ignore(numeric_limits<streamsize>::max(), '\n');

        if (ch == 'y' || ch == 'Y' || ch == 'n' || ch == 'N')
            return ch;

        cout << "\033[1;31mInvalid choice! Enter y/n only:\033[0m ";
    }
}


void Utils::clearScreen()
{
#ifdef _WIN32
	system("cls");

#else
    system("clear");
#endif
}


int Utils::getIntInput()
{
    int x;
    cin >> x;

    while (cin.fail())
    {
        cin.clear();
        cin.ignore(numeric_limits<streamsize>::max(), '\n');

        cout << "\033[1;31mInvalid input! Enter a number: \033[0m";

        cin >> x;
    }

    return x;
}


double Utils::getDoubleInput()
{
    double x;
    cin >> x;

    while (cin.fail())
    {
        cin.clear();
        cin.ignore(numeric_limits<streamsize>::max(), '\n');

        cout << "\033[1;31mInvalid number! Enter again: \033[0m";

        cin >> x;
    }

    return x;
}


double Utils::getDoubleInput(string message)
{
    double value;

    cout << message;

    while (!(cin >> value))
    {
        cout << "\033[1;31mInvalid amount! Enter again:\033[0m ";

        cin.clear();
        cin.ignore(numeric_limits<streamsize>::max(), '\n');
    }

    cin.ignore(numeric_limits<streamsize>::max(), '\n');

    return value;
}


char Utils::getYesNoInput()
{
    char ch;

    while (true)
    {
        cin >> ch;
        cin.ignore(numeric_limits<streamsize>::max(), '\n');

        ch = tolower(ch);

        if (ch == 'y' || ch == 'n')
            return ch;

        cout << "\033[1;31mInvalid input! Enter y or n only: \033[0m";
    }
}


string Utils::toLower(string str)
{
    for (char& c : str)
        c = tolower(c);

    return str;
}


void Utils::appTheme()
{
    clearScreen();

    cout << "\033[1;45m\033[1;97m";
    cout << "█                ARS MART                   █\n";
    cout << "\033[0m\n";
}


void Utils::pressEnterToContinue()
{
    cout << "\033[1;33m\nPress Enter to continue...\033[0m";

    cin.ignore(numeric_limits<streamsize>::max(), '\n');

    cin.get();

    clearScreen();
}