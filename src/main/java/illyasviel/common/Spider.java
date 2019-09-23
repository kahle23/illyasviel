package illyasviel.common;

import java.util.Map;

/**
 * 爬虫的抽象类.
 * @author Kahle
 */
public interface Spider {

    /**
     * 执行爬虫逻辑
     * @param context 命令行上下文（会把参数传递进来）
     */
    void execute(Map<String, String> context);

}
