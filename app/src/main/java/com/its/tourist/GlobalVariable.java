package com.its.tourist;

class GlobalVariable {
    private static final GlobalVariable ourInstance = new GlobalVariable();

    static GlobalVariable getInstance() {
        return ourInstance;
    }

    private boolean backPeople;
    private boolean handlerBudget;

    private GlobalVariable() {
        backPeople = true;
        handlerBudget = true;
    }

    boolean getHandlerBudget(){
        return handlerBudget;
    }

    void setHandlerBudget(boolean handler){
        handlerBudget = handler;
    }

    boolean getBackPeople(){
        return backPeople;
    }

    void setBackPeople(boolean back){
        backPeople = back;
    }


}
