package org.mdubois.basics.spring.batch.launcher;

import java.util.Set;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 *
 * @author Mickael Dubois
 */
public class CustomJobLauncher extends SimpleJobLauncher implements InitializingBean {
    
    private JobExecutionDao jobExecutionDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.jobExecutionDao);
        super.afterPropertiesSet();
    }


    @Override
    public JobExecution run(final Job pJob, final JobParameters pJobParameters)
            throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException,
            JobParametersInvalidException {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder(pJobParameters);
        if (pJob.isRestartable()) {
            Set<JobExecution> jobExecutions = jobExecutionDao.findRunningJobExecutions(pJob.getName());
            if (jobExecutions.isEmpty()) {
                jobParametersBuilder.addLong("time", System.currentTimeMillis());
            } else {
                throw new JobExecutionAlreadyRunningException("A '" + pJob.getName() + "' job is already running");
            }
        }
        return super.run(pJob, jobParametersBuilder.toJobParameters());
    }

    public JobExecutionDao getJobExecutionDao() {
        return jobExecutionDao;
    }

    public void setJobExecutionDao(JobExecutionDao jobExecutionDao) {
        this.jobExecutionDao = jobExecutionDao;
    }
    

}
