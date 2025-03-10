/**
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
 * @author Terry Packer
 */
package com.serotonin.m2m2.web.mvc.rest.v1.model.dataSource;

import java.io.IOException;

import com.serotonin.m2m2.db.dao.DataSourceDao;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.permission.PermissionException;
import com.serotonin.m2m2.vo.permission.Permissions;
import com.serotonin.m2m2.web.mvc.rest.v1.MangoVoRestController;
import com.serotonin.m2m2.web.mvc.rest.v1.model.VoStreamCallback;

/**
 * Class to discard any data sources that the user does not have access to during a query response.
 * 
 * @author Terry Packer
 *
 */
public class DataSourceStreamCallback<T extends DataSourceVO<T>> extends VoStreamCallback<T, AbstractDataSourceModel<T>, DataSourceDao<T>>{

	/**
	 * @param controller
	 */
	public DataSourceStreamCallback(
			MangoVoRestController<T, AbstractDataSourceModel<T>, DataSourceDao<T>> controller,
			User user) {
		super(controller, user);
	}

	/**
	 * Do the work of writing the VO
	 * @param vo
	 * @throws IOException
	 */
	@Override
	protected void writeJson(T vo) throws IOException{
		
		try{
    		if(Permissions.hasDataSourcePermission(user, vo)){
    			AbstractDataSourceModel<?> model = this.controller.createModel(vo, user);
    			this.jgen.writeObject(model);
    		}
    	}catch(PermissionException e){
    		//Munched
    	}
		
		
	}
	@Override
	protected void writeCsv(T vo) throws IOException{
		try{
    		if(Permissions.hasDataSourcePermission(user, vo)){
    			AbstractDataSourceModel<?> model = this.controller.createModel(vo, user);
    			this.csvWriter.writeNext(model);
    		}
    	}catch(PermissionException e){
    		//Munched
    	}
		
	}
	
}
