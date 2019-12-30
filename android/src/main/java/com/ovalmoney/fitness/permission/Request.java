package com.ovalmoney.fitness.permission;

public class Request {
    public final @Permission int permissionType;
    public final int access;

    public Request(@Permission int permissionType, int access) {
        this.permissionType = permissionType;
        this.access = access;
    }
}
