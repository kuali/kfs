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
