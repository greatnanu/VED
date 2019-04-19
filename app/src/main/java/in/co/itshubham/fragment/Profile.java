package in.co.itshubham.fragment;

public class Profile {
    static String person_name;
    static String mobile;
    static String email;
    static String address;
    static String image_url;
    static String desc;

    public static String getPerson_name() {
        return person_name;
    }

    public static void setPerson_name(String person_name) {
        Profile.person_name = person_name;
    }

    public static String getMobile() {
        return mobile;
    }

    public static void setMobile(String mobile) {
        Profile.mobile = mobile;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Profile.email = email;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        Profile.address = address;
    }

    public static String getImage_url() {
        return image_url;
    }

    public static void setImage_url(String image_url) {
        Profile.image_url = image_url;
    }

    public static String getDesc() {
        return desc;
    }

    public static void setDesc(String desc) {
        Profile.desc = desc;
    }
}
