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
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;

public abstract class EndowmentTransactionLinesDocumentFormBase extends FinancialSystemTransactionalDocumentFormBase {

    protected FormFile transactionLineImportFile;
    private EndowmentTransactionLine newSourceTransactionLine;
    private EndowmentTransactionLine newTargetTransactionLine;
    private EndowmentSourceTransactionSecurity sourceTransactionSecurity;
    private EndowmentTargetTransactionSecurity targetTransactionSecurity;

    private String balanceInquiryReturnAnchor;

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
}
