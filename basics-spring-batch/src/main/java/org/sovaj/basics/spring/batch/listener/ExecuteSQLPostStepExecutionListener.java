package org.sovaj.basics.spring.batch.listener;

import javax.sql.DataSource;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 *
 * @author Mickael Dubois
 */
public class ExecuteSQLPostStepExecutionListener implements StepExecutionListener, InitializingBean {

    private DataSource dataSource;
    private String[] sqls;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(dataSource != null, "A transaction manager must be provided");
        Assert.state(sqls != null, "A transaction manager must be provided");
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        //DO Nothing
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        for (String sql : sqls) {
            new JdbcTemplate(dataSource).execute(sql);
        }
        return ExitStatus.COMPLETED;
    }

    public void setDataSource(DataSource pDataSource) {
        this.dataSource = pDataSource;
    }

    public void setSqls(String... sqls) {
        this.sqls = sqls;
    }

}
