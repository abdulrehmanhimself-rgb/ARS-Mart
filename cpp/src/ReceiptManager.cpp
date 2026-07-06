#include "ReceiptManager.h"

#include <iostream>
#include <fstream>
#include <iomanip>
#include <algorithm>
#include <limits>

using namespace std;

void ReceiptManager::generateReceipt(const vector<Inventory>& purchasedItems,
    double totalBill,
    double codCharges)
{
    int receiptCount = 1;

    ofstream receiptfile("data/Receipt.txt", ios::app);

    string line = "============================================================";

    cout << "\033[1;35m" << line << "\033[0m\n";

    cout << "\033[1;34m===================== SHOPPING RECEIPT =====================\033[0m\n";

    cout << "\033[1;36mNo   Product             Qty       Price        Total\033[0m\n";

    cout << "\033[1;33m------------------------------------------------------------\033[0m\n";

    receiptfile << "=============== ARS MART ===============\n\n";

    receiptfile << "===================== SHOPPING RECEIPT =====================\nReceipt # " << receiptCount << endl;;

    receiptfile << left << setw(5) << "No"
        << setw(20) << "Product"
        << setw(10) << "Qty"
        << setw(15) << "Price"
        << setw(15) << "Total" << endl;

    receiptfile << "------------------------------------------------------------\n";

    for (int i = 0; i < purchasedItems.size(); ++i)
    {
        double itemTotal = purchasedItems[i].cartQty * purchasedItems[i].price;

        cout << "\033[1;36m" << left << setw(5) << (i + 1)
            << setw(20) << purchasedItems[i].name
            << "\033[1;32m" << setw(10) << purchasedItems[i].cartQty
            << "\033[1;33m" << setw(15) << purchasedItems[i].price
            << "\033[1;35m" << setw(15) << itemTotal << "\033[0m"
            << endl;

        receiptfile << left << setw(5) << (i + 1)
            << setw(20) << purchasedItems[i].name
            << setw(10) << purchasedItems[i].cartQty
            << setw(15) << purchasedItems[i].price
            << setw(15) << itemTotal << endl;
    }

    if (codCharges > 0)
    {
        cout << "\033[1;31mCash on Delivery Charges: Rs. " << codCharges << "\033[0m\n";

        receiptfile << "Cash on Delivery Charges: Rs. " << codCharges << endl;
    }

    double finalBill = totalBill + codCharges;

    cout << "\033[1;33m------------------------------------------------------------\033[0m\n";

    cout << "\033[1;32m" << right << setw(50) << "TOTAL BILL: Rs. " << finalBill << " \033[0m\n";

    cout << "\033[1;35m" << line << "\033[0m\n";

    cout << "\033[1;34m ORDER CONFIRMED! THANK YOU FOR SHOPPING WITH US! \033[0m\n";

    receiptfile << "------------------------------------------------------------\n";

    receiptfile << right << setw(50) << "TOTAL BILL: Rs. " << finalBill << endl;

    receiptfile << "============================================================\n";

    receiptfile << "ORDER CONFIRMED!\nTHANK YOU FOR SHOPPING WITH US!\n\n";

    receiptfile.close();

    cout << "\n\033[1;33mPress Enter to continue...\033[0m";
    cin.ignore(numeric_limits<streamsize>::max(), '\n');
    cin.get();
}

