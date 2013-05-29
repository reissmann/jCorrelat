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

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CorrelationChannelHandler extends SimpleChannelUpstreamHandler {
    private static final Logger logger = LoggerFactory.getLogger(CorrelationChannelHandler.class);
    
    private final StatefulKnowledgeSession session;
    
    public CorrelationChannelHandler(final String host) {
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

        final MessagePersister persister = new MessagePersister(host);
        this.session.addEventListener(new PersistingEventListener(persister));
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Message message = (Message) e.getMessage();

        this.session.insert(message);
        this.session.fireAllRules();
    }
}
