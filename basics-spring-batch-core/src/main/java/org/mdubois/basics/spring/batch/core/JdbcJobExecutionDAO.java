package org.mdubois.basics.spring.batch.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.dao.JdbcJobExecutionDao;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 *
 * @author Mickael Dubois
 */
public class JdbcJobExecutionDAO extends JdbcJobExecutionDao implements JobExecutionDao {

    private static final String GET_EXECUTIONS = "SELECT E.JOB_EXECUTION_ID, E.START_TIME, E.END_TIME, E.STATUS, E.EXIT_CODE, E.EXIT_MESSAGE, E.CREATE_TIME, E.LAST_UPDATED, E.VERSION, E.JOB_INSTANCE_ID, E.JOB_CONFIGURATION_LOCATION, I.JOB_NAME from %PREFIX%JOB_EXECUTION E, %PREFIX%JOB_INSTANCE I where E.JOB_INSTANCE_ID=I.JOB_INSTANCE_ID order by E.JOB_EXECUTION_ID desc limit ?, ?";

    private static final String FIND_PARAMS_FROM_ID = "SELECT JOB_EXECUTION_ID, KEY_NAME, TYPE_CD, STRING_VAL, DATE_VAL, LONG_VAL, DOUBLE_VAL, IDENTIFYING from %PREFIX%JOB_EXECUTION_PARAMS where JOB_EXECUTION_ID = ?";

    @Override
    public Collection<JobExecution> getJobInstances(int page, int size) {
        int from = (page - 1) * size;
        int to = page * size;
        final Set<JobExecution> result = new HashSet<>();
        RowCallbackHandler handler = new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                JobExecutionRowMapper mapper = new JobExecutionRowMapper();
                result.add(mapper.mapRow(rs, 0));
            }
        };
        getJdbcTemplate().query(getQuery(GET_EXECUTIONS), new Object[]{from, to}, handler);

        return result;
    }

    /**
     * @param executionId
     * @return
     */
    protected JobParameters getJobParameters(Long executionId) {
        final Map<String, JobParameter> map = new HashMap<>();
        RowCallbackHandler handler = new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                JobParameter.ParameterType type = JobParameter.ParameterType.valueOf(rs.getString(3));
                JobParameter value = null;

                if (type == JobParameter.ParameterType.STRING) {
                    value = new JobParameter(rs.getString(4), rs.getString(8).equalsIgnoreCase("Y"));
                } else if (type == JobParameter.ParameterType.LONG) {
                    value = new JobParameter(rs.getLong(6), rs.getString(8).equalsIgnoreCase("Y"));
                } else if (type == JobParameter.ParameterType.DOUBLE) {
                    value = new JobParameter(rs.getDouble(7), rs.getString(8).equalsIgnoreCase("Y"));
                } else if (type == JobParameter.ParameterType.DATE) {
                    value = new JobParameter(rs.getTimestamp(5), rs.getString(8).equalsIgnoreCase("Y"));
                }

                // No need to assert that value is not null because it's an enum
                map.put(rs.getString(2), value);
            }
        };

        getJdbcTemplate().query(getQuery(FIND_PARAMS_FROM_ID), new Object[]{executionId}, handler);

        return new JobParameters(map);
    }

    /**
     * Re-usable mapper for {@link JobExecution} instances.
     *
     * @author Dave Syer
     *
     */
    private final class JobExecutionRowMapper implements ParameterizedRowMapper<JobExecution> {

        private JobInstance jobInstance;

        private JobParameters jobParameters;

        public JobExecutionRowMapper() {
        }

        public JobExecutionRowMapper(JobInstance jobInstance) {
            this.jobInstance = jobInstance;
        }

        @Override
        public JobExecution mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong(1);
            String jobConfigurationLocation = rs.getString(10);
            String jobName = rs.getString(12);
            JobExecution jobExecution;
            if (jobParameters == null) {
                jobParameters = getJobParameters(id);
            }

            if (jobInstance == null) {
                jobExecution = new JobExecution(new JobInstance(null, jobName), id, jobParameters, jobConfigurationLocation);
            } else {
                jobExecution = new JobExecution(jobInstance, id, jobParameters, jobConfigurationLocation);
            }

            jobExecution.setStartTime(rs.getTimestamp(2));
            jobExecution.setEndTime(rs.getTimestamp(3));
            jobExecution.setStatus(BatchStatus.valueOf(rs.getString(4)));
            jobExecution.setExitStatus(new ExitStatus(rs.getString(5), rs.getString(6)));
            jobExecution.setCreateTime(rs.getTimestamp(7));
            jobExecution.setLastUpdated(rs.getTimestamp(8));
            jobExecution.setVersion(rs.getInt(9));
            return jobExecution;
        }

    }

}
