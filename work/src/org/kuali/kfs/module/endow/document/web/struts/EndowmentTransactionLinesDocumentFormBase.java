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
package org.kuali.kfs.module.endow.document.web.struts;

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public abstract class EndowmentTransactionLinesDocumentFormBase extends FinancialSystemTransactionalDocumentFormBase {

    protected FormFile transactionLineImportFile;
    protected FormFile transactionSourceLinesImportFile;
    
    protected EndowmentTransactionLine newSourceTransactionLine;
    protected EndowmentTransactionLine newTargetTransactionLine;
    private EndowmentSourceTransactionSecurity sourceTransactionSecurity;
    private EndowmentTargetTransactionSecurity targetTransactionSecurity;

    private String balanceInquiryReturnAnchor;

    // labels that appear on the transaction lines as group headers.
    protected String sourceGroupLabelName = EndowConstants.SOURCE_TRANSACTION_LINE_GROUP_LABEL_NAME;
    protected String targetGroupLabelName = EndowConstants.TARGET_TRANSACTION_LINE_GROUP_LABEL_NAME;

    // labels for the source and taget tax lot lines
    protected String sourceTaxLotsLabelName = EndowConstants.SOURCE_TRANSACTION_LINE_GROUP_LABEL_NAME;
    protected String targetTaxLotsLabelName = EndowConstants.TARGET_TRANSACTION_LINE_GROUP_LABEL_NAME;

    // these properties control if the total amount and units show on the summary lines.
    // the default is set to true to show the values on the document.
    protected boolean showIncomeTotalAmount = true;
    protected boolean showPrincipalTotalAmount = true;

    protected boolean showIncomeTotalUnits = true;
    protected boolean showPrincipalTotalUnits = true;
    protected boolean showTransactionAmount = true;
    protected boolean showUnitAdjustmentAmount = false; // default -> do not show

    // the income or principal box value will be set on tag file using the property.
    // if set to true, income or principal drop-down box will be readonly with value set to P-Principal
    protected boolean fieldValueToPrincipal = false;

    // show the etran code on the document...
    protected boolean showETranCode = true;

    // show the import button on source transaction lines
    protected boolean showSourceImport = true;

    // show the import button on target transaction lines
    protected boolean showTargetImport = true;

    // show the add button on the source transaction lines
    protected boolean showSourceAdd = true;

    // show the add button on the target transaction lines
    protected boolean showTargetAdd = true;

    // make the KEMID field on the source transaction lines read only.
    protected boolean sourceKemidReadOnly = false;

    // make the KEMID field on the target transaction lines read only.
    protected boolean targetKemidReadOnly = false;

    // make the income/principal indicator on the source transaction lines read only.
    protected boolean sourceIncomePrincipalIndicatorReadOnly = false;

    // make the income/principal indicator on the target transaction lines read only.
    protected boolean targetIncomePrincipalIndicatorReadOnly = false;

    // show all the added source transaction lines on the form.
    protected boolean showSourceTransLines = true;

    // show all the added target transaction lines on the form.
    protected boolean showTargetTransLines = true;

    protected boolean showSourceRefresh = true;
    protected boolean showTargetRefresh = true;
    protected boolean showSourceBalance = true;
    protected boolean showTargetBalance = true;
    protected boolean showSourceDelete = true;
    protected boolean showTargetDelete = true;

    /**
     * Constructs a EndowmentTransactionLinesDocumentFormBase.java.
     */
    public EndowmentTransactionLinesDocumentFormBase() {
        super();
        newSourceTransactionLine = new EndowmentSourceTransactionLine();
        newTargetTransactionLine = new EndowmentTargetTransactionLine();
        sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
    }

    /**
     * Gets an EndowmentTransactionLinesDocumentBase.
     * 
     * @return EndowmentTransactionLinesDocumentBase
     */
    public EndowmentTransactionLinesDocumentBase getEndowmentTransactionLinesDocumentBase() {
        return (EndowmentTransactionLinesDocumentBase) getDocument();
    }

    /**
     * Gets the newSourceTransactionLine attribute.
     * 
     * @return Returns the newSourceTransactionLine.
     */
    public EndowmentTransactionLine getNewSourceTransactionLine() {
        return newSourceTransactionLine;
    }

    /**
     * Sets the newSourceTransactionLine attribute value.
     * 
     * @param newSourceTransactionLine The newSourceTransactionLine to set.
     */
    public void setNewSourceTransactionLine(EndowmentTransactionLine newSourceTransactionLine) {
        this.newSourceTransactionLine = newSourceTransactionLine;
    }

    /**
     * Gets the newTargetTransactionLine attribute.
     * 
     * @return Returns the newTargetTransactionLine.
     */
    public EndowmentTransactionLine getNewTargetTransactionLine() {
        return newTargetTransactionLine;
    }

    /**
     * Sets the newTargetTransactionLine attribute value.
     * 
     * @param newTargetTransactionLine The newTargetTransactionLine to set.
     */
    public void setNewTargetTransactionLine(EndowmentTransactionLine newTargetTransactionLine) {
        this.newTargetTransactionLine = newTargetTransactionLine;
    }

    public EndowmentSourceTransactionSecurity getSourceTransactionSecurity() {
        return sourceTransactionSecurity;
    }

    public void setSourceTransactionSecurity(EndowmentSourceTransactionSecurity sourceTransactionSecurity) {
        this.sourceTransactionSecurity = sourceTransactionSecurity;
    }

    public EndowmentTargetTransactionSecurity getTargetTransactionSecurity() {
        return targetTransactionSecurity;
    }

    public void setTargetTransactionSecurity(EndowmentTargetTransactionSecurity targetTransactionSecurity) {
        this.targetTransactionSecurity = targetTransactionSecurity;
    }

    /**
     * Gets the transactionLineImportFile.
     * 
     * @return transactionLineImportFile
     */
    public FormFile getTransactionLineImportFile() {
        return transactionLineImportFile;
    }

    /**
     * Sets the transactionLineImportFile.
     * 
     * @param transactionLineImportFile
     */
    public void setTransactionLineImportFile(FormFile transactionLineImportFile) {
        this.transactionLineImportFile = transactionLineImportFile;
    }

    /**
     * Gets the balanceInquiryReturnAnchor attribute.
     * 
     * @return Returns the balanceInquiryReturnAnchor.
     */
    public String getBalanceInquiryReturnAnchor() {
        return balanceInquiryReturnAnchor;
    }

    /**
     * Sets the balanceInquiryReturnAnchor attribute value.
     * 
     * @param balanceInquiryReturnAnchor The balanceInquiryReturnAnchor to set.
     */
    public void setBalanceInquiryReturnAnchor(String balanceInquiryReturnAnchor) {
        this.balanceInquiryReturnAnchor = balanceInquiryReturnAnchor;
    }

    /**
     * Tells whether the source transaction lines add transaction line section should be displayed. By default this returns true for
     * all documents. If a document needs to handle this in a more special way than this method should be overridden in the document
     * Form class.
     * 
     * @return true
     */
    public boolean getShowFromTransactionLine() {
        return true;
    }

    /**
     * Tells whether the target transaction lines add transaction line section should be displayed. By default this returns true for
     * all documents. If a document needs to handle this in a more special way than this method should be overridden in the document
     * Form class.
     * 
     * @return true
     */
    public boolean getShowToTransactionLine() {
        return true;
    }

    /**
     * Gets the sourceGroupLabelName attribute.
     * 
     * @return Returns the sourceGroupLabelName.
     */
    public String getSourceGroupLabelName() {
        return sourceGroupLabelName;
    }

    /**
     * Sets the sourceGroupLabelName attribute value.
     * 
     * @param sourceGroupLabelName The sourceGroupLabelName to set.
     */
    public void setSourceGroupLabelName(String sourceGroupLabelName) {
        this.sourceGroupLabelName = sourceGroupLabelName;
    }

    /**
     * Gets the targetGroupLabelName attribute.
     * 
     * @return Returns the targetGroupLabelName.
     */
    public String getTargetGroupLabelName() {
        return targetGroupLabelName;
    }

    /**
     * Sets the targetGroupLabelName attribute value.
     * 
     * @param targetGroupLabelName The targetGroupLabelName to set.
     */
    public void setTargetGroupLabelName(String targetGroupLabelName) {
        this.targetGroupLabelName = targetGroupLabelName;
    }

    /**
     * Gets the showIncomeTotalAmount attribute.
     * 
     * @return Returns the showIncomeTotalAmount.
     */
    public boolean isShowIncomeTotalAmount() {
        return showIncomeTotalAmount;
    }

    /**
     * Sets the showIncomeTotalAmount attribute value.
     * 
     * @param showIncomeTotalAmount The showIncomeTotalAmount to set.
     */
    public void setShowIncomeTotalAmount(boolean showIncomeTotalAmount) {
        this.showIncomeTotalAmount = showIncomeTotalAmount;
    }

    /**
     * Gets the showIncomeTotalAmount attribute.
     * 
     * @return Returns the showIncomeTotalAmount.
     */
    public boolean isShowPrincipalTotalAmount() {
        return showPrincipalTotalAmount;
    }

    /**
     * Sets the showPrincipalTotalAmount attribute value.
     * 
     * @param showPrincipalTotalAmount The showPrincipalTotalAmount to set.
     */
    public void setShowPrincipalTotalAmount(boolean showPrincipalTotalAmount) {
        this.showPrincipalTotalAmount = showPrincipalTotalAmount;
    }

    /**
     * Gets the showIncomeTotalUnits attribute.
     * 
     * @return Returns the showIncomeTotalUnits.
     */
    public boolean isShowIncomeTotalUnits() {
        return showIncomeTotalUnits;
    }

    /**
     * Sets the showIncomeTotalUnits attribute value.
     * 
     * @param showIncomeTotalUnits The showIncomeTotalUnits to set.
     */
    public void setShowIncomeTotalUnits(boolean showIncomeTotalUnits) {
        this.showIncomeTotalUnits = showIncomeTotalUnits;
    }

    /**
     * Gets the showPrincipalTotalUnits attribute.
     * 
     * @return Returns the showPrincipalTotalUnits.
     */
    public boolean isShowPrincipalTotalUnits() {
        return showPrincipalTotalUnits;
    }

    /**
     * Sets the showIncomeTotalUnits attribute value.
     * 
     * @param showIncomeTotalUnits The showIncomeTotalUnits to set.
     */
    public void setShowPrincipalTotalUnits(boolean showPrincipalTotalUnits) {
        this.showPrincipalTotalUnits = showPrincipalTotalUnits;
    }

    /**
     * Gets the fieldValueToPrincipal attribute.
     * 
     * @return Returns the fieldValueToPrincipal.
     */
    public boolean isFieldValueToPrincipal() {
        return fieldValueToPrincipal;
    }

    /**
     * Sets the fieldValueToPrincipal attribute value.
     * 
     * @param fieldValueToPrincipal The fieldValueToPrincipal to set.
     */
    public void setFeildValueToPrincipal(boolean fieldValueToPrincipal) {
        this.fieldValueToPrincipal = fieldValueToPrincipal;
    }

    /**
     * Gets the showETranCode attribute.
     * 
     * @return Returns the showETranCode.
     */
    public boolean isShowETranCode() {
        return showETranCode;
    }

    /**
     * Sets the showETranCode attribute value.
     * 
     * @param showETranCode The showETranCode to set.
     */
    public void setShowETranCode(boolean showETranCode) {
        this.showETranCode = showETranCode;
    }

    /**
     * Gets sourceTaxLotsLabelName.
     * 
     * @return sourceTaxLotsLabelName
     */
    public String getSourceTaxLotsLabelName() {
        return sourceTaxLotsLabelName;
    }

    /**
     * Sets the sourceTaxLotsLabelName.
     * 
     * @param sourceTaxLotsLabelName
     */
    public void setSourceTaxLotsLabelName(String sourceTaxLotsLabelName) {
        this.sourceTaxLotsLabelName = sourceTaxLotsLabelName;
    }

    /**
     * Gets the targetTaxLotsLabelName.
     * 
     * @return targetTaxLotsLabelName
     */
    public String getTargetTaxLotsLabelName() {
        return targetTaxLotsLabelName;
    }

    /**
     * Sets targetTaxLotsLabelName.
     * 
     * @param targetTaxLotsLabelName
     */
    public void setTargetTaxLotsLabelName(String targetTaxLotsLabelName) {
        this.targetTaxLotsLabelName = targetTaxLotsLabelName;
    }

    /**
     * Gets showTransactionAmount.
     * 
     * @return showTransactionAmount
     */
    public boolean isShowTransactionAmount() {
        return showTransactionAmount;
    }

    /**
     * Sets showTransactionAmount.
     * 
     * @param showTransactionAmount
     */
    public void setShowTransactionAmount(boolean showTransactionAmount) {
        this.showTransactionAmount = showTransactionAmount;
    }

    /**
     * Gets showUnitAdjustmentAmount.
     * 
     * @return showUnitAdjustmentAmount
     */
    public boolean isShowUnitAdjustmentAmount() {
        return showUnitAdjustmentAmount;
    }

    /**
     * Sets showUnitAdjustmentAmount.
     * 
     * @param showUnitAdjustmentAmount
     */
    public void setShowUnitAdjustmentAmount(boolean showUnitAdjustmentAmount) {
        this.showUnitAdjustmentAmount = showUnitAdjustmentAmount;
    }

    /**
     * Gets the showSourceImport.
     * 
     * @return showSourceImport
     */
    public boolean isShowSourceImport() {
        return showSourceImport;
    }

    /**
     * Sets the showSourceImport.
     * 
     * @param showSourceImport
     */
    public void setShowSourceImport(boolean showSourceImport) {
        this.showSourceImport = showSourceImport;
    }

    /**
     * Gets the showTargetImport.
     * 
     * @return showTargetImport
     */
    public boolean isShowTargetImport() {
        return showTargetImport;
    }

    /**
     * Sets the showTargetImport.
     * 
     * @param showTargetImport
     */
    public void setShowTargetImport(boolean showTargetImport) {
        this.showTargetImport = showTargetImport;
    }

    /**
     * Gets the showSourceAdd attribute.
     * 
     * @return Returns the showSourceAdd.
     */
    public boolean isShowSourceAdd() {
        return showSourceAdd;
    }

    /**
     * Sets the showSourceAdd attribute value.
     * 
     * @param showSourceAdd The showSourceAdd to set.
     */
    public void setShowSourceAdd(boolean showSourceAdd) {
        this.showSourceAdd = showSourceAdd;
    }

    /**
     * Gets the showTargetAdd attribute.
     * 
     * @return Returns the showTargetAdd.
     */
    public boolean isShowTargetAdd() {
        return showTargetAdd;
    }

    /**
     * Sets the showTargetAdd attribute value.
     * 
     * @param showTargetAdd The showTargetAdd to set.
     */
    public void setShowTargetAdd(boolean showTargetAdd) {
        this.showTargetAdd = showTargetAdd;
    }

    /**
     * Gets the sourceKemidReadOnly attribute.
     * 
     * @return Returns the sourceKemidReadOnly.
     */
    public boolean isSourceKemidReadOnly() {
        return sourceKemidReadOnly;
    }

    /**
     * Sets the sourceKemidReadOnly attribute value.
     * 
     * @param sourceKemidReadOnly The sourceKemidReadOnly to set.
     */
    public void setSourceKemidReadOnly(boolean sourceKemidReadOnly) {
        this.sourceKemidReadOnly = sourceKemidReadOnly;
    }

    /**
     * Gets the targetKemidReadOnly attribute.
     * 
     * @return Returns the targetKemidReadOnly.
     */
    public boolean isTargetKemidReadOnly() {
        return targetKemidReadOnly;
    }

    /**
     * Sets the targetKemidReadOnly attribute value.
     * 
     * @param targetKemidReadOnly The targetKemidReadOnly to set.
     */
    public void setTargetKemidReadOnly(boolean targetKemidReadOnly) {
        this.targetKemidReadOnly = targetKemidReadOnly;
    }

    /**
     * Gets the targetIncomePrincipalIndicatorReadOnly attribute.
     * 
     * @return Returns the targetIncomePrincipalIndicatorReadOnly.
     */
    public boolean isTargetIncomePrincipalIndicatorReadOnly() {
        return targetIncomePrincipalIndicatorReadOnly;
    }

    /**
     * Sets the targetIncomePrincipalIndicatorReadOnly attribute value.
     * 
     * @param targetIncomePrincipalIndicatorReadOnly The targetIncomePrincipalIndicatorReadOnly to set.
     */
    public void setTargetIncomePrincipalIndicatorReadOnly(boolean targetIncomePrincipalIndicatorReadOnly) {
        this.targetIncomePrincipalIndicatorReadOnly = targetIncomePrincipalIndicatorReadOnly;
    }

    /**
     * Gets the sourceIncomePrincipalIndicatorReadOnly attribute.
     * 
     * @return Returns the sourceIncomePrincipalIndicatorReadOnly.
     */
    public boolean isSourceIncomePrincipalIndicatorReadOnly() {
        return sourceIncomePrincipalIndicatorReadOnly;
    }

    /**
     * Sets the sourceIncomePrincipalIndicatorReadOnly attribute value.
     * 
     * @param sourceIncomePrincipalIndicatorReadOnly The sourceIncomePrincipalIndicatorReadOnly to set.
     */
    public void setSourceIncomePrincipalIndicatorReadOnly(boolean sourceIncomePrincipalIndicatorReadOnly) {
        this.sourceIncomePrincipalIndicatorReadOnly = sourceIncomePrincipalIndicatorReadOnly;
    }

    /**
     * Gets the showSourceTransLines attribute.
     * 
     * @return Returns the showSourceTransLines.
     */
    public boolean isShowSourceTransLines() {
        return showSourceTransLines;
    }

    /**
     * Sets the showSourceTransLines attribute value.
     * 
     * @param showSourceTransLines The showSourceTransLines to set.
     */
    public void setShowSourceTransLines(boolean showSourceTransLines) {
        this.showSourceTransLines = showSourceTransLines;
    }

    /**
     * Gets the showTargetTransLines attribute.
     * 
     * @return Returns the showTargetTransLines.
     */
    public boolean isShowTargetTransLines() {
        return showTargetTransLines;
    }

    /**
     * Sets the showTargetTransLines attribute value.
     * 
     * @param showTargetTransLines The showTargetTransLines to set.
     */
    public void setShowTargetTransLines(boolean showTargetTransLines) {
        this.showTargetTransLines = showTargetTransLines;
    }

    /**
     * Gets the showSourceRefresh attribute.
     * 
     * @return Returns the showSourceRefresh.
     */
    public boolean isShowSourceRefresh() {
        return showSourceRefresh;
    }

    /**
     * Sets the showSourceRefresh attribute value.
     * 
     * @param showSourceRefresh The showSourceRefresh to set.
     */
    public void setShowSourceRefresh(boolean showSourceRefresh) {
        this.showSourceRefresh = showSourceRefresh;
    }

    /**
     * Gets the showTargetRefresh attribute.
     * 
     * @return Returns the showTargetRefresh.
     */
    public boolean isShowTargetRefresh() {
        return showTargetRefresh;
    }

    /**
     * Sets the showTargetRefresh attribute value.
     * 
     * @param showTargetRefresh The showTargetRefresh to set.
     */
    public void setShowTargetRefresh(boolean showTargetRefresh) {
        this.showTargetRefresh = showTargetRefresh;
    }

    /**
     * Gets the showSourceBalance attribute.
     * 
     * @return Returns the showSourceBalance.
     */
    public boolean isShowSourceBalance() {
        return showSourceBalance;
    }

    /**
     * Sets the showSourceBalance attribute value.
     * 
     * @param showSourceBalance The showSourceBalance to set.
     */
    public void setShowSourceBalance(boolean showSourceBalance) {
        this.showSourceBalance = showSourceBalance;
    }

    /**
     * Gets the showTargetBalance attribute.
     * 
     * @return Returns the showTargetBalance.
     */
    public boolean isShowTargetBalance() {
        return showTargetBalance;
    }

    /**
     * Sets the showTargetBalance attribute value.
     * 
     * @param showTargetBalance The showTargetBalance to set.
     */
    public void setShowTargetBalance(boolean showTargetBalance) {
        this.showTargetBalance = showTargetBalance;
    }

    /**
     * Gets the showSourceDelete attribute.
     * 
     * @return Returns the showSourceDelete.
     */
    public boolean isShowSourceDelete() {
        return showSourceDelete;
    }

    /**
     * Sets the showSourceDelete attribute value.
     * 
     * @param showSourceDelete The showSourceDelete to set.
     */
    public void setShowSourceDelete(boolean showSourceDelete) {
        this.showSourceDelete = showSourceDelete;
    }

    /**
     * Gets the showTargetDelete attribute.
     * 
     * @return Returns the showTargetDelete.
     */
    public boolean isShowTargetDelete() {
        return showTargetDelete;
    }

    /**
     * Sets the showTargetDelete attribute value.
     * 
     * @param showTargetDelete The showTargetDelete to set.
     */
    public void setShowTargetDelete(boolean showTargetDelete) {
        this.showTargetDelete = showTargetDelete;
    }

    /**
     * @return the URL to the accounting line import instructions
     */
    public String getTransactionLineImportInstructionsUrl() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY) + SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.ENDOWMENT_DOCUMENT.class, EndowParameterKeyConstants.ENDOWMENT_TRANSACTION_LINE_IMPORT);
    }

    /**
     * Gets the transactionSourceLinesImportFile.
     * 
     * @return transactionSourceLinesImportFile
     */
    public FormFile getTransactionSourceLinesImportFile() {
        return transactionSourceLinesImportFile;
    }

    /**
     * Sets the transactionSourceLinesImportFile.
     * 
     * @param transactionSourceLinesImportFile
     */
    public void setTransactionSourceLinesImportFile(FormFile transactionSourceLinesImportFile) {
        this.transactionSourceLinesImportFile = transactionSourceLinesImportFile;
    }
    
}
