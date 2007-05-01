/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.web.struts.form;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.struts.form.MultipleValueLookupForm;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.Entry;

/**
 * Balance inquiries are pretty much just lookups already, but are not used in the traditional sense. In most
 * cases, balance inquiries only show the end-user data, and allow the end-user to drill-down into inquiries. A
 * traditional lookup allows the user to return data to a form. This class is for balance inquiries implemented
 * in the sense of a traditional lookup for forms that pull data out of inquiries.<br/>
 * <br/>
 * One example of this is the <code>{@link org.kuali.module.labor.document.SalaryExpenseTransferDocument}</code>
 * which creates source lines from a labor ledger balance inquiry screen.<br/>
 * <br/>
 * This is a <code>{@link KualiMultipleValueLookupAction}</code> which required some customization because requirements
 * were not possible with displaytag. There are a number of properties/attributes that are used for pagination, formatting,
 * etc...
 *
 * @see org.kuali.module.labor.document.SalaryExpenseTransferDocument
 * @see org.kuali.module.labor.web.struts.action.SalaryExpenseTransferAction;
 * @see org.kuali.module.labor.web.struts.form.SalaryExpenseTransferForm;
 */
public class BalanceInquiryLookupForm extends LookupForm {
    private static final long serialVersionUID = 1L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceInquiryForm.class);

    private Lookupable pendingEntryLookupable;
    private LookupResultsSelectable selectable;

    public BalanceInquiryLookupForm() {
        selectable = new BalanceInquiryLookupResults();
    }

    /**
     * Picks out business object name from the request to get retrieve a lookupable and set properties.
     *
     * @param request <code>{@link javax.servlet.http.HttpServletRequest}</code> instance for Struts
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        Lookupable localPendingEntryLookupable = null;
        
        if (Entry.class.getName().equals(getBusinessObjectClassName())) {
            localPendingEntryLookupable = SpringServiceLocator.getLookupable(GLConstants.LookupableBeanKeys.PENDING_ENTRY);
            }
        
        if (localPendingEntryLookupable != null) {
            localPendingEntryLookupable.setBusinessObjectClass(GeneralLedgerPendingEntry.class);
            localPendingEntryLookupable.setFieldConversions(getFieldConversions());
        }
        setPendingEntryLookupable(localPendingEntryLookupable);

        getLookupResultsSelectable().populate(request);
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
     * Retrieve the selectable
     *
     * @return LookupResultsSelectable
     */
    public LookupResultsSelectable getLookupResultsSelectable() {
        return selectable;
    }

    
    /**
     * Assign selectable
     *
     * @param sel the <code>{@link LookupResultsSelectable}</code> for this form.
     */
    public void setLookupResultsSelectable(LookupResultsSelectable sel) {
        selectable = sel;
    }

}
