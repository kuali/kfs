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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonResidentAlienTax;
import org.kuali.kfs.fp.businessobject.NonResidentAlienTaxPercent;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService;
import org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherNonResidentAlienInformationValidation;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.MaintenanceDocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * This is the default implementation of the PaymentSourceExtractionService interface.
 * This class handles queries and validation on tax id numbers.
 */
public class DisbursementVoucherTaxServiceImpl implements DisbursementVoucherTaxService, DisbursementVoucherConstants {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherTaxServiceImpl.class);

    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private MaintenanceDocumentService maintenanceDocumentService;

    /**
     * This method retrieves the universal id of the individual or business entity who matches the tax id number and type
     * code given.
     * 
     * @param taxIDNumber The tax identification number of the user being retrieved.
     * @param taxPayerTypeCode The tax payer type code of the user being retrieved.  See the TAX_TYPE_* constants defined in 
     *                         DisbursementVoucherRuleConstants for examples of valid tax type codes.
     * @return The universal id of the individual who matches the tax id and type code given.  Null if no matching user is found.
     * 
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService#getEmployeeNumber(java.lang.String, java.lang.String)
     */
    public String getUniversalId(String taxIDNumber, String taxPayerTypeCode) {
        if (TAX_TYPE_FEIN.equals(taxPayerTypeCode)) {
            return null;
        }

        Person person = (Person) SpringContext.getBean(PersonService.class).getPersonByExternalIdentifier(org.kuali.rice.kim.api.KimConstants.PersonExternalIdentifierTypes.TAX, taxIDNumber).get(0);
        
        String universalId = null;
        if (person != null) {
            universalId = person.getPrincipalId();
        }
        return universalId;
    }

    /**
     * This method retrieves the vendor identification code for the vendor found who has a matching tax id and tax payer type 
     * code.
     * 
     * @param taxIDNumber The tax id number used to retrieve the associated vendor.
     * @param taxPayerTypeCode The tax payer type code used to retrieve the associated vendor.  See the TAX_TYPE_* constants defined in 
     *                         DisbursementVoucherRuleConstants for examples of valid tax type codes.
     * @return The id of the vendor found matching the tax id and type code provided.  Null if no matching vendor is found.
     * 
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService#getPayeeNumber(java.lang.String, java.lang.String)
     */
    public String getVendorId(String taxIDNumber, String taxPayerTypeCode) {
        String vendorId = null;

        Map taxIDCrit = new HashMap();
        taxIDCrit.put("taxIdNumber", taxIDNumber);
        taxIDCrit.put("taxpayerTypeCode", taxPayerTypeCode);
        Collection<VendorDetail> foundPayees = businessObjectService.findMatching(VendorDetail.class, taxIDCrit);

        if (!foundPayees.isEmpty()) {
            VendorDetail vendor = (VendorDetail) foundPayees.iterator().next();
            vendorId = vendor.getVendorHeaderGeneratedIdentifier().toString();
        }

        return vendorId;
    }

    /**
     * This method generates non-resident alien (NRA) tax lines for the given disbursement voucher.  
     * 
     * The NRA tax lines consist of three possible sets of tax lines: 
     * - Gross up tax lines
     * - Federal tax lines
     * - State tax lines
     * 
     * Gross up tax lines are generated if the income tax gross up code is set on the DisbursementVoucherNonResidentAlienTax 
     * attribute of the disbursement voucher.
     * 
     * Federal tax lines are generated if the federal tax rate in the DisbursementVoucherNonResidentAlienTax attribute is
     * other than zero.
     * 
     * State tax lines are generated if the state tax rate in the DisbursementVoucherNonResidentAlienTax attribute is
     * other than zero.
     * 
     * @param document The disbursement voucher the NRA tax lines will be added to.
     * 
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService#generateNRATaxLines(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    protected void generateNRATaxLines(DisbursementVoucherDocument document) {
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

            // update check total, is added because line amount is negative, so this will take check amount down
            document.setDisbVchrCheckTotalAmount(document.getDisbVchrCheckTotalAmount().add(grossLine.getAmount()));
        }

        KualiDecimal taxableAmount = document.getDisbVchrCheckTotalAmount();

        // generate federal tax line
        if (!(KualiDecimal.ZERO.equals(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent()))) {
            String federalTaxChart = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.FEDERAL_TAX_PARM_PREFIX + DisbursementVoucherConstants.TAX_PARM_CHART_SUFFIX);
            String federalTaxAccount = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.FEDERAL_TAX_PARM_PREFIX + DisbursementVoucherConstants.TAX_PARM_ACCOUNT_SUFFIX);
            String federalTaxObjectCode = parameterService.getSubParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.FEDERAL_TAX_PARM_PREFIX + DisbursementVoucherConstants.TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX, document.getDvNonResidentAlienTax().getIncomeClassCode());
            if (StringUtils.isBlank(federalTaxChart) || StringUtils.isBlank(federalTaxAccount) || StringUtils.isBlank(federalTaxObjectCode)) {
                LOG.error("Unable to retrieve federal tax parameters.");
                throw new RuntimeException("Unable to retrieve federal tax parameters.");
            }

            AccountingLine federalTaxLine = generateTaxAccountingLine(document, federalTaxChart, federalTaxAccount, federalTaxObjectCode, document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent(), taxableAmount);

            // put line number in line number list, and update next line property in document
            taxLineNumbers.add(federalTaxLine.getSequenceNumber());
            document.setNextSourceLineNumber(new Integer(document.getNextSourceLineNumber().intValue() + 1));

            // add to source accounting lines
            federalTaxLine.refresh();
            document.getSourceAccountingLines().add(federalTaxLine);

            // update check total, is added because line amount is negative, so this will take check amount down
            document.setDisbVchrCheckTotalAmount(document.getDisbVchrCheckTotalAmount().add(federalTaxLine.getAmount()));
        }

        // generate state tax line
        if (!(KualiDecimal.ZERO.equals(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent()))) {
            String stateTaxChart = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.STATE_TAX_PARM_PREFIX + DisbursementVoucherConstants.TAX_PARM_CHART_SUFFIX);
            String stateTaxAccount = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.STATE_TAX_PARM_PREFIX + DisbursementVoucherConstants.TAX_PARM_ACCOUNT_SUFFIX);
            String stateTaxObjectCode = parameterService.getSubParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.STATE_TAX_PARM_PREFIX + DisbursementVoucherConstants.TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX, document.getDvNonResidentAlienTax().getIncomeClassCode());

            if (StringUtils.isBlank(stateTaxChart) || StringUtils.isBlank(stateTaxAccount) || StringUtils.isBlank(stateTaxObjectCode)) {
                LOG.error("Unable to retrieve state tax parameters.");
                throw new RuntimeException("Unable to retrieve state tax parameters.");
            }

            AccountingLine stateTaxLine = generateTaxAccountingLine(document, stateTaxChart, stateTaxAccount, stateTaxObjectCode, document.getDvNonResidentAlienTax().getStateIncomeTaxPercent(), taxableAmount);

            // put line number in line number list, and update next line property in document
            taxLineNumbers.add(stateTaxLine.getSequenceNumber());
            document.setNextSourceLineNumber(new Integer(document.getNextSourceLineNumber().intValue() + 1));

            // add to source accounting lines
            stateTaxLine.refresh();
            document.getSourceAccountingLines().add(stateTaxLine);

            // update check total, is added because line amount is negative, so this will take check amount down
            document.setDisbVchrCheckTotalAmount(document.getDisbVchrCheckTotalAmount().add(stateTaxLine.getAmount()));
        }

        // update line number field
        document.getDvNonResidentAlienTax().setFinancialDocumentAccountingLineText(StringUtils.join(taxLineNumbers.iterator(), ","));
    }

    /**
     * Generates an accounting line for the chart, account, object code & tax percentage values given.
     * 
     * @param document The disbursement voucher the tax will be applied to.
     * @param chart The chart code to be assigned to the accounting line generated.
     * @param account The account code to be assigned to the accounting line generated.
     * @param objectCode The object code used on the accounting line generated.
     * @param taxPercent The tax rate to be used to calculate the tax amount.
     * @param taxableAmount The total amount that is taxable.  This amount is used in conjunction with the tax percent
     *                      to calculate the amount for the accounting lined being generated.
     * @return A fully populated AccountingLine instance representing the amount of tax that will be applied to the 
     *         disbursement voucher provided.
     */
    protected AccountingLine generateTaxAccountingLine(DisbursementVoucherDocument document, String chart, String account, String objectCode, KualiDecimal taxPercent, KualiDecimal taxableAmount) {
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
        KualiDecimal taxAmount = new KualiDecimal(amount.multiply(taxDecimal).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR));
        taxLine.setAmount(taxAmount.negated());

        return taxLine;
    }

    /**
     * This method validates the non-resident alien (NRA) tax information for the document and if the information validates, 
     * the NRA tax lines are generated. 
     * 
     * @param document The disbursement voucher document the NRA tax information will be validated and the subsequent 
     *                 tax lines generated for.
     * 
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService#processNonResidentAlienTax(org.kuali.kfs.fp.document.DisbursementVoucherDocument,
     *      java.util.List)
     */
    public void processNonResidentAlienTax(DisbursementVoucherDocument document) {
        if (validateNRATaxInformation(document)) {
            generateNRATaxLines(document);
        }
    }
    
    /**
     * Removes non-resident alien (NRA) check boxes and sets information to empty values.
     * 
     * @param document The disbursement voucher the NRA tax lines will be removed from.
     */
    public void clearNRATaxInfo(DisbursementVoucherDocument document) {
        
        document.getDvNonResidentAlienTax().setIncomeClassCode(null);
        document.getDvNonResidentAlienTax().setFederalIncomeTaxPercent(null);
        document.getDvNonResidentAlienTax().setStateIncomeTaxPercent(null);
        document.getDvNonResidentAlienTax().setPostalCountryCode(null);
        document.getDvNonResidentAlienTax().setTaxNQIId(null);
        document.getDvNonResidentAlienTax().setReferenceFinancialDocumentNumber(null);
        document.getDvNonResidentAlienTax().setForeignSourceIncomeCode(false);
        document.getDvNonResidentAlienTax().setIncomeTaxTreatyExemptCode(false);
        document.getDvNonResidentAlienTax().setTaxOtherExemptIndicator(false);
        document.getDvNonResidentAlienTax().setIncomeTaxGrossUpCode(false);
        document.getDvNonResidentAlienTax().setTaxUSAIDPerDiemIndicator(false);
        document.getDvNonResidentAlienTax().setTaxSpecialW4Amount(null);

        clearNRATaxLines(document);
        
    }    

    /**
     * Removes non-resident alien (NRA) tax lines from the document's accounting lines and updates the check total.
     * 
     * @param document The disbursement voucher the NRA tax lines will be removed from.
     */
    public void clearNRATaxLines(DisbursementVoucherDocument document) {
        ArrayList<SourceAccountingLine> taxLines = new ArrayList<SourceAccountingLine>();
        KualiDecimal taxTotal = KualiDecimal.ZERO;

        DisbursementVoucherNonResidentAlienTax dvnrat = document.getDvNonResidentAlienTax();
        if (dvnrat != null) {
            List<Integer> previousTaxLineNumbers = getNRATaxLineNumbers(dvnrat.getFinancialDocumentAccountingLineText());

            // get tax lines out of source lines
            boolean previousGrossUp = false;
            List<SourceAccountingLine> srcLines = document.getSourceAccountingLines();
            for (SourceAccountingLine line : srcLines) {
                if (previousTaxLineNumbers.contains(line.getSequenceNumber())) {
                    taxLines.add(line);

                    // check if tax line was a positive amount, in which case we had a gross up
                    if ((KualiDecimal.ZERO).compareTo(line.getAmount()) < 0) {
                        previousGrossUp = true;
                    }
                    else {
                        taxTotal = taxTotal.add(line.getAmount().abs());
                    }
                }
            }

            // remove tax lines
            /*
             * NOTE: a custom remove method needed to be used here because the .equals() method for 
             * AccountingLineBase does not take amount into account when determining equality.  
             * This lead to the issues described in KULRNE-6201.  
             */
            Iterator<SourceAccountingLine> saLineIter  = document.getSourceAccountingLines().iterator();
            while(saLineIter.hasNext()) {
                SourceAccountingLine saLine = saLineIter.next();
                for(SourceAccountingLine taxLine : taxLines) {
                    if(saLine.equals(taxLine)) {
                        if(saLine.getAmount().equals(taxLine.getAmount())) {
                            saLineIter.remove();
                        }
                    }
                }
            }

            // update check total if not grossed up
            if (!previousGrossUp) {
                document.setDisbVchrCheckTotalAmount(document.getDisbVchrCheckTotalAmount().add(taxTotal));
            }

            // clear line string
            dvnrat.setFinancialDocumentAccountingLineText("");
        }
    }

    /**
     * This method retrieves the non-resident alien (NRA) tax amount using the disbursement voucher given to calculate the 
     * amount.  If the vendor is not a non-resident alien or they are and there is no gross up code set, the amount returned 
     * will be zero.  If the vendor is a non-resident alien and gross up has been set, the amount is calculated by 
     * retrieving all the source accounting lines for the disbursement voucher provided and summing the amounts of all the 
     * lines that are NRA tax lines.  
     * 
     * @param document The disbursement voucher the NRA tax line amount will be calculated for.
     * @return The NRA tax amount applicable to the given disbursement voucher or zero if the voucher does not have any 
     *         NRA tax lines.
     * 
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService#getNonResidentAlienTaxAmount(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    public KualiDecimal getNonResidentAlienTaxAmount(DisbursementVoucherDocument document) {
        KualiDecimal taxAmount = KualiDecimal.ZERO;

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
                taxAmount = taxAmount.add(line.getAmount().negated());
            }
        }

        return taxAmount;
    }

    /**
     * This method performs a series of validation checks to ensure that the disbursement voucher given contains non-resident
     * alien specific information and non-resident alien tax lines are necessary.  
     * 
     * The following steps are taken to validate the disbursement voucher given:
     * - Set all percentages (ie. federal, state) to zero if their current value is null.
     * - Call DisbursementVoucherDocumentRule.validateNonResidentAlienInformation to perform more in-depth validation.
     * - The vendor for the disbursement voucher given is a non-resident alien.
     * - No reference document exists for the assigned DisbursementVoucherNonResidentAlienTax attribute of the voucher given.
     * - There is at least one source accounting line to generate the tax line from.
     * - Both the state and federal tax percentages are greater than zero.
     * - The total check amount is not negative.
     * - The total of the accounting lines is not negative.
     * - The total check amount is equal to the total of the accounting lines.
     * 
     * 
     * @param document The disbursement voucher document to validate the tax lines for.
     * @return True if the information associated with non-resident alien tax is correct and valid, false otherwise.
     * 
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService#validateNRATaxInformation(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     * @see org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherDocumentRule#validateNonResidentAlienInformation(DisbursementVoucherDocument)
     */
    protected boolean validateNRATaxInformation(DisbursementVoucherDocument document) {
        MessageMap errors = GlobalVariables.getMessageMap();

        DisbursementVoucherNonResidentAlienInformationValidation dvNRA = new DisbursementVoucherNonResidentAlienInformationValidation();
        dvNRA.setAccountingDocumentForValidation(document);
        dvNRA.setValidationType("GENERATE");
                    
        if(!dvNRA.validate(null)) {
            return false;
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            return false;
        }

        /* make sure vendor is nra */
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
        if (KualiDecimal.ZERO.equals(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent()) && KualiDecimal.ZERO.equals(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent())) {
            errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_GENERATE_TAX_BOTH_0);
            return false;
        }
        
        /* check total cannot be negative */
        if (KualiDecimal.ZERO.compareTo(document.getDisbVchrCheckTotalAmount()) == 1) {
            errors.putErrorWithoutFullErrorPath("document.disbVchrCheckTotalAmount", KFSKeyConstants.ERROR_NEGATIVE_OR_ZERO_CHECK_TOTAL);
            return false;
        }

        /* total accounting lines cannot be negative */
        if (KualiDecimal.ZERO.compareTo(document.getSourceTotal()) == 1) {
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
     * Parses the tax line string given and returns a list of line numbers as Integers.
     * 
     * @param taxLineString The string to be parsed.
     * @return A collection of line numbers represented as Integers.
     */
    public List<Integer> getNRATaxLineNumbers(String taxLineString) {
        List<Integer> taxLineNumbers = new ArrayList();
        if (StringUtils.isNotBlank(taxLineString)) {
            List<String> taxLineNumberStrings = Arrays.asList(StringUtils.split(taxLineString, ","));
            for (String lineNumber : taxLineNumberStrings) {
                taxLineNumbers.add(Integer.valueOf(lineNumber));
            }
        }

        return taxLineNumbers;
    }

    /**
     * This method sets the parameterService attribute to the value given.
     * @param parameterService The ParameterService to be set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the value of the businessObjectService instance.
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService attribute to the value given.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the value of the maintenanceDocumentService instance.
     * @return Returns the maintenanceDocumentService.
     */
    public MaintenanceDocumentService getMaintenanceDocumentService() {
        return maintenanceDocumentService;
    }

    /**
     * This method sets the maintenanceDocumentService attribute to the value given.
     * @param maintenanceDocumentService The maintenanceDocumentService to set.
     */
    public void setMaintenanceDocumentService(MaintenanceDocumentService maintenanceDocumentService) {
        this.maintenanceDocumentService = maintenanceDocumentService;
    }
}

