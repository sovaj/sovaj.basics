package org.sovaj.basics.spring.batch.listener;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.util.Assert;

/**
 *
 * @author Mickael Dubois
 */
public class SQLJobExecutionListener implements JobExecutionListener, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLJobExecutionListener.class);

    /**
     * Load SQL string
     */
    private String loadSQL;

    /**
     * Load SQL file name
     */
    private String loadSQLFileName;

    /**
     * Clear SQL string
     */
    private String clearSQL;

    /**
     * Clear SQL file name
     */
    private String clearSQLFileName;
    /**
     * {@link JdbcOperations}
     */
    private JdbcOperations jdbcTemplate;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(!(this.loadSQL != null && this.loadSQLFileName != null),
                "One of the loadSQL or loadSQLFileName property have to be set");
        Assert.isTrue(this.loadSQL != null || this.loadSQLFileName != null, "loadSQL ou loadSQLFileName property have to be set");
        if (loadSQLFileName != null) {
            final String codeSql = sqlFileToString(loadSQLFileName);
            setLoadSQL(codeSql);
        }
        Assert.isTrue(!(this.clearSQL != null && this.clearSQLFileName != null),
                "One of the clearSQL or clearSQLFileName property have to be set");
        Assert.isTrue(this.clearSQL != null || this.clearSQLFileName != null, "clearSQL ou clearSQLFileName property have to be set");
        if (loadSQLFileName != null) {
            final String codeSql = sqlFileToString(clearSQLFileName);
            setClearSQL(codeSql);
        }
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        LOGGER.info("Clearing temporary merchant into temporary table");
        jdbcTemplate.execute(clearSQL);
        LOGGER.info("Temporary merchant table cleared");
        
        LOGGER.info("Loading temporary merchant into temporary table ");
        jdbcTemplate.execute(loadSQL);
        LOGGER.info("Temporary merchant loaded");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LOGGER.info("Clearing temporary merchant into temporary table");
        jdbcTemplate.execute(clearSQL);
        LOGGER.info("Temporary merchant table cleared");
    }

    private String sqlFileToString(String sqlFileName) throws IOException {

        String codeSql = StringUtils.EMPTY;

        try {
            final InputStream inputStream = getClass().getResourceAsStream(sqlFileName);
            final StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer);
            codeSql = writer.toString();
        } catch (final IOException e) {
            throw e;
        }

        return codeSql;
    }

    public String getLoadSQL() {
        return loadSQL;
    }

    public void setLoadSQL(String loadSQL) {
        this.loadSQL = loadSQL;
    }

    public String getLoadSQLFileName() {
        return loadSQLFileName;
    }

    public void setLoadSQLFileName(String loadSQLFileName) {
        this.loadSQLFileName = loadSQLFileName;
    }

    public String getClearSQL() {
        return clearSQL;
    }

    public void setClearSQL(String clearSQL) {
        this.clearSQL = clearSQL;
    }

    public String getClearSQLFileName() {
        return clearSQLFileName;
    }

    public void setClearSQLFileName(String clearSQLFileName) {
        this.clearSQLFileName = clearSQLFileName;
    }

    public JdbcOperations getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    

}
