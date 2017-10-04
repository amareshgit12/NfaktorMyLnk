package nfactornote.android.com.nfactorenote.Models;

/**
 * Created by User on 09-08-2017.
 */

public class DeviceDetails {
    String dev_userid,dev_usertype;

    public DeviceDetails() {

    }

    public String getDev_userid() {
        return dev_userid;
    }

    public void setDev_userid(String dev_userid) {
        this.dev_userid = dev_userid;
    }

    public String getDev_usertype() {
        return dev_usertype;
    }

    public void setDev_usertype(String dev_usertype) {
        this.dev_usertype = dev_usertype;
    }

    public DeviceDetails(String dev_userid, String dev_usertype) {
        this.dev_userid=dev_userid;
        this.dev_usertype=dev_usertype;

    }
}
