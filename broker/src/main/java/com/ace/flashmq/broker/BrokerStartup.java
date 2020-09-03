package com.ace.flashmq.broker;

/**
 * @author:ace
 * @date:2020-09-02
 */
public class BrokerStartup {
    public static void main(String[] args) throws Throwable {
        createController().start();
    }

    public static BrokerController createController(){
        return new BrokerController();
    }
}
