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
package org.kuali.kfs.fp.document.service.impl;

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.CashieringTransaction;
import org.kuali.kfs.fp.businessobject.CoinDetail;
import org.kuali.kfs.fp.businessobject.CurrencyDetail;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.CashReceiptFamilyBase;
import org.kuali.kfs.fp.document.dataaccess.CashManagementDao;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSKeyConstants.CashReceipt;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.campus.CampusService;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * This is the default implementation of the CashReceiptService interface.
 */
@Transactional
public class CashReceiptServiceImpl implements CashReceiptService {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashReceiptServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected CashManagementDao cashManagementDao;
    protected CashDrawerService cashDrawerService;
    protected DataDictionaryService dataDictionaryService;
    protected DictionaryValidationService dictionaryValidationService;

    /**
     * This method verifies the campus code provided exists.  This is done by retrieving all the available campuses from
     * the BusinessObjectService and then looking for a matching campus code within the result set.
     *
     * @param campusCode The campus code to be verified.
     * @return True if the campus code provided is valid and exists, false otherwise.
     */
    protected boolean verifyCampus(String campusCode) {
        return SpringContext.getBean(CampusService.class).getCampus(campusCode) != null;
    }


    /**
     * This method retrieves the cash receipt verification unit based on the user provided.  This is done by retrieving the campus
     * code associated with the user provided and then performing the lookup using this campus code.
     *
     * @param user The user to be used to retrieve the verification unit.
     * @return The cash receipt verification unit associated with the user provided.
     *
     * @see org.kuali.kfs.fp.document.service.CashReceiptService#getCashReceiptVerificationUnit(org.kuali.rice.krad.bo.user.KualiUser)
     */
    @Override
    public String getCashReceiptVerificationUnitForUser(Person user) {
        String unitName = null;

        if (user == null) {
            throw new IllegalArgumentException("invalid (null) user");
        }

        return user.getCampusCode();
    }


    /**
     * This method retrieves a collection of cash receipts using the verification unit and the status provided to
     * retrieve the cash receipts.
     *
     * @param verificationUnit The verification unit used to retrieve a collection of associated cash receipts.
     * @param statusCode The status code of the cash receipts to be retrieved.
     * @return A collection of cash receipt documents which match the search criteria provided.
     *
     * @see org.kuali.kfs.fp.document.service.CashReceiptService#getCashReceipts(java.lang.String, java.lang.String)
     */
    @Override
    public List getCashReceipts(String verificationUnit, String statusCode) {
        if (StringUtils.isBlank(statusCode)) {
            throw new IllegalArgumentException("invalid (blank) statusCode");
        }

        String[] statii = new String[] { statusCode };
        return getCashReceipts(verificationUnit, statii);
    }

    /**
     * This method retrieves a collection of cash receipts using the verification unit and the statuses provided to
     * retrieve the cash receipts.
     *
     * @param verificationUnit The verification unit used to retrieve a collection of associated cash receipts.
     * @param statii A collection of possible statuses that will be used in the lookup of cash receipts.
     * @return A collection of cash receipt documents which match the search criteria provided.
     *
     * @see org.kuali.kfs.fp.document.service.CashReceiptService#getCashReceipts(java.lang.String, java.lang.String[])
     */
    @Override
    public List getCashReceipts(String verificationUnit, String[] statii) {
        if (StringUtils.isBlank(verificationUnit)) {
            throw new IllegalArgumentException("invalid (blank) verificationUnit");
        }
        if (statii == null) {
            throw new IllegalArgumentException("invalid (null) statii");
        }
        else {
            if (statii.length == 0) {
                throw new IllegalArgumentException("invalid (empty) statii");
            }
            else {
                for (int i = 0; i < statii.length; ++i) {
                    if (StringUtils.isBlank(statii[i])) {
                        throw new IllegalArgumentException("invalid (blank) status code " + i);
                    }
                }
            }
        }

        return getPopulatedCashReceipts(verificationUnit, statii);
    }

    /**
     * This method retrieves a populated collection of cash receipts using the lookup parameters provided.  A populated
     * cash receipt document is a cash receipt document with fully populated workflow fields.
     *
     * @param verificationUnit The verification unit used to retrieve a collection of associated cash receipts.
     * @param statii A collection of possible statuses that will be used in the lookup of the cash receipts.
     * @return List of CashReceiptDocument instances with their associated workflowDocuments populated.
     */
    public List<CashReceiptDocument> getPopulatedCashReceipts(String verificationUnit, String[] statii) {
        Map queryCriteria = buildCashReceiptCriteriaMap(verificationUnit, statii);

        List<CashReceiptDocument> documents = new ArrayList<CashReceiptDocument>(businessObjectService.findMatchingOrderBy(CashReceiptDocument.class, queryCriteria, KFSPropertyConstants.DOCUMENT_NUMBER, true));

        populateWorkflowFields(documents);

        return documents;
    }


    /**
     * This method builds out a map of search criteria for performing cash receipt lookups using the values provided.
     *
     * @param campusCode The campus code to use as search criteria for looking up cash receipts.
     * @param statii A collection of possible statuses to use as search criteria for looking up cash receipts.
     * @return The search criteria provided in a map with CashReceiptConstants used as keys to the parameters given.
     */
    protected Map buildCashReceiptCriteriaMap(String campusCode, String[] statii) {
        Map queryCriteria = new HashMap();

        if (statii.length == 1) {
            queryCriteria.put(KFSConstants.CashReceiptConstants.CASH_RECEIPT_DOC_HEADER_STATUS_CODE_PROPERTY_NAME, statii[0]);
        }
        else if (statii.length > 0) {
            List<String> statusList = Arrays.asList(statii);
            queryCriteria.put(KFSConstants.CashReceiptConstants.CASH_RECEIPT_DOC_HEADER_STATUS_CODE_PROPERTY_NAME, statusList);
        }

        queryCriteria.put(KFSConstants.CashReceiptConstants.CASH_RECEIPT_CAMPUS_LOCATION_CODE_PROPERTY_NAME, campusCode);

        return queryCriteria;
    }

    /**
     * This method populates the workflowDocument field of each CashReceiptDocument in the given List
     *
     * @param documents A collection of CashReceiptDocuments to be populated with workflow document data.
     */
    protected void populateWorkflowFields(List documents) {
        for (Iterator i = documents.iterator(); i.hasNext();) {
            CashReceiptDocument cr = (CashReceiptDocument) i.next();
            DocumentHeader docHeader = cr.getDocumentHeader();
            WorkflowDocument workflowDocument = WorkflowDocumentFactory.loadDocument(GlobalVariables.getUserSession().getPrincipalId(), docHeader.getDocumentNumber());
    
            docHeader.setWorkflowDocument(workflowDocument);
        }
    }

    /**
     * This method retrieves the cash details from the cash receipt document provided and adds those details to the
     * associated cash drawer.  After the details are added to the drawer, the drawer is persisted to the database.
     *
     * @param crDoc The cash receipt document the cash details will be retrieved from.
     *
     * @see org.kuali.kfs.fp.document.service.CashReceiptService#addCashDetailsToCashDrawer(org.kuali.kfs.fp.document.CashReceiptDocument)
     */
    @Override
    public void addCashDetailsToCashDrawer(CashReceiptDocument crDoc) {
        CashDrawer drawer = retrieveCashDrawer(crDoc);
        // we need to to add the currency and coin to the cash management doc's cumulative CR as well
        if (crDoc.getCurrencyDetail() != null && !crDoc.getCurrencyDetail().isEmpty()) {
            CurrencyDetail cumulativeCurrencyDetail = cashManagementDao.findCurrencyDetailByCashieringStatus(drawer.getReferenceFinancialDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
            cumulativeCurrencyDetail.add(crDoc.getConfirmedCurrencyDetail());
            cumulativeCurrencyDetail.subtract(crDoc.getChangeCurrencyDetail());
            businessObjectService.save(cumulativeCurrencyDetail);

            drawer.addCurrency(crDoc.getConfirmedCurrencyDetail());
            drawer.removeCurrency(crDoc.getChangeCurrencyDetail());
        }
        if (crDoc.getCoinDetail() != null && !crDoc.getCoinDetail().isEmpty()) {
            CoinDetail cumulativeCoinDetail = cashManagementDao.findCoinDetailByCashieringStatus(drawer.getReferenceFinancialDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
            cumulativeCoinDetail.add(crDoc.getConfirmedCoinDetail());
            cumulativeCoinDetail.subtract(crDoc.getChangeCoinDetail());
            businessObjectService.save(cumulativeCoinDetail);

            drawer.addCoin(crDoc.getConfirmedCoinDetail());
            drawer.removeCoin(crDoc.getChangeCoinDetail());
        }
        businessObjectService.save(drawer);
    }

    /**
     * This method finds the appropriate cash drawer for this cash receipt document to add cash to.
     *
     * @param crDoc The document the cash drawer will be retrieved from.
     * @return An instance of a cash drawer associated with the cash receipt document provided.
     */
    protected CashDrawer retrieveCashDrawer(CashReceiptDocument crDoc) {
        String campusCode = crDoc.getCampusLocationCode();
        if (campusCode == null) {
            throw new RuntimeException("Cannot find workgroup name for Cash Receipt document: "+crDoc.getDocumentNumber());
        }

        CashDrawer drawer = cashDrawerService.getByCampusCode(campusCode);
        if (drawer == null) {
            throw new RuntimeException("There is no Cash Drawer for Workgroup "+campusCode);
        }
        return drawer;
    }

    /**
     * @see org.kuali.module.financial.service.CashReceiptTotalsVerificationService#areCashTotalsInvalid(org.kuali.kfs.fp.document.CashReceiptDocument)
     */
    @Override
    public boolean areCashTotalsInvalid(CashReceiptDocument cashReceiptDocument) {
        String documentEntryName = cashReceiptDocument.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

        boolean isInvalid = isTotalInvalid(cashReceiptDocument, cashReceiptDocument.getTotalCheckAmount(), documentEntryName, KFSPropertyConstants.TOTAL_CHECK_AMOUNT);
        isInvalid |= isTotalInvalid(cashReceiptDocument, cashReceiptDocument.getTotalCashAmount(), documentEntryName, KFSPropertyConstants.TOTAL_CASH_AMOUNT);
        isInvalid |= isTotalInvalid(cashReceiptDocument, cashReceiptDocument.getTotalCoinAmount(), documentEntryName, KFSPropertyConstants.TOTAL_COIN_AMOUNT);

        isInvalid |= isTotalInvalid(cashReceiptDocument, cashReceiptDocument.getTotalDollarAmount(), documentEntryName, KFSPropertyConstants.SUM_TOTAL_AMOUNT);

        return isInvalid;
    }

    /**
     * Puts an error message in the error map for that property if the amount is negative.
     *
     * @param cashReceiptDocument submitted cash receipt document
     * @param totalAmount total amount (cash total, check total, etc)
     * @param documentEntryName document type
     * @param propertyName property type (i.e totalCashAmount, totalCheckAmount, etc)
     * @return true if the totalAmount is an invalid value
     */
    protected boolean isTotalInvalid(CashReceiptFamilyBase cashReceiptDocument, KualiDecimal totalAmount, String documentEntryName, String propertyName) {
        boolean isInvalid = false;
        String errorProperty = DOCUMENT_ERROR_PREFIX + propertyName;

        if (totalAmount != null) {
            if (totalAmount.isNegative()) {
                String errorLabel = dataDictionaryService.getAttributeLabel(documentEntryName, propertyName);
                GlobalVariables.getMessageMap().putError(errorProperty, CashReceipt.ERROR_NEGATIVE_TOTAL, errorLabel);

                isInvalid = true;
            } else {
                int precount = GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors();

                dictionaryValidationService.validateDocumentAttribute(cashReceiptDocument, propertyName, DOCUMENT_ERROR_PREFIX);

                String errorLabel = dataDictionaryService.getAttributeLabel(documentEntryName, propertyName);
                // replace generic error message, if any, with something more readable
                GlobalVariables.getMessageMap().replaceError(errorProperty, KFSKeyConstants.ERROR_MAX_LENGTH, CashReceipt.ERROR_EXCESSIVE_TOTAL, errorLabel);

                int postcount = GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors();
                isInvalid = (postcount > precount);
            }
        }

        return isInvalid;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    public void setCashManagementDao(CashManagementDao cashManagementDao) {
        this.cashManagementDao = cashManagementDao;
    }
    public void setCashDrawerService(CashDrawerService cashDrawerService) {
        this.cashDrawerService = cashDrawerService;
    }
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}

