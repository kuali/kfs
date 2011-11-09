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

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiInteger;

public class PaymentAccountHistory extends TimestampedBusinessObjectBase {

    private KualiInteger id; // PMT_ACCT_HIST_ID

    private String accountingChangeCode;
    private AccountingChangeCode accountingChange; // ACCTG_CHG_CD

    private String acctAttributeName; // ACCT_ATTRIB_NM
    private String acctAttributeOrigValue; // ACCT_ATTRIB_ORIG_VAL
    private String acctAttributeNewValue; // ACCT_ATTRIB_NEW_VAL
    private Timestamp acctChangeDate; // ACCT_CHG_TS
    
    private KualiInteger paymentAccountDetailId;
    private PaymentAccountDetail paymentAccountDetail; // PMT_ACCT_DTL_ID

    public PaymentAccountHistory() {
        super();
    }

    /**
     * @hibernate.many-to-one column="PMT_ACCT_DTL_ID" class="edu.iu.uis.pdp.bo.PaymentAccountHistory"
     * @return Returns the accountDetailId.
     */
    public PaymentAccountDetail getPaymentAccountDetail() {
        return paymentAccountDetail;
    }

    /**
     * @param accountDetailId The accountDetailId to set.
     */
    public void setPaymentAccountDetail(PaymentAccountDetail pad) {
        this.paymentAccountDetail = pad;
    }

    /**
     * @hibernate.id column="PMT_ACCT_HIST_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_ACCT_HIST_ID_SEQ"
     * @return Returns the Id.
     */
    public KualiInteger getId() {
        return id;
    }

    /**
     * @return
     * @hibernate.property column="ACCT_ATTRIB_NM" length="25"
     */
    public String getAcctAttributeName() {
        return acctAttributeName;
    }

    /**
     * @return
     * @hibernate.property column="ACCT_ATTRIB_NEW_VAL" length="15"
     */
    public String getAcctAttributeNewValue() {
        return acctAttributeNewValue;
    }

    /**
     * @return
     * @hibernate.property column="ACCT_ATTRIB_ORIG_VAL" length="15"
     */
    public String getAcctAttributeOrigValue() {
        return acctAttributeOrigValue;
    }

    /**
     * @return
     * @hibernate.many-to-one column="ACCTG_CHG_CD" class="edu.iu.uis.pdp.bo.AccountingChange"
     */
    public AccountingChangeCode getAccountingChange() {
        return accountingChange;
    }

    /**
     * @return
     * @hibernate.property column="ACCT_CHG_TS"
     */
    public Timestamp getAcctChangeDate() {
        return acctChangeDate;
    }

    /**
     * @param string
     */
    public void setAcctAttributeName(String string) {
        acctAttributeName = string;
    }

    /**
     * @param string
     */
    public void setAcctAttributeNewValue(String string) {
        acctAttributeNewValue = string;
    }

    /**
     * @param string
     */
    public void setAcctAttributeOrigValue(String string) {
        acctAttributeOrigValue = string;
    }

    /**
     * @param string
     */
    public void setAccountingChange(AccountingChangeCode ac) {
        accountingChange = ac;
    }

    /**
     * @param timestamp
     */
    public void setAcctChangeDate(Timestamp timestamp) {
        acctChangeDate = timestamp;
    }

    /**
     * @param integer
     */
    public void setId(KualiInteger integer) {
        id = integer;
    }

    /**
     * This method gets the accountingChangeCode
     * @return accountingChangeCode
     */
    public String getAccountingChangeCode() {
        return accountingChangeCode;
    }

    /**
     * This method sets the accountingChangeCode
     * @param accountingChangeCode
     */
    public void setAccountingChangeCode(String accountingChangeCode) {
        this.accountingChangeCode = accountingChangeCode;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        
        m.put(KFSPropertyConstants.ID, this.id);

        return m;
    }
   
}
