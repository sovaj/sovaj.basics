package io.sovaj.basics.web.servlet;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.HeaderGroup;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestHandler;

/**
 *
 * @author baloo
 */
public class ServiceProxyServlet implements HttpRequestHandler, InitializingBean, DisposableBean {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceProxyServlet.class);

    protected String serviceScheme;

    protected String serviceHost;

    protected int servicePort;

    protected String serviceContext;

    private Boolean doForwardIP;

    private String serviceURL;

    protected URI targetUriObj;

    protected HttpClient proxyClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.serviceScheme);
        Assert.notNull(this.serviceHost);
        Assert.notNull(this.servicePort);
        Assert.notNull(this.doForwardIP);
        serviceURL = serviceScheme + "://" + serviceHost + ":" + servicePort;
        targetUriObj = new URI(serviceURL);

        HttpParams hcParams = new BasicHttpParams();
        proxyClient = new SystemDefaultHttpClient(hcParams);
    }

    @Override
    public void destroy() throws Exception {
        if (proxyClient != null) {
            proxyClient.getConnectionManager().shutdown();
        }
    }

    @Override
    public void handleRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        //Make the Request
        //note: we won't transfer the protocol version because I'm not sure it would truly be compatible
        String method = servletRequest.getMethod();
        String proxyRequestUri;
        try {
            proxyRequestUri = rewriteUrlFromRequest(servletRequest);
        } catch (URISyntaxException ex) {
            throw new ServletException(ex);
        }
        HttpRequest proxyRequest;
        //spec: RFC 2616, sec 4.3: either of these two headers signal that there is a message body.
        if (servletRequest.getHeader(HttpHeaders.CONTENT_LENGTH) != null
                || servletRequest.getHeader(HttpHeaders.TRANSFER_ENCODING) != null) {
            HttpEntityEnclosingRequest eProxyRequest = new BasicHttpEntityEnclosingRequest(method, proxyRequestUri);
            // Add the input entity (streamed)
            //  note: we don't bother ensuring we close the servletInputStream since the container handles it
            eProxyRequest.setEntity(new InputStreamEntity(servletRequest.getInputStream(), servletRequest.getContentLength()));
            proxyRequest = eProxyRequest;
        } else {
            proxyRequest = new BasicHttpRequest(method, proxyRequestUri);
        }

        copyRequestHeaders(servletRequest, proxyRequest);

        setXForwardedForHeader(servletRequest, proxyRequest);

        HttpResponse proxyResponse = null;
        try {
            // Execute the request
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("proxy " + method + " uri: " + servletRequest.getRequestURI() + " -- " + proxyRequest.getRequestLine().getUri());
            }
            proxyResponse = proxyClient.execute(URIUtils.extractHost(targetUriObj), proxyRequest);

            // Process the response
            int statusCode = proxyResponse.getStatusLine().getStatusCode();

            if (doResponseRedirectOrNotModifiedLogic(servletRequest, servletResponse, proxyResponse, statusCode)) {
                //the response is already "committed" now without any body to send
                //TODO copy response headers?
                return;
            }

            servletResponse.setStatus(statusCode);

            copyResponseHeaders(proxyResponse, servletResponse);

            // Send the content to the client
            copyResponseEntity(proxyResponse, servletResponse);

        } catch (IOException | ServletException e) {
            LOGGER.error(e.getMessage(), e);
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            //abort request, according to best practice with HttpClient
            if (proxyRequest instanceof AbortableHttpRequest) {
                AbortableHttpRequest abortableHttpRequest = (AbortableHttpRequest) proxyRequest;
                abortableHttpRequest.abort();
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            if (e instanceof ServletException) {
                throw (ServletException) e;
            }
            //noinspection ConstantConditions
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new RuntimeException(e);

        } finally {
            // make sure the entire entity was consumed, so the connection is released
            if (proxyResponse != null) {
                consumeQuietly(proxyResponse.getEntity());
            }
            closeQuietly(servletResponse.getOutputStream());
        }
    }

    protected String rewriteUrlFromRequest(HttpServletRequest servletRequest) throws URISyntaxException {

        URI uri = new URI(serviceScheme, null, serviceHost, servicePort, "/" + serviceContext + servletRequest.getPathInfo(), servletRequest.getQueryString(), null);

        return uri.toString();
    }

    protected boolean doResponseRedirectOrNotModifiedLogic(
            HttpServletRequest servletRequest, HttpServletResponse servletResponse,
            HttpResponse proxyResponse, int statusCode)
            throws ServletException, IOException {
        // Check if the proxy response is a redirect
        // The following code is adapted from org.tigris.noodle.filters.CheckForRedirect
        if (statusCode >= HttpServletResponse.SC_MULTIPLE_CHOICES /* 300 */
                && statusCode < HttpServletResponse.SC_NOT_MODIFIED /* 304 */) {
            Header locationHeader = proxyResponse.getLastHeader(HttpHeaders.LOCATION);
            if (locationHeader == null) {
                throw new ServletException("Received status code: " + statusCode
                        + " but no " + HttpHeaders.LOCATION + " header was found in the response");
            }
            // Modify the redirect to go to this proxy servlet rather that the proxied host
            String locStr = rewriteUrlFromResponse(servletRequest, locationHeader.getValue());

            servletResponse.sendRedirect(locStr);
            return true;
        }
        // 304 needs special handling.  See:
        // http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
        // We get a 304 whenever passed an 'If-Modified-Since'
        // header and the data on disk has not changed; server
        // responds w/ a 304 saying I'm not going to send the
        // body because the file has not changed.
        if (statusCode == HttpServletResponse.SC_NOT_MODIFIED) {
            servletResponse.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
            servletResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return true;
        }
        return false;
    }

    protected void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * HttpClient v4.1 doesn't have the
     * {@link org.apache.http.util.EntityUtils#consumeQuietly(org.apache.http.HttpEntity)} method.
     *
     * @param entity - {@link HttpEntity}
     */
    protected void consumeQuietly(HttpEntity entity) {
        try {
            EntityUtils.consume(entity);
        } catch (IOException e) {//ignore
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * These are the "hop-by-hop" headers that should not be copied.
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html I use an HttpClient HeaderGroup class instead of
     */
    protected static final HeaderGroup HOP_BY_HOP_HEADERS;

    static {
        HOP_BY_HOP_HEADERS = new HeaderGroup();
        String[] headers = new String[]{
            "Connection", "Keep-Alive", "Proxy-Authenticate", 
            "TE", "Trailers", "Transfer-Encoding", "Upgrade"};
        for (String header : headers) {
            HOP_BY_HOP_HEADERS.addHeader(new BasicHeader(header, null));
        }
    }

    /**
     * Copy request headers from the servlet client to the proxy request.
     *
     * @param servletRequest - {@link HttpServletRequest}
     * @param proxyRequest - {@link HttpRequest}
     */
    protected void copyRequestHeaders(HttpServletRequest servletRequest, HttpRequest proxyRequest) {

        Enumeration enumerationOfHeaderNames = servletRequest.getHeaderNames();
        while (enumerationOfHeaderNames.hasMoreElements()) {
            String headerName = (String) enumerationOfHeaderNames.nextElement();
            //Instead the content-length is effectively set via InputStreamEntity
            if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                continue;
            }
            if (HOP_BY_HOP_HEADERS.containsHeader(headerName)) {
                continue;
            }

            Enumeration headers = servletRequest.getHeaders(headerName);
            while (headers.hasMoreElements()) {//sometimes more than one value
                String headerValue = (String) headers.nextElement();
                // In case the proxy host is running multiple virtual servers,
                // rewrite the Host header to ensure that we get content from
                // the correct virtual server
                if (headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
                    HttpHost host = URIUtils.extractHost(this.targetUriObj);
                    headerValue = host.getHostName();
                    if (host.getPort() != -1) {
                        headerValue += ":" + host.getPort();
                    }
                }
                proxyRequest.addHeader(headerName, headerValue);
            }
        }
    }


    private void setXForwardedForHeader(HttpServletRequest servletRequest,
            HttpRequest proxyRequest) {
        String headerName = "X-Forwarded-For";
        if (doForwardIP) {
            String newHeader = servletRequest.getRemoteAddr();
            String existingHeader = servletRequest.getHeader(headerName);
            if (existingHeader != null) {
                newHeader = existingHeader + ", " + newHeader;
            }
            proxyRequest.setHeader(headerName, newHeader);
        }
    }

    /**
     * Copy proxied response headers back to the servlet client.
     *
     * @param proxyResponse - {@link HttpResponse}
     * @param servletResponse - {@link HttpServletResponse}
     */
    protected void copyResponseHeaders(HttpResponse proxyResponse, HttpServletResponse servletResponse) {
        for (Header header : proxyResponse.getAllHeaders()) {
            if (HOP_BY_HOP_HEADERS.containsHeader(header.getName())) {
                continue;
            }
            servletResponse.addHeader(header.getName(), header.getValue());
        }
    }

    /**
     * Copy response body data (the entity) from the proxy to the servlet client..
     *
     * @param proxyResponse - {@link HttpResponse}
     * @param servletResponse - {@link HttpServletResponse}
     * @throws java.io.IOException
     */
    protected void copyResponseEntity(HttpResponse proxyResponse, HttpServletResponse servletResponse) throws IOException {
        HttpEntity entity = proxyResponse.getEntity();
        if (entity != null) {
            OutputStream servletOutputStream = servletResponse.getOutputStream();
            entity.writeTo(servletOutputStream);
        }
    }

    /**
     * For a redirect response from the target server, this translates {@code theUrl} to redirect to and translates it
     * to one the original client can use..
     *
     * @param servletRequest - {@link HttpServletRequest}
     * @param theUrl - The url to rewrite
     * @return the new URL
     */
    protected String rewriteUrlFromResponse(HttpServletRequest servletRequest, String theUrl) {
        //TODO document example paths
        if (theUrl.startsWith(serviceURL)) {
            String curUrl = servletRequest.getRequestURL().toString();//no query
            String pathInfo = servletRequest.getPathInfo();
            if (pathInfo != null) {
                assert curUrl.endsWith(pathInfo);
                curUrl = curUrl.substring(0, curUrl.length() - pathInfo.length());//take pathInfo off
            }
            theUrl = curUrl + theUrl.substring(serviceURL.length());
        }
        return theUrl;
    }

    public void setServiceScheme(String serviceScheme) {
        this.serviceScheme = serviceScheme;
    }

    public void setServiceHost(String serviceHost) {
        this.serviceHost = serviceHost;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public void setDoForwardIP(Boolean doForwardIP) {
        this.doForwardIP = doForwardIP;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public void setServiceContext(String serviceContext) {
        this.serviceContext = serviceContext;
    }
}
