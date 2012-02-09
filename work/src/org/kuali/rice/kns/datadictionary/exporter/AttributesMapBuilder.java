/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary.exporter;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.control.ButtonControlDefinition;
import org.kuali.rice.kns.datadictionary.control.CurrencyControlDefinition;
import org.kuali.rice.kns.datadictionary.control.LinkControlDefinition;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.datadictionary.exporter.ExportMap;

/**
 * AttributesMapBuilder
 *
 *
 */
@Deprecated
public class AttributesMapBuilder {

    /**
     * Default constructor
     */
    public AttributesMapBuilder() {
    }


    /**
     * @param entry
     * @return ExportMap containing the standard entries for the entry's AttributesDefinition
     */
    public ExportMap buildAttributesMap(DataDictionaryEntryBase entry) {
        ExportMap attributesMap = new ExportMap("attributes");

        for ( AttributeDefinition attribute : entry.getAttributes() ) {
            attributesMap.set(buildAttributeMap(attribute, entry.getFullClassName()));
        }

        return attributesMap;
    }

    public ExportMap buildAttributeMap(AttributeDefinition attribute, String fullClassName) {
        ExportMap attributeMap = new ExportMap(attribute.getName());

        // simple properties
        attributeMap.set("name", attribute.getName());
        attributeMap.set("forceUppercase", String.valueOf( attribute.getForceUppercase() ));
        attributeMap.set("label", attribute.getLabel());
        attributeMap.set("shortLabel", attribute.getShortLabel());

        attributeMap.set("maxLength", String.valueOf( attribute.getMaxLength() ));
        String exclusiveMin = attribute.getExclusiveMin();
        if (exclusiveMin != null) {
            attributeMap.set("exclusiveMin", exclusiveMin/*.toString()*/);
        }
        String exclusiveMax = attribute.getInclusiveMax();
        if (exclusiveMax != null) {
            attributeMap.set("exclusiveMax", exclusiveMax/*.toString()*/);
        }

        attributeMap.set("required", String.valueOf( attribute.isRequired() ) );
        if (attribute.getSummary() != null) {
            attributeMap.set("summary", attribute.getSummary());
        }
        if (attribute.getDescription() != null) {
            attributeMap.set("description", attribute.getDescription());
        }
        if (attribute.hasFormatterClass()) {
            attributeMap.set("formatterClass", attribute.getFormatterClass());
        }

        // complex properties
        if (attribute.hasValidationPattern()) {
            attributeMap.set(attribute.getValidationPattern().buildExportMap("validationPattern"));
        }

        if(attribute.hasAttributeSecurity()){
        	attributeMap.set("attributeSecurityMask", String.valueOf(attribute.getAttributeSecurity().isMask()));
        	attributeMap.set("attributeSecurityPartialMask", String.valueOf(attribute.getAttributeSecurity().isPartialMask()));
        	attributeMap.set("attributeSecurityHide", String.valueOf(attribute.getAttributeSecurity().isHide()));
        	attributeMap.set("attributeSecurityReadOnly", String.valueOf(attribute.getAttributeSecurity().isReadOnly()));

        	// TODO: consider whether to export class names from the attribute security
        }

        attributeMap.set(buildControlMap(attribute));
        if (attribute.getOptionsFinder() != null) {
            attributeMap.set(buildKeyLabelMap(attribute));
        }
        if (StringUtils.isNotBlank(fullClassName)) {
            attributeMap.set("fullClassName", fullClassName);
        }

        return attributeMap;
    }

    private ExportMap buildKeyLabelMap(AttributeDefinition attribute) {

        ExportMap keyLabelMap = new ExportMap("keyLabelMap");

        for (Map.Entry<String, String> entry : attribute.getOptionsFinder().getKeyLabelMap().entrySet()) {
            if (StringUtils.isNotBlank(entry.getKey())) {
                keyLabelMap.set(entry.getKey(), entry.getValue());
            }
        }
        return keyLabelMap;
    }

    private ExportMap buildControlMap(AttributeDefinition attribute) {
        ControlDefinition control = attribute.getControl();
        ExportMap controlMap = new ExportMap("control");

        if (control.isCheckbox()) {
            controlMap.set("checkbox", "true");
        }
        else if (control.isHidden()) {
            controlMap.set("hidden", "true");
        }
        else if (control.isKualiUser()) {
            controlMap.set("kualiUser", "true");
        }
        else if (control.isRadio()) {
            controlMap.set("radio", "true");
            controlMap.set("valuesFinder", control.getValuesFinderClass());
            if (control.getBusinessObjectClass() != null) {
                controlMap.set("businessObject", control.getBusinessObjectClass());
            }
            if (StringUtils.isNotEmpty(control.getKeyAttribute())) {
                controlMap.set("keyAttribute", control.getKeyAttribute());
            }
            if (StringUtils.isNotEmpty(control.getLabelAttribute())) {
                controlMap.set("labelAttribute", control.getLabelAttribute());
            }
            if (control.getIncludeKeyInLabel() != null) {
                controlMap.set("includeKeyInLabel", String.valueOf( control.getIncludeKeyInLabel() ) );
            }
        }
        else if (control.isSelect()) {
            controlMap.set("select", "true");
            controlMap.set("valuesFinder", control.getValuesFinderClass());
            if (control.getBusinessObjectClass() != null) {
                controlMap.set("businessObject", control.getBusinessObjectClass());
            }
            if (StringUtils.isNotEmpty(control.getKeyAttribute())) {
                controlMap.set("keyAttribute", control.getKeyAttribute());
            }
            if (StringUtils.isNotEmpty(control.getLabelAttribute())) {
                controlMap.set("labelAttribute", control.getLabelAttribute());
            }
            if (control.getIncludeBlankRow() != null) {
                controlMap.set("includeBlankRow", control.getIncludeBlankRow().toString());
            }
            if (control.getIncludeKeyInLabel() != null) {
                controlMap.set("includeKeyInLabel", control.getIncludeKeyInLabel().toString());
            }
        }
        else if (control.isMultiselect()) {
            controlMap.set("multiselect", "true");
            controlMap.set("valuesFinder", control.getValuesFinderClass());
            if (control.getBusinessObjectClass() != null) {
                controlMap.set("businessObject", control.getBusinessObjectClass());
            }
            if (StringUtils.isNotEmpty(control.getKeyAttribute())) {
                controlMap.set("keyAttribute", control.getKeyAttribute());
            }
            if (StringUtils.isNotEmpty(control.getLabelAttribute())) {
                controlMap.set("labelAttribute", control.getLabelAttribute());
            }
            if (control.getIncludeKeyInLabel() != null) {
                controlMap.set("includeKeyInLabel", String.valueOf( control.getIncludeKeyInLabel() ));
            }
            if (control.getSize() != null) {
            	controlMap.set("size", String.valueOf( control.getSize() ) );
            }
        }
        else if (control.isText()) {
            controlMap.set("text", "true");
            controlMap.set("size", String.valueOf( control.getSize() ) );
            controlMap.set("datePicker", Boolean.valueOf(control.isDatePicker()).toString());
            controlMap.set("ranged", Boolean.valueOf(control.isRanged()).toString());
        }
        else if (control.isTextarea()) {
            controlMap.set("textarea", "true");
            controlMap.set("rows", String.valueOf( control.getRows() ));
            controlMap.set("cols", String.valueOf( control.getCols() ));
            controlMap.set("expandedTextArea", Boolean.valueOf(control.isExpandedTextArea()).toString());
        }
        else if (control.isCurrency()) {
            controlMap.set("currency", "true");
            controlMap.set("size", String.valueOf( control.getSize() ));
            controlMap.set("formattedMaxLength", String.valueOf( ((CurrencyControlDefinition) control).getFormattedMaxLength() ));
        }
        else if (control.isLookupHidden()) {
            controlMap.set("lookupHidden", "true");
        }
        else if (control.isLookupReadonly()) {
            controlMap.set("lookupReadonly", "true");
        }else if (control.isButton()) {
            controlMap.set("button", "true");
            if (StringUtils.isNotEmpty(((ButtonControlDefinition) control).getImageSrc())) {
            	controlMap.set("imageSrc", ((ButtonControlDefinition) control).getImageSrc());
            }
            if (StringUtils.isNotEmpty(((ButtonControlDefinition) control).getStyleClass())) {
            	controlMap.set("styleClass", ((ButtonControlDefinition) control).getStyleClass() );
            }
        }else if (control.isLink()) {
            controlMap.set("link", "true");
            if (StringUtils.isNotEmpty(((LinkControlDefinition) control).getTarget())) {
            	controlMap.set("target", ((LinkControlDefinition) control).getTarget());
            }
            if (StringUtils.isNotEmpty(((LinkControlDefinition) control).getStyleClass())) {
            	controlMap.set("styleClass", ((LinkControlDefinition) control).getStyleClass() );
            }
            if (StringUtils.isNotEmpty(((LinkControlDefinition) control).getHrefText())) {
            	controlMap.set("hrefText", ((LinkControlDefinition) control).getHrefText());
            }
        }

        return controlMap;
    }
}
