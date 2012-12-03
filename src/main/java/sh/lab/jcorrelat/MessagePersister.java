/*
 * Copyright 2011 Dustin Frisch<fooker@lab.sh>
 * 
 * This file is part of senchineru.
 * 
 * senchineru is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * senchineru is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with senchineru.  If not, see <http://www.gnu.org/licenses/>.
 */
package sh.lab.jcorrelat;

import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessagePersister {
    private static final Logger logger = LoggerFactory.getLogger(MessagePersister.class);
    
    private static final String INDEX = "syslog";
    private static final String TYPE = "event";
    private final Client client;
    private final ObjectMapper mapper;

    private MessagePersister(final Client client) {
        this.client = client;

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                MessagePersister.this.client.close();
            }
        }));

        this.mapper = new ObjectMapper();
    }

    public MessagePersister(final String host) {
        this(NodeBuilder.nodeBuilder()
                .client(true)
                .settings(ImmutableSettings.settingsBuilder()
                    .put("network.bind_host", host)
                    .put("network.publish_host", host)
                    .build())
                .node()
                .client());
    }

    private String toJson(final Message message) {
        try {
            return this.mapper.writeValueAsString(message);
            
        } catch (IOException ex) {
            logger.error(null, ex);
            return null;
        }
    }

    public void index(final Message message) {
        IndexResponse response = this.client.prepareIndex()
                .setIndex(MessagePersister.INDEX)
                .setType(MessagePersister.TYPE)
                .setId(message.getId())
                .setSource(this.toJson(message))
                .execute()
                .actionGet();
        
        message.setId(response.getId());
    }

    public void delete(final Message message) {
        this.client.prepareDelete()
                .setIndex(MessagePersister.INDEX)
                .setType(MessagePersister.TYPE)
                .setId(message.getId())
                .execute().actionGet();
    }
}
