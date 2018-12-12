package com.uniprogy.outquiz.views;

public abstract class AnswerOptionListener {

    abstract public Boolean canSelectOption();
    abstract public Boolean optionSelected(int id);

}
