/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.businessobject;

import java.io.Serializable;

import org.kuali.rice.core.api.util.type.KualiDecimal;


public interface PurApItemUseTax extends Serializable {

    public String getAccountNumber();

    public void setAccountNumber(String accountNumber);

    public String getChartOfAccountsCode();

    public void setChartOfAccountsCode(String chartOfAccountsCode);

    public String getFinancialObjectCode();

    public void setFinancialObjectCode(String financialObjectCode);

    public Integer getItemIdentifier();

    public void setItemIdentifier(Integer itemIdentifier);

    public Integer getUseTaxId();

    public void setUseTaxId(Integer useTaxId);

    public String getRateCode();

    public void setRateCode(String rateCode);

    public KualiDecimal getTaxAmount();

    public void setTaxAmount(KualiDecimal taxAmount);

}
