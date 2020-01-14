.. _viper_wallet_support:


Wallet Support
--------------

``public static WalletManager getInstance()`` - returns the WalletManager singleton object.

``public boolean hasSeller()`` - returns if user is connected to any seller type user.

``public String getMyAddress()`` - returns user's wallet address.

``public int getMyEndpoint()`` - returns user's current blockchain network endpoint value.

``public boolean isGiftGot()`` - returns if user already has received the gift point and ether by airdrop.

``public void setWalletListener(WalletListener walletListener)`` - set WalletListener from your wallet activity to receive various events

``public static void openActivity(Context context, byte[] picture)`` - if you want to use the default wallet activity, calling this method will do that. @params: 1. Context: activity context, 2. byte[]: picture you walt to show in the default wallet page.

``public boolean giftEther()`` - call this method to initialize point and ether gift process. If user is capable of getting gift, will get it.

``public void setEndpoint(int endpoint)`` - call this method to set different blockchain network endpoint value (this is related to configuration file provided by application end initially)

``public void refreshMyBalance()`` - call this method to refresh balance.

``public void getAllOpenDrawableBlock()`` - call to withdraw pending balances stored in the channel.

``public LiveData<Double> getTotalEarn(String myAddress, int endPoint)`` - observe this to get total earning live data by user

``public LiveData<Double> getTotalSpent(String myAddress, int endPoint)`` - observe this to get total spent live data by user

``public LiveData<Double> getTotalPendingEarning(String myAddress, int endPoint)`` - observe this to get pending earning(stored in microraiden channel) live data by seller

``public Flowable<List<NetworkInfo>> getNetworkInfoByNetworkType()`` - observe this to get balance change, blockchain network information change.

``public void createWallet(Context context, String password, WalletCreateListener listener)`` - This api is used to create wallet. Call this the user is totally new.

``public void loadWallet(Context context, String password, WalletLoadListener listener)`` - This api is used to load wallet for a returning user, provided that wallet file already exists in the system.

``public void importWallet(Context context, String password, Uri fileUri, WalletImportListener listener)`` - This api is used to import wallet, provided that user already has a wallet file of his/her own created from other source.

  Interface
  ---------
  ::

     public interface WalletCreateListener {
         ``void onWalletCreated(String walletAddress, String publicKey)`` - called  when wallet is created.
         ``void onError(String message)`` - called when there is an error
     }


     public interface WalletLoadListener {
         ``void onWalletLoaded(String walletAddress, String publicKey)`` - called  when wallet is loaded.
         ``void onError(String message)`` - called when there is an error
     }



     public interface WalletImportListener {
         ``void onWalletImported(String walletAddress, String publicKey)`` - called  when wallet is imported.
         ``void onError(String message)`` - called when there is an error
     }


     public interface WalletListener {
         ``void onGiftResponse(boolean success, boolean isGifted, String message)`` - called at various steps in ether and point gift process.
         ``void onBalanceInfo(boolean success, String msg)`` - called when refresh balance response received

     }











