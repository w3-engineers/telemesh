.. _viper_data_plan_support:


Data plan Support
-----------------

``public static DataPlanManager getInstance()`` - returns DataPlanManager singleton object

``public int getDataPlanRole()`` - returns user role

``public long getSellAmountData()`` - returns amount of data user wnts to share.

``public int getDataAmountMode()`` - returns whether shared data is limited or unlimited.

``public long getSellFromDate()`` - returns timestamp from when data selling starts.

``public long getSellDataAmount()`` - returns value of data amount what user set for limited data plan.

``public long getRemainingData()`` - returns remaining amount of data shared by seller.

``public long getUsedData(Context context, long fromDate)`` - returns used data amount from specific timestamp.

``public static void openActivity(Context context, int imageValue)`` - call this method to open default dataplan activity.

``public static void resumeMessaging()`` - call this method to resume seller side functionality to help buyer messaging.

``public void closeMesh(int role)`` - call this method to stop mesh communication.

``public void roleSwitch(int newRole)`` - call this method to switch user role

``public void setSellFromDate(long fromDate)``- call this method to set data selling starting timestamp.

``public void setDataAmountMode(int mode)`` - call this method to set user choise for data sharing limited/unlimited, value 1 for limited and 0 for unlimited.

``public void setSellDataAmount(Long sharedData) `` - call this method to set data sell amount in MB

``public void closeAllActiveChannel()`` - call this method to close all active channel by seller.

``public void initPurchase(double amount, String sellerId)`` - call this method to purchase data in MB from seller.

``public void closePurchase(String sellerId)`` - call this method to close any purchased channel by buyer.

``public void processAllSeller(Context context)`` - call this method to process the connected seller list in UI by buyer.

``public void setCurrentSeller(Context context, String sellerId, String currentSellerStatus)`` - call this method to set status of the seller.

``public void precessDisconnectedSeller(Context context, String sellerId)`` - call this method to process disconnected seller from list.

``public void setDataPlanListener(DataPlanListener dataPlanListener)`` - set DataPlanListener from dataplan activity.

``public Flowable<List<Seller>> getAllSellers()`` - observe this to get any change in connected seller list


