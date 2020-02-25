package com.ovalmoney.fitness.permission;

public class Request {
    public final @Permission int permissionKind;
    public final int permissionAccess;

    public Request(@Permission int permissionKind, int permissionAccess) {
        this.permissionKind = permissionKind;
        this.permissionAccess = permissionAccess;
    }
}
