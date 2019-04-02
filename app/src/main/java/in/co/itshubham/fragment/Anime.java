package in.co.itshubham.fragment;

public class Anime {
    private String name ;
    private String id ;
    private String gender ;
    private String mobile ;
    private String purpose ;
    private String address;
    private String date;
    private String exittime;

    public String getExittime() {
        return exittime;
    }

    public void setExittime(String exittime) {
        this.exittime = exittime;
    }

    private String image_url;
    private String audio_url;
    public String getAudio_url() {
        return audio_url;
    }
    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }

    public Anime() {
    }

    public Anime(String name, String purpose, String date, String image_url) {
        this.name = name;
        this.purpose = purpose;
        this.date = date;
        this.image_url = image_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getDate() {
        return date;
    }

    public String getImage_url() {
        return image_url;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
