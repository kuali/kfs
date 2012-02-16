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
package org.kuali.kfs.integration.ar;

import java.util.List;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.util.AbstractKualiDecimal;
import org.kuali.rice.kns.util.KualiDecimal;

public interface AccountsReceivablePaymentApplicationDocument extends ExternalizableBusinessObject {

    public void setCashControlDetail(AccountsReceivableCashControlDetail newCashControlDetail);

    public String getDocumentNumber();

    public void setAccountsReceivableDocumentHeader(AccountsRecievableDocumentHeader arDocHeader);

    public FinancialSystemDocumentHeader getDocumentHeader();

    public void refreshNonUpdateableReferences();

    public AccountsRecievableDocumentHeader getAccountsReceivableDocumentHeader();

    public KualiDecimal getTotalFromControl();

    public List<AccountsReceivableNonInvoiced> getAccountsReceivableNonInvoiceds();
    
    public void setAccountsReceivableNonInvoiceds(List<AccountsReceivableNonInvoiced> accountsReceivableNonInvoiceds);

    public Integer getPostingYear();

    public KualiDecimal getSumOfNonInvoiceds();

    public List<AccountsReceivableInvoicePaidApplied> getAccountsReceivableInvoicePaidApplieds();

    public void setAccountsReceivableInvoicePaidApplieds(List<AccountsReceivableInvoicePaidApplied> accountsReceivableInvoicePaidApplieds);
    
    public AccountsReceivableCashControlDetail getAccountsReceivableCashControlDetail();

}
