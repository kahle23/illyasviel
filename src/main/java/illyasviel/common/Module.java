package illyasviel.common;

import artoria.common.Param;
import artoria.common.Result;
import artoria.lifecycle.Destroyable;
import artoria.lifecycle.Initializable;

import java.util.Map;

/**
 * Abstract module.
 * @author Kahle
 */
public interface Module extends Initializable, Destroyable {

    /**
     * Module entry.
     * @param param Query parameter
     * @return Executive Outcome
     * @throws Exception Possible anomaly
     */
    Result<Object> call(Param<Map<String, Object>> param) throws Exception;

}
