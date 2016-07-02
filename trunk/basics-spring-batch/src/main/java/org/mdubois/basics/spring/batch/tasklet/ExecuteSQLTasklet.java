package org.mdubois.basics.spring.batch.tasklet;

import javax.sql.DataSource;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 *
 * @author Mickael Dubois
 */
public class ExecuteSQLTasklet implements Tasklet, InitializingBean {

    private DataSource dataSource;
    private String[] sqls;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(dataSource != null, "A datasource must be provided");
        Assert.state(sqls != null, "SQL string(s) must be provided");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        for (String sql : sqls) {
            new JdbcTemplate(dataSource).execute(sql);
        }
        return RepeatStatus.FINISHED;
    }

    public void setDataSource(DataSource pDataSource) {
        this.dataSource = pDataSource;
    }

    public void setSqls(String... sqls) {
        this.sqls = sqls;
    }
}
