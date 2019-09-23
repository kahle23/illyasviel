package illyasviel;

import artoria.common.Param;
import artoria.common.Result;
import artoria.exception.BusinessException;
import artoria.exception.ErrorCode;
import artoria.lifecycle.LifecycleUtils;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.util.Assert;
import artoria.util.BooleanUtils;
import artoria.util.ClassUtils;
import illyasviel.common.Module;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static artoria.common.Constants.EMPTY_STRING;
import static artoria.common.Constants.MINUS;

/**
 * Application entry.
 * @author Kahle
 */
public class Application {
    private static final Map<String, Module> MODULE_MAP = new ConcurrentHashMap<String, Module>();
    private static ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
    private static Logger log = LoggerFactory.getLogger(Application.class);
    private static boolean debug = false;

    public static void main(String[] args) {
        try {
            Map<String, Object> argsMap = Application.parseArgsToMap(args);
            debug = BooleanUtils.parseBoolean((String) argsMap.get("debug"));
            String call = (String) argsMap.get("call");
            Assert.notBlank(call, "Parameter \"call\" must not blank. ");
            Module module = Application.getModule(call);
            Param<Map<String, Object>> param = new Param<Map<String, Object>>(argsMap);
            Result<Object> result = module.call(param);
            boolean isSuccess = result != null && result.getSuccess();
            log.info(isSuccess ?
                    "Success: " + (result.getData() != null ? result.getData() : EMPTY_STRING) :
                    "Failure: " + (result != null ? result.getMessage() : EMPTY_STRING));
            LifecycleUtils.destroy(MODULE_MAP.values());
        }
        catch (BusinessException e) {
            ErrorCode errorCode = e.getErrorCode();
            boolean hasErrorCode = errorCode != null;
            String message = hasErrorCode ? errorCode.getCode() : EMPTY_STRING;
            message = hasErrorCode && debug
                    ? message + " (" + errorCode.getDescription() + ")" : message;
            log.error("Failure: " + message, debug ? e : null);
        }
        catch (Exception e) {
            log.error("An unexpected error. ", e);
        }
        log.info("Powered by Illyasviel (\"https://github.com/kahlkn/illyasviel\"). ");
    }

    private static Map<String, Object> parseArgsToMap(String[] args) {
        Map<String, Object> result = new HashMap<String, Object>();
        int length = args.length;
        for (int i = 0; i < length; i++) {
            String arg = args[i];
            if (!arg.startsWith(MINUS)) { continue; }
            String key = arg.substring(1);
            int tmpInt = i + 1;
            if (tmpInt < length && !args[tmpInt].startsWith(MINUS)) {
                result.put(key, args[tmpInt]);
                i = tmpInt;
            }
            else {
                result.put(key, null);
            }
        }
        return result;
    }

    private static Module getModule(String moduleName) throws Exception {
        Module module = MODULE_MAP.get(moduleName);
        if (module == null) {
            String prefix = "illyasviel.module.";
            String suffix = "Module";
            String className = prefix + moduleName + suffix;
            Assert.state(ClassUtils.isPresent(className, classLoader)
                    , "Can not find the class \"" + className + "\". ");
            Class<?> clazz = ClassUtils.forName(className);
            module = (Module) clazz.newInstance();
            LifecycleUtils.initialize(module);
            MODULE_MAP.put(moduleName, module);
        }
        return module;
    }

}
