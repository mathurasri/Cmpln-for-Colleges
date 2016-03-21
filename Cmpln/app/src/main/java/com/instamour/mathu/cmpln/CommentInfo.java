package com.instamour.mathu.cmpln;

public class CommentInfo {
    public String avaPos;
    public String commentSentence;
    public String userName;
    public String time;
    public String likes;
    public String likesflag;
    public String followed_flag;
    public String noofreplies;
    public CommentInfo(String pos, String comment, String name, String time, String likes, String likesflag, String followed_flag, String noofreplies)
    {
        this.avaPos = pos;
        this.commentSentence = comment;
        this.userName =  name;
        this.time = time;
        this.likes = likes;
        this.likesflag = likesflag;
        this.followed_flag = followed_flag;
        this.noofreplies = noofreplies;
    }
}
