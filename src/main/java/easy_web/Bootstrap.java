package easy_web;

import easy_web.connector.Connector;

/**
 * Package:easy_web
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class Bootstrap {
    public static void main(String[] args) {
        Connector connector = new Connector();
        connector.start();
    }
}
