package org.mdubois.basics.test.database.embedded;

import java.io.File;
import java.io.IOException;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.mysql.management.MysqldResource;
import com.mysql.management.MysqldResourceI;
import com.mysql.management.driverlaunched.ServerLauncherSocketFactory;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mickael Dubois
 */
public class EmbeddedMysqlDataSource extends MysqlDataSource {

    private final File basedir;
    private final File datadir;
    public final String MYSQL_PID_FILENAME = "MYSQL.PID";
    public final String MYSQL_BASEDIR_FILENAME = "MYSQL.BASEDIR";

    private boolean deleteDirOnExit = true;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedMysqlDataSource.class);

    public EmbeddedMysqlDataSource(String userLogin, String userPass) throws IOException {

        // We need to set our own base/data dirs as we must
        // pass those values to the shutdown() method later
        basedir = File.createTempFile("mysqld-base", null);
        datadir = File.createTempFile("mysqld-data", null);

        // Wish there was a better way to make temp folders!
        basedir.delete();
        datadir.delete();
        basedir.mkdir();
        datadir.mkdir();
        basedir.deleteOnExit();
        datadir.deleteOnExit();

        MysqldResource mysqldResource = new MysqldResource(basedir, datadir);
        Map database_options = new HashMap();
        database_options.put(MysqldResourceI.PORT, 3307);
        database_options.put(MysqldResourceI.INITIALIZE_USER, "true");
        database_options.put(MysqldResourceI.INITIALIZE_USER_NAME, userLogin);
        database_options.put(MysqldResourceI.INITIALIZE_PASSWORD, userPass);
        mysqldResource.start("test-mysqld-thread", database_options);
        if (!mysqldResource.isRunning()) {
            throw new RuntimeException("MySQL did not start.");
        } else {
            File basedirFile = new File(MYSQL_BASEDIR_FILENAME);
            try (FileWriter fw = new FileWriter(basedirFile)) {
                if (basedirFile.exists()) {
                    basedirFile.delete();
                }
                basedirFile.deleteOnExit();
                fw.append(basedir.getAbsolutePath());
                fw.append(System.lineSeparator());
                fw.append(datadir.getAbsolutePath());
                fw.close();
            }
            File pidFile = new File(MYSQL_PID_FILENAME);
            try (FileWriter fw = new FileWriter(pidFile)) {
                if (pidFile.exists()) {
                    pidFile.delete();
                }
                pidFile.deleteOnExit();
                fw.write(ManagementFactory.getRuntimeMXBean().getName());
                fw.close();
            }
        }
    }

    public static void shutdown(EmbeddedMysqlDataSource ds) {
        try {
            ds.shutdown();
        } catch (IOException e) {
            LOGGER.error("Could not shutdown embedded server. (%s)", e);
        }
    }

    public void shutdown() throws IOException {
        try {
            new File(MYSQL_PID_FILENAME).delete();
            new File(MYSQL_BASEDIR_FILENAME).delete();
            if (deleteDirOnExit) {
                basedir.delete();
                datadir.delete();
            }
        } catch (Exception e) {

        }
        ServerLauncherSocketFactory.shutdown(basedir, datadir);
    }

    public boolean isDeleteDirOnExit() {
        return deleteDirOnExit;
    }

    public void setDeleteDirOnExit(boolean deleteDirOnExit) {
        this.deleteDirOnExit = deleteDirOnExit;
    }

}
