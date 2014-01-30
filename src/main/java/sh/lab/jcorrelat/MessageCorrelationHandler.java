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

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class MessageCorrelationHandler extends SimpleChannelInboundHandler<Message> implements Runnable, WorkingMemoryEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(MessagePersister.class);

    private final MessagePersister persister;

    private final StatefulKnowledgeSession session;

    private final Thread driver = new Thread(this);

    public MessageCorrelationHandler(final MessagePersister persister) {
        this.persister = persister;

        final KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add(ResourceFactory.newClassPathResource("sh/lab/jcorrelat/rules/test.drl"), ResourceType.DRL);

        if (knowledgeBuilder.hasErrors()) {
            throw new RuntimeException(knowledgeBuilder.getErrors().toString());
        }

        final KnowledgeBaseConfiguration knowledgeBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        knowledgeBaseConfiguration.setOption(EventProcessingOption.STREAM);

        final KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(knowledgeBaseConfiguration);
        knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());

        KnowledgeSessionConfiguration knowledgeSessionConfiguration = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
//        knowledgeSessionConfiguration.setOption(ClockTypeOption.get("pseudo"));
        knowledgeSessionConfiguration.setOption(ClockTypeOption.get("realtime"));

        this.session = knowledgeBase.newStatefulKnowledgeSession(knowledgeSessionConfiguration, null);

//        this.session.addEventListener(new DebugAgendaEventListener());
//        this.session.addEventListener(new DebugWorkingMemoryEventListener());
        
        this.session.addEventListener(this);
        
        this.driver.start();

        LOG.info("Correlation engine is up and running");
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Message message) throws Exception {
        MessageCorrelationHandler.this.session.insert(message);

        LOG.trace("Correlated message: {}", message);
    }

    public void run() {
        this.session.fireUntilHalt();
    }

    public void objectInserted(ObjectInsertedEvent event) {
        if (!(event.getObject() instanceof Message)) {
            return;
        }

        final Message message = (Message) event.getObject();

        this.persister.index(message);

        LOG.trace("Inserted message to working memory: {}", message);
    }

    public void objectUpdated(ObjectUpdatedEvent event) {
        if (!(event.getObject() instanceof Message)) {
            return;
        }

        final Message message = (Message) event.getObject();

        this.persister.index(message);

        LOG.debug("Updated message from working memory: {}", message);
    }

    public void objectRetracted(ObjectRetractedEvent event) {
        if (!(event.getOldObject() instanceof Message)) {
            return;
        }

        final Message message = (Message) event.getOldObject();

        this.persister.delete(message);

        LOG.debug("Deleted message from working memory: {}", message);
    }
}
