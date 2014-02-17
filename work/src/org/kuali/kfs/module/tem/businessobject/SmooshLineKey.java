/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Hash key of lines we want to smoosh
 */
public class SmooshLineKey {
    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String subAccountNumber;

    public SmooshLineKey(TemSourceAccountingLine accountingLine) {
        this.chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        this.accountNumber = accountingLine.getAccountNumber();
        this.subAccountNumber = accountingLine.getSubAccountNumber();
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(getChartOfAccountsCode());
        hcb.append(getAccountNumber());
        hcb.append(getSubAccountNumber());
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SmooshLineKey) || obj == null) {
            return false;
        }
        final SmooshLineKey golyadkin = (SmooshLineKey)obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(getChartOfAccountsCode(), golyadkin.getChartOfAccountsCode());
        eb.append(getAccountNumber(), golyadkin.getAccountNumber());
        eb.append(getSubAccountNumber(), golyadkin.getSubAccountNumber());
        return eb.isEquals();
    }
}
