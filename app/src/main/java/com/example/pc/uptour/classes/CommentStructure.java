package com.example.pc.uptour.classes;

/**
 * Created by apoor on 3/13/2018.
 */

public class CommentStructure
{
    private String userId;
    private String placeId;
    private String type;
    private String commentText;

    public CommentStructure(String userId, String placeId, String type) {
        this.userId = userId;
        this.placeId = placeId;
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }


    public String getPlaceId() {
        return placeId;
    }


    public String getType() {
        return type;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }



}
