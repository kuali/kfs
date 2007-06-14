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
package org.kuali.module.purap.rules;

import static org.kuali.kfs.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.kfs.KFSConstants.MONTH1;
import static org.kuali.module.purap.PurapConstants.PO_DOC_TYPE_CODE;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.validation.fieldlevel.ZipcodeValidationPattern;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.workflow.KualiWorkflowUtils.RouteLevelNames;

public class PurchaseOrderDocumentRule extends PurchasingDocumentRuleBase {

    /**
     * Tabs included on Purchase Order Documents are: Stipulation
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processAdditionalValidation((PurchasingDocument) purapDocument);
        valid &= processVendorStipulationValidation((PurchaseOrderDocument) purapDocument);
        return valid;
    }

    /**
     * This method performs any validation for the Additional tab.
     * 
     * @param purDocument
     * @return
     */
    public boolean processAdditionalValidation(PurchasingDocument purDocument) {
        boolean valid = true;
        valid = validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(purDocument);
        return valid;
    }

    /**
     * @see org.kuali.module.purap.rules.PurchasingDocumentRuleBase#processItemValidation(org.kuali.module.purap.document.PurchasingDocument)
     */
    @Override
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processItemValidation(purapDocument);
        for (PurchasingApItem item : purapDocument.getItems()) {
            String identifierString = (item.getItemType().isItemTypeAboveTheLineIndicator() ? "Item " + item.getItemLineNumber().toString() : item.getItemType().getItemTypeDescription());
            valid &= validateEmptyItemWithAccounts((PurchaseOrderItem) item, identifierString);
            valid &= validateItemWithoutAccounts((PurchaseOrderItem) item, identifierString);
            valid &= validateItemUnitOfMeasure((PurchaseOrderItem) item, identifierString);
            if (purapDocument.getDocumentHeader().getWorkflowDocument() != null && purapDocument.getDocumentHeader().getWorkflowDocument().getDocumentType().equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT)) {
                valid &= validateItemForAmendment((PurchaseOrderItem) item, identifierString);
            }
        }
        valid &= validateTradeInAndDiscountCoexistence((PurchasingDocument)purapDocument);
        return valid;
    }

    private boolean validateItemForAmendment(PurchaseOrderItem item, String identifierString) {
        boolean valid = true;
        if ((item.getItemInvoicedTotalQuantity() != null) && (!(item.getItemInvoicedTotalQuantity()).isZero())) {
            if (item.getItemQuantity() == null) {
                valid = false;
                GlobalVariables.getErrorMap().putError("newPurchasingItemLine" , PurapKeyConstants.ERROR_ITEM_AMND_NULL, "Item Quantity", identifierString);
            }
            else if (item.getItemQuantity().compareTo(item.getItemInvoicedTotalQuantity()) < 0) {
                valid = false;
                GlobalVariables.getErrorMap().putError("newPurchasingItemLine" , PurapKeyConstants.ERROR_ITEM_AMND_INVALID, "Item Quantity", identifierString);
            }
        }

        if (item.getItemInvoicedTotalAmount() != null) {
            KualiDecimal total = item.getExtendedPrice();
            if ((total == null) || total.compareTo(item.getItemInvoicedTotalAmount()) < 0) {
                valid = false;
                GlobalVariables.getErrorMap().putError("newPurchasingItemLine" , PurapKeyConstants.ERROR_ITEM_AMND_INVALID_AMT, "Item Extended Price", identifierString);
            }
        }
        return valid;
    }

    /**
     * This method validates that the item detail must not be empty if its account is not empty and its item type is ITEM.
     * 
     * @param item
     * @return
     */
    boolean validateEmptyItemWithAccounts(PurchaseOrderItem item, String identifierString) {
        boolean valid = true;
        if (item.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE) && item.isItemDetailEmpty() && !item.isAccountListEmpty()) {
            valid = false;
            GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_ACCOUNTING_NOT_ALLOWED, identifierString);
        }
        return valid;
    }

    /**
     * This method validates that the item must contain at least one account
     * 
     * @param item
     * @return
     */
    boolean validateItemWithoutAccounts(PurchaseOrderItem item, String identifierString) {
        boolean valid = true;
        if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isNonZero() && item.isAccountListEmpty()) {
            valid = false;
            GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_ACCOUNTING_INCOMPLETE, identifierString);
        }
        return valid;
    }

    /**
     * This method validates that if the item type is ITEM, the unit of measure field is required.
     * 
     * @param item
     * @return
     */
    boolean validateItemUnitOfMeasure(PurchaseOrderItem item, String identifierString) {
        boolean valid = true;
        if (item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ITEM_CODE) && StringUtils.isEmpty(item.getItemUnitOfMeasureCode())) {
            valid = false;
            GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_UNIT_OF_MEASURE_REQUIRED, identifierString);
        }
        return valid;
    }

    /**
     * This method validates that the purchase order cannot have both trade in and discount item.
     * 
     * @param purDocument
     * @return
     */
    boolean validateTradeInAndDiscountCoexistence(PurchasingDocument purDocument) {
        boolean discountExists = false;
        boolean tradeInExists = false;

        for (PurchasingApItem item : purDocument.getItems()) {
            if (item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) {
                discountExists = true;
                if (tradeInExists) {
                    GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_TRADEIN_DISCOUNT_COEXISTENCE);
                    return false;
                }
            }
            else if (item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)) {
                tradeInExists = true;
                if (discountExists) {
                    GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_TRADEIN_DISCOUNT_COEXISTENCE);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method performs any validation for the Stipulation tab.
     * 
     * @param poDocument
     * @return
     */
    public boolean processVendorStipulationValidation(PurchaseOrderDocument poDocument) {
        boolean valid = true;
        List<PurchaseOrderVendorStipulation> stipulations = poDocument.getPurchaseOrderVendorStipulations();
        for (int i = 0; i < stipulations.size(); i++) {
            PurchaseOrderVendorStipulation stipulation = stipulations.get(i);
            if (StringUtils.isBlank(stipulation.getVendorStipulationDescription())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_STIPULATION + "[" + i + "]." + PurapPropertyConstants.VENDOR_STIPULATION_DESCRIPTION, PurapKeyConstants.ERROR_STIPULATION_DESCRIPTION);
                valid = false;
            }
        }
        return valid;
    }

    @Override
    public boolean processVendorValidation(PurchasingAccountsPayableDocument purapDocument) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        boolean valid = super.processVendorValidation(purapDocument);
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) purapDocument;
        if (StringUtils.isBlank(poDocument.getVendorCountryCode())) {
            // TODO can't this be done by the data dictionary?
            valid = false;
            errorMap.putError(PurapPropertyConstants.VENDOR_COUNTRY_CODE, KFSKeyConstants.ERROR_REQUIRED);
        }
        else if (poDocument.getVendorCountryCode().equals(KFSConstants.COUNTRY_CODE_UNITED_STATES)) {
            if (StringUtils.isBlank(poDocument.getVendorStateCode())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.VENDOR_STATE_CODE, KFSKeyConstants.ERROR_REQUIRED_FOR_US);
            }
            ZipcodeValidationPattern zipPattern = new ZipcodeValidationPattern();
            if (StringUtils.isBlank(poDocument.getVendorPostalCode())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.VENDOR_POSTAL_CODE, KFSKeyConstants.ERROR_REQUIRED_FOR_US);
            }
            else if (!zipPattern.matches(poDocument.getVendorPostalCode())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.VENDOR_POSTAL_CODE, PurapKeyConstants.ERROR_POSTAL_CODE_INVALID);
            }
        }
        return valid;
    }

    // TODO check comments; mentions REQ, but this class performs only PO validation
    /**
     * Validate that if Vendor Id (VendorHeaderGeneratedId) is not empty, and tranmission method is fax, vendor fax number cannot be
     * empty and must be valid. In other words: allow reqs to not force fax # when transmission type is fax if vendor id is empty
     * because it will not be allowed to become an APO and it will be forced on the PO.
     * 
     * @return False if VendorHeaderGeneratedId is not empty, tranmission method is fax, and VendorFaxNumber is empty or invalid.
     *         True otherwise.
     */
    private boolean validateFaxNumberIfTransmissionTypeIsFax(PurchasingDocument purDocument) {
        boolean valid = true;
        if (ObjectUtils.isNotNull(purDocument.getVendorHeaderGeneratedIdentifier()) && purDocument.getPurchaseOrderTransmissionMethodCode().equals(PurapConstants.POTransmissionMethods.FAX)) {
            if (ObjectUtils.isNull(purDocument.getVendorFaxNumber()) || !SpringServiceLocator.getPhoneNumberService().isValidPhoneNumber(purDocument.getVendorFaxNumber())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.REQUISITION_VENDOR_FAX_NUMBER, PurapKeyConstants.ERROR_FAX_NUMBER_PO_TRANSMISSION_TYPE);
                valid &= false;
            }
        }
        return valid;
    }

    /**
     * Validate that if the PurchaseOrderTotalLimit is not null then the TotalDollarAmount cannot be greater than the
     * PurchaseOrderTotalLimit.
     * 
     * @return True if the TotalDollarAmount is less than the PurchaseOrderTotalLimit. False otherwise.
     */
    public boolean validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(PurchasingDocument purDocument) {
        boolean valid = true;
        if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderTotalLimit()) && ObjectUtils.isNotNull(((AmountTotaling) purDocument).getTotalDollarAmount())) {
            KualiDecimal totalAmount = ((AmountTotaling) purDocument).getTotalDollarAmount();
            if (((AmountTotaling) purDocument).getTotalDollarAmount().isGreaterThan(purDocument.getPurchaseOrderTotalLimit())) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_TOTAL_LIMIT, PurapKeyConstants.REQ_TOTAL_GREATER_THAN_PO_TOTAL_LIMIT);
            }
        }
        return valid;
    }

    @Override
    protected boolean checkAccountingLineAccountAccessibility(AccountingDocument financialDocument, AccountingLine accountingLine, AccountingLineAction action) {
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();
        List currentRouteLevels = getCurrentRouteLevels(workflowDocument);

        if (currentRouteLevels.contains(RouteLevelNames.PURCHASE_ORDER_INTERNAL_REVIEW) && workflowDocument.isApprovalRequested()) {
            // DO NOTHING: do not check that user owns acct lines; at this level, approvers can edit all detail on PO
            return true;
        }
        
        else {
            return super.checkAccountingLineAccountAccessibility(financialDocument, accountingLine, action);
        }
    }

    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(accountingDocument, accountingLine, explicitEntry);
        PurchaseOrderDocument po = (PurchaseOrderDocument)accountingDocument;

        purapCustomizeGeneralLedgerPendingEntry(po, accountingLine, explicitEntry, po.getPurapDocumentIdentifier(), GL_DEBIT_CODE, true);
        
        explicitEntry.setTransactionLedgerEntryDescription(entryDescription(po.getVendorName()));
        explicitEntry.setFinancialDocumentTypeCode(PO_DOC_TYPE_CODE);  //don't think i should have to override this, but default isn't getting the right PO doc
        
        UniversityDate uDate = SpringServiceLocator.getUniversityDateService().getCurrentUniversityDate();
        if (po.getPostingYear().compareTo(uDate.getUniversityFiscalYear()) > 0) {
            //USE NEXT AS SET ON PO; POs can be forward dated to not encumber until next fiscal year
            explicitEntry.setUniversityFiscalYear(po.getPostingYear());
            explicitEntry.setUniversityFiscalPeriodCode(MONTH1);
        }
        else {
            //USE CURRENT; don't use FY on PO in case it's a prior year
            explicitEntry.setUniversityFiscalYear(uDate.getUniversityFiscalYear());
            explicitEntry.setUniversityFiscalPeriodCode(uDate.getUniversityFiscalAccountingPeriod());
            //TODO do we need to update the doc posting year?
        }
    }


}
