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
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;

/**
 * 
 */

public class BudgetGraduateAssistantRate extends PersistableBusinessObjectBase implements Comparable {
    private static final long serialVersionUID = -5360306689257897228L;

    private String documentNumber;
    private String campusCode;
    private KualiDecimal campusMaximumPeriod1Rate;
    private KualiDecimal campusMaximumPeriod2Rate;
    private KualiDecimal campusMaximumPeriod3Rate;
    private KualiDecimal campusMaximumPeriod4Rate;
    private KualiDecimal campusMaximumPeriod5Rate;
    private KualiDecimal campusMaximumPeriod6Rate;
    private Timestamp lastUpdateTimestamp;

    private GraduateAssistantRate graduateAssistantRate;

    /**
     * Default no-arg constructor.
     */
    public BudgetGraduateAssistantRate() {
        super();
        graduateAssistantRate = new GraduateAssistantRate();
    }
    
    public BudgetGraduateAssistantRate(String documentNumber, String campusCode) {
        this();
        this.documentNumber = documentNumber;
        this.campusCode = campusCode;
    }

    public BudgetGraduateAssistantRate(String documentNumber, String campusCode, KualiDecimal campusMaximumPeriod1Rate, KualiDecimal campusMaximumPeriod2Rate, KualiDecimal campusMaximumPeriod3Rate, KualiDecimal campusMaximumPeriod4Rate, KualiDecimal campusMaximumPeriod5Rate, KualiDecimal campusMaximumPeriod6Rate, GraduateAssistantRate graduateAssistantRate) {
        this(documentNumber, campusCode, campusMaximumPeriod1Rate, campusMaximumPeriod2Rate, campusMaximumPeriod3Rate, campusMaximumPeriod4Rate, campusMaximumPeriod5Rate, campusMaximumPeriod3Rate, graduateAssistantRate, null, null);
    }

    public BudgetGraduateAssistantRate(String documentNumber, String campusCode, KualiDecimal campusMaximumPeriod1Rate, KualiDecimal campusMaximumPeriod2Rate, KualiDecimal campusMaximumPeriod3Rate, KualiDecimal campusMaximumPeriod4Rate, KualiDecimal campusMaximumPeriod5Rate, KualiDecimal campusMaximumPeriod6Rate, GraduateAssistantRate graduateAssistantRate, String objectId, Long versionNumber) {
        super();
        this.documentNumber = documentNumber;
        this.campusCode = campusCode;
        this.campusMaximumPeriod1Rate = campusMaximumPeriod1Rate;
        this.campusMaximumPeriod2Rate = campusMaximumPeriod2Rate;
        this.campusMaximumPeriod3Rate = campusMaximumPeriod3Rate;
        this.campusMaximumPeriod4Rate = campusMaximumPeriod4Rate;
        this.campusMaximumPeriod5Rate = campusMaximumPeriod5Rate;
        this.campusMaximumPeriod6Rate = campusMaximumPeriod6Rate;
        this.graduateAssistantRate = graduateAssistantRate;
        setObjectId(objectId);
        setVersionNumber(versionNumber);
    }

    
    public BudgetGraduateAssistantRate(String documentNumber, GraduateAssistantRate gradAssistantRate) {
        this(documentNumber, gradAssistantRate.getCampusCode(), gradAssistantRate.getCampusMaximumPeriod1Rate(), gradAssistantRate.getCampusMaximumPeriod2Rate(), gradAssistantRate.getCampusMaximumPeriod3Rate(), gradAssistantRate.getCampusMaximumPeriod4Rate(), gradAssistantRate.getCampusMaximumPeriod5Rate(), gradAssistantRate.getCampusMaximumPeriod6Rate(), gradAssistantRate);
    }


    public String getCampusCode() {
        return campusCode;
    }

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

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    protected String toStringBuilder(LinkedHashMap fieldValues) {
        // TODO Auto-generated method stub
        return super.toStringBuilder(fieldValues);
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("campusCode", this.campusCode);
        return m;
    }

    /**
     * @return Returns the graduateAssistantRate.
     */
    public GraduateAssistantRate getGraduateAssistantRate() {
        return graduateAssistantRate;
    }

    /**
     * @param graduateAssistantRate The graduateAssistantRate to set.
     */
    public void setGraduateAssistantRate(GraduateAssistantRate graduateAssistantRate) {
        this.graduateAssistantRate = graduateAssistantRate;
    }

    /**
     * @return Returns the lastUpdateTimestamp.
     */
    public Timestamp getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    /**
     * @param lastUpdateTimestamp The lastUpdateTimestamp to set.
     */
    public void setLastUpdateTimestamp(Timestamp lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        return this.getCampusCode().compareTo(((BudgetGraduateAssistantRate) o).getCampusCode());
    }

    
    /*
     * (non-Javadoc)
     * Doesn't compare timestamps 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equals = true;

        if (ObjectUtils.isNotNull(obj) && obj instanceof BudgetGraduateAssistantRate) {
            BudgetGraduateAssistantRate objCompare = (BudgetGraduateAssistantRate) obj;
            equals &= this.documentNumber.equals(objCompare.getDocumentNumber());
            equals &= this.campusCode.equals(objCompare.getCampusCode());
            
            if (this.campusMaximumPeriod1Rate == null && objCompare.getCampusMaximumPeriod1Rate() == null) {
            }
            else {
                equals &= this.campusMaximumPeriod1Rate != null && objCompare.getCampusMaximumPeriod1Rate() != null && this.campusMaximumPeriod1Rate.equals(objCompare.getCampusMaximumPeriod1Rate());
            }
            
            if (this.campusMaximumPeriod2Rate == null && objCompare.getCampusMaximumPeriod2Rate() == null) {
            }
            else {
                equals &= this.campusMaximumPeriod2Rate != null && objCompare.getCampusMaximumPeriod2Rate() != null && this.campusMaximumPeriod2Rate.equals(objCompare.getCampusMaximumPeriod2Rate());
            }
            
            if (this.campusMaximumPeriod3Rate == null && objCompare.getCampusMaximumPeriod3Rate() == null) {
            }
            else {
                equals &= this.campusMaximumPeriod3Rate != null && objCompare.getCampusMaximumPeriod3Rate() != null && this.campusMaximumPeriod3Rate.equals(objCompare.getCampusMaximumPeriod3Rate());
            }
            
            if (this.campusMaximumPeriod3Rate == null && objCompare.getCampusMaximumPeriod3Rate() == null) {
            }
            else {
                equals &= this.campusMaximumPeriod4Rate != null && objCompare.getCampusMaximumPeriod4Rate() != null && this.campusMaximumPeriod4Rate.equals(objCompare.getCampusMaximumPeriod4Rate());
            }
            
            if (this.campusMaximumPeriod5Rate == null && objCompare.getCampusMaximumPeriod5Rate() == null) {
            }
            else {
                equals &= this.campusMaximumPeriod5Rate != null && objCompare.getCampusMaximumPeriod5Rate() != null && this.campusMaximumPeriod5Rate.equals(objCompare.getCampusMaximumPeriod5Rate());
            }
            
            if (this.campusMaximumPeriod6Rate == null && objCompare.getCampusMaximumPeriod6Rate() == null) {
            }
            else {
                equals &= this.campusMaximumPeriod6Rate != null && objCompare.getCampusMaximumPeriod6Rate() != null && this.campusMaximumPeriod6Rate.equals(objCompare.getCampusMaximumPeriod6Rate());
            }
        }

        return equals;
    }
}
