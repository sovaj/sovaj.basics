package io.sovaj.basics.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class PropertiesServlet extends HttpServlet {


    /**
     * Sprint property bean name you created
     */
    private String propertiesBeanName;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        propertiesBeanName = config.getInitParameter("propertiesBeanName");
        if (propertiesBeanName == null) {
            throw new ServletException("Missing 'propertiesBeanName' init-param");
        }
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        Properties propertiesBean = Properties.class.cast(ctx.getBean(propertiesBeanName));

        List properties = new ArrayList();

        String[] requestProperties = req.getParameterValues("property");
        if (requestProperties == null) {
            // si aucun paramètre, on retourne toutes les properties
            properties.addAll(propertiesBean.keySet());
        } else {
            Collections.addAll(properties, requestProperties);
        }

        // récupération des données et construction du xml
        final StringBuilder builder = new StringBuilder();
        String acceptedFormat = req.getHeader("Accept");
        if (acceptedFormat.contains("application/json")) {
            builder.append("[");
            String prefix = "";
            for (Object prop : properties) {
                builder.append(prefix);
                prefix = ",";
                builder.append(getJSONString((String) prop, propertiesBean));
            }
            builder.append("]");

            // écriture de la réponse
            resp.setContentType("application/json; charset=utf-8");
        } else  {
            builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            builder.append("<properties>");
            for (Object prop : properties) {
                builder.append(getXmlString((String) prop, propertiesBean));
            }
            builder.append("</properties>");

            // écriture de la réponse
            resp.setContentType("application/xml; charset=utf-8");
        }
        resp.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", -1);
        resp.getWriter().print(builder.toString());
    }

    private static StringBuilder getJSONString(String property, Properties properties) {
        final StringBuilder builder = new StringBuilder();
        final String propertyValue = (String) properties.get(property);
        builder.append("{\"Key\" : \"");
        builder.append(property);
        builder.append("\", \"Value\" : \"").append(propertyValue).append("\"}");

        return builder;
    }

    private static StringBuilder getXmlString(String property, Properties properties) {
        final StringBuilder builder = new StringBuilder();
        final String propertyValue = (String) properties.get(property);
        builder.append("<property id=\"").append(property).append("\">").append(propertyValue).append("</property>");

        return builder;
    }
}