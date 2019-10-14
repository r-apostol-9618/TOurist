package com.its.tourist;

class GlobalVariable {
    private static final GlobalVariable ourInstance = new GlobalVariable();

    static GlobalVariable getInstance() {
        return ourInstance;
    }

    private boolean backPeople;
    private boolean handlerBudget;
    private int budgetStart;
    private int budgetEnd;
    private String typePerson;
    private String calendarDay;
    private String timeStart;
    private String timeEnd;

    private GlobalVariable() {
        backPeople = true;
        handlerBudget = true;
        budgetStart = 0;
        budgetEnd = 0;
        typePerson = "singolo";
        calendarDay = "0";
        timeStart = "0";
        timeEnd = "0";
    }


    public int getBudgetStart() {
        return budgetStart;
    }

    public void setBudgetStart(int budgetStart) {
        this.budgetStart = budgetStart;
    }

    public int getBudgetEnd() {
        return budgetEnd;
    }

    public void setBudgetEnd(int budgetEnd) {
        this.budgetEnd = budgetEnd;
    }

    public String getTypePerson() {
        return typePerson;
    }

    public void setTypePerson(String typePerson) {
        this.typePerson = typePerson;
    }

    public String getCalendarDay() {
        return calendarDay;
    }

    public void setCalendarDay(String calendarDay) {
        this.calendarDay = calendarDay;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    boolean isBackPeople() {
        return backPeople;
    }

    void setBackPeople(boolean backPeople) {
        this.backPeople = backPeople;
    }

    boolean isHandlerBudget() {
        return handlerBudget;
    }

    void setHandlerBudget(boolean handlerBudget) {
        this.handlerBudget = handlerBudget;
    }
}
