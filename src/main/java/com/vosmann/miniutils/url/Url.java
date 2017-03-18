package com.vosmann.miniutils.url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

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
    private final Map<String, List<String>> queryParameters;

    private final String fullUrl;

    private Url(Builder builder) {
        checkArgument(SCHEME_HTTP.equals(builder.scheme) || SCHEME_HTTPS.equals(builder.scheme), "Unsupported scheme.");
        checkArgument(MIN_PORT <= builder.port && builder.port <= MAX_PORT, "Invalid port.");
        checkArgument(!isNullOrEmpty(builder.host), "Missing host.");

        // Parts.
        scheme = builder.scheme;
        host = builder.host;
        port = builder.port;
        pathElements = unmodifiableList(builder.pathElements);
        final Map<String, List<String>> params = new HashMap<>();
        builder.queryParameters.forEach((paramName, paramValues) -> params.put(paramName, unmodifiableList
                (paramValues)));
        queryParameters = unmodifiableMap(params);

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
        for (final Map.Entry<String, List<String>> queryParameter : queryParameters.entrySet()) {

            final String paramName = queryParameter.getKey();
            final List<String> paramValues = queryParameter.getValue();

            if (isNullOrEmpty(paramName) || paramValues.isEmpty()) {
                throw new IllegalArgumentException("Null or empty query parameter name or no values provided.");
            }

            for (final String paramValue : paramValues) {
                if (isNullOrEmpty(paramValue)) {
                    throw new IllegalArgumentException("Null or empty query parameter value.");
                }
                urlBuilder.append(delimiter)
                          .append(paramName)
                          .append(NAME_VALUE_DELIMITER)
                          .append(paramValue);
                delimiter = QUERY_PARAMETER_DELIMITER;
            }

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

    public Map<String, List<String>> getQueryParameters() {
        return queryParameters;
    }

    public List<String> getQueryParameter(final String name) {
        return queryParameters.get(name);
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public static final class Builder {

        private String scheme = SCHEME_HTTP;
        private String host;
        private int port = PORT_DEFAULT;
        private final List<String> pathElements = new ArrayList<>();
        private final Map<String, List<String>> queryParameters = new HashMap<>();

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
            if (!queryParameters.containsKey(parameterName)) {
                queryParameters.put(parameterName, new ArrayList<>());
            }
            queryParameters.get(parameterName).add(parameterValue);
            return this;
        }

        public Url build() {
            return new Url(this);
        }
    }

}
