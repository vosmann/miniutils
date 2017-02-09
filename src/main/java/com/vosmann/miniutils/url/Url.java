package com.vosmann.miniutils.url;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

// Add:
// public static Url from(final String fullUrl)
public class Url {

    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 1 << 16 - 1;

    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    private static final String SCHEME_DELIMITER = "://";
    private static final String PORT_DELIMITER = ":";

    private static final int PORT_DEFAULT = 80;

    private static final String PATH_ELEMENT_DELIMITER = "/";
    private static final String QUERY_DELIMITER = "?";
    private static final String QUERY_PARAMETER_DELIMITER = "&";
    private static final String NAME_VALUE_DELIMITER = "=";

    private final String scheme;
    private final String host;
    private final int port;
    private final List<String> pathElements;
    private final Map<String, String> queryParameters;

    private final String fullUrl;

    private Url(Builder builder) {
        checkArgument(builder.scheme == SCHEME_HTTP || builder.scheme == SCHEME_HTTPS, "Unsupported scheme.");
        checkArgument(MIN_PORT <= builder.port && builder.port <= MAX_PORT, "Invalid port.");
        checkArgument(!isNullOrEmpty(builder.host), "Missing host.");

        // Parts.
        scheme = builder.scheme;
        host = builder.host;
        port = builder.port;
        pathElements = builder.pathElements.build();
        queryParameters = builder.queryParameters.build();

        // Full URL.
        final StringBuilder urlBuilder = new StringBuilder().append(scheme)
                                                            .append(SCHEME_DELIMITER)
                                                            .append(host);
        if (port != PORT_DEFAULT) {
            urlBuilder.append(PORT_DELIMITER)
                      .append(port);
        }

        for (final String pathElement : pathElements) {
            if (isNullOrEmpty(pathElement)) {
                throw new IllegalArgumentException("Null or empty path element.");
            }
            urlBuilder.append(PATH_ELEMENT_DELIMITER)
                      .append(pathElement);
        }

        String delimiter = QUERY_DELIMITER;
        for (final Map.Entry<String, String> queryParameter : queryParameters.entrySet()) {
            if (isNullOrEmpty(queryParameter.getKey()) || isNullOrEmpty(queryParameter.getValue())) {
                throw new IllegalArgumentException("Null or empty query parameter name/value.");
            }
            urlBuilder.append(delimiter)
                      .append(queryParameter.getKey())
                      .append(NAME_VALUE_DELIMITER)
                      .append(queryParameter.getValue());

            delimiter = QUERY_PARAMETER_DELIMITER;
        }

        fullUrl = urlBuilder.toString();
    }

    public String getScheme() {
        return scheme;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public List<String> getPathElements() {
        return pathElements;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public String getQueryParameter(final String name) {
        return queryParameters.get(name);
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public static final class Builder {
        private String scheme = SCHEME_HTTP;
        private String host;
        private int port = PORT_DEFAULT;
        private final ImmutableList.Builder<String> pathElements = ImmutableList.builder();
        private final ImmutableMap.Builder<String, String> queryParameters = ImmutableMap.builder();

        public Builder scheme(String val) {
            scheme = val;
            return this;
        }

        public Builder host(String val) {
            host = val;
            return this;
        }

        public Builder port(int val) {
            port = val;
            return this;
        }

        public Builder pathElement(String val) {
            pathElements.add(val);
            return this;
        }

        public Builder queryParameter(String parameterName, String parameterValue) {
            queryParameters.put(parameterName, parameterValue);
            return this;
        }

        public Url build() {
            return new Url(this);
        }
    }

}
