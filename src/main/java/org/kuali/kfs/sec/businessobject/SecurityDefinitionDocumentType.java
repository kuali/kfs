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
package org.kuali.kfs.sec.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


/**
 * Associates a document type to a security definition
 */
public class SecurityDefinitionDocumentType extends PersistableBusinessObjectBase implements MutableInactivatable {
    private KualiInteger definitionId;
    private String financialSystemDocumentTypeCode;
    private boolean active;

    private transient DocumentTypeEBO financialSystemDocumentType;

    public SecurityDefinitionDocumentType() {
        super();
    }


    /**
     * Gets the definitionId attribute.
     *
     * @return Returns the definitionId.
     */
    public KualiInteger getDefinitionId() {
        return definitionId;
    }


    /**
     * Sets the definitionId attribute value.
     *
     * @param definitionId The definitionId to set.
     */
    public void setDefinitionId(KualiInteger definitionId) {
        this.definitionId = definitionId;
    }


    /**
     * Gets the financialSystemDocumentTypeCode attribute.
     *
     * @return Returns the financialSystemDocumentTypeCode.
     */
    public String getFinancialSystemDocumentTypeCode() {
        return financialSystemDocumentTypeCode;
    }


    /**
     * Sets the financialSystemDocumentTypeCode attribute value.
     *
     * @param financialSystemDocumentTypeCode The financialSystemDocumentTypeCode to set.
     */
    public void setFinancialSystemDocumentTypeCode(String financialSystemDocumentTypeCode) {
        this.financialSystemDocumentTypeCode = financialSystemDocumentTypeCode;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the financialSystemDocumentType attribute.
     *
     * @return Returns the financialSystemDocumentType.
     */
    public DocumentTypeEBO getFinancialSystemDocumentType() {
        if ( StringUtils.isBlank( financialSystemDocumentTypeCode ) ) {
            financialSystemDocumentType = null;
        } else {
            if ( financialSystemDocumentType == null || !StringUtils.equals(financialSystemDocumentTypeCode, financialSystemDocumentType.getName() ) ) {
                org.kuali.rice.kew.api.doctype.DocumentType temp = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(financialSystemDocumentTypeCode);
                if ( temp != null ) {
                    financialSystemDocumentType = DocumentType.from( temp );
                } else {
                    financialSystemDocumentType = null;
                }
            }
        }
        return financialSystemDocumentType;
    }


    /**
     * Sets the financialSystemDocumentType attribute value.
     *
     * @param financialSystemDocumentType The financialSystemDocumentType to set.
     */
    public void setFinancialSystemDocumentType(DocumentTypeEBO financialSystemDocumentType) {
        this.financialSystemDocumentType = financialSystemDocumentType;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(SecPropertyConstants.DEFINITION_ID, this.definitionId);
        m.put(KFSPropertyConstants.DOCUMENT_TYPE_CODE, this.financialSystemDocumentTypeCode);

        return m;
    }

}
