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
/*
* Copyright 2006-2012 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary.validation;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.Truth;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.service.DataDictionaryRemoteFieldService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.ObjectUtils;

import java.beans.PropertyDescriptor;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>An abstract base class for type service implementations which provides default validation of attributes from the Data
 * Dictionary.  It attempts to remain module independent by requiring the translation of the attribute definitions to a
 * generic format that includes the required {@link RemotableAttributeField}s as an unimplemented template method,
 * see{@link #getTypeAttributeDefinitions(String)}.
 * </p>
 * <p>Note that any {@link RemotableAttributeError}s returned from here should be fully resolved to the messages to be
 * displayed to the user (in other words, they should not contain error keys).  <b>The same approach should be taken by
 * subclasses since the message resources may not be present on the remote server that is invoking this service</b>.
 * There is a {@link #createErrorString(String, String...)} utility method that can be used to resolve
 * errorKeys and format them appropriately.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class AttributeValidatingTypeServiceBase {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AttributeValidatingTypeServiceBase.class);
    private static final String ANY_CHAR_PATTERN_S = ".*";
    private static final Pattern ANY_CHAR_PATTERN = Pattern.compile(ANY_CHAR_PATTERN_S);

	private DictionaryValidationService dictionaryValidationService;
	private DataDictionaryService dataDictionaryService;
    private DataDictionaryRemoteFieldService dataDictionaryRemoteFieldService;

    /**
     * Retrieves active type attribute definitions and translates them into a module-independent representation.  Note
     * that they should be returned in the order desired for display.
     *
     * @param typeId the identifier for the type
     * @return a correctly ordered List of active, module-independent type attribute definitions
     */
    protected abstract List<TypeAttributeDefinition> getTypeAttributeDefinitions(String typeId);

    /**
     * Validates an attribute that is *not* mapped to a data dictionary component via
     * {@link TypeAttributeDefinition#componentName} and {@link TypeAttributeDefinition#name}.
     *
     * @param attr the RemotableAttributeField for which to validate.
     * @param key the attribute name
     * @param value the attribute value
     * @return a List of {@link RemotableAttributeError}s with fully resolved error messages (not error keys).  May
     * return null or an empty List if no errors are encountered.
     */
    protected abstract List<RemotableAttributeError>
    validateNonDataDictionaryAttribute(RemotableAttributeField attr, String key, String value);



    /**
     * <p>This is the default implementation.  It calls into the service for each attribute to
     * validate it there.  No combination validation is done.  That should be done
     * by overriding this method.</p>
     * <p>This implementation calls {@link #getTypeAttributeDefinitions(String)} to retrieve module-agnostic 
     * representations.  It then iterates through the entry set of attributes, and calls 
     * {@link #validateNonDataDictionaryAttribute(RemotableAttributeField, String, String)}
     * or {@link #validateDataDictionaryAttribute(org.kuali.rice.kns.datadictionary.validation.AttributeValidatingTypeServiceBase.TypeAttributeDefinition, String, String)}
     * as appropriate.  Lastly it calls {@link #validateReferencesExistAndActive(Map, Map, List)}.
     * </p>
     *
     * @param typeId the identifier for the type
     * @param attributes the Map of attribute names to values
     * @return the List of errors ({@link RemotableAttributeError}s) encountered during validation.
     */
    public List<RemotableAttributeError> validateAttributes(String typeId, Map<String, String> attributes) {

        if (StringUtils.isBlank(typeId)) {
            throw new RiceIllegalArgumentException("typeId was null or blank");
        }

        if (attributes == null) {
            throw new RiceIllegalArgumentException("attributes was null or blank");
        }

        List<TypeAttributeDefinition> definitions = getTypeAttributeDefinitions(typeId);
        Map<String, TypeAttributeDefinition> typeAttributeDefinitionMap =
                buildTypeAttributeDefinitionMapByName(definitions);

        final List<RemotableAttributeError> validationErrors = new ArrayList<RemotableAttributeError>();

        for ( Map.Entry<String, String> entry : attributes.entrySet() ) {

            TypeAttributeDefinition typeAttributeDefinition = typeAttributeDefinitionMap.get(entry.getKey());

            final List<RemotableAttributeError> attributeErrors;
            if ( typeAttributeDefinition.getComponentName() == null) {
                attributeErrors = validateNonDataDictionaryAttribute(typeAttributeDefinition.getField(), entry.getKey(), entry.getValue());
            } else {
                attributeErrors = validateDataDictionaryAttribute(typeAttributeDefinition, entry.getKey(), entry.getValue());
            }

            if ( attributeErrors != null ) {
                validationErrors.addAll(attributeErrors);
            }
        }


        final List<RemotableAttributeError> referenceCheckErrors = validateReferencesExistAndActive(typeAttributeDefinitionMap, attributes, validationErrors);
        validationErrors.addAll(referenceCheckErrors);

        return Collections.unmodifiableList(validationErrors);
    }

    private Map<String, TypeAttributeDefinition> buildTypeAttributeDefinitionMapByName(
            List<TypeAttributeDefinition> definitions) {// throw them into a map by name
        Map<String, TypeAttributeDefinition> typeAttributeDefinitionMap;
        if (definitions == null || definitions.size() == 0) {
            typeAttributeDefinitionMap = Collections.<String, TypeAttributeDefinition>emptyMap();
        } else {
            typeAttributeDefinitionMap = new HashMap<String, TypeAttributeDefinition>();

            for (TypeAttributeDefinition definition : definitions) {
                typeAttributeDefinitionMap.put(definition.getName(), definition);
            }
        }
        return typeAttributeDefinitionMap;
    }

    /**
     * <p>Cross-validates referenced components amongst attributes to ensure they refer to existing and active
     * business objects.</p>
     * <p>This implementation instantiates any components mapped by attributes, populates them as best it can, and then
     * uses the {@link DataDictionaryService} to get relationship information.  Then, through the
     * {@link DictionaryValidationService} it attempts to ensure that any referenced business objects mapped by other
     * attributes exist and are active.  It pulls any errors encountered out of the global error map via calls to 
     * {@link #extractErrorsFromGlobalVariablesErrorMap(String)}</p>
     * <p>TODO: who can explain this? :-)</p>
     *
     * @param typeAttributeDefinitionMap a Map from attribute name to {@link TypeAttributeDefinition} containing all of
     * the attribute definitions for this type.
     * @param attributes the Map of attribute names to values
     * @param previousValidationErrors a List of previously encountered errors used to short circuit testing on
     * attributes that are already known to have errors.
     * @return the List of errors encountered. Cannot return null.
     */
	protected List<RemotableAttributeError> validateReferencesExistAndActive( Map<String, TypeAttributeDefinition> typeAttributeDefinitionMap, Map<String, String> attributes, List<RemotableAttributeError> previousValidationErrors) {
        //
        // Here there be dragons -- adapted from DataDictionaryTypeServiceBase, please excuse X-.
        //

		Map<String, BusinessObject> componentClassInstances = new HashMap<String, BusinessObject>();
		List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();

        // Create an instance of each component and shove it into the componentClassInstances
		for ( String attributeName : attributes.keySet() ) {
			TypeAttributeDefinition attr = typeAttributeDefinitionMap.get(attributeName);

			if (StringUtils.isNotBlank(attr.getComponentName())) {
				if (!componentClassInstances.containsKey(attr.getComponentName())) {
					try {
						Class<?> componentClass = Class.forName(attr.getComponentName());
						if (!BusinessObject.class.isAssignableFrom(componentClass)) {
							LOG.warn("Class " + componentClass.getName() + " does not implement BusinessObject.  Unable to perform reference existence and active validation");
							continue;
						}
						BusinessObject componentInstance = (BusinessObject) componentClass.newInstance();
						componentClassInstances.put(attr.getComponentName(), componentInstance);
					} catch (Exception e) {
						LOG.error("Unable to instantiate class for attribute: " + attributeName, e);
					}
				}
			}
		}

		// now that we have instances for each component class, try to populate them with any attribute we can,
		// assuming there were no other validation errors associated with it
		for ( Map.Entry<String, String> entry : attributes.entrySet() ) {
			if (!RemotableAttributeError.containsAttribute(entry.getKey(), previousValidationErrors)) {
				for (Object componentInstance : componentClassInstances.values()) {
					try {
						ObjectUtils.setObjectProperty(componentInstance, entry.getKey(), entry.getValue());
					} catch (NoSuchMethodException e) {
						// this is expected since not all attributes will be in all components
					} catch (Exception e) {
						LOG.error("Unable to set object property class: " + componentInstance.getClass().getName() + " property: " + entry.getKey(), e);
					}
				}
			}
		}

		for (Map.Entry<String, BusinessObject> entry : componentClassInstances.entrySet()) {
			List<RelationshipDefinition> relationships = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(entry.getKey()).getRelationships();
			if (relationships == null) {
				continue;
			}

			for (RelationshipDefinition relationshipDefinition : relationships) {
				List<PrimitiveAttributeDefinition> primitiveAttributes = relationshipDefinition.getPrimitiveAttributes();

				// this code assumes that the last defined primitiveAttribute is the attributeToHighlightOnFail
				String attributeToHighlightOnFail = primitiveAttributes.get(primitiveAttributes.size() - 1).getSourceName();

				// TODO: will this work for user ID attributes?

                if (attributes.containsKey(attributeToHighlightOnFail)) {

                    TypeAttributeDefinition attr = typeAttributeDefinitionMap.get(attributeToHighlightOnFail);
                    if (attr != null) {
                        final String attributeDisplayLabel;
                        if (StringUtils.isNotBlank(attr.getComponentName())) {
                            attributeDisplayLabel = getDataDictionaryService().getAttributeLabel(attr.getComponentName(), attributeToHighlightOnFail);
                        } else {
                            attributeDisplayLabel = attr.getLabel();
                        }

                        getDictionaryValidationService().validateReferenceExistsAndIsActive(entry.getValue(), relationshipDefinition.getObjectAttributeName(),
                                attributeToHighlightOnFail, attributeDisplayLabel);
                    }
                    List<String> extractedErrors = extractErrorsFromGlobalVariablesErrorMap(attributeToHighlightOnFail);
                    if (CollectionUtils.isNotEmpty(extractedErrors)) {
                        errors.add(RemotableAttributeError.Builder.create(attributeToHighlightOnFail, extractedErrors).build());
                    }
                }
			}
		}
		return errors;
	}

    /**
     * <p>Returns a String suitable for use in error messages to represent the given attribute.</p>
     * <p>This implementation returns a String of the format "longLabel (shortLabel)" where those fields are pulled
     * from the passed in definition.</p>
     *
     * @param definition the definition for which to create an error label.
     * @return the error label String.
     */
    protected static String getAttributeErrorLabel(RemotableAttributeField definition) {
        String longAttributeLabel = definition.getLongLabel();
        String shortAttributeLabel = definition.getShortLabel();

        return longAttributeLabel + " (" + shortAttributeLabel + ")";
    }

    /**
     * <p>creates an error String from the given errorKey and parameters.</p>
     * <p>This implementation will attempt to resolve the errorKey using the {@link ConfigurationService}, and format it
     * with the provided params using {@link MessageFormat#format(String, Object...)}.  If the errorKey can't be
     * resolved, it will return a string like the following: errorKey:param1;param2;param3;
     * </p>
     *
     * @param errorKey the errorKey
     * @param params the error params
     * @return error string
     */
    protected String createErrorString(String errorKey, String... params) {

        String errorString = getConfigurationService().getPropertyValueAsString(errorKey);
        if (StringUtils.isEmpty(errorString)) {
            final StringBuilder s = new StringBuilder(errorKey).append(':');
            if (params != null) {
                for (String p : params) {
                    if (p != null) {
                        s.append(p);
                        s.append(';');
                    }
                }
            }
            errorString = s.toString();
        } else {
            errorString = MessageFormat.format(errorString, params);
        }
        return errorString;
    }

    /**
     * <p>Validates a data dictionary mapped attribute for a primitive property.</p>
     * <p>This implementation checks that the attribute is defined using the {@link DataDictionaryService} if it is
     * from a specific set of types defined in TypeUtils.  Then, if the value is not blank, it checks for errors by
     * calling
     * {@link #validateAttributeFormat(RemotableAttributeField, String, String, String, String)}.
     * If it is blank, it checks for errors by calling
     * {@link #validateAttributeRequired(RemotableAttributeField, String, String, Object, String)}
     * .</p>
     *
     * @param typeAttributeDefinition the definition for the attribute
     * @param componentName the data dictionary component name
     * @param object the instance of the component
     * @param propertyDescriptor the descriptor for the property that the attribute maps to
     * @return a List of errors ({@link RemotableAttributeError}s) encountered during validation.  Cannot return null.
     */
    protected List<RemotableAttributeError> validatePrimitiveAttributeFromDescriptor (
            TypeAttributeDefinition typeAttributeDefinition, String componentName, Object object,
            PropertyDescriptor propertyDescriptor) {

        List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
        // validate the primitive attributes if defined in the dictionary
        if (null != propertyDescriptor
                && getDataDictionaryService().isAttributeDefined(componentName, propertyDescriptor.getName())) {

            Object value = ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
            Class<?> propertyType = propertyDescriptor.getPropertyType();

            if (TypeUtils.isStringClass(propertyType)
                    || TypeUtils.isIntegralClass(propertyType)
                    || TypeUtils.isDecimalClass(propertyType)
                    || TypeUtils.isTemporalClass(propertyType)) {

                // check value format against dictionary
                if (value != null && StringUtils.isNotBlank(value.toString())) {
                    if (!TypeUtils.isTemporalClass(propertyType)) {
                        errors.addAll(validateAttributeFormat(typeAttributeDefinition.getField(), componentName,
                                propertyDescriptor.getName(), value.toString(), propertyDescriptor.getName()));
                    }
                } else {
                	// if it's blank, then we check whether the attribute should be required
                    errors.addAll(validateAttributeRequired(typeAttributeDefinition.getField(), componentName,
                            propertyDescriptor.getName(), value, propertyDescriptor.getName()));
                }
            }
        }
        return errors;
    }


    /**
     * <p>Validates required-ness of an attribute against its corresponding value</p>
     * <p>This implementation checks if an attribute value is null or blank, and if so checks if the
     * {@link RemotableAttributeField} is required.  If it is, a {@link RemotableAttributeError} is created
     * with the message populated by a call to {@link #createErrorString(String, String...)}.</p>
     *
     * @param field the field for the attribute being tested
     * @param objectClassName the class name for the component
     * @param attributeName the name of the attribute
     * @param attributeValue the value of the attribute
     * @param errorKey the errorKey used to identify the field
     * @return the List of errors ({@link RemotableAttributeError}s) encountered during validation.  Cannot return null.
     */
    protected List<RemotableAttributeError> validateAttributeRequired(RemotableAttributeField field,
            String objectClassName, String attributeName, Object attributeValue, String errorKey) {
        List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
        // check if field is a required field for the business object

        if (attributeValue == null
                || (attributeValue instanceof String && StringUtils.isBlank((String) attributeValue))) {

            boolean required = field.isRequired();
            if (required) {
                // get label of attribute for message
                String errorLabel = getAttributeErrorLabel(field);
                errors.add(RemotableAttributeError.Builder.create(errorKey,
                        createErrorString(RiceKeyConstants.ERROR_REQUIRED, errorLabel)).build());
            }
        }

        return errors;
    }

    /**
     * <p>Gets the validation {@link Pattern} for the given {@link RemotableAttributeField}.</p>
     * <p>This implementation checks if there is a regexConstraint set on the field, and if so
     * it compiles a Pattern (with no special flags) using it.  Otherwise, it returns a pattern that
     * always matches.</p>
     * 
     * @param field the field for which to return a validation {@link Pattern}.
     * @return the compiled {@link Pattern} to use in validation the given field.
     */
    protected Pattern getAttributeValidatingExpression(RemotableAttributeField field) {
        if (field == null || StringUtils.isBlank(field.getRegexConstraint())) {
            return ANY_CHAR_PATTERN;
        }

        return Pattern.compile(field.getRegexConstraint());
    }

    /**
     * <p>Gets a {@link Formatter} appropriate for the data type of the given field.</p>
     * <p>This implementation returns null if {@link RemotableAttributeField#getDataType()}
     * returns null.  Otherwise, it returns the result of calling {@link Formatter#getFormatter(Class)} on the
     * {@link org.kuali.rice.core.api.uif.DataType}'s type</p>
     *
     * @param field the field for which to provide a {@link Formatter}.
     * @return an applicable {@link Formatter}, or null if one can't be found.
     */
	protected Formatter getAttributeFormatter(RemotableAttributeField field) {
        if (field.getDataType() == null) {
            return null;
        }

        return Formatter.getFormatter(field.getDataType().getType());
    }

    /**
     * <p>Validates the format of the value for the given attribute field.</p>
     * <p>This implementation checks if the attribute value is not blank, in which case it checks (as applicable) the
     * max length, min length, min value, max value, and format (using the {@link Pattern} returned by
     * {@link #getAttributeValidatingExpression(RemotableAttributeField)}).  If that doesn't
     * match, it will use the Formatter returned by
     * {@link #getAttributeFormatter(RemotableAttributeField)} to format the value and try
     * matching against it again.  For each format error that is found,
     * {@link #createErrorString(String, String...)} is called to prepare the text for the
     * {@link RemotableAttributeError} that is generated.
     *
     * @param field the field for the attribute whose value we are validating
     * @param objectClassName the name of the class to which the attribute belongs
     * @param attributeName the name of the attribute
     * @param attributeValue the String value whose format we are validating
     * @param errorKey the name of the property on the object class that this attribute maps to
     * @return a List containing any errors ({@link RemotableAttributeError}s) that are detected.
     */
    protected List<RemotableAttributeError> validateAttributeFormat(RemotableAttributeField field, String objectClassName, String attributeName, String attributeValue, String errorKey) {
    	List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();

        String errorLabel = getAttributeErrorLabel(field);

        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("(bo, attributeName, attributeValue) = (" + objectClassName + "," + attributeName + "," + attributeValue + ")");
        }

        if (StringUtils.isNotBlank(attributeValue)) {
            Integer maxLength = field.getMaxLength();
            if ((maxLength != null) && (maxLength.intValue() < attributeValue.length())) {
                errors.add(RemotableAttributeError.Builder.create(errorKey,
                        createErrorString(RiceKeyConstants.ERROR_MAX_LENGTH, errorLabel, maxLength.toString())).build());
                return errors;
            }
            Integer minLength = field.getMinLength();
            if ((minLength != null) && (minLength.intValue() > attributeValue.length())) {
                errors.add(RemotableAttributeError.Builder.create(errorKey,
                        createErrorString(RiceKeyConstants.ERROR_MIN_LENGTH, errorLabel, minLength.toString())).build());
                return errors;
            }
            Pattern validationExpression = getAttributeValidatingExpression(field);
            if (!ANY_CHAR_PATTERN_S.equals(validationExpression.pattern())) {
            	if ( LOG.isDebugEnabled() ) {
            		LOG.debug("(bo, attributeName, validationExpression) = (" + objectClassName + "," + attributeName + "," + validationExpression + ")");
            	}

                if (!validationExpression.matcher(attributeValue).matches()) {
                    boolean isError=true;
                    final Formatter formatter = getAttributeFormatter(field);
                    if (formatter != null) {
                        Object o = formatter.format(attributeValue);
                        isError = !validationExpression.matcher(String.valueOf(o)).matches();
                    }
                    if (isError) {
                        errors.add(RemotableAttributeError.Builder.create(errorKey, createErrorString(field.getRegexContraintMsg(), errorLabel))
                                .build());
                    }
                    return errors;
                }
            }
            Double min = field.getMinValue();
            if (min != null) {
                try {
                    if (Double.parseDouble(attributeValue) < min) {
                        errors.add(RemotableAttributeError.Builder.create(errorKey, createErrorString(
                                RiceKeyConstants.ERROR_INCLUSIVE_MIN, errorLabel, min.toString())).build());
                        return errors;
                    }
                }
                catch (NumberFormatException e) {
                    // quash; this indicates that the DD contained a min for a non-numeric attribute
                }
            }
            Double max = field.getMaxValue();
            if (max != null) {
                try {

                    if (Double.parseDouble(attributeValue) > max) {
                        errors.add(RemotableAttributeError.Builder.create(errorKey, createErrorString(
                                RiceKeyConstants.ERROR_INCLUSIVE_MAX, errorLabel, max.toString())).build());
                        return errors;
                    }
                }
                catch (NumberFormatException e) {
                    // quash; this indicates that the DD contained a max for a non-numeric attribute
                }
            }
        }
        return errors;
    }


    /**
     * <p>Removes all errors for the given attributeName from the global error map, transforms them as appropriate and
     * returns them as a List of Strings.</p>
     * <p>This implementation iterates through any errors found in the error map, transforms them by calling
     * {@link #createErrorString(String, String...)} and adds them to the List that is then returned</p>
     *
     * @param attributeName the attribute name for which to extract errors from the global error map.
     * @return a List of error Strings
     */
	protected List<String> extractErrorsFromGlobalVariablesErrorMap(String attributeName) {
		Object results = GlobalVariables.getMessageMap().getErrorMessagesForProperty(attributeName);
		List<String> errors = new ArrayList<String>();
        if (results instanceof String) {
        	errors.add(createErrorString((String) results));
        } else if ( results != null) {
        	if (results instanceof List) {
	        	List<?> errorList = (List<?>)results;
	        	for (Object msg : errorList) {
	        		ErrorMessage errorMessage = (ErrorMessage)msg;
	        		errors.add(createErrorString(errorMessage.getErrorKey(), errorMessage.getMessageParameters()));
				}
	        } else {
	        	String [] temp = (String []) results;
	        	for (String string : temp) {
					errors.add(createErrorString(string));
				}
	        }
        }
        GlobalVariables.getMessageMap().removeAllErrorMessagesForProperty(attributeName);
        return errors;
	}

    /**
     * <p>Validates the attribute value for the given {@link TypeAttributeDefinition} having a componentName.</p>
     * <p>This implementation instantiates a component object using reflection on the class name specified in the
     * {@link TypeAttributeDefinition}s componentName, gets a {@link PropertyDescriptor} for the attribute of the
     * component object, hydrates the attribute's value from it's String form, sets that value on the component object,
     * and then delegates to
     * {@link #validatePrimitiveAttributeFromDescriptor(org.kuali.rice.kns.datadictionary.validation.AttributeValidatingTypeServiceBase.TypeAttributeDefinition, String, Object, PropertyDescriptor)}.
     * </p>
     *
     * @param typeAttributeDefinition
     * @param attributeName
     * @param value
     * @return
     */
    protected List<RemotableAttributeError> validateDataDictionaryAttribute(TypeAttributeDefinition typeAttributeDefinition, String attributeName, String value) {
		try {
            // create an object of the proper type per the component
            Object componentObject = Class.forName( typeAttributeDefinition.getComponentName() ).newInstance();

            if ( attributeName != null ) {
                // get the bean utils descriptor for accessing the attribute on that object
                PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(componentObject, attributeName);
                if ( propertyDescriptor != null ) {
                    // set the value on the object so that it can be checked
                    Object attributeValue = getAttributeValue(propertyDescriptor, value);
                    propertyDescriptor.getWriteMethod().invoke( componentObject, attributeValue);
                    return validatePrimitiveAttributeFromDescriptor(typeAttributeDefinition,
                            typeAttributeDefinition.getComponentName(), componentObject, propertyDescriptor);
                }
            }
        } catch (Exception e) {
            throw new TypeAttributeValidationException(e);
        }
        return Collections.emptyList();
	}

    private Object getAttributeValue(PropertyDescriptor propertyDescriptor, String attributeValue){
        Object attributeValueObject = null;
        if (propertyDescriptor!=null && attributeValue!=null) {
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            if (String.class.equals(propertyType)) {
                // it's already a String
                attributeValueObject = attributeValue;
            } // KULRICE-6808: Kim Role Maintenance - Custom boolean role qualifier values are not being converted properly
            else if (Boolean.class.equals(propertyType) || Boolean.TYPE.equals(propertyType)) {
                attributeValueObject = Truth.strToBooleanIgnoreCase(attributeValue);
            } else {
                // try to create one with KRADUtils for other misc data types
                attributeValueObject = KRADUtils.createObject(propertyType, new Class[]{String.class}, new Object[]{attributeValue});
                // if that didn't work, we'll get a null back
                if (attributeValueObject == null ) {
                    // this doesn't seem like a great option, since we know the property isn't of type String
                    attributeValueObject = attributeValue;
                }
            }
        }
        return attributeValueObject;
    }


    // lazy initialization holder class
    private static class DictionaryValidationServiceHolder {
        public static DictionaryValidationService dictionaryValidationService =
                KNSServiceLocator.getKNSDictionaryValidationService();
    }

	protected DictionaryValidationService getDictionaryValidationService() {
		return DictionaryValidationServiceHolder.dictionaryValidationService;
	}

    // lazy initialization holder class
    private static class DataDictionaryServiceHolder {
        public static DataDictionaryService dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
    }

	protected DataDictionaryService getDataDictionaryService() {
		return DataDictionaryServiceHolder.dataDictionaryService;
	}

    // lazy initialization holder class
    private static class DataDictionaryRemoteFieldServiceHolder {
        public static DataDictionaryRemoteFieldService dataDictionaryRemoteFieldService =
                KRADServiceLocatorWeb.getDataDictionaryRemoteFieldService();
    }

    protected DataDictionaryRemoteFieldService getDataDictionaryRemoteFieldService() {
        return DataDictionaryRemoteFieldServiceHolder.dataDictionaryRemoteFieldService;
    }

    // lazy initialization holder class
    private static class ConfigurationServiceHolder {
        public static ConfigurationService configurationService = KRADServiceLocator.getKualiConfigurationService();
    }
    
    protected ConfigurationService getConfigurationService() {
        return ConfigurationServiceHolder.configurationService;
    }


    protected static class TypeAttributeValidationException extends RuntimeException {

        protected TypeAttributeValidationException(String message) {
            super( message );
        }

        protected TypeAttributeValidationException(Throwable cause) {
            super( cause );
        }

        private static final long serialVersionUID = 8220618846321607801L;

    }


    /**
     * A module-independent representation of a type attribute containing all the information that we need
     * in order to validate data dictionary-based attributes.
     */
    protected static class TypeAttributeDefinition {

        private final RemotableAttributeField field;
        private final String name;
        private final String componentName;
        private final String label;
        private final Map<String, String> properties;

        /**
         * Constructs a {@link TypeAttributeDefinition}
         * @param field the RemotableAttributeField corresponding to this definition.  Must not be null.
         * @param name the name for this attribute.  Must not be empty or null.
         * @param componentName The name of a data dictionary component that this field refers to. May be null.
         * @param label The label to use for this attribute.  May be null.
         * @param properties a catch all for properties important to a module's type attrbute definitions
         * that aren't directly supported by {@link TypeAttributeDefinition}.
         */
        public TypeAttributeDefinition(RemotableAttributeField field, String name, String componentName, String label, Map<String, String> properties) {
            if (field == null) throw new RiceIllegalArgumentException("field must not be null");
            if (StringUtils.isEmpty(name)) throw new RiceIllegalArgumentException("name must not be empty or null");
            this.field = field;
            this.name = name;
            this.componentName = componentName;
            this.label = label;

            if (properties == null || properties.isEmpty()) {
                this.properties = Collections.emptyMap();
            } else {
                // make our local variable into a copy of the passed in Map
                properties = new HashMap<String, String>(properties);
                // assign in in immutable form to our class member variable
                this.properties = Collections.unmodifiableMap(properties);
            }
        }

        public RemotableAttributeField getField() {
            return field;
        }

        public String getName() {
            return name;
        }

        public String getComponentName() {
            return componentName;
        }

        public String getLabel() {
            return label;
        }

        /**
         * @return an unmodifiable map of properties for this attribute.  Will never be null.
         */
        public Map<String, String> getProperties() {
            return properties;
        }
    }
}