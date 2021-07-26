package easy_web.processor;

import easy_web.connector.ConnectorUtils;
import easy_web.connector.Request;
import easy_web.connector.Response;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Package:easy_web.processor
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class ServletProcessor {

    URLClassLoader getServletLoader() throws MalformedURLException {
        File webroot = new File(ConnectorUtils.WEB_ROOT);
        URL webrootUrl = webroot.toURI().toURL();
        return new URLClassLoader(new URL[]{webrootUrl});
    }

    Servlet getServlet(URLClassLoader urlClassLoader,Request request) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        /*
        /servlet/TimeServlet
         */
        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);

        Class servletClass = urlClassLoader.loadClass(servletName);
        Servlet o = (Servlet) servletClass.newInstance();
        return o;
    }

    public void processor(Request request, Response response) {
        try {
            URLClassLoader loader = getServletLoader();
            Servlet servlet = getServlet(loader, request);
            servlet.service(request,response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
