package org.mdubois.basics.logback.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.reflect.FieldUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.html.HTMLLayout;
import ch.qos.logback.classic.log4j.XMLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.helpers.CyclicBuffer;
import ch.qos.logback.core.read.CyclicBufferAppender;
import org.mdubois.basics.logback.layout.json.JSONLayout;

/**
 * Une servlet qui affiche les logs. Requiert qu'un {@link CyclicBufferAppender}
 * (par d�faut nomm� "BUFFER") soit install�.
 *
 * @author Mickael Dubois
 */
public class LogbackServlet extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String logger = request.getParameter("logger");
        if (logger == null) {
            logger = "ROOT";
        }

        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        LoggerContext loggerContext = (LoggerContext) loggerFactory;
        // get logger
        Logger theLogger = loggerContext.getLogger(logger);
        // get cyclic buffer appender
        String cyclicBufferAppenderName = request.getParameter("cyclicBufferAppenderName");
        if (cyclicBufferAppenderName == null) {
            cyclicBufferAppenderName = "BUFFER";
        }
        CyclicBufferAppender<ILoggingEvent> bufferAppender
                = (CyclicBufferAppender<ILoggingEvent>) theLogger.getAppender(cyclicBufferAppenderName);
        bufferAppender.reset();
    }

    /**
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    @SuppressWarnings("unchecked")
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            String logger = request.getParameter("logger");
            if (logger == null) {
                logger = "ROOT";
            }

            ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
            LoggerContext loggerContext = (LoggerContext) loggerFactory;
            // get logger
            Logger theLogger = loggerContext.getLogger(logger);
            // get cyclic buffer appender
            String cyclicBufferAppenderName = request.getParameter("cyclicBufferAppenderName");
            if (cyclicBufferAppenderName == null) {
                cyclicBufferAppenderName = "BUFFER";
            }
            CyclicBufferAppender<ILoggingEvent> bufferAppender
                    = (CyclicBufferAppender<ILoggingEvent>) theLogger.getAppender(cyclicBufferAppenderName);
            if (bufferAppender == null) {
                throw new ServletException("No CyclicBufferAppender named '" + cyclicBufferAppenderName
                        + "' found in logger '" + logger + "'");
            }
            // get cyclic buffer
            CyclicBuffer<ILoggingEvent> buffer
                    = (CyclicBuffer<ILoggingEvent>) FieldUtils.readField(bufferAppender, "cb", true);

            String acceptedFormat = request.getHeader("Accept");
            if (acceptedFormat.contains("application/json")) {
                generateJSONOutput(response, loggerContext, buffer);
            } else if (acceptedFormat.contains("application/xhtml")
                    || acceptedFormat.contains("application/html")) {
                generateHTMLOutput(response, loggerContext, buffer);
            } else {
                generateXMLOutput(response, loggerContext, buffer);

            }
        } catch (IllegalAccessException e) {
            throw new ServletException("Erreur lors de la r�cup�ration des logs", e);
        }
    }

    private void generateHTMLOutput(HttpServletResponse response, LoggerContext loggerContext, CyclicBuffer<ILoggingEvent> buffer) throws IOException {
        // prepare response
        response.setContentType("text/html");
        response.setHeader("Cache-control", "no-cache");

        // build encoder + html layout
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        HTMLLayout layout = new HTMLLayout();
        layout.setPattern("%date%level%logger%msg");
        layout.setContext(loggerContext);
        layout.start();
        encoder.setLayout(layout);
        encoder.setContext(loggerContext);
        encoder.start();

        // build output stream appender
        OutputStreamAppender<ILoggingEvent> osAppender = new OutputStreamAppender<>();
        osAppender.setContext(loggerContext);
        osAppender.setEncoder(encoder);
        OutputStream os = response.getOutputStream();
        osAppender.setOutputStream(os);
        osAppender.start();

        // append events to output stream
        List<ILoggingEvent> events = buffer.asList();
        for (ILoggingEvent event : events) {
            osAppender.doAppend(event);
        }
        osAppender.stop();
        encoder.stop();
        layout.stop();
        os.flush();
    }

    private void generateJSONOutput(HttpServletResponse response, LoggerContext loggerContext, CyclicBuffer<ILoggingEvent> buffer) throws IOException {
        // prepare response
        response.setContentType("application/json");
        response.setHeader("Cache-control", "no-cache");

        //json layout
        JSONLayout layout = new JSONLayout();
        layout.setContext(loggerContext);
        layout.setPattern("%date%level%logger%msg");
        layout.start();
        // build encoder
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setLayout(layout);
        encoder.setContext(loggerContext);
        encoder.start();

        // build output stream appender
        OutputStreamAppender<ILoggingEvent> osAppender = new OutputStreamAppender<>();
        osAppender.setContext(loggerContext);
        osAppender.setEncoder(encoder);
        OutputStream os = response.getOutputStream();
        osAppender.setOutputStream(os);
        osAppender.start();

        // append events to output stream
        List<ILoggingEvent> events = buffer.asList();
        for (ILoggingEvent event : events) {
            osAppender.doAppend(event);
        }

        osAppender.stop();
        encoder.stop();
        layout.stop();
        os.flush();
    }

    private void generateXMLOutput(HttpServletResponse response, LoggerContext loggerContext, CyclicBuffer<ILoggingEvent> buffer) throws IOException {
        // prepare response
        response.setContentType("application/json");
        response.setHeader("Cache-control", "no-cache");

        //json layout
        XMLLayout layout = new XMLLayout();
        layout.setContext(loggerContext);
        layout.start();
        // build encoder
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setLayout(layout);
        encoder.setContext(loggerContext);
        encoder.start();

        // build output stream appender
        OutputStreamAppender<ILoggingEvent> osAppender = new OutputStreamAppender<>();
        osAppender.setContext(loggerContext);
        osAppender.setEncoder(encoder);
        OutputStream os = response.getOutputStream();
        osAppender.setOutputStream(os);
        osAppender.start();

        // append events to output stream
        List<ILoggingEvent> events = buffer.asList();
        for (ILoggingEvent event : events) {
            osAppender.doAppend(event);
        }

        osAppender.stop();
        encoder.stop();
        layout.stop();
        os.flush();
    }
}
