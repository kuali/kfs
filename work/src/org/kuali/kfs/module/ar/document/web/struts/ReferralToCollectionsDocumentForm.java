/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.web.struts;

import java.util.Collection;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsDetail;
import org.kuali.kfs.module.ar.document.ReferralToCollectionsDocument;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import java.util.ArrayList;

/**
 * Form class for Referral To Collections.
 */
public class ReferralToCollectionsDocumentForm extends FinancialSystemTransactionalDocumentFormBase {

    private String lookupResultsSequenceNumber;
    private Collection<ReferralToCollectionsDetail> referralToCollectionsDetails;

    /**
     * Default constructor.
     */
    public ReferralToCollectionsDocumentForm() {
        referralToCollectionsDetails = new ArrayList<ReferralToCollectionsDetail>();
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return ArPropertyConstants.REFERRAL_TO_COLL_DOC_TYPE;
    }

    /**
     * Gets the referralToCollectionsDocument attribute.
     * 
     * @return Returns the referralToCollectionsDocument attribute.
     */
    public ReferralToCollectionsDocument getReferralToCollectionsDocument() {
        return (ReferralToCollectionsDocument) getDocument();
    }

    /**
     * Gets the lookupResultsSequenceNumber attribute.
     * 
     * @return Returns the lookupResultsSequenceNumber attribute.
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
     * Gets the collection referralToCollectionsDetail.
     * 
     * @return Returns the collection referralToCollectionsDetails.
     */
    public Collection<ReferralToCollectionsDetail> getReferralToCollectionsDetails() {
        return referralToCollectionsDetails;
    }

    /**
     * Sets the referralToCollectionsDetails attribute.
     * 
     * @param referralToCollectionsDetails The referralToCollectionsDetails collection to set.
     */
    public void setReferralToCollectionsDetails(Collection<ReferralToCollectionsDetail> referralToCollectionsDetails) {
        this.referralToCollectionsDetails = referralToCollectionsDetails;
    }
}
