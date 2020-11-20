package com.ovalmoney.fitness.permission;

public class Request {
    public final @Permissions int permissionKind;
    public final int permissionAccess;

    public Request(@Permissions int permissionKind, int permissionAccess) {
        this.permissionKind = permissionKind;
        this.permissionAccess = permissionAccess;
    }
}
