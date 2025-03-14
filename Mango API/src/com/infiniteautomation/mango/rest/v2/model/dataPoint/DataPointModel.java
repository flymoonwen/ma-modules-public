/*
 * Copyright (C) 2017 Infinite Automation Software. All rights reserved.
 */
package com.infiniteautomation.mango.rest.v2.model.dataPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.util.UnitUtil;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.web.mvc.rest.v1.mapping.SuperclassModelDeserializer;
import com.serotonin.m2m2.web.mvc.rest.v1.model.dataPoint.PointLocatorModel;
import com.serotonin.m2m2.web.mvc.rest.v1.model.dataPoint.TimePeriodModel;
import com.serotonin.m2m2.web.mvc.rest.v1.model.dataPoint.chartRenderer.BaseChartRendererModel;
import com.serotonin.m2m2.web.mvc.rest.v1.model.dataPoint.chartRenderer.ChartRendererFactory;
import com.serotonin.m2m2.web.mvc.rest.v1.model.dataPoint.textRenderer.BaseTextRendererModel;
import com.serotonin.m2m2.web.mvc.rest.v1.model.dataPoint.textRenderer.TextRendererFactory;

/**
 * Data point REST model v2
 *
 * @author Jared Wiltshire
 *
 */
public class DataPointModel {

    Integer id;
    String xid;
    String name;
    Boolean enabled;

    String deviceName;
    String readPermission;
    String setPermission;
    Integer pointFolderId;
    Boolean purgeOverride;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    TimePeriodModel purgePeriod;
    String unit;
    Boolean useIntegralUnit;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String integralUnit;
    Boolean useRenderedUnit;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String renderedUnit;
    PointLocatorModel<?> pointLocator;
    String chartColour;
    String plotType;
    LoggingPropertiesModel loggingProperties;
    @JsonDeserialize(using = SuperclassModelDeserializer.class)
    BaseTextRendererModel<?> textRenderer;
    @JsonDeserialize(using = SuperclassModelDeserializer.class)
    BaseChartRendererModel<?> chartRenderer;
    String rollup;
    String simplifyType;
    Double simplifyTolerance;
    Integer simplifyTarget;
    Boolean preventSetExtremeValues;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Double setExtremeLowLimit;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Double setExtremeHighLimit;

    /**
     * Used to indicate that the templateXid was explicitly set to null in the JSON as opposed to
     * initialized to null by Java.
     */
    @JsonIgnore
    boolean templateXidWasSet = false;
    String templateXid;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String templateName;

    Integer dataSourceId;
    String dataSourceXid;
    String dataSourceName;
    String dataSourceTypeName;
    Set<String> dataSourceEditRoles;

    boolean mergeTags = false;
    Map<String, String> tags;

    public DataPointModel() {
    }

    /**
     * For writing out JSON
     * @param point
     */
    public DataPointModel(DataPointVO point) {
        this.id = point.getId();
        this.xid = point.getXid();
        this.name = point.getName();
        this.enabled = point.isEnabled();

        this.deviceName = point.getDeviceName();
        this.readPermission = point.getReadPermission();
        this.setPermission = point.getSetPermission();
        this.pointFolderId = point.getPointFolderId();
        this.purgeOverride = point.isPurgeOverride();
        if (this.purgeOverride) {
            this.purgePeriod = new TimePeriodModel(point.getPurgePeriod(), point.getPurgeType());
        }
        this.unit = UnitUtil.formatLocal(point.getUnit());
        this.useIntegralUnit = point.isUseIntegralUnit();
        if (this.useIntegralUnit) {
            this.integralUnit = UnitUtil.formatLocal(point.getIntegralUnit());
        }
        this.useRenderedUnit = point.isUseRenderedUnit();
        if (this.useRenderedUnit) {
            this.renderedUnit = UnitUtil.formatLocal(point.getRenderedUnit());
        }
        //The Point Locator will be set by the RestModelMapper after
        this.chartColour = point.getChartColour();
        this.plotType = DataPointVO.PLOT_TYPE_CODES.getCode(point.getPlotType());
        this.tags = point.getTags();

        this.loggingProperties = new LoggingPropertiesModel(point);
        this.textRenderer = TextRendererFactory.createModel(point);
        this.chartRenderer = ChartRendererFactory.createModel(point);

        this.dataSourceId = point.getDataSourceId();
        this.dataSourceXid = point.getDataSourceXid();
        this.dataSourceName = point.getDataSourceName();
        this.dataSourceTypeName = point.getDataSourceTypeName();

        this.templateXid = point.getTemplateXid();
        this.templateName = point.getTemplateName();

        this.rollup = Common.ROLLUP_CODES.getCode(point.getRollup());
        this.simplifyType = DataPointVO.SIMPLIFY_TYPE_CODES.getCode(point.getSimplifyType());
        this.simplifyTolerance = point.getSimplifyTolerance();
        this.simplifyTarget = point.getSimplifyTarget();

        this.preventSetExtremeValues = point.isPreventSetExtremeValues();
        if (this.preventSetExtremeValues) {
            this.setExtremeLowLimit = point.getSetExtremeLowLimit();
            this.setExtremeHighLimit = point.getSetExtremeHighLimit();
        }
        this.dataSourceEditRoles = point.getDataSourceEditRoles();
    }

    public void copyPropertiesTo(DataPointVO point) {
        if (xid != null) {
            point.setXid(xid);
        }
        if (name != null) {
            point.setName(name);
        }
        if (enabled != null) {
            point.setEnabled(enabled);
        }
        if (deviceName != null) {
            point.setDeviceName(deviceName);
        }
        if (readPermission != null) {
            point.setReadPermission(readPermission);
        }
        if (setPermission != null) {
            point.setSetPermission(setPermission);
        }
        if (pointFolderId != null) {
            point.setPointFolderId(pointFolderId);
        }
        if (purgeOverride != null) {
            point.setPurgeOverride(purgeOverride);
            //Ensure that a purge period must be supplied
            if(purgeOverride) {
                point.setPurgePeriod(-1);
                point.setPurgeType(-1);
            }

        }
        if (purgePeriod != null) {
            point.setPurgePeriod(purgePeriod.getPeriods());
            point.setPurgeType(Common.TIME_PERIOD_CODES.getId(purgePeriod.getPeriodType()));
        }
        if (unit != null) {
            try {
                point.setUnit(UnitUtil.parseLocal(unit));
            } catch(IllegalArgumentException e) {
                point.setUnit(null); //Signal to use the unit string
                point.setUnitString(unit);
            }
        }
        if (useIntegralUnit != null) {
            point.setUseIntegralUnit(useIntegralUnit);
        }
        if (integralUnit != null) {
            try {
                point.setIntegralUnit(UnitUtil.parseLocal(integralUnit));
            } catch(IllegalArgumentException e) {
                point.setIntegralUnit(null); //Signal to use the unit string
                point.setIntegralUnitString(integralUnit);
            }
        }
        if (useRenderedUnit != null) {
            point.setUseRenderedUnit(useRenderedUnit);
        }
        if (this.renderedUnit != null) {
            try {
                point.setRenderedUnit(UnitUtil.parseLocal(renderedUnit));
            } catch(IllegalArgumentException e) {
                point.setRenderedUnit(null); //Signal to use the unit string
                point.setRenderedUnitString(renderedUnit);
            }
        }
        if (chartColour != null) {
            point.setChartColour(chartColour);
        }
        if (plotType != null) {
            point.setPlotType(DataPointVO.PLOT_TYPE_CODES.getId(plotType));
        }
        if (this.tags != null) {
            Map<String, String> existingTags = point.getTags();
            if (!this.mergeTags || existingTags == null) {
                // existingTags is only null if someone tried to use mergeTags when creating a data point
                point.setTags(this.tags);
            } else {
                Map<String, String> mergedTags = new HashMap<>(existingTags);
                for (Entry<String, String> entry : this.tags.entrySet()) {
                    String tagKey = entry.getKey();
                    String tagValue = entry.getValue();

                    if (tagValue == null) {
                        mergedTags.remove(tagKey);
                    } else {
                        mergedTags.put(tagKey, tagValue);
                    }
                }
                point.setTags(mergedTags);
            }
        }
        if (this.loggingProperties != null) {
            loggingProperties.copyPropertiesTo(point);
        }
        if (this.textRenderer != null) {
            TextRendererFactory.updateDataPoint(point, textRenderer);
        }
        if (this.chartRenderer != null) {
            ChartRendererFactory.updateDataPoint(point, chartRenderer);
        }
        if (pointLocator != null) {
            point.setPointLocator(pointLocator.getData());
        }

        // template XID is allowed to be null, if it is then clear the template properties on the point
        if (templateXidWasSet && templateXid == null) {
            point.setTemplateId(null);
            point.setTemplateXid(null);
            point.setTemplateName(null);
        }

        if (this.rollup != null) {
            point.setRollup(Common.ROLLUP_CODES.getId(this.rollup));
        }
        if(this.simplifyType != null) {
            point.setSimplifyType(DataPointVO.SIMPLIFY_TYPE_CODES.getId(this.simplifyType));
        }
        if(this.simplifyTolerance != null) {
            point.setSimplifyTolerance(simplifyTolerance);
        }
        if(this.simplifyTarget != null) {
            point.setSimplifyTarget(simplifyTarget);
        }
        if (this.preventSetExtremeValues != null) {
            point.setPreventSetExtremeValues(this.preventSetExtremeValues);
        }
        if (this.setExtremeLowLimit != null) {
            point.setSetExtremeLowLimit(this.setExtremeLowLimit);
        }
        if (this.setExtremeHighLimit != null) {
            point.setSetExtremeHighLimit(this.setExtremeHighLimit);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getReadPermission() {
        return readPermission;
    }

    public void setReadPermission(String readPermission) {
        this.readPermission = readPermission;
    }

    public String getSetPermission() {
        return setPermission;
    }

    public void setSetPermission(String setPermission) {
        this.setPermission = setPermission;
    }

    public Integer getPointFolderId() {
        return pointFolderId;
    }

    public void setPointFolderId(Integer pointFolderId) {
        this.pointFolderId = pointFolderId;
    }

    public Boolean getPurgeOverride() {
        return purgeOverride;
    }

    public void setPurgeOverride(Boolean purgeOverride) {
        this.purgeOverride = purgeOverride;
    }

    public TimePeriodModel getPurgePeriod() {
        return purgePeriod;
    }

    public void setPurgePeriod(TimePeriodModel purgePeriod) {
        this.purgePeriod = purgePeriod;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getUseIntegralUnit() {
        return useIntegralUnit;
    }

    public void setUseIntegralUnit(Boolean useIntegralUnit) {
        this.useIntegralUnit = useIntegralUnit;
    }

    public String getIntegralUnit() {
        return integralUnit;
    }

    public void setIntegralUnit(String integralUnit) {
        this.integralUnit = integralUnit;
    }

    public Boolean getUseRenderedUnit() {
        return useRenderedUnit;
    }

    public void setUseRenderedUnit(Boolean useRenderedUnit) {
        this.useRenderedUnit = useRenderedUnit;
    }

    public String getRenderedUnit() {
        return renderedUnit;
    }

    public void setRenderedUnit(String renderedUnit) {
        this.renderedUnit = renderedUnit;
    }

    public PointLocatorModel<?> getPointLocator() {
        return pointLocator;
    }

    public void setPointLocator(PointLocatorModel<?> pointLocator) {
        this.pointLocator = pointLocator;
    }

    public String getChartColour() {
        return chartColour;
    }

    public void setChartColour(String chartColour) {
        this.chartColour = chartColour;
    }

    public String getPlotType() {
        return plotType;
    }

    public void setPlotType(String plotType) {
        this.plotType = plotType;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public LoggingPropertiesModel getLoggingProperties() {
        return loggingProperties;
    }

    public void setLoggingProperties(LoggingPropertiesModel loggingProperties) {
        this.loggingProperties = loggingProperties;
    }

    public BaseTextRendererModel<?> getTextRenderer() {
        return textRenderer;
    }

    public void setTextRenderer(BaseTextRendererModel<?> textRenderer) {
        this.textRenderer = textRenderer;
    }

    public BaseChartRendererModel<?> getChartRenderer() {
        return chartRenderer;
    }

    public void setChartRenderer(BaseChartRendererModel<?> chartRenderer) {
        this.chartRenderer = chartRenderer;
    }

    public String getTemplateXid() {
        return templateXid;
    }

    public void setTemplateXid(String templateXid) {
        this.templateXid = templateXid;
        this.templateXidWasSet = true;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Integer getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Integer dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getDataSourceXid() {
        return dataSourceXid;
    }

    public void setDataSourceXid(String dataSourceXid) {
        this.dataSourceXid = dataSourceXid;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDataSourceTypeName() {
        return dataSourceTypeName;
    }

    public void setDataSourceTypeName(String dataSourceTypeName) {
        this.dataSourceTypeName = dataSourceTypeName;
    }

    public boolean isTemplateXidWasSet() {
        return templateXidWasSet;
    }

    public String getRollup() {
        return rollup;
    }

    public void setRollup(String rollup) {
        this.rollup = rollup;
    }

    protected void setMergeTags(boolean mergeTags) {
        this.mergeTags = mergeTags;
    }

    public String getSimplifyType() {
        return simplifyType;
    }

    public void setSimplifyType(String simplifyType) {
        this.simplifyType = simplifyType;
    }

    public Double getSimplifyTolerance() {
        return simplifyTolerance;
    }

    public void setSimplifyTolerance(Double simplifyTolerance) {
        this.simplifyTolerance = simplifyTolerance;
    }

    public Integer getSimplifyTarget() {
        return simplifyTarget;
    }

    public void setSimplifyTarget(Integer simplifyTarget) {
        this.simplifyTarget = simplifyTarget;
    }

    public Boolean getPreventSetExtremeValues() {
        return preventSetExtremeValues;
    }

    public void setPreventSetExtremeValues(Boolean preventSetExtremeValues) {
        this.preventSetExtremeValues = preventSetExtremeValues;
    }

    public Double getSetExtremeLowLimit() {
        return setExtremeLowLimit;
    }

    public void setSetExtremeLowLimit(Double setExtremeLowLimit) {
        this.setExtremeLowLimit = setExtremeLowLimit;
    }

    public Double getSetExtremeHighLimit() {
        return setExtremeHighLimit;
    }

    public void setSetExtremeHighLimit(Double setExtremeHighLimit) {
        this.setExtremeHighLimit = setExtremeHighLimit;
    }
    
    public Set<String> getDataSourceEditRoles() {
        return dataSourceEditRoles;
    }

    public void setDataSourceEditRoles(Set<String> dataSourceEditRoles) {
        //No-op
    }
}
