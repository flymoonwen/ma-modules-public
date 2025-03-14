/**
 * Copyright (C) 2017 Infinite Automation Software. All rights reserved.
 *
 */
package com.infiniteautomation.mango.rest.v2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.infiniteautomation.mango.db.query.QueryAttribute;
import com.infiniteautomation.mango.db.query.TableModel;
import com.infiniteautomation.mango.db.query.appender.SQLColumnQueryAppender;
import com.serotonin.m2m2.db.dao.AbstractBasicDao;
import com.serotonin.m2m2.vo.AbstractBasicVO;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.web.mvc.rest.IMangoVoRestController;
import com.serotonin.m2m2.web.mvc.rest.v1.message.RestProcessResult;
import com.serotonin.m2m2.web.mvc.rest.v1.model.PageQueryStream;
import com.serotonin.m2m2.web.mvc.rest.v1.model.QueryStream;
import com.serotonin.m2m2.web.mvc.rest.v1.model.VoStreamCallback;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.jazdw.rql.parser.ASTNode;

/**
 * 
 * @author Terry Packer
 */
public abstract class AbstractMangoVoRestV2Controller <VO extends AbstractBasicVO, MODEL, DAO extends AbstractBasicDao<VO>> extends AbstractMangoRestV2Controller implements IMangoVoRestController<VO, MODEL, DAO>{

	protected DAO dao;
	
	//Map of keys -> model members to value -> Vo member/sql column
	protected Map<String,String> modelMap;
	//Map of Vo member/sql column to value converter
	protected Map<String, SQLColumnQueryAppender> appenders;

	/**
	 * Construct a Controller using the default callback
	 * @param dao
	 */
	public AbstractMangoVoRestV2Controller(DAO dao){
		this.dao = dao;
		this.modelMap = new HashMap<String,String>();
		this.appenders = new HashMap<String, SQLColumnQueryAppender>();
	}

	/**
	 * Get the Query Stream for Streaming an array of data
	 * @param query
	 * @return
	 */
	protected QueryStream<VO, MODEL, DAO> getStream(ASTNode root, User user){
		return this.getStream(root, new VoStreamCallback<VO, MODEL, DAO>(this, user));
	}
	
	/**
	 * Get the Query Stream for Streaming an array of data
	 * @param query
	 * @return
	 */
	protected QueryStream<VO, MODEL, DAO> getStream(ASTNode root, VoStreamCallback<VO, MODEL, DAO> callback){
		QueryStream<VO, MODEL, DAO> stream = new QueryStream<VO, MODEL, DAO>(dao, this, root, callback);
		//Ensure its ready
		stream.setupQuery();
		return stream;
	}
	
	/**
	 * Get a Stream that is more like a result set with a count
	 * @param query
	 * @return
	 */
	protected PageQueryStream<VO, MODEL, DAO> getPageStream(ASTNode root, User user){
		return getPageStream(root, new VoStreamCallback<VO, MODEL, DAO>(this, user));
	}

	/**
	 * Get a Stream that is more like a result set with a count
	 * @param query
	 * @return
	 */
	protected PageQueryStream<VO, MODEL, DAO> getPageStream(ASTNode node, VoStreamCallback<VO, MODEL, DAO> callback){
		PageQueryStream<VO, MODEL, DAO> stream = new PageQueryStream<VO, MODEL, DAO>(dao, this, node, callback);
		//Ensure its ready
		stream.setupQuery();
		return stream;
	}
	
	
	@ApiOperation(
			value = "Get Explaination For Query",
			notes = "What is Query-able on this model"
			)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message = "Ok"),
	@ApiResponse(code = 403, message = "User does not have access")
	})
	@RequestMapping(method = RequestMethod.GET, value = "/explain-query")
    public ResponseEntity<TableModel> getTableModel(HttpServletRequest request) {
        RestProcessResult<TableModel> result = new RestProcessResult<TableModel>(HttpStatus.OK);
        return result.createResponseEntity(this.getQueryAttributeModel());
    }
	
	/**
	 * Get the Table Model
	 * @return
	 */
	protected TableModel getQueryAttributeModel(){
		TableModel model = this.dao.getTableModel();
	
		
		//Add in our mappings
		Iterator<String> it = this.modelMap.keySet().iterator();
		while(it.hasNext()){
			String modelMember = it.next();
			String mappedTo = this.modelMap.get(modelMember);
			for(QueryAttribute attribute : model.getAttributes()){
				if(attribute.getColumnName().equals(mappedTo)){
					attribute.addAlias(modelMember);
				}
			}
		}
		return model;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.serotonin.m2m2.web.mvc.rest.swagger.IMangoVoRestController#getModelMap()
	 */
	@Override
	public Map<String,String> getModelMap(){
		return this.modelMap;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.serotonin.m2m2.web.mvc.rest.swagger.IMangoVoRestController#getAppenders()
	 */
	@Override
	public Map<String, SQLColumnQueryAppender> getAppenders(){
		return this.appenders;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.serotonin.m2m2.web.mvc.rest.swagger.IMangoVoRestController#createModel(com.serotonin.m2m2.vo.AbstractBasicVO)
	 */
	@Override
	public abstract MODEL createModel(VO vo);
	
}