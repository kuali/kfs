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

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class is the struts base form for financial form documents that need
 * capital accounting lines.
 */
public abstract class CapitalAssetInformationFormBase extends KualiAccountingDocumentFormBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiAccountingDocumentActionBase.class);

    // Indicates which result set we are using when refreshing/returning from a multi-value lookup.
    protected String lookupResultsSequenceNumber;

    // Type of result returned by the multi-value lookup. ?to be persisted in the lookup results service instead?
    protected String lookupResultsBOClassName;

    // The name of the collection looked up (by a multiple value lookup)
    protected String lookedUpCollectionName;

    /**
     * Constructs a AdvanceDepositForm.java.
     */
    public CapitalAssetInformationFormBase() {
        super();
    }
   
    /**
     * Gets the lookupResultsSequenceNumber attribute.
     * 
     * @return Returns the lookupResultsSequenceNumber
     */
    
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    /** 
     * Sets the lookupResultsSequenceNumber attribute.
     * 
     * @param lookupResultsSequenceNumber The lookupResultsSequenceNumber to set.
     */
    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    /**
     * Gets the lookupResultsBOClassName attribute.
     * 
     * @return Returns the lookupResultsBOClassName
     */
    
    public String getLookupResultsBOClassName() {
        return lookupResultsBOClassName;
    }

    /** 
     * Sets the lookupResultsBOClassName attribute.
     * 
     * @param lookupResultsBOClassName The lookupResultsBOClassName to set.
     */
    public void setLookupResultsBOClassName(String lookupResultsBOClassName) {
        this.lookupResultsBOClassName = lookupResultsBOClassName;
    }

    /**
     * Gets the lookedUpCollectionName attribute.
     * 
     * @return Returns the lookedUpCollectionName
     */
    
    public String getLookedUpCollectionName() {
        return lookedUpCollectionName;
    }

    /** 
     * Sets the lookedUpCollectionName attribute.
     * 
     * @param lookedUpCollectionName The lookedUpCollectionName to set.
     */
    public void setLookedUpCollectionName(String lookedUpCollectionName) {
        this.lookedUpCollectionName = lookedUpCollectionName;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.AccountingDocumentFormBase#getRefreshCaller()
     */
    @Override
    public String getRefreshCaller() {
        return KFSConstants.MULTIPLE_VALUE;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#addRequiredNonEditableProperties()
     */
    @Override
    public void addRequiredNonEditableProperties() {
        super.addRequiredNonEditableProperties();
        registerRequiredNonEditableProperty(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER);
    }
}
