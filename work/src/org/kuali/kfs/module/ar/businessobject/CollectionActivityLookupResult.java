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
package org.kuali.kfs.module.ar.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * Defines an entry in the Contracts & Grants Invoice Lookup Result.
 */
public class CollectionActivityLookupResult extends TransientBusinessObjectBase {


    private Long proposalNumber;
    private KualiDecimal paymentAmount = KualiDecimal.ZERO;
    private KualiDecimal balanceDue = KualiDecimal.ZERO;


    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }


    /**
     * Sets the proposalNumber attribute value.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    public KualiDecimal getPaymentAmount() {
        return paymentAmount;
    }


    public void setPaymentAmount(KualiDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }


    public KualiDecimal getBalanceDue() {
        return balanceDue;
    }


    public void setBalanceDue(KualiDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }


    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.proposalNumber != null) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        }
                return m;
    }

    public List<String> getAwardAttributesForDisplay() {
        List<String> awardAttributesForDisplay = new ArrayList<String>();
        awardAttributesForDisplay.add(KFSPropertyConstants.PROPOSAL_NUMBER);
        return awardAttributesForDisplay;
    }


}
