package io.sovaj.basics.web.filter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import io.sovaj.basics.web.CharResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XsltFilter implements Filter {

    private FilterConfig filterConfig;

    private final Map<String, Transformer> transformers = new HashMap<String, Transformer>();

    private static final Logger LOGGER = LoggerFactory.getLogger(XsltFilter.class);

    @Override
    @SuppressWarnings("unchecked")
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;

        // default xslt for "html"
        transformers.put("html", getTransformer(filterConfig.getServletContext(), "/xsl/monitoring.xsl"));

        Enumeration<String> initParameterNames = filterConfig.getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            // initParameter name "html", "img"...
            String paramName = initParameterNames.nextElement();
            // initParameter value should be something like
            // "/WEB-INF/xslt/a.xslt"
            String paramValue = filterConfig.getInitParameter(paramName);
            transformers.put(paramName, getTransformer(filterConfig.getServletContext(), paramValue));
        }
    }

    private Transformer getTransformer(ServletContext servletContext, String xsltPath) throws UnavailableException, TransformerFactoryConfigurationError {
        StreamSource xsltStreamSource;

        // Get as stream
        InputStream stream = getClass().getResourceAsStream(xsltPath);
        if (stream != null) {
            xsltStreamSource = new StreamSource(stream);
        } else {
            // convert the context-relative path to a physical path name
            String xsltFileName = servletContext.getRealPath(xsltPath);
            // verify that the file exists
            if (xsltFileName == null || !new File(xsltFileName).exists()) {
                throw new UnavailableException("Unable to locate stylesheet: " + xsltFileName, 30);
            }
            xsltStreamSource = new StreamSource(xsltFileName);
        }

        TransformerFactory tFactory = TransformerFactory.newInstance();
        try {
            return tFactory.newTransformer(xsltStreamSource);
        } catch (TransformerConfigurationException e) {
            throw new UnavailableException("Init the transformer :: " + e.toString());
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (!(res instanceof HttpServletResponse)) {
            throw new ServletException("This filter only supports HTTP");
        }

        String output = req.getParameter("output");
        if (output == null) {
            // no transformation
            chain.doFilter(req, res);
            return;
        }

        Transformer transformer = transformers.get(output);
        if (transformer == null) {
            LOGGER.warn("No xslt defined for output '{}'", output);
            // no transformation
            chain.doFilter(req, res);
            return;
        }

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        final CharResponseWrapper responseWrapper = new CharResponseWrapper(response);
        chain.doFilter(request, responseWrapper);

        // Tomcat 4.0 reuses instances of its HttpServletResponse
        // implementation class in some scenarios. For instance, hitting
        // reload( ) repeatedly on a web browser will cause this to happen.
        // Unfortunately, when this occurs, output is never written to the
        // BufferedHttpResponseWrapper's OutputStream. This means that the
        // XML output array is empty when this happens. The following
        // code is a workaround:
        final byte[] origXML = responseWrapper.toString().getBytes();

        final ByteArrayInputStream origXMLIn = new ByteArrayInputStream(origXML);
        final Source originalSource = new StreamSource(origXMLIn);

        if (output.startsWith("img")) {
            // render as an image
            renderAsImg(transformer, request, response, originalSource);
        } else {
            // render as text
            renderAsText(transformer, response, originalSource);
        }
    }

    private void renderAsImg(Transformer transformer, final HttpServletRequest request, final HttpServletResponse response, final Source source) throws IOException,
            ServletException {

        DataInputStream gifInputStream = null;
        try {
            final StringWriter writer = new StringWriter();
            transformer.transform(source, new StreamResult(writer));
            final String imageString = writer.toString().substring("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length());
            LOGGER.debug("Render as an image : " + imageString);

            String contentType = "image/png";
            String imageExtension = "png";
            BufferedImage buffer = null;
            try {
                final String gifFileUrl = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/")) + "/" + imageString;
                final URLConnection conn = new URL(gifFileUrl).openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(10000);
                conn.connect();
                final InputStream openStream = conn.getInputStream();
                gifInputStream = new DataInputStream(new BufferedInputStream(openStream));
                imageExtension = substringAfterLast(imageString, ".");
                contentType = "image/" + imageExtension;
            } catch (final Exception e) {
                buffer = getBufferedImage(imageString);
            }

            response.setContentType(contentType);
            response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");

            final OutputStream os = response.getOutputStream();
            if (gifInputStream != null) {
                // write the is
                readWriteStream(gifInputStream, os);
            } else {
                // output the image
                ImageIO.write(buffer, imageExtension, os);
            }

            response.flushBuffer();
        } catch (final TransformerConfigurationException e) {
            throw new ServletException("ProblÃ¨me de configuration : ", e);
        } catch (final TransformerException e) {
            throw new ServletException("ProblÃ¨me lors de la transformation : ", e);
        } finally {
            if (gifInputStream != null) {
                gifInputStream.close();
            }
        }
    }

    public static String substringAfterLast(String str, String separator) {
        int pos = str.lastIndexOf(separator);
        if (pos == -1 || pos == str.length() - separator.length()) {
            return "";
        }
        return str.substring(pos + separator.length());
    }

    private BufferedImage getBufferedImage(String image) {
        final Font font = new Font("courier", Font.PLAIN, 12);
        BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = buffer.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final FontRenderContext fc = g2.getFontRenderContext();
        final TextLayout layout = new TextLayout(image, font, fc);
        final Rectangle2D bounds = layout.getBounds();

        // calculate the size of the text
        final int width = (int) bounds.getWidth() + 3;
        final int height = (int) bounds.getHeight() + 4;

        // prepare some output
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2 = buffer.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(font);

        // actually do the drawing
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.BLACK);
        g2.drawString(image, 0, (int) -bounds.getY() + 3);
        return buffer;
    }

    private void renderAsText(Transformer transformer, final HttpServletResponse response, final Source xmlSource) throws IOException, ServletException {
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");

        try {
            final ByteArrayOutputStream resultBuf = new ByteArrayOutputStream();
            transformer.transform(xmlSource, new StreamResult(resultBuf));

            response.setContentLength(resultBuf.size());
            response.getOutputStream().write(resultBuf.toByteArray());

            response.flushBuffer();
        } catch (final TransformerException te) {
            throw new ServletException(te);
        }
    }

    private void readWriteStream(final DataInputStream stream, final OutputStream os) throws IOException {
        int read;
        final byte[] buff = new byte[2048];
        while ((read = stream.read(buff)) != -1) {
            os.write(buff, 0, read);
        }
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}
