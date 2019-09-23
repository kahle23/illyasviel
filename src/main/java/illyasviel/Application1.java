package illyasviel;

import artoria.exception.ExceptionUtils;
import artoria.lifecycle.LifecycleUtils;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.util.Assert;
import artoria.util.ClassUtils;
import artoria.util.StringUtils;
import com.eggxiaoer.local.spider.common.Spider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Startup class.
 * @author Kahle
 */
public class Application1 {
    private static final Map<String, Spider> SPIDER_MAP = new ConcurrentHashMap<>();
    private static ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
    private static Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * Program entry.
     */
    public static void main(String[] args) {
        Map<String, String> context = CommandLineUtils.parseParameters(args);
        String spiderName = context.get("spider");
        if (StringUtils.isBlank(spiderName)) {
            log.info("爬虫名称（spider）不能是空白的！");
            return;
        }
        Spider spider = Application.getSpider(spiderName);
        spider.execute(context);
        log.info("Powered by Egg Xiao Er Team. ");
    }

    private static Spider getSpider(String spiderName) {
        try {
            Spider spider = SPIDER_MAP.get(spiderName);
            if (spider == null) {
                String prefix = "com.eggxiaoer.local.spider.module.";
                String suffix = "Spider";
                spiderName = StringUtils.capitalize(spiderName);
                String className = prefix + spiderName + suffix;
                Assert.state(ClassUtils.isPresent(className, classLoader)
                        , "Can not find the class \"" + className + "\". ");
                Class<?> clazz = ClassLoaderUtils.loadClass(className, Application.class);
                spider = (Spider) clazz.newInstance();
                LifecycleUtils.initialize(spider);
                SPIDER_MAP.put(spiderName, spider);
            }
            return spider;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}
