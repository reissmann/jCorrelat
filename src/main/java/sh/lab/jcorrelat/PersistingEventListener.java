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

import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistingEventListener implements WorkingMemoryEventListener {
    private static final Logger logger = LoggerFactory.getLogger(MessagePersister.class);

    private final MessagePersister persister;
    
    public PersistingEventListener(final MessagePersister persister) {
        this.persister = persister;
    }
    
    public void objectInserted(ObjectInsertedEvent event) {
        if (!(event.getObject() instanceof Message)) {
            return;
        }

        this.persister.index((Message) event.getObject());
    }

    public void objectUpdated(ObjectUpdatedEvent event) {
        if (!(event.getObject() instanceof Message)) {
            return;
        }

        this.persister.index((Message) event.getObject());
    }

    public void objectRetracted(ObjectRetractedEvent event) {
        if (!(event.getOldObject() instanceof Message)) {
            return;
        }
        
        this.persister.delete((Message) event.getOldObject());
    }
}
