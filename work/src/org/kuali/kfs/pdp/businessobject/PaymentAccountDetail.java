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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;

public class PaymentAccountDetail extends TimestampedBusinessObjectBase {

    private KualiInteger id; // PMT_ACCT_DTL_ID
    private String finChartCode; // FIN_COA_CD
    private String accountNbr; // ACCOUNT_NBR
    private String subAccountNbr; // SUB_ACCT_NBR
    private String finObjectCode; // FIN_OBJECT_CD
    private String finSubObjectCode; // FIN_SUB_OBJ_CD
    private String orgReferenceId; // ORG_REFERENCE_ID
    private String projectCode; // PROJECT_CD
    private KualiDecimal accountNetAmount; // ACCT_NET_AMT

    private KualiInteger paymentDetailId;
    private PaymentDetail paymentDetail; // PMT_DTL_ID

    private List<PaymentAccountHistory> accountHistory = new ArrayList<PaymentAccountHistory>();

    private Chart chartOfAccounts;
    private Account account;
    private SubAccount subAccount;
    private ProjectCode project;
    private ObjectCodeCurrent objectCode;

    /**
     * Constructs a PaymentAccountDetail.java.
     */
    public PaymentAccountDetail() {
        super();
    }

    /**
     * This method gets the accountHistory list.
     * 
     * @return the accountHistory list
     */
    public List<PaymentAccountHistory> getAccountHistory() {
        return accountHistory;
    }

    /**
     * This method sets the accountHistory list
     * 
     * @param ah
     */
    public void setAccountHistory(List<PaymentAccountHistory> ah) {
        accountHistory = ah;
    }

    /**
     * This method add a new PaymentAccountHistory.
     * 
     * @param pah
     */
    public void addAccountHistory(PaymentAccountHistory pah) {
        pah.setPaymentAccountDetail(this);
        accountHistory.add(pah);
    }

    /**
     * This method deletes a PaymentAccountHistory.
     * 
     * @param pah
     */
    public void deleteAccountDetail(PaymentAccountHistory pah) {
        accountHistory.remove(pah);
    }

    /**
     * @hibernate.id column="PMT_ACCT_DTL_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_ACCT_DTL_ID_SEQ"
     * @return
     */
    public KualiInteger getId() {
        return id;
    }

    /**
     * @return
     * @hibernate.many-to-one column="PMT_DTL_ID" class="edu.iu.uis.pdp.bo.PaymentDetail"
     */
    public PaymentDetail getPaymentDetail() {
        return this.paymentDetail;
    }

    /**
     * @return
     * @hibernate.property column="ACCOUNT_NBR" length="7"
     */
    public String getAccountNbr() {
        return accountNbr;
    }

    /**
     * @return
     * @hibernate.property column="ACCT_NET_AMT" length="14"
     */
    public KualiDecimal getAccountNetAmount() {
        return accountNetAmount;
    }

    /**
     * @return
     * @hibernate.property column="FIN_COA_CD" length="2"
     */
    public String getFinChartCode() {
        return finChartCode;
    }

    /**
     * @return
     * @hibernate.property column="FIN_OBJECT_CD" length="4"
     */
    public String getFinObjectCode() {
        return finObjectCode;
    }

    /**
     * @return
     * @hibernate.property column="FIN_SUB_OBJ_CD" length="3"
     */
    public String getFinSubObjectCode() {
        return finSubObjectCode;
    }

    /**
     * @return
     * @hibernate.property column="ORG_REFERENCE_ID" length="8"
     */
    public String getOrgReferenceId() {
        return orgReferenceId;
    }

    /**
     * @return
     * @hibernate.property column="PROJECT_CD" length="10"
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * @return
     * @hibernate.property column="SUB_ACCT_NBR" length="5"
     */
    public String getSubAccountNbr() {
        return subAccountNbr;
    }

    /**
     * @param string
     */
    public void setAccountNbr(String string) {
        accountNbr = string;
    }

    /**
     * @param string
     */
    public void setAccountNetAmount(KualiDecimal bigdecimal) {
        accountNetAmount = bigdecimal;
    }

    public void setAccountNetAmount(String bigdecimal) {
        accountNetAmount = new KualiDecimal(bigdecimal);
    }

    /**
     * @param integer
     */
    public void setPaymentDetail(PaymentDetail pd) {
        paymentDetail = pd;
    }

    /**
     * @param string
     */
    public void setFinChartCode(String string) {
        finChartCode = string;
    }

    /**
     * @param string
     */
    public void setFinObjectCode(String string) {
        finObjectCode = string;
    }

    /**
     * @param string
     */
    public void setFinSubObjectCode(String string) {
        finSubObjectCode = string;
    }

    /**
     * @param integer
     */
    public void setId(KualiInteger integer) {
        id = integer;
    }

    /**
     * @param string
     */
    public void setOrgReferenceId(String string) {
        orgReferenceId = string;
    }

    /**
     * @param string
     */
    public void setProjectCode(String string) {
        projectCode = string;
    }

    /**
     * @param string
     */
    public void setSubAccountNbr(String string) {
        subAccountNbr = string;
    }

    /**
     * Gets the paymentDetailId attribute.
     * 
     * @return Returns the paymentDetailId.
     */
    public KualiInteger getPaymentDetailId() {
        return paymentDetailId;
    }

    /**
     * Sets the paymentDetailId attribute value.
     * 
     * @param paymentDetailId The paymentDetailId to set.
     */
    public void setPaymentDetailId(KualiInteger paymentDetailId) {
        this.paymentDetailId = paymentDetailId;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.ID, this.id);

        return m;
    }

    /**
     * This method gets the account.
     * 
     * @return the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * This method sets the account.
     * 
     * @param account
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * This method gets the subAccount.
     * 
     * @return the subAccount
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * This method sets the subAccount.
     * 
     * @param subAccount
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * This method gets the chart of accounts.
     * 
     * @return the chart of accounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * This method sets the chart of accounts.
     * 
     * @param chartOfAccounts
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * This method gets the project.
     * 
     * @return the project
     */
    public ProjectCode getProject() {
        return project;
    }

    /**
     * This method sets the project.
     * 
     * @param project
     */
    public void setProject(ProjectCode project) {
        this.project = project;
    }

    /**
     * This method gets the cuttent object code.
     * 
     * @return the current object code
     */
    public ObjectCodeCurrent getObjectCode() {
        return objectCode;
    }

    /**
     * This method sets the cuttent object code.
     * 
     * @param objectCode
     */
    public void setObjectCode(ObjectCodeCurrent objectCode) {
        this.objectCode = objectCode;
    }
}
