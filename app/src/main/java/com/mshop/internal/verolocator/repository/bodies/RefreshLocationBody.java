package com.mshop.internal.verolocator.repository.bodies;

/**
 * Created by victor on 8/6/18.
 * Mshop Spain.
 */
public class RefreshLocationBody {
    private long userId;
    private String terminalUniqueId;
    private int battery;
    private double userLatitude;
    private double userLongitude;


    public RefreshLocationBody(long userId, String terminalUniqueId, int battery, double userLatitude, double userLongitude) {
        this.userId = userId;
        this.terminalUniqueId = terminalUniqueId;
        this.battery = battery;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
    }
}
