package illyasviel.module;

import artoria.beans.BeanUtils;
import artoria.common.Param;
import artoria.common.Result;
import artoria.lifecycle.LifecycleException;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.validate.ValidateUtils;
import illyasviel.common.Module;
import illyasviel.domain.AutoGetDailyBonusParam;
import illyasviel.function.V2exGetDailyBonusTask;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;

import static artoria.common.Constants.COMMA;
import static illyasviel.common.IllyaErrorCode.AGD_WEBSITE_NAMES_NOT_BLANK;
import static illyasviel.common.IllyaErrorCode.GLB_PARAM_MAP_NOT_NULL;

/**
 * Auto get daily bonus module.
 * @author Kahle
 */
public class AutoGetDailyBonusModule implements Module {
    private static final long DEFAULT_PERIOD = 12 * 60 * 60 * 1000;
    private static final long DEFAULT_DELAY = 0;
    private static Logger log = LoggerFactory.getLogger(AutoGetDailyBonusModule.class);
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final Timer timer = new Timer();

    @Override
    public void initialize() throws LifecycleException {
    }

    @Override
    public void destroy() throws Exception {

        timer.cancel();
    }

    @Override
    public Result<Object> call(Param<Map<String, Object>> param) throws Exception {
        ValidateUtils.notNull(param, GLB_PARAM_MAP_NOT_NULL);
        Result<Object> result = new Result<Object>();
        Map<String, Object> data = param.getData();
        AutoGetDailyBonusParam autoGetDailyBonusParam =
                BeanUtils.mapToBean(data, AutoGetDailyBonusParam.class);
        String websiteNames = autoGetDailyBonusParam.getWebsiteNames();
        ValidateUtils.notBlank(websiteNames, AGD_WEBSITE_NAMES_NOT_BLANK);
        Long delay = autoGetDailyBonusParam.getDelay();
        if (delay == null || delay < 0) { delay = DEFAULT_DELAY; }
        Long period = autoGetDailyBonusParam.getPeriod();
        if (period == null || period <= 0) { period = DEFAULT_PERIOD; }
        String[] websiteNameArray = websiteNames.split(COMMA);
        for (String websiteName : websiteNameArray) {
            AutoGetDailyBonusParam clonedBean =
                    BeanUtils.beanToBean(autoGetDailyBonusParam, AutoGetDailyBonusParam.class);
            if ("v2ex".equalsIgnoreCase(websiteName)) {
                clonedBean.setWebsiteNames(websiteName);
                V2exGetDailyBonusTask v2exTask = new V2exGetDailyBonusTask(clonedBean);
                timer.schedule(v2exTask, delay, period);
            }
            else if ("???".equalsIgnoreCase(websiteName)) {
            }
            else { continue; }
            log.info("Task \"" + websiteName + "\" schedule success. ");
        }
        countDownLatch.await();
        return result;
    }

}
