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
package org.kuali.kfs.module.cam.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 *
 * This class is to store document type use by the asset global document - Asset Payment tab.
 */

public class AssetPaymentDocumentType extends PersistableBusinessObjectBase implements MutableInactivatable {
    private Long documentTypeId;
    private String expenditureFinancialDocumentTypeCode;
    private String label;
    private boolean active;

    private DocumentTypeEBO financialSystemDocumentTypeCode;



    public Long getDocumentTypeId() {
        return documentTypeId;
    }




    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }






    public String getExpenditureFinancialDocumentTypeCode() {
        return expenditureFinancialDocumentTypeCode;
    }




    public void setExpenditureFinancialDocumentTypeCode(String expenditureFinancialDocumentTypeCode) {
        this.expenditureFinancialDocumentTypeCode = expenditureFinancialDocumentTypeCode;
    }




    public String getLabel() {
        if(!ObjectUtils.isNull(getFinancialSystemDocumentTypeCode())) {
            return  getFinancialSystemDocumentTypeCode().getLabel();
        }
        return "";
    }




    public void setLabel(String label) {
        this.label = label;
    }


    @Override
    public boolean isActive() {
        return active;
    }


    @Override
    public void setActive(boolean active) {
        this.active = active;
    }




    /**
     * Gets the financialSystemDocumentTypeCode attribute.
     * @return Returns the financialSystemDocumentTypeCode.
     */
    public DocumentTypeEBO getFinancialSystemDocumentTypeCode() {
        if ( financialSystemDocumentTypeCode == null || !StringUtils.equals(financialSystemDocumentTypeCode.getName(), expenditureFinancialDocumentTypeCode) ) {
            financialSystemDocumentTypeCode = null;
            if ( StringUtils.isNotBlank(expenditureFinancialDocumentTypeCode) ) {
                DocumentType docType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(expenditureFinancialDocumentTypeCode);
                if ( docType != null ) {
                    financialSystemDocumentTypeCode = org.kuali.rice.kew.doctype.bo.DocumentType.from(docType);
                }
            }
        }
        return financialSystemDocumentTypeCode;
    }

}
