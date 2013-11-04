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

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.KualiCodeBase;

public class AgreementStatus extends KualiCodeBase {
    
    private String defaultTransactionRestrictionCode;    
    private TransactionRestrictionCode transactionRestrictionCode;

    /**
     * Gets the defaultTransactionRestrictionCode attribute. 
     * @return Returns the defaultTransactionRestrictionCode.
     */
    public String getDefaultTransactionRestrictionCode() {
        return defaultTransactionRestrictionCode;
    }

    /**
     * Sets the defaultTransactionRestrictionCode attribute value.
     * @param defaultTransactionRestrictionCode The defaultTransactionRestrictionCode to set.
     */
    public void setDefaultTransactionRestrictionCode(String defaultTransactionRestrictionCode) {
        this.defaultTransactionRestrictionCode = defaultTransactionRestrictionCode;
    }

    /**
     * Gets the transactionRestrictionCode attribute. 
     * @return Returns the transactionRestrictionCode.
     */
    public TransactionRestrictionCode getTransactionRestrictionCode() {
        return transactionRestrictionCode;
    }

    /**
     * Sets the transactionRestrictionCode attribute value.
     * @param transactionRestrictionCode The transactionRestrictionCode to set.
     */
    public void setTransactionRestrictionCode(TransactionRestrictionCode transactionRestrictionCode) {
        this.transactionRestrictionCode = transactionRestrictionCode;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KUALICODEBASE_CODE, super.code);
        return m;
    }
    
    /**
     * @see org.kuali.rice.krad.bo.KualiCodeBase#getCodeAndDescription()
     */
    @Override
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(code)) {
            return KFSConstants.EMPTY_STRING;
        }
        return super.getCodeAndDescription();
    }

}
