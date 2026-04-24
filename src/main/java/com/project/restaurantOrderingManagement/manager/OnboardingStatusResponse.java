package com.project.restaurantOrderingManagement.manager;

import java.util.List;

public class OnboardingStatusResponse {
    private boolean complete;
    private boolean managerCredentialChanged;
    private boolean hoursConfigured;
    private boolean hasWaiters;
    private boolean hasChefs;
    private boolean hasManagers;
    private boolean hasFoods;
    private boolean hasTables;
    private boolean foodQuantityConfiguredForAllFoods;
    private long waiterCount;
    private long chefCount;
    private long managerCount;
    private long foodCount;
    private long tableCount;
    private List<String> pendingItems;

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isManagerCredentialChanged() {
        return managerCredentialChanged;
    }

    public void setManagerCredentialChanged(boolean managerCredentialChanged) {
        this.managerCredentialChanged = managerCredentialChanged;
    }

    public boolean isHoursConfigured() {
        return hoursConfigured;
    }

    public void setHoursConfigured(boolean hoursConfigured) {
        this.hoursConfigured = hoursConfigured;
    }

    public boolean isHasWaiters() {
        return hasWaiters;
    }

    public void setHasWaiters(boolean hasWaiters) {
        this.hasWaiters = hasWaiters;
    }

    public boolean isHasChefs() {
        return hasChefs;
    }

    public void setHasChefs(boolean hasChefs) {
        this.hasChefs = hasChefs;
    }

    public boolean isHasManagers() {
        return hasManagers;
    }

    public void setHasManagers(boolean hasManagers) {
        this.hasManagers = hasManagers;
    }

    public boolean isHasFoods() {
        return hasFoods;
    }

    public void setHasFoods(boolean hasFoods) {
        this.hasFoods = hasFoods;
    }

    public boolean isHasTables() {
        return hasTables;
    }

    public void setHasTables(boolean hasTables) {
        this.hasTables = hasTables;
    }

    public boolean isFoodQuantityConfiguredForAllFoods() {
        return foodQuantityConfiguredForAllFoods;
    }

    public void setFoodQuantityConfiguredForAllFoods(boolean foodQuantityConfiguredForAllFoods) {
        this.foodQuantityConfiguredForAllFoods = foodQuantityConfiguredForAllFoods;
    }

    public long getWaiterCount() {
        return waiterCount;
    }

    public void setWaiterCount(long waiterCount) {
        this.waiterCount = waiterCount;
    }

    public long getChefCount() {
        return chefCount;
    }

    public void setChefCount(long chefCount) {
        this.chefCount = chefCount;
    }

    public long getManagerCount() {
        return managerCount;
    }

    public void setManagerCount(long managerCount) {
        this.managerCount = managerCount;
    }

    public long getFoodCount() {
        return foodCount;
    }

    public void setFoodCount(long foodCount) {
        this.foodCount = foodCount;
    }

    public long getTableCount() {
        return tableCount;
    }

    public void setTableCount(long tableCount) {
        this.tableCount = tableCount;
    }

    public List<String> getPendingItems() {
        return pendingItems;
    }

    public void setPendingItems(List<String> pendingItems) {
        this.pendingItems = pendingItems;
    }
}
