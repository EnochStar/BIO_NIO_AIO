package easy_web.connector;

import easy_web.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


/**
 * Package:easy_web.connector
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class RequestTest {
    private static final String validRequest = "GET /index.html HTTP/1.1";
    @Test
    public void givenValidRequest() {
        Request request = TestUtils.createRequest(validRequest);
        Assert.assertEquals("/index.html",request.getUri());
    }
}