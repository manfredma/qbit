package io.advantageous.qbit.http;

import io.advantageous.qbit.util.MultiMap;
import io.advantageous.qbit.util.MultiMapImpl;
import org.boon.Str;
import org.boon.primitive.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Created by rhightower on 10/24/14.
 *
 * @author rhightower
 */
public class HttpRequestBuilder {


    public static final byte[] EMPTY_STRING = "".getBytes(StandardCharsets.UTF_8);
    private String uri;

    private String contentType;
    private String remoteAddress;
    private MultiMap<String, String> params;
    private String body;
    private String method = "GET";
    private HttpResponse response;
    private MultiMap<String, String> headers;


    public String getUri() {
        return uri;
    }

    public HttpRequestBuilder setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public HttpRequestBuilder setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
        return this;
    }

    public MultiMap<String, String> getParams() {
        return params;
    }

    public HttpRequestBuilder setParams(MultiMap<String, String> params) {
        this.params = params;
        return this;
    }

    public String getBody() {
        return body;
    }

    public HttpRequestBuilder setBody(String body) {
        this.body = body;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public HttpRequestBuilder setMethod(String method) {
        this.method = method;
        return this;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public HttpRequestBuilder setResponse(HttpResponse response) {
        this.response = response;
        return this;
    }

    public HttpRequest build() {


        if (params != null && params.size() > 1) {
            String paramString = paramString();
            switch (method) {
                case "GET":
                case "OPTION":
                case "HEAD":
                case "DELETE":
                    uri = Str.add(uri, "?", paramString);
                    break;
                case "POST":
                case "PUT":
                    body = paramString;
                    contentType = "application/x-www-form-urlencoded";
                    break;
            }
        }
        return new HttpRequest(uri, method, params, headers,
                body != null ? body.getBytes(StandardCharsets.UTF_8) : EMPTY_STRING,
                remoteAddress, contentType, response);
    }

    public String getContentType() {
        return contentType;
    }

    public HttpRequestBuilder setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public MultiMap<String, String> getHeaders() {
        return headers;
    }

    public HttpRequestBuilder setHeaders(MultiMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequestBuilder setJsonContentType() {

        contentType = "application/json";
        return this;
    }

    public HttpRequestBuilder setJsonBodyForPost(final String body) {
        setJsonContentType();
        this.setBody(body);
        this.setMethod("POST");
        return this;
    }

    public HttpRequestBuilder setJsonBodyForPut(final String body) {
        setJsonContentType();
        this.setBody(body);
        this.setMethod("PUT");
        return this;
    }



    public HttpRequestBuilder addHeader(final String name, final String value) {
        if (headers == null) {
            headers = new MultiMapImpl<>();
        }
        headers.put(name, value);
        return this;
    }


    public HttpRequestBuilder addParam(final String name, final String value) {
        if (params == null) {
            params = new MultiMapImpl<>();
        }
        params.put(name, value);
        return this;
    }



    private String paramString() {
        String paramString = null;


        if (params != null) {

            ByteBuf buf = ByteBuf.create(244);

            final Set<String> keys = params.keySet();

            int index = 0;
            for (String key : keys) {

                final Iterable<String> paramsAtKey = params.getAll(key);

                for (Object value : paramsAtKey) {
                    if (index > 0) {
                        buf.addByte('&');
                    }


                    buf.addUrlEncoded(key);
                    buf.addByte('=');

                    if (!(value instanceof byte[])) {
                        buf.addUrlEncoded(value.toString());
                    } else {
                        buf.addUrlEncodedByteArray((byte[]) value);
                    }
                    index++;
                }
            }

            paramString = buf.toString();
        }


        return paramString;

    }
}
