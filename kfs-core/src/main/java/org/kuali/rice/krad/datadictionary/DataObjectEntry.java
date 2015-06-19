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

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.kuali.rice.krad.bo.Exporter;
import org.kuali.rice.krad.datadictionary.InactivationBlockingDefinition;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.validation.capability.MustOccurConstrainable;
import org.kuali.rice.krad.datadictionary.validation.constraint.MustOccurConstraint;

import java.util.List;

/**
 * Generic dictionary entry for an object that does not have to implement BusinessObject. It provides support
 * for general objects.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataObjectEntry extends DataDictionaryEntryBase implements MustOccurConstrainable {

    protected String name;
    protected Class<?> dataObjectClass;

    protected String titleAttribute;
    protected String objectLabel;
    protected String objectDescription;

    protected List<String> primaryKeys;
    protected Class<? extends Exporter> exporterClass;

    protected List<MustOccurConstraint> mustOccurConstraints;

    protected List<String> groupByAttributesForEffectiveDating;

    protected HelpDefinition helpDefinition;


    protected boolean boNotesEnabled = false;

    protected List<InactivationBlockingDefinition> inactivationBlockingDefinitions;

    @Override
    public void completeValidation() {
        //KFSMI-1340 - Object label should never be blank
        if (StringUtils.isBlank(getObjectLabel())) {
            throw new AttributeValidationException("Object label cannot be blank for class " + dataObjectClass.getName());
        }

        super.completeValidation();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntry#getJstlKey()
     */
    @Override
    @JsonIgnore
    public String getJstlKey() {
        if (dataObjectClass == null) {
            throw new IllegalStateException("cannot generate JSTL key: dataObjectClass is null");
        }

        return (dataObjectClass != null) ? dataObjectClass.getSimpleName() : dataObjectClass.getSimpleName();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntry#getFullClassName()
     */
    @Override
    @JsonIgnore
    public String getFullClassName() {
        return dataObjectClass.getName();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntryBase#getEntryClass()
     */
    @Override
    @JsonIgnore
    public Class<?> getEntryClass() {
        return dataObjectClass;
    }

    /**
     * @return the dataObjectClass
     */
    public Class<?> getDataObjectClass() {
        return this.dataObjectClass;
    }

    /**
     * @param dataObjectClass the dataObjectClass to set
     */
    public void setDataObjectClass(Class<?> dataObjectClass) {
        this.dataObjectClass = dataObjectClass;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the objectLabel.
     */
    public String getObjectLabel() {
        return objectLabel;
    }

    /**
     * The objectLabel provides a short name of the business
     * object for use on help screens.
     *
     * @param objectLabel The objectLabel to set.
     */
    public void setObjectLabel(String objectLabel) {
        this.objectLabel = objectLabel;
    }

    /**
     * @return Returns the description.
     */
    public String getObjectDescription() {
        return objectDescription;
    }

    /**
     * The objectDescription provides a brief description
     * of the business object for use on help screens.
     *
     * @param description The description to set.
     */
    public void setObjectDescription(String objectDescription) {
        this.objectDescription = objectDescription;
    }

    /**
     * Gets the helpDefinition attribute.
     *
     * @return Returns the helpDefinition.
     */
    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    /**
     * Sets the helpDefinition attribute value.
     *
     * The objectHelp element provides the keys to
     * obtain a help description from the system parameters table.
     *
     * parameterNamespace the namespace of the parameter containing help information
     * parameterName the name of the parameter containing help information
     * parameterDetailType the detail type of the parameter containing help information
     *
     * @param helpDefinition The helpDefinition to set.
     */
    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DataObjectEntry for " + getDataObjectClass();
    }

    /**
     * @return the mustOccurConstraints
     */
    public List<MustOccurConstraint> getMustOccurConstraints() {
        return this.mustOccurConstraints;
    }

    /**
     * @param mustOccurConstraints the mustOccurConstraints to set
     */
    public void setMustOccurConstraints(List<MustOccurConstraint> mustOccurConstraints) {
        this.mustOccurConstraints = mustOccurConstraints;
    }

    /**
     * @return the titleAttribute
     */
    public String getTitleAttribute() {
        return this.titleAttribute;
    }

    /**
     * The titleAttribute element is the name of the attribute that
     * will be used as an inquiry field when the lookup search results
     * fields are displayed.
     *
     * For some business objects, there is no obvious field to serve
     * as the inquiry field. in that case a special field may be required
     * for inquiry purposes.
     */
    public void setTitleAttribute(String titleAttribute) {
        this.titleAttribute = titleAttribute;
    }

    /**
     * @return the primaryKeys
     */
    public List<String> getPrimaryKeys() {
        return this.primaryKeys;
    }

    /**
     * @param primaryKeys the primaryKeys to set
     */
    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public Class<? extends Exporter> getExporterClass() {
        return this.exporterClass;
    }

    public void setExporterClass(Class<? extends Exporter> exporterClass) {
        this.exporterClass = exporterClass;
    }

    /**
     * Provides list of attributes that should be used for grouping
     * when performing effective dating logic in the framework
     *
     * @return List<String> list of attributes to group by
     */
    public List<String> getGroupByAttributesForEffectiveDating() {
        return this.groupByAttributesForEffectiveDating;
    }

    /**
     * Setter for the list of attributes to group by
     *
     * @param groupByAttributesForEffectiveDating
     */
    public void setGroupByAttributesForEffectiveDating(List<String> groupByAttributesForEffectiveDating) {
        this.groupByAttributesForEffectiveDating = groupByAttributesForEffectiveDating;
    }


    /**
     * Gets the boNotesEnabled flag for the Data object
     *
     * <p>
     * true indicates that notes and attachments will be permanently
     * associated with the business object
     * false indicates that notes and attachments are associated
     * with the document used to create or edit the business object.
     * </p>
     *
     * @return the boNotesEnabled flag
     */
    public boolean isBoNotesEnabled() {
        return boNotesEnabled;
    }

    /**
     * Setter for the boNotesEnabled flag
     */
    public void setBoNotesEnabled(boolean boNotesEnabled) {
        this.boNotesEnabled = boNotesEnabled;
    }

    /**
     * Gets the inactivationBlockingDefinitions for the Data object
     *
     * <p>
     *
     * </p>
     *
     * @return the list of <code>InactivationBlockingDefinition</code> 
     */
    public List<InactivationBlockingDefinition> getInactivationBlockingDefinitions() {
        return this.inactivationBlockingDefinitions;
    }

    /**
     * Setter for the inactivationBlockingDefinitions
     */
    public void setInactivationBlockingDefinitions(
            List<InactivationBlockingDefinition> inactivationBlockingDefinitions) {
        this.inactivationBlockingDefinitions = inactivationBlockingDefinitions;
    }
}
