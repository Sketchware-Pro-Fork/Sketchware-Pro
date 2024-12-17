package pro.sketchware.activities.main.fragments.projects_store.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProjectDownloadUrlModel {
    
    @SerializedName("status")
    @Expose
    private String status;
    
    @SerializedName("message")
    @Expose
    private String downloadUrl;
    
    @SerializedName("new_access_token")
    @Expose
    private String newAccessToken;
    
    public void setStatus(final String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setDownloadUrl(final String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setNewAccessToken(final String newAccessToken) {
        this.newAccessToken = newAccessToken;
    }
    
    public String getNewAccessToken() {
        return newAccessToken;
    }
}