package illyasviel.function;

import artoria.beans.BeanUtils;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.net.*;
import artoria.time.DateUtils;
import artoria.util.CollectionUtils;
import artoria.util.StringUtils;
import artoria.validate.ValidateUtils;
import illyasviel.domain.WorkReportParam;
import illyasviel.domain.WorkReportResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static artoria.common.Constants.COLON;
import static artoria.common.Constants.COMMA;
import static illyasviel.common.IllyaErrorCode.*;

/**
 * Gitlab work report spider.
 * @author Kahle
 */
public class GitlabWorkReportSpider extends AbstractWorkReportSpider {
    private static final String GITLAB_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss Z";
    private static final String SOURCE = "Gitlab";
    private static Logger log = LoggerFactory.getLogger(GitlabWorkReportSpider.class);
    private static HttpClient httpClient = new DefaultHttpClient();

    @Override
    public List<WorkReportResult> handle(WorkReportParam param) throws Exception {
        ValidateUtils.notNull(param, WRM_WORK_REPORT_PARAM_NOT_NULL);
        String gitlabAddress = param.getGitlabAddress();
        String gitlabProjects = param.getGitlabProjects();
        ValidateUtils.notBlank(gitlabAddress, WRM_GITLAB_ADDRESS_NOT_BLANK);
        ValidateUtils.notBlank(gitlabProjects, WRM_GITLAB_PROJECTS_NOT_BLANK);
        List<WorkReportResult> resList = new ArrayList<WorkReportResult>();
        WorkReportParam workReportParam = BeanUtils.beanToBean(param, WorkReportParam.class);
        String[] projectArray = gitlabProjects.split(COMMA);
        for (String projectString : projectArray) {
            if (StringUtils.isBlank(projectString)) { continue; }
            String queryAddress = this.handleGitlabQueryAddress(gitlabAddress, projectString);
            workReportParam.setGitlabAddress(queryAddress);
            workReportParam.setGitlabProjects(projectString);
            log.info("Dealing with \"" + projectString + "\" under \"" + SOURCE + "\". ");
            resList.addAll(this.pagingQueryCommitList(workReportParam));
        }
        this.distinct(resList);
        return resList;
    }

    private void distinct(List<WorkReportResult> resList) {
        if (CollectionUtils.isEmpty(resList)) { return; }
        List<String> contentList = new ArrayList<String>();
        List<WorkReportResult> newList = new ArrayList<WorkReportResult>();
        for (WorkReportResult result : resList) {
            String content = result.getContent();
            if (!contentList.contains(content)) {
                newList.add(result);
                contentList.add(content);
            }
        }
        resList.clear();
        resList.addAll(newList);
    }

    private String handleGitlabQueryAddress(String gitlabAddress, String projectString) {
        ValidateUtils.notBlank(gitlabAddress, WRM_GITLAB_ADDRESS_NOT_BLANK);
        ValidateUtils.notBlank(projectString, WRM_PROJECT_STRING_NOT_BLANK);
        String[] split = projectString.split(COLON);
        String username = split[0];
        String projectName = split[1];
        String branchName = split[2];
        ValidateUtils.state(StringUtils.isNotBlank(username)
                && StringUtils.isNotBlank(projectName)
                && StringUtils.isNotBlank(branchName), WRM_PROJECT_STRING_NOT_WRONG);
        String result = gitlabAddress + "/" + username + "/" + projectName + "/commits/";
        result += branchName + "?ref=" + branchName;
        return result;
    }

    private List<WorkReportResult> pagingQueryCommitList(WorkReportParam param) throws Exception {
        ValidateUtils.notNull(param, WRM_WORK_REPORT_PARAM_NOT_NULL);
        Date beginTime = DateUtils.parse(param.getBeginTime(), INPUT_DATE_PATTERN);
        Date endTime = DateUtils.parse(param.getEndTime(), INPUT_DATE_PATTERN);
        String gitlabAddress = param.getGitlabAddress();
        String gitlabCookies = param.getGitlabCookies();
        String gitlabProject = param.getGitlabProjects();
        String gitlabCommitter = param.getGitlabCommitter();
        List<WorkReportResult> resList = new ArrayList<WorkReportResult>();
        for (int i = 0; true; i++) {
            boolean isFinish = false;
            Integer limit = DEFAULT_PAGE_SIZE;
            Integer offset = i * limit;
            String address = gitlabAddress + "&limit=" + limit + "&offset=" + offset;
            HttpRequest request = new HttpRequest();
            request.setMethod(HttpMethod.GET);
            request.setUrl(address);
            request.addCookies(gitlabCookies);
            HttpResponse httpResponse = httpClient.execute(request);
            Element commitsList = Jsoup.parse(httpResponse.getBodyAsString()).getElementById("commits-list");
            if (commitsList == null) { log.info("Can not find element by id \"commits-list\". "); break; }
            Elements commitsRowList = commitsList.getElementsByClass("row commits-row");
            if (CollectionUtils.isEmpty(commitsRowList)) {
                log.info("Can not find element by class \"row commits-row\". ");
                break;
            }
            for (Element commitsRow : commitsRowList) {
                Elements commitJsToggleContainer = commitsRow.getElementsByClass("commit js-toggle-container");
                if (commitJsToggleContainer == null) { continue; }
                for (Element bordered : commitJsToggleContainer) {
                    if (bordered == null) { continue; }
                    Elements timeAgoElements = bordered.getElementsByClass("time_ago");
                    String timeAgoStr = timeAgoElements.get(0).text();
                    Date timeAgo = DateUtils.parse(timeAgoStr, GITLAB_DATE_PATTERN);
                    if (timeAgo.before(beginTime)) { isFinish = true; break; }
                    if (timeAgo.after(endTime)) { continue; }
                    Elements commitAuthorElements = bordered.getElementsByClass("commit-author-name");
                    String commitAuthor = commitAuthorElements.get(0).text();
                    if (!gitlabCommitter.contains(commitAuthor)) { continue; }
                    Elements commitMessageElements = bordered.getElementsByClass("commit-row-message");
                    String commitMessage = commitMessageElements.get(0).text();
                    WorkReportResult result = new WorkReportResult();
                    result.setSourceName(SOURCE);
                    result.setSourceAddress(gitlabProject);
                    result.setOccurredTime(DateUtils.format(timeAgo, OUTPUT_DATE_PATTERN));
                    result.setTargetUser(commitAuthor);
                    result.setContent(commitMessage);
                    resList.add(result);
                }
                if (isFinish) { break; }
            }
            if (isFinish) { break; }
        }
        return resList;
    }

}
