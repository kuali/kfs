/*
 * Copyright 2011 The Kuali Foundation.
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
