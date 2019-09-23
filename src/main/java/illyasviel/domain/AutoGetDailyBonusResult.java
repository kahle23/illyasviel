package illyasviel.domain;

/**
 * Auto get daily bonus result.
 * @author Kahle
 */
public class AutoGetDailyBonusResult {
    private String websiteName;
    private Boolean status = false;
    private String result;

    public String getWebsiteName() {

        return websiteName;
    }

    public void setWebsiteName(String websiteName) {

        this.websiteName = websiteName;
    }

    public Boolean getStatus() {

        return status;
    }

    public void setStatus(Boolean status) {

        this.status = status;
    }

    public String getResult() {

        return result;
    }

    public void setResult(String result) {

        this.result = result;
    }

}
