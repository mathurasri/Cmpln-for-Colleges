package com.instamour.mathu.cmpln;

public class ReplyInfo {
    public String reply;
    public String avatarPos;
    public String user;
    public String commentTime;
    public String comment_id;

    public ReplyInfo(String reply, String avatarPos, String user, String commentTime, String comment_id) {
        this.reply = reply;
        this.avatarPos = avatarPos;
        this.user = user;
        this.commentTime = commentTime;
        this.comment_id = comment_id;
    }
}
