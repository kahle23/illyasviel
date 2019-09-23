package illyasviel.function;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.net.*;
import artoria.time.DateUtils;
import artoria.util.BooleanUtils;
import artoria.validate.ValidateUtils;
import illyasviel.domain.WorkReportParam;
import illyasviel.domain.WorkReportResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static artoria.common.Constants.EMPTY_STRING;
import static illyasviel.common.IllyaErrorCode.*;

/**
 * Zentao work report spider.
 * @author Kahle
 */
public class ZentaoWorkReportSpider extends AbstractWorkReportSpider {
    private static final String ZENTAO_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String SOURCE = "Zentao";
    private static Logger log = LoggerFactory.getLogger(ZentaoWorkReportSpider.class);
    private static HttpClient httpClient = new DefaultHttpClient();

    @Override
    public List<WorkReportResult> handle(WorkReportParam param) throws Exception {
        ValidateUtils.notNull(param, WRM_WORK_REPORT_PARAM_NOT_NULL);
        List<WorkReportResult> resList = new ArrayList<WorkReportResult>();
        resList.addAll(this.pagingQueryTaskList(param));
        resList.addAll(this.pagingQueryBugList(param));
        return resList;
    }

    public List<WorkReportResult> pagingQueryBugList(WorkReportParam param) throws Exception {
        log.info("Dealing with bug list under \"" + SOURCE + "\". ");
        ValidateUtils.notNull(param, WRM_WORK_REPORT_PARAM_NOT_NULL);
        String zentaoAddress = param.getZentaoAddress();
        String zentaoCookies = param.getZentaoCookies();
        String bugPrefix = param.getZentaoBugPrefix();
        bugPrefix = bugPrefix == null ? EMPTY_STRING : bugPrefix;
        String bugSuffix = param.getZentaoBugSuffix();
        bugSuffix = bugSuffix == null ? EMPTY_STRING : bugSuffix;
        boolean showBugId = BooleanUtils.parseBoolean(param.getZentaoShowBugId());
        ValidateUtils.notBlank(zentaoAddress, WRM_ZENTAO_ADDRESS_NOT_BLANK);
        Date beginTime = DateUtils.parse(param.getBeginTime(), INPUT_DATE_PATTERN);
        Date endTime = DateUtils.parse(param.getEndTime(), INPUT_DATE_PATTERN);
        List<WorkReportResult> resList = new ArrayList<WorkReportResult>();
        for (int pageNum = 1; true; pageNum++) {
            boolean isFinish = false;
            String address = zentaoAddress + "/zentao/my-bug-resolvedBy-id_desc-0-";
            address += DEFAULT_PAGE_SIZE + "-" + pageNum + ".html";
            HttpRequest request = new HttpRequest();
            request.setMethod(HttpMethod.GET);
            request.setUrl(address);
            request.addCookies(zentaoCookies);
            HttpResponse httpResponse = httpClient.execute(request);
            Element bugList = Jsoup.parse(httpResponse.getBodyAsString()).getElementById("bugList");
            if (bugList == null) { log.info("Can not find element by id \"bugList\". "); break; }
            Elements trElements = bugList.getElementsByTag("tr");
            int length = trElements.size() - 1;
            for (int i = 1; i < length; i++) {
                Element trElement = trElements.get(i);
                Elements tdElements = trElement.getElementsByTag("td");
                ValidateUtils.state(tdElements.size() == 10, WRM_BUG_TD_ELEMENTS_SIZE_ERROR);
                Elements bugContentCell = tdElements.get(4).getElementsByTag("a");
                String bugAddress = bugContentCell.get(0).attr("href");
                String bugContent = bugContentCell.get(0).text();
                String occurredTimeStr = this.takeOccurredTime(param, bugAddress, "解决");
                Date occurredTime = DateUtils.parse(occurredTimeStr, ZENTAO_DATE_PATTERN);
                if (occurredTime.before(beginTime)) { isFinish = true; break; }
                if (occurredTime.after(endTime)) { continue; }
                String bugId = tdElements.get(0).text();
                String targetUser = tdElements.get(7).text();
                bugContent = bugPrefix + bugContent + bugSuffix;
                bugContent += (showBugId ? " (Bug " + bugId + ")" : EMPTY_STRING);
                WorkReportResult result = new WorkReportResult();
                result.setSourceName(SOURCE);
                result.setSourceAddress("bug:" + bugId);
                result.setOccurredTime(DateUtils.format(occurredTime, OUTPUT_DATE_PATTERN));
                result.setTargetUser(targetUser);
                result.setContent(bugContent);
                resList.add(result);
            }
            if (isFinish) { break; }
        }
        return resList;
    }

    public List<WorkReportResult> pagingQueryTaskList(WorkReportParam param) throws Exception {
        log.info("Dealing with task list under \"" + SOURCE + "\". ");
        ValidateUtils.notNull(param, WRM_WORK_REPORT_PARAM_NOT_NULL);
        String zentaoAddress = param.getZentaoAddress();
        String zentaoCookies = param.getZentaoCookies();
        String taskPrefix = param.getZentaoTaskPrefix();
        taskPrefix = taskPrefix == null ? EMPTY_STRING : taskPrefix;
        String taskSuffix = param.getZentaoBugSuffix();
        taskSuffix = taskSuffix == null ? EMPTY_STRING : taskSuffix;
        boolean showTaskId = BooleanUtils.parseBoolean(param.getZentaoShowBugId());
        ValidateUtils.notBlank(zentaoAddress, WRM_ZENTAO_ADDRESS_NOT_BLANK);
        Date beginTime = DateUtils.parse(param.getBeginTime(), INPUT_DATE_PATTERN);
        Date endTime = DateUtils.parse(param.getEndTime(), INPUT_DATE_PATTERN);
        List<WorkReportResult> resList = new ArrayList<WorkReportResult>();
        for (int pageNum = 1; true; pageNum++) {
            boolean isFinish = false;
            String address = zentaoAddress + "/zentao/my-task-finishedBy-id_desc-0-";
            address += DEFAULT_PAGE_SIZE + "-" + pageNum + ".html";
            HttpRequest request = new HttpRequest();
            request.setMethod(HttpMethod.GET);
            request.setUrl(address);
            request.addCookies(zentaoCookies);
            HttpResponse httpResponse = httpClient.execute(request);
            Element bugList = Jsoup.parse(httpResponse.getBodyAsString()).getElementById("tasktable");
            if (bugList == null) { log.info("Can not find element by id \"tasktable\". "); break; }
            Elements trElements = bugList.getElementsByTag("tr");
            int length = trElements.size() - 1;
            for (int i = 1; i < length; i++) {
                Element trElement = trElements.get(i);
                Elements tdElements = trElement.getElementsByTag("td");
                ValidateUtils.state(tdElements.size() == 13, WRM_TASK_TD_ELEMENTS_SIZE_ERROR);
                Elements taskContentCell = tdElements.get(3).getElementsByTag("a");
                String taskAddress = taskContentCell.get(0).attr("href");
                String taskContent = taskContentCell.get(0).text();
                String occurredTimeStr = this.takeOccurredTime(param, taskAddress, "完成");
                Date occurredTime = DateUtils.parse(occurredTimeStr, ZENTAO_DATE_PATTERN);
                if (occurredTime.before(beginTime)) { isFinish = true; break; }
                if (occurredTime.after(endTime)) { continue; }
                String taskId = tdElements.get(0).text();
                String targetUser = tdElements.get(6).text();
                taskContent = taskPrefix + taskContent + taskSuffix;
                taskContent += (showTaskId ? " (Task " + taskId + ")" : EMPTY_STRING);
                WorkReportResult result = new WorkReportResult();
                result.setSourceName(SOURCE);
                result.setSourceAddress("task:" + taskId);
                result.setOccurredTime(DateUtils.format(occurredTime, OUTPUT_DATE_PATTERN));
                result.setTargetUser(targetUser);
                result.setContent(taskContent);
                resList.add(result);
            }
            if (isFinish) { break; }
        }
        return resList;
    }

    private String takeOccurredTime(WorkReportParam param, String detailAddress, String keyword) throws Exception {
        ValidateUtils.notNull(param, WRM_WORK_REPORT_PARAM_NOT_NULL);
        ValidateUtils.notBlank(detailAddress, WRM_DETAIL_ADDRESS_NOT_BLANK);
        ValidateUtils.notBlank(keyword, WRM_KEYWORD_NOT_BLANK);
        String zentaoAddress = param.getZentaoAddress();
        String zentaoCookies = param.getZentaoCookies();
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.GET);
        request.setUrl(zentaoAddress + detailAddress);
        request.addCookies(zentaoCookies);
        HttpResponse httpResponse = httpClient.execute(request);
        Element historyItem = Jsoup.parse(httpResponse.getBodyAsString()).getElementById("historyItem");
        String historyItemString = historyItem.text();
        int endIndex = historyItemString.lastIndexOf(keyword);
        ValidateUtils.state(endIndex != -1, WRM_MUST_FIND_SPECIFIED_TAG);
        historyItemString = historyItemString.substring(0, endIndex);
        endIndex = historyItemString.lastIndexOf(",");
        ValidateUtils.state(endIndex != -1, WRM_MUST_FIND_SPECIFIED_TAG);
        historyItemString = historyItemString.substring(0, endIndex);
        int beginIndex = endIndex - 19;
        ValidateUtils.state(beginIndex != -1, WRM_TIME_FORMAT_MATCH);
        historyItemString = historyItemString.substring(beginIndex);
        return historyItemString.trim();
    }

}
