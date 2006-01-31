/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.MaintenanceDocumentService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.rules.DisbursementVoucherDocumentRule;
import org.kuali.module.financial.rules.DisbursementVoucherRuleConstants;
import org.kuali.module.financial.service.DisbursementVoucherTaxService;

/**
 * Handles queries and validation on tax id numbers.
 * @author Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherTaxServiceImpl implements DisbursementVoucherTaxService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherTaxServiceImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
    private MaintenanceDocumentService maintenanceDocumentService;

    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#getTaxIDNumberUsage(java.lang.String)
     */
    public Integer getTaxIDNumberUsage(String taxIDNumber, String taxpayerTypeCode) {
        Integer taxIDMatch = DisbursementVoucherTaxService.TAX_NUMBER_NOT_FOUND;
        if (taxIDNumber == null || taxpayerTypeCode == null) {
            return taxIDMatch;
        }

        String institutionFeinNumber = kualiConfigurationService.getPropertyString(KeyConstants.INSTITUTION_TAX_FEIN_NUMBER);
        if (taxIDNumber.equals(institutionFeinNumber)) {
            taxIDMatch = DisbursementVoucherTaxService.INSTITUTION_TAX_NUMBER;
        }
        else if (isVendorIDNumber(taxIDNumber)) {
            taxIDMatch = DisbursementVoucherTaxService.TAX_ID_VENDOR;
        }
        else if (isPayeeTaxNumber(taxIDNumber, taxpayerTypeCode)) {
            taxIDMatch = DisbursementVoucherTaxService.TAX_ID_EXISTING_PAYEE;
        }
        else if (isPendingPayeeTaxNumber(taxIDNumber, taxpayerTypeCode)) {
            taxIDMatch = DisbursementVoucherTaxService.TAX_ID_PENDING_PAYEE;
        }
        else if (isEmployeeTaxNumber(taxIDNumber)) {
            taxIDMatch = DisbursementVoucherTaxService.TAX_ID_EMPLOYEE;
        }

        return taxIDMatch;
    }

    /**
     * Checks whether the given tax id number matches a current vendor id number.
     * @param taxIDNumber
     * @return
     */
    private boolean isVendorIDNumber(String taxIDNumber) {
        boolean isVendorID = false;
        // TODO: Implement check once Epic is integrated

        return isVendorID;
    }

    /**
     * Checks whether the given tax id number matches a current payee tax number.
     * @param taxIDNumber
     * @return
     */
    private boolean isPayeeTaxNumber(String taxIDNumber, String taxpayerTypeCode) {
        boolean isPayeeNumber = false;

        Map taxIDCrit = new HashMap();
        taxIDCrit.put("taxIdNumber", taxIDNumber);
        taxIDCrit.put("taxpayerTypeCode", taxpayerTypeCode);
        Collection foundPayees = businessObjectService.findMatching(Payee.class, taxIDCrit);

        if (!foundPayees.isEmpty()) {
            isPayeeNumber = true;
        }

        return isPayeeNumber;
    }

    /**
     * Checks whether the given tax id number matches a pending payee tax number.
     * @param taxIDNumber
     * @return
     */
    private boolean isPendingPayeeTaxNumber(String taxIDNumber, String taxpayerTypeCode) {
        boolean isPendingPayeeNumber = false;
        List pendingPayees = maintenanceDocumentService.getPendingObjects(Payee.class);

        for (Iterator iter = pendingPayees.iterator(); iter.hasNext();) {
            Payee pendingPayee = (Payee) iter.next();
            if (taxIDNumber.equals(pendingPayee.getTaxIdNumber()) && taxpayerTypeCode.equals(pendingPayee.getTaxpayerTypeCode())) {
                isPendingPayeeNumber = true;
            }
        }


        return isPendingPayeeNumber;
    }

    /**
     * Checks whether the given tax id number matches a current employee tax number.
     * @param taxIDNumber
     * @return
     */
    private boolean isEmployeeTaxNumber(String taxIDNumber) {
        boolean isEmployeeNumber = false;

        // TODO: implement LDAP search of tax number to find employee uuid

        return isEmployeeNumber;
    }

    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#getEmployeeNumber(java.lang.String, java.lang.String)
     */
    public String getEmployeeNumber(String taxIDNumber, String taxpayerTypeCode) {
        String employeeNumber = null;


        return employeeNumber;
    }

    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#getPayeeNumber(java.lang.String, java.lang.String)
     */
    public String getPayeeNumber(String taxIDNumber, String taxpayerTypeCode) {
        String payeeNumber = null;

        Map taxIDCrit = new HashMap();
        taxIDCrit.put("taxIdNumber", taxIDNumber);
        taxIDCrit.put("taxpayerTypeCode", taxpayerTypeCode);
        Collection foundPayees = businessObjectService.findMatching(Payee.class, taxIDCrit);

        if (!foundPayees.isEmpty()) {
            Payee payee = (Payee) foundPayees.iterator().next();
            payeeNumber = payee.getPayeeIdNumber();
        }

        return payeeNumber;
    }

    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#getVendorNumber(java.lang.String, java.lang.String)
     */
    public String getVendorNumber(String taxIDNumber, String taxpayerTypeCode) {
        String vendorNumber = null;

        return vendorNumber;
    }

    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#getPendingPayeeNumber(java.lang.String,
     *      java.lang.String)
     */
    public String getPendingPayeeNumber(String taxIDNumber, String taxpayerTypeCode) {
        String pendingPayeeNumber = null;

        List pendingPayees = maintenanceDocumentService.getPendingObjects(Payee.class);

        for (Iterator iter = pendingPayees.iterator(); iter.hasNext();) {
            Payee pendingPayee = (Payee) iter.next();
            if (taxIDNumber.equals(pendingPayee.getTaxIdNumber()) && taxpayerTypeCode.equals(pendingPayee.getTaxpayerTypeCode())) {
                pendingPayeeNumber = pendingPayee.getPayeeIdNumber();
            }
        }

        return pendingPayeeNumber;
    }


    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#generateNRATaxLines(org.kuali.module.financial.document.DisbursementVoucherDocument)
     */
    private List generateNRATaxLines(DisbursementVoucherDocument document) {
        // retrieve first accounting line for tax line attributes
        AccountingLine line1 = document.getSourceAccountingLine(0);

        List taxLineNumbers = new ArrayList();

        // generate gross up
        if (document.getDvNonResidentAlienTax().isIncomeTaxGrossUpCode()) {
            AccountingLine grossLine = new SourceAccountingLine();

            grossLine.setFinancialDocumentNumber(document.getFinancialDocumentNumber());
            grossLine.setSequenceNumber(document.getNextSourceLineNumber());
            grossLine.setChartOfAccountsCode(line1.getChartOfAccountsCode());
            grossLine.setAccountNumber(line1.getAccountNumber());
            grossLine.setFinancialObjectCode(line1.getFinancialObjectCode());

            // calculate gross up amount and set as line amount
            BigDecimal federalTaxPercent = document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent().bigDecimalValue();
            BigDecimal stateTaxPercent = document.getDvNonResidentAlienTax().getStateIncomeTaxPercent().bigDecimalValue();
            BigDecimal documentAmount = document.getDisbVchrCheckTotalAmount().bigDecimalValue();

            KualiDecimal grossAmount1 = new KualiDecimal((documentAmount.multiply(federalTaxPercent).divide(new BigDecimal(100)
                    .subtract(federalTaxPercent).subtract(stateTaxPercent), 5, BigDecimal.ROUND_HALF_UP)));
            KualiDecimal grossAmount2 = new KualiDecimal((documentAmount.multiply(stateTaxPercent).divide(new BigDecimal(100)
                    .subtract(federalTaxPercent).subtract(stateTaxPercent), 5, BigDecimal.ROUND_HALF_UP)));
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
            String federalTaxChart = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(
                    DisbursementVoucherRuleConstants.NRA_TAX_PARM_GROUP_NM,
                    DisbursementVoucherRuleConstants.FEDERAL_TAX_CHART_PARM_NM);
            String federalTaxAccount = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(
                    DisbursementVoucherRuleConstants.NRA_TAX_PARM_GROUP_NM,
                    DisbursementVoucherRuleConstants.FEDERAL_TAX_ACCOUNT_PARM_NM);
            String federalTaxObjectCode = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(
                    DisbursementVoucherRuleConstants.NRA_TAX_PARM_GROUP_NM,
                    DisbursementVoucherRuleConstants.FEDERAL_OBJECT_CODE_PARM_PREFIX
                            + document.getDvNonResidentAlienTax().getIncomeClassCode());
            if (StringUtils.isBlank(federalTaxChart) || StringUtils.isBlank(federalTaxAccount)
                    || StringUtils.isBlank(federalTaxObjectCode)) {
                LOG.error("Unable to retrieve federal tax parameters.");
                throw new RuntimeException("Unable to retrieve federal tax parameters.");
            }

            AccountingLine federalTaxLine = generateTaxAccountingLine(document, federalTaxChart, federalTaxAccount,
                    federalTaxObjectCode, document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent(), taxableAmount);

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
            String stateTaxChart = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(
                    DisbursementVoucherRuleConstants.NRA_TAX_PARM_GROUP_NM,
                    DisbursementVoucherRuleConstants.STATE_TAX_CHART_PARM_NM);
            String stateTaxAccount = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(
                    DisbursementVoucherRuleConstants.NRA_TAX_PARM_GROUP_NM,
                    DisbursementVoucherRuleConstants.STATE_TAX_ACCOUNT_PARM_NM);
            String stateTaxObjectCode = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(
                    DisbursementVoucherRuleConstants.NRA_TAX_PARM_GROUP_NM,
                    DisbursementVoucherRuleConstants.STATE_OBJECT_CODE_PARM_PREFIX
                            + document.getDvNonResidentAlienTax().getIncomeClassCode());
            if (StringUtils.isBlank(stateTaxChart) || StringUtils.isBlank(stateTaxAccount)
                    || StringUtils.isBlank(stateTaxObjectCode)) {
                LOG.error("Unable to retrieve state tax parameters.");
                throw new RuntimeException("Unable to retrieve state tax parameters.");
            }

            AccountingLine stateTaxLine = generateTaxAccountingLine(document, stateTaxChart, stateTaxAccount, stateTaxObjectCode,
                    document.getDvNonResidentAlienTax().getStateIncomeTaxPercent(), taxableAmount);

            // put line number in line number list, and update next line property in document
            taxLineNumbers.add(stateTaxLine.getSequenceNumber());
            document.setNextSourceLineNumber(new Integer(document.getNextSourceLineNumber().intValue() + 1));

            // add to source accounting lines
            stateTaxLine.refresh();
            document.getSourceAccountingLines().add(stateTaxLine);

            // udpate check total, is added because line amount is negative, so this will take check amount down
            document.setDisbVchrCheckTotalAmount(document.getDisbVchrCheckTotalAmount().add(stateTaxLine.getAmount()));
        }

        return taxLineNumbers;
    }

    /**
     * Generates an accounting line for the chart, account, object code, & tax percentage.
     * @param document
     * @returns
     */
    private AccountingLine generateTaxAccountingLine(DisbursementVoucherDocument document, String chart, String account,
            String objectCode, KualiDecimal taxPercent, KualiDecimal taxableAmount) {
        AccountingLine taxLine = new SourceAccountingLine();

        taxLine.setFinancialDocumentNumber(document.getFinancialDocumentNumber());
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
    public List processNonResidentAlienTax(DisbursementVoucherDocument document) {
        List newTaxLineNumbers = new ArrayList();

        /* validate nra information */
        if (validateNRATaxInformation(document)) {
            newTaxLineNumbers = generateNRATaxLines(document);
        }

        return newTaxLineNumbers;
    }

    /**
     * Removes tax lines from the document's accounting lines and updates the check total.
     * @param document
     * @param previousTaxLineNumbers
     */
    public void clearNRATaxLines(DisbursementVoucherDocument document, List previousTaxLineNumbers) {
        List taxLines = new ArrayList();
        KualiDecimal taxTotal = new KualiDecimal(0);

        // get tax lines out of source lines
        for (Iterator iter = document.getSourceAccountingLines().iterator(); iter.hasNext();) {
            AccountingLine line = (AccountingLine) iter.next();
            if (previousTaxLineNumbers.contains(line.getSequenceNumber())) {
                taxLines.add(line);
                taxTotal = taxTotal.add(line.getAmount().abs());
            }
        }

        // remove tax lines
        document.getSourceAccountingLines().removeAll(taxLines);

        // update check total if not grossed up (3 previous accounting lines)
        if (previousTaxLineNumbers.size() != 3) {
          document.setDisbVchrCheckTotalAmount(document.getDisbVchrCheckTotalAmount().add(taxTotal));
        }
    }


    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTaxService#validateNRATaxInformation(org.kuali.module.financial.document.DisbursementVoucherDocument)
     */
    private boolean validateNRATaxInformation(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        /* call dv rule to do general nra validation */
        DisbursementVoucherDocumentRule documentRule = new DisbursementVoucherDocumentRule();
        documentRule.validateNonResidentAlienInformation(document);

        if (!GlobalVariables.getErrorMap().isEmpty()) {
            return false;
        }

        /* make sure payee is nra */
        if (!document.getDvPayeeDetail().isDisbVchrAlienPaymentCode()) {
            errors.put("DVNRATaxErrors", KeyConstants.ERROR_DV_GENERATE_TAX_NOT_NRA);
            return false;
        }

        /* don't generate tax if reference doc is given */
        if (StringUtils.isNotBlank(document.getDvNonResidentAlienTax().getFinancialDocumentReferenceNbr())) {
            errors.put("DVNRATaxErrors", KeyConstants.ERROR_DV_GENERATE_TAX_DOC_REFERENCE);
            return false;
        }


        // check attributes needed to generate lines
        /* need at least 1 line */
        if (!(document.getSourceAccountingLines().size() >= 1)) {
            errors.put("DVNRATaxErrors", KeyConstants.ERROR_DV_GENERATE_TAX_NO_SOURCE);
            return false;
        }

        /* make sure both fed and state tax percents are not 0, in which case there is no need to generate lines */
        if (new KualiDecimal(0).equals(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent())
                && new KualiDecimal(0).equals(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent())) {
            errors.put("DVNRATaxErrors", KeyConstants.ERROR_DV_GENERATE_TAX_BOTH_0);
            return false;
        }

        /* check total cannot be negative */
        if (Constants.ZERO.compareTo(document.getDisbVchrCheckTotalAmount()) == 1) {
            errors.put("document.disbVchrCheckTotalAmount", KeyConstants.ERROR_NEGATIVE_CHECK_TOTAL);
            return false;
        }

        /* total accounting lines cannot be negative */
        if (Constants.ZERO.compareTo(document.getSourceTotal()) == 1) {
            errors.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_NEGATIVE_ACCOUNTING_TOTAL);
            return false;
        }

        /* total of accounting lines must match check total */
        if (document.getDisbVchrCheckTotalAmount().compareTo(document.getSourceTotal()) != 0) {
            errors.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_CHECK_ACCOUNTING_TOTAL);
            return false;
        }

        return true;
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
}