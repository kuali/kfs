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
import java.util.LinkedHashMap;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;

/**
 * Budget Fringe Rate Business Object
 * 
 * @author Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetFringeRate extends BusinessObjectBase {

    private static final long serialVersionUID = -1305514388024735249L;
    private String documentHeaderId;
    private String universityAppointmentTypeCode;
    private KualiDecimal contractsAndGrantsFringeRateAmount;
    private KualiDecimal universityCostShareFringeRateAmount;
    private Timestamp budgetLastUpdateTimestamp;

    private AppointmentType appointmentType;


    /**
     * 
     */
    public BudgetFringeRate() {
        super();
        appointmentType = new AppointmentType();
    }

    public BudgetFringeRate(String documentHeaderId, String universityAppointmentTypeCode, KualiDecimal contractsAndGrantsFringeRateAmount, KualiDecimal universityCostShareFringeRateAmount, AppointmentType appointmentType) {
        this(documentHeaderId, universityAppointmentTypeCode, contractsAndGrantsFringeRateAmount, universityCostShareFringeRateAmount, appointmentType, null, null);
    }

    public BudgetFringeRate(String documentHeaderId, AppointmentType appointmentType) {
        this(documentHeaderId, appointmentType.getAppointmentTypeCode(), appointmentType.getFringeRateAmount(), appointmentType.getCostShareFringeRateAmount(), appointmentType, null, null);
    }


    public BudgetFringeRate(String documentHeaderId, String universityAppointmentTypeCode, KualiDecimal contractsAndGrantsFringeRateAmount, KualiDecimal universityCostShareFringeRateAmount, AppointmentType appointmentType, String objectId, Long versionNumber) {
        super();
        this.documentHeaderId = documentHeaderId;
        this.universityAppointmentTypeCode = universityAppointmentTypeCode;
        this.contractsAndGrantsFringeRateAmount = contractsAndGrantsFringeRateAmount;
        this.universityCostShareFringeRateAmount = universityCostShareFringeRateAmount;
        this.appointmentType = appointmentType;
        setObjectId(objectId);
        setVersionNumber(versionNumber);
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

        m.put("documentHeaderId", this.documentHeaderId);
        m.put("universityAppointmentTypeCode", this.universityAppointmentTypeCode);
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        String hashString = this.getDocumentHeaderId() + "|" + this.getUniversityAppointmentTypeCode() + "|" + this.getAppointmentType().toString() + "|" + this.getAppointmentTypeCostShareFringeRateAmount().toString() + "|" + this.getAppointmentTypeFringeRateAmount().toString() + "|" + this.getBudgetLastUpdateTimestamp().toString() + "|" + this.getContractsAndGrantsFringeRateAmount().toString() + "|" + this.getUniversityCostShareFringeRateAmount().toString();
        return hashString.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        // TODO Auto-generated method stub
        super.finalize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    protected Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equals = true;

        if (ObjectUtils.isNotNull(obj) && obj instanceof BudgetFringeRate) {
            BudgetFringeRate objCompare = (BudgetFringeRate) obj;
            equals &= this.documentHeaderId.equals(objCompare.getDocumentHeaderId());
            equals &= this.universityAppointmentTypeCode.equals(objCompare.getUniversityAppointmentTypeCode());
            if (this.budgetLastUpdateTimestamp == null && objCompare.getBudgetLastUpdateTimestamp() == null) {
            }
            else {
                equals &= this.budgetLastUpdateTimestamp != null && objCompare.getBudgetLastUpdateTimestamp() != null && this.budgetLastUpdateTimestamp.equals(objCompare.getBudgetLastUpdateTimestamp());
            }
            if (this.contractsAndGrantsFringeRateAmount == null && objCompare.getContractsAndGrantsFringeRateAmount() == null) {
            }
            else {
                equals &= this.contractsAndGrantsFringeRateAmount != null && objCompare.getContractsAndGrantsFringeRateAmount() != null && this.contractsAndGrantsFringeRateAmount.equals(objCompare.getContractsAndGrantsFringeRateAmount());
            }
            if (this.universityCostShareFringeRateAmount == null && objCompare.getUniversityCostShareFringeRateAmount() == null) {
            }
            else {
                equals &= this.universityCostShareFringeRateAmount != null && objCompare.getUniversityCostShareFringeRateAmount() != null && this.universityCostShareFringeRateAmount.equals(objCompare.getUniversityCostShareFringeRateAmount());
            }
        }

        return equals;
    }

    /**
     * @return Returns the budgetLastUpdate.
     */
    public Timestamp getBudgetLastUpdateTimestamp() {
        return budgetLastUpdateTimestamp;
    }

    /**
     * @param budgetLastUpdate The budgetLastUpdate to set.
     */
    public void setBudgetLastUpdateTimestamp(Timestamp budgetLastUpdateTimestamp) {
        this.budgetLastUpdateTimestamp = budgetLastUpdateTimestamp;
    }

    /**
     * @return Returns the documentHeaderId.
     */
    public String getDocumentHeaderId() {
        return documentHeaderId;
    }

    /**
     * @param documentHeaderId The documentHeaderId to set.
     */
    public void setDocumentHeaderId(String documentHeaderId) {
        this.documentHeaderId = documentHeaderId;
    }

    /**
     * @return Returns the contractsAndGrantsFringeRateAmount.
     */
    public KualiDecimal getContractsAndGrantsFringeRateAmount() {
        return contractsAndGrantsFringeRateAmount;
    }

    /**
     * @param contractsAndGrantsFringeRateAmount The contractsAndGrantsFringeRateAmount to set.
     */
    public void setContractsAndGrantsFringeRateAmount(KualiDecimal contractsAndGrantsFringeRateAmount) {
        this.contractsAndGrantsFringeRateAmount = contractsAndGrantsFringeRateAmount;
    }

    /**
     * @return Returns the universityAppointmentTypeCode.
     */
    public String getUniversityAppointmentTypeCode() {
        return universityAppointmentTypeCode;
    }

    /**
     * @param universityAppointmentTypeCode The universityAppointmentTypeCode to set.
     */
    public void setUniversityAppointmentTypeCode(String universityAppointmentTypeCode) {
        this.universityAppointmentTypeCode = universityAppointmentTypeCode;
    }

    /**
     * @return Returns the universityCostShareFringeRateAmount.
     */
    public KualiDecimal getUniversityCostShareFringeRateAmount() {
        return universityCostShareFringeRateAmount;
    }

    /**
     * @param universityCostShareFringeRateAmount The universityCostShareFringeRateAmount to set.
     */
    public void setUniversityCostShareFringeRateAmount(KualiDecimal universityCostShareFringeRateAmount) {
        this.universityCostShareFringeRateAmount = universityCostShareFringeRateAmount;
    }

    /**
     * @return Returns the appointmentType.
     */
    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    /**
     * @param appointmentType The appointmentType to set.
     */
    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public KualiDecimal getAppointmentTypeFringeRateAmount() {
        return this.appointmentType.getFringeRateAmount();
    }

    public KualiDecimal getAppointmentTypeCostShareFringeRateAmount() {
        return this.appointmentType.getCostShareFringeRateAmount();
    }
}