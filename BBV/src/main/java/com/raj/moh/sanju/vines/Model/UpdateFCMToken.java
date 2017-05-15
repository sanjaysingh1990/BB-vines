package com.raj.moh.sanju.vines.Model;

/**
 * Created by NEERAJ on 5/12/2017.
 */

public class UpdateFCMToken {
    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    private String fcmToken;
    private String updateAt;
}
