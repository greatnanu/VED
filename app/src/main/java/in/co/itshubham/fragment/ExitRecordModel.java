package in.co.itshubham.fragment;

public class ExitRecordModel {
    private String name ;
    private String id ;
    private String studentDetails ;
    private String studentName ;
    private String mobile ;
    private String relation ;
    private String date;
    private String image_url;
    private String audio_url;
    private String address;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }



    public ExitRecordModel(String name, String id, String studentDetails, String mobile, String relation, String address, String date, String image_url, String audio_url) {
        this.name = name;
        this.id = id;
        this.studentDetails = studentDetails;
        this.mobile = mobile;
        this.relation = relation;
        this.address = address;
        this.date = date;
        this.image_url = image_url;
        this.audio_url = audio_url;
    }
    public ExitRecordModel(){

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentDetails() {
        return studentDetails;
    }

    public void setStudentDetails(String studentDetails) {
        this.studentDetails = studentDetails;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getAudio_url() {
        return audio_url;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }

}
