/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAssetAccountsGroupDetails;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * validate the capital asset information associated with the accounting document for validation
 */
public class CapitalAssetInformationValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CapitalAssetInformationValidation.class);

    private CapitalAssetBuilderModuleService capitalAssetBuilderModuleService = SpringContext.getBean(CapitalAssetBuilderModuleService.class);
    private AccountingDocument accountingDocumentForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true ;
        
        //check if accounting lines have been distributed to capital assets..
        valid &= accountingLinesDisributedToCapitalAssets(accountingDocumentForValidation);

        if (valid) {
            //check if capital assets accounting lines exist in source/target accounting lines...
            valid &= capitalAssetsAccountLinesMatchToAccountingLines(accountingDocumentForValidation);
        }

        if (valid) {
            //check if capital assets accounting lines totals match the capital asset amount...
            valid &= amountsForCapitalAssetsAndAccountLinesMatch(accountingDocumentForValidation);
        }

        if (valid) {
            //check if distributed accounting lines total matches the capital asset amount...
            valid &= amountsForCapitalAssetsAndDistributedAccountLinesMatch(accountingDocumentForValidation);
        }
        
        if (valid) {
            //make sure capital asset information is valid...
            valid &= hasValidCapitalAssetInformation(accountingDocumentForValidation);
        }
        
        return valid;
    }

    /**
     * validates that all the accounting lines in source/target section have been
     * distributed in the capital assets. Any given accounting line must exist in
     * at least one capital asset.  Return true if accounting lines exist in capital asset
     * else return false.
     * 
     * @param accountingDocument
     * @return true if lines have been distributed else false.
     */
    protected boolean accountingLinesDisributedToCapitalAssets(AccountingDocument accountingDocument) {
        LOG.debug("accountingLinesDisributedToCapitalAssets(accountingDocument) - start");

        boolean distributed = true;
        
        if (accountingDocument instanceof CapitalAssetEditable == false) {
            return true;
        }

        CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) accountingDocument;
        List<CapitalAssetInformation> capitalAssets = capitalAssetEditable.getCapitalAssetInformation();
        
        List<SourceAccountingLine> sourceAccountLines = accountingDocument.getSourceAccountingLines();
        
        int accountIndex = 0;
        
        for (SourceAccountingLine sourceAccount : sourceAccountLines)  {
            if (capitalAssetBuilderModuleService.hasCapitalAssetObjectSubType(sourceAccount)) {
                //capital object code so we need to check capital asset info...
                //check if this sourceAccount does exist in any one capital assets....
                if (!checkSourceDistributedAccountingLineExists(sourceAccount, capitalAssets)) {
                    //account does not exist so put out an error message and get out.
                    GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KFSPropertyConstants.DOCUMENT + "." + KFSConstants.SOURCE_ACCOUNTING_LINE_ERRORS + "[" + accountIndex + "]" + "." + KFSPropertyConstants.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_DOCUMENT_SOURCE_ACCOUNTING_LINE_NOT_DISTRIBUTED, sourceAccount.getAccountNumber());
                    distributed = false;
                    accountIndex++;
                    
                    break;
                }
            }
        }

        List<TargetAccountingLine> targetAccountLines = accountingDocument.getTargetAccountingLines();
        
        accountIndex = 0;
        for (TargetAccountingLine targetAccount : targetAccountLines)  {
            //check if this targetAccount does exist in any one capital assets....
            if (capitalAssetBuilderModuleService.hasCapitalAssetObjectSubType(targetAccount)) {
                //capital object code so we need to check capital asset info...
                //check if this sourceAccount does exist in any one capital assets....
                if (!checkTargetDistributedAccountingLineExists(targetAccount, capitalAssets)) {
                    //account does not exist so put out an error message and get out.
                    GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KFSPropertyConstants.DOCUMENT + "." + KFSConstants.TARGET_ACCOUNTING_LINE_ERRORS + "[" + accountIndex + "]" + "." + KFSPropertyConstants.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_DOCUMENT_TARGET_ACCOUNTING_LINE_NOT_DISTRIBUTED, targetAccount.getAccountNumber());
                    distributed = false;
                    accountIndex++;
                    
                    break;
                }
            }
        }
        
        return distributed;
    }
    
    /**
     * checks source accounting lines again the distributed accounting line and if found 
     * return true else false so that this distributed accounting line may be removed.
     * 
     * @param accountLine
     * @param capitalAssets
     * @return true if accounting line exists else return false
     */
    protected boolean checkSourceDistributedAccountingLineExists(SourceAccountingLine accountLine, List<CapitalAssetInformation> capitalAssets) {
        boolean exists = false;
        
        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            for (CapitalAssetAccountsGroupDetails groupAccountLine : capitalAsset.getCapitalAssetAccountsGroupDetails()) {
                if (groupAccountLine.getSequenceNumber().compareTo(accountLine.getSequenceNumber()) == 0 && 
                        groupAccountLine.getFinancialDocumentLineTypeCode().equals(accountLine.getFinancialDocumentLineTypeCode()) && 
                        groupAccountLine.getChartOfAccountsCode().equals(accountLine.getChartOfAccountsCode()) && 
                        groupAccountLine.getAccountNumber().equals(accountLine.getAccountNumber()) && 
                        groupAccountLine.getFinancialObjectCode().equals(accountLine.getFinancialObjectCode())) {
                    return true;
                }
            }
        }
        
        return exists;
    }
    
    /**
     * checks target accounting lines again the distributed accounting line and if found 
     * return true else false so that this distributed accounting line may be removed.
     * 
     * @param accountLine
     * @param capitalAssets
     * @return true if accounting line exists else return false
     */
    protected boolean checkTargetDistributedAccountingLineExists(TargetAccountingLine accountLine, List<CapitalAssetInformation> capitalAssets) {
        boolean exists = false;
        
        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            for (CapitalAssetAccountsGroupDetails groupAccountLine : capitalAsset.getCapitalAssetAccountsGroupDetails()) {
                if (groupAccountLine.getSequenceNumber().compareTo(accountLine.getSequenceNumber()) == 0 && 
                        groupAccountLine.getFinancialDocumentLineTypeCode().equals(accountLine.getFinancialDocumentLineTypeCode()) && 
                        groupAccountLine.getChartOfAccountsCode().equals(accountLine.getChartOfAccountsCode()) && 
                        groupAccountLine.getAccountNumber().equals(accountLine.getAccountNumber()) && 
                        groupAccountLine.getFinancialObjectCode().equals(accountLine.getFinancialObjectCode())) {
                    return true;
                }
            }
        }
        
        return exists;
    }
    
    // determine whether the given document has valid capital asset information if any
    protected boolean hasValidCapitalAssetInformation(AccountingDocument accountingDocument) {
        LOG.debug("hasValidCapitalAssetInformation(Document) - start");
        boolean isValid = true;
        
        if (accountingDocument instanceof CapitalAssetEditable == false) {
            return true;
        }

        CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) accountingDocument;
        List<CapitalAssetInformation> capitalAssets = capitalAssetEditable.getCapitalAssetInformation();

        int index = 0;
        for (CapitalAssetInformation capitalAssetInformation : capitalAssets) {
            if (ObjectUtils.isNotNull(capitalAssetInformation)) {
                MessageMap errors = GlobalVariables.getMessageMap();
                errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);
                String parentName = (capitalAssetInformation.getCapitalAssetActionIndicator().equalsIgnoreCase(KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR) ? KFSPropertyConstants.CAPITAL_ASSET_INFORMATION :  KFSPropertyConstants.CAPITAL_ASSET_MODIFY_INFORMATION);
                errors.addToErrorPath(parentName);
                String errorPathPrefix = KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "[" + index + "].";
                errors.addToErrorPath(errorPathPrefix);
                
                isValid &= capitalAssetBuilderModuleService.validateFinancialProcessingData(accountingDocument, capitalAssetInformation, index);
                
                errors.removeFromErrorPath(errorPathPrefix);
                errors.removeFromErrorPath(parentName);
                errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);
                index++;
            }
        }
        
        isValid &= capitalAssetBuilderModuleService.validateAssetTags(accountingDocument);
        
        return isValid;
    }

    /**
     * validates that all the accounting lines in capital assets do exist
     * source/target accounting line sections. Any given accounting line in the capital asset
     * must exist in source/target sections.
     * Return true if accounting lines in capital asset exist in source/target accounting lines else
     * return false.
     * 
     * @param accountingDocument
     * @return true if lines in capital assets exist in source/target accounts else return false.
     */
    protected boolean capitalAssetsAccountLinesMatchToAccountingLines(AccountingDocument accountingDocument) {
        LOG.debug("capitalAssetsAccountLinesMatchToAccountingLines(accountingDocument) - start");

        boolean distributed = true;
        
        if (accountingDocument instanceof CapitalAssetEditable == false) {
            return true;
        }

        CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) accountingDocument;
        List<CapitalAssetInformation> capitalAssets = capitalAssetEditable.getCapitalAssetInformation();

        int index = 0;
        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "[" + index + "]." + KFSPropertyConstants.CAPITAL_ASSET_NUMBER;
            
            if (!checkAccountingLineExists(accountingDocument, capitalAsset, errorPathPrefix)) {
              //  MessageMap errors = GlobalVariables.getMessageMap();
              //  errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);
              //  String parentName = (capitalAsset.getCapitalAssetActionIndicator().equalsIgnoreCase(KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR) ? KFSPropertyConstants.CAPITAL_ASSET_INFORMATION :  KFSPropertyConstants.CAPITAL_ASSET_MODIFY_INFORMATION);
             //   errors.addToErrorPath(parentName);
             //   String errorPathPrefix = KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "[" + index + "].";
             //   errors.addToErrorPath(errorPathPrefix);
                //account does not exist so put out an error message and get out.
             //   GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix, KFSKeyConstants.ERROR_ASSET_ACCOUNT_NUMBER_LINE_NOT_IN_SOURCE_OR_TARGET_ACCOUNTING_LINES, accountNumber);
                
              // errors.removeFromErrorPath(errorPathPrefix);
              //  errors.removeFromErrorPath(parentName);
              //  errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);
                index++;
                distributed = false;
                
                break;
            }
        }
        
        return distributed;
    }
    
    /**
     * compares the account number from the capital asset accounting lines
     * to the source/target accounting lines.  If the line does not exist
     * then return false, else return true.
     * 
     * @param accountingDocument
     * @param capitalAsset
     * @return true if capital asset account line exists in 
     * source/target lines else return false
     */
    protected boolean checkAccountingLineExists(AccountingDocument accountingDocument, CapitalAssetInformation capitalAsset, String errorPathPrefix) {
        boolean exists = true;
        
        List<CapitalAssetAccountsGroupDetails> groupAccounts = capitalAsset.getCapitalAssetAccountsGroupDetails();
        for (CapitalAssetAccountsGroupDetails groupAccount: groupAccounts) {
            if (!accountLineExists(accountingDocument, groupAccount)) {
                //this account is not found in source/target accounts list...
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix, KFSKeyConstants.ERROR_ASSET_ACCOUNT_NUMBER_LINE_NOT_IN_SOURCE_OR_TARGET_ACCOUNTING_LINES, groupAccount.getAccountNumber());
                return false;
            }
        }
        
        return exists;
    }
    
    /**
     * 
     * @param accountingDocument
     * @param groupAccount
     * @return true if capital asset account exists in source/target lines else return false
     */
    protected boolean accountLineExists(AccountingDocument accountingDocument, CapitalAssetAccountsGroupDetails groupAccountLine) {
        boolean exists = false;
        
        List<SourceAccountingLine> sourceAccountLines = accountingDocument.getSourceAccountingLines();
        for (SourceAccountingLine sourceAccount : sourceAccountLines)  {
            if (groupAccountLine.getSequenceNumber().compareTo(sourceAccount.getSequenceNumber()) == 0 && 
                    groupAccountLine.getFinancialDocumentLineTypeCode().equals(sourceAccount.getFinancialDocumentLineTypeCode()) && 
                    groupAccountLine.getChartOfAccountsCode().equals(sourceAccount.getChartOfAccountsCode()) && 
                    groupAccountLine.getAccountNumber().equals(sourceAccount.getAccountNumber()) && 
                    groupAccountLine.getFinancialObjectCode().equals(sourceAccount.getFinancialObjectCode())) {
                return true;
            }
        }
        
        List<TargetAccountingLine> targetAccountLines = accountingDocument.getTargetAccountingLines();
        for (TargetAccountingLine targetAccount : targetAccountLines)  {
            if (groupAccountLine.getSequenceNumber().compareTo(targetAccount.getSequenceNumber()) == 0 && 
                    groupAccountLine.getFinancialDocumentLineTypeCode().equals(targetAccount.getFinancialDocumentLineTypeCode()) && 
                    groupAccountLine.getChartOfAccountsCode().equals(targetAccount.getChartOfAccountsCode()) && 
                    groupAccountLine.getAccountNumber().equals(targetAccount.getAccountNumber()) && 
                    groupAccountLine.getFinancialObjectCode().equals(targetAccount.getFinancialObjectCode())) {
                return true;
            }
        }

        return exists;
    
    }
    
    /**
     * total amount in each capital asset is compared to the distributed accounting lines
     * and returns true if they are equal, else return false.
     * 
     * @param accountingDocument
     * @return true if total amount in capital asset match its distributed accounting lines else
     * return false.
     */
    protected boolean amountsForCapitalAssetsAndAccountLinesMatch(AccountingDocument accountingDocument) {
        LOG.debug("amountsForCapitalAssetsAndAccountLinesMatch(accountingDocument) - start");

        boolean amountMatch = true ;
        
        if (accountingDocument instanceof CapitalAssetEditable == false) {
            return true;
        }

        CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) accountingDocument;
        List<CapitalAssetInformation> capitalAssets = capitalAssetEditable.getCapitalAssetInformation();
        
        int accountIndex = 0;
        
        List<SourceAccountingLine> sourceAccountLines = accountingDocument.getSourceAccountingLines();
        for (SourceAccountingLine sourceAccount : sourceAccountLines)  {
            if (capitalAssetBuilderModuleService.hasCapitalAssetObjectSubType(sourceAccount)) {
                //capital object code so we need to check capital asset info...
                //check if this sourceAccount amount match from accounting lines in capital assets....
                KualiDecimal distributedAmount = getSourceDistributedTotalAmount(sourceAccount, capitalAssets);
                //if the amounts to not match then do not proceed further...
                if (sourceAccount.getAmount().compareTo(distributedAmount) != 0) {
                    //account does not exist so put out an error message and get out.
                    GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KFSPropertyConstants.DOCUMENT + "." + KFSConstants.SOURCE_ACCOUNTING_LINE_ERRORS + "[" + accountIndex + "]" + "." + KFSPropertyConstants.AMOUNT, KFSKeyConstants.ERROR_DOCUMENT_SOURCE_ACCOUNTING_LINE_AMOUNT_NOT_DISTRIBUTED, sourceAccount.getAccountNumber());
                    amountMatch = false;
                    accountIndex++;
                    
                    break;
                }
            }
        }

        accountIndex = 0;
        List<TargetAccountingLine> targetAccountLines = accountingDocument.getTargetAccountingLines();
        for (TargetAccountingLine targetAccount : targetAccountLines)  {
            //check if this targetAccount does exist in any one capital assets....
            if (capitalAssetBuilderModuleService.hasCapitalAssetObjectSubType(targetAccount)) {
                //capital object code so we need to check capital asset info...
                //check if this sourceAccount amount match from accounting lines in capital assets....
                KualiDecimal distributedAmount = getTargetDistributedTotalAmount(targetAccount, capitalAssets);
                //if the amounts to not match then do not proceed further...
                if (targetAccount.getAmount().compareTo(distributedAmount) != 0) {
                    //account does not exist so put out an error message and get out.
                    GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KFSPropertyConstants.DOCUMENT + "." + KFSConstants.TARGET_ACCOUNTING_LINE_ERRORS + "[" + accountIndex + "]" + "." + KFSPropertyConstants.AMOUNT, KFSKeyConstants.ERROR_DOCUMENT_TARGET_ACCOUNTING_LINE_AMOUNT_NOT_DISTRIBUTED, targetAccount.getAccountNumber());
                    amountMatch = false;
                    accountIndex++;
                    
                    break;
                }
            }
        }
        
        return amountMatch;
    }
    
    /**
     * checks amount from source accounting line to that of all distributed accounting line 
     * from capital assets and return true if matched else false.
     * 
     * @param accountLine
     * @param capitalAssets
     * @return true if amount match to distributed accounting lines in capital assets
     * else return false
     */
    protected KualiDecimal getSourceDistributedTotalAmount(SourceAccountingLine accountLine, List<CapitalAssetInformation> capitalAssets) {
        KualiDecimal amount = new KualiDecimal(0);
        
        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            for (CapitalAssetAccountsGroupDetails groupAccountLine : capitalAsset.getCapitalAssetAccountsGroupDetails()) {
                if (groupAccountLine.getSequenceNumber().compareTo(accountLine.getSequenceNumber()) == 0 && 
                        groupAccountLine.getFinancialDocumentLineTypeCode().equals(accountLine.getFinancialDocumentLineTypeCode()) && 
                        groupAccountLine.getChartOfAccountsCode().equals(accountLine.getChartOfAccountsCode()) && 
                        groupAccountLine.getAccountNumber().equals(accountLine.getAccountNumber()) && 
                        groupAccountLine.getFinancialObjectCode().equals(accountLine.getFinancialObjectCode())) {
                    amount = amount.add(groupAccountLine.getAmount());
                }
            }
        }
        
        return amount;
    }
    
    /**
     * checks amount from target accounting line to that of all distributed accounting line 
     * from capital assets and return true if matched else false.
     * 
     * @param accountLine
     * @param capitalAssets
     * @return true if amount match to distributed accounting lines in capital assets
     * else return false
     */
    protected KualiDecimal getTargetDistributedTotalAmount(TargetAccountingLine accountLine, List<CapitalAssetInformation> capitalAssets) {
        KualiDecimal amount = new KualiDecimal(0);
        
        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            for (CapitalAssetAccountsGroupDetails groupAccountLine : capitalAsset.getCapitalAssetAccountsGroupDetails()) {
                if (groupAccountLine.getSequenceNumber().compareTo(accountLine.getSequenceNumber()) == 0 && 
                        groupAccountLine.getFinancialDocumentLineTypeCode().equals(accountLine.getFinancialDocumentLineTypeCode()) && 
                        groupAccountLine.getChartOfAccountsCode().equals(accountLine.getChartOfAccountsCode()) && 
                        groupAccountLine.getAccountNumber().equals(accountLine.getAccountNumber()) && 
                        groupAccountLine.getFinancialObjectCode().equals(accountLine.getFinancialObjectCode())) {
                    amount = amount.add(groupAccountLine.getAmount());
                }
            }
        }
        
        return amount;
    }
    
    /**
     * compares each capital asset amount to its distributed accounting lines.  If they match
     * return true else false
     * 
     * @param accountingDocument
     * @return true if capital asset amount match to its distributed accounting lines
     * else return false
     */
    protected boolean amountsForCapitalAssetsAndDistributedAccountLinesMatch(AccountingDocument accountingDocument) {
        LOG.debug("amountsForCapitalAssetsAndDistributedAccountLinesMatch(accountingDocument) - start");

        boolean amountMatch = true;
        
        if (accountingDocument instanceof CapitalAssetEditable == false) {
            return true;
        }

        CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) accountingDocument;
        List<CapitalAssetInformation> capitalAssets = capitalAssetEditable.getCapitalAssetInformation();

        int index = 0;
        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "[" + index + "]." + KFSPropertyConstants.CAPITAL_ASSET_NUMBER;
            
            if (!checkAmount(capitalAsset, errorPathPrefix)) {
                index++;
                amountMatch = false;
                
                break;
            }
        }
        
        return amountMatch;
    
    }
    
    /**
     * compares the capital asset amount to this accounting lines and if they match return
     * true else return false
     * to the source/target accounting lines.  If the line does not exist
     * then return false, else return true.
     * 
     * @param capitalAsset
     * @return true if capital asset account line exists in 
     * source/target lines else return false
     */
    protected boolean checkAmount(CapitalAssetInformation capitalAsset, String errorPathPrefix) {
        boolean amountMatch = true;
        
        KualiDecimal distributedAccountLinesAmount = new KualiDecimal(0);
        
        List<CapitalAssetAccountsGroupDetails> groupAccounts = capitalAsset.getCapitalAssetAccountsGroupDetails();
        for (CapitalAssetAccountsGroupDetails groupAccount: groupAccounts) {
            distributedAccountLinesAmount = distributedAccountLinesAmount.add(groupAccount.getAmount());
        }
        
        if (capitalAsset.getCapitalAssetLineAmount().compareTo(distributedAccountLinesAmount) != 0) {
            //amount from capital asset does not match its accounting lines sum...
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix, KFSKeyConstants.ERROR_ASSET_LINE_AMOUNT_NOT_EQUAL_TO_DISTRIBUTED_ACCOUNTING_LINES);
            return false;
        }
        
        return amountMatch;
    }
    
    /**
     * Sets the accountingDocumentForValidation attribute value.
     * 
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

}
