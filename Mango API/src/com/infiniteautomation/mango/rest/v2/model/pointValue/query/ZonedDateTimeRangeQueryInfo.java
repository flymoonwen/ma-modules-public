/**
 * @copyright 2017 {@link http://infiniteautomation.com|Infinite Automation Systems, Inc.} All
 *            rights reserved.
 * @author Terry Packer
 */
package com.infiniteautomation.mango.rest.v2.model.pointValue.query;

import java.time.Instant;
import java.time.ZonedDateTime;

import com.infiniteautomation.mango.rest.v2.exception.BadRequestException;
import com.infiniteautomation.mango.rest.v2.model.pointValue.PointValueField;
import com.infiniteautomation.mango.rest.v2.model.time.TimePeriod;
import com.infiniteautomation.mango.rest.v2.model.time.TimePeriodType;
import com.infiniteautomation.mango.util.datetime.ExpandTimePeriodAdjuster;
import com.infiniteautomation.mango.util.datetime.TruncateTimePeriodAdjuster;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.web.mvc.rest.v1.model.time.RollupEnum;

/**
 *
 * @author Terry Packer
 */
public class ZonedDateTimeRangeQueryInfo extends LatestQueryInfo {

    protected ZonedDateTime to;
    protected final RollupEnum rollup;
    protected final TimePeriod timePeriod;
    protected final boolean bookend; //Do we want virtual values at the to/from time if they don't already exist?


    /**
     * This class with use an optional timzone to ensure that the to/from dates are correct and
     * attempt to determine the timezone to use for rendering and rollup edges using the following
     * rules:
     * 
     * if 'timezone' is supplied use that for all timezones if 'timezone' is not supplied the rules
     * are applied in this order: use timezone of from if not null use timezone of to if not null
     * use server timezone 
     * 
     * @param from
     * @param to
     * @param dateTimeFormat
     * @param timezone
     * @param rollup
     * @param timePeriod
     * @param limit
     * @param bookend
     * @param multiplePointsPerArray
     * @param singleArray
     * @param useCache
     * @param simplifyTolerance
     * @param simplifyTarget
     * @param boolean truncate - Truncate the start and end dates to be round numbers
     * @param extraFields - Extra information about each data point
     */
    public ZonedDateTimeRangeQueryInfo(ZonedDateTime from, ZonedDateTime to,
            String dateTimeFormat, String timezone, RollupEnum rollup, TimePeriod timePeriod,
            Integer limit, boolean bookend, boolean multiplePointsPerArray, 
            boolean singleArray, PointValueTimeCacheControl useCache, 
            Double simplifyTolerance, Integer simplifyTarget, boolean truncate, PointValueField[] fields) {
        super(from, dateTimeFormat, timezone,
                limit, multiplePointsPerArray, 
                singleArray, useCache, simplifyTolerance, simplifyTarget, fields);


        // Determine the timezone to use based on the incoming dates
        if (timezone == null) {
            if (to != null)
                this.zoneId = to.getZone();
        } 

        // Set the timezone on the from and to dates
        long current = Common.timer.currentTimeMillis();
        if (to != null)
            this.to = to.withZoneSameInstant(zoneId);
        else
            this.to = ZonedDateTime.ofInstant(Instant.ofEpochMilli(current), zoneId);

        // Validate time
        if (!this.to.isAfter(this.from))
            throw new BadRequestException(new TranslatableMessage("rest.validate.timeRange.invalid"));
        
        this.rollup = rollup;
        this.timePeriod = timePeriod;
        this.bookend = bookend;
        
        if(truncate)
            setupDates();
    }

    /**
     * Round off the period for rollups
     */
    protected void setupDates() {
        // Round off the period if we are using periodic rollup
        if (this.timePeriod != null) {
            TruncateTimePeriodAdjuster adj = new TruncateTimePeriodAdjuster(
                    TimePeriodType.convertFrom(this.timePeriod.getType()),
                    this.timePeriod.getPeriods());
            from = from.with(adj);
            ExpandTimePeriodAdjuster expander = new ExpandTimePeriodAdjuster(from,
                    TimePeriodType.convertFrom(this.timePeriod.getType()),
                    this.timePeriod.getPeriods());
            to = to.with(expander);
        }
    }

    public long getToMillis() {
        return to.toInstant().toEpochMilli();
    }

    public ZonedDateTime getTo() {
        return to;
    }
    
    @Override
    public RollupEnum getRollup() {
        return rollup;
    }

    public TimePeriod getTimePeriod() {
        return timePeriod;
    }
    public boolean isBookend() {
        return bookend;
    }
}
