/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This KemidBenefittingOrganization class provides the institutional organizations that benefit from the KEMID.
 */
public class KemidBenefittingOrganization extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String kemid;
    private KualiInteger benefittingOrgSeqNumber;
    private String benefittingOrgCode;
    private String benefittingChartCode;
    private KualiDecimal benefitPrecent;
    private Date startDate;
    private Date lastChangeDate;
    private boolean active;

    private KEMID kemidObjRef;
    private Organization organization;
    private Chart chart;


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        m.put(EndowPropertyConstants.KEMID_BENE_ORG_CD, this.benefittingOrgCode);
        m.put(EndowPropertyConstants.KEMID_BENE_CHRT_CD, this.benefittingChartCode);
        m.put(EndowPropertyConstants.KEMID_BENE_ORG_SEQ_NBR, String.valueOf(this.benefittingOrgSeqNumber));
        return m;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the benefittingOrgSeqNumber.
     * 
     * @return benefittingOrgSeqNumber
     */
    public KualiInteger getBenefittingOrgSeqNumber() {
        return benefittingOrgSeqNumber;
    }

    /**
     * Sets the benefittingOrgSeqNumber.
     * 
     * @param benefittingOrgSeqNumber
     */
    public void setBenefittingOrgSeqNumber(KualiInteger benefittingOrgSeqNumber) {
        this.benefittingOrgSeqNumber = benefittingOrgSeqNumber;
    }

    /**
     * Gets the benefittingOrgCode.
     * 
     * @return benefittingOrgCode
     */
    public String getBenefittingOrgCode() {
        return benefittingOrgCode;
    }

    /**
     * Sets the benefittingOrgCode.
     * 
     * @param benefittingOrgCode
     */
    public void setBenefittingOrgCode(String benefittingOrgCode) {
        this.benefittingOrgCode = benefittingOrgCode;
    }

    /**
     * Gets the benefitPrecent.
     * 
     * @return benefitPrecent
     */
    public KualiDecimal getBenefitPrecent() {
        return benefitPrecent;
    }

    /**
     * Sets the benefitPrecent.
     * 
     * @param benefitPrecent
     */
    public void setBenefitPrecent(KualiDecimal benefitPrecent) {
        this.benefitPrecent = benefitPrecent;
    }

    /**
     * Gets the startDate.
     * 
     * @return startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the startDate.
     * 
     * @param startDate
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the lastChangeDate.
     * 
     * @return lastChangeDate
     */
    public Date getLastChangeDate() {
        return lastChangeDate;
    }

    /**
     * Sets the lastChangeDate.
     * 
     * @param lastChangeDate
     */
    public void setLastChangeDate(Date lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }

    /**
     * Gets the kemidObjRef.
     * 
     * @return kemidObjRef
     */
    public KEMID getKemidObjRef() {
        return kemidObjRef;
    }

    /**
     * Sets the kemidObjRef.
     * 
     * @param kemidObjRef
     */
    public void setKemidObjRef(KEMID kemidObjRef) {
        this.kemidObjRef = kemidObjRef;
    }

    /**
     * Gets the kemid.
     * 
     * @return kemid
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * Sets the kemid.
     * 
     * @param kemid
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * Gets the benefittingChartCode.
     * 
     * @return benefittingChartCode
     */
    public String getBenefittingChartCode() {
        return benefittingChartCode;
    }

    /**
     * Sets the benefittingChartCode.
     * 
     * @param benefittingChartCode
     */
    public void setBenefittingChartCode(String benefittingChartCode) {
        this.benefittingChartCode = benefittingChartCode;
    }

    /**
     * Gets the chart.
     * 
     * @return chart
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart.
     * 
     * @param chart
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * Gets the organization.
     * 
     * @return organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization.
     * 
     * @param organization
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the organization code for report 
     * 
     * @return benefittingOrgCode
     */
    public String getBenefittingOrgCodeForReport() {
        return benefittingOrgCode;
    }

    /**
     * Gets the organization chart code for report 
     * 
     * @return benefittingChartCode
     */
    public String getBenefittingChartCodeForReport() {
        return benefittingChartCode;
    }
}
