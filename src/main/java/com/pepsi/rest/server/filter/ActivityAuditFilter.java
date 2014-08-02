package com.pepsi.rest.server.filter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.internal.util.collection.StringIgnoreCaseKeyComparator;

/**
 * 
 * Universal logging filter used on server side. It has the highest priority.
 * 
 * This class is most copied from org.glassfish.jersey.filter.LoggingFilter
 * We do this so that we can use Log4J instead of JDK logging.
 *
 */

//Comment out this filter since we do not use it at this moment.
//Without @Provider, this filter will not be registered.
//@Provider
@PreMatching
@Priority(Integer.MIN_VALUE)
public class ActivityAuditFilter implements ContainerRequestFilter, ContainerResponseFilter, WriterInterceptor {
    private static final Logger LOG = LogManager.getLogger(ActivityAuditFilter.class);

    private static final String NOTIFICATION_PREFIX = "* ";
    private static final String REQUEST_PREFIX = "> ";
    private static final String RESPONSE_PREFIX = "< ";
    private static final String ENTITY_LOGGER_PROPERTY = ActivityAuditFilter.class.getName() + ".entityLogger";
    private static final int DEFAULT_MAX_ENTITY_SIZE = 8 * 1024;
    
    private static final Comparator<Map.Entry<String, List<String>>> COMPARATOR =
            new Comparator<Map.Entry<String, List<String>>>() {

                @Override
                public int compare(final Map.Entry<String, List<String>> o1, final Map.Entry<String, List<String>> o2) {
                    return StringIgnoreCaseKeyComparator.SINGLETON.compare(o1.getKey(), o2.getKey());
                }
            };
    
    private final AtomicLong activityRequestID = new AtomicLong(0);
    private final AtomicLong activityResponseID = new AtomicLong(0);
    private final boolean printEntity;
    private final int maxEntitySize;
    
    public ActivityAuditFilter() {
        this(false);
    }

    public ActivityAuditFilter(final boolean printEntity) {
        this.printEntity = printEntity;
        this.maxEntitySize = DEFAULT_MAX_ENTITY_SIZE;
    }
    
    public ActivityAuditFilter(final int maxEntitySize) {
        this.printEntity = true;
        this.maxEntitySize = maxEntitySize;
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

    private void printResponseLine(final StringBuilder b, final String note, final long id, final int status) {
        prefixId(b, id).append(NOTIFICATION_PREFIX)
                .append(note)
                .append(" on thread ").append(Thread.currentThread().getName()).append("\n");
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
    
    private InputStream logInboundEntity(final StringBuilder b, InputStream stream) throws IOException {
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        stream.mark(maxEntitySize + 1);
        final byte[] entity = new byte[maxEntitySize + 1];
        final int entitySize = stream.read(entity);
        b.append(new String(entity, 0, Math.min(entitySize, maxEntitySize)));
        if (entitySize > maxEntitySize) {
            b.append("...more...");
        }
        b.append('\n');
        stream.reset();
        return stream;
    }
    
    /**
     * TODO: update the log format
     */
    @Override
    public void filter(final ContainerRequestContext context) throws IOException {
        final long activityRequestID = this.activityRequestID.incrementAndGet();
        final StringBuilder builder = new StringBuilder();
        if (LOG.isDebugEnabled()) {
            printRequestLine(builder, "Server has received a request", activityRequestID, context.getMethod(), context.getUriInfo().getRequestUri());
            printPrefixedHeaders(builder, activityRequestID, REQUEST_PREFIX, context.getHeaders());

            if (printEntity && context.hasEntity()) {
                context.setEntityStream(logInboundEntity(builder, context.getEntityStream()));
            }
            LOG.debug(builder.toString());
        }
    }

    /**
     * TODO: update the log format
     */
    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
        final long activityResponseID = this.activityResponseID.incrementAndGet();
        final StringBuilder builder = new StringBuilder();
        if (LOG.isDebugEnabled()) {
            printResponseLine(builder, "Server responded with a response", activityResponseID, responseContext.getStatus());
            printPrefixedHeaders(builder, activityResponseID, RESPONSE_PREFIX, responseContext.getStringHeaders());

            if (printEntity && responseContext.hasEntity()) {
                final OutputStream stream = new LoggingStream(builder, responseContext.getEntityStream());
                responseContext.setEntityStream(stream);
                requestContext.setProperty(ENTITY_LOGGER_PROPERTY, stream);
                // not calling log(b) here - it will be called by the interceptor
            } else {
                LOG.debug(builder.toString());
            }
        }
    }
    
    @Override
    public void aroundWriteTo(final WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
        final LoggingStream stream = (LoggingStream) writerInterceptorContext.getProperty(ENTITY_LOGGER_PROPERTY);
        writerInterceptorContext.proceed();
        if (stream != null) {
            LOG.debug("{}", stream.getStringBuilder());
        }
    }

    private class LoggingStream extends OutputStream {
        private final StringBuilder b;
        private final OutputStream inner;
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        LoggingStream(final StringBuilder b, final OutputStream inner) {
            this.b = b;
            this.inner = inner;
        }

        StringBuilder getStringBuilder() {
            // write entity to the builder
            final byte[] entity = baos.toByteArray();

            b.append(new String(entity, 0, Math.min(entity.length, maxEntitySize)));
            if (entity.length > maxEntitySize) {
                b.append("...more...");
            }
            b.append('\n');

            return b;
        }

        @Override
        public void write(final int i) throws IOException {
            if (baos.size() <= maxEntitySize) {
                baos.write(i);
            }
            inner.write(i);
        }
    }
}
