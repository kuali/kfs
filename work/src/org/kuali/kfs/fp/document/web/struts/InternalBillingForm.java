/*
 * Copyright 2005 The Kuali Foundation
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
