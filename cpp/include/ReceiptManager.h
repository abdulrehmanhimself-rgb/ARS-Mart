// ReceiptManager.h - CORRECTED
#ifndef RECEIPT_MANAGER_H
#define RECEIPT_MANAGER_H

#include <vector>
#include "Inventory.h" 

class ReceiptManager
{
public:
    static void generateReceipt(const std::vector<Inventory>& items,
        double totalBill,
        double codCharges);
};

#endif
