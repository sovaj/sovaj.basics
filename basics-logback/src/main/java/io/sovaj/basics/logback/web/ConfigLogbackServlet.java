package io.sovaj.basics.logback.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.Level;
import java.io.PrintWriter;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mickael
 */
public class ConfigLogbackServlet extends HttpServlet {

    final Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    final LoggerContext loggerRepository = rootLogger.getLoggerContext();

    /**
     * @param request - The {@link HttpServletRequest}
     * @param response - The {@link HttpServletResponse}
     * @throws java.io.IOException Can't write to the ouput stream
     */
    @Override
    @SuppressWarnings("unchecked")
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws  IOException {

        final List<Logger> loggers = loggerRepository.getLoggerList();
        PrintWriter responseWriter = response.getWriter();
        String prefix = "";
        responseWriter.append("[");
        for (Logger logger : loggers) {
            responseWriter.append(prefix);
            prefix = ",";
            responseWriter.append("{\"Name\" : \"");
            responseWriter.append(logger.getName());
            responseWriter.append("\", \"Level\" : \"");
            responseWriter.append(logger.getLevel() != null
                    ? logger.getLevel().toString()
                    : logger.getEffectiveLevel().toString());
            responseWriter.append("\"}");

        }
        responseWriter.append("]");
        response.getWriter().flush();
        response.getWriter().close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String newRootLevel = request.getParameter("rootLoggingLevel");
        if (StringUtils.isNotBlank(newRootLevel)) {
            final Level newLevel = Level.toLevel(newRootLevel);
            rootLogger.setLevel(newLevel);
        }
        final String loggerName = request.getParameter("loggerName");
        final String loggerLevel = request.getParameter("loggerLevel");
        if (StringUtils.isNotBlank(loggerName)
                && StringUtils.isNotBlank(loggerLevel)) {
            final Level level = Level.toLevel(loggerLevel);
            loggerRepository.getLogger(loggerName).setLevel(level);
        }
        response.getOutputStream().flush();
    }

}
