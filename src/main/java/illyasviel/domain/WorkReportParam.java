package illyasviel.domain;

/**
 * Work report param.
 * @author Kahle
 */
public class WorkReportParam {
    /**
     * Source name list: gitlab, zentao
     * If multiple English commas are separated.
     */
    private String sourceNames;
    /**
     * Begin time (yyyy-MM-dd).
     */
    private String beginTime;
    /**
     * End time (yyyy-MM-dd).
     */
    private String endTime;
    /**
     * Create table.
     */
    private String createTable;
    /**
     * Save path.
     */
    private String savePath;
    /**
     * File encoding.
     */
    private String fileEncoding;
    /**
     * Gitlab address.
     */
    private String gitlabAddress;
    /**
     * Gitlab projects (username:projectName:branchName).
     * If multiple English commas are separated.
     */
    private String gitlabProjects;
    /**
     * Gitlab committer.
     * If multiple English commas are separated.
     */
    private String gitlabCommitter;
    /**
     * Gitlab cookies.
     */
    private String gitlabCookies;
    /**
     * Zentao address.
     */
    private String zentaoAddress;
    /**
     * Zentao cookies.
     */
    private String zentaoCookies;
    /**
     * Zentao show task id.
     */
    private String zentaoShowTaskId;
    /**
     * Zentao task prefix.
     */
    private String zentaoTaskPrefix;
    /**
     * Zentao task suffix.
     */
    private String zentaoTaskSuffix;
    /**
     * Zentao show bug id.
     */
    private String zentaoShowBugId;
    /**
     * Zentao bug prefix.
     */
    private String zentaoBugPrefix;
    /**
     * Zentao bug suffix.
     */
    private String zentaoBugSuffix;

    public String getSourceNames() {

        return sourceNames;
    }

    public void setSourceNames(String sourceNames) {

        this.sourceNames = sourceNames;
    }

    public String getBeginTime() {

        return beginTime;
    }

    public void setBeginTime(String beginTime) {

        this.beginTime = beginTime;
    }

    public String getEndTime() {

        return endTime;
    }

    public void setEndTime(String endTime) {

        this.endTime = endTime;
    }

    public String getCreateTable() {

        return createTable;
    }

    public void setCreateTable(String createTable) {

        this.createTable = createTable;
    }

    public String getSavePath() {

        return savePath;
    }

    public void setSavePath(String savePath) {

        this.savePath = savePath;
    }

    public String getFileEncoding() {

        return fileEncoding;
    }

    public void setFileEncoding(String fileEncoding) {

        this.fileEncoding = fileEncoding;
    }

    public String getGitlabAddress() {

        return gitlabAddress;
    }

    public void setGitlabAddress(String gitlabAddress) {

        this.gitlabAddress = gitlabAddress;
    }

    public String getGitlabProjects() {

        return gitlabProjects;
    }

    public void setGitlabProjects(String gitlabProjects) {

        this.gitlabProjects = gitlabProjects;
    }

    public String getGitlabCommitter() {

        return gitlabCommitter;
    }

    public void setGitlabCommitter(String gitlabCommitter) {

        this.gitlabCommitter = gitlabCommitter;
    }

    public String getGitlabCookies() {

        return gitlabCookies;
    }

    public void setGitlabCookies(String gitlabCookies) {

        this.gitlabCookies = gitlabCookies;
    }

    public String getZentaoAddress() {

        return zentaoAddress;
    }

    public void setZentaoAddress(String zentaoAddress) {

        this.zentaoAddress = zentaoAddress;
    }

    public String getZentaoCookies() {

        return zentaoCookies;
    }

    public void setZentaoCookies(String zentaoCookies) {

        this.zentaoCookies = zentaoCookies;
    }

    public String getZentaoShowTaskId() {

        return zentaoShowTaskId;
    }

    public void setZentaoShowTaskId(String zentaoShowTaskId) {

        this.zentaoShowTaskId = zentaoShowTaskId;
    }

    public String getZentaoTaskPrefix() {

        return zentaoTaskPrefix;
    }

    public void setZentaoTaskPrefix(String zentaoTaskPrefix) {

        this.zentaoTaskPrefix = zentaoTaskPrefix;
    }

    public String getZentaoTaskSuffix() {

        return zentaoTaskSuffix;
    }

    public void setZentaoTaskSuffix(String zentaoTaskSuffix) {

        this.zentaoTaskSuffix = zentaoTaskSuffix;
    }

    public String getZentaoShowBugId() {

        return zentaoShowBugId;
    }

    public void setZentaoShowBugId(String zentaoShowBugId) {

        this.zentaoShowBugId = zentaoShowBugId;
    }

    public String getZentaoBugPrefix() {

        return zentaoBugPrefix;
    }

    public void setZentaoBugPrefix(String zentaoBugPrefix) {

        this.zentaoBugPrefix = zentaoBugPrefix;
    }

    public String getZentaoBugSuffix() {

        return zentaoBugSuffix;
    }

    public void setZentaoBugSuffix(String zentaoBugSuffix) {

        this.zentaoBugSuffix = zentaoBugSuffix;
    }

}
