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
package org.kuali.rice.kns.kim.type;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAbstractWidget;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.framework.type.KimTypeService;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.comparator.StringValueComparator;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.ExternalizableBusinessObjectUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.ObjectUtils;

import java.beans.PropertyDescriptor;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A base class for {@code KimTypeService} implementations which read attribute-related information from the Data
 * Dictionary. This implementation is currently written against the KNS apis for Data Dictionary. Additionally, it
 * supports the ability to read non-Data Dictionary attribute information from the {@link KimTypeInfoService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataDictionaryTypeServiceBase implements KimTypeService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DataDictionaryTypeServiceBase.class);
    private static final String ANY_CHAR_PATTERN_S = ".*";
    private static final Pattern ANY_CHAR_PATTERN = Pattern.compile(ANY_CHAR_PATTERN_S);

	private BusinessObjectService businessObjectService;
	private DictionaryValidationService dictionaryValidationService;
	private DataDictionaryService dataDictionaryService;
	private KimTypeInfoService typeInfoService;
    private DocumentTypeService documentTypeService;

	@Override
	public String getWorkflowDocumentTypeName() {
		return null;
	}

	@Override
	public List<String> getWorkflowRoutingAttributes(String routeLevel) {
		if (StringUtils.isBlank(routeLevel)) {
            throw new RiceIllegalArgumentException("routeLevel was blank or null");
        }

        return Collections.emptyList();
	}

    @Override
	public List<KimAttributeField> getAttributeDefinitions(String kimTypeId) {
        final List<String> uniqueAttributes = getUniqueAttributes(kimTypeId);

        //using map.entry as a 2-item tuple
        final List<Map.Entry<String,KimAttributeField>> definitions = new ArrayList<Map.Entry<String,KimAttributeField>>();
        final KimType kimType = getTypeInfoService().getKimType(kimTypeId);
        final String nsCode = kimType.getNamespaceCode();

        for (KimTypeAttribute typeAttribute : kimType.getAttributeDefinitions()) {
            final KimAttributeField definition;
            if (typeAttribute.getKimAttribute().getComponentName() == null) {
                definition = getNonDataDictionaryAttributeDefinition(nsCode,kimTypeId,typeAttribute, uniqueAttributes);
            } else {
                definition = getDataDictionaryAttributeDefinition(nsCode,kimTypeId,typeAttribute, uniqueAttributes);
            }

            if (definition != null) {
                definitions.add(new AbstractMap.SimpleEntry<String,KimAttributeField>(typeAttribute.getSortCode() != null ? typeAttribute.getSortCode() : "", definition));
            }
        }

        //sort by sortCode
        Collections.sort(definitions, new Comparator<Map.Entry<String, KimAttributeField>>() {
            @Override
            public int compare(Map.Entry<String, KimAttributeField> o1, Map.Entry<String, KimAttributeField> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        //transform removing sortCode
		return Collections.unmodifiableList(Lists.transform(definitions, new Function<Map.Entry<String, KimAttributeField>, KimAttributeField>() {
            @Override
            public KimAttributeField apply(Map.Entry<String, KimAttributeField> v) {
                return v.getValue();
            }
        }));
	}

    /**
	 * This is the default implementation.  It calls into the service for each attribute to
	 * validate it there.  No combination validation is done.  That should be done
	 * by overriding this method.
	 */
	@Override
	public List<RemotableAttributeError> validateAttributes(String kimTypeId, Map<String, String> attributes) {
		if (StringUtils.isBlank(kimTypeId)) {
            throw new RiceIllegalArgumentException("kimTypeId was null or blank");
        }

        if (attributes == null) {
            throw new RiceIllegalArgumentException("attributes was null or blank");
        }

        final List<RemotableAttributeError> validationErrors = new ArrayList<RemotableAttributeError>();
		KimType kimType = getTypeInfoService().getKimType(kimTypeId);

		for ( Map.Entry<String, String> entry : attributes.entrySet() ) {
            KimTypeAttribute attr = kimType.getAttributeDefinitionByName(entry.getKey());
			final List<RemotableAttributeError> attributeErrors;
            if ( attr.getKimAttribute().getComponentName() == null) {
                attributeErrors = validateNonDataDictionaryAttribute(attr, entry.getKey(), entry.getValue());
            } else {
                attributeErrors = validateDataDictionaryAttribute(attr, entry.getKey(), entry.getValue());
            }

			if ( attributeErrors != null ) {
                validationErrors.addAll(attributeErrors);
			}
		}


		final List<RemotableAttributeError> referenceCheckErrors = validateReferencesExistAndActive(kimType, attributes, validationErrors);
        validationErrors.addAll(referenceCheckErrors);

		return Collections.unmodifiableList(validationErrors);
	}

    @Override
	public List<RemotableAttributeError> validateAttributesAgainstExisting(String kimTypeId, Map<String, String> newAttributes, Map<String, String> oldAttributes){
        if (StringUtils.isBlank(kimTypeId)) {
            throw new RiceIllegalArgumentException("kimTypeId was null or blank");
        }

        if (newAttributes == null) {
            throw new RiceIllegalArgumentException("newAttributes was null or blank");
        }

        if (oldAttributes == null) {
            throw new RiceIllegalArgumentException("oldAttributes was null or blank");
        }
        return Collections.emptyList();
        //final List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
        //errors.addAll(validateUniqueAttributes(kimTypeId, newAttributes, oldAttributes));
        //return Collections.unmodifiableList(errors);

	}

	/**
	 *
	 * This method matches input attribute set entries and standard attribute set entries using literal string match.
	 *
	 */
	protected boolean performMatch(Map<String, String> inputAttributes, Map<String, String> storedAttributes) {
		if ( storedAttributes == null || inputAttributes == null ) {
			return true;
		}
		for ( Map.Entry<String, String> entry : storedAttributes.entrySet() ) {
			if (inputAttributes.containsKey(entry.getKey()) && !StringUtils.equals(inputAttributes.get(entry.getKey()), entry.getValue()) ) {
				return false;
			}
		}
		return true;
	}

	protected Map<String, String> translateInputAttributes(Map<String, String> qualification){
		return qualification;
	}

	protected List<RemotableAttributeError> validateReferencesExistAndActive( KimType kimType, Map<String, String> attributes, List<RemotableAttributeError> previousValidationErrors) {
		Map<String, BusinessObject> componentClassInstances = new HashMap<String, BusinessObject>();
		List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
		
		for ( String attributeName : attributes.keySet() ) {
			KimTypeAttribute attr = kimType.getAttributeDefinitionByName(attributeName);
			
			if (StringUtils.isNotBlank(attr.getKimAttribute().getComponentName())) {
				if (!componentClassInstances.containsKey(attr.getKimAttribute().getComponentName())) {
					try {
						Class<?> componentClass = Class.forName( attr.getKimAttribute().getComponentName() );
						if (!BusinessObject.class.isAssignableFrom(componentClass)) {
							LOG.warn("Class " + componentClass.getName() + " does not implement BusinessObject.  Unable to perform reference existence and active validation");
							continue;
						}
						BusinessObject componentInstance = (BusinessObject) componentClass.newInstance();
						componentClassInstances.put(attr.getKimAttribute().getComponentName(), componentInstance);
					} catch (Exception e) {
						LOG.error("Unable to instantiate class for attribute: " + attributeName, e);
					}
				}
			}
		}
		
		// now that we have instances for each component class, try to populate them with any attribute we can, assuming there were no other validation errors associated with it
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
				
				if (!attributes.containsKey(attributeToHighlightOnFail)) {
					// if the attribute to highlight wasn't passed in, don't bother validating
					continue;
				}
				

				KimTypeAttribute attr = kimType.getAttributeDefinitionByName(attributeToHighlightOnFail);
				if (attr != null) {
					final String attributeDisplayLabel;
                    if (StringUtils.isNotBlank(attr.getKimAttribute().getComponentName())) {
						attributeDisplayLabel = getDataDictionaryService().getAttributeLabel(attr.getKimAttribute().getComponentName(), attributeToHighlightOnFail);
					} else {
						attributeDisplayLabel = attr.getKimAttribute().getAttributeLabel();
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
		return errors;
	}
	
    protected List<RemotableAttributeError> validateAttributeRequired(String kimTypeId, String objectClassName, String attributeName, Object attributeValue, String errorKey) {
        List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
        // check if field is a required field for the business object
        if (attributeValue == null || (attributeValue instanceof String && StringUtils.isBlank((String) attributeValue))) {
        	List<KimAttributeField> map = getAttributeDefinitions(kimTypeId);
        	KimAttributeField definition = DataDictionaryTypeServiceHelper.findAttributeField(attributeName, map);
        	
            boolean required = definition.getAttributeField().isRequired();
            if (required) {
                // get label of attribute for message
                String errorLabel = DataDictionaryTypeServiceHelper.getAttributeErrorLabel(definition);
                errors.add(RemotableAttributeError.Builder.create(errorKey, DataDictionaryTypeServiceHelper
                        .createErrorString(RiceKeyConstants.ERROR_REQUIRED, errorLabel)).build());
            }
        }
        return errors;
    }
    
	protected List<RemotableAttributeError> validateDataDictionaryAttribute(String kimTypeId, String entryName, Object object, PropertyDescriptor propertyDescriptor) {
		return validatePrimitiveFromDescriptor(kimTypeId, entryName, object, propertyDescriptor);
	}

    protected List<RemotableAttributeError> validatePrimitiveFromDescriptor(String kimTypeId, String entryName, Object object, PropertyDescriptor propertyDescriptor) {
        List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
        // validate the primitive attributes if defined in the dictionary
        if (null != propertyDescriptor && getDataDictionaryService().isAttributeDefined(entryName, propertyDescriptor.getName())) {
            Object value = ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
            Class<?> propertyType = propertyDescriptor.getPropertyType();

            if (TypeUtils.isStringClass(propertyType) || TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) || TypeUtils.isTemporalClass(propertyType)) {

                // check value format against dictionary
                if (value != null && StringUtils.isNotBlank(value.toString())) {
                    if (!TypeUtils.isTemporalClass(propertyType)) {
                        errors.addAll(validateAttributeFormat(kimTypeId, entryName, propertyDescriptor.getName(), value.toString(), propertyDescriptor.getName()));
                    }
                }
                else {
                	// if it's blank, then we check whether the attribute should be required
                    errors.addAll(validateAttributeRequired(kimTypeId, entryName, propertyDescriptor.getName(), value, propertyDescriptor.getName()));
                }
            }
        }
        return errors;
    }
    
    protected Pattern getAttributeValidatingExpression(KimAttributeField definition) {
        if (definition == null || StringUtils.isBlank(definition.getAttributeField().getRegexConstraint())) {
            return ANY_CHAR_PATTERN;
        }

        return Pattern.compile(definition.getAttributeField().getRegexConstraint());
     }
    
	protected Formatter getAttributeFormatter(KimAttributeField definition) {
        if (definition.getAttributeField().getDataType() == null) {
            return null;
        }

        return Formatter.getFormatter(definition.getAttributeField().getDataType().getType());
    }
    

    
	protected Double getAttributeMinValue(KimAttributeField definition) {
        return definition == null ? null : definition.getAttributeField().getMinValue();
    }

	protected Double getAttributeMaxValue(KimAttributeField definition) {
        return definition == null ? null : definition.getAttributeField().getMaxValue();
    }
	
    protected List<RemotableAttributeError> validateAttributeFormat(String kimTypeId, String objectClassName, String attributeName, String attributeValue, String errorKey) {
    	List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();

        List<KimAttributeField> attributeDefinitions = getAttributeDefinitions(kimTypeId);
    	KimAttributeField definition = DataDictionaryTypeServiceHelper.findAttributeField(attributeName,
                attributeDefinitions);
    	
        String errorLabel = DataDictionaryTypeServiceHelper.getAttributeErrorLabel(definition);

        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("(bo, attributeName, attributeValue) = (" + objectClassName + "," + attributeName + "," + attributeValue + ")");
        }

        if (StringUtils.isNotBlank(attributeValue)) {
            Integer maxLength = definition.getAttributeField().getMaxLength();
            if ((maxLength != null) && (maxLength.intValue() < attributeValue.length())) {
                errors.add(RemotableAttributeError.Builder.create(errorKey, DataDictionaryTypeServiceHelper
                        .createErrorString(RiceKeyConstants.ERROR_MAX_LENGTH, errorLabel, maxLength.toString())).build());
                return errors;
            }
            Pattern validationExpression = getAttributeValidatingExpression(definition);
            if (!ANY_CHAR_PATTERN_S.equals(validationExpression.pattern())) {
            	if ( LOG.isDebugEnabled() ) {
            		LOG.debug("(bo, attributeName, validationExpression) = (" + objectClassName + "," + attributeName + "," + validationExpression + ")");
            	}

                if (!validationExpression.matcher(attributeValue).matches()) {
                    boolean isError=true;
                    final Formatter formatter = getAttributeFormatter(definition);
                    if (formatter != null) {
                        Object o = formatter.format(attributeValue);
                        isError = !validationExpression.matcher(String.valueOf(o)).matches();
                    }
                    if (isError) {
                        errors.add(RemotableAttributeError.Builder.create(errorKey, DataDictionaryTypeServiceHelper
                                .createErrorString(definition)).build());
                    }
                    return errors;
                }
            }
            Double min = getAttributeMinValue(definition);
            if (min != null) {
                try {
                    if (Double.parseDouble(attributeValue) < min) {
                        errors.add(RemotableAttributeError.Builder.create(errorKey, DataDictionaryTypeServiceHelper
                                .createErrorString(RiceKeyConstants.ERROR_INCLUSIVE_MIN, errorLabel, min.toString())).build());
                        return errors;
                    }
                }
                catch (NumberFormatException e) {
                    // quash; this indicates that the DD contained a min for a non-numeric attribute
                }
            }
            Double max = getAttributeMaxValue(definition);
            if (max != null) {
                try {

                    if (Double.parseDouble(attributeValue) > max) {
                        errors.add(RemotableAttributeError.Builder.create(errorKey, DataDictionaryTypeServiceHelper
                                .createErrorString(RiceKeyConstants.ERROR_INCLUSIVE_MAX, errorLabel, max.toString())).build());
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



    /*
     * will create a list of errors in the following format:
     *
     *
     * error_key:param1;param2;param3;
     */
	protected List<String> extractErrorsFromGlobalVariablesErrorMap(String attributeName) {
		Object results = GlobalVariables.getMessageMap().getErrorMessagesForProperty(attributeName);
		List<String> errors = new ArrayList<String>();
        if (results instanceof String) {
        	errors.add((String)results);
        } else if ( results != null) {
        	if (results instanceof List) {
	        	List<?> errorList = (List<?>)results;
	        	for (Object msg : errorList) {
	        		ErrorMessage errorMessage = (ErrorMessage)msg;
	        		errors.add(DataDictionaryTypeServiceHelper.createErrorString(errorMessage.getErrorKey(),
                            errorMessage.getMessageParameters()));
				}
	        } else {
	        	String [] temp = (String []) results;
	        	for (String string : temp) {
					errors.add(string);
				}
	        }
        }
        GlobalVariables.getMessageMap().removeAllErrorMessagesForProperty(attributeName);
        return errors;
	}

	protected List<RemotableAttributeError> validateNonDataDictionaryAttribute(KimTypeAttribute attr, String key, String value) {
		return Collections.emptyList();
	}

    protected List<RemotableAttributeError> validateDataDictionaryAttribute(KimTypeAttribute attr, String key, String value) {
		try {
            // create an object of the proper type per the component
            Object componentObject = Class.forName( attr.getKimAttribute().getComponentName() ).newInstance();

            if ( attr.getKimAttribute().getAttributeName() != null ) {
                // get the bean utils descriptor for accessing the attribute on that object
                PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(componentObject, attr.getKimAttribute().getAttributeName());
                if ( propertyDescriptor != null ) {
                    // set the value on the object so that it can be checked
                    Object attributeValue = KRADUtils.hydrateAttributeValue(propertyDescriptor.getPropertyType(), value);
                    if (attributeValue == null) {
                        attributeValue = value; // not a super-awesome fallback strategy, but...
                    }
                    propertyDescriptor.getWriteMethod().invoke( componentObject, attributeValue);
                    return validateDataDictionaryAttribute(attr.getKimTypeId(), attr.getKimAttribute().getComponentName(), componentObject, propertyDescriptor);
                }
            }
        } catch (Exception e) {
            throw new KimTypeAttributeValidationException(e);
        }
        return Collections.emptyList();
	}


	/**
	 * @param namespaceCode
	 * @param typeAttribute
	 * @return an AttributeDefinition for the given KimTypeAttribute, or null no base AttributeDefinition 
	 * matches the typeAttribute parameter's attributeName.
	 */
	protected KimAttributeField getDataDictionaryAttributeDefinition( String namespaceCode, String kimTypeId, KimTypeAttribute typeAttribute, List<String> uniqueAttributes) {

		final String componentClassName = typeAttribute.getKimAttribute().getComponentName();
		final String attributeName = typeAttribute.getKimAttribute().getAttributeName();
        final Class<? extends BusinessObject> componentClass;
        final AttributeDefinition baseDefinition;

		// try to resolve the component name - if not possible - try to pull the definition from the app mediation service
		try {
            if (StringUtils.isNotBlank(componentClassName)) {
                componentClass = (Class<? extends BusinessObject>) Class.forName(componentClassName);
                baseDefinition = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(componentClassName).getAttributeDefinition(attributeName);
            } else {
                baseDefinition = null;
                componentClass = null;
            }
        } catch (ClassNotFoundException ex) {
            throw new KimTypeAttributeException(ex);
		}

        if (baseDefinition == null) {
            return null;
        }
        final RemotableAttributeField.Builder definition = RemotableAttributeField.Builder.create(baseDefinition.getName());

        definition.setLongLabel(baseDefinition.getLabel());
        definition.setShortLabel(baseDefinition.getShortLabel());
        definition.setMaxLength(baseDefinition.getMaxLength());
        definition.setRequired(baseDefinition.isRequired());
        definition.setForceUpperCase(baseDefinition.getForceUppercase());
        definition.setControl(DataDictionaryTypeServiceHelper.toRemotableAbstractControlBuilder(
                baseDefinition));
        final RemotableQuickFinder.Builder qf = createQuickFinder(componentClass, attributeName);
        if (qf != null) {
            definition.setWidgets(Collections.<RemotableAbstractWidget.Builder>singletonList(qf));
        }
        final KimAttributeField.Builder kimField = KimAttributeField.Builder.create(definition, typeAttribute.getKimAttribute().getId());

        if(uniqueAttributes!=null && uniqueAttributes.contains(definition.getName())){
            kimField.setUnique(true);
        }

		return kimField.build();
	}

    private RemotableQuickFinder.Builder createQuickFinder(Class<? extends BusinessObject> componentClass, String attributeName) {

        Field field = FieldUtils.getPropertyField(componentClass, attributeName, false);
        if ( field != null ) {
            final BusinessObject sampleComponent;
            try {
                sampleComponent = componentClass.newInstance();
            } catch(InstantiationException e) {
                throw new KimTypeAttributeException(e);
            } catch (IllegalAccessException e) {
                throw new KimTypeAttributeException(e);
            }

            field = LookupUtils.setFieldQuickfinder( sampleComponent, attributeName, field, Collections.singletonList(attributeName) );
            if ( StringUtils.isNotBlank( field.getQuickFinderClassNameImpl() ) ) {
                final Class<? extends BusinessObject> lookupClass;
                try {
                    lookupClass = (Class<? extends BusinessObject>) Class.forName( field.getQuickFinderClassNameImpl() );
                } catch (ClassNotFoundException e) {
                    throw new KimTypeAttributeException(e);
                }

                String baseLookupUrl = LookupUtils.getBaseLookupUrl(false) + "?methodToCall=start";

                if (ExternalizableBusinessObjectUtils.isExternalizableBusinessObject(lookupClass)) {
                    ModuleService moduleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(lookupClass);
                    if (moduleService.isExternalizableBusinessObjectLookupable(lookupClass)) {
                        baseLookupUrl = moduleService.getExternalizableBusinessObjectLookupUrl(lookupClass, Collections.<String,String>emptyMap());
                        // XXX: I'm not proud of this:
                        baseLookupUrl = baseLookupUrl.substring(0,baseLookupUrl.indexOf("?")) + "?methodToCall=start";
                    }
                }

                final RemotableQuickFinder.Builder builder =
                        RemotableQuickFinder.Builder.create(baseLookupUrl, lookupClass.getName());
                builder.setLookupParameters(toMap(field.getLookupParameters()));
                builder.setFieldConversions(toMap(field.getFieldConversions()));
                return builder;
            }
        }
        return null;
    }

    private static Map<String, String> toMap(String s) {
        if (StringUtils.isBlank(s)) {
            return Collections.emptyMap();
        }
        final Map<String, String> map = new HashMap<String, String>();
        for (String string : s.split(",")) {
            String [] keyVal = string.split(":");
            map.put(keyVal[0], keyVal[1]);
        }
        return Collections.unmodifiableMap(map);
    }

	protected KimAttributeField getNonDataDictionaryAttributeDefinition(String namespaceCode, String kimTypeId, KimTypeAttribute typeAttribute, List<String> uniqueAttributes) {
		RemotableAttributeField.Builder field = RemotableAttributeField.Builder.create(typeAttribute.getKimAttribute().getAttributeName());
		field.setLongLabel(typeAttribute.getKimAttribute().getAttributeLabel());

        //KULRICE-9143 shortLabel must be set for KIM to render attribute
        field.setShortLabel(typeAttribute.getKimAttribute().getAttributeLabel());

        KimAttributeField.Builder definition = KimAttributeField.Builder.create(field, typeAttribute.getKimAttribute().getId());

        if(uniqueAttributes!=null && uniqueAttributes.contains(typeAttribute.getKimAttribute().getAttributeName())){
            definition.setUnique(true);
        }
		return definition.build();
	}

	protected static final String COMMA_SEPARATOR = ", ";

	protected void validateRequiredAttributesAgainstReceived(Map<String, String> receivedAttributes){
		// abort if type does not want the qualifiers to be checked
		if ( !isCheckRequiredAttributes() ) {
			return;
		}
		// abort if the list is empty, no attributes need to be checked
		if ( getRequiredAttributes() == null || getRequiredAttributes().isEmpty() ) {
			return;
		}
		List<String> missingAttributes = new ArrayList<String>();
		// if attributes are null or empty, they're all missing
		if ( receivedAttributes == null || receivedAttributes.isEmpty() ) {
			return;		
		} else {
			for( String requiredAttribute : getRequiredAttributes() ) {
				if( !receivedAttributes.containsKey(requiredAttribute) ) {
					missingAttributes.add(requiredAttribute);
				}
			}
		}
        if(!missingAttributes.isEmpty()) {
        	StringBuilder errorMessage = new StringBuilder();
        	Iterator<String> attribIter = missingAttributes.iterator();
        	while ( attribIter.hasNext() ) {
        		errorMessage.append( attribIter.next() );
        		if( attribIter.hasNext() ) {
        			errorMessage.append( COMMA_SEPARATOR );
        		}
        	}
        	errorMessage.append( " not found in required attributes for this type." );
            throw new KimTypeAttributeValidationException(errorMessage.toString());
        }
	}


	@Override
	public List<RemotableAttributeError> validateUniqueAttributes(String kimTypeId, Map<String, String> newAttributes, Map<String, String> oldAttributes) {
        if (StringUtils.isBlank(kimTypeId)) {
            throw new RiceIllegalArgumentException("kimTypeId was null or blank");
        }

        if (newAttributes == null) {
            throw new RiceIllegalArgumentException("newAttributes was null or blank");
        }

        if (oldAttributes == null) {
            throw new RiceIllegalArgumentException("oldAttributes was null or blank");
        }
        List<String> uniqueAttributes = getUniqueAttributes(kimTypeId);
		if(uniqueAttributes==null || uniqueAttributes.isEmpty()){
			return Collections.emptyList();
		} else{
			List<RemotableAttributeError> m = new ArrayList<RemotableAttributeError>();
            if(areAttributesEqual(uniqueAttributes, newAttributes, oldAttributes)){
				//add all unique attrs to error map
                for (String a : uniqueAttributes) {
                    m.add(RemotableAttributeError.Builder.create(a, RiceKeyConstants.ERROR_DUPLICATE_ENTRY).build());
                }

                return m;
			}
		}
		return Collections.emptyList();
	}
	
	protected boolean areAttributesEqual(List<String> uniqueAttributeNames, Map<String, String> aSet1, Map<String, String> aSet2){
		StringValueComparator comparator = StringValueComparator.getInstance();
		for(String uniqueAttributeName: uniqueAttributeNames){
			String attrVal1 = getAttributeValue(aSet1, uniqueAttributeName);
			String attrVal2 = getAttributeValue(aSet2, uniqueAttributeName);
			if(comparator.compare(attrVal1, attrVal2)!=0){
				return false;
			}
		}
		return true;
	}

	protected String getAttributeValue(Map<String, String> aSet, String attributeName){
		if(StringUtils.isEmpty(attributeName)) {
			return null;
		}
		for(Map.Entry<String, String> entry : aSet.entrySet()){
			if(attributeName.equals(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}

	protected List<String> getUniqueAttributes(String kimTypeId){
		KimType kimType = getTypeInfoService().getKimType(kimTypeId);
        List<String> uniqueAttributes = new ArrayList<String>();
        if ( kimType != null ) {
	        for(KimTypeAttribute attributeDefinition: kimType.getAttributeDefinitions()){
	        	uniqueAttributes.add(attributeDefinition.getKimAttribute().getAttributeName());
	        }
        } else {
        	LOG.error("Unable to retrieve a KimTypeInfo for a null kimTypeId in getUniqueAttributes()");
        }
        return Collections.unmodifiableList(uniqueAttributes);
	}

    @Override
	public List<RemotableAttributeError> validateUnmodifiableAttributes(String kimTypeId, Map<String, String> originalAttributes, Map<String, String> newAttributes){
        if (StringUtils.isBlank(kimTypeId)) {
            throw new RiceIllegalArgumentException("kimTypeId was null or blank");
        }

        if (newAttributes == null) {
            throw new RiceIllegalArgumentException("newAttributes was null or blank");
        }

        if (originalAttributes == null) {
            throw new RiceIllegalArgumentException("oldAttributes was null or blank");
        }
        List<RemotableAttributeError> validationErrors = new ArrayList<RemotableAttributeError>();
		KimType kimType = getTypeInfoService().getKimType(kimTypeId);
		List<String> uniqueAttributes = getUniqueAttributes(kimTypeId);
		for(String attributeNameKey: uniqueAttributes){
			KimTypeAttribute attr = kimType.getAttributeDefinitionByName(attributeNameKey);
			String mainAttributeValue = getAttributeValue(originalAttributes, attributeNameKey);
			String delegationAttributeValue = getAttributeValue(newAttributes, attributeNameKey);

			if(!StringUtils.equals(mainAttributeValue, delegationAttributeValue)){
				validationErrors.add(RemotableAttributeError.Builder.create(attributeNameKey, DataDictionaryTypeServiceHelper
                        .createErrorString(RiceKeyConstants.ERROR_CANT_BE_MODIFIED,
                                dataDictionaryService.getAttributeLabel(attr.getKimAttribute().getComponentName(),
                                        attributeNameKey))).build());
			}
		}
		return validationErrors;
	}

    protected List<String> getRequiredAttributes() {
        return Collections.emptyList();
    }

	protected boolean isCheckRequiredAttributes() {
		return false;
	}

	protected String getClosestParentDocumentTypeName(
			DocumentType documentType,
			Set<String> potentialParentDocumentTypeNames) {
		if ( potentialParentDocumentTypeNames == null || documentType == null ) {
			return null;
		}
		if (potentialParentDocumentTypeNames.contains(documentType.getName())) {
			return documentType.getName();
		} 
		if ((documentType.getParentId() == null)
				|| documentType.getParentId().equals(
						documentType.getId())) {
			return null;
		} 
		return getClosestParentDocumentTypeName(getDocumentTypeService().getDocumentTypeById(documentType
				.getParentId()), potentialParentDocumentTypeNames);
	}

    protected static class KimTypeAttributeValidationException extends RuntimeException {

        protected KimTypeAttributeValidationException(String message) {
            super( message );
        }

        protected KimTypeAttributeValidationException(Throwable cause) {
            super( cause );
        }

        private static final long serialVersionUID = 8220618846321607801L;

    }

    protected static class KimTypeAttributeException extends RuntimeException {

        protected KimTypeAttributeException(String message) {
            super( message );
        }

        protected KimTypeAttributeException(Throwable cause) {
            super( cause );
        }

        private static final long serialVersionUID = 8220618846321607801L;

    }

    protected KimTypeInfoService getTypeInfoService() {
		if ( typeInfoService == null ) {
			typeInfoService = KimApiServiceLocator.getKimTypeInfoService();
		}
		return typeInfoService;
	}

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KRADServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	protected DictionaryValidationService getDictionaryValidationService() {
		if ( dictionaryValidationService == null ) {
			dictionaryValidationService = KNSServiceLocator.getKNSDictionaryValidationService();
		}
		return dictionaryValidationService;
	}

	protected DataDictionaryService getDataDictionaryService() {
		if ( dataDictionaryService == null ) {
			dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
		}
		return this.dataDictionaryService;
	}


	protected DocumentTypeService getDocumentTypeService() {
		if ( documentTypeService == null ) {
			documentTypeService = KewApiServiceLocator.getDocumentTypeService();
		}
		return this.documentTypeService;
	}
}
