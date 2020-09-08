package com.ace.flashmq.broker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author:ace
 * @date:2020-09-02
 */
public class BrokerStartup {
    static final Log logger = LogFactory.getLog(BrokerStartup.class);

    public static void main(String[] args) throws Throwable {
        createController().start();
        logger.info("broker start success ...");
    }

    public static BrokerController createController(){
        return new BrokerController();
    }
}
