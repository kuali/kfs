package org.kuali.module.kra.budget.bo;

/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class BudgetGraduateAssistantRate extends BusinessObjectBase implements Comparable {
    private static final long serialVersionUID = -5360306689257897228L;

    /**
     * Default no-arg constructor.
     */
    public BudgetGraduateAssistantRate() {
        super();
        graduateAssistantRate = new GraduateAssistantRate();
    }

    public BudgetGraduateAssistantRate(String documentHeaderId, String campusCode, KualiDecimal campusMaximumPeriod1Rate, KualiDecimal campusMaximumPeriod2Rate, KualiDecimal campusMaximumPeriod3Rate, KualiDecimal campusMaximumPeriod4Rate, KualiDecimal campusMaximumPeriod5Rate, KualiDecimal campusMaximumPeriod6Rate, GraduateAssistantRate graduateAssistantRate) {
        this(documentHeaderId, campusCode, campusMaximumPeriod1Rate, campusMaximumPeriod2Rate, campusMaximumPeriod3Rate, campusMaximumPeriod4Rate, campusMaximumPeriod5Rate, campusMaximumPeriod3Rate, graduateAssistantRate, null, null);
    }

    public BudgetGraduateAssistantRate(String documentHeaderId, String campusCode, KualiDecimal campusMaximumPeriod1Rate, KualiDecimal campusMaximumPeriod2Rate, KualiDecimal campusMaximumPeriod3Rate, KualiDecimal campusMaximumPeriod4Rate, KualiDecimal campusMaximumPeriod5Rate, KualiDecimal campusMaximumPeriod6Rate, GraduateAssistantRate graduateAssistantRate, String objectId, Long versionNumber) {
        super();
        this.documentHeaderId = documentHeaderId;
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


    private String documentHeaderId;
    private String campusCode;
    private KualiDecimal campusMaximumPeriod1Rate;
    private KualiDecimal campusMaximumPeriod2Rate;
    private KualiDecimal campusMaximumPeriod3Rate;
    private KualiDecimal campusMaximumPeriod4Rate;
    private KualiDecimal campusMaximumPeriod5Rate;
    private KualiDecimal campusMaximumPeriod6Rate;
    private Timestamp lastUpdateTimestamp;

    private GraduateAssistantRate graduateAssistantRate;

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

    public String getDocumentHeaderId() {
        return documentHeaderId;
    }

    public void setDocumentHeaderId(String documentHeaderId) {
        this.documentHeaderId = documentHeaderId;
    }

    public void afterDelete(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.afterDelete(persistenceBroker);
    }

    public void afterInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.afterInsert(persistenceBroker);
    }

    public void afterLookup(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.afterLookup(persistenceBroker);
    }

    public void afterUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.afterUpdate(persistenceBroker);
    }

    public void beforeDelete(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.beforeDelete(persistenceBroker);
    }

    public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.beforeInsert(persistenceBroker);
    }

    public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.beforeUpdate(persistenceBroker);
    }

    public List getExtendedAttributeValues() {
        // TODO Auto-generated method stub
        return super.getExtendedAttributeValues();
    }

    public void setExtendedAttributeValues(List extendedAttributeValues) {
        // TODO Auto-generated method stub
        super.setExtendedAttributeValues(extendedAttributeValues);
    }

    protected String toStringBuilder(LinkedHashMap fieldValues) {
        // TODO Auto-generated method stub
        return super.toStringBuilder(fieldValues);
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("documentHeaderId", this.documentHeaderId);
        m.put("campusCode", this.campusCode);
        return m;
    }

    public void validate() {
        // TODO Auto-generated method stub
        super.validate();
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

}