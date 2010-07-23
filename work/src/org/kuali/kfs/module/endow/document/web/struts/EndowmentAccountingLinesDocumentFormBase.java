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
import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.SourceEndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.TargetEndowmentAccountingLine;
import org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocumentBase;

/**
 * This class...
 */
public abstract class EndowmentAccountingLinesDocumentFormBase extends EndowmentTransactionLinesDocumentFormBase {
    protected FormFile accountingLineImportFile;
    protected EndowmentAccountingLine newSourceAccountingLine;
    protected EndowmentAccountingLine newTargetAccountingLine;

    /**
     * Constructs a EndowmentAccountingLinesDocumentFormBase.java.
     */
    public EndowmentAccountingLinesDocumentFormBase() {
        super();
        newSourceAccountingLine = new SourceEndowmentAccountingLine();
        newTargetAccountingLine = new TargetEndowmentAccountingLine();
    }

    /**
     * Gets the EndowmentAccountingLinesDocumentBase.
     * 
     * @return EndowmentAccountingLinesDocumentBase
     */
    public EndowmentAccountingLinesDocumentBase getEndowmentAccountingLinesDocumentBase() {
        return (EndowmentAccountingLinesDocumentBase) getDocument();
    }

    /**
     * Gets the accountingLineImportFile.
     * 
     * @return accountingLineImportFile
     */
    public FormFile getAccountingLineImportFile() {
        return accountingLineImportFile;
    }

    /**
     * Sets the accountingLineImportFile.
     * 
     * @param accountingLineImportFile
     */
    public void setAccountingLineImportFile(FormFile accountingLineImportFile) {
        this.accountingLineImportFile = accountingLineImportFile;
    }

    /**
     * Gets the newSourceAccountingLine.
     * 
     * @return newSourceAccountingLine
     */
    public EndowmentAccountingLine getNewSourceAccountingLine() {
        return newSourceAccountingLine;
    }

    /**
     * Sets the newSourceAccountingLine.
     * 
     * @param newSourceAccountingLine
     */
    public void setNewSourceAccountingLine(EndowmentAccountingLine newSourceAccountingLine) {
        this.newSourceAccountingLine = newSourceAccountingLine;
    }

    /**
     * Gets the newSourceAccountingLine.
     * 
     * @return newSourceAccountingLine
     */
    public EndowmentAccountingLine getNewTargetAccountingLine() {
        return newTargetAccountingLine;
    }

    /**
     * Sets the newSourceAccountingLine.
     * 
     * @param newTargetAccountingLine
     */
    public void setNewTargetAccountingLine(EndowmentAccountingLine newTargetAccountingLine) {
        this.newTargetAccountingLine = newTargetAccountingLine;
    }

}
