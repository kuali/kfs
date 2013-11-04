/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sys.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.DataDictionaryService;

/**
 * Builder for XML schema types based on a data dictionary attribute. Data dictionary properties such as required, maxLength, and
 * validation pattern are retrieved and then the equivalent schema restriction is rendered for the type
 */
public class AttributeSchemaValidationBuilder {
    protected static final String DD_MAP_MAX_LENGTH_KEY = "maxLength";
    protected static final String DD_MAP_EXACT_LENGTH_KEY = "validationPattern.exactLength";
    protected static final String DD_MAP_REQUIRED_KEY = "required";
    protected static final String DD_MAP_EXCLUSIVE_MIN_KEY = "exclusiveMin";
    protected static final String DD_MAP_EXCLUSIVE_MAX_KEY = "exclusiveMax";
    protected static final String DD_MAP_VALIDATION_KEY = "validationPattern";
    protected static final String DD_MAP_VALIDATION_TYPE_KEY = "type";
    protected static final String DD_ALLOW_WHITESPACE_KEY = "validationPattern.allowWhitespace";

    public static class DD_VALIDATION_TYPES {
        public static final String DATE = "date";
        public static final String EMAIL = "emailAddress";
        public static final String FIXED_POINT = "fixedPoint";
        public static final String FLOATING_POINT = "floatingPoint";
        public static final String MONTH = "month";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String TIMESTAMP = "timestamp";
        public static final String YEAR = "year";
        public static final String ZIP_CODE = "zipcode";
        public static final String ALPHA_NUMBER = "alphaNumeric";
        public static final String ALPHA = "alpha";
        public static final String ANY_CHARACTER = "anyCharacter";
        public static final String CHARSET = "charset";
        public static final String NUMBERIC = "numeric";
        public static final String REGEX = "regex";
    }

    public static final String XSD_SCHEMA_PREFIX = "xsd:";

    public static class SCHEMA_BASE_TYPES {
        public static final String DATE = "date";
        public static final String DATE_TIME = "dateTime";
        public static final String STRING = "normalizedString";
        public static final String INTEGER = "integer";
        public static final String DECIMAL = "decimal";
    }

    protected String attributeKey;
    protected Map attributeMap;

    /**
     * Constructs a AttributeSchemaValidationBuilder.java.
     */
    public AttributeSchemaValidationBuilder() {

    }

    /**
     * Constructs a AttributeSchemaValidationBuilder.java.
     * 
     * @param attributeKey name of data dictionary entry to build type for
     */
    public AttributeSchemaValidationBuilder(String attributeKey) {
        this.attributeKey = attributeKey;

        Map dataDictionaryMap = SpringContext.getBean(DataDictionaryService.class).getDataDictionaryMap();
        String boClassName = StringUtils.substringBefore(attributeKey, ".");
        String attributeName = StringUtils.substringAfter(attributeKey, ".");

        Map boMap = (Map) dataDictionaryMap.get(boClassName);
        if (boMap == null) {
            throw new RuntimeException("Unable to find bo map for class: " + boClassName);
        }
        
        Map attributesMap = (Map) boMap.get("attributes");
        this.attributeMap = (Map) attributesMap.get(attributeName);
        if (this.attributeMap == null) {
            throw new RuntimeException("Unable to find export map for attribute: " + attributeKey);
        }
    }

    /**
     * Based on data dictionary configuration for attribute builds a complete schema simple type with the appropriate base and
     * restrictions
     * 
     * @return collection of XML lines for the type
     */
    public Collection toSchemaType() {
        Collection schemaType = new ArrayList();

        schemaType.add(getTypeTagOpener());
        schemaType.add(getRestrictionTagOpener());
        schemaType.addAll(getFurtherRestrictionTags());
        schemaType.add(getRestrictionTagCloser());
        schemaType.add(getTypeTagCloser());

        return schemaType;
    }

    /**
     * Builds simple type opening tag .
     * 
     * @return XML Line
     */
    public String getTypeTagOpener() {
        return String.format("    <%ssimpleType name=\"%s\">", XSD_SCHEMA_PREFIX, attributeKey);
    }

    /**
     * Builds simple type closing tag .
     * 
     * @return XML Line
     */
    public String getTypeTagCloser() {
        return String.format("    </%ssimpleType>", XSD_SCHEMA_PREFIX);
    }

    /**
     * Builds restriction opening tag. Data dictionary validation type is used to determine base schema type .
     * 
     * @return XML Line
     */
    public String getRestrictionTagOpener() {
        String xsdBase = "";
        if (isDateType()) {
            xsdBase = XSD_SCHEMA_PREFIX + SCHEMA_BASE_TYPES.DATE;
        }
        else if (isTimestampType()) {
            xsdBase = XSD_SCHEMA_PREFIX + SCHEMA_BASE_TYPES.DATE_TIME;
        }
        else if (isDecimalType() || isFloatingType()) {
            xsdBase = XSD_SCHEMA_PREFIX + SCHEMA_BASE_TYPES.DECIMAL;
        }
        else if (isNumericType()) {
            xsdBase = XSD_SCHEMA_PREFIX + SCHEMA_BASE_TYPES.INTEGER;
        }
        else {
            xsdBase = XSD_SCHEMA_PREFIX + SCHEMA_BASE_TYPES.STRING;
        }

        return String.format("        <%srestriction base=\"%s\">", XSD_SCHEMA_PREFIX, xsdBase);
    }

    /**
     * Builds restriction closing tag
     * 
     * @return XML Line
     */
    public String getRestrictionTagCloser() {
        return String.format("        </%srestriction>", XSD_SCHEMA_PREFIX);
    }

    /**
     * Based on attribute definition adds any further restrictions to the type
     * 
     * @return collection of XML lines
     */
    public Collection getFurtherRestrictionTags() {
        Collection restrictions = new ArrayList();

        // required can be applied to all types
        boolean required = getRequiredFromMap();
        if (required) {
            if (isStringType()) {
                restrictions.add(String.format("            <%sminLength value=\"1\"/>", XSD_SCHEMA_PREFIX));
            }
            else {
                restrictions.add(String.format("            <%spattern value=\"[^\\s]+\"/>", XSD_SCHEMA_PREFIX));
            }
        }

        if (isDateType() || isTimestampType() || isFloatingType()) {
            return restrictions;
        }

        if (isDecimalType()) {
            restrictions.add(String.format("            <%sfractionDigits value=\"2\"/>", XSD_SCHEMA_PREFIX));
            return restrictions;
        }

        if (isNumericType()) {
            String exclusiveMin = (String) attributeMap.get(DD_MAP_EXCLUSIVE_MIN_KEY);
            String exclusiveMax = (String) attributeMap.get(DD_MAP_EXCLUSIVE_MAX_KEY);
            if (StringUtils.isNotBlank(exclusiveMin)) {
                restrictions.add(String.format("            <%sminExclusive value=\"%s\"/>", XSD_SCHEMA_PREFIX, exclusiveMin));
            }
            if (StringUtils.isNotBlank(exclusiveMax)) {
                restrictions.add(String.format("            <%smaxExclusive value=\"%s\"/>", XSD_SCHEMA_PREFIX, exclusiveMax));
            }

            int exactLength = getExactLengthFromMap();
            if (exactLength > 0) {
                restrictions.add(String.format("            <%stotalDigits value=\"%s\"/>", XSD_SCHEMA_PREFIX, exactLength));
            }

            return restrictions;
        }

        // here we are dealing with string types
        int maxLength = getMaxLengthFromMap();
        if (maxLength > 0) {
            restrictions.add(String.format("            <%smaxLength value=\"%s\"/>", XSD_SCHEMA_PREFIX, maxLength));
        }

        int exactLength = getExactLengthFromMap();
        if (exactLength > 0) {
            restrictions.add(String.format("            <%slength value=\"%s\"/>", XSD_SCHEMA_PREFIX, exactLength));
        }

        boolean collapseWhitespace = !getAllowWhitespaceFromMap();
        if (collapseWhitespace) {
            restrictions.add(String.format("            <%swhiteSpace value=\"replace\"/>", XSD_SCHEMA_PREFIX));
        }

        return restrictions;
    }

    /**
     * Helper method to get the max length from the dd map
     * 
     * @return max length, or -1 if not found
     */
    protected int getMaxLengthFromMap() {
        String maxLengthStr = (String) attributeMap.get(DD_MAP_MAX_LENGTH_KEY);
        if (StringUtils.isNotBlank(maxLengthStr)) {
            int maxLength = Integer.parseInt(maxLengthStr);

            return maxLength;
        }

        return -1;
    }

    /**
     * Helper method to get the exact length from the dd Map
     * 
     * @return exact length or -1 if not found
     */
    protected int getExactLengthFromMap() {
        String exactLengthStr = (String) attributeMap.get(DD_MAP_EXACT_LENGTH_KEY);
        if (StringUtils.isNotBlank(exactLengthStr)) {
            int exactLength = Integer.parseInt(exactLengthStr);

            return exactLength;
        }

        return -1;
    }

    /**
     * Helper method to get the required setting from dd Map
     * 
     * @return true if required setting is set to true in dd, false if setting is false or was not found
     */
    protected boolean getRequiredFromMap() {
        String requiredStr = (String) attributeMap.get(DD_MAP_REQUIRED_KEY);
        if (StringUtils.isNotBlank(requiredStr)) {
            boolean required = Boolean.parseBoolean(requiredStr);

            return required;
        }

        return false;
    }

    /**
     * Helper method to get the allow whitespace setting from dd Map
     * 
     * @return true if allow whitespace setting is set to true in dd, false if setting is false or was not found
     */
    protected boolean getAllowWhitespaceFromMap() {
        String whitespaceStr = (String) attributeMap.get(DD_ALLOW_WHITESPACE_KEY);
        if (StringUtils.isNotBlank(whitespaceStr)) {
            boolean allowWhitespace = Boolean.parseBoolean(whitespaceStr);

            return allowWhitespace;
        }

        return false;
    }

    /**
     * Helper method to get validation type from dd Map
     * 
     * @return dd validation type
     */
    protected String getValidationType() {
        String validationType = "";
        Map validationMap = (Map) attributeMap.get(DD_MAP_VALIDATION_KEY);
        if (validationMap != null) {
            validationType = (String) validationMap.get(DD_MAP_VALIDATION_TYPE_KEY);
        }
        
        return validationType;
    }

    /**
     * Determines if the attribute's validation type is the Date validation type
     * 
     * @return boolean true if type is Date, false otherwise
     */
    protected boolean isDateType() {
        return DD_VALIDATION_TYPES.DATE.equals(getValidationType());
    }

    /**
     * Determines if the attribute's validation type is the Timestamp validation type
     * 
     * @return boolean true if type is Timestamp, false otherwise
     */
    protected boolean isTimestampType() {
        return DD_VALIDATION_TYPES.TIMESTAMP.equals(getValidationType());
    }

    /**
     * Determines if the attribute's validation type is the Numeric validation type
     * 
     * @return boolean true if type is Numeric, false otherwise
     */
    protected boolean isNumericType() {
        return DD_VALIDATION_TYPES.NUMBERIC.equals(getValidationType());
    }

    /**
     * Determines if the attribute's validation type is the Decimal validation type
     * 
     * @return boolean true if type is Decimal, false otherwise
     */
    protected boolean isDecimalType() {
        return DD_VALIDATION_TYPES.FIXED_POINT.equals(getValidationType());
    }

    /**
     * Determines if the attribute's validation type is the Floating validation type
     * 
     * @return boolean true if type is Floating, false otherwise
     */
    protected boolean isFloatingType() {
        return DD_VALIDATION_TYPES.FLOATING_POINT.equals(getValidationType());
    }

    /**
     * Determines if the attribute's validation type is a String validation type
     * 
     * @return boolean true if type is String, false otherwise
     */
    protected boolean isStringType() {
        return !isDateType() && !isTimestampType() && !isNumericType() && !isDecimalType() && !isFloatingType();
    }

    /**
     * Gets the attributeKey attribute.
     * 
     * @return Returns the attributeKey.
     */
    public String getAttributeKey() {
        return attributeKey;
    }

    /**
     * Sets the attributeKey attribute value.
     * 
     * @param attributeKey The attributeKey to set.
     */
    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }


}
