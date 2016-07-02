package org.mdubois.basics.spring.batch.core;

import java.util.Collection;
import org.springframework.batch.core.JobExecution;

/**
 *
 * @author Mickael Dubois
 */
public interface JobExecutionDao extends org.springframework.batch.core.repository.dao.JobExecutionDao {

    Collection<JobExecution> getJobInstances(int from, int to);
    
}
