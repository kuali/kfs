/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
