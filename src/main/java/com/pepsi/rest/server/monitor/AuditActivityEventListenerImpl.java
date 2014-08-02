package com.pepsi.rest.server.monitor;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.internal.util.collection.StringIgnoreCaseKeyComparator;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

/**
 * This class is used to log request & response message and monitoring statistics.
 * TODO: Better format of the LOG
 */
@Provider
public class AuditActivityEventListenerImpl implements RequestEventListener {
    private static final Logger LOG = LogManager.getLogger(AuditActivityEventListenerImpl.class);

    private static final String NOTIFICATION_PREFIX = "* ";
    private static final String REQUEST_PREFIX = "> ";
    private static final String RESPONSE_PREFIX = "< ";

    private final long activityID;
    private final long methodStartTime;

    private static final Comparator<Map.Entry<String, List<String>>> COMPARATOR =
            new Comparator<Map.Entry<String, List<String>>>() {

        @Override
        public int compare(final Map.Entry<String, List<String>> o1, final Map.Entry<String, List<String>> o2) {
            return StringIgnoreCaseKeyComparator.SINGLETON.compare(o1.getKey(), o2.getKey());
        }
    };

    public AuditActivityEventListenerImpl(long activityID) {
        this.activityID = activityID;
        this.methodStartTime = System.currentTimeMillis();
    }

    private StringBuilder prefixId(final StringBuilder builder, final long id) {
        builder.append(Long.toString(id)).append(" ");
        return builder;
    }

    private void printRequestLine(final StringBuilder b, final String note, final long id, final String method, final URI uri) {
        prefixId(b, id).append(NOTIFICATION_PREFIX)
        .append(note)
        .append(" on thread ").append(Thread.currentThread().getName())
        .append("\n");
        prefixId(b, id).append(REQUEST_PREFIX).append(method).append(" ").
        append(uri.toASCIIString()).append("\n");
    }

    private void printResponseLine(final StringBuilder b, final String note, final long id, final int status, final long timeElapsed) {
        prefixId(b, id).append(NOTIFICATION_PREFIX)
        .append(note).append(timeElapsed)
        .append(" ms on thread ").append(Thread.currentThread().getName()).append("\n");
        prefixId(b, id).append(RESPONSE_PREFIX).
        append(Integer.toString(status)).
        append("\n");
    }

    private void printPrefixedHeaders(final StringBuilder b, final long id, final String prefix, final MultivaluedMap<String, String> headers) {
        for (final Map.Entry<String, List<String>> headerEntry : getSortedHeaders(headers.entrySet())) {
            final List<?> val = headerEntry.getValue();
            final String header = headerEntry.getKey();

            if (val.size() == 1) {
                prefixId(b, id).append(prefix).append(header).append(": ").append(val.get(0)).append("\n");
            } else {
                final StringBuilder sb = new StringBuilder();
                boolean add = false;
                for (final Object s : val) {
                    if (add) {
                        sb.append(',');
                    }
                    add = true;
                    sb.append(s);
                }
                prefixId(b, id).append(prefix).append(header).append(": ").append(sb.toString()).append("\n");
            }
        }
    }

    private Set<Map.Entry<String, List<String>>> getSortedHeaders(final Set<Map.Entry<String, List<String>>> headers) {
        final TreeSet<Map.Entry<String, List<String>>> sortedHeaders = new TreeSet<Map.Entry<String, List<String>>>(COMPARATOR);
        sortedHeaders.addAll(headers);
        return sortedHeaders;
    }

    @Override
    public void onEvent(RequestEvent event) {
        if (LOG.isDebugEnabled()) {
            switch (event.getType()) {
            // Note: The following code will only run if the request passed the preMatching filter.
            case MATCHING_START:
                ContainerRequest request = event.getContainerRequest();
                final StringBuilder requestBuilder = new StringBuilder();
                printRequestLine(requestBuilder, "Server has received a request", activityID, request.getMethod(), request.getUriInfo().getRequestUri());
                printPrefixedHeaders(requestBuilder, activityID, REQUEST_PREFIX, request.getHeaders());
                LOG.debug(requestBuilder.toString());
                break;

            case FINISHED:
                final StringBuilder responseBuilder = new StringBuilder();
                ContainerResponse response = event.getContainerResponse();
                printResponseLine(responseBuilder, "Server responded with a response in ", activityID, response.getStatus(), (System.currentTimeMillis() - methodStartTime));
                printPrefixedHeaders(responseBuilder, activityID, RESPONSE_PREFIX, response.getStringHeaders());
                LOG.debug(responseBuilder.toString());
                break;
            default:
                break;
            }
        }
    }
}
