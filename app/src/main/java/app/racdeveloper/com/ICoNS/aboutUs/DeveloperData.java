package app.racdeveloper.com.ICoNS.aboutUs;

/**
 * Created by Rachit on 2/6/2017.
 */

public class DeveloperData {
    private int id, developerImage;
    private String developerName, developerBranch, developerFBUrl, developerLinkedInUrl, developerTwitterUrl, developerGoogleUrl;

    public DeveloperData(int ID, int ContributorImage, String ContributorName, String ContributorBranch, String ContributorFBUrl,
                         String ContributorLinkedInUrl, String ContributorTwitterUrl, String ContributorGoogleUrl) {

        id = ID;
        developerImage = ContributorImage;
        developerName = ContributorName;
        developerBranch = ContributorBranch;

        developerFBUrl = ContributorFBUrl;
        developerLinkedInUrl = ContributorLinkedInUrl;
        developerTwitterUrl = ContributorTwitterUrl;
        developerGoogleUrl = ContributorGoogleUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeveloperImage() {
        return developerImage;
    }

    public void setDeveloperImage(int developerImage) {
        this.developerImage = developerImage;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getDeveloperBranch() {
        return developerBranch;
    }

    public void setDeveloperBranch(String developerBranch) {
        this.developerBranch = developerBranch;
    }

    public String getDeveloperFBUrl() {
        return developerFBUrl;
    }

    public void setDeveloperFBUrl(String developerFBUrl) {
        this.developerFBUrl = developerFBUrl;
    }

    public String getDeveloperLinkedInUrl() {
        return developerLinkedInUrl;
    }

    public void setDeveloperLinkedInUrl(String developerLinkedInUrl) {
        this.developerLinkedInUrl = developerLinkedInUrl;
    }

    public String getDeveloperTwitterUrl() {
        return developerTwitterUrl;
    }

    public void setDeveloperTwitterUrl(String developerTwitterUrl) {
        this.developerTwitterUrl = developerTwitterUrl;
    }

    public String getDeveloperGoogleUrl() {
        return developerGoogleUrl;
    }

    public void setDeveloperGoogleUrl(String developerGoogleUrl) {
        this.developerGoogleUrl = developerGoogleUrl;
    }
}
