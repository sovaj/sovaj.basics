package org.mdubois.basics.web;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author Mickael Dubois
 */
public class CharResponseWrapper extends HttpServletResponseWrapper {

    private final CharArrayWriter output;

    public String toString() {
        return output.toString();
    }

    byte[] getBuffer() {
        return toString().getBytes();
    }

    public CharResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new CharArrayWriter();
    }

    public PrintWriter getWriter() {
        return new PrintWriter(output);
    }

}
