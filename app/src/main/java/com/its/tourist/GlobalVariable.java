package com.its.tourist;


/**
 *  GlobalVariable
 *  Classe Singleton usata per poter salvare ed ottenere i dati necessari al corretto funzionamento dell'applicazione
 */
class GlobalVariable {

    private static final GlobalVariable ourInstance = new GlobalVariable();

    static GlobalVariable getInstance() {
        return ourInstance;
    }

    private boolean backPeople;
    private boolean handlerPeople;
    private int budgetStart;
    private int budgetEnd;
    private String typePerson;
    private String calendarDay;
    private String timeStart;
    private String timeEnd;

    private GlobalVariable() {
        backPeople = true;
        handlerPeople = true;
        budgetStart = 0;
        budgetEnd = 0;
        typePerson = "singolo";
        calendarDay = "0";
        timeStart = "0";
        timeEnd = "0";
    }


    int getBudgetStart() {
        return budgetStart;
    }

    void setBudgetStart(int budgetStart) {
        this.budgetStart = budgetStart;
    }

    int getBudgetEnd() {
        return budgetEnd;
    }

    void setBudgetEnd(int budgetEnd) {
        this.budgetEnd = budgetEnd;
    }

    String getTypePerson() {
        return typePerson;
    }

    void setTypePerson(String typePerson) {
        this.typePerson = typePerson;
    }

    public String getCalendarDay() {
        return calendarDay;
    }

    void setCalendarDay(String calendarDay) {
        this.calendarDay = calendarDay;
    }

    public String getTimeStart() {
        return timeStart;
    }

    void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    boolean isBackPeople() {
        return backPeople;
    }

    void setBackPeople(boolean backPeople) {
        this.backPeople = backPeople;
    }

    boolean isHandlerPeople() {
        return handlerPeople;
    }

    void setHandlerPeople(boolean handlerPeople) {
        this.handlerPeople = handlerPeople;
    }
}
