package com.cudocomm.troubleticket.model;

public class MenuModel {

    private int counter;
    private int icon;
    private String iconUrl;
    private boolean isGroupTitle;
    private String title;

    public MenuModel(String title, int icon, boolean isGroupTitle) {
        this.title = title;
        this.icon = icon;
        this.isGroupTitle = isGroupTitle;
    }

    public MenuModel(String title, String icon, boolean isGroupTitle) {
        this.title = title;
        this.iconUrl = icon;
        this.icon = 0;
        this.isGroupTitle = isGroupTitle;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return this.icon;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getCounter() {
        return this.counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public boolean isGroupTitle() {
        return this.isGroupTitle;
    }

    public void setGroupTitle(boolean isGroupTitle) {
        this.isGroupTitle = isGroupTitle;
    }
}
