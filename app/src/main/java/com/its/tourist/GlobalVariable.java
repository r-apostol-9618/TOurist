package com.its.tourist;

class GlobalVariable {
    private static final GlobalVariable ourInstance = new GlobalVariable();

    static GlobalVariable getInstance() {
        return ourInstance;
    }

    private boolean backEnable;

    private GlobalVariable() {
        backEnable=false;
    }

    public boolean getBackEnable(){
        return backEnable;
    }

    public void setBackEnable(boolean back){
        backEnable=back;
    }


}
