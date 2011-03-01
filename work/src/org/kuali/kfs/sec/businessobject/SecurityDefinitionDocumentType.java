/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sec.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.service.impl.KEWModuleService;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiInteger;


/**
 * Associates a document type to a security definition
 */
public class SecurityDefinitionDocumentType extends PersistableBusinessObjectBase implements Inactivateable {
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
        return financialSystemDocumentType = SpringContext.getBean(KEWModuleService.class).retrieveExternalizableBusinessObjectIfNecessary(this, financialSystemDocumentType, "financialSystemDocumentType");
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
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(SecPropertyConstants.DEFINITION_ID, this.definitionId);
        m.put(KFSPropertyConstants.DOCUMENT_TYPE_CODE, this.financialSystemDocumentTypeCode);

        return m;
    }

}
