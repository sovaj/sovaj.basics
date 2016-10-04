package io.sovaj.basics.test.server.embedded;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 *
 * @author Mickael Dubois
 */
public class EmbeddedTomcatServer implements InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedTomcatServer.class);
    public static final String TOMCAT_PID_FILENAME = "TOMCAT.PID";

    /**
     * The port number of the embedded tomcat server
     */
    private int port;
    /**
     * The location of the application to deploy.
     */
    private String webappDirectory;
    /**
     * The web app context root path.
     */
    private String webappContextPath;

    /**
     * The context.xml file if needed.
     */
    private Resource contextFile;
    /**
     * The base directory location
     */
    private String baseDirLocation;

    /**
     * Controls if the loggers will be silenced or not:
     * <ul>
     *     <li>true sets the log level to WARN for the loggers that log information on Tomcat start up. This prevents the usual
     * startup information being logged;</li>
     *     <li>false sets the log level to the default level of INFO.</li>
     * </ul>
     */
    private boolean silent = true;

    private static Tomcat tomcat;

    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.notNull(port);
        Assert.notNull(webappDirectory);
        Assert.notNull(webappContextPath);
        Assert.notNull(baseDirLocation);
        tomcat = new Tomcat();
        tomcat.setSilent(silent);
        tomcat.setBaseDir(baseDirLocation);
        tomcat.enableNaming();
        tomcat.setPort(port);
        tomcat.init();
        tomcat.getServer().setPort(port + 15);
        tomcat.getServer().setAddress("127.0.0.1");
        Context ctx = tomcat.addWebapp(webappContextPath, new File(webappDirectory).getAbsolutePath());
        if (contextFile != null) {
            ctx.setConfigFile(contextFile.getURL());
        }
        LOGGER.info("Starting app with basedir: " + new File("./" + webappDirectory).getAbsolutePath());

        tomcat.start();

        while (tomcat.getServer().getState() != LifecycleState.STARTED) {
            Thread.sleep(1000);
        }
        LOGGER.info("Server is running");
        File file = new File(TOMCAT_PID_FILENAME);
        try (FileWriter fw = new FileWriter(file)) {
            if (file.exists()) {
                file.delete();
            }
            file.deleteOnExit();
            fw.append(tomcat.getServer().getAddress());
            fw.append(System.lineSeparator());
            fw.append(tomcat.getServer().getPort() + "");
            fw.append(System.lineSeparator());
            fw.append(tomcat.getServer().getShutdown());
        }
    }

    public void setContextFile(Resource contextFile) {
        this.contextFile = contextFile;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getWebappDirectory() {
        return webappDirectory;
    }

    public void setWebappDirectory(String webappDirectory) {
        this.webappDirectory = webappDirectory;
    }

    public String getWebappContextPath() {
        return webappContextPath;
    }

    public void setWebappContextPath(String webappContextPath) {
        this.webappContextPath = webappContextPath;
    }

    public String getBaseDirLocation() {
        return baseDirLocation;
    }

    public void setBaseDirLocation(String baseDirLocation) {
        this.baseDirLocation = baseDirLocation;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    @Override
    public void destroy() throws Exception {
        new File(TOMCAT_PID_FILENAME).delete();
        tomcat.stop();
    }

    public static Tomcat getTomcat() {
        return tomcat;
    }

}
