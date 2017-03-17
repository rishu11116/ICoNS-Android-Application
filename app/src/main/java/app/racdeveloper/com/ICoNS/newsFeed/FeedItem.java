package app.racdeveloper.com.ICoNS.newsFeed;

/**
 * Created by Rachit on 10/7/2016.
 */
public class FeedItem {
    private int id, commentCount;
    private String author, authorRollno, content, image, profilePic, timeStamp, url;

    public FeedItem(){
    }

    public FeedItem(int id, String author, String content, String image, String profilePic, String timeStamp, String url, int commentCount){
        super();
        this.id = id;
        this.author = author;
        this.image = image;
        this.content = content;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.url = url;
        this.commentCount = commentCount;
    }

    public int getCommentCount(){
        return commentCount;
    }

    public void setCommentCount(int commentCount){
        this.commentCount = commentCount;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public String getAuthorRollno(){
        return authorRollno;
    }

    public void setAuthorRollno(String authorRollno){
        this.authorRollno = authorRollno;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
