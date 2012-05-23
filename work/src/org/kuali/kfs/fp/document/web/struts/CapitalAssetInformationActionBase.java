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
package org.kuali.kfs.fp.document.web.struts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.CapitalAccountingLines;
import org.kuali.kfs.fp.businessobject.CapitalAssetAccountsGroupDetails;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformationDetail;
import org.kuali.kfs.fp.document.CapitalAccountingLinesDocumentBase;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.fp.document.CapitalAssetInformationDocumentBase;
import org.kuali.kfs.integration.cam.businessobject.Asset;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.service.SegmentedLookupResultsService;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This is the action class for the CapitalAssetInformationActionBase.
 */
public abstract class CapitalAssetInformationActionBase extends KualiAccountingDocumentActionBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CapitalAssetInformationActionBase.class);
    
    /**
     * Multi-value asset lookup is implemented through the integration package by module's service
     * to gather the results. The results are processed for any capital accounting lines where
     * the line is marked for selection.  After the capital assets are populated with the 
     * selected asset numbers, the system control amount is redistributed equally among the assets
     * when the distribution method is "distribute cost equally".
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        
        //process the multiple value lookup data 
        CapitalAssetInformationFormBase capitalAssetInformationFormBase = (CapitalAssetInformationFormBase) form;

        Collection<PersistableBusinessObject> rawValues = null;
        Map<String, Set<String>> segmentedSelection = new HashMap<String, Set<String>>();

        // If multiple asset lookup was used to select the assets, then....
        if (StringUtils.equals(KFSConstants.MULTIPLE_VALUE, capitalAssetInformationFormBase.getRefreshCaller())) {
            String lookupResultsSequenceNumber = capitalAssetInformationFormBase.getLookupResultsSequenceNumber();

            if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
                // actually returning from a multiple value lookup
                Set<String> selectedIds = SpringContext.getBean(SegmentedLookupResultsService.class).retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
                for (String selectedId : selectedIds) {
                    String selectedObjId = StringUtils.substringBefore(selectedId, ".");
                    if (!segmentedSelection.containsKey(selectedObjId)) {
                        segmentedSelection.put(selectedObjId, new HashSet<String>());
                    }
                }
                // Retrieving selected data from table.
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Asking segmentation service for object ids " + segmentedSelection.keySet());
                }
                rawValues = SpringContext.getBean(SegmentedLookupResultsService.class).retrieveSelectedResultBOs(lookupResultsSequenceNumber, segmentedSelection.keySet(), Asset.class, GlobalVariables.getUserSession().getPerson().getPrincipalId());
            }
            
            if (rawValues == null || rawValues.size() == 0) {
                //redistribute capital asset amount to its group accounting lines on refresh
                DistributeCapitalAssetAmountToGroupAccountingLines((KualiAccountingDocumentFormBase) form);
                
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
            
            KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
            CapitalAccountingLinesFormBase calfb = (CapitalAccountingLinesFormBase) form;
            CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) calfb.getFinancialDocument();
            List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();        
            List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);

            List<CapitalAccountingLines> selectedCapitalAccountingLines = new ArrayList<CapitalAccountingLines>();
            
            for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
                if (capitalAccountingLine.isSelectLine()  && !capitalAccountingLine.isAmountDistributed()) {
                    selectedCapitalAccountingLines.add(capitalAccountingLine);
                }
            }
            
            //process the data and create assets only for those accounting lines
            //where capital accounting line is "selected" and its amount is greater than already allocated.
            if (rawValues != null) {
                for (PersistableBusinessObject bo : rawValues) {
                    Asset asset = (Asset) bo;
                    
                 //   boolean addIt = true;
                    boolean addIt = modifyAssetAlreadyExists(capitalAssetInformation, asset.getCapitalAssetNumber());
                    
                    // If it doesn't already exist in the list add it.
                    if (addIt) {
                        createNewModifyCapitalAsset(selectedCapitalAccountingLines, capitalAssetInformation, calfb.getDocument().getDocumentNumber(), KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR, getNextCapitalAssetLineNumber(kualiAccountingDocumentFormBase), asset.getCapitalAssetNumber());                                
                    }
                }
                
                checkCapitalAccountingLinesSelected(calfb);
                
                // remove the blank capital asset modify records now...
                removeEmptyCapitalAssetModify(capitalAssetInformation);
                
                //now redistribute the amount for all assets if needed....
                redistributeModifyCapitalAssetAmount(mapping, form, request, response);                
            }
        }
        
        //redistribute capital asset amount to its group accounting lines on refresh
        DistributeCapitalAssetAmountToGroupAccountingLines((KualiAccountingDocumentFormBase) form);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
        
    }
    
    /**
     * Remove if and any blank capital asset modify lines.
     * @param capitalAssetInformation
     */
    protected void removeEmptyCapitalAssetModify(List<CapitalAssetInformation> capitalAssetInformation) {
        List<CapitalAssetInformation> removeCapitalAssetModify = new ArrayList<CapitalAssetInformation>();
        
        for (CapitalAssetInformation capitalAssetRecord : capitalAssetInformation) {
            if (ObjectUtils.isNull(capitalAssetRecord.getCapitalAssetNumber()) && KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR.equalsIgnoreCase(capitalAssetRecord.getCapitalAssetActionIndicator())) {
                removeCapitalAssetModify.add(capitalAssetRecord);
            }
        }
        
        if (!removeCapitalAssetModify.isEmpty()) {
            capitalAssetInformation.removeAll(removeCapitalAssetModify);
        }
    }
    
    /**
     * sums the capital assets amount distributed so far for a given capital accounting line
     * 
     * @param currentCapitalAssetInformation
     * @param existingCapitalAsset
     * @return capitalAssetsAmount amount that has been distributed for the specific capital accounting line
     */
    protected KualiDecimal getCapitalAssetsAmountAllocated(List<CapitalAssetInformation> currentCapitalAssetInformation, CapitalAccountingLines capitalAccountingLine) {
        //check the capital assets records totals
        KualiDecimal capitalAssetsAmount = KualiDecimal.ZERO;

        
        for (CapitalAssetInformation capitalAsset : currentCapitalAssetInformation) {
            List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
            for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
                if (groupAccountLine.getCapitalAssetLineNumber().compareTo(capitalAsset.getCapitalAssetLineNumber()) == 0 &&
                        groupAccountLine.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&                        
                        groupAccountLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                        groupAccountLine.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
                        groupAccountLine.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
                        groupAccountLine.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
                    capitalAssetsAmount = capitalAssetsAmount.add(groupAccountLine.getAmount());
                }
            }
         }
        
        return capitalAssetsAmount;
    }
    
    /**
     * checks the capital asset information list for the specific capital asset number
     * that was returned as part of the multi-value lookup.
     * 
     * @param capitalAssetInformation
     * @param capitalAssetNumber
     * @return true if asset does not exist in the list else return false
     */
    protected boolean modifyAssetAlreadyExists(List<CapitalAssetInformation> capitalAssetInformation, Long capitalAssetNumber) {
       boolean addIt = true; 
       KualiDecimal capitalAssetCreatedAmount = KualiDecimal.ZERO;
       
       for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
           if (KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR.equals(capitalAsset.getCapitalAssetActionIndicator()) &&
                   ObjectUtils.isNotNull(capitalAsset.getCapitalAssetNumber()) &&
                   capitalAsset.getCapitalAssetNumber().compareTo(capitalAssetNumber) == 0) {
                addIt = false;
                break;
            }
        }
        
        return addIt;
    }

    /**
     * checks if the selected capital accounting lines have a capital asset created by checking the
     * accounts associated with the capital asset information.
     * 
     * @param capitalAccountingLine
     * @param capitalAsset
     * @return true if capital accounting line has a capital asset else return false.
     */
    protected boolean capitalAssetExists(List<CapitalAccountingLines> capitalAccountingLines, CapitalAssetInformation capitalAsset, String actionTypeCode) {
        boolean exists = true;
        
        List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
        
        for(CapitalAccountingLines capitalAccountLine : capitalAccountingLines) {
            for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
                if (groupAccountLine.getCapitalAssetLineNumber().compareTo(capitalAsset.getCapitalAssetLineNumber()) == 0 &&
                        groupAccountLine.getSequenceNumber().compareTo(capitalAccountLine.getSequenceNumber()) == 0 &&                        
                        groupAccountLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                        groupAccountLine.getChartOfAccountsCode().equals(capitalAccountLine.getChartOfAccountsCode()) && 
                        groupAccountLine.getAccountNumber().equals(capitalAccountLine.getAccountNumber()) && 
                        groupAccountLine.getFinancialObjectCode().equals(capitalAccountLine.getFinancialObjectCode())) {
                     return exists;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 
     * @param form
     */
    protected void redistributeCostEquallyForModifiedAssets(ActionForm form) {
        KualiDecimal remainingAmountToDistribute = KualiDecimal.ZERO;

        CapitalAccountingLinesFormBase calfb = (CapitalAccountingLinesFormBase) form;
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) calfb.getFinancialDocument();
        
        String distributionCode = calfb.getCapitalAccountingLine().getDistributionCode();
        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();

        List<CapitalAccountingLines> selectedCapitalAccountingLines = new ArrayList<CapitalAccountingLines>();
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.isSelectLine()  && !capitalAccountingLine.isAmountDistributed()) {
                selectedCapitalAccountingLines.add(capitalAccountingLine);
                remainingAmountToDistribute = remainingAmountToDistribute.add(capitalAccountingLine.getAmount());
            }
        }
        
        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);
        redistributeAmountsForAccountingsLineForModifyAssets(selectedCapitalAccountingLines, capitalAssetInformation, remainingAmountToDistribute);
        
        //now process any capital assets that has distribution set to "by amount"
        redistributeAmountsForAccountingsLineForModifyAssetsByAmounts(selectedCapitalAccountingLines, capitalAssetInformation, remainingAmountToDistribute);
    }

    /**
     * 
     * @param accountingLine
     * @param capitalAssetInformation
     * @param remainingAmountToDistribute
     */
    protected void redistributeAmountsForAccountingsLineForModifyAssets(List<CapitalAccountingLines> selectedCapitalAccountingLines, List<CapitalAssetInformation> capitalAssetInformation, KualiDecimal remainingAmountToDistribute) {
        //get the total capital assets quantity
        int totalQuantity = getNumberOfModifiedAssetsExist(selectedCapitalAccountingLines, capitalAssetInformation);
        if (totalQuantity > 0) {    
            KualiDecimal equalModifyAssetAmount = remainingAmountToDistribute.divide(new KualiDecimal(totalQuantity), true);

            int lastAssetIndex = 0;
            CapitalAssetInformation lastCapitalAsset = new CapitalAssetInformation();
            
            if (equalModifyAssetAmount.compareTo(KualiDecimal.ZERO) != 0) {
                for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
                    if (KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR.equals(capitalAsset.getCapitalAssetActionIndicator()) &&
                            (ObjectUtils.isNotNull(capitalAsset.getCapitalAssetNumber())) &&
                            (KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_CODE.equalsIgnoreCase(capitalAsset.getDistributionAmountCode()))) {
                        if (capitalAssetExists(selectedCapitalAccountingLines, capitalAsset, KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR)) {
                            capitalAsset.setCapitalAssetQuantity(1);
                            redistributeEqualAmounts(selectedCapitalAccountingLines, capitalAsset, equalModifyAssetAmount, totalQuantity);
                            lastAssetIndex++;
                            //get a reference to the last capital create asset to fix any variances...
                            lastCapitalAsset = capitalAsset;
                        }
                    }
                }
            }
            
            //apply any variance left to the last 
            KualiDecimal varianceForAssets = remainingAmountToDistribute.subtract(equalModifyAssetAmount.multiply(new KualiDecimal(lastAssetIndex)));
            if (varianceForAssets.isNonZero()) {
                lastCapitalAsset.setCapitalAssetLineAmount(lastCapitalAsset.getCapitalAssetLineAmount().add(varianceForAssets));
                redistributeEqualAmountsOnLastCapitalAsset(selectedCapitalAccountingLines, lastCapitalAsset, capitalAssetInformation, KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR);            
            }
        }
    }
    
    /**
     * for modified assets the amount is distributed
     * @param selectedCapitalAccountingLines
     * @param capitalAssetInformation
     */
    protected void redistributeAmountsForAccountingsLineForModifyAssetsByAmounts(List<CapitalAccountingLines> selectedCapitalAccountingLines, List<CapitalAssetInformation> capitalAssetInformation, KualiDecimal remainingAmountToDistribute) {
        KualiDecimal appliedAccountingLinesTotal = KualiDecimal.ZERO;
        CapitalAssetAccountsGroupDetails lastAccountingLine = new CapitalAssetAccountsGroupDetails();

        for (CapitalAccountingLines capitalAccountLine : selectedCapitalAccountingLines) {
            for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
                if (KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_CODE.equalsIgnoreCase(capitalAsset.getDistributionAmountCode())) {
                    if (capitalAsset.getCapitalAssetLineAmount().compareTo(getAccountingLinesTotalAmount(capitalAsset)) != 0) {
                        List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
                        for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
                            if (groupAccountLine.getCapitalAssetLineNumber().compareTo(capitalAsset.getCapitalAssetLineNumber()) == 0 &&
                                    groupAccountLine.getSequenceNumber().compareTo(capitalAccountLine.getSequenceNumber()) == 0 &&                        
                                    groupAccountLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                                    groupAccountLine.getChartOfAccountsCode().equals(capitalAccountLine.getChartOfAccountsCode()) && 
                                    groupAccountLine.getAccountNumber().equals(capitalAccountLine.getAccountNumber()) && 
                                    groupAccountLine.getFinancialObjectCode().equals(capitalAccountLine.getFinancialObjectCode())) {
                                    //found the accounting line
                                groupAccountLine.setAmount((capitalAsset.getCapitalAssetLineAmount().multiply(capitalAccountLine.getAccountLinePercent())).divide(new KualiDecimal(100)));
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * redistributes the amounts to the capital asset and its related group accounting lines.
     * Adjusts any variance to the last capital asset accounting line.
     * 
     * @param selectedCapitalAccountingLines
     * @param capitalAsset
     * @param amount
     * @param totalQuantity
     */
    protected void redistributeEqualAmounts(List<CapitalAccountingLines> selectedCapitalAccountingLines, CapitalAssetInformation capitalAsset, KualiDecimal amount, int totalQuantity) {
        int assetQuantity = 0;
        KualiDecimal totalCapitalAssetQuantity = new KualiDecimal(totalQuantity);
        
        if (ObjectUtils.isNotNull(capitalAsset.getCapitalAssetQuantity())) {
            assetQuantity = capitalAsset.getCapitalAssetQuantity();
        }
        
        capitalAsset.setCapitalAssetLineAmount(capitalAsset.getCapitalAssetLineAmount().add(amount.multiply(new KualiDecimal(assetQuantity))));
        int lastAccountIndex = 0;
        KualiDecimal appliedAccountingLinesTotal = KualiDecimal.ZERO;
        
        CapitalAssetAccountsGroupDetails lastLine = new CapitalAssetAccountsGroupDetails();
        
        List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
        
        for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
            KualiDecimal capitalAccountingLineAmount = getCapitalAssetAccountLineAmount(selectedCapitalAccountingLines, groupAccountLine, capitalAsset);
            KualiDecimal calculatedLineAmount = capitalAccountingLineAmount.divide(totalCapitalAssetQuantity, true);
            groupAccountLine.setAmount(calculatedLineAmount.multiply(new KualiDecimal(assetQuantity)));
            appliedAccountingLinesTotal = appliedAccountingLinesTotal.add(groupAccountLine.getAmount());
            
            lastAccountIndex++;
            lastLine = groupAccountLine;
        }
        
        //apply any variance left to the last 
        KualiDecimal varianceForLines = capitalAsset.getCapitalAssetLineAmount().subtract(appliedAccountingLinesTotal);
        if (varianceForLines.isNonZero()) {
            lastLine.setAmount(lastLine.getAmount().add(varianceForLines));
        }
    }
    
    /**
     * redistributes the amounts to the capital asset and its related group accounting lines.
     * Adjusts any variance to the last capital asset accounting line.
     * 
     * @param selectedCapitalAccountingLines
     * @param capitalAsset
     * @param capitalAssetInformation
     * @param actionTypeCode
     */
    protected void redistributeEqualAmountsOnLastCapitalAsset(List<CapitalAccountingLines> selectedCapitalAccountingLines, CapitalAssetInformation lastCapitalAsset, List<CapitalAssetInformation> capitalAssetInformation, String actionTypeCode) {
        for (CapitalAccountingLines capitalAccountingLine : selectedCapitalAccountingLines ) {
            KualiDecimal lineAmount = capitalAccountingLine.getAmount();
            KualiDecimal distributedAmount = getAccountingLinesDistributedAmount(capitalAccountingLine, capitalAssetInformation, actionTypeCode);
            KualiDecimal difference = lineAmount.subtract(distributedAmount);
            if (!difference.isZero()) {
                adjustAccountingLineAmountOnLastCapitalAsset(capitalAccountingLine, lastCapitalAsset, difference);
            }
        }
    }
    
    /**
     * Gets the amount on the capital assets line for the selected capital accounting line by 
     * finding the group accounting line.  When group accounting line is found in the selected
     * capital accounting lines, the amount from that capital accounting line is returned.
     * @param selectedCapitalAccountingLines
     * @param groupAccountLine
     * @return lineAmount
     */
    protected KualiDecimal getCapitalAssetAccountLineAmount(List<CapitalAccountingLines> selectedCapitalAccountingLines, CapitalAssetAccountsGroupDetails groupAccountLine, CapitalAssetInformation capitalAsset) {
        KualiDecimal lineAmount = KualiDecimal.ZERO;
        
        for (CapitalAccountingLines  capitalAccountingLine : selectedCapitalAccountingLines) {
            if (groupAccountLine.getCapitalAssetLineNumber().compareTo(capitalAsset.getCapitalAssetLineNumber()) == 0 &&
                    groupAccountLine.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&                        
                    groupAccountLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                    groupAccountLine.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
                    groupAccountLine.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
                    groupAccountLine.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
                return capitalAccountingLine.getAmount();
            }
        }
        
        return lineAmount;
    }
    
    /**
     * 
     * @param capitalAccountingLines
     * @param capitalAssetInformation
     */
    protected void redistributeIndividualAmountsForAccountingLinesForModifyAssets(List<CapitalAccountingLines> selectedCapitalAccountingLines, List<CapitalAssetInformation> capitalAssetInformation) {
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            if (KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR.equals(capitalAsset.getCapitalAssetActionIndicator())) {
                if (capitalAssetExists(selectedCapitalAccountingLines, capitalAsset, KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR)) {
                    redistributeIndividualAmounts(selectedCapitalAccountingLines, capitalAsset);                        
                }
            }
        }
    }
    
    /**
     * checks the capital accounting line's amount to the sum of the distributed
     * accounting lines amounts and adjusts if there are any variances..
     * 
     * @param selectedCapitalAccountingLines
     * @param capitalAssetInformation
     */
    protected void adjustCapitalAssetsAccountingLinesAmounts(List<CapitalAccountingLines> selectedCapitalAccountingLines, List<CapitalAssetInformation> capitalAssetInformation) {
        for (CapitalAccountingLines capitalAcctLine : selectedCapitalAccountingLines) {
            adjustAccountingLinesAmounts(capitalAcctLine, capitalAssetInformation);
        }
    }
    
    /**
     * for each capital account line, compares its amounts to the accounting lines
     * on capital assets and adjusts its accounting lines amounts for any variances.
     * 
     * @param capitalAcctLine
     * @param capitalAssetInformation
     */
    protected void adjustAccountingLinesAmounts(CapitalAccountingLines capitalAcctLine, List<CapitalAssetInformation> capitalAssetInformation) {
        
        CapitalAssetAccountsGroupDetails lastAcctLine = null;
        
        KualiDecimal totalAccountsAmount = KualiDecimal.ZERO;
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            if (capitalAsset.getCapitalAssetLineAmount().compareTo(getAccountingLinesTotalAmount(capitalAsset)) != 0) {
                List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
                for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
                    if (groupAccountLine.getCapitalAssetLineNumber().compareTo(capitalAsset.getCapitalAssetLineNumber()) == 0 &&
                            groupAccountLine.getSequenceNumber().compareTo(capitalAcctLine.getSequenceNumber()) == 0 &&                        
                            groupAccountLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAcctLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                            groupAccountLine.getChartOfAccountsCode().equals(capitalAcctLine.getChartOfAccountsCode()) && 
                            groupAccountLine.getAccountNumber().equals(capitalAcctLine.getAccountNumber()) && 
                            groupAccountLine.getFinancialObjectCode().equals(capitalAcctLine.getFinancialObjectCode())) {
                        totalAccountsAmount = totalAccountsAmount.add(groupAccountLine.getAmount());
                        lastAcctLine = groupAccountLine;
                    }
                }
            }
        }
        
        KualiDecimal variance = capitalAcctLine.getAmount().subtract(totalAccountsAmount);
        if (variance.isNonZero() && ObjectUtils.isNotNull(lastAcctLine)) {
            lastAcctLine.setAmount(lastAcctLine.getAmount().add(variance));
        }
    }
    
    /**
     * adjusts variances on capital assets where distribution method is set
     * as "distribute evenly" and capital asset amount is odd value.  Reduce the
     * capital asset amount line by 0.01 and then adjust the account amounts. Finally
     * any variance between capital accounting lines and capital assets is added
     * to the last capital asset and its accounting lines.
     * 
     * @param capitalAssetInformation
     */
    protected void adjustVarianceOnCapitalAssets(List<CapitalAssetInformation> capitalAssetInformation) {
        KualiDecimal adjustedCapitalAssetBalance = KualiDecimal.ZERO;
        CapitalAssetInformation lastCapitalAsset = null;
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            //look at only cost equall assets...
            if (KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_CODE.equalsIgnoreCase(capitalAsset.getDistributionAmountCode())) {
                if (capitalAsset.getCapitalAssetLineAmount().mod(new KualiDecimal(2)) != KualiDecimal.ZERO) {
                    capitalAsset.setCapitalAssetLineAmount(capitalAsset.getCapitalAssetLineAmount().subtract(new KualiDecimal(0.01)));
                    adjustedCapitalAssetBalance = adjustedCapitalAssetBalance.add(new KualiDecimal(0.01));
                    lastCapitalAsset = capitalAsset;                    
                }
            }
        }
        
        if (ObjectUtils.isNotNull(lastCapitalAsset) && adjustedCapitalAssetBalance.isNonZero()) {
            lastCapitalAsset.setCapitalAssetLineAmount(lastCapitalAsset.getCapitalAssetLineAmount().add(adjustedCapitalAssetBalance));
        }
    }
    
    /**
     * 
     * @param capitalAccountingLines
     * @param capitalAssetInformation
     */
    protected void redistributeIndividualAmountsForAccountingLinesForCreateAssets(List<CapitalAccountingLines> selectedCapitalAccountingLines, List<CapitalAssetInformation> capitalAssetInformation) {
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            if (KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR.equals(capitalAsset.getCapitalAssetActionIndicator())) {
                if (capitalAssetExists(selectedCapitalAccountingLines, capitalAsset, KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR)) {
                    redistributeIndividualAmounts(selectedCapitalAccountingLines, capitalAsset);                        
                }
            }
        }
    }
    
    /**
     * 
     * @param selectedCapitalAccountingLines
     * @param capitalAsset
     */
    protected void redistributeIndividualAmounts(List<CapitalAccountingLines> selectedCapitalAccountingLines, CapitalAssetInformation capitalAsset) {
        KualiDecimal capitalAssetAmount = capitalAsset.getCapitalAssetLineAmount();
        
        KualiDecimal totalCapitalAccountsAmount = getTotalCapitalAccountsAmounts(selectedCapitalAccountingLines);
        
        CapitalAssetAccountsGroupDetails lastAccountLine = new CapitalAssetAccountsGroupDetails();
        int lastAccountLineIndex = 0;
        KualiDecimal distributedAmount = KualiDecimal.ZERO;
        
        List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
        for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
            BigDecimal linePercent = getCapitalAccountingLinePercent(selectedCapitalAccountingLines, groupAccountLine, totalCapitalAccountsAmount);
            groupAccountLine.setAmount(new KualiDecimal(capitalAssetAmount.bigDecimalValue().multiply(linePercent)));
            lastAccountLine = groupAccountLine;
            distributedAmount = distributedAmount.add(groupAccountLine.getAmount());
            
            lastAccountLineIndex++;
        }
        
        lastAccountLine.setAmount(lastAccountLine.getAmount().add(capitalAssetAmount.subtract(distributedAmount)));
    }
    
    /**
     * 
     * @param selectedCapitalAccountingLines
     * @param groupAccountLine
     * @param totalCapitalAccountsAmount
     * @return percent
     */
    protected BigDecimal getCapitalAccountingLinePercent(List<CapitalAccountingLines> selectedCapitalAccountingLines, CapitalAssetAccountsGroupDetails groupAccountLine, KualiDecimal totalCapitalAccountsAmount) {
        for (CapitalAccountingLines capitalAccountingLine : selectedCapitalAccountingLines) {
            if (groupAccountLine.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&
                    groupAccountLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                    groupAccountLine.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
                    groupAccountLine.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
                    groupAccountLine.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
                return (capitalAccountingLine.getAmount().bigDecimalValue().divide(totalCapitalAccountsAmount.bigDecimalValue(), KFSConstants.CapitalAssets.CAPITAL_ACCOUNT_LINE_PERCENT_SCALE, KFSConstants.CapitalAssets.PERCENT_SCALE));
            }
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * 
     * @param capitalAccountingLines
     * @param capitalAssetInformation
     * @param remainingAmountToDistribute
     */
    protected void redistributeEqualAmountsForAccountingLineForCreateAssets(List<CapitalAccountingLines> selectedCapitalAccountingLines, List<CapitalAssetInformation> capitalAssetInformation, KualiDecimal remainingAmountToDistribute) {
        //get the total capital assets quantity
        int totalQuantity = getTotalQuantityOfCreateAssets(selectedCapitalAccountingLines, capitalAssetInformation);
        if (totalQuantity > 0) {    
            KualiDecimal equalCreateAssetAmount = remainingAmountToDistribute.divide(new KualiDecimal(totalQuantity), true);

            int lastAssetIndex = 0;
            CapitalAssetInformation lastCapitalAsset = new CapitalAssetInformation();
            
            if (equalCreateAssetAmount.compareTo(KualiDecimal.ZERO) != 0) {
                for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
                    if (KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR.equals(capitalAsset.getCapitalAssetActionIndicator()) &&
                            (KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_CODE.equalsIgnoreCase(capitalAsset.getDistributionAmountCode()))) {
                        if (capitalAssetExists(selectedCapitalAccountingLines, capitalAsset, KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR)) {
                            redistributeEqualAmounts(selectedCapitalAccountingLines, capitalAsset, equalCreateAssetAmount, totalQuantity);                        
                            if (ObjectUtils.isNotNull(capitalAsset.getCapitalAssetQuantity())) {
                                lastAssetIndex = lastAssetIndex + capitalAsset.getCapitalAssetQuantity();
                            }
                            //get a reference to the last capital create asset to fix any variances...
                            lastCapitalAsset = capitalAsset;
                        }
                    }
                }
            }
            
            //apply any variance left to the last 
            KualiDecimal varianceForAssets = remainingAmountToDistribute.subtract(equalCreateAssetAmount.multiply(new KualiDecimal(lastAssetIndex)));
            if (varianceForAssets.isNonZero()) {
                lastCapitalAsset.setCapitalAssetLineAmount(lastCapitalAsset.getCapitalAssetLineAmount().add(varianceForAssets));
                redistributeEqualAmountsOnLastCapitalAsset(selectedCapitalAccountingLines, lastCapitalAsset, capitalAssetInformation, KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR);            
            }
        }
    }
    
    /**
     * 
     * @param selectedCapitalAccountingLines
     * @param capitalAssetInformation
     * @return createAssetsCount count of create assets
     */
    protected int getTotalQuantityOfCreateAssets(List<CapitalAccountingLines> selectedCapitalAccountingLines, List<CapitalAssetInformation> capitalAssetInformation) {
        int totalQuantity = 0; 
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            if (KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR.equals(capitalAsset.getCapitalAssetActionIndicator())) {
                if (capitalAssetExists(selectedCapitalAccountingLines, capitalAsset, KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR)) {
                    if (ObjectUtils.isNotNull(capitalAsset.getCapitalAssetQuantity())) {
                        totalQuantity += capitalAsset.getCapitalAssetQuantity();   
                    }
                }
            }
        }
        
        return totalQuantity;
    }
    
    /**
     * 
     * @param selectedCapitalAccountingLines
     * @param capitalAssetInformation
     * @return createAssetsCount count of create assets
     */
    protected int numberOfCreateAssetsExist(List<CapitalAccountingLines> selectedCapitalAccountingLines, List<CapitalAssetInformation> capitalAssetInformation) {
        int createAssetsCount = 0; 
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            if (KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR.equals(capitalAsset.getCapitalAssetActionIndicator())) {
                if (capitalAssetExists(selectedCapitalAccountingLines, capitalAsset, KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR)) {
                    createAssetsCount++;   
                }
            }
        }
        
        if (createAssetsCount == 0) {
            return 1;
        }
        
        return createAssetsCount;
    }
    
    /**
     * 
     * @param selectedCapitalAccountingLines
     * @param capitalAssetInformation
     * @return modifiedAssetsCount number of modified assets 
     */
    protected int getNumberOfModifiedAssetsExist(List<CapitalAccountingLines> selectedCapitalAccountingLines, List<CapitalAssetInformation> capitalAssetInformation) {
        int modifiedAssetsCount = 0;
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            if (KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR.equals(capitalAsset.getCapitalAssetActionIndicator()) &&
                    ObjectUtils.isNotNull(capitalAsset.getCapitalAssetNumber())) {
                if (capitalAssetExists(selectedCapitalAccountingLines, capitalAsset, KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR)) {
                    modifiedAssetsCount++;
                }
            }
        }
        
        if (modifiedAssetsCount == 0) {
            return 1;
        }
        
        return modifiedAssetsCount;
    }
    
    /**
     * Clear the capital asset information that the user has entered
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward string
     * @throws Exception
     */
    public ActionForward clearCapitalAssetInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("clearCapitalAssetInfo() - start");

        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);

        if (capitalAssetInformation == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        int clearIndex = getSelectedLine(request);
        this.resetCapitalAssetInfo(capitalAssetInformation.get(clearIndex)); 
        
        //now process the remaining capital asset records
        processRemainingCapitalAssetInfo(form, capitalAssetInformation);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * clear up the modify capital asset information.  The amount field is reset to 0
     * Processes any remaining capital assets so that it recalculates the system control
     * and system control remaining amounts.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward string
     * @throws Exception
     */
    public ActionForward clearCapitalAssetModify(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("clearCapitalAssetModify() - start");

        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);

        if (capitalAssetInformation == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        int clearIndex = getSelectedLine(request);
        capitalAssetInformation.get(clearIndex).setCapitalAssetLineAmount(KualiDecimal.ZERO);
        
        //zero out the amount distribute on the accounting lines...
        for (CapitalAssetAccountsGroupDetails groupAccountLine : capitalAssetInformation.get(clearIndex).getCapitalAssetAccountsGroupDetails()) {
            groupAccountLine.setAmount(KualiDecimal.ZERO);
        }
        
        //now process the remaining capital asset records
        processRemainingCapitalAssetInfo(form, capitalAssetInformation);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * inserts capital asset information into capital assets list.
     * Also recalculates the system control and system control remaining amounts.
     * Puts a global error message if the user does not enter capital asset quantity.
     * If the quantity is > 1, it will insert that many tag/location detail records for this
     * capital asset item.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward string
     * @throws Exception
     */
    public ActionForward insertCapitalAssetInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("insertCapitalAssetInfo() - start");

        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);

        if (capitalAssetInformation == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        int addIndex = getSelectedLine(request);        
        if (capitalAssetInformation.get(addIndex).getCapitalAssetQuantity() == null || capitalAssetInformation.get(addIndex).getCapitalAssetQuantity() <= 0) {
            GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_CAPITAL_ASSET_INFORMATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_CAPITAL_ASSET_QUANTITY_REQUIRED);
        }
        else {
            this.addCapitalAssetInfoDetailLines(capitalAssetInformation.get(addIndex));
        }

        /*
        DistributeCapitalAssetAmountToGroupAccountingLines(kualiAccountingDocumentFormBase);
        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = (CapitalAccountingLinesFormBase) kualiAccountingDocumentFormBase;
        checkSelectForCapitalAccountingLines(capitalAccountingLinesFormBase);        
        
        checkCapitalAccountingLinesSelected(capitalAccountingLinesFormBase);
        calculatePercentsForSelectedCapitalAccountingLines(capitalAccountingLinesFormBase);
        
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) capitalAccountingLinesFormBase.getFinancialDocument();
        String distributionAmountCode = capitalAccountingLinesFormBase.getCapitalAccountingLine().getDistributionCode();

        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();        
        
        List<CapitalAccountingLines> selectedCapitalAccountingLines = new ArrayList<CapitalAccountingLines>();

        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.isSelectLine() && !capitalAccountingLine.isAmountDistributed()) {
                capitalAccountingLine.setDistributionAmountCode(KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR);
                selectedCapitalAccountingLines.add(capitalAccountingLine);
            }
        }

        //redistribute the capital asset modify amount to the group accounting lines
        //based on amount.
        redistributeToGroupAccountingLinesFromAssetsByAmounts(selectedCapitalAccountingLines, capitalAssetInformation.get(addIndex));
        
        */
        
        //now process the remaining capital asset records
        processRemainingCapitalAssetInfo(form, capitalAssetInformation);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * inserts capital asset information into capital assets list.
     * Also recalculates the system control and system control remaining amounts.
     * Puts a global error message if the user does not enter capital asset quantity.
     * If the quantity is > 1, it will insert that many tag/location detail records for this
     * capital asset item.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward string
     * @throws Exception
     */
    public ActionForward addCapitalAssetTagLocationInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("addCapitalAssetTagLocationInfo() - start");

        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);

        if (capitalAssetInformation == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        int addIndex = getSelectedLine(request);        
        if (capitalAssetInformation.get(addIndex).getCapitalAssetQuantity() == null || capitalAssetInformation.get(addIndex).getCapitalAssetQuantity() <= 0) {
            GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_CAPITAL_ASSET_INFORMATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_CAPITAL_ASSET_QUANTITY_REQUIRED);
        }
        else {
            this.addCapitalAssetInfoDetailLines(capitalAssetInformation.get(addIndex));
        }
        
        //now process the remaining capital asset records
        processRemainingCapitalAssetInfo(form, capitalAssetInformation);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * refreshes capital asset modify information to the modify capital assets list.
     * Also recalculates the system control and system control remaining amounts.
     * Puts a global error message if the user does not enter capital asset number.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward string
     * @throws Exception
     */
    public ActionForward refreshCapitalAssetModify(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("refreshCapitalAssetModify() - start");

        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);

        if (capitalAssetInformation == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        int refreshIndex = getSelectedLine(request);  
        
        if (capitalAssetInformation.get(refreshIndex).getCapitalAssetNumber() == null) {
            GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_CAPITAL_ASSET_MODIFY_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_CAPITAL_ASSET_NUMBER_REQUIRED);
        }

        /*
        DistributeCapitalAssetAmountToGroupAccountingLines(kualiAccountingDocumentFormBase);

        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = (CapitalAccountingLinesFormBase) kualiAccountingDocumentFormBase;
        checkSelectForCapitalAccountingLines(capitalAccountingLinesFormBase);        
        
        checkCapitalAccountingLinesSelected(capitalAccountingLinesFormBase);
        calculatePercentsForSelectedCapitalAccountingLines(capitalAccountingLinesFormBase);
        
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) capitalAccountingLinesFormBase.getFinancialDocument();
        String distributionAmountCode = capitalAccountingLinesFormBase.getCapitalAccountingLine().getDistributionCode();

        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();        
        
        List<CapitalAccountingLines> selectedCapitalAccountingLines = new ArrayList<CapitalAccountingLines>();

        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.isSelectLine() && !capitalAccountingLine.isAmountDistributed()) {
                capitalAccountingLine.setDistributionAmountCode(KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR);
                selectedCapitalAccountingLines.add(capitalAccountingLine);
            }
        }

        //redistribute the capital asset modify amount to the group accounting lines
        //based on amount.
        redistributeToGroupAccountingLinesFromAssetsByAmounts(selectedCapitalAccountingLines, capitalAssetInformation.get(refreshIndex));
    */
        
        //now process the remaining capital asset records
        processRemainingCapitalAssetInfo(form, capitalAssetInformation);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * deletes the capital asset information
     */
    public ActionForward deleteCapitalAssetInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("deleteCapitalAssetInfoDetail() - start");

        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);

        if (capitalAssetInformation == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        int lineIndexForCapitalAssetInfo = this.getLineToDelete(request);
        
        if (capitalAssetInformation.get(lineIndexForCapitalAssetInfo).getCapitalAssetInformationDetails() != null &&
                capitalAssetInformation.get(lineIndexForCapitalAssetInfo).getCapitalAssetInformationDetails().size() > 0) {
            capitalAssetInformation.get(lineIndexForCapitalAssetInfo).getCapitalAssetInformationDetails().clear();
            
        }

        if (capitalAssetInformation.get(lineIndexForCapitalAssetInfo).getCapitalAssetAccountsGroupDetails() != null &&
                capitalAssetInformation.get(lineIndexForCapitalAssetInfo).getCapitalAssetAccountsGroupDetails().size() > 0) {
            capitalAssetInformation.get(lineIndexForCapitalAssetInfo).getCapitalAssetAccountsGroupDetails().clear();
        }
        
        capitalAssetInformation.remove(lineIndexForCapitalAssetInfo);
        
        //now process the remaining capital asset records
        processRemainingCapitalAssetInfo(form, capitalAssetInformation);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * deletes the capital asset information
     */
    public ActionForward deleteCapitalAssetModify(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("deleteCapitalAssetModify() - start");

        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);

        if (capitalAssetInformation == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        int lineIndexForCapitalAssetInfo = this.getLineToDelete(request);
        
        capitalAssetInformation.remove(lineIndexForCapitalAssetInfo);
        
        //now process the remaining capital asset records
        processRemainingCapitalAssetInfo(form, capitalAssetInformation);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * process any remaining capital asset info in the list to check and calculate the
     * remaining distributed amount.  Also checks to make sure if "select Lines" is to be
     * checked on/off
     * @param form
     * @param capitalAssetInformation
     */
    protected void processRemainingCapitalAssetInfo(ActionForm form, List<CapitalAssetInformation> capitalAssetInformation) {
        CapitalAccountingLinesFormBase calfb = (CapitalAccountingLinesFormBase) form;
        
        //recalculate the amount remaining to be distributed and save the value on the form
        calculateRemainingDistributedAmount(calfb, capitalAssetInformation);
        
        //set the amountDistributed property to true if the total amount of all the capital assets 
        //for a given capital accounting line is greater or equal to the line amount.
        checkCapitalAccountingLinesSelected(calfb);
        
        //redistribute each capital asset amount to its group accounting lines...
        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        DistributeCapitalAssetAmountToGroupAccountingLines(kualiAccountingDocumentFormBase);
        
        setTabStatesForCapitalAssets(form);
    }
    
    /**
     * delete a detail line from the capital asset information
     */
    public ActionForward deleteCapitalAssetInfoDetailLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("deleteCapitalAssetInfoDetailLine() - start");

        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> capitalAssetInformation =  this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);

        if (capitalAssetInformation == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        int lineIndexForCapitalAssetInfo = this.getLineToDelete(request);
        capitalAssetInformation.get(lineIndexForCapitalAssetInfo).getCapitalAssetInformationDetails().remove(0);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * get the capital asset information object currently associated with the document
     */
    protected List<CapitalAssetInformation> getCurrentCapitalAssetInformationObject(KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase) {
        LOG.debug("getCurrentCapitalAssetInformationObject() - start");

        CapitalAssetInformationDocumentBase capitalAssetInformationDocumentBase = (CapitalAssetInformationDocumentBase) kualiAccountingDocumentFormBase.getFinancialDocument();
        
        if (!(capitalAssetInformationDocumentBase instanceof CapitalAssetEditable)) {
            return null;
        }

        List<CapitalAssetInformation> capitalAssetInformation = capitalAssetInformationDocumentBase.getCapitalAssetInformation();
        return capitalAssetInformation;
    }

    /**
     * reset the nonkey fields of the given capital asset information
     * removes the corresponding capital asset information detail record from the list.
     * @param capitalAssetInformation the given capital asset information
     */
    protected void resetCapitalAssetInfo(CapitalAssetInformation capitalAssetInformation) {
        if (capitalAssetInformation != null) {
            capitalAssetInformation.setCapitalAssetDescription(null);
            capitalAssetInformation.setCapitalAssetManufacturerModelNumber(null);
            capitalAssetInformation.setCapitalAssetManufacturerName(null);

            capitalAssetInformation.setCapitalAssetNumber(null);
            capitalAssetInformation.setCapitalAssetTypeCode(null);
            capitalAssetInformation.setCapitalAssetQuantity(null);

            capitalAssetInformation.setVendorDetailAssignedIdentifier(null);
            capitalAssetInformation.setVendorHeaderGeneratedIdentifier(null);
            // Set the BO to null cause it won't be updated automatically when vendorDetailAssetIdentifier and
            // VendorHeanderGeneratedIndentifier set to null.
            capitalAssetInformation.setVendorDetail(null);
            capitalAssetInformation.setVendorName(null);
            capitalAssetInformation.setCapitalAssetLineAmount(KualiDecimal.ZERO);
            capitalAssetInformation.getCapitalAssetInformationDetails().clear();

            //zero out the amount distribute on the accounting lines...
            for (CapitalAssetAccountsGroupDetails groupAccountLine : capitalAssetInformation.getCapitalAssetAccountsGroupDetails()) {
                groupAccountLine.setAmount(KualiDecimal.ZERO);
            }
        }
    }

    /**
     * Overridden to guarantee that form of copied document is set to whatever the entry mode of the document is
     * @see org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase#copy(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = (CapitalAccountingLinesFormBase) form;;
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) capitalAccountingLinesFormBase.getFinancialDocument();
        
        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();
        List<CapitalAccountingLines> copiedCapitalAccountingLines = new ArrayList<CapitalAccountingLines>();
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            copiedCapitalAccountingLines.add(capitalAccountingLine);
        }
        capitalAccountingLines.clear();    
        
        ActionForward forward = super.copy(mapping, form, request, response);
        
        caldb.setCapitalAccountingLines(copiedCapitalAccountingLines);
        
        // if the copied document has capital asset collection, remove the collection
        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        AccountingDocument document = kualiAccountingDocumentFormBase.getFinancialDocument();
        if (document instanceof CapitalAssetEditable) {
            CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) document;
            
            List<CapitalAssetInformation> capitalAssets = capitalAssetEditable.getCapitalAssetInformation();
            for (CapitalAssetInformation capitalAsset : capitalAssets) {
                Long capitalAssetNumber = capitalAsset.getCapitalAssetNumber();
                resetCapitalAssetInfo(capitalAsset);
                
                //set capital asset number to copied asset line if "modify" asset
                //because resetCapitalAssetInfo cleared the value.
                if (KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR.equalsIgnoreCase(capitalAsset.getCapitalAssetActionIndicator())) {
                    capitalAsset.setCapitalAssetNumber(capitalAssetNumber);
                }
                capitalAsset.setCapitalAssetProcessedIndicator(false);
            }
        }
        
        //setup the initial next sequence number column..
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        setupIntialNextCapitalAssetLineNumber(kualiDocumentFormBase);

        checkCapitalAccountingLinesSelected(capitalAccountingLinesFormBase);
        
        return forward;
    }

    /**
     * setups the next capital asset line number
     * 
     * @param kualiDocumentFormBase
     */
    protected void setupIntialNextCapitalAssetLineNumber(KualiDocumentFormBase kualiDocumentFormBase) {
        KualiAccountingDocumentFormBase kadfb = (KualiAccountingDocumentFormBase) kualiDocumentFormBase;
        CapitalAssetInformationDocumentBase caidb = (CapitalAssetInformationDocumentBase) kadfb.getFinancialDocument();
        
        List<CapitalAssetInformation> currentCapitalAssetInformation =  this.getCurrentCapitalAssetInformationObject(kadfb);
        for (CapitalAssetInformation capitalAsset : currentCapitalAssetInformation) {
            if (capitalAsset.getCapitalAssetLineNumber() > caidb.getNextCapitalAssetLineNumber()) {
                caidb.setNextCapitalAssetLineNumber(capitalAsset.getCapitalAssetLineNumber());
            }
        }
        
        caidb.setNextCapitalAssetLineNumber(caidb.getNextCapitalAssetLineNumber()+1);
    }
    
    /**
     * calculates the percents for the selected capital accounting lines only
     * 
     * @param calfb
     */
    protected void calculatePercentsForSelectedCapitalAccountingLines(CapitalAccountingLinesFormBase calfb) {
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) calfb.getFinancialDocument();
        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();        

        KualiDecimal totalCapitalLinesSelectedAmount = (calculateTotalCapitalLinesSelectedAmount(capitalAccountingLines)).abs();
        if (totalCapitalLinesSelectedAmount.isNonZero()) {
            for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
                if (capitalAccountingLine.isSelectLine() && !capitalAccountingLine.isAmountDistributed()) {
                    capitalAccountingLine.setAccountLinePercent(capitalAccountingLine.getAmount().divide(totalCapitalLinesSelectedAmount).multiply(new KualiDecimal(100), true));
                }
            }
        }
    }
    
    /**
     * 
     * @param capitalAccountingLines
     * @return
     */
    protected KualiDecimal calculateTotalCapitalLinesSelectedAmount(List<CapitalAccountingLines> capitalAccountingLines) {
        KualiDecimal totalLineAmount = KualiDecimal.ZERO;
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.isSelectLine() && !capitalAccountingLine.isAmountDistributed()) {
                totalLineAmount = totalLineAmount.add(capitalAccountingLine.getAmount());
            }
        }

        return totalLineAmount;
    }

    /**
     * Populates capital asset information collection with capital accounting lines.
     * Based on actionType, capitalassetactionindicator attribute is filled with 'C' for create
     * and 'M' for modify assets, which will be used to differentiate to pull the records in
     * create asset screen or modify asset screen. 
     * 
     * @param calfb
     * @param actionType
     * @param distributionAmountCode
     */
    protected void createCapitalAssetForGroupAccountingLines(CapitalAccountingLinesFormBase calfb, String actionType, String distributionAmountCode) {
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) calfb.getFinancialDocument();

        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();        
        List<CapitalAssetInformation> capitalAssetInformation = new ArrayList<CapitalAssetInformation>();
        
        KualiAccountingDocumentFormBase kadfb = (KualiAccountingDocumentFormBase) calfb;

        List<CapitalAssetInformation> currentCapitalAssetInformation =  this.getCurrentCapitalAssetInformationObject(kadfb);
        
        String documentNumber = calfb.getDocument().getDocumentNumber();
        calfb.setSystemControlAmount(KualiDecimal.ZERO);
        
        List<CapitalAccountingLines> selectedCapitalAccountingLines = new ArrayList<CapitalAccountingLines>();

        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.isSelectLine() && !capitalAccountingLine.isAmountDistributed()) {
                capitalAccountingLine.setDistributionAmountCode(distributionAmountCode);
                selectedCapitalAccountingLines.add(capitalAccountingLine);
            }
        }
        
        CapitalAssetInformation existingCapitalAsset = capitalAssetCreated(selectedCapitalAccountingLines, currentCapitalAssetInformation);
        if (ObjectUtils.isNotNull(existingCapitalAsset)) {
            if (!accountingLinesAmountDistributed(selectedCapitalAccountingLines, existingCapitalAsset)) {
                //accounting line amount not completely distributed yet so we need to create more assets
                //add the capital information record to the list of asset information
                createNewCapitalAsset(selectedCapitalAccountingLines, currentCapitalAssetInformation, documentNumber, actionType, getNextCapitalAssetLineNumber(kadfb));
            }
        }
        else {
            //add the capital information record to the list of asset information
            createNewCapitalAsset(selectedCapitalAccountingLines, currentCapitalAssetInformation, documentNumber, actionType, getNextCapitalAssetLineNumber(kadfb));                    
        }
    }

    /**
     * helper method to add accounting details for this new capital asset record 
     * 
     * @param capitalAccountingLines
     * @param currentCapitalAssetInformation
     * @param documentNumber
     * @param actionType
     * @param nextCapitalAssetLineNumnber
     */
    protected void createNewCapitalAsset(List<CapitalAccountingLines> capitalAccountingLines, List<CapitalAssetInformation> currentCapitalAssetInformation, String documentNumber, String actionType, Integer nextCapitalAssetLineNumber) {
        CapitalAssetInformation capitalAsset = new CapitalAssetInformation();
        capitalAsset.setCapitalAssetLineAmount(KualiDecimal.ZERO);
        capitalAsset.setDocumentNumber(documentNumber);
        capitalAsset.setCapitalAssetLineNumber(nextCapitalAssetLineNumber);
        capitalAsset.setCapitalAssetActionIndicator(actionType);
        capitalAsset.setCapitalAssetProcessedIndicator(false);                    
        
        //now setup the account line information associated with this capital asset
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            capitalAsset.setDistributionAmountCode(capitalAccountingLine.getDistributionAmountCode());
            createCapitalAssetAccountingLinesDetails(capitalAccountingLine, capitalAsset);
        }
        
        currentCapitalAssetInformation.add(capitalAsset);
    }

    /**
     * helper method to add accounting details for this new modify capital asset record 
     * 
     * @param capitalAccountingLines
     * @param currentCapitalAssetInformation
     * @param documentNumber
     * @param actionType
     * @param nextCapitalAssetLineNumnber
     * @param capitalAssetNumber
     */
    protected void createNewModifyCapitalAsset(List<CapitalAccountingLines> capitalAccountingLines, List<CapitalAssetInformation> currentCapitalAssetInformation, String documentNumber, String actionType, Integer nextCapitalAssetLineNumber, long capitalAssetNumber) {
        CapitalAssetInformation capitalAsset = new CapitalAssetInformation();
        capitalAsset.setCapitalAssetNumber(capitalAssetNumber);
        capitalAsset.setCapitalAssetLineAmount(KualiDecimal.ZERO);
        capitalAsset.setDocumentNumber(documentNumber);
        capitalAsset.setCapitalAssetLineNumber(nextCapitalAssetLineNumber);
        capitalAsset.setCapitalAssetActionIndicator(actionType);
        capitalAsset.setCapitalAssetProcessedIndicator(false);                    
        
        //now setup the account line information associated with this capital asset
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            capitalAsset.setDistributionAmountCode(capitalAccountingLine.getDistributionAmountCode());
            createCapitalAssetAccountingLinesDetails(capitalAccountingLine, capitalAsset);
        }
        
        currentCapitalAssetInformation.add(capitalAsset);
    }
    
    /**
     * 
     * 
     * @param capitalAccountingLine
     * @param capitalAsset
     */
    protected void createCapitalAssetAccountingLinesDetails(CapitalAccountingLines capitalAccountingLine, CapitalAssetInformation capitalAsset) {
        //now setup the account line information associated with this capital asset
        CapitalAssetAccountsGroupDetails capitalAssetAccountLine = new CapitalAssetAccountsGroupDetails();
        capitalAssetAccountLine.setDocumentNumber(capitalAsset.getDocumentNumber());
        capitalAssetAccountLine.setChartOfAccountsCode(capitalAccountingLine.getChartOfAccountsCode());
        capitalAssetAccountLine.setAccountNumber(capitalAccountingLine.getAccountNumber());
        capitalAssetAccountLine.setFinancialDocumentLineTypeCode(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE);
        capitalAssetAccountLine.setCapitalAssetAccountLineNumber(getNextAccountingLineNumber(capitalAccountingLine, capitalAsset));
        capitalAssetAccountLine.setCapitalAssetLineNumber(capitalAsset.getCapitalAssetLineNumber());
        capitalAssetAccountLine.setFinancialObjectCode(capitalAccountingLine.getFinancialObjectCode());
        capitalAssetAccountLine.setSequenceNumber(capitalAccountingLine.getSequenceNumber());
        capitalAssetAccountLine.setAmount(KualiDecimal.ZERO);
        capitalAsset.getCapitalAssetAccountsGroupDetails().add(capitalAssetAccountLine);
    }
    
    /**
     * calculates the next accounting line number for accounts details for each capital asset.
     * Goes through the current records and gets the last accounting line number.
     * 
     * @param capitalAsset
     * @return nextAccountingLineNumber
     */
    protected Integer getNextAccountingLineNumber(CapitalAccountingLines capitalAccountingLine, CapitalAssetInformation capitalAsset) {
        Integer nextAccountingLineNumber = 0;
        List<CapitalAssetAccountsGroupDetails> capitalAssetAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
        for (CapitalAssetAccountsGroupDetails capitalAssetAccountLine : capitalAssetAccountLines) {
            nextAccountingLineNumber = capitalAssetAccountLine.getCapitalAssetAccountLineNumber();
        }
        
        return ++nextAccountingLineNumber;
    }
    
    /**
     * @param capitalAccountingLine
     * @param capitalAssetInformation
     * @return return existingCapitalAsset
     */
    protected CapitalAssetInformation getCapitalAssetCreated(CapitalAccountingLines capitalAccountingLine, List<CapitalAssetInformation> capitalAssetInformation) {
        CapitalAssetInformation existingCapitalAsset = null;
        if (ObjectUtils.isNull(capitalAssetInformation) ||capitalAssetInformation.size() <= 0) {
            return existingCapitalAsset;
        }
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
            for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
                if (groupAccountLine.getCapitalAssetLineNumber().compareTo(capitalAsset.getCapitalAssetLineNumber()) == 0 &&
                        groupAccountLine.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&
                        groupAccountLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                        groupAccountLine.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
                        groupAccountLine.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
                        groupAccountLine.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
                     return capitalAsset;
                }
            }
        }
       
        return existingCapitalAsset;
    }
    
    /**
     * 
     * @param capitalAccountingLine
     * @param capitalAssetInformation
     * @return capitalAsset
     */
    protected CapitalAssetInformation capitalAssetCreated(List<CapitalAccountingLines> capitalAccountingLines, List<CapitalAssetInformation> capitalAssetInformation) {
        CapitalAssetInformation existingCapitalAsset = null;
        
        if (ObjectUtils.isNull(capitalAssetInformation) && capitalAssetInformation.size() <= 0) {
            return existingCapitalAsset;
        }
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            existingCapitalAsset = getCapitalAssetCreated(capitalAccountingLine, capitalAssetInformation);
            if (ObjectUtils.isNotNull(existingCapitalAsset)) {
                return existingCapitalAsset;
            }
        }
        
        return existingCapitalAsset;
    }
    
    /**
     * 
     * @param capitalAccountingLine
     * @param capitalAssetInformation
     * @return modify capital asset
     */
    protected CapitalAssetInformation modifyCapitalAssetCreated(CapitalAccountingLines capitalAccountingLine, List<CapitalAssetInformation> capitalAssetInformation) {
        CapitalAssetInformation existingCapitalAsset = null;
        
        if (ObjectUtils.isNull(capitalAssetInformation) && capitalAssetInformation.size() <= 0) {
            return existingCapitalAsset;
        }
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            if (KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR.equals(capitalAsset.getCapitalAssetActionIndicator()) &&
                    ObjectUtils.isNull(capitalAsset.getCapitalAssetNumber())) {
                List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
                for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
                    if (groupAccountLine.getCapitalAssetLineNumber().compareTo(capitalAsset.getCapitalAssetLineNumber()) == 0 &&
                            groupAccountLine.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&                        
                            groupAccountLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                            groupAccountLine.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
                            groupAccountLine.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
                            groupAccountLine.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
                        return capitalAsset;
                    }
                }
            }
        }
        
        return existingCapitalAsset;
    }

    
    /**
     * 
     * @param capitalAssetInformation
     * @param existingCapitalAssetInformation
     * @return
     */
    protected Integer getNextCapitalAssetLineNumber(KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase) {
        int nextCapitalAssetLineNumber = 1;
        CapitalAssetInformationDocumentBase caidb = (CapitalAssetInformationDocumentBase) kualiAccountingDocumentFormBase.getFinancialDocument();
        nextCapitalAssetLineNumber = caidb.getNextCapitalAssetLineNumber();
        caidb.setNextCapitalAssetLineNumber(nextCapitalAssetLineNumber+1);
        
        return nextCapitalAssetLineNumber;
    }
    
    /**
     * 
     * @param kadfb
     * @param existingCapitalAsset
     * @return true if accounting line amount equals to capital asset amount, else false.
     */
    protected boolean accountingLinesAmountDistributed(List<CapitalAccountingLines> capitalAccountingLines, CapitalAssetInformation existingCapitalAsset) {

        KualiDecimal accountingLineAmount = KualiDecimal.ZERO;
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            accountingLineAmount = accountingLineAmount.add(capitalAccountingLine.getAmount().abs());
        }
        
        KualiDecimal capitalAssetsAmount = existingCapitalAsset.getCapitalAssetLineAmount();
        
        return accountingLineAmount.equals(capitalAssetsAmount);
    }
    
    /**
     * 
     * @param kadfb
     * @param capitalAssetsInformation
     * @return true if accounting line amount equals to capital asset amount, else false.
     */
    protected boolean capitalAccountingLineAmountDistributed(CapitalAccountingLines capitalAccountingLine, List<CapitalAssetInformation> capitalAssetsInformation) {        
        KualiDecimal amountDistributed = KualiDecimal.ZERO;        
        for (CapitalAssetInformation capitalAsset : capitalAssetsInformation) {
            amountDistributed = amountDistributed.add(getAccountingLineAmount(capitalAsset, capitalAccountingLine));
        }

        KualiDecimal capitalAccountingLineAmount = capitalAccountingLine.getAmount();

        return amountDistributed.equals(capitalAccountingLineAmount);
    }
    
    /**
     * 
     * 
     * @param capitalAsset
     * @param capitalAccountingLine
     * @return accountLineAmount
     */
    protected KualiDecimal getAccountingLineAmount(CapitalAssetInformation capitalAsset, CapitalAccountingLines capitalAccountingLine) {
        KualiDecimal accountLineAmount = KualiDecimal.ZERO;
        
        List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
        
        for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
            if (groupAccountLine.getCapitalAssetLineNumber().compareTo(capitalAsset.getCapitalAssetLineNumber()) == 0 &&
                    groupAccountLine.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&
                    groupAccountLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                    groupAccountLine.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
                    groupAccountLine.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
                    groupAccountLine.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
                 return groupAccountLine.getAmount();
            }
        }
        
        return accountLineAmount;
    }
    
    /**
     * adds any missing capital accounting line details as an accounting line into the collection of
     * accounting lines for this capital asset based on the action type.
     * 
     * @param capitalAccountingLines
     * @param existingCapitalAsset
     */
    protected void addMissingAccountingLinesToCapitalAsset(List<CapitalAccountingLines> capitalAccountingLines, CapitalAssetInformation existingCapitalAsset) {
        List<CapitalAssetAccountsGroupDetails> groupAccountLines = existingCapitalAsset.getCapitalAssetAccountsGroupDetails();
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLineMissing(capitalAccountingLine, groupAccountLines, existingCapitalAsset.getCapitalAssetLineNumber())) {
                createCapitalAssetAccountingLinesDetails(capitalAccountingLine, existingCapitalAsset);                
            }
        }
    }
    
    /**
     * creates a new tag/location details record and adds to the collection for capital asset
     * @param capitalAsset
     */
    protected void createCapitalAssetInformationDetail(CapitalAssetInformation capitalAsset) {
        CapitalAssetInformationDetail assetDetail = new CapitalAssetInformationDetail();
        assetDetail.setDocumentNumber(capitalAsset.getDocumentNumber());
        assetDetail.setCapitalAssetLineNumber(capitalAsset.getCapitalAssetLineNumber());
        assetDetail.setItemLineNumber(getNextLineItemNumber(capitalAsset));
        capitalAsset.getCapitalAssetInformationDetails().add(0,assetDetail);
    }
    
    /**
     * calculates the next line item number for tag/location details for each capital asset.
     * Goes through the current records and gets the last number.
     * 
     * @param capitalAsset
     * @return nextLineNumber
     */
    protected Integer getNextLineItemNumber(CapitalAssetInformation capitalAsset) {
        Integer nextLineNumber = 0;
        List<CapitalAssetInformationDetail> capitalAssetDetails = capitalAsset.getCapitalAssetInformationDetails();
        for (CapitalAssetInformationDetail capitalAssetDetail : capitalAssetDetails) {
            nextLineNumber = capitalAssetDetail.getItemLineNumber();
        }
        
        return ++nextLineNumber;
    }
    
    protected void calculateRemainingDistributedAmount(CapitalAccountingLinesFormBase calfb, List<CapitalAssetInformation> capitalAssetInformation) {
        calfb.setCreatedAssetsControlAmount(calfb.getSystemControlAmount());
            
        //get amount allocated so far....or the system control remainder amount field.
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            calfb.setCreatedAssetsControlAmount(calfb.getCreatedAssetsControlAmount().subtract(capitalAsset.getCapitalAssetLineAmount()));
        }
    }
    
    /**
     * checks the current list of accounting lines created for the capital asset against the given
     * capital accounting line and returns true or false
     * 
     * @param capitalAccountingLine
     * @param groupAccountLines
     * @param capitalAssetLineNumber
     * @return true if line exists else return false
     */
    protected boolean capitalAccountingLineMissing(CapitalAccountingLines capitalAccountingLine, List<CapitalAssetAccountsGroupDetails> groupAccountLines, Integer capitalAssetLineNumber) {
        boolean missing = true;
        
        for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
            if (groupAccountLine.getCapitalAssetLineNumber().compareTo(capitalAssetLineNumber) == 0 &&
                    groupAccountLine.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&
                    groupAccountLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                    groupAccountLine.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
                    groupAccountLine.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
                    groupAccountLine.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
                 return false;
            }
        }
        
        return missing;
    }
    
    /**
     * sets the capital accounting lines select and amount distributed values to true if
     * there are capital asset records for a given capital accounting line. The system control
     * amount and system control remaining amounts are calculated here.
     * 
     * @param calfb
     */
    protected void checkCapitalAccountingLinesSelected(CapitalAccountingLinesFormBase calfb) {
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) calfb.getFinancialDocument();

        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();        

        KualiAccountingDocumentFormBase kadfb = (KualiAccountingDocumentFormBase) calfb;

        List<CapitalAssetInformation> currentCapitalAssetInformation =  this.getCurrentCapitalAssetInformationObject(kadfb);
        
        calfb.setCreatedAssetsControlAmount(KualiDecimal.ZERO);
        calfb.setSystemControlAmount(KualiDecimal.ZERO);

        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.isSelectLine()) {
                calfb.setSystemControlAmount(calfb.getSystemControlAmount().add(capitalAccountingLine.getAmount()));                
            }

            if (currentCapitalAssetInformation.size() <= 0) {
                capitalAccountingLine.setAccountLinePercent(null);
                capitalAccountingLine.setAmountDistributed(false);
                capitalAccountingLine.setSelectLine(false);
            } else {
                   CapitalAssetInformation existingCapitalAsset = getCapitalAssetCreated(capitalAccountingLine, currentCapitalAssetInformation);
                   if (ObjectUtils.isNotNull(existingCapitalAsset)) {
                       capitalAccountingLine.setSelectLine(true);
                   } else {
                       capitalAccountingLine.setAccountLinePercent(null);
                       capitalAccountingLine.setSelectLine(false);
                   }
            }
            
            if (capitalAccountingLineAmountDistributed(capitalAccountingLine, currentCapitalAssetInformation)) {
                capitalAccountingLine.setAmountDistributed(true);
            } else {
                capitalAccountingLine.setAmountDistributed(false);
            }
        }
        
        KualiDecimal capitalAssetsTotal = KualiDecimal.ZERO;
        
        //get amount allocated so far....or the system control remainder amount field.
        for (CapitalAssetInformation capitalAsset : currentCapitalAssetInformation) {
            capitalAssetsTotal= capitalAssetsTotal.add(capitalAsset.getCapitalAssetLineAmount());
        }
        
        calfb.setCreatedAssetsControlAmount(calfb.getSystemControlAmount().subtract(capitalAssetsTotal));        
    }

    /**
     * sets the capital assets screens for create and modify and accounting lines
     * for capitalization screen as open. If accounting lines for capitalizataion list is
     * not empty then set "Accounting Lines for Capitalization" tab to open else set to close.
     * If capital asset with capital asset action indicator = 'C' then set "Create Capital Asset"
     * tab to open else set to close
     * If capital asset with capital asset action indicator = 'M' then set "Modify Capital Asset"
     * tab to open else set to close
     * 
     * @param form
     */
    protected void setTabStatesForCapitalAssets(ActionForm form) {
        KualiForm kualiForm = (KualiForm) form;

        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = (CapitalAccountingLinesFormBase) form;
        
        
        Map<String, String> tabStates = kualiForm.getTabStates();
        Map<String, String> newTabStates = new HashMap<String, String>();
        
        CapitalAssetInformationFormBase capitalAssetInformationFormBase = (CapitalAssetInformationFormBase) form;
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) capitalAssetInformationFormBase.getFinancialDocument();

        //generated tab key for the three tabs
        String tabIdForAccountingLinesForCapitalization = WebUtils.generateTabKey(KFSConstants.CapitalAssets.ACCOUNTING_LINES_FOR_CAPITALIZATION_TAB_TITLE);
        String tabIdForCreateCapitalAsset = WebUtils.generateTabKey(KFSConstants.CapitalAssets.CREATE_CAPITAL_ASSETS_TAB_TITLE);
        String tabIdForModifyCapitalAsset = WebUtils.generateTabKey(KFSConstants.CapitalAssets.MODIFY_CAPITAL_ASSETS_TAB_TITLE);
        
        //if there are any capital accounting lines for capitalization exists then
        if (caldb.getCapitalAccountingLines().size() > 0) {
            newTabStates.put(tabIdForAccountingLinesForCapitalization, KFSConstants.CapitalAssets.CAPITAL_ASSET_TAB_STATE_OPEN);
        }
        else {
            newTabStates.put(tabIdForAccountingLinesForCapitalization, KFSConstants.CapitalAssets.CAPITAL_ASSET_TAB_STATE_CLOSE);
        }

        if (checkCreateAssetsExist(capitalAccountingLinesFormBase)) {
            newTabStates.put(tabIdForCreateCapitalAsset, KFSConstants.CapitalAssets.CAPITAL_ASSET_TAB_STATE_OPEN);  
        }
        else {
            newTabStates.put(tabIdForCreateCapitalAsset, KFSConstants.CapitalAssets.CAPITAL_ASSET_TAB_STATE_CLOSE);  
        }
        
        if (checkModifyAssetsExist(capitalAccountingLinesFormBase)) {
            newTabStates.put(tabIdForModifyCapitalAsset, KFSConstants.CapitalAssets.CAPITAL_ASSET_TAB_STATE_OPEN);  
        }
        else {
            newTabStates.put(tabIdForModifyCapitalAsset, KFSConstants.CapitalAssets.CAPITAL_ASSET_TAB_STATE_CLOSE);  
        }
        
        kualiForm.setTabStates(newTabStates);
    }
    
    /**
     * 
     * @param capitalAccountingLinesFormBase
     * @return true if a capital asset with capital asset action indicator = 'C' else false;
     */
    protected boolean checkCreateAssetsExist(CapitalAccountingLinesFormBase capitalAccountingLinesFormBase) {
        boolean exists = false;
        
        CapitalAssetInformationDocumentBase capitalAssetInformationDocumentBase = (CapitalAssetInformationDocumentBase) capitalAccountingLinesFormBase.getFinancialDocument();
        
        List<CapitalAssetInformation> capitalAssets = capitalAssetInformationDocumentBase.getCapitalAssetInformation();

        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            if (KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR.equals(capitalAsset.getCapitalAssetActionIndicator())) {
                return true;
            }
        }
        
        return exists;
    }
    
    /**
     * 
     * @param capitalAccountingLinesFormBase
     * @return true if a capital asset with capital asset action indicator = 'C' else false;
     */
    protected boolean checkModifyAssetsExist(CapitalAccountingLinesFormBase capitalAccountingLinesFormBase) {
        boolean exists = false;
        
        CapitalAssetInformationDocumentBase capitalAssetInformationDocumentBase = (CapitalAssetInformationDocumentBase) capitalAccountingLinesFormBase.getFinancialDocument();
        
        List<CapitalAssetInformation> capitalAssets = capitalAssetInformationDocumentBase.getCapitalAssetInformation();
        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            if (KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR.equals(capitalAsset.getCapitalAssetActionIndicator())) {
                return true;
            }
        }
        
        return exists;
    }
    
    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return actionForward
     * @throws Exception
     */
    public ActionForward redistributeCreateCapitalAssetAmount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("redistributeCreateCapitalAssetAmount() - start");
        
        KualiDecimal remainingAmountToDistribute = KualiDecimal.ZERO;
        
        CapitalAccountingLinesFormBase calfb = (CapitalAccountingLinesFormBase) form;        
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) calfb.getFinancialDocument();        
        List<CapitalAccountingLines> selectedCapitalAccountingLines = new ArrayList<CapitalAccountingLines>();
        String distributionCode = calfb.getCapitalAccountingLine().getDistributionCode();
        
        remainingAmountToDistribute = getRemainingAmounToDistribute(selectedCapitalAccountingLines, form);
        
        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);
        
        //run the process to redistribute the accounting line amount to the capital assets.
        redistributeEqualAmountsForAccountingLineForCreateAssets(selectedCapitalAccountingLines, capitalAssetInformation, remainingAmountToDistribute);
            
        redistributeIndividualAmountsForAccountingLinesForCreateAssets(selectedCapitalAccountingLines, capitalAssetInformation);
        
        //adjust any variance from capital accounting lines to the distributed accounting lines amounts....
        adjustCapitalAssetsAccountingLinesAmounts(selectedCapitalAccountingLines, capitalAssetInformation);
        
        //now perform calculations of adjusting variance when asset amounts are odd
    /////    adjustVarianceOnCapitalAssets(capitalAssetInformation);
        
        checkCapitalAccountingLinesSelected(calfb);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward redistributeModifyCapitalAssetAmount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("redistributeModifyCapitalAssetAmount() - start");
        
        KualiDecimal remainingAmountToDistribute = KualiDecimal.ZERO;
        CapitalAccountingLinesFormBase calfb = (CapitalAccountingLinesFormBase) form;        
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) calfb.getFinancialDocument();        
        List<CapitalAccountingLines> selectedCapitalAccountingLines = new ArrayList<CapitalAccountingLines>();
        String distributionCode = calfb.getCapitalAccountingLine().getDistributionCode();
        
        remainingAmountToDistribute = getRemainingAmounToDistribute(selectedCapitalAccountingLines, form);
        
        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);
        
        //run the process to redistribute the accounting line amount to the capital assets.
        redistributeAmountsForAccountingsLineForModifyAssets(selectedCapitalAccountingLines, capitalAssetInformation, remainingAmountToDistribute);
            
        redistributeIndividualAmountsForAccountingLinesForModifyAssets(selectedCapitalAccountingLines, capitalAssetInformation);
        
        //now process any capital assets that has distribution set to "by amount"
        redistributeAmountsForAccountingsLineForModifyAssetsByAmounts(selectedCapitalAccountingLines, capitalAssetInformation, remainingAmountToDistribute);
        
        //adjust any variance from capital accounting lines to the distributed accounting lines amounts....
        adjustCapitalAssetsAccountingLinesAmounts(selectedCapitalAccountingLines, capitalAssetInformation);
        
        checkCapitalAccountingLinesSelected(calfb);
        
        //redistribute capital asset amount to its group accounting lines on refresh
        DistributeCapitalAssetAmountToGroupAccountingLines((KualiAccountingDocumentFormBase) form);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Calculates the remaining amount to distribute by taking selecte capital accounting lines
     * and subtracting the allocated capital asset accounting lines amounts totals.
     * 
     * @param selectedCapitalAccountingLines
     * @param form
     * @return remainingAmountToDistribute
     */
    protected KualiDecimal getRemainingAmounToDistribute(List<CapitalAccountingLines> selectedCapitalAccountingLines, ActionForm form) {
        KualiDecimal remainingAmountToDistribute = KualiDecimal.ZERO;
        KualiDecimal capitalAccountsAmountToDistribute = KualiDecimal.ZERO;
        KualiDecimal capitalAssetsAllocatedAmount = KualiDecimal.ZERO;
        
        CapitalAccountingLinesFormBase calfb = (CapitalAccountingLinesFormBase) form;

        CapitalAssetInformationDocumentBase capitalAssetInformationDocumentBase = (CapitalAssetInformationDocumentBase) calfb.getFinancialDocument();
        List<CapitalAssetInformation> capitalAssets = capitalAssetInformationDocumentBase.getCapitalAssetInformation();
        
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) calfb.getFinancialDocument();
        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();

        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.isSelectLine()  && !capitalAccountingLine.isAmountDistributed()) {
                selectedCapitalAccountingLines.add(capitalAccountingLine);
                capitalAccountsAmountToDistribute = capitalAccountsAmountToDistribute.add(capitalAccountingLine.getAmount());
                capitalAssetsAllocatedAmount = capitalAssetsAllocatedAmount.add(getCapitalAssetsAmountAllocated(capitalAssets, capitalAccountingLine));                
            }
        }

        remainingAmountToDistribute = capitalAccountsAmountToDistribute.subtract(capitalAssetsAllocatedAmount);
        
        return remainingAmountToDistribute;
    }
    
    /**
     * Removes any matching accounting line in capital asset records 
     * whenever an accounting line is removed.
     * @param financialDocumentForm
     * @param line
     */
    protected void deleteCapitalAssetLines(KualiAccountingDocumentFormBase financialDocumentForm, AccountingLine accountingLine) {
        CapitalAssetInformationDocumentBase capitalAssetInformationDocumentBase = (CapitalAssetInformationDocumentBase) financialDocumentForm.getFinancialDocument();

        List<CapitalAssetInformation> removalCaiList = new ArrayList<CapitalAssetInformation>();
        
        List<CapitalAssetInformation> capitalAssets = capitalAssetInformationDocumentBase.getCapitalAssetInformation();
        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            removeDistributedAccountingLine(capitalAsset, accountingLine);
            if (capitalAsset.getCapitalAssetAccountsGroupDetails().size() == 0) {
                capitalAsset.getCapitalAssetInformationDetails().clear();
                removalCaiList.add(capitalAsset);
            }
        }
        
        //if the removal list is not empty, remove these bunch of capital asset records 
        //for that accounting line.
        if (ObjectUtils.isNotNull(removalCaiList)) {
            capitalAssets.removeAll(removalCaiList);
        }
    }
    
    /**
     * Removes the matching accounting line that has been distributed to the capital asset
     * 
     * @param capitalAsset
     * @param accountingLine
     */
    protected void removeDistributedAccountingLine(CapitalAssetInformation capitalAsset, AccountingLine accountingLine) {
        List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
        CapitalAssetAccountsGroupDetails accountLineToDelete = null;
        
        for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
            if (groupAccountLine.getSequenceNumber().compareTo(accountingLine.getSequenceNumber()) == 0 &&                        
                    groupAccountLine.getFinancialDocumentLineTypeCode().equals(groupAccountLine.getFinancialDocumentLineTypeCode()) && 
                    groupAccountLine.getChartOfAccountsCode().equals(accountingLine.getChartOfAccountsCode()) && 
                    groupAccountLine.getAccountNumber().equals(accountingLine.getAccountNumber()) && 
                    groupAccountLine.getFinancialObjectCode().equals(accountingLine.getFinancialObjectCode())) {
                accountLineToDelete = groupAccountLine;
                break;
            }
        }

        if (ObjectUtils.isNotNull(accountLineToDelete)) {
            capitalAsset.setCapitalAssetLineAmount(capitalAsset.getCapitalAssetLineAmount().subtract(accountLineToDelete.getAmount()));
            groupAccountLines.remove(accountLineToDelete);
        }
    }
    
    /**
     * add detail lines into the given capital asset information
     * 
     * @param capitalAssetInformation the given capital asset information
     */
    protected void addCapitalAssetInfoDetailLines(CapitalAssetInformation capitalAssetInformation) {
        LOG.debug("addCapitalAssetInfoDetailLines() - start");

        if (ObjectUtils.isNull(capitalAssetInformation)) {
            return;
        }

        Integer quantity = capitalAssetInformation.getCapitalAssetQuantity();
        if (quantity == null || quantity <= 0) {
            String errorPath = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION;
            GlobalVariables.getMessageMap().putError(errorPath, KFSKeyConstants.ERROR_INVALID_CAPITAL_ASSET_QUANTITY);
            return;
        }

        List<CapitalAssetInformationDetail> detailLines = capitalAssetInformation.getCapitalAssetInformationDetails();
        Integer nextItemLineNumber = 0;

        if (ObjectUtils.isNotNull(detailLines) || detailLines.size() > 0) {
            for (CapitalAssetInformationDetail detailLine : detailLines) {
                nextItemLineNumber  = detailLine.getItemLineNumber().intValue();
            }
        }
        
        // If details collection has old lines, this loop will add new lines to make the total equal to the quantity.
        for (int index = 1; detailLines.size() < quantity; index++) {
            CapitalAssetInformationDetail detailLine = new CapitalAssetInformationDetail();
            detailLine.setDocumentNumber(capitalAssetInformation.getDocumentNumber());
            detailLine.setCapitalAssetLineNumber(capitalAssetInformation.getCapitalAssetLineNumber());
            detailLine.setItemLineNumber(++nextItemLineNumber);
            detailLines.add(detailLine);
        }
    }
    
    /**
     * unchecks the capital accounting lines select when there are no capital assets created yet.
     * 
     * @param calfb
     */
    protected void uncheckCapitalAccountingLinesSelected(CapitalAccountingLinesFormBase calfb) {
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) calfb.getDocument();
        
        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();        
        KualiAccountingDocumentFormBase kadfb = (KualiAccountingDocumentFormBase) calfb;

        List<CapitalAssetInformation> currentCapitalAssetInformation =  this.getCurrentCapitalAssetInformationObject(kadfb);
        
        
        List<CapitalAccountingLines> selectedCapitalAccountingLines = new ArrayList<CapitalAccountingLines>();

        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.isSelectLine() && !capitalAccountingLine.isAmountDistributed()) {
                calfb.setSystemControlAmount(calfb.getSystemControlAmount().add(capitalAccountingLine.getAmount()));
                selectedCapitalAccountingLines.add(capitalAccountingLine);
            }
        }
        
        CapitalAssetInformation existingCapitalAsset = capitalAssetCreated(selectedCapitalAccountingLines, currentCapitalAssetInformation);
        
        for (CapitalAccountingLines capitalAccountingLine : selectedCapitalAccountingLines) {
            if (ObjectUtils.isNull(existingCapitalAsset)) {
                capitalAccountingLine.setSelectLine(false);
            }
        }
    }
    
    /**
     * checks "select" check box on capital accounting lines if there are 
     * corresponding capital asset records.
     * 
     * @param calfb
     */
    protected void checkSelectForCapitalAccountingLines(CapitalAccountingLinesFormBase calfb) {
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) calfb.getFinancialDocument();
        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();        
        KualiAccountingDocumentFormBase kadfb = (KualiAccountingDocumentFormBase) calfb;
        List<CapitalAssetInformation> currentCapitalAssetInformation =  this.getCurrentCapitalAssetInformationObject(kadfb);
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (currentCapitalAssetInformation.size() <= 0) {
                capitalAccountingLine.setSelectLine(false);
            } else {
                   CapitalAssetInformation existingCapitalAsset = getCapitalAssetCreated(capitalAccountingLine, currentCapitalAssetInformation);
                   if (ObjectUtils.isNotNull(existingCapitalAsset)) {
                       capitalAccountingLine.setSelectLine(true);
                   } else {
                       capitalAccountingLine.setSelectLine(false);
                   }
            }
        }
    }
    
    /**
     * gets the total of all accounting lines from that capital asset.
     * 
     * @param capitalAssetInformation
     * @return accountingLinesTotalAmount
     */
    protected KualiDecimal getAccountingLinesTotalAmount(CapitalAssetInformation capitalAssetInformation) {
        KualiDecimal accountingLinesTotalAmount = KualiDecimal.ZERO;
        
        List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAssetInformation.getCapitalAssetAccountsGroupDetails();
        
        for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
            accountingLinesTotalAmount = accountingLinesTotalAmount.add(groupAccountLine.getAmount());
        }
        
        return accountingLinesTotalAmount;
    }
    
    /**
     * 
     * 
     * @param capitalAccountingLine
     * @param capitalAssetInformation
     * @param actionTypeCode
     * @return accountingLinesTotalAmount
     */
    protected KualiDecimal getAccountingLinesDistributedAmount(CapitalAccountingLines capitalAccountingLine, List<CapitalAssetInformation> capitalAssetInformation, String actionTypeCode) {
        KualiDecimal accountingLinesTotalAmount = KualiDecimal.ZERO;
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            if (capitalAsset.getCapitalAssetActionIndicator().equalsIgnoreCase(actionTypeCode)) {
                accountingLinesTotalAmount = accountingLinesTotalAmount.add(getAccountingLineAmount(capitalAsset, capitalAccountingLine));
            }
        }
        
        return accountingLinesTotalAmount;
    }
    
    /**
     * 
     * @param capitalAccountingLine
     * @param lastCapitalAsset
     * @param difference
     */
    protected void adjustAccountingLineAmountOnLastCapitalAsset(CapitalAccountingLines capitalAccountingLine, CapitalAssetInformation lastCapitalAsset, KualiDecimal difference) {
        List<CapitalAssetAccountsGroupDetails> groupAccountLines = lastCapitalAsset.getCapitalAssetAccountsGroupDetails();
        
        for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
            if (groupAccountLine.getCapitalAssetLineNumber().compareTo(lastCapitalAsset.getCapitalAssetLineNumber()) == 0 &&
                    groupAccountLine.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&                        
                    groupAccountLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                    groupAccountLine.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
                    groupAccountLine.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
                    groupAccountLine.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
                groupAccountLine.setAmount(groupAccountLine.getAmount().add(difference));
            }
        }
    }
    
    /**
     * when the user user hits refresh button, takes the amount in the amount field and
     * distributes to the group capital accounting lines for that asset only.
     * @param capitalAssetInformation
     * @param selectedCapitalAccountingLines
     */
    protected void redistributeToGroupAccountingLinesFromAssetsByAmounts(List<CapitalAccountingLines> selectedCapitalAccountingLines, CapitalAssetInformation capitalAsset) {
        KualiDecimal amountToDistribute = capitalAsset.getCapitalAssetLineAmount();
        KualiDecimal amountDistributed = KualiDecimal.ZERO;
        
        KualiDecimal totalCapitalAccountsAmount = getTotalCapitalAccountsAmounts(selectedCapitalAccountingLines);
        
        //to capture the last group accounting line to update its amount with any variance.
        CapitalAssetAccountsGroupDetails lastGroupAccountLine = new CapitalAssetAccountsGroupDetails();
        
        List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
        for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
            BigDecimal linePercent = getCapitalAccountingLinePercent(selectedCapitalAccountingLines, groupAccountLine, totalCapitalAccountsAmount);
            //found the accounting line
            lastGroupAccountLine = groupAccountLine;
            
            KualiDecimal groupAccountLineAmount = capitalAsset.getCapitalAssetLineAmount().multiply(new KualiDecimal(linePercent));
            groupAccountLine.setAmount(groupAccountLineAmount);
            
            //keep track of amoutn distributed so far.
            amountDistributed = amountDistributed.add(groupAccountLineAmount);
        }
        
        //add any variance in the amounts to the last group accounting line.
        lastGroupAccountLine.setAmount(lastGroupAccountLine.getAmount().add(amountToDistribute.subtract(amountDistributed)));
    }
    
    /**
     * calculates the total amount of the selected capital accounting lines
     * 
     * @param capitalAccountingLines
     * @return total amount of the selected capital accounting lines.
     */
    protected KualiDecimal getTotalCapitalAccountsAmounts(List<CapitalAccountingLines> capitalAccountingLines) {
        KualiDecimal totalCapitalAccountsAmount = KualiDecimal.ZERO;
        for (CapitalAccountingLines capitalLine : capitalAccountingLines) {
            totalCapitalAccountsAmount = totalCapitalAccountsAmount.add(capitalLine.getAmount());
        }
        
        return totalCapitalAccountsAmount;
    }
    
    /**
     * This method redistributes the capital asset amount to its group accounting lines
     * based on the accounting line's percent.  Takes each capital assets amount and
     * distributes to the capital asset group accounting lines.
     * 
     * @param kualiAccountingDocumentFormBase
     */
    protected void DistributeCapitalAssetAmountToGroupAccountingLines(KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase) {
        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = (CapitalAccountingLinesFormBase) kualiAccountingDocumentFormBase;
        checkSelectForCapitalAccountingLines(capitalAccountingLinesFormBase);        
        
        checkCapitalAccountingLinesSelected(capitalAccountingLinesFormBase);
        calculatePercentsForSelectedCapitalAccountingLines(capitalAccountingLinesFormBase);
        
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) capitalAccountingLinesFormBase.getFinancialDocument();
        String distributionAmountCode = capitalAccountingLinesFormBase.getCapitalAccountingLine().getDistributionCode();

        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();        
        
        List<CapitalAccountingLines> selectedCapitalAccountingLines = new ArrayList<CapitalAccountingLines>();

        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.isSelectLine() && !capitalAccountingLine.isAmountDistributed()) {
                capitalAccountingLine.setDistributionAmountCode(KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR);
                selectedCapitalAccountingLines.add(capitalAccountingLine);
            }
        }

        List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
        //redistribute the capital asset modify amount to the group accounting lines
        //based on amount.
            redistributeToGroupAccountingLinesFromAssetsByAmounts(selectedCapitalAccountingLines, capitalAsset);
        }
    }
    
}
