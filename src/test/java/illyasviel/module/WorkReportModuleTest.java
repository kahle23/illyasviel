package illyasviel.module;

import artoria.beans.BeanUtils;
import artoria.common.Param;
import artoria.common.Result;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import illyasviel.domain.WorkReportParam;
import org.junit.Test;

import java.util.Map;

public class WorkReportModuleTest {
    private static Logger log = LoggerFactory.getLogger(WorkReportModuleTest.class);
    private static WorkReportModule workReportModule = new WorkReportModule();

    @Test
    public void test1() throws Exception {
        WorkReportParam workReportParam = new WorkReportParam();
        workReportParam.setSourceNames("gitlab");
        workReportParam.setCreateTable("true");
        workReportParam.setSavePath("E:\\");
        workReportParam.setFileEncoding("GB2312");
        workReportParam.setGitlabAddress("http://");
        workReportParam.setGitlabCookies("");
        workReportParam.setGitlabProjects("username:projectName:branchName,username1:projectName1:branchName1");
        workReportParam.setGitlabCommitter("Kahle");
        Param<Map<String, Object>> param = new Param<Map<String, Object>>();
        param.setData(BeanUtils.beanToMap(workReportParam));
        Result<Object> call = workReportModule.call(param);
        log.info("Success: " + call.getSuccess()
                + ", Message: " + call.getMessage() + ", Data: " + call.getData());
    }

    @Test
    public void test2() throws Exception {
        WorkReportParam workReportParam = new WorkReportParam();
        workReportParam.setSourceNames("zentao");
        workReportParam.setCreateTable("true");
        workReportParam.setSavePath("E:\\");
        workReportParam.setFileEncoding("GB2312");
        workReportParam.setZentaoAddress("http://");
        workReportParam.setZentaoCookies("");
        workReportParam.setZentaoShowBugId("true");
        workReportParam.setZentaoShowTaskId("true");
        Param<Map<String, Object>> param = new Param<Map<String, Object>>();
        param.setData(BeanUtils.beanToMap(workReportParam));
        Result<Object> call = workReportModule.call(param);
        log.info("Success: " + call.getSuccess()
                + ", Message: " + call.getMessage() + ", Data: " + call.getData());
    }

}
