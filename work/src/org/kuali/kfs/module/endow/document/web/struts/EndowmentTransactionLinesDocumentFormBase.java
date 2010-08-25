/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.web.struts;

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;

public abstract class EndowmentTransactionLinesDocumentFormBase extends FinancialSystemTransactionalDocumentFormBase {

    protected FormFile transactionLineImportFile;
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
}
