/*
 * Copyright 2007 The Kuali Foundation
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
