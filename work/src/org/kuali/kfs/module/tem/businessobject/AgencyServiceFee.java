/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

@Entity
@Table(name = "TEM_AGENCY_SRVC_FEE_T")
public class AgencyServiceFee extends PersistableBusinessObjectBase{

    private Integer id;
    private String diCode;
    private String diCodeDescription;
    private String creditChartCode;
    private String creditAccountNumber;
    private String creditObjectCode;
    private KualiDecimal serviceFee;
    private Boolean active = Boolean.TRUE;
    
    private Account account;
    private ObjectCode objectCode;
    
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("diCode", diCode);
        map.put("diCodeDescription", diCodeDescription);
        map.put("creditChartCode", creditChartCode);
        map.put("creditAccountNumber", creditAccountNumber);
        map.put("creditObjectCode", creditObjectCode);
        map.put("serviceFee", serviceFee);
        
        return map;
    }

    /**
     * Gets the id attribute. 
     * @return Returns the id.
     */
    @Id
    @GeneratedValue(generator = "TEM_AGENCY_SRVC_FEE_ID_SEQ")
    @SequenceGenerator(name = "TEM_AGENCY_SRVC_FEE_ID_SEQ", sequenceName = "TEM_AGENCY_SRVC_FEE_ID_SEQ", allocationSize = 5)
    @Column(name = "ID", nullable = false)
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id attribute value.
     * @param id The id to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Gets the diCode attribute. 
     * @return Returns the diCode.
     */
    @Column(name = "DI_CD", length = 3, nullable = false)
    public String getDiCode() {
        return diCode;
    }

    /**
     * Sets the diCode attribute value.
     * @param diCode The diCode to set.
     */
    public void setDiCode(String diCode) {
        this.diCode = diCode;
    }

    /**
     * Gets the diCodeDescription attribute. 
     * @return Returns the diCodeDescription.
     */
    @Column(name = "DI_DESC", length = 40, nullable = true)
    public String getDiCodeDescription() {
        return diCodeDescription;
    }

    /**
     * Sets the diCodeDescription attribute value.
     * @param diCodeDescription The diCodeDescription to set.
     */
    public void setDiCodeDescription(String diCodeDescription) {
        this.diCodeDescription = diCodeDescription;
    }

    /**
     * Gets the creditChartCode attribute. 
     * @return Returns the creditChartCode.
     */
    @Column(name = "CHART_CD", length = 2, nullable = false)
    public String getCreditChartCode() {
        return creditChartCode;
    }

    /**
     * Sets the creditChartCode attribute value.
     * @param creditChartCode The creditChartCode to set.
     */
    public void setCreditChartCode(String creditChartCode) {
        this.creditChartCode = creditChartCode;
    }

    /**
     * Gets the creditAccountNumber attribute. 
     * @return Returns the creditAccountNumber.
     */
    @Column(name = "ACCT_NBR", length = 7, nullable = false)
    public String getCreditAccountNumber() {
        return creditAccountNumber;
    }

    /**
     * Sets the creditAccountNumber attribute value.
     * @param creditAccountNumber The creditAccountNumber to set.
     */
    public void setCreditAccountNumber(String creditAccountNumber) {
        this.creditAccountNumber = creditAccountNumber;
    }

    /**
     * Gets the creditObjectCode attribute. 
     * @return Returns the creditObjectCode.
     */
    @Column(name = "OBJ_CD", length = 4, nullable = false)
    public String getCreditObjectCode() {
        return creditObjectCode;
    }

    /**
     * Sets the creditObjectCode attribute value.
     * @param creditObjectCode The creditObjectCode to set.
     */
    public void setCreditObjectCode(String creditObjectCode) {
        this.creditObjectCode = creditObjectCode;
    }

    /**
     * Gets the serviceFee attribute. 
     * @return Returns the serviceFee.
     */
    @Column(name = "SRVC_FEE", nullable = true)
    public KualiDecimal getServiceFee() {
        return serviceFee;
    }

    /**
     * Sets the serviceFee attribute value.
     * @param serviceFee The serviceFee to set.
     */
    public void setServiceFee(KualiDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    @Column(name="ACTV_IND",nullable=false,length=1)
    public Boolean getActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * Gets the account attribute. 
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the objectCode attribute. 
     * @return Returns the objectCode.
     */
    public ObjectCode getObjectCode() {
        return objectCode;
    }

    /**
     * Sets the objectCode attribute value.
     * @param objectCode The objectCode to set.
     */
    public void setObjectCode(ObjectCode objectCode) {
        this.objectCode = objectCode;
    }

}
