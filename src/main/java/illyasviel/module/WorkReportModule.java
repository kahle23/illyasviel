package illyasviel.module;

import artoria.beans.BeanUtils;
import artoria.common.Param;
import artoria.common.Result;
import artoria.file.FileUtils;
import artoria.lifecycle.LifecycleException;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.time.DateTime;
import artoria.time.DateUtils;
import artoria.util.BooleanUtils;
import artoria.util.StringUtils;
import artoria.validate.ValidateUtils;
import illyasviel.common.Module;
import illyasviel.domain.WorkReportParam;
import illyasviel.domain.WorkReportResult;
import illyasviel.function.GitlabWorkReportSpider;
import illyasviel.function.ZentaoWorkReportSpider;

import java.io.File;
import java.util.*;

import static artoria.common.Constants.*;
import static illyasviel.common.IllyaErrorCode.*;

/**
 * Work report module.
 * @author Kahle
 */
public class WorkReportModule implements Module {
    private static final String OUTPUT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss Z";
    private static final String SHOW_DATE_PATTERN = "yyyy-MM-dd Z EEEE";
    private static final String INPUT_DATE_PATTERN = "yyyy-MM-dd";
    private static String[] keys = new String[]{"occurredTime", "content", "targetUser", "sourceName", "sourceAddress"};
    private static Logger log = LoggerFactory.getLogger(WorkReportModule.class);
    private GitlabWorkReportSpider gitlabWorkReportSpider = new GitlabWorkReportSpider();
    private ZentaoWorkReportSpider zentaoWorkReportSpider = new ZentaoWorkReportSpider();

    @Override
    public void initialize() throws LifecycleException {
    }

    @Override
    public void destroy() throws Exception {
    }

    @Override
    public Result<Object> call(Param<Map<String, Object>> param) throws Exception {
        ValidateUtils.notNull(param, GLB_PARAM_MAP_NOT_NULL);
        Result<Object> result = new Result<Object>();
        Map<String, Object> data = param.getData();
        WorkReportParam workReportParam = BeanUtils.mapToBean(data, WorkReportParam.class);
        if (StringUtils.isBlank(workReportParam.getFileEncoding())) {
            workReportParam.setFileEncoding(DEFAULT_CHARSET_NAME);
        }
        this.handleQueryTime(workReportParam);
        List<WorkReportResult> resList = new ArrayList<WorkReportResult>();
        this.takeResultData(workReportParam, resList);
        Map<String, List<WorkReportResult>> resMap = this.handleWorkReportResult(resList);
        this.saveToText(workReportParam, resMap);
        boolean createTable = BooleanUtils.parseBoolean(workReportParam.getCreateTable());
        if (createTable) { this.saveToTable(workReportParam, resMap); }
        result.setData(resList.size());
        return result;
    }

    private File takeSavePath(WorkReportParam param, String suffix) {
        ValidateUtils.notNull(param, WRM_WORK_REPORT_PARAM_NOT_NULL);
        ValidateUtils.notBlank(suffix, WRM_SAVE_FILE_SUFFIX_NOT_BLANK);
        String beginTime = param.getBeginTime();
        String endTime = param.getEndTime();
        ValidateUtils.notBlank(beginTime, WRM_BEGIN_TIME_NOT_BLANK);
        ValidateUtils.notBlank(endTime, WRM_END_TIME_NOT_BLANK);
        suffix = suffix.startsWith(DOT) ? suffix : DOT + suffix;
        String fileName = "WorkReport_" + beginTime + "_" + endTime + suffix;
        String savePathStr = param.getSavePath();
        savePathStr = StringUtils.isBlank(savePathStr) ? "./" : savePathStr;
        return new File(new File(savePathStr), fileName);
    }

    private void handleQueryTime(WorkReportParam param) throws Exception {
        ValidateUtils.notNull(param, WRM_WORK_REPORT_PARAM_NOT_NULL);
        String beginTime = param.getBeginTime();
        String endTime = param.getEndTime();
        boolean notBlankBeginTime = StringUtils.isNotBlank(beginTime);
        boolean notBlankEndTime = StringUtils.isNotBlank(endTime);
        if (notBlankBeginTime && notBlankEndTime) {
            Date beginTimeDate = DateUtils.parse(beginTime, INPUT_DATE_PATTERN);
            Date endTimeDate = DateUtils.parse(endTime, INPUT_DATE_PATTERN);
            ValidateUtils.state(beginTimeDate.before(endTimeDate), WRM_BEGIN_TIME_BEFORE_END);
        }
        else {
            Date date = !notBlankBeginTime && !notBlankEndTime
                    ? new Date()
                    : DateUtils.parse(notBlankBeginTime ? beginTime : endTime, INPUT_DATE_PATTERN);
            DateTime weekOfStart = DateUtils.getWeekOfStart(DateUtils.create(date), 1);
            DateTime weekOfEnd = DateUtils.getWeekOfEnd(DateUtils.create(date), 1);
            weekOfEnd.addMillisecond(1);
            param.setBeginTime(DateUtils.format(weekOfStart, INPUT_DATE_PATTERN));
            param.setEndTime(DateUtils.format(weekOfEnd, INPUT_DATE_PATTERN));
        }
    }

    private void takeResultData(WorkReportParam param, List<WorkReportResult> resList) throws Exception {
        ValidateUtils.notNull(param, WRM_WORK_REPORT_PARAM_NOT_NULL);
        String sourceNames = param.getSourceNames();
        ValidateUtils.notBlank(sourceNames, WRM_SOURCE_NAMES_NOT_BLANK);
        String[] sourceNameArray = sourceNames.split(COMMA);
        for (String sourceName : sourceNameArray) {
            log.info("Start processing \"" + sourceName + "\"... ");
            if ("gitlab".equalsIgnoreCase(sourceName)) {
                resList.addAll(gitlabWorkReportSpider.handle(param));
            }
            else if ("zentao".equalsIgnoreCase(sourceName)) {
                resList.addAll(zentaoWorkReportSpider.handle(param));
            }
            else {
            }
        }
    }

    private void saveToText(WorkReportParam param, Map<String, List<WorkReportResult>> resMap) throws Exception {
        ValidateUtils.notNull(param, WRM_WORK_REPORT_PARAM_NOT_NULL);
        ValidateUtils.notNull(resMap, WRM_RESULT_MAP_NOT_NULL);
        String fileEncoding = param.getFileEncoding();
        ValidateUtils.notBlank(fileEncoding, WRM_FILE_ENCODING_NOT_BLANK);
        File savePath = this.takeSavePath(param, "txt");
        StringBuilder builder = new StringBuilder();
        List<String> keyList = new ArrayList<String>(resMap.keySet());
        Collections.sort(keyList);
        for (String key : keyList) {
            List<WorkReportResult> list = resMap.get(key);
            builder.append(key).append(NEWLINE);
            int count = 1;
            for (WorkReportResult result : list) {
                builder.append(count++)
                        .append(DOT)
                        .append(BLANK_SPACE)
                        .append(result.getContent())
                        .append(NEWLINE);
            }
            builder.append(NEWLINE).append(NEWLINE);
        }
        String content = builder.toString();
        FileUtils.write(content.getBytes(fileEncoding), savePath);
        log.info("Save the file to \"" + savePath + "\" success. ");
    }

    private void saveToTable(WorkReportParam param, Map<String, List<WorkReportResult>> resMap) throws Exception {
        ValidateUtils.notNull(param, WRM_WORK_REPORT_PARAM_NOT_NULL);
        ValidateUtils.notNull(resMap, WRM_RESULT_MAP_NOT_NULL);
        String fileEncoding = param.getFileEncoding();
        ValidateUtils.notBlank(fileEncoding, WRM_FILE_ENCODING_NOT_BLANK);
        File savePath = this.takeSavePath(param, "csv");
        StringBuilder builder = new StringBuilder();
        for (String key : keys) { builder.append(key).append(COMMA); }
        builder.append(NEWLINE);
        List<String> resMapKeyList = new ArrayList<String>(resMap.keySet());
        Collections.sort(resMapKeyList);
        for (String resMapKey : resMapKeyList) {
            List<WorkReportResult> list = resMap.get(resMapKey);
            List<Map<String, Object>> mapList = BeanUtils.beanToMapInList(list);
            for (Map<String, Object> beanToMap : mapList) {
                for (String key : keys) {
                    Object val = beanToMap.get(key);
                    builder.append(val);
                    builder.append(COMMA);
                }
                builder.append(NEWLINE);
            }
        }
        String content = builder.toString();
        FileUtils.write(content.getBytes(fileEncoding), savePath);
        log.info("Save the file to \"" + savePath + "\" success. ");
    }

    private Map<String, List<WorkReportResult>> handleWorkReportResult(List<WorkReportResult> resList) throws Exception {
        ValidateUtils.notNull(resList, WRM_RESULT_LIST_NOT_NULL);
        Map<String, List<WorkReportResult>> workReportMap = new HashMap<String, List<WorkReportResult>>();
        for (WorkReportResult result : resList) {
            String occurredTime = result.getOccurredTime();
            Date parse = DateUtils.parse(occurredTime, OUTPUT_DATE_PATTERN);
            String key = DateUtils.format(parse, SHOW_DATE_PATTERN);
            List<WorkReportResult> list = workReportMap.get(key);
            if (list == null) {
                list = new ArrayList<WorkReportResult>();
                workReportMap.put(key, list);
            }
            list.add(result);
        }
        for (List<WorkReportResult> list : workReportMap.values()) {
            Collections.sort(list, new Comparator<WorkReportResult>() {
                @Override
                public int compare(WorkReportResult o1, WorkReportResult o2) {
                    if (o1 == null || o2 == null) { return 0; }
                    String occurredTime1 = o1.getOccurredTime();
                    String occurredTime2 = o2.getOccurredTime();
                    if (occurredTime1 == null || occurredTime2 == null) { return 0; }
                    return occurredTime1.hashCode() - occurredTime2.hashCode();
                }
            });
        }
        return workReportMap;
    }

}
