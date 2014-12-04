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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.InternalBillingItem;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.fp.document.InternalBillingDocument;

/**
 * This class is the action form for Internal Billing.
 */
public class InternalBillingForm extends CapitalAccountingLinesFormBase implements CapitalAssetEditable{
    protected static final long serialVersionUID = 1L;
    protected InternalBillingItem newItem;
    
    protected List<CapitalAssetInformation> capitalAssetInformation;

    public InternalBillingForm() {
        super();
        
        this.newItem = new InternalBillingItem();
        capitalAssetInformation = new ArrayList<CapitalAssetInformation>();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "IB";
    }
    
    /**
     * @return Returns the internalBillingDocument.
     */
    public InternalBillingDocument getInternalBillingDocument() {
        return (InternalBillingDocument) getDocument();
    }

    /**
     * @param internalBillingDocument The internalBillingDocument to set.
     */
    public void setInternalBillingDocument(InternalBillingDocument internalBillingDocument) {
        setDocument(internalBillingDocument);
    }

    /**
     * @return Returns the newItem.
     */
    public InternalBillingItem getNewItem() {
        return newItem;
    }

    /**
     * @param newItem The newItem to set.
     */
    public void setNewItem(InternalBillingItem newItem) {
        this.newItem = newItem;
    }
    
    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#getCapitalAssetInformation()
     */
    public List<CapitalAssetInformation> getCapitalAssetInformation() {
        return this.capitalAssetInformation;
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#setCapitalAssetInformation(org.kuali.kfs.fp.businessobject.CapitalAssetInformation)
     */
    public void setCapitalAssetInformation(List<CapitalAssetInformation> capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;        
    }
}
