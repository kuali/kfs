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
package org.kuali.kfs.gl.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.lookup.LookupableSpringContext;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm;

/**
 * Balance inquiries are pretty much just lookups already, but are not used in the traditional sense. In most cases, balance
 * inquiries only show the end-user data, and allow the end-user to drill-down into inquiries. A traditional lookup allows the user
 * to return data to a form. This class is for balance inquiries implemented in the sense of a traditional lookup for forms that
 * pull data out of inquiries.<br/> <br/> One example of this is the
 * <code>{@link org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument}</code> which creates source lines from a labor
 * ledger balance inquiry screen.<br/> <br/> This is a <code>{@link KualiMultipleValueLookupAction}</code> which required some
 * customization because requirements were not possible with displaytag. There are a number of properties/attributes that are used
 * for pagination, formatting, etc...
 * 
 * @see org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument
 * @see org.kuali.kfs.module.ld.document.web.struts.SalaryExpenseTransferAction;
 * @see org.kuali.kfs.module.ld.document.web.struts.SalaryExpenseTransferForm;
 */
public class BalanceInquiryLookupForm extends MultipleValueLookupForm {
    private static final long serialVersionUID = 1L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceInquiryForm.class);

    private Lookupable pendingEntryLookupable;
    private LookupResultsSelectable selectable;
    private boolean segmented;

    public BalanceInquiryLookupForm() {
    }

    /**
     * Picks out business object name from the request to get retrieve a lookupable and set properties.
     * 
     * @param request <code>{@link javax.servlet.http.HttpServletRequest}</code> instance for Struts
     */
    @Override
    public void populate(HttpServletRequest request) {
        Lookupable localPendingEntryLookupable = null;

        super.populate(request);

        if (Entry.class.getName().equals(getBusinessObjectClassName())) {
            localPendingEntryLookupable = LookupableSpringContext.getLookupable(GeneralLedgerConstants.LookupableBeanKeys.PENDING_ENTRY);
        }

        if (localPendingEntryLookupable != null) {
            localPendingEntryLookupable.setBusinessObjectClass(GeneralLedgerPendingEntry.class);
            localPendingEntryLookupable.setFieldConversions(getFieldConversions());
        }
        setPendingEntryLookupable(localPendingEntryLookupable);
    }


    /**
     * @param pendingEntryLookupable
     */
    public void setPendingEntryLookupable(Lookupable pendingEntryLookupable) {
        this.pendingEntryLookupable = pendingEntryLookupable;
    }


    /**
     * @return Returns the pendingEntryLookupable.
     */
    public Lookupable getPendingEntryLookupable() {
        return this.pendingEntryLookupable;
    }

    /**
     * Determines if the balance inquiry lookup should be segmented or not
     * 
     * @return boolean
     */
    public boolean isSegmented() {
        return segmented;
    }

    /**
     * Tells the balance inquiry lookup whether to be segmented or not
     * 
     * @param seg
     */
    public void setSegmented(boolean seg) {
        segmented = seg;
    }
}
