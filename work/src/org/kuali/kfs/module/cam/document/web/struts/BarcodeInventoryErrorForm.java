/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.web.struts;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;

/**
 * 
 * Action form for the asset barcode inventory error document
 */
public class BarcodeInventoryErrorForm extends FinancialSystemTransactionalDocumentFormBase {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorForm.class);

    protected int[] rowCheckbox; 
    protected boolean selectAllCheckbox;
    protected HashMap barcodeInventoryStatuses; 

    /**
     * 
     * Constructs a BarcodeInventoryErrorForm.java.
     */
    public BarcodeInventoryErrorForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "BCIE";
    }
    
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(this.getBarcodeInventoryErrorDocument());
    }

    /**
     *  @returns BCIE document 
     */
    public BarcodeInventoryErrorDocument getBarcodeInventoryErrorDocument() {
        return (BarcodeInventoryErrorDocument) getDocument();
    }

    /**
     * Get rowCheckbox
     * @return String
     */
    public int[] getRowCheckbox() {
        return rowCheckbox;
    }

    /**
     * Set rowCheckbox
     * @param <code>String</code>
     */
    public void setRowCheckbox(int r[]) {
        this.rowCheckbox = r;
    }


    public boolean isSelectAllCheckbox() {
        return selectAllCheckbox;
    }


    public void setSelectAllCheckbox(boolean selectAllCheckbox) {
        this.selectAllCheckbox = selectAllCheckbox;
    }

    /**
     * 
     * Reset the BCIE document checkboxes. 
     */
    public void resetCheckBoxes() {
        if (rowCheckbox == null)
            return;

        this.rowCheckbox = new int[rowCheckbox.length];
        this.selectAllCheckbox = false;
    }
}
