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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessagePersister {

    private static final Logger LOG = LoggerFactory.getLogger(MessagePersister.class);

    private static final String INDEX = "syslog";
    private static final String TYPE = "event";

    private final Client client;

    private final ObjectMapper mapper;

    private BulkRequestBuilder bulkRequestBuilder = null;

    private MessagePersister(final Client client) {
        this.client = client;

        this.bulkRequestBuilder = this.client.prepareBulk();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                MessagePersister.this.client.close();
            }
        }));

        this.mapper = new ObjectMapper();
    }

    public MessagePersister() {
        this(NodeBuilder.nodeBuilder()
                .client(App.CONF_ES_CLIENT)
                .settings(ImmutableSettings.settingsBuilder()
                        .put("network.bind_host", App.CONF_ES_BIND)
                        .put("network.publish_host", App.CONF_ES_BIND)
                        .build())
                .node()
                .client());
    }

    public void index(final Message message) {
        final byte[] source;
        try {
            source = this.mapper.writeValueAsBytes(message);

        } catch (final JsonProcessingException ex) {
            LOG.error(null, ex);
            return;
        }
        
        this.bulkRequestBuilder.add(this.client.prepareIndex()
                .setIndex(MessagePersister.INDEX)
                .setType(MessagePersister.TYPE)
                .setId(message.getId())
                .setSource(source, 0, source.length, true));
        
        if (this.bulkRequestBuilder.numberOfActions() > 64) this.execute();
    }

    public void delete(final Message message) {
        this.bulkRequestBuilder.add(this.client.prepareDelete()
                .setIndex(MessagePersister.INDEX)
                .setType(MessagePersister.TYPE)
                .setId(message.getId()));
        
        if (this.bulkRequestBuilder.numberOfActions() > 64) this.execute();
    }

    private void execute() {
        this.bulkRequestBuilder.execute(new ActionListener<BulkResponse>() {
            public void onResponse(final BulkResponse response) {
                LOG.trace("Executed elasticsearch build request: {} ms", response.getTookInMillis());
            }

            public void onFailure(final Throwable e) {
                LOG.error(null, e);
            }
        });
        
        this.bulkRequestBuilder = this.client.prepareBulk();
    }
}
