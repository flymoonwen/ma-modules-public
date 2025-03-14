/**
 * Copyright (C) 2017 Infinite Automation Software. All rights reserved.
 *
 */
package com.infiniteautomation.mango.rest.v2.exception;

import java.io.IOException;
import java.util.concurrent.CompletionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.infiniteautomation.mango.db.query.RQLToCondition.RQLVisitException;
import com.infiniteautomation.mango.rest.v2.views.AdminView;
import com.infiniteautomation.mango.util.exception.InvalidRQLException;
import com.infiniteautomation.mango.util.exception.NotFoundException;
import com.infiniteautomation.mango.util.exception.TranslatableIllegalStateException;
import com.infiniteautomation.mango.util.exception.TranslatableRuntimeException;
import com.infiniteautomation.mango.util.exception.ValidationException;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.module.DefaultPagesDefinition;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.permission.PermissionException;
import com.serotonin.m2m2.web.mvc.spring.security.authentication.MangoPasswordAuthenticationProvider.AuthenticationRateException;

/**
 *
 * @author Terry Packer
 */
@ControllerAdvice(basePackages= {"com.infiniteautomation.mango.rest.v2"})
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final String REST_DEVELOPMENT_MODE = "development";  //Used to show exceptions during development instead of redirects
    private final Log LOG = LogFactory.getLog(RestExceptionHandler.class);

    final RequestMatcher browserHtmlRequestMatcher;
    final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public RestExceptionHandler(@Qualifier("browserHtmlRequestMatcher") RequestMatcher browserHtmlRequestMatcher, HandlerExceptionResolver handlerExceptionResolver) {
        this.browserHtmlRequestMatcher = browserHtmlRequestMatcher;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    /**
     * Work around for CompletionException until this PR is merged https://github.com/spring-projects/spring-framework/pull/22476
     */
    @ExceptionHandler(CompletionException.class)
    public ModelAndView handleCompletionException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, CompletionException ex) {
        return handlerExceptionResolver.resolveException(request, response, handlerMethod, (Exception) ex.getCause());
    }

    //Anything that extends our Base Exception
    @ExceptionHandler({AbstractRestV2Exception.class})
    public ResponseEntity<Object> handleMangoError(HttpServletRequest request, HttpServletResponse response, AbstractRestV2Exception ex, WebRequest req) {
        return handleExceptionInternal(ex, ex, new HttpHeaders(), ex.getStatus(), req);
    }

    //TODO Handle Permission Exception Here

    private final String ACCESS_DENIED = "/exception/accessDenied.jsp";
    @SuppressWarnings("unused")
    // XXX Previous code used this page if it was a CSRF exception, but this is not really an invalid session
    private final String INVALID_SESSION = "/exception/invalidSession.jsp";

    @ExceptionHandler({
        org.springframework.security.access.AccessDeniedException.class,
        PermissionException.class
    })
    public ResponseEntity<Object> handleAccessDenied(HttpServletRequest request, HttpServletResponse response, Exception ex, WebRequest req){
        Object model;

        if (ex instanceof PermissionException) {
            PermissionException permissionException = (PermissionException) ex;
            model = new AccessDeniedException(permissionException.getTranslatableMessage(), ex);
        } else {
            model = new AccessDeniedException(ex);
        }

        return handleExceptionInternal(ex, model, new HttpHeaders(), HttpStatus.FORBIDDEN, req);
    }

    @ExceptionHandler({
        ValidationException.class
    })
    public ResponseEntity<Object> handleValidationException(HttpServletRequest request, HttpServletResponse response, Exception ex, WebRequest req) {
        ValidationException validationException = (ValidationException) ex;
        return handleExceptionInternal(ex, new ValidationFailedRestException(validationException.getValidationResult()), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, req);
    }

    @ExceptionHandler({
        NotFoundException.class,
        ResourceNotFoundException.class
    })
    public ResponseEntity<Object> handleNotFoundException(HttpServletRequest request, HttpServletResponse response, Exception ex, WebRequest req) {
        return handleExceptionInternal(ex, new NotFoundRestException(ex), new HttpHeaders(), HttpStatus.NOT_FOUND, req);
    }

    @ExceptionHandler({
        InvalidRQLException.class
    })
    public ResponseEntity<Object> handleInvalidRQLException(HttpServletRequest request, HttpServletResponse response, InvalidRQLException ex, WebRequest req) {
        return handleExceptionInternal(ex, new InvalidRQLRestException(ex), new HttpHeaders(), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler({
        RQLVisitException.class
    })
    public ResponseEntity<Object> handleRQLVisitException(HttpServletRequest request, HttpServletResponse response, RQLVisitException ex, WebRequest req) {
        return handleExceptionInternal(ex, new RQLVisitRestException(ex), new HttpHeaders(), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler({
        TranslatableIllegalStateException.class
    })
    public ResponseEntity<Object> handleTranslatableIllegalStateException(HttpServletRequest request, HttpServletResponse response, TranslatableIllegalStateException ex, WebRequest req) {
        return handleExceptionInternal(ex, new IllegalStateRestException(ex.getTranslatableMessage(), ex), new HttpHeaders(), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler({
        TranslatableRuntimeException.class
    })
    public ResponseEntity<Object> handleTranslatableRuntimeException(HttpServletRequest request, HttpServletResponse response, TranslatableRuntimeException ex, WebRequest req) {

        GenericRestException body = new GenericRestException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getTranslatableMessage(), ex.getCause());
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, req);
    }

    @ExceptionHandler({
        TranslatableException.class
    })
    public ResponseEntity<Object> handleTranslatableException(HttpServletRequest request, HttpServletResponse response, TranslatableException ex, WebRequest req) {

        GenericRestException body = new GenericRestException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getTranslatableMessage(), ex.getCause());
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, req);
    }

    @ExceptionHandler(AuthenticationRateException.class)
    public ResponseEntity<Object> handleIpAddressAuthenticationRateException(HttpServletRequest request, HttpServletResponse response, AuthenticationRateException ex, WebRequest req) {
        RateLimitedRestException body = RateLimitedRestException.restExceptionFor(ex);
        return handleExceptionInternal(ex, body, new HttpHeaders(), body.getStatus(), req);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleUsernameAuthenticationRateException(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex, WebRequest req) {
        AuthenticationFailedRestException body = AuthenticationFailedRestException.restExceptionFor(ex);
        return handleExceptionInternal(ex, body, new HttpHeaders(), body.getStatus(), req);
    }

    @ExceptionHandler({
        Exception.class
    })
    public ResponseEntity<Object> handleAllOtherErrors(HttpServletRequest request, HttpServletResponse response, Exception ex, WebRequest req){
        return handleExceptionInternal(ex, new ServerErrorException(ex), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, req);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
            Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if(LOG.isDebugEnabled())
            LOG.debug("Rest API Exception", ex);
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        HttpServletResponse servletResponse = ((ServletWebRequest) request).getResponse();

        this.storeException(servletRequest, ex, status);

        if (this.browserHtmlRequestMatcher.matches(servletRequest) && !REST_DEVELOPMENT_MODE.equals(Common.envProps.getString("rest.mode", ""))) {
            String uri;
            if (status == HttpStatus.FORBIDDEN) {
                // browser HTML request
                uri = ACCESS_DENIED;

                User user = Common.getHttpUser();
                if (user != null) {
                    uri = DefaultPagesDefinition.getUnauthorizedUri(servletRequest, servletResponse,
                            user);
                }

                // Put exception into request scope (perhaps of use to a view)
                servletRequest.setAttribute(WebAttributes.ACCESS_DENIED_403, ex);

                // Set the 403 status code.
                servletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {
                uri = DefaultPagesDefinition.getErrorUri(servletRequest, servletResponse);
            }
            try {
                servletResponse.sendRedirect(uri);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
            return null;
        } else {
            // To strip off the double messages generated by this...
            if (ex instanceof NestedRuntimeException)
                ex = (Exception) ((NestedRuntimeException) ex).getMostSpecificCause();

            // If no body provided we will create one
            if (body == null)
                body = new GenericRestException(status, ex);

            //Add admin view if necessary
            User user = Common.getHttpUser();
            MappingJacksonValue value = new MappingJacksonValue(body);
            if(user != null && user.hasAdminPermission())
                value.setSerializationView(AdminView.class);
            else
                value.setSerializationView(Object.class);
            body = value;
            return new ResponseEntity<Object>(body, headers, status);
        }
    }

    /**
     * Store the exception into the session if one exists
     *
     * @param request
     * @param ex
     */
    protected void storeException(HttpServletRequest request, Exception ex, HttpStatus status){
        // Set Exception into Context
        HttpSession sesh = request.getSession(false);
        if (sesh != null)
            sesh.setAttribute(Common.SESSION_USER_EXCEPTION, ex);
    }

}
