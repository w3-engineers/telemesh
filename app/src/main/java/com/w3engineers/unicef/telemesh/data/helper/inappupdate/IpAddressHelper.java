package com.w3engineers.unicef.telemesh.data.helper.inappupdate;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class IpAddressHelper {

    public static InetAddress getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (inetAddress.isLoopbackAddress())
                        continue;

                    return inetAddress;
                } // for
            } // for
        } catch (SocketException ex) { ex.printStackTrace(); }
        return null;
    }

    public static InetAddress getMyDeviceInetAddress(boolean isLocalAddress) {
        String my_ip = null;
        InetAddress inetAddress = null;
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        if (isLocalAddress) {
                            my_ip = inetAddress.getHostAddress();
                        }
                    }else {
                        if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                            my_ip = inetAddress.getHostAddress();
                        }
                    }
                }
            }
            //Generate Inetaddress from host Ip address

            if(my_ip != null) {
                InetAddress myInetAddress = InetAddress.getByName(my_ip);
                return myInetAddress;
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
