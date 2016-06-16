package edu.arizona.kfs.fp.document.validation.impl;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.kew.api.WorkflowDocument;

public class DisbursementVoucherAccountingLineAccessibleValidation extends org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherAccountingLineAccessibleValidation {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAccountingLineAccessibleValidation.class);

    protected AccountService accountService;
    
    /**
     * Provided the current accounting line is editable by the current user, we do additional 
     * validation to make sure the current user can use the account.
     *  
     * <strong>This method expects a document as the first parameter and an accounting line as the second</strong>
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("start UA account validate");
        
        boolean isValid = super.validate(event);
        Person financialSystemUser = GlobalVariables.getUserSession().getPerson();
        AccountingDocument accountingDocument = this.getAccountingDocumentForValidation();
        AccountingLine accountingLineForValidation = this.getAccountingLineForValidation();
       
        if (isValid) {
            WorkflowDocument workflowDocument = accountingDocument.getDocumentHeader().getWorkflowDocument();
            Set<String> currentRouteLevels = workflowDocument.getNodeNames();
            
            // if at "Account" route node, don't accept current account if it doesn't belong to current user
            if (workflowDocument.isEnroute() && currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.ACCOUNT)) {
                Account currentAccount = accountingLineForValidation.getAccount();
                String accountingLineTotal = accountingLineForValidation.getAmount().toString();
                AccountDelegate currentAccountDelegate = new AccountDelegate();
                currentAccountDelegate.setChartOfAccountsCode(accountingLineForValidation.getChartOfAccountsCode());
                currentAccountDelegate.setAccountNumber(accountingLineForValidation.getAccountNumber());
                currentAccountDelegate.setFinancialDocumentTypeCode(accountingDocument.getFinancialDocumentTypeCode());
                
                // check to see if current user is the fiscal officer
                isValid = checkFiscalOfficer(currentAccount, financialSystemUser);
                
                // check to see if current user is a primary delegate
                if (!isValid) {
                    AccountDelegate primaryDelegate = getAccountService().getPrimaryDelegationByExample(currentAccountDelegate, accountingLineTotal);
                    isValid = checkPrimaryDelegate(primaryDelegate, financialSystemUser);
                }
                
                // check to see if current user is a secondary delegate
                if (!isValid) {
                    List<AccountDelegate> secondaryDelegates = getAccountService().getSecondaryDelegationsByExample(currentAccountDelegate, accountingLineTotal);
                    isValid = checkSecondaryDelegates(secondaryDelegates, financialSystemUser);
                }                                
            }
            
            // report errors if the current user can have no access to the account
            if (!isValid) {
                String accountNumber = accountingLineForValidation.getAccountNumber();
                String principalName = GlobalVariables.getUserSession().getPerson().getPrincipalName();
                String errorKey = this.convertEventToMessage(event);

                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, errorKey, accountNumber, principalName);
            }
        }
        
        LOG.debug("finished UA account validate");
        return isValid;
    }
    
    /**
     * This method checks to see if the currentUser is the fiscal officer of the currentAccount.
     * 
     * @param currentAccount
     * @param currentUser
     * @return true if the currentUser is the fiscal officer; false otherwise
     */
    protected boolean checkFiscalOfficer(Account currentAccount, Person currentUser) {
        boolean isFiscalOfficer = false;
                
        if (currentAccount != null) {
            String accountFOPrincipalName = currentAccount.getAccountFiscalOfficerUser().getPrincipalName();
            if (currentUser.getPrincipalName().equals(accountFOPrincipalName)) {
                isFiscalOfficer = true;
            }
        }
        return isFiscalOfficer;
    }
    
    /**
     * This method checks to see if the primaryDelegate of the current account is the currentUser.
     * @param primaryDelegate
     * @param currentUser
     * @return true if the currentUser is the primaryDelegate; false otherwise
     */
    protected boolean checkPrimaryDelegate(AccountDelegate primaryDelegate, Person currentUser) {
        boolean isPrimaryDelegate = false;
        
        if (primaryDelegate != null) {
            String primaryDelegatePrincipalName = primaryDelegate.getAccountDelegate().getPrincipalName();
            if (currentUser.getPrincipalName().equals(primaryDelegatePrincipalName)) {
                isPrimaryDelegate = true;
            }
        }
        
        return isPrimaryDelegate;
    }
    
    /**
     * This method checks to see if a secondary delegate of the current account is the currentUser.
     * @param secondaryDelegates
     * @param currentUser
     * @return true if the currentUser is a secondary delegate; false otherwise
     */
    protected boolean checkSecondaryDelegates(List<AccountDelegate> secondaryDelegates, Person currentUser) {
        String currentPrincipalName = currentUser.getPrincipalName();
        AccountDelegate currentDelegate;
        String secondaryDelegatePrincipalName;
        
        if (!secondaryDelegates.isEmpty()) {
            ListIterator iterator = secondaryDelegates.listIterator();
            while (iterator.hasNext()) {
                currentDelegate = (AccountDelegate) iterator.next();
                secondaryDelegatePrincipalName = currentDelegate.getAccountDelegate().getPrincipalName();
                if (currentPrincipalName.equals(secondaryDelegatePrincipalName)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * Gets the accountService attribute. 
     * @return Returns the accountService.
     */
    public AccountService getAccountService() {
    	if(accountService == null) {
    		accountService = SpringContext.getBean(AccountService.class);
    	}
        return accountService;
    }

}

