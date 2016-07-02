package org.mdubois.basics.spring.batch.runner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.launch.support.CommandLineJobRunner;

/**
 *
 * @author Mickael Dubois
 */
public class CustomCommandLineJobRunner extends CommandLineJobRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCommandLineJobRunner.class);

    /**
     * Launch date format
     */
    public static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * "launch.date" parameter
     */
    public static final String LAUNCH_DATE_JOB_PARAM = "launch.date";
    
    public static void main(String[] args) throws Exception {
        final List<String> newArgs = new ArrayList<>(Arrays.asList(args));
        newArgs.add(LAUNCH_DATE_JOB_PARAM + '=' + DATE_FORMAT.format(new Date()));

        // appel du CommandLineJobRunner
        try {
            CommandLineJobRunner.main(newArgs.toArray(new String[newArgs.size()]));
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
