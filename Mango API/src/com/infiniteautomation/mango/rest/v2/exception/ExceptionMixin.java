/**
 * Copyright (C) 2019  Infinite Automation Software. All rights reserved.
 */
package com.infiniteautomation.mango.rest.v2.exception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.infiniteautomation.mango.rest.v2.views.AdminView;

/**
 * Limit output for exceptions
 * 
 * @author Terry Packer
 *
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public interface ExceptionMixin {

    @JsonProperty
    String getLocalizedMessage();
    
    @JsonProperty
    @JsonView(AdminView.class)
    Throwable getCause();
    
    @JsonProperty
    @JsonView(AdminView.class)
    StackTraceElement[] getStackTrace();
}
