package illyasviel.module;

import artoria.beans.BeanUtils;
import artoria.common.Param;
import artoria.common.Result;
import artoria.file.BinaryFile;
import artoria.file.FileFactory;
import artoria.file.Table;
import artoria.file.Txt;
import artoria.lifecycle.LifecycleException;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.template.RenderUtils;
import artoria.util.StringUtils;
import artoria.validate.ValidateUtils;
import illyasviel.common.Module;
import illyasviel.domain.TableToSQLParam;

import java.io.File;
import java.util.List;
import java.util.Map;

import static artoria.common.Constants.DEFAULT_CHARSET_NAME;
import static artoria.common.Constants.NEWLINE;
import static illyasviel.common.IllyaErrorCode.*;

public class TableToSQLModule implements Module {
    private static final String MODULE_NAME = "TableToSQL";
    private static Logger log = LoggerFactory.getLogger(TableToSQLModule.class);

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
        TableToSQLParam tableToSqlParam = BeanUtils.mapToBean(data, TableToSQLParam.class);
        String sqlTemplate = tableToSqlParam.getSqlTemplate();
        String sqlSavePath = tableToSqlParam.getSqlSavePath();
        String tablePath = tableToSqlParam.getTablePath();
        String charset = tableToSqlParam.getCharset();
        charset = StringUtils.isBlank(charset) ? DEFAULT_CHARSET_NAME : charset;
        ValidateUtils.notBlank(sqlTemplate, TTS_SQL_TEMPLATE_NOT_BLANK);
        ValidateUtils.notBlank(sqlSavePath, TTS_SQL_SAVE_PATH_NOT_BLANK);
        ValidateUtils.notBlank(tablePath, TTS_TABLE_PATH_NOT_BLANK);
        File tablePathFile = new File(tablePath);
        BinaryFile instance = FileFactory.getInstance(tablePathFile, charset);
        ValidateUtils.isInstanceOf(Table.class, instance, TTS_TABLE_PATH_MUST_BE_TABLE);
        Table table = (Table) instance;
        int count = 0;
        List<Map<String, Object>> mapList = table.toMapList();
        Txt txt = FileFactory.getInstance("txt");
        for (Map<String, Object> map : mapList) {
            String sql = RenderUtils.renderToString(map, MODULE_NAME, sqlTemplate, charset);
            txt.append(sql).append(NEWLINE);
            count++;
        }
        txt.writeToFile(new File(sqlSavePath));
        result.setData("Render " + count + " times and save the file successfully. ");
        return result;
    }

}
