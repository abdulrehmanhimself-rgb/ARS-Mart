#ifndef ENTITY_H
#define ENTITY_H

#include <string>

class Entity
{
protected:
    std::string id;
    std::string name;

public:
    Entity() : id(""), name("") {}
    Entity(const std::string& _id, const std::string& _name) 
        : id(_id), name(_name) {}
    
    virtual ~Entity() = default;

    virtual std::string getID() const = 0;
    virtual std::string getName() const = 0;
    virtual void display() const = 0;
    virtual std::string serialize() const = 0;

    virtual bool operator==(const Entity& other) const;
    virtual bool operator!=(const Entity& other) const;
    virtual bool operator<(const Entity& other) const;
    virtual bool operator>(const Entity& other) const;
};

#endif
