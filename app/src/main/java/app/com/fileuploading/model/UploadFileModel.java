package app.com.fileuploading.model;


public class UploadFileModel {
    public String name;
    public String url;
    public String userName;

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String uploaderName;

    public UploadFileModel() {
    }

    public UploadFileModel(String name, String url,String userName) {
        this.name = name;
        this.url = url;
        this.userName=userName;
    }

    public String getUserId() {
        return userName;
    }

    public void setUserId(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
