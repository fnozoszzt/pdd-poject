package fnozoszzt.pdd.common;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @ClassName: RestExceptionHandler
 * @Author: lipeisheng
 * @Date: 2018/9/4 10:09
 * @Description: TODO
 * @Version: 1.0
 */

public class MyRestExceptionHandler implements ResponseErrorHandler {

    /**
     * Delegates to {@link #hasError(HttpStatus)} with the response status code.
     */
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        return (statusCode != null && hasError(statusCode));
    }

    /**
     * Template method called from {@link #hasError(ClientHttpResponse)}.
     * <p>The default implementation checks if the given status code is
     * {@link HttpStatus.Series#CLIENT_ERROR CLIENT_ERROR} or
     * {@link HttpStatus.Series#SERVER_ERROR SERVER_ERROR}.
     * Can be overridden in subclasses.
     * @param statusCode the HTTP status code
     * @return {@code true} if the response has an error; {@code false} otherwise
     */
    protected boolean hasError(HttpStatus statusCode) {
        return (statusCode.series() == HttpStatus.Series.CLIENT_ERROR ||
                statusCode.series() == HttpStatus.Series.SERVER_ERROR);
    }

    /**
     * Delegates to {@link #handleError(ClientHttpResponse, HttpStatus)} with the response status code.
     */
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        if (statusCode == null) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(),
                    response.getHeaders(), getResponseBody(response), getCharset(response));
        }
        handleError(response, statusCode);
    }

    /**
     * Handle the error in the given response with the given resolved status code.
     * <p>This default implementation throws a {@link HttpClientErrorException} if the response status code
     * is {@link HttpStatus.Series#CLIENT_ERROR}, a {@link HttpServerErrorException}
     * if it is {@link HttpStatus.Series#SERVER_ERROR},
     * @since 5.0
     */
    protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
        switch (statusCode.series()) {
            case CLIENT_ERROR:
                throw new HttpClientErrorException(statusCode, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response));
            case SERVER_ERROR:
                throw new HttpServerErrorException(statusCode, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response));
            default:
                throw new UnknownHttpStatusCodeException(statusCode.value(), response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response));
        }
    }


    /**
     * Determine the HTTP status of the given response.
     * @param response the response to inspect
     * @return the associated HTTP status
     * @throws IOException in case of I/O errors
     * @throws UnknownHttpStatusCodeException in case of an unknown status code
     * that cannot be represented with the {@link HttpStatus} enum
     * @since 4.3.8
     * @deprecated as of 5.0, in favor of {@link #handleError(ClientHttpResponse, HttpStatus)}
     */
    @Deprecated
    protected HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        if (statusCode == null) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(),
                    response.getHeaders(), getResponseBody(response), getCharset(response));
        }
        return statusCode;
    }

    /**
     * Read the body of the given response (for inclusion in a status exception).
     * @param response the response to inspect
     * @return the response body as a byte array,
     * or an empty byte array if the body could not be read
     * @since 4.3.8
     */
    protected byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody());
        }
        catch (IOException ex) {
            // ignore
        }
        return new byte[0];
    }

    /**
     * Determine the charset of the response (for inclusion in a status exception).
     * @param response the response to inspect
     * @return the associated charset, or {@code null} if none
     * @since 4.3.8
     */
    @Nullable
    protected Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return (contentType != null ? contentType.getCharset() : null);
    }

}
