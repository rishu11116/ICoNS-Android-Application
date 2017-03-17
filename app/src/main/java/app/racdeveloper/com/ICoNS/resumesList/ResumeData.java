package app.racdeveloper.com.ICoNS.resumesList;

public class ResumeData {

    private int id;
    private String resumeName,resumeBranch,resumeBatch,resumeUrl;

    public ResumeData(int id, String resumeName, String resumeBranch, String resumeBatch, String resumeUrl) {
        this.id = id;
        this.resumeName = resumeName;
        this.resumeBranch = resumeBranch;
        this.resumeBatch = resumeBatch;
        this.resumeUrl = resumeUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResumeName() {
        return resumeName;
    }

    public void setResumeName(String resumeName) {
        this.resumeName = resumeName;
    }

    public String getResumeBranch() {
        return resumeBranch;
    }

    public void setResumeBranch(String resumeBranch) {
        this.resumeBranch = resumeBranch;
    }

    public String getResumeBatch() {
        return resumeBatch;
    }

    public void setResumeBatch(String resumeBatch) {
        this.resumeBatch = resumeBatch;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
}