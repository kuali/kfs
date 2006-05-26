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
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.OriginationCode;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.OriginationCodeService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.ScrubberValidator;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.gl.util.StringHelper;
import org.springframework.util.StringUtils;

/**
 * @author Kuali General Ledger Team <kualigltech@oncourse.indiana.edu>
 * @version $Id$
 */

public class ScrubberValidatorImpl implements ScrubberValidator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberValidatorImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private PersistenceService persistenceService;
    private UniversityDateDao universityDateDao;
    private AccountService accountService;
    private OriginationCodeService originationCodeService;

    private static String[] debitOrCredit = new String[] { Constants.GL_DEBIT_CODE, Constants.GL_CREDIT_CODE };
    private static String[] continuationAccountBypassOriginationCodes = new String[] {"EU", "PL"};
    private static String[] continuationAccountBypassBalanceTypeCodes = new String[] {"EX", "IE", "PE"};
    private static String[] continuationAccountBypassDocumentTypeCodes = new String[] {
        "PREQ", "ACHC", "ACHD", "ACHR", "CHKC",
        "CHKD", "CHKR", "TOPS", "CD", "LOCR"
    };

    public ScrubberValidatorImpl() {
    }

    public List validateTransaction(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate) {
        LOG.debug("validateTransaction() started");

        List errors = new ArrayList();

        // It's important that this check come before the checks for object, sub-object and accountingPeriod
        // because this validation method will set the fiscal year and reload those three objects if the fiscal
        // year was invalid. This will also set originEntry.getOption and workingEntry.getOption. So, it's 
        // probably a good idea to validate the fiscal year first thing.
        String err = validateFiscalYear(originEntry, scrubbedEntry, universityRunDate);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateBalanceType(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateTransactionDate(originEntry, scrubbedEntry, universityRunDate);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateTransactionAmount(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateAccount(originEntry, scrubbedEntry, universityRunDate);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateSubAccount(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateSubObjectCode(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateProjectCode(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateDocumentType(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateOrigination(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateDocumentNumber(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateChart(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateObjectCode(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateObjectType(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateUniversityFiscalPeriodCode(originEntry, scrubbedEntry, universityRunDate);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateEncumbranceUpdateCode(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateReferenceDocument(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        err = validateReversalDate(originEntry, scrubbedEntry);
        if ( err != null ) {
            errors.add(err);
        }

        if ( errors.size() == 0 ) {
            persistenceService.retrieveNonKeyFields(originEntry);
            persistenceService.retrieveNonKeyFields(scrubbedEntry);
        }

        return errors;
    }

    private void setAccount(OriginEntry workingEntry,String chartOfAccountsCode,String accountNumber) {
        workingEntry.setChartOfAccountsCode(chartOfAccountsCode);
        workingEntry.setAccountNumber(accountNumber);
        persistenceService.retrieveReferenceObject(workingEntry, "chart");
        persistenceService.retrieveReferenceObject(workingEntry, "account");
    }

    /**
     * 
     * @param originEntry
     * @param workingEntry
     */
    public String validateAccount(OriginEntry originEntry, OriginEntry workingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateAccount() started");

        if ( ! StringUtils.hasText(originEntry.getAccountNumber())) {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ACCOUNT_NOT_FOUND) + "(" +
                originEntry.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber() + ")";
        }

        if (originEntry.getAccount() == null) {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ACCOUNT_NOT_FOUND) + "(" +
                originEntry.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber() + ")";
        }

        if ("ACLO".equals(originEntry.getFinancialDocumentTypeCode())) {
            setAccount(workingEntry,originEntry.getChartOfAccountsCode(),originEntry.getAccountNumber());
            return null;
        }
        
        Account account = originEntry.getAccount();

        if ( (account.getAccountExpirationDate() == null) && !account.isAccountClosedIndicator()) {
            // account is neither closed nor expired                       
            setAccount(workingEntry,originEntry.getChartOfAccountsCode(),originEntry.getAccountNumber());
            return null;
        }

        // Has an expiration date or is closed
        if ( (org.apache.commons.lang.StringUtils.isNumeric(originEntry.getFinancialSystemOriginationCode()) ||
                ObjectHelper.isOneOf(originEntry.getFinancialSystemOriginationCode(), continuationAccountBypassOriginationCodes)) &&
                account.isAccountClosedIndicator() ) {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ORIGIN_CODE_CANNOT_HAVE_CLOSED_ACCOUNT) + "(" +
                account.getChartOfAccountsCode() + "-" + account.getAccountNumber() + ")";
        }

        if ( (org.apache.commons.lang.StringUtils.isNumeric(originEntry.getFinancialSystemOriginationCode()) ||
                ObjectHelper.isOneOf(originEntry.getFinancialSystemOriginationCode(), continuationAccountBypassOriginationCodes) ||
                ObjectHelper.isOneOf(originEntry.getFinancialBalanceTypeCode(), continuationAccountBypassBalanceTypeCodes) ||
                ObjectHelper.isOneOf(originEntry.getFinancialDocumentTypeCode().trim(), continuationAccountBypassDocumentTypeCodes)) &&
                ! account.isAccountClosedIndicator() ) {
            setAccount(workingEntry,originEntry.getChartOfAccountsCode(),originEntry.getAccountNumber());
            return null;
        }

        Calendar today = Calendar.getInstance();
        today.setTime(universityRunDate.getUniversityDate());

        if (isExpired(account, today) || account.isAccountClosedIndicator()) {
            String error = continuationAccountLogic(originEntry, workingEntry, today);
            if ( error != null ) {
                return error;
            }

            // If the account has changed ...
            if( ! originEntry.getAccount().getAccountNumber().equals(workingEntry.getAccountNumber()) ) {
                workingEntry.setTransactionLedgerEntryDescription("AUTO FR " + originEntry.getChartOfAccountsCode() 
                        + originEntry.getAccountNumber() + originEntry.getTransactionLedgerEntryDescription());
            }
        }

        setAccount(workingEntry,originEntry.getChartOfAccountsCode(),originEntry.getAccountNumber());
        return null;
    }

    private String continuationAccountLogic(OriginEntry originEntry, OriginEntry workingEntry, Calendar today) {

        List checkedAccountNumbers = new ArrayList();

        Account account = null;

        String chartCode = originEntry.getAccount().getContinuationFinChrtOfAcctCd();
        String accountNumber = originEntry.getAccount().getContinuationAccountNumber();

        for (int i = 0; i < 10; ++i) {
            if ( checkedAccountNumbers.contains(chartCode + accountNumber) ) {
                // Something is really wrong with the data because this account has already been evaluated.
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CIRCULAR_DEPENDENCY_IN_CONTINUATION_ACCOUNT_LOGIC);
            }

            if ( (chartCode == null) || (accountNumber ==  null) ) {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CONTINUATION_ACCOUNT_NOT_FOUND);                
            }

            // Lookup the account
            account = accountService.getByPrimaryId(chartCode, accountNumber);
            if (null == account) { 
                // account not found
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CONTINUATION_ACCOUNT_NOT_FOUND);
            } else {
                // the account exists
                if (account.getAccountExpirationDate() == null) {
                    // No expiration date
                    workingEntry.setAccount(account);
                    workingEntry.setAccountNumber(accountNumber);
                    workingEntry.setChartOfAccountsCode(chartCode);
                    return null;
                } else {
                    // the account does have an expiration date.
                    // This is the only case in which we might go 
                    // on for another iteration of the loop.
                    checkedAccountNumbers.add(chartCode + accountNumber);

                    // Add 3 months to the expiration date if it's a contract and grant account.
                    String msg = adjustAccountIfContractsAndGrants(account);
                    if ( msg != null ) {
                        return msg;
                    }

                    // Check that the account has not expired.

                    // If the account has expired go around for another iteration.
                    if(isExpired(account, today)) {
                        chartCode = account.getContinuationFinChrtOfAcctCd();
                        accountNumber = account.getContinuationAccountNumber();
                    } else {
                        workingEntry.setAccount(account);
                        workingEntry.setAccountNumber(accountNumber);
                        workingEntry.setChartOfAccountsCode(chartCode);                        
                        return null;
                    }
                }
            }
        }

        // We failed to find a valid continuation account.
        return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CONTINUATION_ACCOUNT_LIMIT_REACHED);
    }

    private String adjustAccountIfContractsAndGrants(Account account) {
        if (! (account == null) && "CG".equals(account.getSubFundGroupCode())  && ! account.isAccountClosedIndicator()) {
            if (account.getSubFundGroup() == null) {
                // make sure the sub fund group really exists
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SUB_FUND_GROUP_NOT_FOUND) + "(" +
                    account.getSubFundGroupCode() + ")";
            } else  {
                Calendar tempCal = Calendar.getInstance();
                tempCal.setTimeInMillis(account.getAccountExpirationDate().getTime());
                tempCal.add(Calendar.MONTH, 3); //TODO: make this configurable
                account.setAccountExpirationDate(new Timestamp(tempCal.getTimeInMillis()));
                return null;
            }
        } else {
            return null;
        }
    }

	public String validateReversalDate(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateReversalDate() started");

//          3234  021620     IF FDOC-REVERSAL-DT OF GLEN-RECORD = SPACES
//          3235  021630        MOVE SPACES
//          3236  021640          TO FDOC-REVERSAL-DT OF ALT-GLEN-RECORD
//          3237  021650     ELSE
//          3238  021660        MOVE DCLSH-UNIV-DATE-T TO WS-UNIV-DATE-T
//          3239  021670        MOVE FDOC-REVERSAL-DT OF GLEN-RECORD TO SCREEN-DATE
//          3240  021680        MOVE 'S'                             TO DATE-TYPE
//          3241  021690        CALL 'gledates' USING DATE-ROUTINE-PARMS
//          3242  021700        IF DATE-IS-OK
//          3243  021710           MOVE DATE-CCYY-MM-DD
//          3244  021720             TO FDOC-REVERSAL-DT OF ALT-GLEN-RECORD
//          3245  021730                SHUDAT-UNIV-DT
//          3246  021740       EXEC SQL
//          3247  021750          SELECT   UNIV_FISCAL_YR,
//          3248  021760                   UNIV_FISCAL_PRD_CD
//          3249  021770          INTO     :SHUDAT-UNIV-FISCAL-YR,
//          3250  021780                   :SHUDAT-UNIV-FISCAL-PRD-CD
//          3251  021790          FROM     SH_UNIV_DATE_T
//          3252  021800          WHERE    UNIV_DT       = RTRIM(:SHUDAT-UNIV-DT)
//          3253  021810       END-EXEC
//          3254  021830       EVALUATE SQLCODE
//          3255  021840         WHEN 0
//          3256  021850           MOVE FDOC-REVERSAL-DT OF GLEN-RECORD
//          3257  021860             TO FDOC-REVERSAL-DT OF ALT-GLEN-RECORD
//          3258  021870       WHEN +100
//          3259  021880       WHEN +1403
//          3260  021890           MOVE GLEN-RECORD (1:51) TO RP-TABLE-KEY
//          3261  021900           MOVE FDOC-REVERSAL-DT OF GLEN-RECORD TO RP-DATA-ERROR
//          3262  021910                FDOC-REVERSAL-DT OF ALT-GLEN-RECORD
//          3263  021920           MOVE 'REVERSAL DATE NOT IN UDAT' TO RP-MSG-ERROR
//          3264  021930           PERFORM WRITE-ERROR-LINE THRU WRITE-ERROR-LINE-EXIT
//          3265  021940           MOVE SPACES TO DCLSH-UNIV-DATE-T
//          3266  021950       WHEN OTHER
//          3267  021960           DISPLAY ' ERROR ACCESSING UDAT TABLE '
//          3268  021970               'SQL CODE IS ' SQLCODE
//          3269  021980           DISPLAY ' DATA IS ' SHUDAT-UNIV-DT ' AT REV DATE'
//          3270  021990           MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//          3271  022000           MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//          3272  022010           GO TO 2000-ENTRY-EXIT
//          3273  022020       END-EVALUATE
//          3274  022030        MOVE WS-UNIV-DATE-T TO DCLSH-UNIV-DATE-T
//          3275  022040           END-IF
//          3276  022050     END-IF.

        if ( originEntry.getFinancialDocumentReversalDate() != null ) {
            UniversityDate universityDate = universityDateDao.getByPrimaryKey(originEntry.getFinancialDocumentReversalDate());
            if ( universityDate == null ) {
                Date reversalDate = originEntry.getFinancialDocumentReversalDate();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_REVERSAL_DATE_NOT_FOUND) + "(" +
                    format.format(reversalDate) + ")";
            } else {
                workingEntry.setFinancialDocumentReversalDate(originEntry.getFinancialDocumentReversalDate());
            }
        }
        return null;
	}

	public String validateSubAccount(OriginEntry originEntry, OriginEntry workingEntry) {
	    LOG.debug("validateSubAccount() started");

        // If the sub account number is empty, set it to dashes. 
        // Otherwise set the workingEntry sub account number to the
        // sub account number of the input origin entry.
        if ( StringUtils.hasText(originEntry.getSubAccountNumber()) ) {                              
            // sub account IS specified
            if (! Constants.DASHES_SUB_ACCOUNT_NUMBER.equals(originEntry.getSubAccountNumber())) {
                if ( originEntry.getSubAccount() == null ) {
                    // sub account is not valid
                    return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SUB_ACCOUNT_NOT_FOUND) + "(" +
                        workingEntry.getChartOfAccountsCode() + "-" + workingEntry.getAccountNumber() + "-" + workingEntry.getSubAccountNumber() + ")";
            	} else {                                                                           
                    // sub account IS valid
            		if(originEntry.getSubAccount().isSubAccountActiveIndicator()) {                
                        // sub account IS active
        				workingEntry.setSubAccountNumber(originEntry.getSubAccountNumber());
            			workingEntry.setSubAccount(originEntry.getSubAccount());
            		} else {                                                                       
                        // sub account IS NOT active
            			if("ACLO".equals(originEntry.getFinancialDocumentTypeCode())) {            
                            // document IS annual closing
            				workingEntry.setSubAccountNumber(originEntry.getSubAccountNumber());
            				workingEntry.setSubAccount(originEntry.getSubAccount());
            			} else {                                                                   
                            // document is NOT annual closing
                            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SUB_ACCOUNT_NOT_ACTIVE) + "(" +
                                workingEntry.getChartOfAccountsCode() + "-" + workingEntry.getAccountNumber() + "-" + workingEntry.getSubAccountNumber() + ")";
            			}
            		}
            	}
            } else {                                                                               
                // the sub account is dashes
                workingEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
                workingEntry.setSubAccount(null);
            }
        } else {                                                                                   
            // No sub account is specified.
            workingEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
            workingEntry.setSubAccount(null);
        }
        return null;
	}

	public String validateProjectCode(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateProjectCode() started");

        if ( StringUtils.hasText(originEntry.getProjectCode()) 
        		&& ! Constants.DASHES_PROJECT_CODE.equals(originEntry.getProjectCode()) ) {
            if ( originEntry.getProject() == null ) {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_PROJECT_CODE_NOT_FOUND) + " (" +
                    originEntry.getProjectCode() + ")";
            } else {
                if ( originEntry.getProject().isActive() ) {
                    workingEntry.setProjectCode(originEntry.getProjectCode());
                    workingEntry.setProject(originEntry.getProject());
                } else {
                    return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_PROJECT_CODE_MUST_BE_ACTIVE) + " (" +
                    originEntry.getProjectCode() + ")";
                }
            }
        }

        workingEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        return null;
	}

    public String validateFiscalYear(OriginEntry originEntry, OriginEntry workingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateFiscalYear() started");

        if ( (originEntry.getUniversityFiscalYear() == null) || (originEntry.getUniversityFiscalYear().intValue() == 0) ) {
            originEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());
            workingEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());

            // Retrieve these objects because the fiscal year is the primary key for them
            persistenceService.retrieveReferenceObject(originEntry, "financialSubObject");
            persistenceService.retrieveReferenceObject(originEntry, "financialObject");
            persistenceService.retrieveReferenceObject(originEntry, "accountingPeriod");
            persistenceService.retrieveReferenceObject(originEntry, "option");
        } else {
            workingEntry.setUniversityFiscalYear(originEntry.getUniversityFiscalYear());
            workingEntry.setOption(originEntry.getOption());
        }

        if ( originEntry.getOption() == null ) {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND) + " (" +
                originEntry.getUniversityFiscalYear() + ")";
        }
        return null;
    }

	public String validateTransactionDate(OriginEntry originEntry, OriginEntry workingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateTransactionDate() started");

        if ( originEntry.getTransactionDate() == null ) {
            Date transactionDate = new Date(universityRunDate.getUniversityDate().getTime());

            // Set the transaction date to the run date.
            originEntry.setTransactionDate(transactionDate);
            workingEntry.setTransactionDate(transactionDate);
        } else {
            workingEntry.setTransactionDate(originEntry.getTransactionDate());
        }

        // Next, we have to validate the transaction date against the university date table.
        if (universityDateDao.getByPrimaryKey(originEntry.getTransactionDate()) == null ) {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_TRANSACTION_DATE_INVALID) + " (" +
            originEntry.getTransactionDate() + ")";
        }
        return null;
	}

    /**
     * 
     * @param originEntry
     * @param workingEntryInfo
     */
	public String validateDocumentType(OriginEntry originEntry, OriginEntry workingEntry) {
	    LOG.debug("validateDocumentType() started");

        if ( originEntry.getDocumentType() == null ) {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DOCUMENT_TYPE_NOT_FOUND) + " (" +
                originEntry.getFinancialDocumentTypeCode() + ")";
        }

        workingEntry.setFinancialDocumentTypeCode(originEntry.getFinancialDocumentTypeCode());
        workingEntry.setDocumentType(originEntry.getDocumentType());
        return null;
	}

	public String validateOrigination(OriginEntry originEntry, OriginEntry workingEntry) {
	    LOG.debug("validateOrigination() started");

		if ( StringUtils.hasText(originEntry.getFinancialSystemOriginationCode()) ) {
			if ( originEntry.getOrigination() == null ) {
			    return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ORIGIN_CODE_NOT_FOUND) + " (" +
						originEntry.getFinancialSystemOriginationCode() + ")";
			} else {
                workingEntry.setFinancialSystemOriginationCode(originEntry.getFinancialSystemOriginationCode());
				workingEntry.setOrigination(originEntry.getOrigination());
			}
		} else {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ORIGIN_CODE_NOT_FOUND) + " (" +
                originEntry.getFinancialSystemOriginationCode() + ")";
		}
		return null;
    }

	public String validateDocumentNumber(OriginEntry originEntry, OriginEntry workingEntry) {
	    LOG.debug("validateDocumentNumber() started");

        if (! StringUtils.hasText(originEntry.getFinancialDocumentNumber()) ) {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DOCUMENT_NUMBER_REQUIRED);
        } else {
            workingEntry.setFinancialDocumentNumber(originEntry.getFinancialDocumentNumber());
            return null;
        }
	}

	/**
	 * 
	 * @param originEntry
	 * @param workingEntryInfo
	 */
	public String validateChart(OriginEntry originEntry, OriginEntry workingEntry) {
	    LOG.debug("validateChart() started");

        if ( originEntry.getChartOfAccountsCode() == null ) {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CHART_NOT_FOUND);
        }

        if ( originEntry.getChart() == null ) {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CHART_NOT_FOUND) + " (" +
            originEntry.getChartOfAccountsCode() + ")";
        }

        if ( ! originEntry.getChart().isFinChartOfAccountActiveIndicator() ) {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CHART_NOT_ACTIVE) + " (" +
                originEntry.getChartOfAccountsCode() + ")";
        }

        workingEntry.setChartOfAccountsCode(originEntry.getChartOfAccountsCode());
        workingEntry.setChart(originEntry.getChart());
        return null;
	}

    /**
     * 
     * @param originEntry
     * @param workingEntryInfo
     */
	public String validateObjectCode(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateObjectCode() started");

        if ( ! StringUtils.hasText(originEntry.getFinancialObjectCode()) ) {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_CODE_EMPTY);
        }

        if ( originEntry.getFinancialObject() == null ) {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_CODE_NOT_FOUND) + " (" +
                originEntry.getUniversityFiscalYear() + "-" + originEntry.getChartOfAccountsCode() + "-" + 
                originEntry.getFinancialObjectCode() + ")";
        } else {
            // object code IS valid
            if ( ! originEntry.getFinancialObject().isFinancialObjectActiveCode() ) {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_CODE_NOT_ACTIVE) + " (" +
                    originEntry.getUniversityFiscalYear() + "-" + originEntry.getChartOfAccountsCode() + "-" + 
                    originEntry.getFinancialObjectCode() + ")";
            }
        }

	    // object code IS active
	    workingEntry.setFinancialObject(originEntry.getFinancialObject());
	    workingEntry.setFinancialObjectCode(originEntry.getFinancialObjectCode());
        return null;
	}

	public String validateObjectType(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateObjectType() started");

		if ( ! StringUtils.hasText(originEntry.getFinancialObjectTypeCode()) ) {
            // If not specified, use the object type from the object code
		    workingEntry.setFinancialObjectTypeCode(originEntry.getFinancialObject().getFinancialObjectTypeCode());
            workingEntry.setObjectType(originEntry.getFinancialObject().getFinancialObjectType());
        } else {
            workingEntry.setFinancialObjectTypeCode(originEntry.getFinancialObjectTypeCode());
            workingEntry.setObjectType(originEntry.getObjectType());
        }

        if ( workingEntry.getObjectType() == null ) {
            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_TYPE_NOT_FOUND) + " (" +
                originEntry.getFinancialObjectTypeCode() + ")";
        }
        return null;
	}

	public String validateSubObjectCode(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateFinancialSubObjectCode() started");

        if (! StringUtils.hasText(originEntry.getFinancialSubObjectCode()) ) {
            workingEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
            workingEntry.setFinancialSubObject(null);
            return null;
        }

        if ( ! Constants.DASHES_SUB_OBJECT_CODE.equals(originEntry.getFinancialSubObjectCode()) ) {
            if ( originEntry.getFinancialSubObject() == null ) {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SUB_OBJECT_CODE_NOT_FOUND) + " (" +
                    originEntry.getUniversityFiscalYear() + "-" + originEntry.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber() + "-" +
                    originEntry.getFinancialObjectCode() + "-" + originEntry.getFinancialSubObjectCode() + ")";
            }

            if ( ! originEntry.getFinancialSubObject().isFinancialSubObjectActiveIndicator() ) {
                // if NOT active, set it to dashes
                workingEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
                workingEntry.setFinancialSubObject(null);
                return null;
            }
        }
        workingEntry.setFinancialSubObjectCode(originEntry.getFinancialSubObjectCode());
        workingEntry.setFinancialSubObject(originEntry.getFinancialSubObject());
        return null;
	}

	public String validateBalanceType(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateBalanceType() started");

        if ( StringUtils.hasText(originEntry.getFinancialBalanceTypeCode()) ) {
            // balance type IS NOT empty
            if ( originEntry.getBalanceType() == null) {
                // balance type IS NOT valid
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_BALANCE_TYPE_NOT_FOUND) + " (" + originEntry.getFinancialBalanceTypeCode() + ")";
        	} else {
                // balance type IS valid
        		if ( originEntry.getBalanceType().isFinancialOffsetGenerationIndicator() ) {
                    // entry IS an offset 
        			if ( originEntry.getTransactionLedgerEntryAmount().isNegative() ) {       
                        // it's an INVALID non-budget transaction
        			    return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_TRANS_CANNOT_BE_NEGATIVE_IF_OFFSET);
        			} else {                                                                
                        // it's a VALID non-budget transaction
                        if ( ! originEntry.isCredit() && ! originEntry.isDebit() ) { // entries requiring an offset must be either a debit or a credit
                            return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DC_INDICATOR_MUST_BE_D_OR_C) + " (" +
                                originEntry.getTransactionDebitCreditCode() + ")";
                        } else {
                            workingEntry.setFinancialBalanceTypeCode(originEntry.getFinancialBalanceTypeCode());
                            workingEntry.setBalanceType(originEntry.getBalanceType());
                        }
        			}
        		} else {                                                                    
                    // entry IS NOT an offset, means it's a budget transaction
        			if ( StringUtils.hasText(originEntry.getTransactionDebitCreditCode()) ) {
        			    return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DC_INDICATOR_MUST_BE_EMPTY) + " (" +
                    			originEntry.getTransactionDebitCreditCode() + ")";
        			} else {
        				if ( originEntry.isCredit() || originEntry.isDebit()) {
        				    // budget transactions must be neither debit nor credit
        	            	return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DC_INDICATOR_MUST_BE_NEITHER_D_NOR_C) + " (" +
       	                        originEntry.getTransactionDebitCreditCode() + ")";
        				} else {                                             
                            // it's a valid budget transaction
                            workingEntry.setFinancialBalanceTypeCode(originEntry.getFinancialBalanceTypeCode());
                            workingEntry.setBalanceType(originEntry.getBalanceType());
        				}
        			}
        		}
        	}
        } else {
            // balance type IS empty
            workingEntry.setFinancialBalanceTypeCode(workingEntry.getOption().getActualFinancialBalanceTypeCd());
            workingEntry.setBalanceType(workingEntry.getOption().getBalanceTyp());
        }
        return null;
	}

	public String validateUniversityFiscalPeriodCode(OriginEntry originEntry, OriginEntry workingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateUniversityFiscalPeriodCode() started");

        if ( StringUtils.hasText(originEntry.getUniversityFiscalPeriodCode()) ) {
            if ( originEntry.getAccountingPeriod() == null ) {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_FOUND) + " (" +
                        originEntry.getUniversityFiscalPeriodCode() + ")";
            }
            if ( Constants.ACCOUNTING_PERIOD_STATUS_CLOSED.equals(originEntry.getAccountingPeriod().getUniversityFiscalPeriodStatusCode()) ) {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_FISCAL_PERIOD_CLOSED) + " (" + 
                    originEntry.getUniversityFiscalPeriodCode() + ")";
            }
            
            workingEntry.setUniversityFiscalPeriodCode(originEntry.getUniversityFiscalPeriodCode());
        } else {
            workingEntry.setUniversityFiscalPeriodCode(universityRunDate.getUniversityFiscalAccountingPeriod());
        }
        return null;
	}

    /**
     * 
     * @param originEntry
     * @param workingEntryInfo
     */
    public String validateReferenceDocument(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateReferenceDocument() started");

        if ( ! StringUtils.hasText(originEntry.getReferenceFinancialDocumentNumber()) ) {
            workingEntry.setReferenceDocumentType(null);
            workingEntry.setReferenceFinancialSystemOriginationCode(null);
            workingEntry.setReferenceFinancialDocumentTypeCode(null);
            workingEntry.setReferenceFinancialDocumentNumber(null);

            if ( Constants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(originEntry.getTransactionEncumbranceUpdateCode()) ) {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL_IF_UPDATE_CODE_IS_R);
            }
            return null;
        } else {
            if ( originEntry.getReferenceDocumentType() == null ) {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_REFERENCE_DOCUMENT_TYPE_NOT_FOUND) + " (" +
                originEntry.getReferenceFinancialDocumentTypeCode() + ")";
            }

            OriginationCode originationCode = originationCodeService.getByPrimaryKey(originEntry.getReferenceFinancialSystemOriginationCode());
            if ( originationCode == null ) {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_REFERENCE_ORIGINATION_CODE_NOT_FOUND) + " (" +
                        originEntry.getReferenceFinancialSystemOriginationCode() + ")";
            }

            workingEntry.setReferenceFinancialSystemOriginationCode(originEntry.getReferenceFinancialSystemOriginationCode());
            workingEntry.setReferenceFinancialDocumentTypeCode(originEntry.getReferenceFinancialDocumentTypeCode());
            workingEntry.setReferenceDocumentType(originEntry.getReferenceDocumentType());
            workingEntry.setReferenceFinancialDocumentNumber(originEntry.getReferenceFinancialDocumentNumber());
        }
        return null;
    }

    /**
     * 
     * @param originEntry
     * @param workingEntryInfo
     */
	public String validateEncumbranceUpdateCode(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateEncumbranceUpdateCode() started");

        if ( (originEntry.getBalanceType() == null) || (originEntry.getObjectType() == null) ) {
            // We can't validate the encumbrance update code without these
            return null;
        }

        if ( originEntry.getBalanceType().isFinBalanceTypeEncumIndicator() && ! originEntry.getObjectType().isFundBalanceIndicator() ) {
            if ( Constants.ENCUMB_UPDT_DOCUMENT_CD.equals(originEntry.getTransactionEncumbranceUpdateCode()) || 
                    Constants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD.equals(originEntry.getTransactionEncumbranceUpdateCode()) ||
                    Constants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(originEntry.getTransactionEncumbranceUpdateCode()) ) {
                workingEntry.setTransactionEncumbranceUpdateCode(originEntry.getTransactionEncumbranceUpdateCode());
                return null;
            } else {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ENC_UPDATE_CODE_NOT_DRN) + " (" + 
                    originEntry.getTransactionEncumbranceUpdateCode() + ")";
            }
        }
        return null;
	}

    /**
     * 
     * @param originEntry
     * @param workingEntryInfo
     */
    public String validateTransactionAmount(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateTransactionAmount() started");

        KualiDecimal amount = originEntry.getTransactionLedgerEntryAmount();
        if ( originEntry.getBalanceType() == null ) {
            // We can't validate the amount without a balance type code
            return null;
        }

        if ( originEntry.getBalanceType().isFinancialOffsetGenerationIndicator() ) {
            if (amount.isPositive() || amount.isZero()) {
                workingEntry.setTransactionLedgerEntryAmount(originEntry.getTransactionLedgerEntryAmount());
            } else {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_NEGATIVE_AMOUNT) + " (" + amount.toString() + ")";
            }
            if (StringHelper.isEmpty(originEntry.getTransactionDebitCreditCode())) {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DEBIT_CREDIT_INDICATOR_NEITHER_D_NOR_C) + " (" +
                    originEntry.getTransactionDebitCreditCode() + ")";
            }
            if (ObjectHelper.isOneOf(originEntry.getTransactionDebitCreditCode(), debitOrCredit)) {
                workingEntry.setTransactionDebitCreditCode(originEntry.getTransactionDebitCreditCode());
            } else {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DEBIT_CREDIT_INDICATOR_NEITHER_D_NOR_C) + " (" +
                    originEntry.getTransactionDebitCreditCode() + ")";
            }
        } else {
            if ( (originEntry.getTransactionDebitCreditCode() == null) || (" ".equals(originEntry.getTransactionDebitCreditCode())) ) {
                workingEntry.setTransactionDebitCreditCode(Constants.GL_BUDGET_CODE);
            } else {
                return kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DEBIT_CREDIT_INDICATOR_MUST_BE_SPACE) + " (" +
                    originEntry.getTransactionDebitCreditCode() + ")";
            }
        }
        return null;
    }

    private boolean isExpired(Account account, Calendar runCalendar) {

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTimeInMillis(account.getAccountExpirationDate().getTime());

        int expirationYear = expirationDate.get(Calendar.YEAR);
        int runYear = runCalendar.get(Calendar.YEAR);
        int expirationDoy = expirationDate.get(Calendar.DAY_OF_YEAR);
        int runDoy = runCalendar.get(Calendar.DAY_OF_YEAR);

        return (expirationYear < runYear) || (expirationYear == runYear && expirationDoy < runDoy);
    }

    public void setUniversityDateDao(UniversityDateDao udd) {
        universityDateDao = udd;
    }

	public void setKualiConfigurationService(KualiConfigurationService service) {
    	kualiConfigurationService = service;
    }

    public void setPersistenceService(PersistenceService ps) {
        persistenceService = ps;
    }

    public void setAccountService(AccountService as) {
        accountService = as;
    }

    public void setOriginationCodeService(OriginationCodeService ocs) {
        originationCodeService = ocs;
    }
}
