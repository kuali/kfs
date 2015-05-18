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
package org.kuali.rice.kns.web.ui;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.datadictionary.mask.Mask;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a Field (form field or read only)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public class Field implements java.io.Serializable, PropertyRenderingConfigElement {
    private static final long serialVersionUID = 6549897986355019202L;
    public static final int DEFAULT_MAXLENGTH = 30;
    public static final int DEFAULT_SIZE = 30;

    public static final String HIDDEN = "hidden";
    public static final String TEXT = "text";
    public static final String DROPDOWN = "dropdown";
    public static final String MULTIBOX = "multibox";
    public static final String MULTISELECT = "multiselect";
    public static final String RADIO = "radio";
    public static final String QUICKFINDER = "quickFinder";
    public static final String LOOKUP_RESULT_ONLY = "lookupresultonly";
    public static final String DROPDOWN_REFRESH = "dropdown_refresh";
    public static final String DROPDOWN_SCRIPT = "dropdown_script";
    public static final String CHECKBOX = "checkbox";
    public static final String CURRENCY = "currency";
    public static final String TEXT_AREA = "textarea";
    public static final String FILE = "file";
    public static final String IMAGE_SUBMIT = "imagesubmit";
    public static final String CONTAINER = "container";
    public static final String KUALIUSER = "kualiuser";
    public static final String READONLY = "readOnly";
    public static final String EDITABLE = "editable";
    public static final String LOOKUP_HIDDEN = "lookuphidden";
    public static final String LOOKUP_READONLY = "lookupreadonly";
    public static final String WORKFLOW_WORKGROUP = "workflowworkgroup";
    public static final String MASKED = "masked";
    public static final String PARTIALLY_MASKED = "partiallyMasked";

    public static final String SUB_SECTION_SEPARATOR = "subSectionSeparator";
    public static final String BLANK_SPACE = "blankSpace";
    public static final String BUTTON = "button";
    public static final String LINK = "link";

    //#START MOVED FROM DOC SEARCH RELATED
    public static final String DATEPICKER = "datePicker";

    public static final Set<String> SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES;
    public static final Set<String> MULTI_VALUE_FIELD_TYPES = new HashSet<String>();

    static {
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES = new HashSet<String>();
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(HIDDEN);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(TEXT);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(CURRENCY);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(DROPDOWN);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(RADIO);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(DROPDOWN_REFRESH);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(MULTIBOX);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(MULTISELECT);

        MULTI_VALUE_FIELD_TYPES.add(MULTIBOX);
        MULTI_VALUE_FIELD_TYPES.add(MULTISELECT);
    }

    private boolean isIndexedForSearch = true;

    // following values used in ranged searches
    private String mainFieldLabel;  // the fieldLabel holds things like "From" and "Ending" and this field holds things like "Total Amount"
    private Boolean rangeFieldInclusive;
    private boolean memberOfRange = false;
    private boolean allowInlineRange = false;

    // this field is currently a hack to allow us to indicate whether or not the column of data associated
    // with a particular field will be visible in the result set of a search or not
    private boolean isColumnVisible = true;

    //FIXME: this one definitely seems iffy, could be confused with regular fieldType, is there another better name or can this go away?
    private String fieldDataType = KewApiConstants.SearchableAttributeConstants.DEFAULT_SEARCHABLE_ATTRIBUTE_TYPE_NAME;

    //used by multibox/select etc
    private String[] propertyValues;

    //extra field to skip blank option value (for route node)
    private boolean skipBlankValidValue = false;

    //#END DOC SEARCH RELATED

    private String fieldType;

    private String fieldLabel;
    private String fieldHelpUrl;
    private String propertyName;
    private String propertyValue;

    private String alternateDisplayPropertyName;
    private String alternateDisplayPropertyValue;
    private String additionalDisplayPropertyName;
    private String additionalDisplayPropertyValue;

    private List<KeyValue> fieldValidValues;
    private String quickFinderClassNameImpl;
    private String baseLookupUrl;

    private boolean clear;
    private boolean dateField;
    private String fieldConversions;
    private boolean fieldRequired;

    private List fieldInactiveValidValues;
    private Formatter formatter;
    private boolean highlightField;
    private boolean isReadOnly;
    private String lookupParameters;
    private int maxLength;

    private HtmlData inquiryURL;
    private String propertyPrefix;
    private int size;
    private boolean upperCase;
    private int rows;
    private int cols;
    private List<Row> containerRows;
    private String fieldHelpSummary;
    private String businessObjectClassName;
    private String fieldHelpName;
    private String script;
    private String universalIdAttributeName;
    private String universalIdValue;
    private String userIdAttributeName;
    private String personNameAttributeName;
    private String personNameValue;
    private String defaultValue = KRADConstants.EMPTY_STRING;
    private boolean keyField;
    private String displayEditMode;
    private Mask displayMask;
    private String displayMaskValue;
    private String encryptedValue;
    private boolean secure;
    private String webOnBlurHandler;
    private String webOnBlurHandlerCallback;
    protected List<String> webUILeaveFieldFunctionParameters = new ArrayList<String>();
    private String styleClass;
    private int formattedMaxLength;
    private String containerName;
    private String containerElementName;
    private List<Field> containerDisplayFields;
    private boolean isDatePicker;
    private boolean ranged;

    private boolean expandedTextArea;
    private String referencesToRefresh;
    private int numberOfColumnsForCollection;
    public String cellAlign;
    private String inquiryParameters;
    private boolean fieldDirectInquiryEnabled;

    public boolean fieldLevelHelpEnabled;

    public boolean fieldLevelHelpDisabled;
    public String fieldLevelHelpUrl;

    private String imageSrc;
    private String target;
    private String hrefText;

    private boolean triggerOnChange;


    /**
     * No-args constructor
     */
    public Field() {
        this.fieldLevelHelpEnabled = false;
        this.triggerOnChange = false;
    }

    /**
     * Constructor that creates an instance of this class to support inquirable
     *
     * @param propertyName property attribute of the bean
     * @param fieldLabel   label of the display field
     */
    public Field(String propertyName, String fieldLabel) {
        this.propertyName = propertyName;
        this.fieldLabel = fieldLabel;
        this.isReadOnly = false;
        this.upperCase = false;
        this.keyField = false;
        this.secure = false;
        this.fieldLevelHelpEnabled = false;
        this.triggerOnChange = false;
    }

    /**
     * Constructor that creates an instance of this class.
     *
     * @param fieldLabel               label of the search criteria field
     * @param fieldHelpUrl             url of a help link to help instructions
     * @param fieldType                type of input field for this search criteria
     * @param clear                    clear action flag
     * @param propertyName             name of the bean attribute for this search criteria
     * @param propertyValue            value of the bean attribute
     * @param fieldRequired            flag to denote if field is required
     * @param dateField                falg to denot if field should be validated as a date object
     * @param fieldValidValues         used for drop down list
     * @param quickFinderClassNameImpl class name to transfer control to quick finder
     */
    public Field(String fieldLabel, String fieldHelpUrl, String fieldType, boolean clear, String propertyName, String propertyValue, boolean fieldRequired, boolean dateField, List<KeyValue> fieldValidValues, String quickFinderClassNameImpl) {
        this.dateField = dateField;
        this.fieldLabel = fieldLabel;
        this.fieldHelpUrl = fieldHelpUrl;
        this.fieldType = fieldType;
        this.fieldRequired = fieldRequired;
        this.clear = clear;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.fieldValidValues = fieldValidValues;
        this.quickFinderClassNameImpl = quickFinderClassNameImpl;
        this.size = DEFAULT_SIZE;
        this.maxLength = DEFAULT_MAXLENGTH;
        this.isReadOnly = false;
        this.upperCase = false;
        this.keyField = false;
        this.fieldLevelHelpEnabled = false;
        this.triggerOnChange = false;
    }

    /**
     * Constructor that creates an instance of this class.
     *
     * @param fieldLabel               label of the search criteria field
     * @param fieldHelpUrl             url of a help link to help instructions
     * @param fieldType                type of input field for this search criteria
     * @param clear                    clear action flag
     * @param propertyName             name of the bean attribute for this search criteria
     * @param propertyValue            value of the bean attribute
     * @param fieldRequired            flag to denote if field is required
     * @param dateField                falg to denot if field should be validated as a date object
     * @param fieldValidValues         used for drop down list
     * @param quickFinderClassNameImpl class name to transfer control to quick finder
     * @param size                     size of the input field
     * @param maxLength                maxLength of the input field
     */
    public Field(String fieldLabel, String fieldHelpUrl, String fieldType, boolean clear, String propertyName, String propertyValue, boolean fieldRequired, boolean dateField, List<KeyValue> fieldValidValues, String quickFinderClassNameImpl, int size, int maxLength) {
        this.dateField = dateField;
        this.fieldLabel = fieldLabel;
        this.fieldHelpUrl = fieldHelpUrl;
        this.fieldType = fieldType;
        this.fieldRequired = fieldRequired;
        this.clear = clear;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.fieldValidValues = fieldValidValues;
        this.upperCase = false;
        this.quickFinderClassNameImpl = quickFinderClassNameImpl;
        if (size <= 0) {
            this.size = DEFAULT_SIZE;
        } else {
            this.size = size;
        }
        if (size <= 0) {
            this.size = DEFAULT_MAXLENGTH;
        } else {
            this.maxLength = maxLength;
        }
        this.isReadOnly = false;
        this.keyField = false;
        this.fieldLevelHelpEnabled = false;
        this.triggerOnChange = false;
    }


    /**
     * Helper method to determine if this is an INPUT type field
     *
     * @param fieldType
     */
    public static boolean isInputField(String fieldType) {
        if (StringUtils.isBlank(fieldType)) {
            return false;
        }
        // JJH: Would it be good to create a InputField Set and test to see if the fieldType exists in the set?
        if (fieldType.equals(Field.DROPDOWN) || fieldType.equals(Field.DROPDOWN_REFRESH) || fieldType.equals(Field.TEXT) || fieldType.equals(Field.RADIO) || fieldType.equals(Field.CURRENCY) || fieldType.equals(Field.KUALIUSER) || fieldType.equals(Field.DROPDOWN_SCRIPT) || fieldType.equals(LOOKUP_READONLY) || fieldType.equals(TEXT_AREA)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * @return the imageSrc
     */
    public String getImageSrc() {
        return this.imageSrc;
    }

    /**
     * @param imageSrc the imageSrc to set
     */
    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }


    /**
     * @return the target
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the hrefText
     */
    public String getHrefText() {
        return this.hrefText;
    }

    /**
     * @param hrefText the hrefText to set
     */
    public void setHrefText(String hrefText) {
        this.hrefText = hrefText;
    }

    /**
     * For container fields (i.e. fieldType.equals(CONTAINER)) with MV lookups enabled, the DD defined objectLabel of the class on which a multiple value lookup is performed.
     * The user friendly name
     */
    private String multipleValueLookupClassLabel;
    /**
     * For container fields (i.e. fieldType.equals(CONTAINER)) with MV lookups enabled, this is the class to perform a lookup upon
     */
    private String multipleValueLookupClassName;
    /**
     * For container fields (i.e. fieldType.equals(CONTAINER)) with MV lookups enabled, this is the name of the collection on the doc on which the
     * MV lookup is performed
     */
    private String multipleValueLookedUpCollectionName;

    public HtmlData getInquiryURL() {
        return inquiryURL;
    }

    public void setInquiryURL(HtmlData propertyURL) {
        this.inquiryURL = propertyURL;
    }

    public int getNumberOfColumnsForCollection() {
        return numberOfColumnsForCollection;
    }

    public void setNumberOfColumnsForCollection(int numberOfColumnsForCollection) {
        this.numberOfColumnsForCollection = numberOfColumnsForCollection;
    }

    public boolean isDatePicker() {
        return isDatePicker;
    }

    public void setDatePicker(boolean isDatePicker) {
        this.isDatePicker = isDatePicker;
    }

    public boolean isRanged() {
        return this.ranged;
    }

    public void setRanged(boolean ranged) {
        this.ranged = ranged;
    }

    public boolean isExpandedTextArea() {
        return expandedTextArea;
    }

    public void setExpandedTextArea(boolean expandedTextArea) {
        this.expandedTextArea = expandedTextArea;
    }

    /**
     * @return Returns the defaultValue.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue The defaultValue to set.
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean containsBOData() {
        if (StringUtils.isNotBlank(this.propertyName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return Returns the CHECKBOX.
     */
    public String getCHECKBOX() {
        return CHECKBOX;
    }

    /**
     * @return Returns the CONTAINER.
     */
    public String getCONTAINER() {
        return CONTAINER;
    }

    /**
     * @return Returns the dROPDOWN.
     */
    public String getDROPDOWN() {
        return DROPDOWN;
    }

    /**
     * @return Returns the TEXT_AREA.
     */
    public String getTEXT_AREA() {
        return TEXT_AREA;
    }

    /**
     * @return Returns the DROPDOWN_REFRESH
     */
    public String getDROPDOWN_REFRESH() {
        return DROPDOWN_REFRESH;
    }

    /**
     * @return Returns DROPDOWN_SCRIPT
     */
    public String getDROPDOWN_SCRIPT() {
        return DROPDOWN_SCRIPT;
    }

    /**
     * @return Returns MULTISELECT
     */
    public String getMULTISELECT() {
        return MULTISELECT;
    }

    /**
     * @return Returns KUALIUSER
     */
    public String getKUALIUSER() {
        return KUALIUSER;
    }

    /**
     * @return Returns the FILE.
     */
    public String getFILE() {
        return FILE;
    }

    /**
     * @return Returns SUB_SECTION_SEPARATOR
     */
    public String getSUB_SECTION_SEPARATOR() {
        return SUB_SECTION_SEPARATOR;
    }

    /**
     * @return Returns BLANK_SPACE
     */
    public String getBLANK_SPACE() {
        return BLANK_SPACE;
    }

    /**
     * @return the BUTTON
     */
    public String getBUTTON() {
        return BUTTON;
    }

    /**
     * @return the LINK
     */
    public String getLINK() {
        return LINK;
    }


    /**
     * @return Returns the fieldConversions.
     */
    public String getFieldConversions() {
        return fieldConversions;
    }


    public Map<String, String> getFieldConversionMap() {
        Map<String, String> fieldConversionMap = new HashMap<String, String>();
        if (!StringUtils.isBlank(fieldConversions)) {
            String[] splitFieldConversions = fieldConversions.split(KRADConstants.FIELD_CONVERSIONS_SEPARATOR);
            for (String fieldConversion : splitFieldConversions) {
                if (!StringUtils.isBlank(fieldConversion)) {
                    String[] splitFieldConversion = fieldConversion.split(KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR, 2);
                    String originalFieldName = splitFieldConversion[0];
                    String convertedFieldName = "";
                    if (splitFieldConversion.length > 1) {
                        convertedFieldName = splitFieldConversion[1];
                    }
                    fieldConversionMap.put(originalFieldName, convertedFieldName);
                }
            }
        }
        return fieldConversionMap;
    }


    /**
     * @return Returns the fieldHelpUrl.
     */
    public String getFieldHelpUrl() {
        return fieldHelpUrl;
    }

    /**
     * @return Returns the fieldLabel.
     */
    public String getFieldLabel() {
        return fieldLabel;
    }

    /**
     * @return Returns the fieldType.
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * @return Returns the fieldValidValues.
     */
    public List<KeyValue> getFieldValidValues() {
        return fieldValidValues;
    }


    /**
     * @return Returns the formatter.
     */
    @Override
    public Formatter getFormatter() {
        return formatter;
    }

    /**
     * @return Returns the hIDDEN.
     */
    public String getHIDDEN() {
        return HIDDEN;
    }


    /**
     * @return Returns the lookupParameters.
     */
    public String getLookupParameters() {
        return lookupParameters;
    }

    /**
     * @return Returns the maxLength.
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * @return Returns the propertyName.
     */
    @Override
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @return Returns the propertyValue.
     */
    @Override
    public String getPropertyValue() {
        if (propertyValue == null) {
            propertyValue = KRADConstants.EMPTY_STRING;
        }

        return propertyValue;
    }


    /**
     * Gets the propertyPrefix attribute.
     *
     * @return Returns the propertyPrefix.
     */
    public String getPropertyPrefix() {
        return propertyPrefix;
    }

    /**
     * Sets the propertyPrefix attribute value.
     *
     * @param propertyPrefix The propertyPrefix to set.
     */
    public void setPropertyPrefix(String propertyPrefix) {
        this.propertyPrefix = propertyPrefix;
    }

    /**
     * @return Returns the qUICKFINDER.
     */
    public String getQUICKFINDER() {
        return QUICKFINDER;
    }

    /**
     * @return Returns the quickFinderClassNameImpl.
     */
    public String getQuickFinderClassNameImpl() {
        return quickFinderClassNameImpl;
    }

    /**
     * @return Returns the rADIO.
     */
    public String getRADIO() {
        return RADIO;
    }

    /**
     * @return Returns the size.
     */
    public int getSize() {
        return size;
    }

    /**
     * @return Returns the TEXT.
     */
    public String getTEXT() {
        return TEXT;
    }

    public String getCURRENCY() {
        return CURRENCY;
    }

    /**
     * @return Returns the iMAGE_SUBMIT.
     */
    public String getIMAGE_SUBMIT() {
        return IMAGE_SUBMIT;
    }

    /**
     * @return Returns the LOOKUP_HIDDEN.
     */
    public String getLOOKUP_HIDDEN() {
        return LOOKUP_HIDDEN;
    }

    /**
     * @return Returns the LOOKUP_READONLY.
     */
    public String getLOOKUP_READONLY() {
        return LOOKUP_READONLY;
    }

    /**
     * @return Returns the WORKFLOW_WORKGROUP.
     */
    public String getWORKFLOW_WORKGROUP() {
        return WORKFLOW_WORKGROUP;
    }


    /**
     * @return Returns the clear.
     */
    public boolean isClear() {
        return clear;
    }

    /**
     * @return Returns the dateField.
     */
    public boolean isDateField() {
        return dateField;
    }

    /**
     * @return Returns the fieldRequired.
     */
    public boolean isFieldRequired() {
        return fieldRequired;
    }


    /**
     * @return Returns the highlightField.
     */
    public boolean isHighlightField() {
        return highlightField;
    }

    /**
     * @return Returns the isReadOnly.
     */
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * @return Returns the upperCase.
     */
    public boolean isUpperCase() {
        return upperCase;
    }

    /**
     * @param clear The clear to set.
     */
    public void setClear(boolean clear) {
        this.clear = clear;
    }

    /**
     * @param dateField The dateField to set.
     */
    public void setDateField(boolean dateField) {
        this.dateField = dateField;
    }

    /**
     * @param fieldConversionsMap The fieldConversions to set.
     */
    public void setFieldConversions(Map<String,String> fieldConversionsMap) {
        List<String> keyValuePairStrings = new ArrayList<String>();
        for (String key : fieldConversionsMap.keySet()) {
            String mappedField = fieldConversionsMap.get(key);
            keyValuePairStrings.add(key + ":" + mappedField) ;
        }
        String commaDelimitedConversions = StringUtils.join(keyValuePairStrings,",");
        setFieldConversions(commaDelimitedConversions );
    }


    /**
     * @param fieldConversions The fieldConversions to set.
     */
    public void setFieldConversions(String fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    public void appendFieldConversions(String fieldConversions) {
        if (StringUtils.isNotBlank(fieldConversions)) {
            this.fieldConversions = this.fieldConversions + "," + fieldConversions;
        }
    }

    /**
     * @param fieldHelpUrl The fieldHelpUrl to set.
     */
    public void setFieldHelpUrl(String fieldHelpUrl) {
        this.fieldHelpUrl = fieldHelpUrl;
    }

    /**
     * @param fieldLabel The fieldLabel to set.
     */
    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    /**
     * @param fieldRequired The fieldRequired to set.
     */
    public void setFieldRequired(boolean fieldRequired) {
        this.fieldRequired = fieldRequired;
    }

    /**
     * @param fieldType The fieldType to set.
     */
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * @param fieldValidValues The fieldValidValues to set.
     */
    public void setFieldValidValues(List<KeyValue> fieldValidValues) {
        this.fieldValidValues = fieldValidValues;
    }

    public boolean getHasBlankValidValue() {
        if (fieldValidValues == null) {
            throw new IllegalStateException("Valid values are undefined");
        }
        for (KeyValue keyLabel : fieldValidValues) {
            if (keyLabel.getKey().equals("")) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param formatter The formatter to set.
     */
    @Override
    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }


    /**
     * @param highlightField The highlightField to set.
     */
    public void setHighlightField(boolean highlightField) {
        this.highlightField = highlightField;
    }

    /**
     * @param lookupParametersMap The lookupParameters to set.
     */
    public void setLookupParameters(Map lookupParametersMap) {
        String lookupParameterString = "";
        for (Iterator iter = lookupParametersMap.keySet().iterator(); iter.hasNext();) {
            String field = (String) iter.next();
            String mappedField = (String) lookupParametersMap.get(field);
            lookupParameterString += field + ":" + mappedField;
            if (iter.hasNext()) {
                lookupParameterString += ",";
            }
        }
        setLookupParameters(lookupParameterString);
    }


    /**
     * @param lookupParameters The lookupParameters to set.
     */
    public void setLookupParameters(String lookupParameters) {
        this.lookupParameters = lookupParameters;
    }

    /**
     * This method appends the passed-in lookupParameters to the existing
     *
     * @param lookupParameters
     */
    public void appendLookupParameters(String lookupParameters) {
        if (StringUtils.isNotBlank(lookupParameters)) {
            if (StringUtils.isBlank(this.lookupParameters)) {
                this.lookupParameters = lookupParameters;
            } else {
                this.lookupParameters = this.lookupParameters + "," + lookupParameters;
            }
        }
    }

    /**
     * @param maxLength The maxLength to set.
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @param propertyName The propertyName to set.
     */
    @Override
    public void setPropertyName(String propertyName) {
        String newPropertyName = KRADConstants.EMPTY_STRING;
        if (propertyName != null) {
            newPropertyName = propertyName;
        }
        this.propertyName = newPropertyName;
    }

    /**
     * @param propertyValue The propertyValue to set.
     */
    public void setPropertyValue(Object propertyValue) {
        String newPropertyValue = ObjectUtils.formatPropertyValue(propertyValue);

        if (isUpperCase()) {
            newPropertyValue = newPropertyValue.toUpperCase();
        }

        this.propertyValue = newPropertyValue;
    }

    /**
     * @param quickFinderClassNameImpl The quickFinderClassNameImpl to set.
     */
    public void setQuickFinderClassNameImpl(String quickFinderClassNameImpl) {
        this.quickFinderClassNameImpl = quickFinderClassNameImpl;
    }

    /**
     * @param isReadOnly The isReadOnly to set.
     */
    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    /**
     * @param size The size to set.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @param upperCase The upperCase to set.
     */
    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }


    /**
     * @return Returns the cols.
     */
    public int getCols() {
        return cols;
    }


    /**
     * @param cols The cols to set.
     */
    public void setCols(int cols) {
        this.cols = cols;
    }


    /**
     * @return Returns the rows.
     */
    public int getRows() {
        return rows;
    }


    /**
     * @param rows The rows to set.
     */
    public void setRows(int rows) {
        this.rows = rows;
    }


    /**
     * @return Returns the containerRows.
     */
    public List<Row> getContainerRows() {
        return containerRows;
    }


    /**
     * @param containerRows The containerRows to set.
     */
    public void setContainerRows(List<Row> containerRows) {
        this.containerRows = containerRows;
    }


    /**
     * @return Returns the businessObjectClassName.
     */
    public String getBusinessObjectClassName() {
        return businessObjectClassName;
    }


    /**
     * @param businessObjectClassName The businessObjectClassName to set.
     */
    public void setBusinessObjectClassName(String businessObjectClassName) {
        this.businessObjectClassName = businessObjectClassName;
    }


    /**
     * @return Returns the fieldHelpSummary.
     */
    public String getFieldHelpSummary() {
        return fieldHelpSummary;
    }


    /**
     * @param fieldHelpSummary The fieldHelpSummary to set.
     */
    public void setFieldHelpSummary(String fieldHelpSummary) {
        this.fieldHelpSummary = fieldHelpSummary;
    }


    /**
     * @return Returns the fieldHelpName.
     */
    public String getFieldHelpName() {
        return fieldHelpName;
    }


    /**
     * @param fieldHelpName The fieldHelpName to set.
     */
    public void setFieldHelpName(String fieldHelpName) {
        this.fieldHelpName = fieldHelpName;
    }

    /**
     * Gets the script attribute.
     *
     * @return Returns the script.
     */
    public String getScript() {
        return script;
    }

    /**
     * Sets the script attribute value.
     *
     * @param script The script to set.
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * Gets the personNameAttributeName attribute.
     *
     * @return Returns the personNameAttributeName.
     */
    public String getPersonNameAttributeName() {
        return personNameAttributeName;
    }

    /**
     * Sets the personNameAttributeName attribute value.
     *
     * @param personNameAttributeName The personNameAttributeName to set.
     */
    public void setPersonNameAttributeName(String personNameAttributeName) {
        this.personNameAttributeName = personNameAttributeName;
    }

    /**
     * Gets the universalIdAttributeName attribute.
     *
     * @return Returns the universalIdAttributeName.
     */
    public String getUniversalIdAttributeName() {
        return universalIdAttributeName;
    }

    /**
     * Sets the universalIdAttributeName attribute value.
     *
     * @param universalIdAttributeName The universalIdAttributeName to set.
     */
    public void setUniversalIdAttributeName(String universalIdAttributeName) {
        this.universalIdAttributeName = universalIdAttributeName;
    }

    /**
     * Gets the userIdAttributeName attribute.
     *
     * @return Returns the userIdAttributeName.
     */
    public String getUserIdAttributeName() {
        return userIdAttributeName;
    }

    /**
     * Sets the userIdAttributeName attribute value.
     *
     * @param userIdAttributeName The userIdAttributeName to set.
     */
    public void setUserIdAttributeName(String userIdAttributeName) {
        this.userIdAttributeName = userIdAttributeName;
    }

    /**
     * Gets the keyField attribute.
     *
     * @return Returns the keyField.
     */
    public boolean isKeyField() {
        return keyField;
    }

    /**
     * Sets the keyField attribute value.
     *
     * @param keyField The keyField to set.
     */
    public void setKeyField(boolean keyField) {
        this.keyField = keyField;
    }


    /**
     * Gets the displayEditMode attribute.
     *
     * @return Returns the displayEditMode.
     */
    public String getDisplayEditMode() {
        return displayEditMode;
    }

    /**
     * Sets the displayEditMode attribute value.
     *
     * @param displayEditMode The displayEditMode to set.
     */
    public void setDisplayEditMode(String displayEditMode) {
        this.displayEditMode = displayEditMode;
    }

    /**
     * Gets the displayMask attribute.
     *
     * @return Returns the displayMask.
     */
    public Mask getDisplayMask() {
        return displayMask;
    }

    /**
     * Sets the displayMask attribute value.
     *
     * @param displayMask The displayMask to set.
     */
    public void setDisplayMask(Mask displayMask) {
        this.displayMask = displayMask;
    }

    /**
     * Gets the displayMaskValue attribute.
     *
     * @return Returns the displayMaskValue.
     */
    public String getDisplayMaskValue() {
        return displayMaskValue;
    }

    /**
     * Sets the displayMaskValue attribute value.
     *
     * @param displayMaskValue The displayMaskValue to set.
     */
    public void setDisplayMaskValue(String displayMaskValue) {
        this.displayMaskValue = displayMaskValue;
    }

    /**
     * Gets the encryptedValue attribute.
     *
     * @return Returns the encryptedValue.
     */
    public String getEncryptedValue() {
        return encryptedValue;
    }

    /**
     * Sets the encryptedValue attribute value.
     *
     * @param encryptedValue The encryptedValue to set.
     */
    public void setEncryptedValue(String encryptedValue) {
        this.encryptedValue = encryptedValue;
    }

    /**
     * Gets the secure attribute.
     *
     * @return Returns the secure.
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * Sets the secure attribute value.
     *
     * @param secure The secure to set.
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    /**
     * Returns the method name of a function present in the page which should be called
     * when the user tabs away from the field.
     *
     * @return
     */
    public String getWebOnBlurHandler() {
        return webOnBlurHandler;
    }

    public void setWebOnBlurHandler(String webOnBlurHandler) {
        this.webOnBlurHandler = webOnBlurHandler;
    }

    /**
     * Returns the method name of a function present in the page which should be called
     * after an AJAX call from the onblur handler.
     *
     * @return
     */
    public String getWebOnBlurHandlerCallback() {
        return webOnBlurHandlerCallback;
    }

    public void setWebOnBlurHandlerCallback(String webOnBlurHandlerCallback) {
        this.webOnBlurHandlerCallback = webOnBlurHandlerCallback;
    }

    /**
     * Sets the propertyValue attribute value.
     *
     * @param propertyValue The propertyValue to set.
     */
    @Override
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    @Override
    public String toString() {
        return "[" + getFieldType() + "] " + getPropertyName() + " = '" + getPropertyValue() + "'";
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public int getFormattedMaxLength() {
        return formattedMaxLength;
    }

    public void setFormattedMaxLength(int formattedMaxLength) {
        this.formattedMaxLength = formattedMaxLength;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    /**
     * Gets the containerElementName attribute.
     *
     * @return Returns the containerElementName.
     */
    public String getContainerElementName() {
        return containerElementName;
    }

    /**
     * Sets the containerElementName attribute value.
     *
     * @param containerElementName The containerElementName to set.
     */
    public void setContainerElementName(String containerElementName) {
        this.containerElementName = containerElementName;
    }

    /**
     * Gets the containerDisplayFields attribute.
     *
     * @return Returns the containerDisplayFields.
     */
    public List<Field> getContainerDisplayFields() {
        return containerDisplayFields;
    }

    /**
     * Sets the containerDisplayFields attribute value.
     *
     * @param containerDisplayFields The containerDisplayFields to set.
     */
    public void setContainerDisplayFields(List<Field> containerDisplayFields) {
        this.containerDisplayFields = containerDisplayFields;
    }

    public String getReferencesToRefresh() {
        return referencesToRefresh;
    }

    public void setReferencesToRefresh(String referencesToRefresh) {
        this.referencesToRefresh = referencesToRefresh;
    }

    /**
     * The DD defined objectLabel of the class on which a multiple value lookup is performed
     *
     * @return The DD defined objectLabel of the class on which a multiple value lookup is performed
     */
    public String getMultipleValueLookupClassLabel() {
        return multipleValueLookupClassLabel;
    }

    /**
     * The DD defined objectLabel of the class on which a multiple value lookup is performed
     *
     * @param multipleValueLookupClassLabel The DD defined objectLabel of the class on which a multiple value lookup is performed
     */
    public void setMultipleValueLookupClassLabel(String multipleValueLookupClassLabel) {
        this.multipleValueLookupClassLabel = multipleValueLookupClassLabel;
    }

    /**
     * For container fields (i.e. fieldType.equals(CONTAINER)) with MV lookups enabled, this is the name of the collection on the doc on which the
     * MV lookup is performed
     *
     * @return
     */
    public String getMultipleValueLookedUpCollectionName() {
        return multipleValueLookedUpCollectionName;
    }

    /**
     * For container fields (i.e. fieldType.equals(CONTAINER)) with MV lookups enabled, this is the name of the collection on the doc on which the
     * MV lookup is performed
     *
     * @param multipleValueLookedUpCollectionName
     *
     */
    public void setMultipleValueLookedUpCollectionName(String multipleValueLookedUpCollectionName) {
        this.multipleValueLookedUpCollectionName = multipleValueLookedUpCollectionName;
    }

    /**
     * For container fields (i.e. fieldType.equals(CONTAINER)) with MV lookups enabled, this is the class to perform a lookup upon
     *
     * @return
     */
    public String getMultipleValueLookupClassName() {
        return multipleValueLookupClassName;
    }

    /**
     * For container fields (i.e. fieldType.equals(CONTAINER)) with MV lookups enabled, this is the class to perform a lookup upon
     *
     * @param multipleValueLookupClassName
     */
    public void setMultipleValueLookupClassName(String multipleValueLookupClassName) {
        this.multipleValueLookupClassName = multipleValueLookupClassName;
    }

    /**
     * The td alignment to use for the Field.
     *
     * @return the cellAlign
     */
    public String getCellAlign() {
        return cellAlign;
    }

    /**
     * Sets the td alignment for the Field.
     *
     * @param cellAlign the cellAlign to set
     */
    public void setCellAlign(String cellAlign) {
        this.cellAlign = cellAlign;
    }

    public String getInquiryParameters() {
        return this.inquiryParameters;
    }

    public void setInquiryParameters(String inquiryParameters) {
        this.inquiryParameters = inquiryParameters;
    }

    /**
     * Returns whether field level help is enabled for this field.  If this value is true, then the field level help will be enabled.
     * If false, then whether a field is enabled is determined by the value returned by {@link #isFieldLevelHelpDisabled()} and the system-wide
     * parameter setting.  Note that if a field is read-only, that may cause field-level help to not be rendered.
     *
     * @return true if field level help is enabled, false if the value of this method should NOT be used to determine whether this method's return value
     *         affects the enablement of field level help
     */
    public boolean isFieldLevelHelpEnabled() {
        return this.fieldLevelHelpEnabled;
    }

    public void setFieldLevelHelpEnabled(boolean fieldLevelHelpEnabled) {
        this.fieldLevelHelpEnabled = fieldLevelHelpEnabled;
    }

    /**
     * Returns whether field level help is disabled for this field.  If this value is true and {@link #isFieldLevelHelpEnabled()} returns false,
     * then the field level help will not be rendered.  If both this and {@link #isFieldLevelHelpEnabled()} return false, then the system-wide
     * setting will determine whether field level help is enabled.  Note that if a field is read-only, that may cause field-level help to not be rendered.
     *
     * @return true if field level help is disabled, false if the value of this method should NOT be used to determine whether this method's return value
     *         affects the enablement of field level help
     */
    public boolean isFieldLevelHelpDisabled() {
        return this.fieldLevelHelpDisabled;
    }

    public void setFieldLevelHelpDisabled(boolean fieldLevelHelpDisabled) {
        this.fieldLevelHelpDisabled = fieldLevelHelpDisabled;
    }

    public boolean isFieldDirectInquiryEnabled() {
        return this.fieldDirectInquiryEnabled;
    }

    public void setFieldDirectInquiryEnabled(boolean fieldDirectInquiryEnabled) {
        this.fieldDirectInquiryEnabled = fieldDirectInquiryEnabled;
    }

    /**
     * @return the fieldInactiveValidValues
     */
    public List getFieldInactiveValidValues() {
        return this.fieldInactiveValidValues;
    }

    /**
     * @param fieldInactiveValidValues the fieldInactiveValidValues to set
     */
    public void setFieldInactiveValidValues(List fieldInactiveValidValues) {
        this.fieldInactiveValidValues = fieldInactiveValidValues;
    }

    public boolean isTriggerOnChange() {
        return this.triggerOnChange;
    }

    public void setTriggerOnChange(boolean triggerOnChange) {
        this.triggerOnChange = triggerOnChange;
    }

    public boolean getHasLookupable() {
        if (StringUtils.isBlank(quickFinderClassNameImpl) ) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getAlternateDisplayPropertyName() {
        return this.alternateDisplayPropertyName;
    }

    @Override
    public void setAlternateDisplayPropertyName(String alternateDisplayPropertyName) {
        this.alternateDisplayPropertyName = alternateDisplayPropertyName;
    }

    public String getAlternateDisplayPropertyValue() {
        return this.alternateDisplayPropertyValue;
    }

    public void setAlternateDisplayPropertyValue(Object alternateDisplayPropertyValue) {
        String formattedValue = ObjectUtils.formatPropertyValue(alternateDisplayPropertyValue);

        this.alternateDisplayPropertyValue = formattedValue;
    }

    @Override
    public String getAdditionalDisplayPropertyName() {
        return this.additionalDisplayPropertyName;
    }

    @Override
    public void setAdditionalDisplayPropertyName(String additionalDisplayPropertyName) {
        this.additionalDisplayPropertyName = additionalDisplayPropertyName;
    }

    public String getAdditionalDisplayPropertyValue() {
        return this.additionalDisplayPropertyValue;
    }

    public void setAdditionalDisplayPropertyValue(Object additionalDisplayPropertyValue) {
        String formattedValue = ObjectUtils.formatPropertyValue(additionalDisplayPropertyValue);

        this.additionalDisplayPropertyValue = formattedValue;
    }

    //#BEGIN DOC SEARCH RELATED
    public boolean isIndexedForSearch() {
        return this.isIndexedForSearch;
    }

    public void setIndexedForSearch(boolean indexedForSearch) {
        this.isIndexedForSearch = indexedForSearch;
    }

    public String getMainFieldLabel() {
        return this.mainFieldLabel;
    }

    public Boolean getRangeFieldInclusive() {
        return this.rangeFieldInclusive;
    }

    public boolean isMemberOfRange() {
        return this.memberOfRange;
    }

    public void setMainFieldLabel(String mainFieldLabel) {
        this.mainFieldLabel = mainFieldLabel;
    }

    public void setRangeFieldInclusive(Boolean rangeFieldInclusive) {
        this.rangeFieldInclusive = rangeFieldInclusive;
    }

    public void setMemberOfRange(boolean memberOfRange) {
        this.memberOfRange = memberOfRange;
    }

    public boolean isInclusive() {
        return (rangeFieldInclusive == null) ? true : rangeFieldInclusive;
    }

    public String getFieldDataType() {
        return this.fieldDataType;
    }

    public void setFieldDataType(String fieldDataType) {
        this.fieldDataType = fieldDataType;
    }

    public boolean isColumnVisible() {
        return this.isColumnVisible;
    }

    public void setColumnVisible(boolean isColumnVisible) {
        this.isColumnVisible = isColumnVisible;
    }

    public String[] getPropertyValues() {
        return this.propertyValues;
    }

    public void setPropertyValues(String[] propertyValues) {
        this.propertyValues = propertyValues;
    }

    /**
     * @return the skipBlankValidValue
     */
    public boolean isSkipBlankValidValue() {
        return this.skipBlankValidValue;
    }

    /**
     * @param skipBlankValidValue the skipBlankValidValue to set
     */
    public void setSkipBlankValidValue(boolean skipBlankValidValue) {
        this.skipBlankValidValue = skipBlankValidValue;
    }

    /**
     * @return the allowInlineRange
     */
    public boolean isAllowInlineRange() {
        return this.allowInlineRange;
    }

    /**
     * @param allowInlineRange the allowInlineRange to set
     */
    public void setAllowInlineRange(boolean allowInlineRange) {
        this.allowInlineRange = allowInlineRange;
    }

    public String getUniversalIdValue() {
        return this.universalIdValue;
    }

    public void setUniversalIdValue(String universalIdValue) {
        this.universalIdValue = universalIdValue;
    }

    public String getPersonNameValue() {
        return this.personNameValue;
    }

    public void setPersonNameValue(String personNameValue) {
        this.personNameValue = personNameValue;
    }

    public String getBaseLookupUrl() {
        return this.baseLookupUrl;
    }

    public void setBaseLookupUrl(String baseLookupURL) {
        this.baseLookupUrl = baseLookupURL;
    }

    public String getFieldLevelHelpUrl() {
        return fieldLevelHelpUrl;
    }

    public void setFieldLevelHelpUrl(String fieldLevelHelpUrl) {
        this.fieldLevelHelpUrl = fieldLevelHelpUrl;
    }

    /**
     * @return the webUILeaveFieldFunctionParameters
     */
    public List<String> getWebUILeaveFieldFunctionParameters() {
        return this.webUILeaveFieldFunctionParameters;
    }

    /**
     * @param webUILeaveFieldFunctionParameters
     *         the webUILeaveFieldFunctionParameters to set
     */
    public void setWebUILeaveFieldFunctionParameters(
            List<String> webUILeaveFieldFunctionParameters) {
        this.webUILeaveFieldFunctionParameters = webUILeaveFieldFunctionParameters;
    }

    public String getWebUILeaveFieldFunctionParametersString() {
        return KRADUtils.joinWithQuotes(getWebUILeaveFieldFunctionParameters());
    }


    //#END DOC SEARCH RELATED
}
