package com.aop.annotation.logging;

import java.awt.Color;

public class TestPojo {

    @FieldLog(write=false)
    private String firstName;

    @FieldLog(read=false)
    private String lastName; 

    @FieldLog(prefix="Don't be color-blind: ")
    private Color favoriteColor; 

    @FieldLog(suffix=". Dad gum! that's old!!")
    private int age;

    @MethodLog(prefix="Constructor", level=LogLevel.INFO)
    public TestPojo(String firstName, String lastName, Color favoriteColor, int age) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.favoriteColor = favoriteColor;
        this.age = age;
    }

    @MethodLog
    public String getFirstName() {
        return firstName;
    }

    @MethodLog
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @MethodLog
    public String getLastName() {
        return lastName;
    }

    @MethodLog
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @MethodLog
    public Color getFavoriteColor() {
        return favoriteColor;
    }

    @MethodLog
    public void setFavoriteColor(Color favoriteColor) {
        this.favoriteColor = favoriteColor;
    }

    @MethodLog
    public int getAge() {
        return age;
    }

    @MethodLog
    public void setAge(int age) {
        this.age = age;
    }

    public TestPojo() {
      super();
    } 

    
}