package ru.yandex.praktikum.pojo.createuser;

import ru.yandex.praktikum.pojo.updateuser.User;

public class CreateUserDeserialization {
    private boolean success;
    private User user;
    private String accessToken;
    private String refreshToken;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
