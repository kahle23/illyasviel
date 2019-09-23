package illyasviel.domain;

public class TableToSQLParam {
    private String charset;
    private String tablePath;
    private String sqlTemplate;
    private String sqlSavePath;

    public String getCharset() {

        return charset;
    }

    public void setCharset(String charset) {

        this.charset = charset;
    }

    public String getTablePath() {

        return tablePath;
    }

    public void setTablePath(String tablePath) {

        this.tablePath = tablePath;
    }

    public String getSqlTemplate() {

        return sqlTemplate;
    }

    public void setSqlTemplate(String sqlTemplate) {

        this.sqlTemplate = sqlTemplate;
    }

    public String getSqlSavePath() {

        return sqlSavePath;
    }

    public void setSqlSavePath(String sqlSavePath) {

        this.sqlSavePath = sqlSavePath;
    }

}
