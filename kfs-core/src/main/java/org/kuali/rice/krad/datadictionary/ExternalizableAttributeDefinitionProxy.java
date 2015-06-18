/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary;

/**
 * A single attribute definition in the DataDictionary, which contains information relating to the display, validation, and general
 * maintenance of a specific attribute of an entry.
 *
 *
 */
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.datadictionary.exception.CompletionException;
import org.kuali.rice.krad.datadictionary.validation.ValidationPattern;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

/**
 * A single attribute definition in the DataDictionary, which contains
 * information relating to the display, validation, and general maintenance of a
 * specific attribute of an entry.
 *
 *
 */
public class ExternalizableAttributeDefinitionProxy extends AttributeDefinition {
    private static final long serialVersionUID = -3204870440281417429L;

    // logger
    private static Log LOG = LogFactory
            .getLog(ExternalizableAttributeDefinitionProxy.class);

    private String sourceExternalizableBusinessObjectInterface;
    private String sourceAttributeName;
    @JsonIgnore
    private AttributeDefinition delegate;

    /**
     * Constructs an AttributeReferenceDefinition
     */
    public ExternalizableAttributeDefinitionProxy() {
        LOG.debug("creating new ExternalizableAttributeDefinitionProxy");
    }

    public void setSourceExternalizableBusinessObjectInterface(
            String sourceClassName) {
        if (StringUtils.isBlank(sourceClassName)) {
            throw new IllegalArgumentException(
                    "invalid (blank) sourceClassName");
        }

        this.sourceExternalizableBusinessObjectInterface = sourceClassName;
    }

    public String getSourceExternalizableBusinessObjectInterface() {
        return this.sourceExternalizableBusinessObjectInterface;
    }

    public void setSourceAttributeName(String sourceAttributeName) {
        if (StringUtils.isBlank(sourceAttributeName)) {
            throw new IllegalArgumentException(
                    "invalid (blank) sourceAttributeName");
        }

        this.sourceAttributeName = sourceAttributeName;
    }

    public String getSourceAttributeName() {
        return this.sourceAttributeName;
    }

    /**
     * @return AttributeDefinition acting as delegate for this
     *         AttributeReferenceDefinition
     */
    AttributeDefinition getDelegate() {
        BusinessObjectEntry delegateEntry = null;
        if ( delegate == null ) {
            try {
                delegateEntry = KRADServiceLocatorWeb
                        .getKualiModuleService()
                        .getResponsibleModuleService(
                                Class
                                        .forName(getSourceExternalizableBusinessObjectInterface()))
                        .getExternalizableBusinessObjectDictionaryEntry(
                                Class
                                        .forName(getSourceExternalizableBusinessObjectInterface()));
            } catch (ClassNotFoundException e) {
                LOG.error("Unable to get delegate entry for sourceExternalizableBusinessObjectInterface",e);
            }

            if (delegateEntry == null) {
                throw new CompletionException(
                        "no BusinessObjectEntry exists for sourceClassName '"
                                + getSourceExternalizableBusinessObjectInterface()
                                + "'");
            }
            delegate = delegateEntry
                    .getAttributeDefinition(getSourceAttributeName());
            if (delegate == null) {
                throw new CompletionException(
                        "no AttributeDefnintion exists for sourceAttributeName '"
                                + getSourceExternalizableBusinessObjectInterface()
                                + "." + getSourceAttributeName() + "'");
            }
        }

        return delegate;
    }

    /**
     * Sets the given AttributeDefinition as the delegate for this instance
     *
     * @param delegate
     */
    void setDelegate(AttributeDefinition delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("invalid (null) delegate");
        }

        this.delegate = delegate;
    }

    /**
     * If forceUppercase wasn't set on this instance, use the value from its
     * delegate.
     *
     * @see org.kuali.core.datadictionary.AttributeDefinition#getForceUppercase()
     */
    public Boolean getForceUppercase() {
        Boolean value = super.getForceUppercase();
        if (value == null) {
            value = getDelegate().getForceUppercase();
        }

        return value;
    }

    /**
     * If name wasn't set on this instance, use the value from its delegate.
     *
     * @see org.kuali.core.datadictionary.AttributeDefinition#getName()
     */
    public String getName() {
        String name = super.getName();
        if (name == null) {
            name = getDelegate().getName();
        }

        return name;
    }

    /**
     * If label wasn't set on this instance, use the value from its delegate.
     *
     * @see org.kuali.core.datadictionary.AttributeDefinition#getLabel()
     */
    public String getLabel() {
        String label = super.getLabel();

        if (label == null) {
            label = getDelegate().getLabel();
        }

        return label;
    }

    /**
     * If shortlabel wasn't set on this instance, use the value from its
     * delegate.
     *
     * @see org.kuali.core.datadictionary.AttributeDefinition#getShortLabel()
     */
    public String getShortLabel() {
        String shortLabel = super.getDirectShortLabel();
        if (shortLabel == null) {
            shortLabel = getDelegate().getShortLabel();
        }

        return shortLabel;
    }

    /**
     * If maxLength wasn't set on this instance, use the value from its
     * delegate.
     *
     * @see org.kuali.core.datadictionary.AttributeDefinition#getMaxLength()
     */
    public Integer getMaxLength() {
        Integer maxLength = super.getMaxLength();
        if (maxLength == null) {
            maxLength = getDelegate().getMaxLength();
        }

        return maxLength;
    }

    /**
     * @return true if a validationPattern is available, directly or indirectly
     *
     * @see org.kuali.core.datadictionary.AttributeDefinition#hasValidationPattern()
     */
    public boolean hasValidationPattern() {
        return (getValidationPattern() != null);
    }

    /**
     * If validationPattern wasn't set on this instance, use the value from its
     * delegate.
     *
     * @see org.kuali.core.datadictionary.AttributeDefinition#getValidationPattern()
     */
    public ValidationPattern getValidationPattern() {
        ValidationPattern validationPattern = super.getValidationPattern();
        if (validationPattern == null) {
            validationPattern = getDelegate().getValidationPattern();
        }

        return validationPattern;
    }

    /**
     * If required wasn't set on this instance, use the value from its delegate.
     *
     * @see org.kuali.core.datadictionary.AttributeDefinition#isRequired()
     */
    public Boolean isRequired() {
        Boolean required = super.isRequired();
        if (required == null) {
            required = getDelegate().isRequired();
        }

        return required;
    }

    /**
     * If control wasn't set on this instance, use the value from its delegate.
     *
     * @see org.kuali.core.datadictionary.AttributeDefinition#getControl()
     */
    public ControlDefinition getControl() {
        ControlDefinition control = super.getControl();
        if (control == null) {
            control = getDelegate().getControl();
        }

        return control;
    }

    /**
     * If summary wasn't set on this instance, use the value from its delegate.
     *
     * @see org.kuali.core.datadictionary.AttributeDefinition#getSummary()
     */
    public String getSummary() {
        String summary = super.getSummary();
        if (summary == null) {
            summary = getDelegate().getSummary();
        }

        return summary;
    }

    /**
     * If description wasn't set on this instance, use the value from its
     * delegate.
     *
     * @see org.kuali.core.datadictionary.AttributeDefinition#getDescription()
     */
    public String getDescription() {
        String description = super.getDescription();
        if (description == null) {
            description = getDelegate().getDescription();
        }

        return description;
    }

    /**
     * @return true if a formatterClass is available, directly or indirectly
     *
     * @see org.kuali.core.datadictionary.AttributeDefinition#hasFormatterClass()
     */
    public boolean hasFormatterClass() {
        return (getFormatterClass() != null);
    }

    /**
     * If a formatterClass wasn't set for this instance, use the value from its
     * delegate.
     *
     * @see org.kuali.core.datadictionary.AttributeDefinition#getFormatterClass()
     */
    public String getFormatterClass() {
        String formatterClass = super.getFormatterClass();
        if (formatterClass == null) {
            formatterClass = getDelegate().getFormatterClass();
        }

        return formatterClass;
    }

    /**
     * @see org.kuali.core.datadictionary.AttributeDefinition#getDisplayLabelAttribute()
     */
    @Override
    public String getDisplayLabelAttribute() {
        String displayLabelAttribute = super.getDisplayLabelAttribute();
        if (StringUtils.isBlank(displayLabelAttribute)) {
            displayLabelAttribute = getDelegate().getDisplayLabelAttribute();
        }
        return displayLabelAttribute;
    }

    /**
     * Validate the fields associated with locating the delegate. Other
     * validation must be deferred until the delegate class has been assigned.
     *
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#completeValidation()
     */
    @Override
    public void completeValidation(Class rootObjectClass, Class otherObjectClass) {
        if (StringUtils.isBlank(sourceExternalizableBusinessObjectInterface)) {
            throw new IllegalArgumentException(
                    "invalid (blank) sourceClassName for attribute '"
                            + rootObjectClass.getName() + "." + getName() + "'");
        }
        if (StringUtils.isBlank(sourceAttributeName)) {
            throw new IllegalArgumentException(
                    "invalid (blank) sourceAttributeName for attribute '"
                            + rootObjectClass.getName() + "." + getName() + "'");
        }
        if ( DataDictionary.validateEBOs ) {
            getDelegate(); // forces validation
            super.completeValidation(rootObjectClass, otherObjectClass);
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String name = super.getName();

        // workaround for the mysterious,
        // still-unreproducible-on-my-machine, null delegate exception on
        // Tomcat startup
        if ((name == null) && (getDelegate() != null)) {
            name = getDelegate().getName();
        }
        return "AttributeReferenceDefinition for attribute " + name;
    }
}
