package it.gristeliti.smartu.utils;

public class BoardMessage {

    private String nickname;
    private String message;
    private String date;

    public BoardMessage(String nickname, String message, String date) {
        this.nickname = nickname;
        this.message = message;
        this.date = date;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }
}
