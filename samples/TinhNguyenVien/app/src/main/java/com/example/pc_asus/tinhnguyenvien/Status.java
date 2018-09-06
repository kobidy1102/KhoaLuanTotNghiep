package com.example.pc_asus.tinhnguyenvien;

public class Status {
    String statusWithFriends;
    String statusWithAll;
    String connectionRequest;

    public Status() {
    }

    public Status(String statusWithFriends, String statusWithAll, String connectionRequest) {
        this.statusWithFriends = statusWithFriends;
        this.statusWithAll = statusWithAll;
        this.connectionRequest = connectionRequest;
    }
}
