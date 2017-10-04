package nfactornote.android.com.nfactorenote.Models;

/**
 * Created by User on 18-08-2017.
 */

public class UrlDetails {
    String orginalUrl,totalclick,url_id,customizetext,shortText,datentime,url_title;
    public UrlDetails(String orginalUrl, String totalclick, String url_id, String customizetext,
                      String shortText, String datentime, String url_title) {
        this.orginalUrl=orginalUrl;
        this.totalclick=totalclick;
        this.url_id=url_id;
        this.customizetext=customizetext;
        this.shortText=shortText;
        this.datentime=datentime;
        this.url_title=url_title;

    }

    public String getOrginalUrl() {
        return orginalUrl;
    }

    public void setOrginalUrl(String orginalUrl) {
        this.orginalUrl = orginalUrl;
    }

    public String getTotalclick() {
        return totalclick;
    }

    public void setTotalclick(String totalclick) {
        this.totalclick = totalclick;
    }

    public String getUrl_id() {
        return url_id;
    }

    public void setUrl_id(String url_id) {
        this.url_id = url_id;
    }

    public String getCustomizetext() {
        return customizetext;
    }

    public void setCustomizetext(String customizetext) {
        this.customizetext = customizetext;
    }

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public String getDatentime() {
        return datentime;
    }

    public void setDatentime(String datentime) {
        this.datentime = datentime;
    }

    public String getUrl_title() {
        return url_title;
    }

    public void setUrl_title(String url_title) {
        this.url_title = url_title;
    }
}
