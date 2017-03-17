package app.racdeveloper.com.ICoNS.commentOnNewsFeed;

public class CommentData {
    private String commentUserImageUrl,commentUserName,commentUserRollNo, commentText;
    int commentID;

    public CommentData() {

    }

    public CommentData(int commentID, String commentUserImageUrl, String commentUserName, String commentUserRollNo, String commentText) {

        this.commentID = commentID;
        this.commentUserImageUrl = commentUserImageUrl;
        this.commentUserName = commentUserName;
        this.commentUserRollNo = commentUserRollNo;
        this.commentText = commentText;
    }

    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public String getCommentUserImageUrl() {
        return commentUserImageUrl;
    }

    public void setCommentUserImageUrl(String commentUserImageUrl) {
        this.commentUserImageUrl = commentUserImageUrl;
    }

    public String getCommentUserName() {
        return commentUserName;
    }

    public void setCommentUserName(String commentUserName) {
        this.commentUserName = commentUserName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentUserRollNo() {
        return commentUserRollNo;
    }

    public void setCommentUserRollNo(String commentUserRollNo) {
        this.commentUserRollNo = commentUserRollNo;
    }
}
