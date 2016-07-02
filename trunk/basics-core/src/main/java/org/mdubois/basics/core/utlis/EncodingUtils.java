package org.mdubois.basics.core.utlis;

import java.net.URI;
import java.util.BitSet;
import java.util.Formatter;

/**
 *
 * @author Mickael Dubois
 */
public class EncodingUtils {

    /**
     * Return the list of restricted chars. This characters should not be use in
     * a URL parameter or string query since they are reserver for URL
     *
     * @return The list of restricted chars
     */
    public static String getRestrictedChars() {
        return PUNCT_STRING + RESERVED_STRING;
    }

    /**
     * Encodes characters in the query or fragment part of the URI.
     *
     * <p>
     * Unfortunately, an incoming URI sometimes has characters disallowed by the
     * spec. HttpClient insists that the outgoing proxied request has a valid
     * URI because it uses Java's {@link URI}. To be more forgiving, we must
     * escape the problematic characters. See the URI class for the spec.
     *
     * @param in example: name=value&amp;foo=bar#fragment
     * @return True if the input contains URL specific character
     */
    public static boolean containsURLSpecificCharacter(CharSequence in) {
        //Note that I can't simply use URI.java to encode because it will escape pre-existing escaped things.
        if (in == null) {
            return false;
        }

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            boolean escape = false;
            if (c < 128) {
                if (RESTRICTED_CHARS.get((int) c)) {
                    escape = true;
                }
            } else if (!Character.isISOControl(c) && !Character.isSpaceChar(c)) {//not-ascii
                escape = true;
            }
            if (escape) {
                return true;
            }
        }
        return false;
    }

    /**
     * Encodes characters in the query or fragment part of the URI.
     *
     * <p>
     * Unfortunately, an incoming URI sometimes has characters disallowed by the
     * spec. HttpClient insists that the outgoing proxied request has a valid
     * URI because it uses Java's {@link URI}. To be more forgiving, we must
     * escape the problematic characters. See the URI class for the spec.
     *
     * @param in example: name=value&amp;foo=bar#fragment
     * @return The encoded {@link  CharSequence}
     */
    public static CharSequence encodeUriQuery(CharSequence in) {
        //Note that I can't simply use URI.java to encode because it will escape pre-existing escaped things.
        StringBuilder outBuf = null;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            boolean escape = true;
            if (c < 128) {
                if (ASCII_QUERY_CHARS.get((int) c)) {
                    escape = false;
                }
            } else if (!Character.isISOControl(c) && !Character.isSpaceChar(c)) {//not-ascii
                escape = false;
            }
            if (!escape) {
                if (outBuf != null) {
                    outBuf.append(c);
                }
            } else {
                //escape
                if (outBuf == null) {
                    outBuf = new StringBuilder(in.length() + 5 * 3);
                    outBuf.append(in, 0, i);
                    Formatter formatter = new Formatter(outBuf);
                    //leading %, 0 padded, width 2, capital hex
                    formatter.format("%%%02X", (int) c);//TODO
                }
            }
        }
        return outBuf != null ? outBuf : in;
    }

    protected static final BitSet ASCII_QUERY_CHARS;
    protected static final BitSet RESTRICTED_CHARS;

    protected static final char[] UNRESERVE_CHARS = "_-!.~'()*".toCharArray();//plus alphanum
    protected static final String PUNCT_STRING = ",;:$&+=";
    protected static final char[] PUNCT_CHARS = PUNCT_STRING.toCharArray();
    protected static final String RESERVED_STRING = "?/[]@\\";
    protected static final char[] RESERVED_CHARS = RESERVED_STRING.toCharArray();//plus punct

    static {

        ASCII_QUERY_CHARS = new BitSet(128);

        RESTRICTED_CHARS = new BitSet(128);
        for (char c = 'a'; c <= 'z'; c++) {
            ASCII_QUERY_CHARS.set((int) c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            ASCII_QUERY_CHARS.set((int) c);
        }
        for (char c = '0'; c <= '9'; c++) {
            ASCII_QUERY_CHARS.set((int) c);
        }
        for (char c : UNRESERVE_CHARS) {
            ASCII_QUERY_CHARS.set((int) c);
        }
        for (char c : PUNCT_CHARS) {
            ASCII_QUERY_CHARS.set((int) c);
            RESTRICTED_CHARS.set((int) c);
        }
        for (char c : RESERVED_CHARS) {
            ASCII_QUERY_CHARS.set((int) c);
            RESTRICTED_CHARS.set((int) c);
        }

        ASCII_QUERY_CHARS.set((int) '%');//leave existing percent escapes in place
        RESTRICTED_CHARS.set((int) '%');//leave existing percent escapes in place
        RESTRICTED_CHARS.set((int) '\\');//leave existing percent escapes in place
        RESTRICTED_CHARS.set((int) '	');//leave existing percent escapes in place
        RESTRICTED_CHARS.set((int) ' ');//leave existing percent escapes in place
    }
}
