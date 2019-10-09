package com.its.tourist;

class GlobalVariable {
    private static final GlobalVariable ourInstance = new GlobalVariable();

    static GlobalVariable getInstance() {
        return ourInstance;
    }

    private boolean backEnable;
    private boolean backPeople;

    private GlobalVariable() {
        backEnable=false;
        backPeople=false;
    }

    public boolean getBackEnable(){
        return backEnable;
    }

    public void setBackEnable(boolean back){
        backEnable=back;
    }

    public boolean getBackPeople(){
        return backPeople;
    }

    public void setBackPeople(boolean back){
        backPeople=back;
    }


}
