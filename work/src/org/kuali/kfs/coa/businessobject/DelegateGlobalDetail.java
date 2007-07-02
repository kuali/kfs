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

package org.kuali.module.chart.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.bo.GlobalBusinessObjectDetailBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;

/**
 * 
 */
public class DelegateGlobalDetail extends GlobalBusinessObjectDetailBase {

    private static final long serialVersionUID = -8089154029664644867L;

    private String accountDelegateUniversalId;
    private String financialDocumentTypeCode;
    private KualiDecimal approvalFromThisAmount;
    private KualiDecimal approvalToThisAmount;
    private boolean accountDelegatePrimaryRoutingIndicator;
    private Date accountDelegateStartDate;

    private UniversalUser accountDelegate;
    private DocumentType documentType;

    /**
     * Default constructor.
     */
    public DelegateGlobalDetail() {
        super();
    }

    public DelegateGlobalDetail(OrganizationRoutingModel model) {
        accountDelegatePrimaryRoutingIndicator = model.getAccountDelegatePrimaryRoutingIndicator();
        accountDelegateStartDate = model.getAccountDelegateStartDate();
        accountDelegateUniversalId = model.getAccountDelegateUniversalId();
        approvalFromThisAmount = model.getApprovalFromThisAmount();
        approvalToThisAmount = model.getApprovalToThisAmount();
        financialDocumentTypeCode = model.getFinancialDocumentTypeCode();
    }

    /**
     * Gets the documentType attribute.
     * 
     * @return Returns the documentType.
     */
    public DocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Sets the documentType attribute value.
     * 
     * @param documentType The documentType to set.
     * @deprecated
     */
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    /**
     * Gets the accountDelegateUniversalId attribute.
     * 
     * @return Returns the accountDelegateUniversalId
     * 
     */
    public String getAccountDelegateUniversalId() {
        return accountDelegateUniversalId;
    }

    /**
     * Sets the accountDelegateUniversalId attribute.
     * 
     * @param accountDelegateUniversalId The accountDelegateUniversalId to set.
     * 
     */
    public void setAccountDelegateUniversalId(String accountDelegateUniversalId) {
        this.accountDelegateUniversalId = accountDelegateUniversalId;
    }


    /**
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return Returns the financialDocumentTypeCode
     * 
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     * 
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }


    /**
     * Gets the approvalFromThisAmount attribute.
     * 
     * @return Returns the approvalFromThisAmount
     * 
     */
    public KualiDecimal getApprovalFromThisAmount() {
        return approvalFromThisAmount;
    }

    /**
     * Sets the approvalFromThisAmount attribute.
     * 
     * @param approvalFromThisAmount The approvalFromThisAmount to set.
     * 
     */
    public void setApprovalFromThisAmount(KualiDecimal approvalFromThisAmount) {
        this.approvalFromThisAmount = approvalFromThisAmount;
    }


    /**
     * Gets the approvalToThisAmount attribute.
     * 
     * @return Returns the approvalToThisAmount
     * 
     */
    public KualiDecimal getApprovalToThisAmount() {
        return approvalToThisAmount;
    }

    /**
     * Sets the approvalToThisAmount attribute.
     * 
     * @param approvalToThisAmount The approvalToThisAmount to set.
     * 
     */
    public void setApprovalToThisAmount(KualiDecimal approvalToThisAmount) {
        this.approvalToThisAmount = approvalToThisAmount;
    }


    /**
     * Gets the accountDelegatePrimaryRoutingIndicator attribute.
     * 
     * @return Returns the accountDelegatePrimaryRoutingIndicator
     * 
     */
    public boolean getAccountDelegatePrimaryRoutingIndicator() {
        return accountDelegatePrimaryRoutingIndicator;
    }

    /**
     * Sets the accountDelegatePrimaryRoutingIndicator attribute.
     * 
     * @param accountDelegatePrimaryRoutingIndicator The accountDelegatePrimaryRoutingIndicator to set.
     * @deprecated
     */
    public void setAccountDelegatePrimaryRoutingIndicator(boolean accountDelegatePrimaryRoutingIndicator) {
        this.accountDelegatePrimaryRoutingIndicator = accountDelegatePrimaryRoutingIndicator;
    }


    /**
     * Gets the accountDelegateStartDate attribute.
     * 
     * @return Returns the accountDelegateStartDate
     * 
     */
    public Date getAccountDelegateStartDate() {
        return accountDelegateStartDate;
    }

    /**
     * Sets the accountDelegateStartDate attribute.
     * 
     * @param accountDelegateStartDate The accountDelegateStartDate to set.
     * 
     */
    public void setAccountDelegateStartDate(Date accountDelegateStartDate) {
        this.accountDelegateStartDate = accountDelegateStartDate;
    }

    public UniversalUser getAccountDelegate() {
        accountDelegate = SpringServiceLocator.getUniversalUserService().updateUniversalUserIfNecessary(accountDelegateUniversalId, accountDelegate);
        return accountDelegate;
    }

    /**
     * @param accountDelegate The accountDelegate to set.
     * @deprecated
     */
    public void setAccountDelegate(UniversalUser accountDelegate) {
        this.accountDelegate = accountDelegate;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                DelegateGlobalDetail other = (DelegateGlobalDetail) obj;
                if (StringUtils.equalsIgnoreCase(getDocumentNumber(), other.getDocumentNumber())) {
                    if (StringUtils.equalsIgnoreCase(this.financialDocumentTypeCode, other.financialDocumentTypeCode)) {
                        if (this.accountDelegatePrimaryRoutingIndicator == other.accountDelegatePrimaryRoutingIndicator) {
                            if (StringUtils.equalsIgnoreCase(this.accountDelegateUniversalId, other.accountDelegateUniversalId)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return toStringBuilder(toStringMapper()).hashCode();
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        m.put("accountDelegateUniversalId", this.accountDelegateUniversalId);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        return m;
    }
}
