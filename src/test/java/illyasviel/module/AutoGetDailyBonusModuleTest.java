package illyasviel.module;

import artoria.beans.BeanUtils;
import artoria.common.Param;
import illyasviel.domain.AutoGetDailyBonusParam;
import org.junit.Test;

import java.util.Map;

public class AutoGetDailyBonusModuleTest {

    @Test
    public void test1() throws Exception {
        AutoGetDailyBonusParam autoGetDailyBonusParam = new AutoGetDailyBonusParam();
        autoGetDailyBonusParam.setWebsiteNames("v2ex");
        autoGetDailyBonusParam.setDelay(1000L);
        autoGetDailyBonusParam.setPeriod(10000L);
        autoGetDailyBonusParam.setV2exCookies("test:");
        Param<Map<String, Object>> param = new Param<Map<String, Object>>();
        param.setData(BeanUtils.beanToMap(autoGetDailyBonusParam));
        new AutoGetDailyBonusModule().call(param);
    }

}
