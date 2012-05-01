/*
 * Copyright 2007 The Kuali Foundation
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
/*
 * Created on Jul 12, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.campus.CampusEbo;

public class DisbursementNumberRange extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected String physCampusProcCode;
    protected KualiInteger beginDisbursementNbr;
    protected KualiInteger lastAssignedDisbNbr;
    protected KualiInteger endDisbursementNbr;
    protected Date disbNbrRangeStartDt;
    protected String bankCode;
    protected String disbursementTypeCode;
    protected boolean active;

    protected CampusEbo campus;
    protected Bank bank;
    protected DisbursementType disbursementType;

    public DisbursementNumberRange() {
        super();
    }

    /**
     * @return
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Gets the bankCode attribute.
     *
     * @return Returns the bankCode.
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Sets the bankCode attribute value.
     *
     * @param bankCode The bankCode to set.
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    /**
     * @return
     * @hibernate.property column="BEG_DISB_NBR"
     */
    public KualiInteger getBeginDisbursementNbr() {
        return beginDisbursementNbr;
    }

    /**
     * @return
     * @hibernate.property column="END_DISB_NBR"
     */
    public KualiInteger getEndDisbursementNbr() {
        return endDisbursementNbr;
    }

    /**
     * @return
     * @hibernate.property column="LST_ASND_DISB_NBR"
     */
    public KualiInteger getLastAssignedDisbNbr() {
        return lastAssignedDisbNbr;
    }

    /**
     * @return
     * @hibernate.property column="PHYS_CMP_PROC_CD" length="2"
     */
    public String getPhysCampusProcCode() {
        return physCampusProcCode;
    }

    /**
     * @param Bank
     */
    @Deprecated
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * @param integer
     */
    public void setBeginDisbursementNbr(KualiInteger integer) {
        beginDisbursementNbr = integer;
    }

    /**
     * @param integer
     */
    public void setEndDisbursementNbr(KualiInteger integer) {
        endDisbursementNbr = integer;
    }

    /**
     * @param integer
     */
    public void setLastAssignedDisbNbr(KualiInteger integer) {
        lastAssignedDisbNbr = integer;
    }

    /**
     * @param string
     */
    public void setPhysCampusProcCode(String string) {
        physCampusProcCode = string;
    }

    /**
     * Gets the disbursementTypeCode attribute.
     *
     * @return Returns the disbursementTypeCode.
     */
    public String getDisbursementTypeCode() {
        return disbursementTypeCode;
    }

    /**
     * Sets the disbursementTypeCode attribute value.
     *
     * @param disbursementTypeCode The disbursementTypeCode to set.
     */
    public void setDisbursementTypeCode(String disbursementTypeCode) {
        this.disbursementTypeCode = disbursementTypeCode;
    }

    /**
     * Gets the disbursementType attribute.
     *
     * @return Returns the disbursementType.
     */
    public DisbursementType getDisbursementType() {
        return disbursementType;
    }

    /**
     * Sets the disbursementType attribute value.
     *
     * @param disbursementType The disbursementType to set.
     */
    public void setDisbursementType(DisbursementType disbursementType) {
        this.disbursementType = disbursementType;
    }

    /**
     * Gets the campus attribute.
     *
     * @return Returns the campus.
     */
    public CampusEbo getCampus() {
        if ( StringUtils.isBlank(physCampusProcCode) ) {
            campus = null;
        } else {
            if ( campus == null || !StringUtils.equals( campus.getCode(),physCampusProcCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CampusEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, physCampusProcCode);
                    campus = moduleService.getExternalizableBusinessObject(CampusEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return campus;
    }

    /**
     * Sets the campus attribute value.
     *
     * @param campus The campus to set.
     */
    public void setCampus(CampusEbo campus) {
        this.campus = campus;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the disbNbrRangeStartDt attribute.
     *
     * @return Returns the disbNbrRangeStartDt.
     */
    public Date getDisbNbrRangeStartDt() {
        return disbNbrRangeStartDt;
    }

    /**
     * Sets the disbNbrRangeStartDt attribute value.
     *
     * @param disbNbrRangeStartDt The disbNbrRangeStartDt to set.
     */
    public void setDisbNbrRangeStartDt(Date disbNbrRangeStartDt) {
        this.disbNbrRangeStartDt = disbNbrRangeStartDt;
    }

}
