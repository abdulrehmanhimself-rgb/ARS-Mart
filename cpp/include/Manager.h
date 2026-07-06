#ifndef MANAGER_H
#define MANAGER_H

#include <string>
#include <vector>


class Manager
{
protected:
    std::string managerName;
    bool isInitialized;

public:
    Manager(const std::string& name = "") 
        : managerName(name), isInitialized(false) {}
    
    virtual ~Manager() = default;

    virtual void initialize() = 0;
    virtual void save() = 0;
    virtual void load() = 0;
    virtual std::string getStatus() const = 0;

    virtual bool isReady() const { return isInitialized; }
    virtual std::string getManagerName() const { return managerName; }
    virtual void setManagerName(const std::string& name) { managerName = name; }

    template<typename T>
    bool findInContainer(const std::vector<T>& container, const T& item)
    {
        for (const auto& element : container)
        {
            if (element == item)
                return true;
        }
        return false;
    }

    template<typename T, typename Predicate>
    T* findWithPredicate(std::vector<T>& container, Predicate pred)
    {
        for (auto& element : container)
        {
            if (pred(element))
                return &element;
        }
        return nullptr;
    }

    template<typename T>
    void sortContainer(std::vector<T>& container)
    {
        for (size_t i = 0; i < container.size(); ++i)
        {
            for (size_t j = i + 1; j < container.size(); ++j)
            {
                if (container[j] < container[i])
                {
                    std::swap(container[i], container[j]);
                }
            }
        }
    }
};

#endif 
