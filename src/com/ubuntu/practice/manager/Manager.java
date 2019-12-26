package com.ubuntu.practice.manager;

public class Manager
{
    protected ManagerHandler handler;
    
    public Manager(final ManagerHandler handler) {
        this.handler = handler;
    }
}
