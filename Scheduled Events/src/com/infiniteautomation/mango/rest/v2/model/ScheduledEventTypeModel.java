/**
 * Copyright (C) 2018  Infinite Automation Software. All rights reserved.
 */
package com.infiniteautomation.mango.rest.v2.model;

import com.infiniteautomation.mango.rest.v2.model.event.AbstractEventTypeModel;
import com.serotonin.m2m2.scheduledEvents.ScheduledEventType;

/**
 * @author Terry Packer
 *
 */
public class ScheduledEventTypeModel extends AbstractEventTypeModel<ScheduledEventType, ScheduledEventModel, Void> {

    public ScheduledEventTypeModel() {
        super(new ScheduledEventType());
    }
    
    public ScheduledEventTypeModel(ScheduledEventType type) {
        super(type);
    }

    public ScheduledEventTypeModel(ScheduledEventType type, ScheduledEventModel source) {
        super(type, source);
    }
    
    @Override
    public ScheduledEventType toVO() {
        return new ScheduledEventType(referenceId1, duplicateHandling);
    }

}
