#ifndef DATAVALIDATOR_H
#define DATAVALIDATOR_H

#include <string>
#include <vector>
#include <cctype>
#include <algorithm>

// Generic template-based data validator class
// Demonstrates function templates and operator overloading
template<typename T>
class DataValidator
{
public:
    // Template method for range validation
    static bool isInRange(const T& value, const T& minVal, const T& maxVal)
    {
        return value >= minVal && value <= maxVal;
    }

    // Template method for empty check
    static bool isEmpty(const T& value)
    {
        return value == T();
    }

    // Template method for duplicate check
    static bool hasDuplicates(const std::vector<T>& container)
    {
        for (size_t i = 0; i < container.size(); ++i)
        {
            for (size_t j = i + 1; j < container.size(); ++j)
            {
                if (container[i] == container[j])
                    return true;
            }
        }
        return false;
    }
};

// Specialization for std::string
template<>
class DataValidator<std::string>
{
public:
    static bool isInRange(const std::string& value, const std::string& minVal, const std::string& maxVal)
    {
        return value >= minVal && value <= maxVal;
    }

    static bool isEmpty(const std::string& value)
    {
        return value.empty();
    }

    static bool hasDuplicates(const std::vector<std::string>& container)
    {
        for (size_t i = 0; i < container.size(); ++i)
        {
            for (size_t j = i + 1; j < container.size(); ++j)
            {
                if (container[i] == container[j])
                    return true;
            }
        }
        return false;
    }

    // String-specific validators
    static bool isAlphaNumeric(const std::string& value)
    {
        return std::all_of(value.begin(), value.end(), 
                          [](unsigned char c) { return std::isalnum(c); });
    }

    static bool hasSpecialChars(const std::string& value)
    {
        return std::any_of(value.begin(), value.end(),
                          [](unsigned char c) { return !std::isalnum(c); });
    }

    static bool hasUpperCase(const std::string& value)
    {
        return std::any_of(value.begin(), value.end(),
                          [](unsigned char c) { return std::isupper(c); });
    }

    static bool hasLowerCase(const std::string& value)
    {
        return std::any_of(value.begin(), value.end(),
                          [](unsigned char c) { return std::islower(c); });
    }

    static bool hasDigit(const std::string& value)
    {
        return std::any_of(value.begin(), value.end(),
                          [](unsigned char c) { return std::isdigit(c); });
    }
};

#endif // DATAVALIDATOR_H
