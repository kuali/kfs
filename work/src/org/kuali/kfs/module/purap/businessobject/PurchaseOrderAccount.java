/*
 * Copyright 2006 The Kuali Foundation
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

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Purchase Order Account Business Object.
 */
public class PurchaseOrderAccount extends PurApAccountingLineBase {

    /**
     * NOTE FOR POTENTIAL ACCOUNTING LINE REFACTORING documentNumber is needed for PO accounts and not for other PURAP docs, however
     * it is already defined in AccountingLineBase so we don't need to add it here
     */
    // private String documentNumber;
    private KualiDecimal itemAccountOutstandingEncumbranceAmount;

    /**
     * Default constructor.
     */
    public PurchaseOrderAccount() {
    }

    public PurchaseOrderAccount(PurApAccountingLine ra) {
        this.setAccountLinePercent(ra.getAccountLinePercent());
        this.setAccountNumber(ra.getAccountNumber());
        this.setChartOfAccountsCode(ra.getChartOfAccountsCode());
        this.setFinancialObjectCode(ra.getFinancialObjectCode());
        this.setFinancialSubObjectCode(ra.getFinancialSubObjectCode());
        this.setOrganizationReferenceId(ra.getOrganizationReferenceId());
        this.setProjectCode(ra.getProjectCode());
        this.setSubAccountNumber(ra.getSubAccountNumber());
        this.setSequenceNumber(ra.getSequenceNumber());
        this.setAmount(ra.getAmount());
        this.setAccountLinePercent(ra.getAccountLinePercent());
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApAccountingLine#getAlternateAmountForGLEntryCreation()
     */
    @Override
    public KualiDecimal getAlternateAmountForGLEntryCreation() {
        if (ObjectUtils.isNull(super.getAlternateAmountForGLEntryCreation())) {
            return getItemAccountOutstandingEncumbranceAmount();
        }
        return super.getAlternateAmountForGLEntryCreation();
    }

    public KualiDecimal getItemAccountOutstandingEncumbranceAmount() {
        return itemAccountOutstandingEncumbranceAmount;
    }

    public void setItemAccountOutstandingEncumbranceAmount(KualiDecimal itemAccountOutstandingEncumbranceAmount) {
        this.itemAccountOutstandingEncumbranceAmount = itemAccountOutstandingEncumbranceAmount;
    }

    public PurchaseOrderItem getPurchaseOrderItem() {
        return super.getPurapItem();
    }

    /**
     * Sets the purchaseOrderItem attribute value.
     *
     * @param purchaseOrderItem The purchaseOrderItem to set.
     * @deprecated
     */
    @Deprecated
    public void setPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
        super.setPurapItem(purchaseOrderItem);
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#copyFrom(org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    public void copyFrom(AccountingLine other) {
        super.copyFrom(other);
        if (other instanceof PurchaseOrderAccount) {
            PurchaseOrderAccount poOther = (PurchaseOrderAccount)other;
            setItemAccountOutstandingEncumbranceAmount(poOther.getItemAccountOutstandingEncumbranceAmount());
        }
    }

    /**
     * Checks if the amount in this PurchaseOrderAccount has the same value as the specified one.
     *
     * @param poAccount
     * @return
     */
    public boolean isAmountLike(PurchaseOrderAccount poAccount) {
        if (poAccount == null) {
            return false;
        }
        if (amount == null && poAccount.getAmount() == null) {
            return true;
        }
        if (amount == null && poAccount.getAmount() != null) {
            return false;
        }
        if (amount != null && poAccount.getAmount() == null) {
            return false;
        }
        return amount.compareTo(poAccount.getAmount()) == 0;
    }

}
