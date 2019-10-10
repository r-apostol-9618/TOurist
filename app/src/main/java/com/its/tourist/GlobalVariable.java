package com.its.tourist;

class GlobalVariable {
    private static final GlobalVariable ourInstance = new GlobalVariable();

    static GlobalVariable getInstance() {
        return ourInstance;
    }

    private boolean backMain;
    private boolean backPeople;

    private GlobalVariable() {
        backMain=false;
        backPeople=false;
    }

    public boolean getBackMain(){
        return backMain;
    }

    public void setBackMain(boolean back){
        backMain=back;
    }

    public boolean getBackPeople(){
        return backPeople;
    }

    public void setBackPeople(boolean back){
        backPeople=back;
    }


}
