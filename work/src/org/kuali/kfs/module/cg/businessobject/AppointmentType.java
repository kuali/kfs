/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.budget.bo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * Account Business Object
 * 
 * @author Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AppointmentType extends BusinessObjectBase {

    private static final long serialVersionUID = 5817907435877665832L;
    private String appointmentTypeCode;
    private String appointmentTypeDescription;
    private String appointmentTypeAbbrieviation;
    private KualiDecimal fringeRateAmount;
    private KualiDecimal costShareFringeRateAmount;
    private Integer fiscalYear;
    private Timestamp lastUpdate;
    private boolean displayGrid;
    private boolean active;
    private List appointmentTypeEffectiveDateItems = new ArrayList();
    private String relatedAppointmentTypeCode;
    private AppointmentType relatedAppointmentType;

    /*******************************************************************************************************************************
     * 
     * 
     */
    public AppointmentType() {
    }


    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return Returns the appointmentTypeAbbrieviation.
     */
    public String getAppointmentTypeAbbrieviation() {
        return appointmentTypeAbbrieviation;
    }

    /**
     * @param appointmentTypeAbbrieviation The appointmentTypeAbbrieviation to set.
     */
    public void setAppointmentTypeAbbrieviation(String appointmentTypeAbbrieviation) {
        this.appointmentTypeAbbrieviation = appointmentTypeAbbrieviation;
    }

    /**
     * @return Returns the appointmentTypeDescription.
     */
    public String getAppointmentTypeDescription() {
        return appointmentTypeDescription;
    }

    /**
     * @param appointmentTypeDescription The appointmentTypeDescription to set.
     */
    public void setAppointmentTypeDescription(String appointmentTypeDescription) {
        this.appointmentTypeDescription = appointmentTypeDescription;
    }

    /**
     * @return Returns the costShareFringeRateAmount.
     */
    public KualiDecimal getCostShareFringeRateAmount() {
        return costShareFringeRateAmount;
    }

    /**
     * @param costShareFringeRateAmount The costShareFringeRateAmount to set.
     */
    public void setCostShareFringeRateAmount(KualiDecimal costShareFringeRateAmount) {
        this.costShareFringeRateAmount = costShareFringeRateAmount;
    }

    /**
     * @return Returns the fiscalYear.
     */
    public Integer getFiscalYear() {
        return fiscalYear;
    }

    /**
     * @param fiscalYear The fiscalYear to set.
     */
    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    /**
     * @return Returns the fringeRateAmount.
     */
    public KualiDecimal getFringeRateAmount() {
        return fringeRateAmount;
    }

    /**
     * @param fringeRateAmount The fringeRateAmount to set.
     */
    public void setFringeRateAmount(KualiDecimal fringeRateAmount) {
        this.fringeRateAmount = fringeRateAmount;
    }

    /**
     * @return Returns the appointmentTypeCode.
     */
    public String getAppointmentTypeCode() {
        return appointmentTypeCode;
    }

    /**
     * @return Returns the lastUpdate.
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterDelete(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterDelete(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.afterDelete(persistenceBroker);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.afterInsert(persistenceBroker);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterLookup(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterLookup(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.afterLookup(persistenceBroker);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.afterUpdate(persistenceBroker);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeDelete(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeDelete(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.beforeDelete(persistenceBroker);
    }

    public void beforeSave() {
        this.lastUpdate = new Timestamp(new Date().getTime());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.beforeInsert(persistenceBroker);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        // TODO Auto-generated method stub
        super.beforeUpdate(persistenceBroker);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.bo.BusinessObject#getExtendedAttributeValues()
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringBuilder(java.util.LinkedHashMap)
     */
    protected String toStringBuilder(LinkedHashMap fieldValues) {
        // TODO Auto-generated method stub
        return super.toStringBuilder(fieldValues);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("appointmentTypeCode", this.appointmentTypeCode);

        return m;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.bo.BusinessObject#validate()
     */
    public void validate() {
        // TODO Auto-generated method stub
        super.validate();
    }

    /**
     * Sets the appointmentTypeCode attribute value.
     * 
     * @param appointmentTypeCode The appointmentTypeCode to set.
     */
    public void setAppointmentTypeCode(String appointmentTypeCode) {
        this.appointmentTypeCode = appointmentTypeCode;
    }

    /**
     * Sets the lastUpdate attribute value.
     * 
     * @param lastUpdate The lastUpdate to set.
     */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Gets the appointmentTypeEffectiveDateItems attribute.
     * 
     * @return - Returns the appointmentTypeEffectiveDateItems
     * 
     */
    public List getAppointmentTypeEffectiveDateItems() {
        return appointmentTypeEffectiveDateItems;
    }

    /**
     * Sets the appointmentTypeEffectiveDateItems attribute.
     * 
     * @param appointmentTypeEffectiveDateItems The appointmentTypeEffectiveDateItems to set.
     * 
     */
    public void setAppointmentTypeEffectiveDateItems(List appointmentTypeEffectiveDateItems) {
        this.appointmentTypeEffectiveDateItems = appointmentTypeEffectiveDateItems;
    }


    public boolean isDisplayGrid() {
        return displayGrid;
    }


    public void setDisplayGrid(boolean displayGrid) {
        this.displayGrid = displayGrid;
    }


    public AppointmentType getRelatedAppointmentType() {
        return relatedAppointmentType;
    }


    public void setRelatedAppointmentType(AppointmentType relatedAppointmentType) {
        this.relatedAppointmentType = relatedAppointmentType;
    }


    public String getRelatedAppointmentTypeCode() {
        return relatedAppointmentTypeCode;
    }


    public void setRelatedAppointmentTypeCode(String relatedAppointmentTypeCode) {
        this.relatedAppointmentTypeCode = relatedAppointmentTypeCode;
    }
}
