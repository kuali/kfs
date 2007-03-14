/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.kra.budget.bo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.ojb.broker.PersistenceBroker;
import org.kuali.core.bo.Campus;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.util.SpringServiceLocator;

/**
 * 
 */

public class GraduateAssistantRate extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 8803703155949499268L;
    private String campusCode;
    private boolean active;
    private KualiDecimal campusMaximumPeriod1Rate;
    private KualiDecimal campusMaximumPeriod2Rate;
    private KualiDecimal campusMaximumPeriod3Rate;
    private KualiDecimal campusMaximumPeriod4Rate;
    private KualiDecimal campusMaximumPeriod5Rate;
    private KualiDecimal campusMaximumPeriod6Rate;
    private Timestamp lastUpdateTimestamp;
    private Campus campus;

    /**
     * Default no-arg constructor.
     */
    public GraduateAssistantRate() {
        super();
    }

    /**
     * Default no-arg constructor.
     */
    public GraduateAssistantRate(String campusCode) {
        this();
        this.campusCode = campusCode;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("campusCode", this.campusCode);
        return m;
    }

    /**
     * 
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * 
     * @return
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * 
     * @param campusCode
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public KualiDecimal getCampusMaximumPeriod1Rate() {
        return campusMaximumPeriod1Rate;
    }

    public void setCampusMaximumPeriod1Rate(KualiDecimal campusMaximumPeriod1Rate) {
        this.campusMaximumPeriod1Rate = campusMaximumPeriod1Rate;
    }

    public KualiDecimal getCampusMaximumPeriod2Rate() {
        return campusMaximumPeriod2Rate;
    }

    public void setCampusMaximumPeriod2Rate(KualiDecimal campusMaximumPeriod2Rate) {
        this.campusMaximumPeriod2Rate = campusMaximumPeriod2Rate;
    }

    public KualiDecimal getCampusMaximumPeriod3Rate() {
        return campusMaximumPeriod3Rate;
    }

    public void setCampusMaximumPeriod3Rate(KualiDecimal campusMaximumPeriod3Rate) {
        this.campusMaximumPeriod3Rate = campusMaximumPeriod3Rate;
    }

    public KualiDecimal getCampusMaximumPeriod4Rate() {
        return campusMaximumPeriod4Rate;
    }

    public void setCampusMaximumPeriod4Rate(KualiDecimal campusMaximumPeriod4Rate) {
        this.campusMaximumPeriod4Rate = campusMaximumPeriod4Rate;
    }

    public KualiDecimal getCampusMaximumPeriod5Rate() {
        return campusMaximumPeriod5Rate;
    }

    public void setCampusMaximumPeriod5Rate(KualiDecimal campusMaximumPeriod5Rate) {
        this.campusMaximumPeriod5Rate = campusMaximumPeriod5Rate;
    }

    public KualiDecimal getCampusMaximumPeriod6Rate() {
        return campusMaximumPeriod6Rate;
    }

    public void setCampusMaximumPeriod6Rate(KualiDecimal campusMaximumPeriod6Rate) {
        this.campusMaximumPeriod6Rate = campusMaximumPeriod6Rate;
    }

    /**
     * @return Returns the campus.
     */
    public Campus getCampus() {
        return campus;
    }

    /**
     * @param campus The campus to set.
     */
    public void setCampus(Campus campus) {
        this.campus = campus;
    }


    /*
     * a "getter" function that treats the Campus Maximum Periods Rates attributes as if they were in an array. @param i - a 1 based
     * index for the academic year subdivision @returns the Campus Maximum Periods Rate for the given academic year subdivision
     */
    public KualiDecimal getCampusMaximumPeriodRate(int i) {
        switch (i) {
            case 1:
                return getCampusMaximumPeriod1Rate();
            case 2:
                return getCampusMaximumPeriod2Rate();
            case 3:
                return getCampusMaximumPeriod3Rate();
            case 4:
                return getCampusMaximumPeriod4Rate();
            case 5:
                return getCampusMaximumPeriod5Rate();
            case 6:
                return getCampusMaximumPeriod6Rate();
            default:
                throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    /*
     * a "setter" function that treats the Campus Maximum Periods Rates attributes as if they were in an array. @param i - a 1 based
     * index for the academic year subdivision @param aRate - the rate to set
     */
    public void setCampusMaximumPeriodRate(int i, KualiDecimal aRate) {
        switch (i) {
            case 1:
                setCampusMaximumPeriod1Rate(aRate);
                break;
            case 2:
                setCampusMaximumPeriod2Rate(aRate);
                break;
            case 3:
                setCampusMaximumPeriod3Rate(aRate);
                break;
            case 4:
                setCampusMaximumPeriod4Rate(aRate);
                break;
            case 5:
                setCampusMaximumPeriod5Rate(aRate);
                break;
            case 6:
                setCampusMaximumPeriod6Rate(aRate);
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    /**
     * 
     * @return
     */
    public Timestamp getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    /**
     * 
     * @param lastUpdateTimestamp
     */
    public void setLastUpdateTimestamp(Timestamp lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }
    
    public void beforeInsert(PersistenceBroker persistenceBroker) {
        super.beforeInsert(persistenceBroker);
        this.lastUpdateTimestamp = SpringServiceLocator.getDateTimeService().getCurrentTimestamp();
    }

    public void beforeUpdate(PersistenceBroker persistenceBroker) {
        super.beforeUpdate(persistenceBroker);
        this.lastUpdateTimestamp = SpringServiceLocator.getDateTimeService().getCurrentTimestamp();
    }
}
