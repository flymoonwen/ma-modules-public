/**
 * Copyright (C) 2019  Infinite Automation Software. All rights reserved.
 */
package com.infiniteautomation.mango.rest.v2;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.infiniteautomation.mango.rest.v2.model.RestModelMapper;
import com.infiniteautomation.mango.rest.v2.model.StreamedArrayWithTotal;
import com.infiniteautomation.mango.rest.v2.model.StreamedVORqlQueryWithTotal;
import com.infiniteautomation.mango.rest.v2.model.publisher.AbstractPublisherModel;
import com.infiniteautomation.mango.rest.v2.patch.PatchVORequestBody;
import com.infiniteautomation.mango.spring.service.PublisherService;
import com.infiniteautomation.mango.util.RQLUtils;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.publish.PublishedPointVO;
import com.serotonin.m2m2.vo.publish.PublisherVO;
import com.serotonin.m2m2.web.MediaTypes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.jazdw.rql.parser.ASTNode;

/**
 * @author Terry Packer
 *
 */
@Api(value="Mango Publishers")
@RestController
@RequestMapping("/publishers-v2")
public class PublishersRestController<POINT extends PublishedPointVO, PUBLISHER extends PublisherVO<POINT>> {

    private final PublisherService<POINT> service;
    private final BiFunction<PublisherVO<?>, User, AbstractPublisherModel<?,?>> map;

    @Autowired
    public PublishersRestController(final PublisherService<POINT> service, final RestModelMapper modelMapper) {
        this.service = service;
        this.map = (vo, user) -> {
            return modelMapper.map(vo, AbstractPublisherModel.class, user);
        };
    }

    
    @ApiOperation(
            value = "Query Publishers Sources",
            notes = "RQL Formatted Query",
            responseContainer="List"
            )
    @RequestMapping(method = RequestMethod.GET)
    public StreamedArrayWithTotal query(
            HttpServletRequest request,
            @ApiParam(value="User", required=true)
            @AuthenticationPrincipal User user,
            UriComponentsBuilder builder) {
        ASTNode rql = RQLUtils.parseRQLtoAST(request.getQueryString());
        return doQuery(rql, user);
    }

    @ApiOperation(
            value = "Get publisher by XID",
            notes = ""
            )
    @RequestMapping(method = RequestMethod.GET, value="/{xid}")
    public AbstractPublisherModel<?,?> get(
            @ApiParam(value = "XID of publisher", required = true, allowMultiple = false)
            @PathVariable String xid,
            @AuthenticationPrincipal User user,
            UriComponentsBuilder builder) {
        return map.apply(service.getFull(xid, user), user);
    }

    @ApiOperation(
            value = "Get publisher by ID",
            notes = ""
            )
    @RequestMapping(method = RequestMethod.GET, value="/by-id/{id}")
    public AbstractPublisherModel<?,?> getById(
            @ApiParam(value = "ID of publisher", required = true, allowMultiple = false)
            @PathVariable int id,
            @AuthenticationPrincipal User user,
            UriComponentsBuilder builder) {
        return map.apply(service.getFull(id, user), user);
    }

    @ApiOperation(value = "Save publisher")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<AbstractPublisherModel<?,?>> save(
            @RequestBody(required=true) AbstractPublisherModel<POINT, PUBLISHER> model,
            @AuthenticationPrincipal User user,
            UriComponentsBuilder builder,
            HttpServletRequest request) {

        PublisherVO<?> vo = this.service.insertFull(model.toVO(), user);
        URI location = builder.path("/publishers-v2/{xid}").buildAndExpand(new Object[]{vo.getXid()}).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>(map.apply(vo, user), headers, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update publisher")
    @RequestMapping(method = RequestMethod.PUT, value = "/{xid}")
    public ResponseEntity<AbstractPublisherModel<?,?>> update(
            @PathVariable String xid,
            @RequestBody(required=true) AbstractPublisherModel<POINT, PUBLISHER> model,
            @AuthenticationPrincipal User user,
            UriComponentsBuilder builder,
            HttpServletRequest request) {

        PublisherVO<?> vo = this.service.update(xid, model.toVO(), user);
        URI location = builder.path("/publishers-v2/{xid}").buildAndExpand(new Object[]{vo.getXid()}).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>(map.apply(vo, user), headers, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Partially update a publisher",
            notes = "Requires edit permission"
            )
    @RequestMapping(method = RequestMethod.PATCH, value = "/{xid}")
    public ResponseEntity<AbstractPublisherModel<?,?>> partialUpdate(
            @PathVariable String xid,

            @ApiParam(value = "Updated data source", required = true)
            @PatchVORequestBody(
                    service=PublisherService.class,
                    modelClass=AbstractPublisherModel.class)
            AbstractPublisherModel<POINT, PUBLISHER> model,
            @AuthenticationPrincipal User user,
            UriComponentsBuilder builder) {

        PublisherVO<?> vo = service.updateFull(xid, model.toVO(), user);

        URI location = builder.path("/publishers-v2/{xid}").buildAndExpand(vo.getXid()).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>(map.apply(vo, user), headers, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Delete a publisher",
            notes = "",
            response=AbstractPublisherModel.class
            )
    @RequestMapping(method = RequestMethod.DELETE, value="/{xid}")
    public AbstractPublisherModel<?,?> delete(
            @ApiParam(value = "XID of publisher to delete", required = true, allowMultiple = false)
            @PathVariable String xid,
            @AuthenticationPrincipal User user,
            UriComponentsBuilder builder) {
        return map.apply(service.delete(xid, user), user);
    }

    @ApiOperation(value = "Enable/disable/restart a publisher")
    @RequestMapping(method = RequestMethod.PUT, value = "/enable-disable/{xid}")
    public void enableDisable(
            @PathVariable String xid,

            @ApiParam(value = "Enable or disable the publisher", required = true, allowMultiple = false)
            @RequestParam(required=true) boolean enabled,

            @ApiParam(value = "Restart the publisher, enabled must equal true", required = false, defaultValue="false", allowMultiple = false)
            @RequestParam(required=false, defaultValue="false") boolean restart,

            @AuthenticationPrincipal User user) {
        service.restart(xid, enabled, restart, user);
    }


    @ApiOperation(
            value = "Export formatted for Configuration Import",
            notes = "")
    @RequestMapping(method = RequestMethod.GET, value = "/export/{xid}", produces = MediaTypes.SEROTONIN_JSON_VALUE)
    public Map<String, Object> exportDataSource(
            @ApiParam(value = "Valid publisher XID", required = true, allowMultiple = false)
            @PathVariable String xid,
            @AuthenticationPrincipal User user) {

        PublisherVO<?> vo = service.get(xid, user);
        Map<String,Object> export = new LinkedHashMap<>();
        export.put("publishers", Collections.singletonList(vo));
        return export;
    }

    /**
     * Perform a query
     * @param rql
     * @param user
     * @return
     */
    private StreamedArrayWithTotal doQuery(ASTNode rql, User user) {
        //If we are admin or have overall data source permission we can view all
        if (user.hasAdminPermission()) {
            return new StreamedVORqlQueryWithTotal<>(service, rql, vo -> map.apply(vo, user), true);
        } else {
            return new StreamedVORqlQueryWithTotal<>(service, rql, user, vo -> map.apply(vo, user), true);
        }
    }
    
}
