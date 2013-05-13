/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentAvailableBalance;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument;
import org.kuali.kfs.module.endow.document.SecurityTransferDocument;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionDocumentService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionLinesDocumentService;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule;
import org.kuali.kfs.module.endow.document.validation.DeleteTransactionLineRule;
import org.kuali.kfs.module.endow.document.validation.RefreshTransactionLineRule;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.AbstractKualiDecimal;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class EndowmentTransactionLinesDocumentBaseRules extends EndowmentTransactionalDocumentBaseRule implements AddTransactionLineRule<EndowmentTransactionLinesDocument, EndowmentTransactionLine>, DeleteTransactionLineRule<EndowmentTransactionLinesDocument, EndowmentTransactionLine>, RefreshTransactionLineRule<EndowmentTransactionLinesDocument, EndowmentTransactionLine, Number> {


    /**
     * @see org.kuali.kfs.module.endow.document.validation.DeleteTransactionLineRule#processDeleteTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processDeleteTransactionLineRules(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine endowmentTransactionLine) {
        return true;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument document, EndowmentTransactionLine line) {
        return validateTransactionLine(document, line, -1);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.RefreshTransactionLineRule#processRefreshTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, java.lang.Number)
     */
    @Override
    public boolean processRefreshTransactionLineRules(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine endowmentTransactionLine, Number index) {
        return validateTransactionLine((EndowmentTransactionLinesDocumentBase) endowmentTransactionLinesDocument, endowmentTransactionLine, (Integer) index);
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        EndowmentTransactionLinesDocumentBase endowmentTransactionLinesDocumentBase = null;

        if (isValid) {
            endowmentTransactionLinesDocumentBase = (EndowmentTransactionLinesDocumentBase) document;

            // validate source transaction lines
            if (endowmentTransactionLinesDocumentBase.getSourceTransactionLines() != null) {
                for (int i = 0; i < endowmentTransactionLinesDocumentBase.getSourceTransactionLines().size(); i++) {
                    EndowmentTransactionLine transactionLine = endowmentTransactionLinesDocumentBase.getSourceTransactionLines().get(i);
                    validateTransactionLine(endowmentTransactionLinesDocumentBase, transactionLine, i);
                }
            }

            // validate target transaction lines
            if (endowmentTransactionLinesDocumentBase.getTargetTransactionLines() != null) {
                for (int i = 0; i < endowmentTransactionLinesDocumentBase.getTargetTransactionLines().size(); i++) {
                    EndowmentTransactionLine transactionLine = endowmentTransactionLinesDocumentBase.getTargetTransactionLines().get(i);
                    validateTransactionLine(endowmentTransactionLinesDocumentBase, transactionLine, i);
                }
            }

        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * This method obtains Prefix for Error fields in UI.
     *
     * @param line
     * @param index
     * @return
     */
    public String getErrorPrefix(EndowmentTransactionLine line, int index) {
        String ERROR_PREFIX = null;
        if (line instanceof EndowmentSourceTransactionLine) {
            if (index == -1) {
                ERROR_PREFIX = EndowPropertyConstants.SOURCE_TRANSACTION_LINE_PREFIX;
            }
            else {
                ERROR_PREFIX = EndowPropertyConstants.EXISTING_SOURCE_TRANSACTION_LINE_PREFIX + "[" + index + "].";
            }
        }
        else {
            if (index == -1) {
                ERROR_PREFIX = EndowPropertyConstants.TARGET_TRANSACTION_LINE_PREFIX;
            }
            else {
                ERROR_PREFIX = EndowPropertyConstants.EXISTING_TARGET_TRANSACTION_LINE_PREFIX + "[" + index + "].";
            }
        }
        return ERROR_PREFIX;

    }

    /**
     * This method obtains security code from a document.
     *
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @return
     */
    public String getSecurityIDForValidation(EndowmentTransactionLinesDocument endowmentTransactionLinesDocumentBase, boolean isSource) {
        EndowmentSecurityDetailsDocumentBase document = (EndowmentSecurityDetailsDocumentBase) endowmentTransactionLinesDocumentBase;
        if (isSource) {
            return document.getSourceTransactionSecurity().getSecurityID();
        }
        else {
            return document.getTargetTransactionSecurity().getSecurityID();
        }
    }

    /**
     * This method obtains Registration code from a document.
     *
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @return
     */
    public String getRegistrationForValidation(EndowmentTransactionLinesDocument endowmentTransactionLinesDocumentBase, boolean isSource) {
        EndowmentSecurityDetailsDocumentBase document = (EndowmentSecurityDetailsDocumentBase) endowmentTransactionLinesDocumentBase;
        if (isSource) {
            return document.getSourceTransactionSecurity().getRegistrationCode();
        }
        else {
            return document.getTargetTransactionSecurity().getRegistrationCode();
        }
    }

    /**
     * This method obtains security from a document.
     *
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @return
     */
    public Security getSecurityForValidation(EndowmentTransactionLinesDocument endowmentTransactionLinesDocumentBase, boolean isSource) {
        EndowmentSecurityDetailsDocumentBase document = (EndowmentSecurityDetailsDocumentBase) endowmentTransactionLinesDocumentBase;
        if (isSource) {
            return document.getSourceTransactionSecurity().getSecurity();
        }
        else {
            return document.getTargetTransactionSecurity().getSecurity();
        }
    }

    /**
     * This method validates a transaction line.
     *
     * @param line
     * @param index
     * @return
     */
    protected boolean validateTransactionLine(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, int index) {
        boolean isValid = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(line);

        isValid &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        String ERROR_PREFIX = getErrorPrefix(line, index);

        if (isValid) {
            GlobalVariables.getMessageMap().clearErrorPath();

            // General not null validation for KemID
            SpringContext.getBean(DictionaryValidationService.class).validateAttributeRequired(line.getClass().getName(), "kemid", line.getKemid(), false, ERROR_PREFIX + EndowPropertyConstants.KEMID);

            // Validate KemID
            if (!validateKemId(line, ERROR_PREFIX)) {
                return false;
            }

            // Active Kemid
            isValid &= isActiveKemId(line, ERROR_PREFIX);

            // Validate no restriction transaction restriction
            isValid &= validateNoTransactionRestriction(line, ERROR_PREFIX);

            // Validate Income/Principal DropDown
            SpringContext.getBean(DictionaryValidationService.class).validateAttributeRequired(line.getClass().getName(), "transactionIPIndicatorCode", line.getTransactionIPIndicatorCode(), false, ERROR_PREFIX + EndowPropertyConstants.TRANSACTION_IPINDICATOR);
            isValid &= GlobalVariables.getMessageMap().getErrorCount() == 0 ? true : false;
            if (!isValid) {
                return isValid;
            }

            // This error is checked in addition save rule method since the sub type is used for determining chart code.
            if (!isSubTypeEmpty(endowmentTransactionLinesDocument)) {
                return false;
            }

            // If non-cash transactions
            if (nonCashTransaction(endowmentTransactionLinesDocument) && hasEtranCode(endowmentTransactionLinesDocument)) {
                // Is Etran code empty
                if (isEndowmentTransactionCodeEmpty(line, ERROR_PREFIX)) {
                    return false;
                }

                // Validate ETran code
                if (!validateEndowmentTransactionCode(line, ERROR_PREFIX)) {
                    return false;
                }

                // Validate ETran code as E or I
                isValid &= validateEndowmentTransactionTypeCode(endowmentTransactionLinesDocument, line, ERROR_PREFIX);

                // Validate if a KEMID can have a principal transaction when IP indicator is P
                if (!canKEMIDHaveAPrincipalTransaction(line, ERROR_PREFIX)) {
                    return false;
                }

                // Validate if the chart is matched between the KEMID and EtranCode
                isValid &= validateChartMatch(line, ERROR_PREFIX);

                // Set Corpus Indicator
                line.setCorpusIndicator(SpringContext.getBean(EndowmentTransactionLinesDocumentService.class).getCorpusIndicatorValueforAnEndowmentTransactionLine(line.getKemid(), line.getEtranCode(), line.getTransactionIPIndicatorCode()));
            }

            // Refresh all references for the given KemId
            // line.getKemidObj().refreshNonUpdateableReferences();


        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * This method checks if this is a non-cash transaction.
     *
     * @param endowmentTransactionLinesDocumentBase
     * @return
     */
    protected boolean nonCashTransaction(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument) {
        if (EndowConstants.TransactionSubTypeCode.NON_CASH.equalsIgnoreCase(endowmentTransactionLinesDocument.getTransactionSubTypeCode())) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Tells if the document has an etran code. Override this method in the BR class specific to your document to return whether the
     * document has etran code or not.
     *
     * @param endowmentTransactionLinesDocument
     * @return true by default. It should return true if the document has an etran code or false otherwise.
     */
    protected boolean hasEtranCode(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument) {
        return true;
    }

    /**
     * This method validates the KEMID code.
     *
     * @param tranSecurity
     * @return
     */
    protected boolean isKemIdCodeEmpty(EndowmentTransactionLine line, String prefix) {
        if (StringUtils.isEmpty(line.getKemid())) {
            putFieldError(prefix + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_REQUIRED);
            return true;
        }

        return false;
    }

    /**
     * This method validates the KemId code and tries to create a KEMID object from the code.
     *
     * @param line
     * @return
     */
    protected boolean validateKemId(EndowmentTransactionLine line, String prefix) {
        boolean success = true;

        KEMID kemId = SpringContext.getBean(KEMIDService.class).getByPrimaryKey(line.getKemid());
        line.setKemidObj(kemId);
        if (null == kemId) {
            putFieldError(prefix + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_INVALID);
            success = false;
        }

        return success;
    }

    /**
     * This method determines if the KEMID is active.
     *
     * @param line
     * @return
     */
    protected boolean isActiveKemId(EndowmentTransactionLine line, String prefix) {
        if (line.getKemidObj().isClose()) {
            putFieldError(prefix + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_INACTIVE);
            return false;

        }
        else {
            return true;
        }
    }

    /**
     * This method checks if the KEMID restriction code is "NTRAN"
     *
     * @param line
     * @return
     */
    protected boolean validateNoTransactionRestriction(EndowmentTransactionLine line, String prefix) {
        if (line.getKemidObj().getTransactionRestrictionCode().equalsIgnoreCase(EndowConstants.TransactionRestrictionCode.TRAN_RESTR_CD_NTRAN)) {
            putFieldError(prefix + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_NO_TRAN_CODE);
            return false;
        }
        else {
            return true;
        }

    }

    /**
     * This method checks is the Transaction amount entered is greater than Zero.
     *
     * @param line
     * @return
     */
    protected boolean validateTransactionAmountGreaterThanZero(EndowmentTransactionLine line, String prefix) {
        if (line.getTransactionAmount() != null && line.getTransactionAmount().isGreaterThan(AbstractKualiDecimal.ZERO)) {
            return true;
        }
        else {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_AMOUNT, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_AMOUNT_GREATER_THAN_ZERO);
            return false;
        }
    }

    /**
     * This method checks is the Transaction amount entered is greater than Zero.
     *
     * @param line
     * @return
     */
    protected boolean validateTransactionAmountLessThanZero(EndowmentTransactionLine line, String prefix) {
        if (line.getTransactionAmount() != null && line.getTransactionAmount().isLessThan(AbstractKualiDecimal.ZERO)) {
            return true;
        }
        else {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_AMOUNT, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_AMOUNT_LESS_THAN_ZERO);
            return false;
        }
    }

    /**
     * This method checks is the Transaction Units entered is greater than Zero.
     *
     * @param line
     * @return
     */
    protected boolean validateTransactionUnitsGreaterThanZero(EndowmentTransactionLine line, String prefix) {
        if (line.getTransactionUnits() != null && line.getTransactionUnits().isGreaterThan(AbstractKualiDecimal.ZERO)) {
            return true;
        }
        else {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_UNITS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_UNITS_GREATER_THAN_ZERO);
            return false;
        }
    }

    /**
     * This method checks is the Transaction Units entered is greater than Zero.
     *
     * @param line
     * @return
     */
    protected boolean validateTransactionUnitsLessThanZero(EndowmentTransactionLine line, String prefix) {
        if (line.getTransactionUnits() != null && line.getTransactionUnits().isLessThan(AbstractKualiDecimal.ZERO)) {
            return true;
        }
        else {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_UNITS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_UNITS_LESS_THAN_ZERO);
            return false;
        }
    }

    /**
     * This method checks is the Transaction Units & Amount entered are equal.
     *
     * @param line
     * @param prefix
     * @return
     */
    protected boolean validateTransactionUnitsAmountEqual(EndowmentTransactionLine line, String prefix) {
        if (line.getTransactionUnits() != null && line.getTransactionAmount() != null && line.getTransactionUnits().compareTo(line.getTransactionAmount()) == 0) {
            return true;
        }
        else {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_AMOUNT, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_AMOUNT_UNITS_EQUAL);
            return false;
        }
    }

    /**
     * This method checks if the ETRAN code has a type code of E or I.
     *
     * @param line
     * @return
     */
    protected boolean validateEndowmentTransactionTypeCode(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, String prefix) {
        if (line.getEtranCodeObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowConstants.EndowmentTransactionTypeCodes.INCOME_TYPE_CODE) || line.getEtranCodeObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowConstants.EndowmentTransactionTypeCodes.EXPENSE_TYPE_CODE)) {
            return true;
        }
        else {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ENDOWMENT_TRANSACTION_TYPE_CODE_VALIDITY);
            return false;
        }
    }

    /**
     * This method validates the EndowmentTransaction code.
     *
     * @param tranSecurity
     * @return
     */
    protected boolean isEndowmentTransactionCodeEmpty(EndowmentTransactionLine line, String prefix) {
        if (StringUtils.isEmpty(line.getEtranCode())) {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_ETRAN_REQUIRED);
            return true;
        }

        return false;
    }

    /**
     * This method validates the EndowmentTransaction code and tries to create a EndowmentTransactionCode object from the code.
     *
     * @param line
     * @return
     */
    protected boolean validateEndowmentTransactionCode(EndowmentTransactionLine line, String prefix) {
        boolean success = true;

        EndowmentTransactionCode etran = SpringContext.getBean(EndowmentTransactionCodeService.class).getByPrimaryKey(line.getEtranCode());
        line.setEtranCodeObj(etran);
        if (null == etran) {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_ETRAN_INVALID);
            success = false;
        }

        return success;
    }

    /**
     * This method validates if a KEMID can have a principal transaction when IP indicator is equal to P.
     *
     * @param line
     * @return
     */
    protected boolean canKEMIDHaveAPrincipalTransaction(EndowmentTransactionLine line, String prefix) {
        boolean canHaveTransaction = true;
        String ipIndicatorCode = line.getTransactionIPIndicatorCode();
        if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(ipIndicatorCode)) {
            String kemid = line.getKemid();
            if (!SpringContext.getBean(EndowmentTransactionLinesDocumentService.class).canKEMIDHaveAPrincipalActivity(kemid, ipIndicatorCode)) {
                putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_IP_INDICATOR_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_CAN_NOT_HAVE_A_PRINCIPAL_TRANSACTION);
                canHaveTransaction = false;
            }
        }
        return canHaveTransaction;
    }

    /**
     * This method validates if the chart is matched between GL Account in the KEMID and GL Link in the Endowment Transaction Code.
     * If the Chart codes do not match, the Etran Code field should be highlighted.
     *
     * @param line
     * @return
     */
    protected boolean validateChartMatch(EndowmentTransactionLine line, String prefix) {
        boolean isChartMatched = true;
        String kemid = line.getKemid();
        String etranCode = line.getEtranCode();
        String ipIndicatorCode = line.getTransactionIPIndicatorCode();
        if (!SpringContext.getBean(EndowmentTransactionDocumentService.class).matchChartBetweenKEMIDAndETranCode(kemid, etranCode, ipIndicatorCode)) {
            if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(ipIndicatorCode)) {
                putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_CHART_CODE_DOES_NOT_MATCH_FOR_PRINCIPAL);
            }
            else {
                putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_CHART_CODE_DOES_NOT_MATCH_FOR_INCOME);
            }

            isChartMatched = false;
        }
        return isChartMatched;
    }

    /**
     * Validates that the security chart and the etran code chart match.
     *
     * @param endowmentTransactionLinesDocument
     * @param line
     * @param prefix
     * @param isSource
     * @return true if valid, false otherwise
     */
    protected boolean validateSecurityEtranChartMatch(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, String prefix, boolean isSource) {
        boolean isChartMatched = true;
        Security security = getSecurityForValidation(endowmentTransactionLinesDocument, isSource);
        String kemID = line.getKemid();
        String ipIndicatorCode = line.getTransactionIPIndicatorCode();
        if (!SpringContext.getBean(EndowmentTransactionDocumentService.class).matchChartBetweenSecurityAndETranCode(security, kemID, ipIndicatorCode)) {
            if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(ipIndicatorCode)) {
                putFieldError(prefix + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_SECURITY_KEMID_CHART_CODE_DOES_NOT_MATCH, EndowConstants.PRINCIPAL);
            }
            else {
                putFieldError(prefix + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_SECURITY_KEMID_CHART_CODE_DOES_NOT_MATCH, EndowConstants.INCOME);
            }

            isChartMatched = false;
        }
        return isChartMatched;
    }

    /**
     * For a true endowment, when the END_TRAN_LN_T: TRAN_IP_IND_CD is equal to P, a warning message will be placed in the document
     * transaction line notifying the viewer that the transaction will reduce the value of the endowment at the time the transaction
     * line is added. WARNING: This transaction will reduce permanently restricted funds!. However, the transaction line would be
     * added on successfully.
     *
     * @param line
     * @return
     */
    protected void checkWhetherReducePermanentlyRestrictedFund(EndowmentTransactionLine line, String prefix) {
        String ipIndicatorCode = line.getTransactionIPIndicatorCode();
        String kemid = line.getKemid();
        if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(ipIndicatorCode) && SpringContext.getBean(KEMIDService.class).isTrueEndowment(kemid)) {
            GlobalVariables.getMessageMap().putWarning(prefix + EndowPropertyConstants.TRANSACTION_LINE_IP_INDICATOR_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.WARNING_REDUCE_PERMANENTLY_RESTRICTED_FUNDS);
        }
    }

    /**
     * Upon adding the transaction line, the system will check to see if there are sufficient funds to process the transaction
     * (END_AVAIL_CSH_T). If there are not, a warning message will be placed in the document transaction line notifying the viewer
     * that there are not sufficient funds. -If END_TRAN_LN_T: TRAN_IP_IND_CD is equal to I verify against END_AVAIL_CSH_T:
     * AVAIL_TOT_CSH -If END_TRAN_LN_T: TRAN_IP_IND_CD is equal to P verify against END_AVAIL_CSH_T: AVAIL_PRIN_CSH However, the
     * transaction line would be added on successfully.
     *
     * @param line
     * @return
     */
    protected void checkWhetherHaveSufficientFundsForCashBasedTransaction(EndowmentTransactionLine line, String prefix) {
        String ipIndicatorCode = line.getTransactionIPIndicatorCode();
        String kemid = line.getKemid();
        KualiDecimal amount = line.getTransactionAmount();

        Map criteria = new HashMap();
        criteria.put(EndowPropertyConstants.KEMID, kemid);
        KEMIDCurrentAvailableBalance theKEMIDCurrentAvailableBalance = (KEMIDCurrentAvailableBalance) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(KEMIDCurrentAvailableBalance.class, criteria);

        if (ObjectUtils.isNotNull(theKEMIDCurrentAvailableBalance)) {
            if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(ipIndicatorCode)) {
                if (amount.isGreaterThan(new KualiDecimal(theKEMIDCurrentAvailableBalance.getAvailablePrincipalCash()))) {
                    GlobalVariables.getMessageMap().putWarning(prefix + EndowPropertyConstants.TRANSACTION_LINE_IP_INDICATOR_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.WARNING_NO_SUFFICIENT_FUNDS);
                }
            }
            else {
                if (amount.isGreaterThan(new KualiDecimal(theKEMIDCurrentAvailableBalance.getAvailableTotalCash()))) {
                    GlobalVariables.getMessageMap().putWarning(prefix + EndowPropertyConstants.TRANSACTION_LINE_IP_INDICATOR_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.WARNING_NO_SUFFICIENT_FUNDS);
                }
            }
        }
    }


    protected boolean templateMethod(EndowmentTransactionLine line) {
        boolean success = true;

        return success;
    }

    /**
     * This methods checks to ensure for cash Tx do not have a Etran.
     *
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @param prefix
     * @return
     */
    protected boolean checkCashTransactionEndowmentCode(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, String prefix) {
        // For Cash based Tx the Etran code must be empty,If Tx is Cash based, check for Etran code and if not null display Error
        // message.
        if (!nonCashTransaction(endowmentTransactionLinesDocument) && (!StringUtils.isEmpty(line.getEtranCode()))) {
            //
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_ETRAN_BLANK);
            return false;
        }

        return true;
    }

    /**
     * Checks that the document has at least one transaction line.
     *
     * @param document
     * @param isSource
     * @return true if valid, false otherwise
     */
    protected boolean transactionLineSizeGreaterThanZero(EndowmentTransactionLinesDocumentBase document, boolean isSource) {
        List<EndowmentTransactionLine> transactionLineList = null;
        if (isSource) {
            transactionLineList = document.getSourceTransactionLines();
            if (transactionLineList == null || transactionLineList.size() == 0) {
                putFieldError(EndowPropertyConstants.SOURCE_TRANSACTION_LINE_PREFIX, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_FROM_TRANSACTION_LINE_COUNT_INSUFFICIENT);
                return false;
            }
        }
        else {
            transactionLineList = document.getTargetTransactionLines();
            if (transactionLineList == null || transactionLineList.size() == 0) {
                putFieldError(EndowPropertyConstants.TARGET_TRANSACTION_LINE_PREFIX, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TO_TRANSACTION_LINE_COUNT_INSUFFICIENT);
                return false;
            }
        }

        return true;
    }

    /**
     * This method is a collection if validation performed on Registration Code. The validations are not null & valid registration
     * code
     *
     * @param isValid
     * @param liabilityIncreaseDocument
     * @return
     */
    protected boolean validateRegistration(boolean isValid, EndowmentSecurityDetailsDocumentBase document, boolean isSource) {
        // Checks if registration code is empty
        if (isRegistrationCodeEmpty(document, isSource)) {
            return false;
        }

        // Validate Registration code.
        if (!validateRegistrationCode(document, isSource)) {
            return false;
        }

        // Checks if registration code is active
        isValid &= isRegistrationCodeActive(document, isSource);
        return isValid;
    }

    /**
     * This method is a collection if validation performed on Security. The validations are not null, valid security,active & class
     * code matches L
     *
     * @param isValid
     * @param document
     * @return
     */
    protected boolean validateSecurity(boolean isValid, EndowmentSecurityDetailsDocumentBase document, boolean isSource) {
        // Checks if Security Code is empty.
        if (isSecurityCodeEmpty(document, isSource)) {
            return false;
        }

        // Validates Security Code.
        if (!validateSecurityCode(document, isSource)) {
            return false;
        }

        // Checks if Security is Active
        isValid &= isSecurityActive(document, isSource);

        // Validates Security class code
        isValid &= validateSecurityClassTypeCode(document, isSource, EndowConstants.ClassCodeTypes.LIABILITY);
        return isValid;
    }

    /**
     * This method validates that the source and target security lines are different from each other.
     *
     * @param document
     * @return True if source and target security codes are different
     */
    protected boolean validateNonDuplicateSecurityCodes(EndowmentSecurityDetailsDocumentBase document) {

        Security sourceSecurity = getSecurityForValidation(document, true);
        Security targetSecurity = getSecurityForValidation(document, false);

        if (sourceSecurity != null && targetSecurity != null) {
            if (sourceSecurity.getId().equalsIgnoreCase(targetSecurity.getId())) {
                putFieldError(getEndowmentTransactionSecurityPrefix(document, false) + EndowPropertyConstants.TRANSACTION_SECURITY_ID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_SECURITY_CODE_EQUAL);
            }
        }

        return true;
    }

    /**
     * Validates that the KEMID has sufficient units in the tax lots to perform the transaction.
     *
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @param index
     * @return true if valid, false otherwise
     */
    public boolean validateSufficientUnits(boolean isAdd, EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, int transLineIndex, int taxLotIndex) {
        EndowmentTransactionSecurity endowmentTransactionSecurity = getEndowmentTransactionSecurity(endowmentTransactionLinesDocument, true);
        boolean isValid = true;
        List<HoldingTaxLot> holdingTaxLots = new ArrayList<HoldingTaxLot>();

        if (isAdd) {
            holdingTaxLots = SpringContext.getBean(HoldingTaxLotService.class).getAllTaxLots(line.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), line.getTransactionIPIndicatorCode());
        }
        else {
            List<EndowmentTransactionTaxLotLine> existingTransactionLines = line.getTaxLotLines();
            for (int i = 0; i < existingTransactionLines.size(); i++) {
                // don't take into account the tax lot line we are now deleting
                if (i != taxLotIndex) {

                    EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine = existingTransactionLines.get(i);
                    HoldingTaxLot holdingTaxLot = SpringContext.getBean(HoldingTaxLotService.class).getByPrimaryKey(line.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), endowmentTransactionTaxLotLine.getTransactionHoldingLotNumber(), line.getTransactionIPIndicatorCode());

                    if (ObjectUtils.isNotNull(holdingTaxLot)) {
                        holdingTaxLots.add(holdingTaxLot);
                    }
                }
            }
        }

        BigDecimal totalTaxLotsUnits = BigDecimal.ZERO;

        if (holdingTaxLots != null && holdingTaxLots.size() > 0) {
            for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                totalTaxLotsUnits = totalTaxLotsUnits.add(holdingTaxLot.getUnits());
            }
        }

        BigDecimal lineUnits = null;
        lineUnits = line.getTransactionUnits().bigDecimalValue();

        if (lineUnits.compareTo(totalTaxLotsUnits) == 1) {
            isValid = false;
            putFieldError(getErrorPrefix(line, transLineIndex) + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_UNITS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ASSET_DECREASE_INSUFFICIENT_UNITS);
        }
        return isValid;
    }

    /**
     * Validates that the tax lots for a transaction line correspond to the information in that transaction line. It might be
     * possible that the user has changed the KEMID or Security related data without refreshing the tax lot lines. On save we need
     * to check that the tax lot lines relate to the KEMID in the transaction lines. Take the first tax lot for the transaction line
     * and check if it is in the holding tax lots for that KEMID. This validation is needed in documents that support tax lot lines
     * deletion as in that case the tax lot lines are not automatically refreshed on save/submit.
     *
     * @param endowmentTransactionLinesDocument
     * @param transLine
     * @param transLineIndex
     * @return
     */
    protected boolean validateTaxLots(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine transLine, int transLineIndex) {
        boolean isValid = true;

        EndowmentTransactionSecurity endowmentTransactionSecurity = getEndowmentTransactionSecurity(endowmentTransactionLinesDocument, true);

        // as it might be possible that the user has changed the KEMID without refreshing the tax lot lines, on save we need to
        // check that the tax lot lines relate to the KEMID in the transaction lines. Take the first tax lot for the transaction
        // line and check if it is in the holding tax lots for that KEMID.
        if (transLine.getTaxLotLines() != null && transLine.getTaxLotLines().size() > 0) {
            EndowmentTransactionTaxLotLine transactionTaxLotLine = transLine.getTaxLotLines().get(0);

            boolean isMatchingSecurity = endowmentTransactionSecurity.getSecurityID().equalsIgnoreCase(transactionTaxLotLine.getSecurityID());
            boolean isMatchingRegistrationCode = endowmentTransactionSecurity.getRegistrationCode().equalsIgnoreCase(transactionTaxLotLine.getRegistrationCode());
            boolean isMatchingKemid = transLine.getKemid().equalsIgnoreCase(transactionTaxLotLine.getKemid());
            boolean isMatchingIpIndicator = transLine.getTransactionIPIndicatorCode().equalsIgnoreCase(transactionTaxLotLine.getIpIndicator());

            if (!isMatchingSecurity || !isMatchingRegistrationCode || !isMatchingKemid || !isMatchingIpIndicator) {
                isValid = false;
                putFieldError(getErrorPrefix(transLine, transLineIndex) + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_TAX_LOT_DONT_CORRESPOND);
            }

        }
        return isValid;
    }

    /**
     * Checks that the transaction line units match the tax lot lines total number of units.
     *
     * @param document
     * @param transactionLine
     * @param index
     * @return true if valid, false otherwise
     */
    protected boolean validateTotalUnits(EndowmentTransactionalDocument document, EndowmentTransactionLine transactionLine, int index) {
        boolean isValid = true;

        BigDecimal transactionLineUnits = transactionLine.getTransactionUnits().bigDecimalValue();
        BigDecimal taxLotLinesTotalUnits = BigDecimal.ZERO;

        if (transactionLine.getTaxLotLines() != null && transactionLine.getTaxLotLines().size() > 0) {

            for (EndowmentTransactionTaxLotLine taxLotLine : transactionLine.getTaxLotLines()) {
                taxLotLinesTotalUnits = taxLotLinesTotalUnits.add(taxLotLine.getLotUnits());
            }
        }

        if (transactionLine instanceof EndowmentSourceTransactionLine) {
            taxLotLinesTotalUnits = taxLotLinesTotalUnits.negate();
        }

        if (transactionLineUnits.compareTo(taxLotLinesTotalUnits) != 0) {
            isValid = false;

            putFieldError(getErrorPrefix(transactionLine, index) + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_UNITS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_TAX_LOT_UNITS_DONT_CORRESPOND);
        }

        return isValid;
    }


    /**
     * This method Check if value of Endowment is being reduced.
     *
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @param ERRORPREFIX
     * @return
     */
    /*
     * protected boolean checkEndowmentValueReduction(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument,
     * EndowmentTransactionLine line, String ERRORPREFIX) { if(
     * EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(line.getTransactionIPIndicatorCode()) ) {
     * line.getKemidObj().refreshNonUpdateableReferences(); line.getKemidObj().getType().refreshNonUpdateableReferences();
     * if(line.getKemidObj().getTypeRestrictionCodeForPrincipalRestrictionCode().getPermanentIndicator()) {
     * GlobalVariables.getMessageMap().putWarningWithoutFullErrorPath(EndowConstants.ENDOWMENT_TRANSACTION_LINE_ERRORS,
     * EndowKeyConstants.EndowmentTransactionDocumentConstants.WARNING_TRANSACTION_LINE_ENDOWMENT_VALUE_REDUCTION); return false; }
     * } return true; }
     */

    /**
     * This method validates if the source & target units are equal.
     *
     * @param securityTransferDocument
     * @return
     */
    protected boolean validateSourceTargetUnitsEqual(SecurityTransferDocument securityTransferDocument) {
        if (!securityTransferDocument.getTargetTotalUnits().equals(securityTransferDocument.getSourceTotalUnits())) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(EndowConstants.TRANSACTION_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_SOURCE_TARGET_UNITS_EQUAL);
            return false;
        }

        return true;
    }

    /**
     * This method validates if the source & target units are equal.
     *
     * @param securityTransferDocument
     * @return
     */
    protected boolean validateSourceTargetAmountEqual(SecurityTransferDocument securityTransferDocument) {
        if (!securityTransferDocument.getTargetTotalAmount().equals(securityTransferDocument.getSourceTotalAmount())) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(EndowConstants.TRANSACTION_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_SOURCE_TARGET_AMOUNT_EQUAL);
            return false;
        }

        return true;
    }

}
