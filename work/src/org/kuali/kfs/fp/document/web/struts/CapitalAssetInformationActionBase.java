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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.CapitalAccountingLines;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformationDetail;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.fp.document.CapitalAssetInformationDocumentBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * This is the action class for the CapitalAssetInformationActionBase.
 */
public abstract class CapitalAssetInformationActionBase extends KualiAccountingDocumentActionBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiAccountingDocumentActionBase.class);
    
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
        capitalAssetInformation.get(clearIndex).setCapitalAssetNumber(new Long(0));
        capitalAssetInformation.get(clearIndex).setAmount(KualiDecimal.ZERO);
        
        //now process the remaining capital asset records
        processRemainingCapitalAssetInfo(form, capitalAssetInformation);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * adds capital asset information to the list and create a new capital asset information
     * detail record for the user enter details. Also recalculates the system control and system
     * control remaining amounts.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward string
     * @throws Exception
     */
    public ActionForward addCapitalAssetInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("addCapitalAssetInfoDetail() - start");

        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);

        if (capitalAssetInformation == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        //now process the remaining capital asset records
        processRemainingCapitalAssetInfo(form, capitalAssetInformation);
        
        //now add the capital asset information detail record....
        int addIndex = getSelectedLine(request);
        createCapitalAssetInformationDetail(capitalAssetInformation.get(addIndex));
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * adds capital asset information to the modify capital assets list.
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
    public ActionForward addCapitalAssetModify(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("addCapitalAssetInfoDetail() - start");

        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);

        if (capitalAssetInformation == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        int addIndex = getSelectedLine(request);        
        if (capitalAssetInformation.get(addIndex).getCapitalAssetNumber() == null) {
            GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_CAPITAL_ASSET_MODIFY_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_CAPITAL_ASSET_NUMBER_REQUIRED);
        }
        
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
            capitalAssetInformation.get(lineIndexForCapitalAssetInfo).getCapitalAssetInformationDetails().remove(0);
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
            capitalAssetInformation.setAmount(KualiDecimal.ZERO);
            
            capitalAssetInformation.getCapitalAssetInformationDetails().clear();
        }
    }

    /**
     * Overridden to guarantee that form of copied document is set to whatever the entry mode of the document is
     * @see org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase#copy(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.copy(mapping, form, request, response);
        
        // if the copied document has capital asset collection, remove the collection
        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        AccountingDocument document = kualiAccountingDocumentFormBase.getFinancialDocument();
        if (document instanceof CapitalAssetEditable) {
            CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) document;
            
            List<CapitalAssetInformation> capitalAssets = capitalAssetEditable.getCapitalAssetInformation();
            for (CapitalAssetInformation capitalAsset : capitalAssets) {
                resetCapitalAssetInfo(capitalAsset);
            }
        }

        return forward;
    }

    /**
     * Populates capital asset information collection with capital accounting lines.
     * Based on actionType, capitalassetactionindicator attribute is filled with 'C' for create
     * and 'M' for modify assets, which will be used to differentiate to pull the records in
     * create asset screen or modify asset screen. 
     * 
     * @param calfb
     * @param actionType tells whether we are here from createAsset or modifyAsset actions
     */
    protected void createCapitalAssetInformation(CapitalAccountingLinesFormBase calfb, String actionType) {
        List<CapitalAccountingLines> capitalAccountingLines = calfb.getCapitalAccountingLines();        
        List<CapitalAssetInformation> capitalAssetInformation = new ArrayList<CapitalAssetInformation>();
        
        calfb.setSystemControlAmount(KualiDecimal.ZERO);
        
        KualiAccountingDocumentFormBase kadfb = (KualiAccountingDocumentFormBase) calfb;

        List<CapitalAssetInformation> currentCapitalAssetInformation =  this.getCurrentCapitalAssetInformationObject(kadfb);
        
        String documentNumber = calfb.getDocument().getDocumentNumber();
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.isSelectLine()) {
                calfb.setSystemControlAmount(calfb.getSystemControlAmount().add(capitalAccountingLine.getAmount()));
                
                //already capital asset information exists... so if not over the line amount,
                //create another asset template for the user to fill in asset information
                CapitalAssetInformation existingCapitalAsset = capitalAssetCreated(capitalAccountingLine, currentCapitalAssetInformation);
                
                if (ObjectUtils.isNotNull(existingCapitalAsset)) {
                    if (!accountingLineAmountDistributed(capitalAccountingLine, currentCapitalAssetInformation, existingCapitalAsset)) {
                        //accounting line amount not completely distributed yet so we need to create more assets
                        //add the capital information record to the list of asset information
                        CapitalAssetInformation capitalAsset = new CapitalAssetInformation();
                        capitalAsset.setSequenceNumber(existingCapitalAsset.getSequenceNumber());
                        capitalAsset.setCapitalAssetLineNumber(getNextCapitalAssetLineNumber(currentCapitalAssetInformation, existingCapitalAsset));
                        capitalAsset.setFinancialDocumentLineTypeCode(existingCapitalAsset.getFinancialDocumentLineTypeCode());
                        capitalAsset.setChartOfAccountsCode(existingCapitalAsset.getChartOfAccountsCode());
                        capitalAsset.setAccountNumber(existingCapitalAsset.getAccountNumber());
                        capitalAsset.setFinancialObjectCode(existingCapitalAsset.getFinancialObjectCode());
                        capitalAsset.setAmount(KualiDecimal.ZERO);
                        capitalAsset.setDocumentNumber(documentNumber);
                        capitalAsset.setCapitalAssetActionIndicator(actionType);
                        currentCapitalAssetInformation.add(capitalAsset);
                    }
                }
                else {
                    //add the capital information record to the list of asset information
                    CapitalAssetInformation capitalAsset = new CapitalAssetInformation();
                    capitalAsset.setSequenceNumber(capitalAccountingLine.getSequenceNumber());
                    capitalAsset.setFinancialDocumentLineTypeCode(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE);
                    capitalAsset.setChartOfAccountsCode(capitalAccountingLine.getChartOfAccountsCode());
                    capitalAsset.setAccountNumber(capitalAccountingLine.getAccountNumber());
                    capitalAsset.setFinancialObjectCode(capitalAccountingLine.getFinancialObjectCode());
                    capitalAsset.setAmount(KualiDecimal.ZERO);
                    capitalAsset.setDocumentNumber(documentNumber);
                    capitalAsset.setCapitalAssetLineNumber(1);
                    capitalAsset.setCapitalAssetActionIndicator(actionType);
                    currentCapitalAssetInformation.add(capitalAsset);
                }
            }
        } //for loop
    }
    
    /**
     * @param capitalAccountingLine
     * @param capitalAssetInformation
     * @return
     */
    protected boolean isCapitalAssetCreated(CapitalAccountingLines capitalAccountingLine, List<CapitalAssetInformation> capitalAssetInformation) {
        boolean created = false;
       
        if (ObjectUtils.isNull(capitalAssetInformation) ||capitalAssetInformation.size() <= 0) {
            return false;
        }
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
           if (capitalAsset.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&
                   capitalAsset.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                   capitalAsset.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
                   capitalAsset.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
                   capitalAsset.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
               created = true;
               break;
           }
        }
       
        return created;
    }
    
    /**
     * 
     * @param capitalAccountingLine
     * @param capitalAssetInformation
     * @return
     */
    protected CapitalAssetInformation capitalAssetCreated(CapitalAccountingLines capitalAccountingLine, List<CapitalAssetInformation> capitalAssetInformation) {
        CapitalAssetInformation existingCapitalAsset = null;
        
        if (ObjectUtils.isNull(capitalAssetInformation) && capitalAssetInformation.size() <= 0) {
            return existingCapitalAsset;
        }
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            if (capitalAsset.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&
                    capitalAsset.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                    capitalAsset.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
                    capitalAsset.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
                    capitalAsset.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
                existingCapitalAsset = capitalAsset;
                break;
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
    protected Integer getNextCapitalAssetLineNumber(List<CapitalAssetInformation> capitalAssetInformation, CapitalAssetInformation existingCapitalAssetInformation) {
        int nextCapitalAssetLineNumber = 1;
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            if (capitalAsset.getSequenceNumber().compareTo(existingCapitalAssetInformation.getSequenceNumber()) == 0 &&
                    capitalAsset.getFinancialDocumentLineTypeCode().equals(existingCapitalAssetInformation.getFinancialDocumentLineTypeCode()) && 
                    capitalAsset.getChartOfAccountsCode().equals(existingCapitalAssetInformation.getChartOfAccountsCode()) && 
                    capitalAsset.getAccountNumber().equals(existingCapitalAssetInformation.getAccountNumber()) && 
                    capitalAsset.getFinancialObjectCode().equals(existingCapitalAssetInformation.getFinancialObjectCode())) {
                nextCapitalAssetLineNumber += 1;
            }
        }
        
        return nextCapitalAssetLineNumber;
    }
    
    /**
     * 
     * @param kadfb
     * @param currentCapitalAssetInformation
     * @param existingCapitalAsset
     * @return true if accounting line amount equals to capital assets amount, else false.
     */
    protected boolean accountingLineAmountDistributed(CapitalAccountingLines capitalAccountingLine, List<CapitalAssetInformation> currentCapitalAssetInformation, CapitalAssetInformation existingCapitalAsset) {
        boolean distributed = true;
        
        KualiDecimal accountingLineAmount = capitalAccountingLine.getAmount();
        KualiDecimal capitalAssetsAmount = getCapitalAssetsAmount(currentCapitalAssetInformation, existingCapitalAsset);
        
        if (accountingLineAmount.isGreaterThan(capitalAssetsAmount)) {
            distributed = false;
        }
        
        return distributed;
    }
    
    /**
     * sums the capital assets amount distributed so far for a given capital accounting line
     * 
     * @param currentCapitalAssetInformation
     * @param existingCapitalAsset
     * @return capitalAssetsAmount amount that has been distributed for the specific capital accounting line
     */
    protected KualiDecimal getCapitalAssetsAmount(List<CapitalAssetInformation> currentCapitalAssetInformation, CapitalAssetInformation existingCapitalAsset) {
        //check the capital assets records totals
        KualiDecimal capitalAssetsAmount = KualiDecimal.ZERO;
        
        for (CapitalAssetInformation capitalAsset: currentCapitalAssetInformation) {
            if (capitalAsset.getSequenceNumber().compareTo(existingCapitalAsset.getSequenceNumber()) == 0 &&
                    capitalAsset.getFinancialDocumentLineTypeCode().equals(existingCapitalAsset.getFinancialDocumentLineTypeCode()) && 
                    capitalAsset.getChartOfAccountsCode().equals(existingCapitalAsset.getChartOfAccountsCode()) && 
                    capitalAsset.getAccountNumber().equals(existingCapitalAsset.getAccountNumber()) && 
                    capitalAsset.getFinancialObjectCode().equals(existingCapitalAsset.getFinancialObjectCode())) {
                capitalAssetsAmount = capitalAssetsAmount.add(capitalAsset.getAmount());
            }
        }
        
        return capitalAssetsAmount;
    }

    /**
     * 
     * @param capitalAsset
     */
    protected void createCapitalAssetInformationDetail(CapitalAssetInformation capitalAsset) {
        CapitalAssetInformationDetail assetDetail = new CapitalAssetInformationDetail();
        assetDetail.setDocumentNumber(capitalAsset.getDocumentNumber());
        assetDetail.setChartOfAccountsCode(capitalAsset.getChartOfAccountsCode());
        assetDetail.setAccountNumber(capitalAsset.getAccountNumber());
        assetDetail.setFinancialDocumentLineTypeCode(capitalAsset.getFinancialDocumentLineTypeCode());
        assetDetail.setFinancialObjectCode(capitalAsset.getFinancialObjectCode());
        assetDetail.setSequenceNumber(capitalAsset.getSequenceNumber());
        assetDetail.setCapitalAssetLineNumber(capitalAsset.getCapitalAssetLineNumber());
        assetDetail.setItemLineNumber(capitalAsset.getCapitalAssetLineNumber());
        capitalAsset.getCapitalAssetInformationDetails().add(0,assetDetail);
    }
    
    protected void calculateRemainingDistributedAmount(CapitalAccountingLinesFormBase calfb, List<CapitalAssetInformation> capitalAssetInformation) {
        calfb.setCreatedAssetsControlAmount(calfb.getSystemControlAmount());
            
        //get amount allocated so far....or the system control remainder amount field.
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
            calfb.setCreatedAssetsControlAmount(calfb.getCreatedAssetsControlAmount().subtract(capitalAsset.getAmount()));
        }
    }
    
    /**
     * Populates capital asset information collection with capital accounting lines
     * 
     * @param calfb
     */
    protected void checkCapitalAccountingLinesSelected(CapitalAccountingLinesFormBase calfb) {
        List<CapitalAccountingLines> capitalAccountingLines = calfb.getCapitalAccountingLines();        

        calfb.setSystemControlAmount(KualiDecimal.ZERO);
        
        KualiAccountingDocumentFormBase kadfb = (KualiAccountingDocumentFormBase) calfb;

        List<CapitalAssetInformation> currentCapitalAssetInformation =  this.getCurrentCapitalAssetInformationObject(kadfb);
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            CapitalAssetInformation existingCapitalAsset = capitalAssetCreated(capitalAccountingLine, currentCapitalAssetInformation);
            if (ObjectUtils.isNotNull(existingCapitalAsset)) {
                capitalAccountingLine.setSelectLine(true);
                calfb.setSystemControlAmount(calfb.getSystemControlAmount().add(capitalAccountingLine.getAmount()));
                if (accountingLineAmountDistributed(capitalAccountingLine, currentCapitalAssetInformation, existingCapitalAsset)) {
                    capitalAccountingLine.setAmountDistributed(true);
                }
                else {
                    capitalAccountingLine.setAmountDistributed(false);
                }
            }
            else {
                capitalAccountingLine.setSelectLine(false);
            }
        }
        
        calfb.setCreatedAssetsControlAmount(calfb.getSystemControlAmount());
        
        //get amount allocated so far....or the system control remainder amount field.
        for (CapitalAssetInformation capitalAsset : currentCapitalAssetInformation) {
            calfb.setCreatedAssetsControlAmount(calfb.getCreatedAssetsControlAmount().subtract(capitalAsset.getAmount()));
        }
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
        if (capitalAccountingLinesFormBase.getCapitalAccountingLines().size() > 0) {
            newTabStates.put("AccountingLinesforCapitalization", KFSConstants.CapitalAssets.CAPITAL_ASSET_TAB_STATE_OPEN);
        }
        else {
            newTabStates.put("AccountingLinesforCapitalization", KFSConstants.CapitalAssets.CAPITAL_ASSET_TAB_STATE_CLOSE);
        }

        if (checkCreateAssetsExist(capitalAccountingLinesFormBase)) {
            newTabStates.put("CreateCapitalAssets", KFSConstants.CapitalAssets.CAPITAL_ASSET_TAB_STATE_OPEN);  
        }
        else {
            newTabStates.put("CreateCapitalAssets", KFSConstants.CapitalAssets.CAPITAL_ASSET_TAB_STATE_CLOSE);  
        }
        
        if (checkModifyAssetsExist(capitalAccountingLinesFormBase)) {
            newTabStates.put("ModifyCapitalAssets", KFSConstants.CapitalAssets.CAPITAL_ASSET_TAB_STATE_OPEN);  
        }
        else {
            newTabStates.put("ModifyCapitalAssets", KFSConstants.CapitalAssets.CAPITAL_ASSET_TAB_STATE_CLOSE);  
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
    
    
}
