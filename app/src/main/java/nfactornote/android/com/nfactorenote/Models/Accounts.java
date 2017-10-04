package nfactornote.android.com.nfactorenote.Models;

/**
 * Created by User on 31-08-2017.
 */

public class Accounts {
    String full_name,business_user_id,email_id,mobile_number;

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getBusiness_user_id() {
        return business_user_id;
    }

    public void setBusiness_user_id(String business_user_id) {
        this.business_user_id = business_user_id;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public Accounts(String full_name, String business_user_id, String email_id, String mobile_number) {
        this.full_name=full_name;
        this.business_user_id=business_user_id;
        this.email_id=email_id;

        this.mobile_number=mobile_number;
    }
}
