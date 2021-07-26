package easy_web.processor;

import easy_web.connector.Request;
import easy_web.connector.Response;

import java.io.IOException;

/**
 * Package:easy_web.processor
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class StaticProcessor {
    public void processor(Request request, Response response) {
        try {
            response.sendStaticResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
