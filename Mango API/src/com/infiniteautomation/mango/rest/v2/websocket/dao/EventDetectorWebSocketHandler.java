/**
 * Copyright (C) 2019  Infinite Automation Software. All rights reserved.
 */
package com.infiniteautomation.mango.rest.v2.websocket.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.infiniteautomation.mango.rest.v2.model.RestModelMapper;
import com.infiniteautomation.mango.rest.v2.model.event.detectors.AbstractEventDetectorModel;
import com.infiniteautomation.mango.rest.v2.websocket.DaoNotificationWebSocketHandler;
import com.infiniteautomation.mango.rest.v2.websocket.WebSocketMapping;
import com.infiniteautomation.mango.spring.events.DaoEvent;
import com.infiniteautomation.mango.spring.service.EventDetectorsService;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.event.detector.AbstractEventDetectorVO;

/**
 * @author Terry Packer
 *
 */
@Component()
@WebSocketMapping("/websocket/full-event-detectors")
public class EventDetectorWebSocketHandler <T extends AbstractEventDetectorVO<T>> extends DaoNotificationWebSocketHandler<T>{

    private final EventDetectorsService<T> service;
    private final RestModelMapper modelMapper;

    @Autowired
    public EventDetectorWebSocketHandler(EventDetectorsService<T> service, RestModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @Override
    protected boolean hasPermission(User user, T vo) {
        return service.hasReadPermission(user, vo);
    }

    @Override
    protected Object createModel(T vo) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object createModel(T vo, User user) {
        return modelMapper.map(vo, AbstractEventDetectorModel.class, user);
    }

    @Override
    @EventListener
    protected void handleDaoEvent(DaoEvent<? extends T> event) {
        this.notify(event);
    }

    @Override
    protected boolean isModelPerUser() {
        return true;
    }

    
    
}
