package com.ovalmoney.fitness.permission;

public class Request {
    public final @Permission int kind;
    public final int access;

    public Request(@Permission int kind, int access) {
        this.kind = kind;
        this.access = access;
    }
}
