/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * GeneralLedgerInputType is used to hold financial system data for particular document types. It links to Kuali Enterprise Workflow
 * and to the Kuali Nervous System Data Dictionary via the document type name.
 */
public class GeneralLedgerInputType extends PersistableBusinessObjectBase {

    private String inputTypeCode;
    private String documentTypeName;
    private boolean documentTypeActiveIndicator;
    private boolean transactionScrubberOffsetGenerationIndicator;

    /**
     * Gets the inputTypeCode attribute. 
     * @return Returns the inputTypeCode.
     */
    public String getInputTypeCode() {
        return inputTypeCode;
    }

    /**
     * Sets the inputTypeCode attribute value.
     * @param inputTypeCode The inputTypeCode to set.
     */
    public void setInputTypeCode(String inputTypeCode) {
        this.inputTypeCode = inputTypeCode;
    }

    /**
     * Gets the documentTypeName attribute. 
     * @return Returns the documentTypeName.
     */
    public String getDocumentTypeName() {
        return documentTypeName;
    }

    /**
     * Sets the documentTypeName attribute value.
     * @param documentTypeName The documentTypeName to set.
     */
    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    /**
     * @return the documentTypeActiveIndicator
     */
    public boolean isDocumentTypeActiveIndicator() {
        return this.documentTypeActiveIndicator;
    }

    /**
     * @param documentTypeActiveIndicator the documentTypeActiveIndicator to set
     */
    public void setDocumentTypeActiveIndicator(boolean documentTypeActiveIndicator) {
        this.documentTypeActiveIndicator = documentTypeActiveIndicator;
    }

    /**
     * Gets the transactionScrubberOffsetGenerationIndicator attribute. 
     * @return Returns the transactionScrubberOffsetGenerationIndicator.
     */
    public boolean isTransactionScrubberOffsetGenerationIndicator() {
        return transactionScrubberOffsetGenerationIndicator;
    }

    /**
     * Sets the transactionScrubberOffsetGenerationIndicator attribute value.
     * @param transactionScrubberOffsetGenerationIndicator The transactionScrubberOffsetGenerationIndicator to set.
     */
    public void setTransactionScrubberOffsetGenerationIndicator(boolean transactionScrubberOffsetGenerationIndicator) {
        this.transactionScrubberOffsetGenerationIndicator = transactionScrubberOffsetGenerationIndicator;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("inputTypeCode", this.inputTypeCode);
        m.put("documentTypeName", this.documentTypeName);
        m.put("activeDocumentType", this.documentTypeActiveIndicator);
        m.put("transactionScrubberOffsetGenerationIndicator", this.transactionScrubberOffsetGenerationIndicator);
        return m;
    }

}
