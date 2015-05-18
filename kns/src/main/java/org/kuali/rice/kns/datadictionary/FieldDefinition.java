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
package org.kuali.rice.kns.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.exception.ClassValidationException;
import org.kuali.rice.krad.datadictionary.mask.Mask;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Contains field-related information for DataDictionary entries.  Used by lookups and inquiries.
 *
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 */
@Deprecated
public class FieldDefinition extends DataDictionaryDefinitionBase implements FieldDefinitionI {
    private static final long serialVersionUID = -3426603523049661524L;
    
	protected String attributeName;
    protected boolean required = false;
    protected boolean forceInquiry = false;
    protected boolean noInquiry = false;
    protected boolean noDirectInquiry = false;
    protected boolean forceLookup = false;
    protected boolean noLookup = false;
    protected boolean useShortLabel = false;
    protected String defaultValue;
    protected Class<? extends ValueFinder> defaultValueFinderClass;
    protected String quickfinderParameterString;
    protected Class<? extends ValueFinder> quickfinderParameterStringBuilderClass;

    protected Integer maxLength = null;

    protected String displayEditMode;
    protected Mask displayMask;

	protected boolean hidden 	= false;
	protected boolean readOnly 	= false;

	protected boolean treatWildcardsAndOperatorsAsLiteral = false;
	
    protected String alternateDisplayAttributeName;
    protected String additionalDisplayAttributeName;
	
	protected boolean triggerOnChange;
	protected boolean total = false;
	
    public FieldDefinition() {
    }


    /**
     * @return attributeName
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Sets attributeName to the given value.
     *
     * @param attributeName
     * @throws IllegalArgumentException if the given attributeName is blank
     */
    public void setAttributeName(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        this.attributeName = attributeName;
    }


    /**
     * @return true if this attribute is required
     */
    public boolean isRequired() {
        return required;
    }


    /**
                    required = true means that the user must enter something
                        into the search criterion lookup field
     */
    public void setRequired(boolean required) {
        this.required = required;
    }


    /**
     * @return Returns the forceInquiry.
     */
    public boolean isForceInquiry() {
        return forceInquiry;
    }


    /**
     * forceInquiry = true means that the displayed field value will
                    always be made inquirable (this attribute is not used within the code).
     */
    public void setForceInquiry(boolean forceInquiry) {
        this.forceInquiry = forceInquiry;
    }

    /**
     * @return Returns the forceLookup.
     */
    public boolean isForceLookup() {
        return forceLookup;
    }

    /**
     * forceLookup = this attribute is not used
     */
    public void setForceLookup(boolean forceLookup) {
        this.forceLookup = forceLookup;
    }

    /**
     * @return Returns the noInquiry.
     */
    public boolean isNoInquiry() {
        return noInquiry;
    }

    /**
     * @return Returns a boolean value indicating whether or not to provide
     *          a direct inquiry for the lookup field
     */
    public boolean isNoDirectInquiry()
    {
        return noDirectInquiry;
    }

    /**
     * noInquiry = true means that the displayed field will never be made inquirable.
     */
    public void setNoInquiry(boolean noInquiry) {
        this.noInquiry = noInquiry;
    }

    /**
     * @param noInquiry If true, the displayed field will not have a direct
	 *     inquiry facility
     */
    public void setNoDirectInquiry(boolean noDirectInquiry) {
        this.noDirectInquiry = noDirectInquiry;
    }

    /**
     * @return Returns the noLookup.
     */
    public boolean isNoLookup() {
        return noLookup;
    }

    /**
     * noLookup = true means that field should not include magnifying glass (i.e. quickfinder)
     */
    public void setNoLookup(boolean noLookup) {
        this.noLookup = noLookup;
    }


    /**
	 * @return the useShortLabel
	 */
	public boolean isUseShortLabel() {
		return this.useShortLabel;
	}


	/**
	 * @param useShortLabel the useShortLabel to set
	 */
	public void setUseShortLabel(boolean useShortLabel) {
		this.useShortLabel = useShortLabel;
	}


	/**
     * @return Returns the defaultValue.
     */
    public String getDefaultValue() {
        return defaultValue;
    }


    /**
           The defaultValue element will pre-load the specified value
           into the field.
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
	 * the quickfinderParameterString is a comma separated list of parameter/value pairs, of the format
	 * "param1=value1,param2=value2", where the parameters correspond to attributes of the target class
	 * for the quickfinder, and the values to literals that those attributes will default to when the
	 * quickfinder is used.
	 * @return the quickfinderParameterString
	 */
	public String getQuickfinderParameterString() {
		return this.quickfinderParameterString;
	}

	/**
	 * @param quickfinderParameterString the quickfinderParameterString to set.  See {@link #getQuickfinderParameterString()}
	 */
	public void setQuickfinderParameterString(String quickfinderParameterString) {
		this.quickfinderParameterString = quickfinderParameterString;
	}


    /**
     * the quickfinderParameterStringBuilderClass specifies the java class that will be used
     * to determine the default value(s) for field(s) on the target lookup when the quickfinder
     * is used. The classname specified in this field must implement
     * {@link ValueFinder}.  See {@link #getQuickfinderParameterString()}
     * for the result string format.
	 * @return the quickfinderParameterStringBuilderClass
	 */
	public Class<? extends ValueFinder> getQuickfinderParameterStringBuilderClass() {
		return this.quickfinderParameterStringBuilderClass;
	}

    /**
     * See {@link #getQuickfinderParameterStringBuilderClass()}
	 * @param quickfinderParameterStringBuilderClass the quickfinderParameterStringBuilderClass to set
     */
	public void setQuickfinderParameterStringBuilderClass(
			Class<? extends ValueFinder> quickfinderParameterStringBuilderClass) {
        if (quickfinderParameterStringBuilderClass == null) {
            throw new IllegalArgumentException("invalid (null) quickfinderParameterStringBuilderClass");
        }
		this.quickfinderParameterStringBuilderClass = quickfinderParameterStringBuilderClass;
    }

    /**
     * Directly validate simple fields.
     *
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(Class, Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
    	BusinessObjectMetaDataService boMetadataService = KNSServiceLocator.getBusinessObjectMetaDataService();

        if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, getAttributeName())) {
            throw new AttributeValidationException("unable to find attribute '" + attributeName + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
        }

        if (StringUtils.isNotBlank(getAlternateDisplayAttributeName())) {
            if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, getAlternateDisplayAttributeName())) {
                throw new AttributeValidationException("unable to find attribute named '" + getName() + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
            }
        }
        
        if (StringUtils.isNotBlank(getAdditionalDisplayAttributeName())) {
            if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, getAdditionalDisplayAttributeName())) {
                throw new AttributeValidationException("unable to find attribute named '" + getName() + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
            }
        }

        if (defaultValueFinderClass != null && defaultValue != null) {
            throw new AttributeValidationException("Both defaultValue and defaultValueFinderClass can not be specified on attribute " + getAttributeName() + " in rootBusinessObjectClass " + rootBusinessObjectClass.getName());
        }

        validateQuickfinderParameters(rootBusinessObjectClass, boMetadataService);

        if (forceInquiry == true && noInquiry == true) {
            throw new AttributeValidationException("Both forceInquiry and noInquiry can not be set to true on attribute " + getAttributeName() + " in rootBusinessObjectClass " + rootBusinessObjectClass.getName());
        }
        if (forceLookup == true && noLookup == true) {
            throw new AttributeValidationException("Both forceLookup and noLookup can not be set to true on attribute " + getAttributeName() + " in rootBusinessObjectClass " + rootBusinessObjectClass.getName());
        }
    }


    /**
	 * This method does validation on the quickfinderParameterString and quickfinderParameterStringBuilderClass members
	 *
	 * @param rootBusinessObjectClass
	 * @param boMetadataService
	 */
	private void validateQuickfinderParameters(Class rootBusinessObjectClass,
			BusinessObjectMetaDataService boMetadataService) {
		if (quickfinderParameterStringBuilderClass != null && quickfinderParameterString != null) {
            throw new AttributeValidationException("Both quickfinderParameterString and quickfinderParameterStringBuilderClass can not be specified on attribute " + getAttributeName() + " in rootBusinessObjectClass " + rootBusinessObjectClass.getName());
        }

        // String used for building exception messages
        String quickfinderParameterStringSource = "quickfinderParameterString";

        if (quickfinderParameterStringBuilderClass != null) {
        	try {
		        quickfinderParameterStringSource = "quickfinderParameterStringBuilderClass " + quickfinderParameterStringBuilderClass.getCanonicalName();
				quickfinderParameterString = quickfinderParameterStringBuilderClass.newInstance().getValue();
			} catch (InstantiationException e) {
				throw new ClassValidationException("unable to create new instance of "+  quickfinderParameterStringSource +" while validating rootBusinessObjectClass '"+ rootBusinessObjectClass.getName() +"'", e);
			} catch (IllegalAccessException e) {
				throw new ClassValidationException("unable to create new instance of "+  quickfinderParameterStringSource +" while validating rootBusinessObjectClass '"+ rootBusinessObjectClass.getName() +"'", e);
			}
        }

        if (!StringUtils.isEmpty(quickfinderParameterString)) {
        	// quickfinderParameterString will look something like "campusTypeCode=P,active=Y"
        	for (String quickfinderParam : quickfinderParameterString.split(",")) { // this is guaranteed to return at least one
        		if (quickfinderParam.contains("=")) {
        			String propertyName = quickfinderParam.split("=")[0];
        			RelationshipDefinition relationship = boMetadataService.getBusinessObjectRelationshipDefinition(rootBusinessObjectClass, attributeName);
        			Class targetClass = relationship.getTargetClass();

        			// This is insufficient to ensure the property is valid for a lookup default, but it's better than nothing.
            		if (!DataDictionary.isPropertyOf(targetClass, propertyName)) {
            			throw new ClassValidationException("malformed parameter string  '"+ quickfinderParameterString +"' from "+ quickfinderParameterStringSource +
            					", '"+ propertyName +"' is not a property of "+ targetClass +"' for rootBusinessObjectClass '"+ rootBusinessObjectClass.getName() +"'");
            		}

        		} else {
        			throw new ClassValidationException("malformed parameter string '"+ quickfinderParameterString +"' from "+ quickfinderParameterStringSource +
        					" for rootBusinessObjectClass '"+ rootBusinessObjectClass.getName() +"'");
        		}
        	}
        }
	}


    /**
     * @see Object#toString()
     */
    public String toString() {
        return "FieldDefinition for attribute " + getAttributeName();
    }


    public String getName() {
        return attributeName;
    }


    public String getDisplayEditMode() {
        return displayEditMode;
    }


    /*
                        The document authorizer classes have a method getEditMode, which is a map of edit mode to
                        value mappings.  Depending on the context, the value of the mapping may be relevant, and the logic determining
                        whether the value is relevant is often implemented in the JSP/tag layer.

                        Fields on a document (particularily maintenance documents) may be associated with
                        an edit mode.  If the edit mode is mapped to a relevant value, then the all fields associated with the edit mode
                        will be rendered unhidden.

                        The displayEditMode element is used to specify the edit mode that will be associated with the field.
                        If the document authorizer returns a map with this edit mode mapped to a proper value, then the field will be unhidden to the user.
     */
    public void setDisplayEditMode(String displayEditMode) {
        this.displayEditMode = displayEditMode;
    }


    public Mask getDisplayMask() {
        return displayMask;
    }

    /**
     * The displayMask element specifies the type of masking to
                    be used to hide the value from un-authorized users.
                    There are three types of masking.
     */
    public void setDisplayMask(Mask displayMask) {
        this.displayMask = displayMask;
    }



    public boolean isReadOnlyAfterAdd() {
        return false;
    }


    /**
     * Gets the maxLength attribute.
     * @return Returns the maxLength.
     */
    public Integer getMaxLength() {
        return maxLength;
    }


    /**
     * maxLength = the maximum allowable length of the field in the lookup result fields.  In other contexts,
                    like inquiries, this field has no effect.
     */
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @return custom defaultValue class
     */
    public Class<? extends ValueFinder> getDefaultValueFinderClass() {
        return this.defaultValueFinderClass;
    }

    /**
                      The defaultValueFinderClass specifies the java class that will be
                      used to determine the default value of a field.  The classname
                      specified in this field must implement ValueFinder
     */
    public void setDefaultValueFinderClass(Class<? extends ValueFinder> defaultValueFinderClass) {
        if (defaultValueFinderClass == null) {
            throw new IllegalArgumentException("invalid (null) defaultValueFinderClass");
        }
        this.defaultValueFinderClass = defaultValueFinderClass;
    }
    
	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return this.hidden;
	}

	/**
	 * @param hidden
     *  If the ControlDefinition.isHidden == true then a corresponding LookupDefinition would
     *  automatically be removed from the search criteria.  In some cases you might want the
     *  hidden field to be used as a search criteria.  For example, in PersonImpl.xml a client
     *  might want to have the campus code hidden and preset to Bloomington.  So when the search
     *  is run, only people from the bloomington campus are returned.
     *
     *   So, if you want to have a hidden search criteria, set this variable to true. Defaults to
     *   false.
     */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return this.readOnly;
	}
	
	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public boolean isTriggerOnChange() {
		return this.triggerOnChange;
	}

	public void setTriggerOnChange(boolean triggerOnChange) {
		this.triggerOnChange = triggerOnChange;
	}

	/**
	 * @return the treatWildcardsAndOperatorsAsLiteralOnLookups
	 */
	public boolean isTreatWildcardsAndOperatorsAsLiteral() {
		return this.treatWildcardsAndOperatorsAsLiteral;
	}


	/**
	 * @param treatWildcardsAndOperatorsAsLiteralOnLookups the treatWildcardsAndOperatorsAsLiteralOnLookups to set
	 */
	public void setTreatWildcardsAndOperatorsAsLiteral(
			boolean treatWildcardsAndOperatorsAsLiteralOnLookups) {
		this.treatWildcardsAndOperatorsAsLiteral = treatWildcardsAndOperatorsAsLiteralOnLookups;
	}


	public String getAlternateDisplayAttributeName() {
		return this.alternateDisplayAttributeName;
	}


	public void setAlternateDisplayAttributeName(String alternateDisplayAttributeName) {
		this.alternateDisplayAttributeName = alternateDisplayAttributeName;
	}


	public String getAdditionalDisplayAttributeName() {
		return this.additionalDisplayAttributeName;
	}


	public void setAdditionalDisplayAttributeName(String additionalDisplayAttributeName) {
		this.additionalDisplayAttributeName = additionalDisplayAttributeName;
	}


	public boolean isTotal() {
		return this.total;
	}


	public void setTotal(boolean total) {
		this.total = total;
	}
	
}
