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
package org.kuali.kfs.module.ar.document.web.struts;

import java.util.ArrayList;
import java.util.Collection;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsDetail;
import org.kuali.kfs.module.ar.document.ReferralToCollectionsDocument;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;

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
        return ArConstants.ArDocumentTypeCodes.REFERRAL_TO_COLLECTIONS;
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
