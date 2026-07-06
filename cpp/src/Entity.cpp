#include "Entity.h"

bool Entity::operator==(const Entity& other) const
{
    return this->id == other.id;
}

bool Entity::operator!=(const Entity& other) const
{
    return !(*this == other);
}

bool Entity::operator<(const Entity& other) const
{
    return this->id < other.id;
}

bool Entity::operator>(const Entity& other) const
{
    return this->id > other.id;
}
