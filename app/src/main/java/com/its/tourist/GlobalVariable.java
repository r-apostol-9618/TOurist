package com.its.tourist;

class GlobalVariable {
    private static final GlobalVariable ourInstance = new GlobalVariable();

    static GlobalVariable getInstance() {
        return ourInstance;
    }

    private boolean backPeople;

    private GlobalVariable() {
        backPeople = true;
    }

    boolean getBackPeople(){
        return backPeople;
    }

    void setBackPeople(boolean back){
        backPeople = back;
    }


}
