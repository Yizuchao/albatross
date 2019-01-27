package com.yogi.albatross.db.server.entity;

import com.yogi.albatross.common.server.ServerSessionProto;
import com.yogi.albatross.db.common.Status;

import java.util.Date;

public class Session {
    private long id;
    private String clientId;
    private ServerSessionProto.ServerSession serverSession;
    private Status status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public ServerSessionProto.ServerSession getServerSession() {
        return serverSession;
    }

    public void setServerSession(ServerSessionProto.ServerSession serverSession) {
        this.serverSession = serverSession;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
