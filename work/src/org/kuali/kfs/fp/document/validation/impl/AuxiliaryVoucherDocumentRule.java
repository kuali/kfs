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
package org.kuali.module.financial.rules;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

/**
 * This class...
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class AuxiliaryVoucherDocumentRule extends TransactionalDocumentRuleBase {
    //TODO refactor and move up to parent class
    private static final String AUX_VOUCHER_ADJUSTMENT_DOC_TYPE = "AVAD";
    private static final String AUX_VOUCHER_RECODE_DOC_TYPE = "AVRC";
    private static final String AUX_VOUCHER_ACCRUAL_DOC_TYPE = "AVAE";
        
    protected static final Set _invalidObjectCodeSubTypes = new TreeSet();    
    protected static final Set _invalidPeriodCodes = new TreeSet();
    
    static {
        _invalidPeriodCodes.add("AB");
        _invalidPeriodCodes.add("BB");
        _invalidPeriodCodes.add("CB");
    }
    
    static {
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.ART_AND_MUSEUM);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.BLDG_FED_FUNDED);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_FED_FUND);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.CASH);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.BUDGET_ONLY);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.BLDG);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.CAP_LEASE_PURCHASE);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.LIBRARY_ACQ_FED_FUND);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.LEASE_IMPROVEMENTS);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.LAND);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.INFRASTRUCTURE);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.SUBTYPE_FUND_BALANCE);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.EQUIP_STARTUP_COSTS);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_OTHER_OWN);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_OWN);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.PLANT);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.PLANT_INDEBT);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.MANDATORY_TRANSFER);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.LIBRARY_ACQ);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.ASSESSMENT);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.TRANSFER_OF_FUNDS);
    }
    
    protected KualiDecimal getGeneralLedgerPendingEntryAmountForAccountingLine(AccountingLine accountingLine) {
        KualiDecimal amount = accountingLine.getAmount();
        if(amount.isNegative()) {
            throw new IllegalStateException(objectTypeCodeIllegalStateExceptionMessage);
        }
        
        return amount;
    } 
    
    /**
     * Overrides the parent to return true, because Auxiliary Voucher documents only use the SourceAccountingLines data structures.
     * The list that holds TargetAccountingLines should be empty. This will be checked when the document is "routed" or submitted to
     * post - it's called automatically by the parent's processRouteDocument method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        return true;
    }
    
    /**
     * Overrides the parent to return true, because Auxiliary Voucher documents aren't restricted from using any object code. This is
     * part of the "save" check that gets done. This method is called automatically by the parent's processSaveDocument method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isObjectCodeAllowed(org.kuali.core.bo.AccountingLine)
     */
    public boolean isObjectCodeAllowed(AccountingLine accountingLine) {
        return true;
    }
    
    /**
     * Accounting lines for Journal Vouchers can be positive or negative, just not "$0.00".
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isAmountValid(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isAmountValid(TransactionalDocument document, AccountingLine accountingLine) {
        KualiDecimal ZERO = new KualiDecimal(0);
        KualiDecimal amount = accountingLine.getAmount();

        // Check for zero amounts
        if (ZERO.compareTo(amount) == 0) { // amount == 0
            GlobalVariables.getErrorMap().put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_ZERO_AMOUNT,
                    "an accounting line");
            return false;
        }

        return true;
    }
    
    /**
     * This is the default implementation for Transactional Documents, which sums the amounts of all of the Source Accounting Lines,
     * and compares it to the total of all of the Target Accounting Lines. In general, this algorithm works, but it does not work
     * for some specific documents such as the Journal Voucher. The method name denotes not an expected behavior, but a more general
     * title so that some documents that don't use this default implementation, can override just this method without having to
     * override the calling method.
     * 
     * @param transactionalDocument
     * @return boolean True if the document is balanced, false otherwise.
     */
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        AuxiliaryVoucherDocument document = (AuxiliaryVoucherDocument)transactionalDocument;
        KualiDecimal ZERO = new KualiDecimal(0);
        if(!(ZERO.compareTo(document.getTotal()) == 0)) {
            GlobalVariables.getErrorMap()
                .put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_BALANCE);
            return false;
        }
        return true;
    }
        
    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processExplicitGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper, org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    protected boolean processExplicitGeneralLedgerPendingEntry(TransactionalDocument document, 
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine line, 
            GeneralLedgerPendingEntry explicitEntry) {
        AuxiliaryVoucherDocument auxVoucher = (AuxiliaryVoucherDocument) document;
        String voucherType = getVoucherTypeCode(document);
        AccountingPeriod postingPeriod = auxVoucher.getAccountingPeriod();
        String postingPeriodString = postingPeriod.getUniversityFiscalPeriodCode();
        
        //first check for the odd posting periods, AA, BB, CB and treat it like period 13
        if(_invalidPeriodCodes.contains(postingPeriodString)) {
            GlobalVariables.getErrorMap()
                .put(Constants.ACCOUNTING_PERIOD_STATUS_CODE_FIELD, KeyConstants.ERROR_CUSTOM, "You have entered an incorrect posting period, it must be a number between 1 and 13.");
            return false;
        }
        Integer postingPeriodInt = new Integer(0);
        
        try {
            postingPeriodInt = Integer.valueOf(postingPeriodString);
        } catch (NumberFormatException nfe) {
            GlobalVariables.getErrorMap()
                .put(Constants.ACCOUNTING_PERIOD_STATUS_CODE_FIELD, KeyConstants.ERROR_CUSTOM, "You have entered an incorrect posting period, it must be a number between 1 and 13.");
            return false;
        }
        
        String objectType = getObjectTypeNormalAV(line);
        if (!setReversalDate(auxVoucher, voucherType, postingPeriod)) {
            return false;
        }
        if(!isValidPeriod(auxVoucher, postingPeriodInt.intValue(), auxVoucher.getAccountingPeriod().getUniversityFiscalYear().intValue())) {
            return false;
        }
        
        if(AUX_VOUCHER_RECODE_DOC_TYPE.equals(voucherType)) {
            
            GeneralLedgerPendingEntry avrcEntry = new GeneralLedgerPendingEntry();
            populateExplicitGeneralLedgerPendingEntry(document, line, sequenceHelper, avrcEntry);
            avrcEntry.setFinancialDocumentReversalDate(new java.sql.Date(auxVoucher.getReversalDate().getTime()));
            avrcEntry.setFinancialDocumentTypeCode(AUX_VOUCHER_RECODE_DOC_TYPE);
            avrcEntry.setFinancialObjectTypeCode(objectType);
            document.addGeneralLedgerPendingEntry(avrcEntry);
            
            //now we need to code our explicit entry (which will be used to create the offset entry
            //later
            String diObjectType = getObjectTypeRecodeAV(objectType);
            explicitEntry.setFinancialDocumentTypeCode(OBJECT_TYPE_CODE.DOCUMENT_TYPE_DISTRIBUTION_OF_INCOME_AND_EXPENSE);
            explicitEntry.setFinancialObjectTypeCode(diObjectType);
            
            document.addGeneralLedgerPendingEntry(explicitEntry);
            sequenceHelper.increment();
            
        } else if (AUX_VOUCHER_ADJUSTMENT_DOC_TYPE.equals(voucherType)) {
            explicitEntry.setFinancialDocumentReversalDate(new java.sql.Date(auxVoucher.getReversalDate().getTime()));
            explicitEntry.setFinancialDocumentTypeCode(AUX_VOUCHER_ADJUSTMENT_DOC_TYPE);
            explicitEntry.setFinancialObjectTypeCode(objectType);
            return true;
        } else if (AUX_VOUCHER_ACCRUAL_DOC_TYPE.equals(voucherType)) {
            explicitEntry.setFinancialDocumentReversalDate(new java.sql.Date(auxVoucher.getReversalDate().getTime()));
            explicitEntry.setFinancialDocumentTypeCode(AUX_VOUCHER_ACCRUAL_DOC_TYPE);
            explicitEntry.setFinancialObjectTypeCode(objectType);
            return true;
        }
        return true;
    }
    
    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processOffsetGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper, org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    protected boolean processOffsetGeneralLedgerPendingEntry(TransactionalDocument document, 
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine line, 
            GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        String voucherType = getVoucherTypeCode(document);
        
        if(AUX_VOUCHER_RECODE_DOC_TYPE.equals(voucherType)) {
            offsetEntry.setFinancialDocumentTypeCode(OBJECT_TYPE_CODE.DOCUMENT_TYPE_DISTRIBUTION_OF_INCOME_AND_EXPENSE);
            //we need to do this because the main class still thinks that there are only two entries
            sequenceHelper.increment();        
        } else {
            //the offsets are automatically generated otherwise
            sequenceHelper.decrement();
            return true;
        }
        return true;
    }
    
    /**
     * 
     * This method returns the appropriate voucher type code based on the document passed in
     * @param document
     * @return voucher type code (AVAE, AVAD, AVRC)
     */
    protected String getVoucherTypeCode(TransactionalDocument document) {
        AuxiliaryVoucherDocument auxVoucher = (AuxiliaryVoucherDocument) document;
        String voucherType = auxVoucher.getTypeCode();
        
        if(voucherType.equals("") || !voucherType.equals(AUX_VOUCHER_ACCRUAL_DOC_TYPE)
                || !voucherType.equals(AUX_VOUCHER_ADJUSTMENT_DOC_TYPE) 
                || !voucherType.equals(AUX_VOUCHER_RECODE_DOC_TYPE)) {
            voucherType = AUX_VOUCHER_ACCRUAL_DOC_TYPE;
        }
        return voucherType;
    }
    
    /**
     * 
     * This method examines the object type passed in and converts it to another object
     * type string depending on what it is. This value is then used when creating the 
     * explicit entry and offset entry
     * @param line
     * @return object type for a normal AuxiliaryVoucher document (not AVRC)
     */
    protected String getObjectTypeNormalAV(AccountingLine line) {
        String objectType = line.getObjectCode().getFinancialObjectType().getCode();
        String returnObjType = line.getObjectCode().getFinancialObjectType().getCode();
        if(OBJECT_TYPE_CODE.EXPENSE_EXPENDITURE.equals(objectType) || OBJECT_TYPE_CODE.EXPENDITURE_NOT_EXPENSE.equals(objectType)) {
            returnObjType = OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE;
        } else if(OBJECT_TYPE_CODE.INCOME_CASH.equals(objectType) || OBJECT_TYPE_CODE.EXPENDITURE_NOT_EXPENSE.equals(objectType)) {
            returnObjType = OBJECT_TYPE_CODE.INCOME_NOT_CASH;
        }
        return returnObjType;
    }
    
    /**
     * 
     * This method is almost identical in functionality to <code>String getObjectTypeNormalAV</code>
     * but it calculates the proper object type for a recode (AVRC) doc
     * This method should be called with the results of the first method for proper calculations
     * @param objectType
     * @return object type for a AuxiliaryVoucher document (not AVRC)
     */
    protected String getObjectTypeRecodeAV(String objectType) {
        String returnObjType = objectType;
        if(OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE.equals(objectType)) {
            returnObjType = OBJECT_TYPE_CODE.EXPENSE_EXPENDITURE;
        } else if(OBJECT_TYPE_CODE.INCOME_NOT_CASH.equals(objectType)) {
            returnObjType = OBJECT_TYPE_CODE.INCOME_CASH;
        }
        return returnObjType;
    }
    
    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine)
     */
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomAddAccountingLineBusinessRules( document, accountingLine ) && validateAccountingLine( document, accountingLine );
    }
    /**
     * @see org.kuali.core.rule.ReviewAccountingLineRule#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine)
     */
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomReviewAccountingLineBusinessRules( document, accountingLine ) && validateAccountingLine( document, accountingLine );
    }

    /**
     * @param document
     * @param accountingLine
     * @return true if the given accountingLine is valid
     */
    protected boolean validateAccountingLine(TransactionalDocument document, AccountingLine accountingLine) {
        if (accountingLine.getAmount().isNegative() 
                || document.getDocumentHeader().getFinancialDocumentInErrorNumber() == null) {
            GlobalVariables.getErrorMap()
                .put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_INVALID_NEGATIVE_AMOUNT, "Amount");
            return false;
        }
        
        SpringServiceLocator.getPersistenceService().linkObjects(accountingLine);

        boolean retval = true;
        retval &= !_invalidObjectCodeSubTypes.contains(accountingLine.getObjectCode().getFinancialObjectType().getCode());
        retval &= isValidDocWithSubAndLevel(document, accountingLine);
        return retval;
    }
    
    /**
     * 
     * This method checks to see if there is a valid combination of sub type and object level
     * @param document
     * @param accountingLine
     * @return
     */
    private boolean isValidDocWithSubAndLevel(TransactionalDocument document, AccountingLine accountingLine) {
        if(document instanceof AuxiliaryVoucherDocument) {
            if(accountingLine.getObjectCode().getFinancialObjectType().getCode().equals(OBJECT_SUB_TYPE_CODE.VALUATIONS_AND_ADJUSTMENTS) 
                    && accountingLine.getObjectType().getCode().equals(OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE) 
                    && accountingLine.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode().equals(OBJECT_LEVEL_CODE.VALUATIONS_ADJUSTMENTS) ) {
                String errorObjects[] = { 
                        accountingLine.getObjectCode().getFinancialObjectCode(),
                        accountingLine.getObjectCode().getFinancialObjectType().getCode(),
                        accountingLine.getObjectType().getCode(),
                        accountingLine.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode()
                };
                GlobalVariables.getErrorMap()
                    .put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE, 
                            errorObjects);
                return false;
            } else {
                return true;
            }
        } else {
            GlobalVariables.getErrorMap()
            .put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_CUSTOM, 
                    "Incorrect Document Type: " + document.getDocumentTitle());
            return false;
        }
    }
    
    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.TransactionalDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(TransactionalDocument document) {
        return isDocumentBalanceValid(document);
    }
    
    /**
     * 
     * This method creates a reversal date for our document
     * @param doc
     * @param voucherType
     * @param postPeriod
     * @return if reversal date creation succeeded true
     */
    protected boolean setReversalDate(AuxiliaryVoucherDocument doc, String voucherType, AccountingPeriod postPeriod) {
        //reversal date in period 13 should have the same reversal date as period 12
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        Timestamp ts = new Timestamp(date.getTime());
        if(voucherType.equals(AUX_VOUCHER_RECODE_DOC_TYPE)) {
            //AVRC means the reversal date is the same as the creation date
            ts = doc.getDocumentHeader().getWorkflowDocument().getCreateDate();
        } else if (voucherType.equals(AUX_VOUCHER_ADJUSTMENT_DOC_TYPE)) {
            //there is no reversal date for AVAD
            ts = null;
        } else {
            //it must be AVAE
            //first check to see if there is an existing reversal date
            Timestamp revDate = doc.getReversalDate();
            if(revDate != null) {
                //now we need to check to make sure that it is greater than today's date first
                if(revDate.compareTo(ts) >= 0) {
                    ts = revDate;
                } else {
                    GlobalVariables.getErrorMap()
                        .put("document.reversalDate", KeyConstants.ERROR_DOCUMENT_INCORRECT_REVERSAL_DATE);
                    return false;
                }
            } else {
                //grab the actual date from the period
                Timestamp endDate = postPeriod.getUniversityFiscalPeriodEndDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(endDate.getTime()));
                cal.set(Calendar.DATE, 15);
                ts = new Timestamp(cal.getTimeInMillis());
            }
        }
        doc.setReversalDate(ts);
        return true;
    }
    
    /**
     * 
     * This method determins if the posting period is valid for the document type
     * @param document
     * @param period
     * @param year
     * @return true if it is a valid period for posting into
     */
    protected boolean isValidPeriod(AuxiliaryVoucherDocument document, int period, int year) {
        //first we need to get the period itself to check these things
        AccountingPeriodService perService = SpringServiceLocator.getAccountingPeriodService();
        AccountingPeriod acctPeriod = perService.getByPeriod(new Integer(period).toString(), new Integer(year));
        
        //can't post into a closed period
        if(acctPeriod.getUniversityFiscalPeriodStatusCode().equalsIgnoreCase(Constants.ACCOUNTING_PERIOD_STATUS_CLOSED)) {
            GlobalVariables.getErrorMap()
                .put(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED);
            return false;
        }
        
        //if current period is period 1 can't post back more than 3 open periods
        //grab the current period
        Timestamp ts = document.getDocumentHeader().getWorkflowDocument().getCreateDate();
        AccountingPeriod currPeriod = perService.getByDate(new Date(ts.getTime()));
        int currPeriodVal = new Integer(currPeriod.getUniversityFiscalPeriodCode()).intValue();
        if(currPeriodVal == 1) {
            if(period < 11) {
                GlobalVariables.getErrorMap()
                    .put(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_DOCUMENT_ACCOUNTING_PERIOD_THREE_OPEN);
                return false;
            }
        }
        
        //can't post back more than 2 periods
        if(period < currPeriodVal) {
            if((currPeriodVal - period) > 2) {
                GlobalVariables.getErrorMap()
                    .put(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                return false;
            }
        } else {
            //if currPeriodVal is less than period then that means it can only be
            //period 2 (see period 1 rule above)and period 13 as the only possible combination, 
            //if not then error out
            if(currPeriodVal != 2) {
                GlobalVariables.getErrorMap()
                    .put(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                return false;
            } else {
                if(period != 13) {
                    GlobalVariables.getErrorMap()
                        .put(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                    return false;
                }
            }
        }
        
        //check for specific posting issues
        String voucherType = getVoucherTypeCode(document);
        if(AUX_VOUCHER_RECODE_DOC_TYPE.equals(voucherType)) {
            //can't post into a previous fiscal year
            int currFiscalYear = currPeriod.getUniversityFiscalYear().intValue();
            if(!(currFiscalYear < year)) {
                GlobalVariables.getErrorMap()
                    .put(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_DOCUMENT_AV_INCORRECT_FISCAL_YEAR_AVRC);
                return false;
            }
            //check the posting period, throw out if period 13
            if(period > 12) {
                GlobalVariables.getErrorMap()
                    .put(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC);
                return false;
            } else if(period < 1) {
                GlobalVariables.getErrorMap()
                    .put(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_CUSTOM, "You have entered an incorrect posting period, it must be a number between 1 and 13.");
                return false;
            }
        }
        return true;
    }
}
