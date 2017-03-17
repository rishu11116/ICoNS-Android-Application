package app.racdeveloper.com.ICoNS.fetchProfiles;

import java.io.Serializable;

/**
 * Created by Rachit on 11/22/2016.
 */
public class MyProfileData implements Serializable {
    private String MyProfileImageUrl;
    private String MyProfileName;
    private String MyProfileStatus;
    private String MyProfileBranch;
    private String MyProfileBatch;
    private String MyProfileId;
    private String MyProfileContact;
    private String MyProfileCouncils;
    private String MyProfileSkills;
    private String MyProfileHobbies;
    private String MyProfileBlood;
    private String MyProfileAddress;
    private String MyProfileEmail;
    private String MyProfileWebsite;
    private String MyProfileFb;
    private String MyProfileGithub;
    private String MyProfileLinkedin;

    public MyProfileData(){

    }
    public MyProfileData(String profileImageUrl, String profileName, String profileStatus, String profileBranch, String profileBatch,
                         String profileId, String profileCouncils, String profileSkills, String profileHobbies,
                         String profileBlood, String profileAddress, String profileEmail, String profileWebsite,
                         String profileFb, String profileGithub, String profileLinkedin, String profileContact) {

        MyProfileImageUrl = profileImageUrl;
        MyProfileName = profileName;
        MyProfileStatus = profileStatus;
        MyProfileBranch = profileBranch;
        MyProfileBatch = profileBatch;
        MyProfileId = profileId;
        MyProfileCouncils = profileCouncils;
        MyProfileSkills = profileSkills;
        MyProfileHobbies = profileHobbies;
        MyProfileBlood = profileBlood;
        MyProfileAddress = profileAddress;
        MyProfileEmail = profileEmail;
        MyProfileWebsite = profileWebsite;
        MyProfileGithub = profileGithub;
        MyProfileLinkedin = profileLinkedin;
        MyProfileContact = profileContact;
    }

    public String getProfileContact() {
        return MyProfileContact;
    }

    public void setProfileContact(String profileContact){
        MyProfileContact = profileContact;
    }

    public String getProfileImageUrl() {
        return MyProfileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        MyProfileImageUrl = profileImageUrl;
    }

    public String getProfileName() {
        return MyProfileName;
    }

    public void setProfileName(String profileName) {
        MyProfileName = profileName;
    }

    public String getProfileStatus() {
        return MyProfileStatus;
    }

    public void setProfileStatus(String profileStatus) {
        MyProfileStatus = profileStatus;
    }

    public String getProfileBranch() {
        return MyProfileBranch;
    }

    public void setProfileBranch(String profileBranch) {
        MyProfileBranch = profileBranch;
    }

    public String getProfileBatch() {
        return MyProfileBatch;
    }

    public void setProfileBatch(String profileBatch) {
        MyProfileBatch = profileBatch;
    }

    public String getProfileId() {
        return MyProfileId;
    }

    public void setProfileId(String profileId) {
        MyProfileId = profileId;
    }

    public String getProfileCouncils() {
        return MyProfileCouncils;
    }

    public void setProfileCouncils(String profileCouncils) {
        MyProfileCouncils = profileCouncils;
    }

    public String getProfileSkills() {
        return MyProfileSkills;
    }

    public void setProfileSkills(String profileSkills) {
        MyProfileSkills = profileSkills;
    }

    public String getProfileHobbies() {
        return MyProfileHobbies;
    }

    public void setProfileHobbies(String profileHobbies) {
        MyProfileHobbies = profileHobbies;
    }

    public String getProfileAddress() {
        return MyProfileAddress;
    }

    public void setProfileAddress(String profileAddress) {
        MyProfileAddress = profileAddress;
    }

    public String getProfileBlood() {
        return MyProfileBlood;
    }

    public void setProfileBlood(String profileBlood) {
        MyProfileBlood = profileBlood;
    }

    public String getProfileEmail() {
        return MyProfileEmail;
    }

    public void setProfileEmail(String profileEmail) {
        MyProfileEmail = profileEmail;
    }

    public String getProfileWebsite() {
        return MyProfileWebsite;
    }

    public void setProfileWebsite(String profileWebsite) {
        MyProfileWebsite = profileWebsite;
    }

    public String getProfileFb() {
        return MyProfileFb;
    }

    public void setProfileFb(String profileFb) {
        MyProfileFb = profileFb;
    }

    public String getProfileGithub() {
        return MyProfileGithub;
    }

    public void setProfileGithub(String profileGithub) {
        MyProfileGithub = profileGithub;
    }

    public String getProfileLinkedin() {
        return MyProfileLinkedin;
    }

    public void setProfileLinkedin(String profileLinkedin) {
        MyProfileLinkedin = profileLinkedin;
    }

}
