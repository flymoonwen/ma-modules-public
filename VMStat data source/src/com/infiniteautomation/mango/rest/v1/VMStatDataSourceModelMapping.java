/**
 * Copyright (C) 2019  Infinite Automation Software. All rights reserved.
 */
package com.infiniteautomation.mango.rest.v1;

import org.springframework.stereotype.Component;

import com.serotonin.m2m2.vmstat.VMStatDataSourceDefinition;
import com.serotonin.m2m2.vmstat.VMStatDataSourceModel;
import com.serotonin.m2m2.vmstat.VMStatDataSourceVO;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.web.mvc.rest.v1.model.RestModelJacksonMapping;
import com.serotonin.m2m2.web.mvc.rest.v1.model.RestModelMapper;

/**
 * @author Terry Packer
 *
 */
@Component
public class VMStatDataSourceModelMapping implements RestModelJacksonMapping<VMStatDataSourceVO, VMStatDataSourceModel> {

    @Override
    public Class<? extends VMStatDataSourceVO> fromClass() {
        return VMStatDataSourceVO.class;
    }

    @Override
    public Class<? extends VMStatDataSourceModel> toClass() {
        return VMStatDataSourceModel.class;
    }

    @Override
    public VMStatDataSourceModel map(Object from, User user, RestModelMapper mapper) {
        return new VMStatDataSourceModel((VMStatDataSourceVO)from);
    }

    @Override
    public String getTypeName() {
        return VMStatDataSourceDefinition.DATA_SOURCE_TYPE;
    }

}
