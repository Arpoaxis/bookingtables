package com.restaurant.model;

public class RestaurantTable {

    private int tableId;
    private int restaurantId;
    private int tableNumber;
    private int minCapacity;
    private int maxCapacity;
    private boolean canCombine;

    public RestaurantTable() {}

    // Constructor used when creating a new table
    public RestaurantTable(int restaurantId,
                           int tableNumber,
                           int minCapacity,
                           int maxCapacity,
                           boolean canCombine) {

        this.restaurantId = restaurantId;
        this.tableNumber = tableNumber;
        this.minCapacity = minCapacity;
        this.maxCapacity = maxCapacity;
        this.canCombine = canCombine;
    }

    //Constructor used when retrieving from DB
    public RestaurantTable(int tableId,
                           int restaurantId,
                           int tableNumber,
                           int minCapacity,
                           int maxCapacity,
                           boolean canCombine) {

        this.tableId = tableId;
        this.restaurantId = restaurantId;
        this.tableNumber = tableNumber;
        this.minCapacity = minCapacity;
        this.maxCapacity = maxCapacity;
        this.canCombine = canCombine;
    }



    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getMinCapacity() {
        return minCapacity;
    }

    public void setMinCapacity(int minCapacity) {
        this.minCapacity = minCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public boolean isCanCombine() {
        return canCombine;
    }

    public void setCanCombine(boolean canCombine) {
        this.canCombine = canCombine;
    }
}
