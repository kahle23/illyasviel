package illyasviel.domain;

/**
 * Auto get daily bonus param.
 * @author Kahle
 */
public class AutoGetDailyBonusParam {
    /**
     * Website names list: v2ex .
     */
    private String websiteNames;
    /**
     * Delay.
     */
    private Long delay;
    /**
     * Period.
     */
    private Long period;
    /**
     * V2ex cookies (alias:base64_cookie,alias1:base64_cookie1,).
     */
    private String v2exCookies;

    public String getWebsiteNames() {

        return websiteNames;
    }

    public void setWebsiteNames(String websiteNames) {

        this.websiteNames = websiteNames;
    }

    public Long getDelay() {

        return delay;
    }

    public void setDelay(Long delay) {

        this.delay = delay;
    }

    public Long getPeriod() {

        return period;
    }

    public void setPeriod(Long period) {

        this.period = period;
    }

    public String getV2exCookies() {

        return v2exCookies;
    }

    public void setV2exCookies(String v2exCookies) {

        this.v2exCookies = v2exCookies;
    }

}
