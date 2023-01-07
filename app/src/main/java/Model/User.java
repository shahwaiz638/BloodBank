package Model;

public class User {
    private String name,bloodgroup,id,email,idNumber,phonenumber,profilepictureurl,search, type;

    public User(String name, String bloodgroup, String email, String phoneNumber, String profilePictureUrl, String type) {
        this.name = name;
        this.bloodgroup = bloodgroup;
        this.email = email;
        this.phonenumber = phoneNumber;
        this.profilepictureurl = profilePictureUrl;
        this.type = type;
    }

    public User(String name, String bloodgroup, String id, String email, String idNumber,
                String phoneNumber, String profilePictureUrl, String search, String type) {
        this.name = name;
        this.bloodgroup = bloodgroup;
        this.id = id;
        this.email = email;
        this.idNumber = idNumber;
        this.phonenumber = phoneNumber;
        this.profilepictureurl = profilePictureUrl;
        this.search = search;
        this.type = type;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getbloodgroup() {
        return bloodgroup;
    }

    public void setbloodgroup(String bloodGroup) {
        this.bloodgroup = bloodGroup;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getphonenumber() {
        return phonenumber;
    }

    public void setphonenumber(String phoneNumber) {
        this.phonenumber = phoneNumber;
    }

    public String getprofilepictureurl() {
        return profilepictureurl;
    }

    public void setprofilepictureurl(String profilePictureUrl) {
        this.profilepictureurl = profilePictureUrl;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
