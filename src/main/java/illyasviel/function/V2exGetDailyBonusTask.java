package illyasviel.function;

import artoria.codec.Base64Utils;
import artoria.exception.ExceptionUtils;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.net.*;
import artoria.util.StringUtils;
import artoria.util.ThreadLocalUtils;
import artoria.validate.ValidateUtils;
import illyasviel.domain.AutoGetDailyBonusParam;
import illyasviel.domain.AutoGetDailyBonusResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import static artoria.common.Constants.COLON;
import static artoria.common.Constants.COMMA;
import static illyasviel.common.IllyaErrorCode.*;

/**
 * Auto get daily bonus implement for v2ex.
 * @author Kahle
 */
public class V2exGetDailyBonusTask extends TimerTask {
    private static final String V2EX_DAILY_MISSION_ADDRESS = "https://www.v2ex.com/mission/daily";
    private static final String V2EX_BALANCE_ADDRESS = "https://www.v2ex.com/balance";
    private static final String V2EX_HOME_ADDRESS = "https://www.v2ex.com";
    private static final String DAILY_BONUS_ADDRESS_KEY = "DAILY_BONUS_ADDRESS";
    private static final String WEBSITE_NAME = "v2ex";
    private static Logger log = LoggerFactory.getLogger(V2exGetDailyBonusTask.class);
    private static HttpClient httpClient = new DefaultHttpClient();
    private Map<String, Map<String, String>> cookiesMap = new HashMap<String, Map<String, String>>();
    private AutoGetDailyBonusParam autoGetDailyBonusParam;

    public V2exGetDailyBonusTask(AutoGetDailyBonusParam autoGetDailyBonusParam) {
        ValidateUtils.notNull(autoGetDailyBonusParam, AGD_AUTO_GET_DAILY_BONUS_PARAM_NOT_NULL);
        this.autoGetDailyBonusParam = autoGetDailyBonusParam;
        String v2exCookies = autoGetDailyBonusParam.getV2exCookies();
        ValidateUtils.notBlank(v2exCookies, AGD_V2EX_COOKIES_NOT_BLANK);
        String[] v2exCookieArray = v2exCookies.split(COMMA);
        for (String v2exCookie : v2exCookieArray) {
            if (StringUtils.isBlank(v2exCookie)) { continue; }
            if (!v2exCookie.contains(COLON)) { continue; }
            String[] split = v2exCookie.split(COLON);
            String alias = split[0];
            String base64Cookie = split[1];
            HttpRequest request = new HttpRequest();
            byte[] decode = Base64Utils.decodeFromString(base64Cookie);
            request.addCookies(new String(decode));
            Map<String, String> cookies = request.getCookies();
            cookiesMap.put(alias, new HashMap<String, String>(cookies));
        }
    }

    @Override
    public void run() {
        for (Map.Entry<String, Map<String, String>> entry : cookiesMap.entrySet()) {
            String alias = entry.getKey();
            Map<String, String> v2exCookies = entry.getValue();
            AutoGetDailyBonusResult result = new AutoGetDailyBonusResult();
            result.setWebsiteName(WEBSITE_NAME);
            try {
                this.visitHomePage(v2exCookies);
                log.info("Alias \"" + alias + "\" v2exCookies: " + v2exCookies);
                this.takeDailyBonusAddress(v2exCookies);
                this.doGetDailyBonus(v2exCookies, result);
                this.takeBalanceInformation(v2exCookies, result);
            }
            catch (Exception e) {
                String message = "An exception occurred at runtime: ";
                message += ExceptionUtils.toString(e);
                result.setStatus(false);
                result.setResult(message);
            }
            log.info("Alias \"" + alias + "\" get daily bonus in \"" + WEBSITE_NAME + "\" "
                    + (result.getStatus() ? "success" : "failure")
                    + " and the message is \"" + result.getResult() + "\". ");
        }
    }

    public void visitHomePage(Map<String, String> v2exCookies) throws IOException {
        ValidateUtils.notEmpty(v2exCookies, AGD_V2EX_COOKIES_NOT_EMPTY);
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.GET);
        request.setUrl(V2EX_HOME_ADDRESS);
        request.addCookies(v2exCookies);
        HttpResponse response = httpClient.execute(request);
        v2exCookies.putAll(response.getCookies());
    }

    public void takeDailyBonusAddress(Map<String, String> v2exCookies) throws IOException {
        ValidateUtils.notEmpty(v2exCookies, AGD_V2EX_COOKIES_NOT_EMPTY);
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.GET);
        request.setUrl(V2EX_DAILY_MISSION_ADDRESS);
        request.addCookies(v2exCookies);
        HttpResponse response = httpClient.execute(request);
        v2exCookies.putAll(response.getCookies());
        Element mainElement = Jsoup.parse(response.getBodyAsString()).getElementById("Main");
        Elements inputElements = mainElement.getElementsByAttributeValue("value", "领取 X 铜币");
        ValidateUtils.notEmpty(inputElements, AGD_NOT_TIME_TO_GET_OR_WEBSITE_CHANGED);
        String onclick = inputElements.attr("onclick");
        int index = onclick.indexOf("'");
        ValidateUtils.state(index != -1, AGD_NOT_PROCESS_ONCLICK);
        String dailyBonusAddress = onclick.substring(index + 1);
        index = dailyBonusAddress.indexOf("'");
        ValidateUtils.state(index != -1, AGD_NOT_PROCESS_ONCLICK);
        dailyBonusAddress = V2EX_HOME_ADDRESS + dailyBonusAddress.substring(0, index);
        log.debug("Daily bonus address: " + dailyBonusAddress);
        ThreadLocalUtils.setValue(DAILY_BONUS_ADDRESS_KEY, dailyBonusAddress);
    }

    public void takeBalanceInformation(Map<String, String> v2exCookies, AutoGetDailyBonusResult result) {
        ValidateUtils.notEmpty(v2exCookies, AGD_V2EX_COOKIES_NOT_EMPTY);
        try {
            HttpRequest request = new HttpRequest();
            request.setMethod(HttpMethod.GET);
            request.setUrl(V2EX_BALANCE_ADDRESS);
            request.addCookies(v2exCookies);
            HttpResponse response = httpClient.execute(request);
            v2exCookies.putAll(response.getCookies());
            Element mainElement = Jsoup.parse(response.getBodyAsString()).getElementById("Main");
            Elements tableElements = mainElement.getElementsByTag("table");
            ValidateUtils.state(tableElements.size() >= 4, AGD_TABLE_ELEMENTS_SIZE_ERROR);
            Element tableElement = tableElements.get(3);
            Elements trElements = tableElement.getElementsByTag("tr");
            ValidateUtils.state(trElements.size() > 1, AGD_TR_ELEMENTS_SIZE_ERROR_IN_TABLE);
            Element firstRecord = trElements.get(1);
            Elements tdElements = firstRecord.getElementsByTag("td");
            StringBuilder balance = new StringBuilder("(");
            boolean isFirst = true;
            for (Element tdElement : tdElements) {
                if (isFirst) { isFirst = false; }
                else { balance.append(", "); }
                balance.append(tdElement.text());
            }
            balance.append("). ");
            result.setResult(result.getResult() + balance);
        }
        catch (Exception e) {
            log.error("Execute takeBalanceInfo error", e);
            result.setResult(result.getResult()
                    + "(An error occurred while getting balance information). ");
        }
    }

    public void doGetDailyBonus(Map<String, String> v2exCookies, AutoGetDailyBonusResult result) throws IOException {
        ValidateUtils.notEmpty(v2exCookies, AGD_V2EX_COOKIES_NOT_EMPTY);
        String dailyBonusAddress = (String) ThreadLocalUtils.getValue(DAILY_BONUS_ADDRESS_KEY);
        ThreadLocalUtils.remove(DAILY_BONUS_ADDRESS_KEY);
        ValidateUtils.notBlank(dailyBonusAddress, AGD_DAILY_BONUS_ADDRESS_NOT_BLANK);
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.GET);
        request.setUrl(dailyBonusAddress);
        request.addCookies(v2exCookies);
        HttpResponse response = httpClient.execute(request);
        v2exCookies.putAll(response.getCookies());
        Element mainElement = Jsoup.parse(response.getBodyAsString()).getElementById("Main");
        Elements grayElements = mainElement.getElementsByClass("gray");
        ValidateUtils.notEmpty(grayElements, AGD_GRAY_ELEMENTS_NOT_EMPTY);
        Elements cellElements = mainElement.getElementsByClass("cell");
        ValidateUtils.state(cellElements.size() >= 3, AGD_CELL_ELEMENTS_ENOUGH);
        String dailyBonusGetInfo = grayElements.get(0).text().substring(1);
        String loginNumInfo = cellElements.get(2).text();
        result.setStatus("每日登录奖励已领取".equals(dailyBonusGetInfo));
        result.setResult(dailyBonusGetInfo + ". " + loginNumInfo + ". ");
    }

}
