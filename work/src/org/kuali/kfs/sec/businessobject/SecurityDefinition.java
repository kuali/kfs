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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Defines a restriction that can be given to a model or principal. A restriction defines the attribute that is restricted on, and the action(s) that are being restricted. A KIM
 * permission and role is created from a definition record
 */
public class SecurityDefinition extends PersistableBusinessObjectBase implements MutableInactivatable {
    private KualiInteger id;
    private String name;
    private String description;
    private String roleId;
    private KualiInteger attributeId;
    private boolean restrictViewAccountingLine;
    private boolean restrictEditAccountingLine;
    private boolean restrictViewDocument;
    private boolean restrictEditDocument;
    private boolean restrictViewNotesAndAttachments;
    private boolean restrictLookup;
    private boolean restrictGLInquiry;
    private boolean restrictLaborInquiry;
    private boolean active;

    private SecurityAttribute securityAttribute;

    private List<SecurityDefinitionDocumentType> definitionDocumentTypes;

    public SecurityDefinition() {
        super();

        definitionDocumentTypes = new ArrayList<SecurityDefinitionDocumentType>();

        restrictViewAccountingLine = false;
        restrictEditAccountingLine = false;
        restrictViewDocument = false;
        restrictEditDocument = false;
        restrictViewNotesAndAttachments = false;
        restrictLookup = false;
        restrictGLInquiry = false;
        restrictLaborInquiry = false;
    }

    /**
     * Gets the id attribute.
     * 
     * @return Returns the id.
     */
    public KualiInteger getId() {
        return id;
    }


    /**
     * Sets the id attribute value.
     * 
     * @param id The id to set.
     */
    public void setId(KualiInteger id) {
        this.id = id;
    }


    /**
     * Gets the name attribute.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the name attribute value.
     * 
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Gets the description attribute.
     * 
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }


    /**
     * Sets the description attribute value.
     * 
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Gets the roleId attribute.
     * 
     * @return Returns the roleId.
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * Sets the roleId attribute value.
     * 
     * @param roleId The roleId to set.
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets the attributeId attribute.
     * 
     * @return Returns the attributeId.
     */
    public KualiInteger getAttributeId() {
        return attributeId;
    }


    /**
     * Sets the attributeId attribute value.
     * 
     * @param attributeId The attributeId to set.
     */
    public void setAttributeId(KualiInteger attributeId) {
        this.attributeId = attributeId;
    }


    /**
     * Gets the restrictViewAccountingLine attribute.
     * 
     * @return Returns the restrictViewAccountingLine.
     */
    public boolean isRestrictViewAccountingLine() {
        return restrictViewAccountingLine;
    }


    /**
     * Sets the restrictViewAccountingLine attribute value.
     * 
     * @param restrictViewAccountingLine The restrictViewAccountingLine to set.
     */
    public void setRestrictViewAccountingLine(boolean restrictViewAccountingLine) {
        this.restrictViewAccountingLine = restrictViewAccountingLine;
    }


    /**
     * Gets the restrictEditAccountingLine attribute.
     * 
     * @return Returns the restrictEditAccountingLine.
     */
    public boolean isRestrictEditAccountingLine() {
        return restrictEditAccountingLine;
    }


    /**
     * Sets the restrictEditAccountingLine attribute value.
     * 
     * @param restrictEditAccountingLine The restrictEditAccountingLine to set.
     */
    public void setRestrictEditAccountingLine(boolean restrictEditAccountingLine) {
        this.restrictEditAccountingLine = restrictEditAccountingLine;
    }


    /**
     * Gets the restrictViewDocument attribute.
     * 
     * @return Returns the restrictViewDocument.
     */
    public boolean isRestrictViewDocument() {
        return restrictViewDocument;
    }


    /**
     * Sets the restrictViewDocument attribute value.
     * 
     * @param restrictViewDocument The restrictViewDocument to set.
     */
    public void setRestrictViewDocument(boolean restrictViewDocument) {
        this.restrictViewDocument = restrictViewDocument;
    }


    /**
     * Gets the restrictViewNotesAndAttachments attribute.
     * 
     * @return Returns the restrictViewNotesAndAttachments.
     */
    public boolean isRestrictViewNotesAndAttachments() {
        return restrictViewNotesAndAttachments;
    }


    /**
     * Sets the restrictViewNotesAndAttachments attribute value.
     * 
     * @param restrictViewNotesAndAttachments The restrictViewNotesAndAttachments to set.
     */
    public void setRestrictViewNotesAndAttachments(boolean restrictViewNotesAndAttachments) {
        this.restrictViewNotesAndAttachments = restrictViewNotesAndAttachments;
    }


    /**
     * Gets the restrictLookup attribute.
     * 
     * @return Returns the restrictLookup.
     */
    public boolean isRestrictLookup() {
        return restrictLookup;
    }


    /**
     * Sets the restrictLookup attribute value.
     * 
     * @param restrictLookup The restrictLookup to set.
     */
    public void setRestrictLookup(boolean restrictLookup) {
        this.restrictLookup = restrictLookup;
    }


    /**
     * Gets the restrictGLInquiry attribute.
     * 
     * @return Returns the restrictGLInquiry.
     */
    public boolean isRestrictGLInquiry() {
        return restrictGLInquiry;
    }


    /**
     * Sets the restrictGLInquiry attribute value.
     * 
     * @param restrictGLInquiry The restrictGLInquiry to set.
     */
    public void setRestrictGLInquiry(boolean restrictGLInquiry) {
        this.restrictGLInquiry = restrictGLInquiry;
    }


    /**
     * Gets the restrictLaborInquiry attribute.
     * 
     * @return Returns the restrictLaborInquiry.
     */
    public boolean isRestrictLaborInquiry() {
        return restrictLaborInquiry;
    }


    /**
     * Sets the restrictLaborInquiry attribute value.
     * 
     * @param restrictLaborInquiry The restrictLaborInquiry to set.
     */
    public void setRestrictLaborInquiry(boolean restrictLaborInquiry) {
        this.restrictLaborInquiry = restrictLaborInquiry;
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
     * Gets the securityAttribute attribute.
     * 
     * @return Returns the securityAttribute.
     */
    public SecurityAttribute getSecurityAttribute() {
        return securityAttribute;
    }


    /**
     * Sets the securityAttribute attribute value.
     * 
     * @param securityAttribute The securityAttribute to set.
     */
    public void setSecurityAttribute(SecurityAttribute securityAttribute) {
        this.securityAttribute = securityAttribute;
    }


    /**
     * Gets the restrictEditDocument attribute.
     * 
     * @return Returns the restrictEditDocument.
     */
    public boolean isRestrictEditDocument() {
        return restrictEditDocument;
    }


    /**
     * Sets the restrictEditDocument attribute value.
     * 
     * @param restrictEditDocument The restrictEditDocument to set.
     */
    public void setRestrictEditDocument(boolean restrictEditDocument) {
        this.restrictEditDocument = restrictEditDocument;
    }


    /**
     * Gets the definitionDocumentTypes attribute.
     * 
     * @return Returns the definitionDocumentTypes.
     */
    public List<SecurityDefinitionDocumentType> getDefinitionDocumentTypes() {
        return definitionDocumentTypes;
    }


    /**
     * Sets the definitionDocumentTypes attribute value.
     * 
     * @param definitionDocumentTypes The definitionDocumentTypes to set.
     */
    public void setDefinitionDocumentTypes(List<SecurityDefinitionDocumentType> definitionDocumentTypes) {
        this.definitionDocumentTypes = definitionDocumentTypes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SecurityDefinition [");
        if (id != null) {
            builder.append("id=");
            builder.append(id);
            builder.append(", ");
        }
        if (name != null) {
            builder.append("name=");
            builder.append(name);
            builder.append(", ");
        }
        if (description != null) {
            builder.append("description=");
            builder.append(description);
            builder.append(", ");
        }
        if (roleId != null) {
            builder.append("roleId=");
            builder.append(roleId);
            builder.append(", ");
        }
        if (attributeId != null) {
            builder.append("attributeId=");
            builder.append(attributeId);
            builder.append(", ");
        }
        builder.append("restrictViewAccountingLine=");
        builder.append(restrictViewAccountingLine);
        builder.append(", restrictEditAccountingLine=");
        builder.append(restrictEditAccountingLine);
        builder.append(", restrictViewDocument=");
        builder.append(restrictViewDocument);
        builder.append(", restrictEditDocument=");
        builder.append(restrictEditDocument);
        builder.append(", restrictViewNotesAndAttachments=");
        builder.append(restrictViewNotesAndAttachments);
        builder.append(", restrictLookup=");
        builder.append(restrictLookup);
        builder.append(", restrictGLInquiry=");
        builder.append(restrictGLInquiry);
        builder.append(", restrictLaborInquiry=");
        builder.append(restrictLaborInquiry);
        builder.append(", active=");
        builder.append(active);
        builder.append(", ");
        if (securityAttribute != null) {
            builder.append("securityAttribute=");
            builder.append(securityAttribute);
            builder.append(", ");
        }
        if (definitionDocumentTypes != null) {
            builder.append("definitionDocumentTypes=");
            builder.append(definitionDocumentTypes);
        }
        builder.append("]");
        return builder.toString();
    }


}
