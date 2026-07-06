#ifndef GLOBALS_H
#define GLOBALS_H

#include "InventoryManager.h"
#include "SalesManager.h"
#include "HistoryManager.h"
#include "Manager.h"
#include <string>

extern InventoryManager inventorySystem;
extern SalesManager salesSystem; 
extern HistoryManager historySystem;
extern std::string currentUsername; 

#define GET_MANAGER_STATUS(manager) (manager).getStatus()

#endif 
