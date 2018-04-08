package app.com.fileuploading.model;

public class SignupModel {

    public String name;
    public String email;
    public String userid;
    public String number;

    public SignupModel(String userId, String name, String email, String number) {
        this.name = name;
        this.email = email;
        this.userid = userId;
        this.number = number;
    }

    public SignupModel(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
