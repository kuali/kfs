/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import static org.kuali.kfs.module.endow.EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.batch.service.CreateAccrualTransactionsService;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.dataaccess.SecurityDao;
import org.kuali.kfs.module.endow.document.CashIncreaseDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageMap;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class CreateAccrualTransactionsServiceImpl implements CreateAccrualTransactionsService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateAccrualTransactionsServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private KEMService kemService;
    private HoldingTaxLotService holdingTaxLotService;
    private SecurityDao securityDao;
    private DocumentService documentService;
    private KualiConfigurationService configService;
    private KualiRuleService kualiRuleService;

    /**
     * @see org.kuali.kfs.module.endow.batch.service.CreateAccrualTransactionsService#createAccrualTransactions()
     */
    public boolean createAccrualTransactions() {
        boolean success = true;
        List<Security> securities = getAllSecuritiesWithNextPayDateEqualCurrentDate();

        for (Security security : securities) {

            Iterator iter = holdingTaxLotService.getAllTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(security.getId());

            if (iter.hasNext()) {

                // 4. create new CashIncreaseDocument

                CashIncreaseDocument cashIncreaseDocument = createNewCashIncreaseDocument(security.getId());

                // keep a counter to create a new document if there are more that 100 transaction lines
                int counter = 0;
                try {
                    while (iter.hasNext()) {

                        // if we have already 100 transaction lines on the current document then create a new document
                        if (counter == 100) {
                            // TODO figure out if/how we use the ad hoc recipients list

                            documentService.routeDocument(cashIncreaseDocument, "Created by Accrual Transactions Barch process.", null);

                            cashIncreaseDocument = createNewCashIncreaseDocument(security.getId());
                            counter = 0;
                        }
                        Object[] collectionEntry = (Object[]) iter.next();
                        String registrationCode = collectionEntry[0].toString();
                        String kemid = collectionEntry[1].toString();
                        BigDecimal totalAccruedIncome = new BigDecimal(collectionEntry[2].toString());

                        // set the registration code
                        if (cashIncreaseDocument.getTargetTransactionSecurity().getRegistrationCode() == null) {
                            cashIncreaseDocument.getTargetTransactionSecurity().setRegistrationCode(registrationCode);
                        }

                        // Create a new transaction line
                        EndowmentTransactionLine endowmentTransactionLine = new EndowmentTargetTransactionLine();
                        endowmentTransactionLine.setTransactionLineNumber(counter + 1);
                        endowmentTransactionLine.setDocumentNumber(cashIncreaseDocument.getDocumentNumber());
                        endowmentTransactionLine.setKemid(kemid);
                        endowmentTransactionLine.setEtranCode(security.getClassCode().getSecurityIncomeEndowmentTransactionPostCode());
                        endowmentTransactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.INCOME);
                        // endowmentTransactionLine.setTransactionAmount(new KualiDecimal(totalAccruedIncome));

                        // TODO add transaction line validation
                        boolean rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(NEW_TARGET_TRAN_LINE_PROPERTY_NAME, cashIncreaseDocument, endowmentTransactionLine));
                        // cashIncreaseDocument.validateBusinessRules(new
                        // AddTransactionLineEvent(NEW_TARGET_TRAN_LINE_PROPERTY_NAME, cashIncreaseDocument,
                        // endowmentTransactionLine));

                        if (rulesPassed) {
                            cashIncreaseDocument.getTargetTransactionLines().add(endowmentTransactionLine);
                        }

                        counter++;

                    }
                    // documentService.routeDocument(cashIncreaseDocument, "Created by Accrual Transactions Barch process.", null);
                    documentService.saveDocument(cashIncreaseDocument);
                }
                catch (WorkflowException ex) {
                    throw new RuntimeException("WorkflowException while routing a CashIncreaseDocument for Accrual Transactions batch process.", ex);
                }

            }
        }

        return success;
    }

    protected boolean extractGlobalVariableErrors() {
        boolean result = true;

        MessageMap errorMap = GlobalVariables.getMessageMap();

        Set<String> errorKeys = errorMap.keySet();
        List<ErrorMessage> errorMessages = null;
        Object[] messageParams;
        String errorKeyString;
        String errorString;

        for (String errorProperty : errorKeys) {
            errorMessages = (List<ErrorMessage>) errorMap.get(errorProperty);
            for (ErrorMessage errorMessage : errorMessages) {
                errorKeyString = configService.getPropertyString(errorMessage.getErrorKey());
                messageParams = errorMessage.getMessageParameters();

                // MessageFormat.format only seems to replace one
                // per pass, so I just keep beating on it until all are gone.
                if (StringUtils.isBlank(errorKeyString)) {
                    errorString = errorMessage.getErrorKey();
                }
                else {
                    errorString = errorKeyString;
                }
                while (errorString.matches("^.*\\{\\d\\}.*$")) {
                    errorString = MessageFormat.format(errorString, messageParams);
                }
                // batchErrors.addError(customerName, errorProperty, Object.class, "", errorString);
                result = false;
            }
        }

        // clear the stuff out of globalvars, as we need to reformat it and put it back
        GlobalVariables.getMessageMap().clear();
        return result;
    }

    /**
     * Gets the CashIncreaseDocument type.
     * 
     * @return the CashIncreaseDocument type
     */
    private String getCashIncreaseDocumentType() {
        return "ECI";
    }

    /**
     * Creates a new CashIncreaseDocument with source type Automated, transaction sub-type Cash, target security id set to the input
     * security id.
     * 
     * @param securityId
     * @return a new CashIncreaseDocument
     */
    private CashIncreaseDocument createNewCashIncreaseDocument(String securityId) {
        try {
            CashIncreaseDocument cashIncreaseDocument = (CashIncreaseDocument) documentService.getNewDocument(getCashIncreaseDocumentType());
            cashIncreaseDocument.getDocumentHeader().setDocumentDescription(EndowConstants.ACCRUAL_TRANSACTIONS_CASH_INCREASE_DOC_DESC);
            cashIncreaseDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);
            cashIncreaseDocument.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);

            // set security details
            EndowmentTransactionSecurity targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
            targetTransactionSecurity.setSecurityID(securityId);
            cashIncreaseDocument.setTargetTransactionSecurity(targetTransactionSecurity);

            return cashIncreaseDocument;
        }
        catch (WorkflowException ex) {
            throw new RuntimeException("WorkflowException while creating a CashIncreaseDocument for Accrual Transactions.", ex);
        }
    }

    public CreateAccrualTransactionsServiceImpl() {

    }

    /**
     * Locates all Security records for which the next income pay date is equal to the current date.
     * 
     * @return
     */
    private List<Security> getAllSecuritiesWithNextPayDateEqualCurrentDate() {
        List<Security> result = new ArrayList<Security>();

        result = securityDao.getAllSecuritiesWithNextPayDateEqualCurrentDate();

        return result;
    }

    private List<HoldingTaxLot> getAllTaxLotsWithHoldingAccruedIncomeGreaterThanZero(String securityId) {
        List<HoldingTaxLot> result = new ArrayList<HoldingTaxLot>();


        return result;
    }

    /**
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the kemService.
     * 
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Sets the holdingTaxLotService.
     * 
     * @param holdingTaxLotService
     */
    public void setHoldingTaxLotService(HoldingTaxLotService holdingTaxLotService) {
        this.holdingTaxLotService = holdingTaxLotService;
    }

    /**
     * Sets the securityDao.
     * 
     * @param securityDao
     */
    public void setSecurityDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }

    /**
     * Sets the documenyService.
     * 
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the configService.
     * 
     * @param configService
     */
    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }

    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }
}
