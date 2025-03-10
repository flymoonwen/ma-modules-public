/*
    Copyright (C) 2014 Infinite Automation Systems Inc. All rights reserved.
    @author Matthew Lohbihler
 */
package com.serotonin.m2m2.envcan;

import java.util.Date;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.db.dao.SystemSettingsDao;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;

public class EnvCanEditDwr extends DataSourceEditDwr {
    @DwrPermission(custom = SystemSettingsDao.PERMISSION_DATASOURCE)
    public ProcessResult saveEnvCanDataSource(BasicDataSourceVO basic, int stationId, Date dataStartTime) {
        EnvCanDataSourceVO ds = (EnvCanDataSourceVO) Common.getHttpUser().getEditDataSource();

        setBasicProps(ds, basic);
        ds.setStationId(stationId);
        ds.setDataStartTime(dataStartTime.getTime());
        ds.setUpdatePeriods(1);
        ds.setUpdatePeriodType(Common.TimePeriods.HOURS);
        return tryDataSourceSave(ds);
    }

    @DwrPermission(custom = SystemSettingsDao.PERMISSION_DATASOURCE)
    public ProcessResult saveEnvCanPointLocator(int id, String xid, String name, EnvCanPointLocatorVO locator) {
        return validatePoint(id, xid, name, locator);
    }
}
