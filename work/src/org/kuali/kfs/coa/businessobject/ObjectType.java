/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 *
 */
public class ObjectType extends KualiCodeBase implements MutableInactivatable {

    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "ObjectType";

    protected String finObjectTypeDebitcreditCd;
    protected boolean finObjectTypeIcrSelectionIndicator;
    protected boolean fundBalanceIndicator;
    protected String financialReportingSortCode;
    protected String basicAccountingCategoryCode;

    protected BasicAccountingCategory basicAccountingCategory;

    /**
     * Gets the finObjectTypeDebitcreditCd attribute.
     *
     * @return Returns the finObjectTypeDebitcreditCd
     */
    public String getFinObjectTypeDebitcreditCd() {
        return finObjectTypeDebitcreditCd;
    }

    /**
     * Sets the finObjectTypeDebitcreditCd attribute.
     *
     * @param finObjectTypeDebitcreditCd The finObjectTypeDebitcreditCd to set.
     */
    public void setFinObjectTypeDebitcreditCd(String finObjectTypeDebitcreditCd) {
        this.finObjectTypeDebitcreditCd = finObjectTypeDebitcreditCd;
    }

    /**
     * Gets the finObjectTypeIcrSelectionIndicator attribute.
     *
     * @return Returns the finObjectTypeIcrSelectionIndicator
     */
    public boolean isFinObjectTypeIcrSelectionIndicator() {
        return finObjectTypeIcrSelectionIndicator;
    }

    /**
     * Sets the finObjectTypeIcrSelectionIndicator attribute.
     *
     * @param finObjectTypeIcrSelectionIndicator The finObjectTypeIcrSelectionIndicator to set.
     */
    public void setFinObjectTypeIcrSelectionIndicator(boolean finObjectTypeIcrSelectionIndicator) {
        this.finObjectTypeIcrSelectionIndicator = finObjectTypeIcrSelectionIndicator;
    }

    /**
     * Gets the fundBalanceIndicator attribute.
     *
     * @return Returns the fundBalanceIndicator
     */
    public boolean isFundBalanceIndicator() {
        return fundBalanceIndicator;
    }

    /**
     * Sets the fundBalanceIndicator attribute.
     *
     * @param fundBalanceIndicator The fundBalanceIndicator to set.
     */
    public void setFundBalanceIndicator(boolean fundBalanceIndicator) {
        this.fundBalanceIndicator = fundBalanceIndicator;
    }

    /**
     * Gets the financialReportingSortCode attribute.
     *
     * @return Returns the financialReportingSortCode
     */
    public String getFinancialReportingSortCode() {
        return financialReportingSortCode;
    }

    /**
     * Sets the financialReportingSortCode attribute.
     *
     * @param financialReportingSortCode The financialReportingSortCode to set.
     */
    public void setFinancialReportingSortCode(String financialReportingSortCode) {
        this.financialReportingSortCode = financialReportingSortCode;
    }

    /**
     * Gets the accountCategoryCode attribute.
     *
     * @return Returns the accountCategoryCode.
     */
    public String getBasicAccountingCategoryCode() {
        return basicAccountingCategoryCode;
    }

    /**
     * Sets the accountCategoryCode attribute value.
     *
     * @param accountCategoryCode The accountCategoryCode to set.
     */
    public void setBasicAccountingCategoryCode(String accountCategoryCode) {
        this.basicAccountingCategoryCode = accountCategoryCode;
    }

    /**
     * Gets the basicAccountingCategory attribute.
     *
     * @return Returns the basicAccountingCategory.
     */
    public BasicAccountingCategory getBasicAccountingCategory() {
        return basicAccountingCategory;
    }

    /**
     * Sets the basicAccountingCategory attribute value.
     *
     * @param basicAccountingCategory The basicAccountingCategory to set.
     * @deprecated
     */
    @Deprecated
    public void setBasicAccountingCategory(BasicAccountingCategory basicAccountingCategory) {
        this.basicAccountingCategory = basicAccountingCategory;
    }

}
