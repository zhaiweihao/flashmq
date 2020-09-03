package com.ace.flashmq.client.impl;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author:ace
 * @date:2020-09-03
 */
public class ClientManager {

    private static ClientManager instance = new ClientManager();
    private ConcurrentHashMap<String /* clientId */, ClientInstance> clientFactory = new ConcurrentHashMap<String, ClientInstance>();

    public static ClientManager getInstance() {
        return instance;
    }

    public ClientInstance getOrCreateClientInstance(final String clientId) {
        ClientInstance client = clientFactory.get(clientId);
        if (null == client) {
            client = new ClientInstance();
            ClientInstance prev = clientFactory.putIfAbsent(clientId, client);
            if(prev != null){
                client = prev;
            }
        }
        return client;
    }
}
