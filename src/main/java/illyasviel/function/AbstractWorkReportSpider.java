package illyasviel.function;

import illyasviel.domain.WorkReportParam;
import illyasviel.domain.WorkReportResult;

import java.util.List;

/**
 * Abstract work report spider.
 * @author Kahle
 */
public abstract class AbstractWorkReportSpider {
    static final String OUTPUT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss Z";
    static final String INPUT_DATE_PATTERN = "yyyy-MM-dd";
    static final Integer DEFAULT_PAGE_SIZE = 40;

    abstract List<WorkReportResult> handle(WorkReportParam param) throws Exception;

}
