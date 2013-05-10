/*
 * Copyright 2009 The Kuali Foundation.
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
