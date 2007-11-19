/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.workflow.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.KualiModule;
import org.kuali.core.bo.Campus;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.lookup.keyvalues.CampusValuesFinder;
import org.kuali.core.lookup.keyvalues.KeyValuesFinder;
import org.kuali.core.lookup.keyvalues.ModuleValuesFinder;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.KualiModuleBO;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.lookup.keyvalues.ParameterValuesFinder;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.workflow.KualiWorkflowUtils;

import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.validation.ValidationContext;
import edu.iu.uis.eden.validation.ValidationResults;

/**
 * A workgroup type extension attribute that supports the extension information associated with System workgroups
 */
public class SystemWorkgroupExtensionAttribute implements ExtensionAttribute {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SystemWorkgroupExtensionAttribute.class);

    private static final String EXTENSIONS_PARAM = "extensions";
    private static final String OPERATION_PARAM = "operation";
    private static final String SEARCH_OP = "search";
    private static final String ENTRY_OP = "entry";

    private static final String MODULE_CODE = "moduleCode";
    private static final String CAMPUS_CODE = "campusCode";
    private static final String AREA_EXTENSION_ATTRIBUTE_NAME = "area";
    private static final String AUTHORIZATION_EXTENSION_ATTRIBUTE_NAME = "usedForAuthorization";
    private static final String AUTHORIZATION_EXTENSION_ATTRIBUTE_LABEL = "Used For Authorization?";
    private static final String ROUTING_EXTENSION_ATTRIBUTE_NAME = "usedForRouting";
    private static final String ROUTING_EXTENSION_ATTRIBUTE_LABEL = "Used For Routing?";

    private List<Row> rows;

    /**
     * Constructs a SystemWorkgroupExtensionAttribute instance
     */
    public SystemWorkgroupExtensionAttribute() {
        rows = new ArrayList<Row>();
        rows.add(buildModuleDropdownRow());
        rows.add(buildCampusDropdownRow());
        rows.add(buildAreaDropdownRow());
        rows.add(buildYesNoRow(AUTHORIZATION_EXTENSION_ATTRIBUTE_NAME, AUTHORIZATION_EXTENSION_ATTRIBUTE_LABEL));
        rows.add(buildYesNoRow(ROUTING_EXTENSION_ATTRIBUTE_NAME, ROUTING_EXTENSION_ATTRIBUTE_LABEL));
    }

    /**
     * Builds the row for campus code
     * @return a Row for campus code
     */
    private Row buildCampusDropdownRow() {
        org.kuali.core.web.ui.Field kualiCampusField = FieldUtils.getPropertyField(Campus.class, KFSPropertyConstants.CAMPUS_CODE, false);
        KeyValuesFinder campusFinder = new CampusValuesFinder();
        List campusFields = new ArrayList();
        campusFields.add(new Field("Campus", KualiWorkflowUtils.getHelpUrl(kualiCampusField), Field.DROPDOWN, false, CAMPUS_CODE, "", campusFinder.getKeyValues(), null, null));
        return new Row(campusFields);
    }

    /**
     * Builds a row for the module drop down
     * 
     * @return a Row for the module code
     */
    private Row buildModuleDropdownRow() {
        org.kuali.core.web.ui.Field kualiModuleField = FieldUtils.getPropertyField(KualiModuleBO.class, MODULE_CODE, false);
        KeyValuesFinder modulesFinder = new ModuleValuesFinder();
        List moduleFields = new ArrayList();
        moduleFields.add(new Field("Module", KualiWorkflowUtils.getHelpUrl(kualiModuleField), Field.DROPDOWN, false, MODULE_CODE, "", modulesFinder.getKeyValues(), null, null));
        return new Row(moduleFields);
    }

    /**
     * Builds a row for the area drop down
     * 
     * @return a row for the area drop down
     */
    private Row buildAreaDropdownRow() {
        KeyValuesFinder areasFinder = new ParameterValuesFinder(ParameterConstants.FINANCIAL_SYSTEM_ALL.class, "SYSTEM_WORKGROUP_TYPE_AREAS");
        List areaFields = new ArrayList();
        areaFields.add(new Field("Area", "", Field.DROPDOWN, false, AREA_EXTENSION_ATTRIBUTE_NAME, "", areasFinder.getKeyValues(), null, null));
        return new Row(areaFields);
    }

    /**
     * Builds a row with yes/no radio buttons
     * @param attributeName the attribute name of this row
     * @param attributeLabel the label to show in the HTML for this row
     * @return a Yes/No radio button Row
     */
    private Row buildYesNoRow(String attributeName, String attributeLabel) {
        List yesNoField = new ArrayList();
        List radioValues = new ArrayList();
        radioValues.add(new KeyLabelPair(Field.CHECKBOX_VALUE_CHECKED, Field.CHECKBOX_VALUE_CHECKED));
        radioValues.add(new KeyLabelPair("", Field.CHECKBOX_VALUE_UNCHECKED));
        yesNoField.add(new Field(attributeLabel, "", Field.RADIO, false, attributeName, "", radioValues, null, null));
        return new Row(yesNoField);
    }

    /**
     * Returns the prebuilt List of rows to accept the editing of System workgroup type extension data for the given workgroup
     * @see org.kuali.workflow.attribute.ExtensionAttribute#getRows()
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     * Validates the extension data given - basically, makes sure that if a module, campus code, or area
     * have been selected, that they exist (ie, a module exists, a campus code exists in SH_CAMPUS_T or the area
     * given is one of the ones within the area parameter)
     * @param validationContext the workgroup validation context to validate
     * @return the ValidationResults returned by the check
     * @see org.kuali.workflow.attribute.ExtensionAttribute#validate(edu.iu.uis.eden.validation.ValidationContext)
     */
    public ValidationResults validate(ValidationContext validationContext) {
        Map<String, String> extensions = (Map<String, String>) validationContext.getParameters().get(EXTENSIONS_PARAM);
        String operation = (String) validationContext.getParameters().get(OPERATION_PARAM);
        if (extensions == null) {
            // not sure if this is the correct exception or not but it feels better than throwing a runtime
            throw new ValidationException("Could not locate workgroup extension data in order to perform validation.");
        }
        List errors = new ArrayList();

        String moduleCode = LookupUtils.forceUppercase(KualiModuleBO.class, MODULE_CODE, extensions.get(MODULE_CODE));
        String campusCode = LookupUtils.forceUppercase(Campus.class, CAMPUS_CODE, extensions.get(CAMPUS_CODE));
        String area = extensions.get(AREA_EXTENSION_ATTRIBUTE_NAME);
        boolean authorizationGroup = translateExtensionToBoolean(extensions.get(AUTHORIZATION_EXTENSION_ATTRIBUTE_NAME));
        boolean routingGroup = translateExtensionToBoolean(extensions.get(ROUTING_EXTENSION_ATTRIBUTE_NAME));

        ValidationResults results = new ValidationResults();
        if (!StringUtils.isBlank(moduleCode)) {
            KualiModule module = SpringContext.getBean(KualiModuleService.class).getModuleByCode(moduleCode);
            if (module == null) {
                results.addValidationResult("Module Code is invalid.");
            }
        }
        if (!StringUtils.isBlank(campusCode)) {
            Map<String, String> campusKeys = new HashMap<String, String>();
            campusKeys.put("campusCode", campusCode);
            Campus campus = (Campus) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Campus.class, campusKeys);
            if (campus == null) {
                results.addValidationResult("Campus Code is invalid.");
            }
        }
        if (!StringUtils.isBlank(area)) {
            List<String> paramValues = SpringContext.getBean(ParameterService.class).getParameterValues(ParameterConstants.FINANCIAL_SYSTEM_ALL.class, "SYSTEM_WORKGROUP_TYPE_AREAS");
            if (paramValues != null) {
                if (!paramValues.contains(area)) {
                    results.addValidationResult("Area is invalid.");
                }
            }
        }
        return results;
    }

    private boolean translateExtensionToBoolean(String extension) {
        return (extension != null && extension.trim().length() > 0 && extension.equals(Field.CHECKBOX_VALUE_CHECKED));
    }
}
