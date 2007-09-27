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
package org.kuali.module.financial.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.PersonTaxId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.bo.user.UserId;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.MaintenanceDocumentService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.financial.bo.DisbursementVoucherNonResidentAlienTax;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.rules.DisbursementVoucherDocumentRule;
import org.kuali.module.financial.rules.DisbursementVoucherRuleConstants;
import org.kuali.module.financial.service.DisbursementVoucherTaxService;

/**
 * Handles queries and validation on tax id numbers.
 * 
 * 
 */
public class DisbursementVoucherTaxServiceImpl implements DisbursementVoucherTaxService, DisbursementVoucherRuleConstants {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherTaxServiceImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
    private MaintenanceDocumentService maintenanceDocumentService;
    private UniversalUserService universalUserService;
    
    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#getEmployeeNumber(java.lang.String, java.lang.String)
     */
    public String getUniversalId(String taxIDNumber, String taxpayerTypeCode) {
        if (TAX_TYPE_FEIN.equals(taxpayerTypeCode)) {
            return null;
        }
        
        String universalId = null;
        UserId userId = (UserId) new PersonTaxId(taxIDNumber);
        UniversalUser universalUser = null;

        try {
            universalUser = universalUserService.getUniversalUser(userId);
        }
        catch (UserNotFoundException e) {
        }
        
        if (universalUser != null) {
            universalId = universalUser.getPersonUniversalIdentifier();
        }
        return universalId;
    }

    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#getPayeeNumber(java.lang.String, java.lang.String)
     */
    public String getPayeeId(String taxIDNumber, String taxpayerTypeCode) {
        String payeeId = null;

        Map taxIDCrit = new HashMap();
        taxIDCrit.put("taxIdNumber", taxIDNumber);
        taxIDCrit.put("taxpayerTypeCode", taxpayerTypeCode);
        Collection foundPayees = businessObjectService.findMatching(Payee.class, taxIDCrit);

        if (!foundPayees.isEmpty()) {
            Payee payee = (Payee) foundPayees.iterator().next();
            payeeId = payee.getPayeeIdNumber();
        }

        return payeeId;
    }

    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#getVendorNumber(java.lang.String, java.lang.String)
     */
    public String getVendorId(String taxIDNumber, String taxpayerTypeCode) {
        String vendorId = null;
        //TODO: implement once EPIC is integrated
        return vendorId;
    }

    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#getPendingPayeeNumber(java.lang.String,
     *      java.lang.String)
     */
    public String getPendingPayeeId(String taxIDNumber, String taxpayerTypeCode) {
        String pendingPayeeId = null;

        List pendingPayees = maintenanceDocumentService.getPendingObjects(Payee.class);

        for (Iterator iter = pendingPayees.iterator(); iter.hasNext();) {
            Payee pendingPayee = (Payee) iter.next();
            if (taxIDNumber.equals(pendingPayee.getTaxIdNumber()) && taxpayerTypeCode.equals(pendingPayee.getTaxpayerTypeCode())) {
                pendingPayeeId = pendingPayee.getPayeeIdNumber();
            }
        }

        return pendingPayeeId;
    }

    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#generateNRATaxLines(org.kuali.module.financial.document.DisbursementVoucherDocument)
     */
    private void generateNRATaxLines(DisbursementVoucherDocument document) {
        // retrieve first accounting line for tax line attributes
        AccountingLine line1 = document.getSourceAccountingLine(0);

        List taxLineNumbers = new ArrayList();

        // generate gross up
        if (document.getDvNonResidentAlienTax().isIncomeTaxGrossUpCode()) {
            AccountingLine grossLine = null;
            try {
                grossLine = (SourceAccountingLine) document.getSourceAccountingLineClass().newInstance();
            }
            catch (IllegalAccessException e) {
                throw new InfrastructureException("unable to access sourceAccountingLineClass", e);
            }
            catch (InstantiationException e) {
                throw new InfrastructureException("unable to instantiate sourceAccountingLineClass", e);
            }

            grossLine.setDocumentNumber(document.getDocumentNumber());
            grossLine.setSequenceNumber(document.getNextSourceLineNumber());
            grossLine.setChartOfAccountsCode(line1.getChartOfAccountsCode());
            grossLine.setAccountNumber(line1.getAccountNumber());
            grossLine.setFinancialObjectCode(line1.getFinancialObjectCode());

            // calculate gross up amount and set as line amount
            BigDecimal federalTaxPercent = document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent().bigDecimalValue();
            BigDecimal stateTaxPercent = document.getDvNonResidentAlienTax().getStateIncomeTaxPercent().bigDecimalValue();
            BigDecimal documentAmount = document.getDisbVchrCheckTotalAmount().bigDecimalValue();

            KualiDecimal grossAmount1 = new KualiDecimal((documentAmount.multiply(federalTaxPercent).divide(new BigDecimal(100).subtract(federalTaxPercent).subtract(stateTaxPercent), 5, BigDecimal.ROUND_HALF_UP)));
            KualiDecimal grossAmount2 = new KualiDecimal((documentAmount.multiply(stateTaxPercent).divide(new BigDecimal(100).subtract(federalTaxPercent).subtract(stateTaxPercent), 5, BigDecimal.ROUND_HALF_UP)));
            grossLine.setAmount(grossAmount1.add(grossAmount2));

            // put line number in line number list, and update next line property in document
            taxLineNumbers.add(grossLine.getSequenceNumber());
            document.setNextSourceLineNumber(new Integer(document.getNextSourceLineNumber().intValue() + 1));

            // add to source accounting lines
            grossLine.refresh();
            document.getSourceAccountingLines().add(grossLine);

            // udpate check total, is added because line amount is negative, so this will take check amount down
            document.setDisbVchrCheckTotalAmount(document.getDisbVchrCheckTotalAmount().add(grossLine.getAmount()));
        }

        KualiDecimal taxableAmount = document.getDisbVchrCheckTotalAmount();

        // generate federal tax line
        if (!(new KualiDecimal(0).equals(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent()))) {
            String federalTaxChart = getKualiConfigurationService().getParameterValue(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.DISBURSEMENT_VOUCHER_DOC, DisbursementVoucherRuleConstants.FEDERAL_TAX_PARM_PREFIX + DisbursementVoucherRuleConstants.TAX_PARM_CHART_SUFFIX);
            String federalTaxAccount = getKualiConfigurationService().getParameterValue(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.DISBURSEMENT_VOUCHER_DOC, DisbursementVoucherRuleConstants.FEDERAL_TAX_PARM_PREFIX + DisbursementVoucherRuleConstants.TAX_PARM_ACCOUNT_SUFFIX );
            String federalTaxObjectCode = getKualiConfigurationService().getConstrainedValues(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.DISBURSEMENT_VOUCHER_DOC, DisbursementVoucherRuleConstants.FEDERAL_TAX_PARM_PREFIX + DisbursementVoucherRuleConstants.TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX, document.getDvNonResidentAlienTax().getIncomeClassCode()).get(0);
            if ( StringUtils.isBlank(federalTaxChart)||StringUtils.isBlank(federalTaxAccount)||StringUtils.isBlank(federalTaxObjectCode)) {
                LOG.error("Unable to retrieve federal tax parameters.");
                throw new RuntimeException("Unable to retrieve federal tax parameters." );
            }

            AccountingLine federalTaxLine = generateTaxAccountingLine(document, federalTaxChart, federalTaxAccount, federalTaxObjectCode, document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent(), taxableAmount);

            // put line number in line number list, and update next line property in document
            taxLineNumbers.add(federalTaxLine.getSequenceNumber());
            document.setNextSourceLineNumber(new Integer(document.getNextSourceLineNumber().intValue() + 1));

            // add to source accounting lines
            federalTaxLine.refresh();
            document.getSourceAccountingLines().add(federalTaxLine);

            // udpate check total, is added because line amount is negative, so this will take check amount down
            document.setDisbVchrCheckTotalAmount(document.getDisbVchrCheckTotalAmount().add(federalTaxLine.getAmount()));
        }

        // generate state tax line
        if (!(new KualiDecimal(0).equals(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent()))) {
            String stateTaxChart = getKualiConfigurationService().getParameterValue(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.DISBURSEMENT_VOUCHER_DOC, DisbursementVoucherRuleConstants.STATE_TAX_PARM_PREFIX + DisbursementVoucherRuleConstants.TAX_PARM_CHART_SUFFIX);
            String stateTaxAccount = getKualiConfigurationService().getParameterValue(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.DISBURSEMENT_VOUCHER_DOC, DisbursementVoucherRuleConstants.STATE_TAX_PARM_PREFIX + DisbursementVoucherRuleConstants.TAX_PARM_ACCOUNT_SUFFIX);
            String stateTaxObjectCode = getKualiConfigurationService().getConstrainedValues(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.DISBURSEMENT_VOUCHER_DOC, DisbursementVoucherRuleConstants.STATE_TAX_PARM_PREFIX + DisbursementVoucherRuleConstants.TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX, document.getDvNonResidentAlienTax().getIncomeClassCode()).get(0);

            if ( StringUtils.isBlank(stateTaxChart)||StringUtils.isBlank(stateTaxAccount)||StringUtils.isBlank(stateTaxObjectCode)) {
                LOG.error("Unable to retrieve state tax parameters.");
                throw new RuntimeException("Unable to retrieve state tax parameters." );
            }

            AccountingLine stateTaxLine = generateTaxAccountingLine(document, stateTaxChart, stateTaxAccount, stateTaxObjectCode, document.getDvNonResidentAlienTax().getStateIncomeTaxPercent(), taxableAmount);

            // put line number in line number list, and update next line property in document
            taxLineNumbers.add(stateTaxLine.getSequenceNumber());
            document.setNextSourceLineNumber(new Integer(document.getNextSourceLineNumber().intValue() + 1));

            // add to source accounting lines
            stateTaxLine.refresh();
            document.getSourceAccountingLines().add(stateTaxLine);

            // udpate check total, is added because line amount is negative, so this will take check amount down
            document.setDisbVchrCheckTotalAmount(document.getDisbVchrCheckTotalAmount().add(stateTaxLine.getAmount()));
        }

        // update line number field
        document.getDvNonResidentAlienTax().setFinancialDocumentAccountingLineText(StringUtils.join(taxLineNumbers.iterator(), ","));
    }

    /**
     * Generates an accounting line for the chart, account, object code, & tax percentage.
     * 
     * @param document
     */
    private AccountingLine generateTaxAccountingLine(DisbursementVoucherDocument document, String chart, String account, String objectCode, KualiDecimal taxPercent, KualiDecimal taxableAmount) {
        AccountingLine taxLine = null;
        try {
            taxLine = (SourceAccountingLine) document.getSourceAccountingLineClass().newInstance();
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("unable to access sourceAccountingLineClass", e);
        }
        catch (InstantiationException e) {
            throw new InfrastructureException("unable to instantiate sourceAccountingLineClass", e);
        }

        taxLine.setDocumentNumber(document.getDocumentNumber());
        taxLine.setSequenceNumber(document.getNextSourceLineNumber());
        taxLine.setChartOfAccountsCode(chart);
        taxLine.setAccountNumber(account);
        taxLine.setFinancialObjectCode(objectCode);

        // calculate tax amount and set as line amount
        BigDecimal amount = taxableAmount.bigDecimalValue();
        BigDecimal tax = taxPercent.bigDecimalValue();
        BigDecimal taxDecimal = tax.divide(new BigDecimal(100), 5, BigDecimal.ROUND_HALF_UP);
        KualiDecimal taxAmount = new KualiDecimal(amount.multiply(taxDecimal));
        taxLine.setAmount(taxAmount.negated());

        return taxLine;
    }


    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#processNonResidentAlienTax(org.kuali.module.financial.document.DisbursementVoucherDocument,
     *      java.util.List)
     */
    public void processNonResidentAlienTax(DisbursementVoucherDocument document) {
        /* validate nra information */
        if (validateNRATaxInformation(document)) {
            generateNRATaxLines(document);
        }
    }

    /**
     * Removes tax lines from the document's accounting lines and updates the check total.
     * 
     * @param document
     * @param previousTaxLineNumbers
     */
    public void clearNRATaxLines(DisbursementVoucherDocument document) {
        List taxLines = new ArrayList();
        KualiDecimal taxTotal = new KualiDecimal(0);

        DisbursementVoucherNonResidentAlienTax dvnrat = document.getDvNonResidentAlienTax();
        if (dvnrat != null) {
            List previousTaxLineNumbers = getNRATaxLineNumbers(dvnrat.getFinancialDocumentAccountingLineText());

            // get tax lines out of source lines
            boolean previousGrossUp = false;
            for (Iterator iter = document.getSourceAccountingLines().iterator(); iter.hasNext();) {
                AccountingLine line = (AccountingLine) iter.next();
                if (previousTaxLineNumbers.contains(line.getSequenceNumber())) {
                    taxLines.add(line);

                    // check if tax line was a positive amount, in which case we had a gross up
                    if ((new KualiDecimal(0)).compareTo(line.getAmount()) < 0) {
                        previousGrossUp = true;
                    }
                    else {
                        taxTotal = taxTotal.add(line.getAmount().abs());
                    }
                }
            }

            // remove tax lines
            document.getSourceAccountingLines().removeAll(taxLines);

            // update check total if not grossed up
            if (!previousGrossUp) {
                document.setDisbVchrCheckTotalAmount(document.getDisbVchrCheckTotalAmount().add(taxTotal));
            }

            // clear line string
            dvnrat.setFinancialDocumentAccountingLineText("");
        }
    }

    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#getNonResidentAlienTaxAmount(org.kuali.module.financial.document.DisbursementVoucherDocument)
     */
    public KualiDecimal getNonResidentAlienTaxAmount(DisbursementVoucherDocument document) {
        KualiDecimal taxAmount = new KualiDecimal(0);

        // if not nra payment or gross has been done, no tax amount should have been taken out
        if (!document.getDvPayeeDetail().isDisbVchrAlienPaymentCode() || (document.getDvPayeeDetail().isDisbVchrAlienPaymentCode() && document.getDvNonResidentAlienTax().isIncomeTaxGrossUpCode())) {
            return taxAmount;
        }

        // get tax line numbers
        List taxLineNumbers = getNRATaxLineNumbers(document.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());

        for (Iterator iter = document.getSourceAccountingLines().iterator(); iter.hasNext();) {
            SourceAccountingLine line = (SourceAccountingLine) iter.next();

            // check if line is nra tax line
            if (taxLineNumbers.contains(line.getSequenceNumber())) {
                taxAmount.add(line.getAmount().negated());
            }
        }

        return taxAmount;
    }

    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#validateNRATaxInformation(org.kuali.module.financial.document.DisbursementVoucherDocument)
     */
    private boolean validateNRATaxInformation(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        // set nulls to 0
        if (document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent() == null) {
            document.getDvNonResidentAlienTax().setFederalIncomeTaxPercent(new KualiDecimal(0));
        }

        if (document.getDvNonResidentAlienTax().getStateIncomeTaxPercent() == null) {
            document.getDvNonResidentAlienTax().setStateIncomeTaxPercent(new KualiDecimal(0));
        }

        /* call dv rule to do general nra validation */
        DisbursementVoucherDocumentRule documentRule = new DisbursementVoucherDocumentRule();
        documentRule.validateNonResidentAlienInformation(document);

        if (!GlobalVariables.getErrorMap().isEmpty()) {
            return false;
        }

        /* make sure payee is nra */
        if (!document.getDvPayeeDetail().isDisbVchrAlienPaymentCode()) {
            errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_GENERATE_TAX_NOT_NRA);
            return false;
        }

        /* don't generate tax if reference doc is given */
        if (StringUtils.isNotBlank(document.getDvNonResidentAlienTax().getReferenceFinancialDocumentNumber())) {
            errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_GENERATE_TAX_DOC_REFERENCE);
            return false;
        }


        // check attributes needed to generate lines
        /* need at least 1 line */
        if (!(document.getSourceAccountingLines().size() >= 1)) {
            errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_GENERATE_TAX_NO_SOURCE);
            return false;
        }

        /* make sure both fed and state tax percents are not 0, in which case there is no need to generate lines */
        if (new KualiDecimal(0).equals(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent()) && new KualiDecimal(0).equals(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent())) {
            errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_GENERATE_TAX_BOTH_0);
            return false;
        }

        /* check total cannot be negative */
        if (KFSConstants.ZERO.compareTo(document.getDisbVchrCheckTotalAmount()) == 1) {
            errors.putErrorWithoutFullErrorPath("document.disbVchrCheckTotalAmount", KFSKeyConstants.ERROR_NEGATIVE_OR_ZERO_CHECK_TOTAL);
            return false;
        }

        /* total accounting lines cannot be negative */
        if (KFSConstants.ZERO.compareTo(document.getSourceTotal()) == 1) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_NEGATIVE_ACCOUNTING_TOTAL);
            return false;
        }

        /* total of accounting lines must match check total */
        if (document.getDisbVchrCheckTotalAmount().compareTo(document.getSourceTotal()) != 0) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_CHECK_ACCOUNTING_TOTAL);
            return false;
        }

        return true;
    }

    /**
     * Parses the tax line string and returns a list of line numbers as Integers.
     * 
     * @param taxLineString
     * @return
     */
    public List getNRATaxLineNumbers(String taxLineString) {
        List taxLineNumbers = new ArrayList();
        if (StringUtils.isNotBlank(taxLineString)) {
            List taxLineNumberStrings = Arrays.asList(StringUtils.split(taxLineString, ","));
            for (Iterator iter = taxLineNumberStrings.iterator(); iter.hasNext();) {
                String lineNumber = (String) iter.next();
                taxLineNumbers.add(Integer.valueOf(lineNumber));
            }
        }

        return taxLineNumbers;
    }

    /**
     * @return Returns the kualiConfigurationService.
     */
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @return Returns the maintenanceDocumentService.
     */
    public MaintenanceDocumentService getMaintenanceDocumentService() {
        return maintenanceDocumentService;
    }

    /**
     * @param maintenanceDocumentService The maintenanceDocumentService to set.
     */
    public void setMaintenanceDocumentService(MaintenanceDocumentService maintenanceDocumentService) {
        this.maintenanceDocumentService = maintenanceDocumentService;
    }

    /**
     * @return Returns the universalUserService.
     */
    public UniversalUserService getUniversalUserService() {
        return universalUserService;
    }

    /**
     * @param universalUserService The universalUserService to set.
     */
    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }
}