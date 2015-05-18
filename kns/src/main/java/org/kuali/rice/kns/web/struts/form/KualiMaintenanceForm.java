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
package org.kuali.rice.kns.web.struts.form;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableAttachment;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.exception.UnknownDocumentTypeException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the base action form for all maintenance documents.
 * 
 * 
 */
public class KualiMaintenanceForm extends KualiDocumentFormBase {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiMaintenanceForm.class);

    protected static final long serialVersionUID = 1L;

    protected String businessObjectClassName;
    protected String description;
    protected boolean readOnly;
    protected Map<String, String> oldMaintainableValues;
    protected Map<String, String> newMaintainableValues;
    protected String maintenanceAction;
    private static final Pattern ELEMENT_IN_COLLECTION = Pattern.compile("(.*)(\\[)([0-9]*)(\\])(.*)");


	/**
     * @see KualiDocumentFormBase#addRequiredNonEditableProperties()
     */
    @Override
    public void addRequiredNonEditableProperties(){
    	super.addRequiredNonEditableProperties();
    	registerRequiredNonEditableProperty(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE);
    	registerRequiredNonEditableProperty(KRADConstants.LOOKUP_RESULTS_BO_CLASS_NAME);
    	registerRequiredNonEditableProperty(KRADConstants.LOOKED_UP_COLLECTION_NAME);
    	registerRequiredNonEditableProperty(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER);
    	registerRequiredNonEditableProperty(KRADConstants.FIELD_NAME_TO_FOCUS_ON_AFTER_SUBMIT);
    }

    /**
     * Used to indicate which result set we're using when refreshing/returning from a multi-value lookup
     */
    protected String lookupResultsSequenceNumber;
    /**
     * The type of result returned by the multi-value lookup
     * 
     * TODO: to be persisted in the lookup results service instead?
     */
    protected String lookupResultsBOClassName;
    
    /**
     * The name of the collection looked up (by a multiple value lookup)
     */
    protected String lookedUpCollectionName;
    
    protected MaintenanceDocumentRestrictions authorizations;
    
    /**
     * Override the default method to add the if statement which can't be called until after parameters from a multipart request
     * have been made accessible, but which must be called before the parameter values are used to instantiate and populate business
     * objects.
     * 
     * @param requestParameters
     */
    @Override
    public void postprocessRequestParameters(Map requestParameters) {
        super.postprocessRequestParameters(requestParameters);

        String docTypeName = null;
        String[] docTypeNames = (String[]) requestParameters.get(KRADConstants.DOCUMENT_TYPE_NAME);
        if ((docTypeNames != null) && (docTypeNames.length > 0)) {
            docTypeName = docTypeNames[0];
        }

        if (StringUtils.isNotBlank(docTypeName)) {          
        	if(this.getDocument() == null){
            setDocTypeName(docTypeName);
            Class documentClass = KRADServiceLocatorWeb.getDataDictionaryService().getDocumentClassByTypeName(docTypeName);
            if (documentClass == null) {
                throw new UnknownDocumentTypeException("unable to get class for unknown documentTypeName '" + docTypeName + "'");
            }
            if (!MaintenanceDocumentBase.class.isAssignableFrom(documentClass)) {
                throw new ConfigurationException("Document class '" + documentClass + "' is not assignable to '" + MaintenanceDocumentBase.class + "'");
            }
            Document document = null;
            try {
                Class[] defaultConstructor = new Class[]{String.class};
                Constructor cons = documentClass.getConstructor(defaultConstructor);
                if (ObjectUtils.isNull(cons)) {
                    throw new ConfigurationException("Could not find constructor with document type name parameter needed for Maintenance Document Base class");
                }
                document = (Document) cons.newInstance(docTypeName);
            } catch (SecurityException e) {
                throw new RuntimeException("Error instantiating Maintenance Document", e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Error instantiating Maintenance Document: No constructor with String parameter found", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error instantiating Maintenance Document", e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Error instantiating Maintenance Document", e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Error instantiating Maintenance Document", e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Error instantiating Maintenance Document", e);
            }
            if (document == null) {
                throw new RuntimeException("Unable to instantiate document with type name '" + docTypeName + "' and document class '" + documentClass + "'");
            }
            setDocument(document);
          } 
       }
        
        MaintenanceDocumentBase maintenanceDocument = (MaintenanceDocumentBase) getDocument();

        //Handling the Multi-Part Attachment
        for ( Object obj : requestParameters.entrySet() ) {
            String parameter = (String)((Map.Entry)obj).getKey(); 
            if (parameter.toUpperCase().startsWith(KRADConstants.MAINTENANCE_NEW_MAINTAINABLE.toUpperCase())) {
                String propertyName = parameter.substring(KRADConstants.MAINTENANCE_NEW_MAINTAINABLE.length());
                Object propertyValue = requestParameters.get(parameter);
                
                if(propertyValue != null && propertyValue instanceof FormFile) {
                    populateAttachmentFile(maintenanceDocument, propertyName, (FormFile) propertyValue);
                    if (propertyName.startsWith(KRADConstants.MAINTENANCE_ADD_PREFIX)) {
                        String parsedPropertyName = propertyName.substring(
                                KRADConstants.MAINTENANCE_ADD_PREFIX.length());
                        String collectionName = parseAddCollectionName(parseAddCollectionName(parsedPropertyName));
                        maintenanceDocument.setAttachmentCollectionName(collectionName);
                        maintenanceDocument.setAttachmentListPropertyName(propertyName.substring(KRADConstants.MAINTENANCE_ADD_PREFIX.length()).substring(collectionName.length() + 1));
                    } else {
                        //if property not part of collection
                        Matcher matcher = ELEMENT_IN_COLLECTION.matcher(propertyName);
                        if (!matcher.matches()) {
                            maintenanceDocument.setAttachmentPropertyName(propertyName);
                        }
                    }
                }
            }
        }
    }

    private void populateAttachmentFile(MaintenanceDocumentBase maintenanceDocument, String propertyName, FormFile propertyValue) {
         if(StringUtils.isNotEmpty(((FormFile)propertyValue).getFileName())) {
 	 	 	 PersistableBusinessObject boClass;
             String boPropertyName;

             Matcher matcher = ELEMENT_IN_COLLECTION.matcher(propertyName);
             if (propertyName.startsWith(KRADConstants.MAINTENANCE_ADD_PREFIX)) {
                 String prefix = matcher.matches() ? "" : KRADConstants.MAINTENANCE_ADD_PREFIX;
                 String collectionName = parseAddCollectionName(propertyName.substring(prefix.length()));
                 boClass = maintenanceDocument.getNewMaintainableObject().getNewCollectionLine(collectionName);
                 boPropertyName = propertyName.substring(prefix.length()).substring(collectionName.length() + 1);

                 setAttachmentProperty(boClass, boPropertyName, propertyValue);
             } else {
                 boClass = maintenanceDocument.getNewMaintainableObject().getBusinessObject();
                 boPropertyName = propertyName;
                 if(StringUtils.isNotEmpty(((FormFile)propertyValue).getFileName())
                         && !matcher.matches()) {
                     maintenanceDocument.setFileAttachment((FormFile) propertyValue);
                 }
                 setAttachmentProperty(boClass, boPropertyName, propertyValue);
             }
         }
    }

    private void setAttachmentProperty(PersistableBusinessObject boClass, String propertyName, Object propertyValue) {
        try {
            PropertyUtils.setProperty(boClass, propertyName, propertyValue);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("no setter for property '" + boClass.getClass().getName() + "." + propertyName + "'", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("no setter for property '" + boClass.getClass().getName() + "." + propertyName + "'", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("problem accessing property '" + boClass.getClass().getName() + "." + propertyName + "'", e);
        }
    }

    /**
     * Hook into populate so we can set the maintenance documents and feed the field values to its maintainables.
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);


        // document type name is null on start, otherwise should be here
        if (StringUtils.isNotBlank(getDocTypeName())) {
            Map<String, String> localOldMaintainableValues = new HashMap<String, String>();
            Map<String, String> localNewMaintainableValues = new HashMap<String, String>();
            Map<String,String> localNewCollectionValues = new HashMap<String,String>();
            for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
                String parameter = (String) i.nextElement();
                if (parameter.toUpperCase().startsWith(KRADConstants.MAINTENANCE_OLD_MAINTAINABLE.toUpperCase())) {
                	if (shouldPropertyBePopulatedInForm(parameter, request)) {
                        String propertyName = parameter.substring(KRADConstants.MAINTENANCE_OLD_MAINTAINABLE.length());
                        localOldMaintainableValues.put(propertyName, request.getParameter(parameter));
                    }
                }
                if (parameter.toUpperCase().startsWith(KRADConstants.MAINTENANCE_NEW_MAINTAINABLE.toUpperCase())) {
                	if (shouldPropertyBePopulatedInForm(parameter, request)) {
                        String propertyName = parameter.substring(KRADConstants.MAINTENANCE_NEW_MAINTAINABLE.length());
                        localNewMaintainableValues.put(propertyName, request.getParameter(parameter));
                    }
                }
            }
            
            // now, get all add lines and store them to a separate map
            // for use in a separate call to the maintainable
            for ( Map.Entry<String, String> entry : localNewMaintainableValues.entrySet() ) {
                String key = entry.getKey(); 
                if ( key.startsWith( KRADConstants.MAINTENANCE_ADD_PREFIX ) ) {
                    localNewCollectionValues.put( key.substring( KRADConstants.MAINTENANCE_ADD_PREFIX.length() ),
                            entry.getValue() );
                }
            }
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "checked for add line parameters - got: " + localNewCollectionValues );
            }
            
            this.newMaintainableValues = localNewMaintainableValues;
            this.oldMaintainableValues = localOldMaintainableValues;

            MaintenanceDocumentBase maintenanceDocument = (MaintenanceDocumentBase) getDocument();

            GlobalVariables.getMessageMap().addToErrorPath("document.oldMaintainableObject");
            maintenanceDocument.getOldMaintainableObject().populateBusinessObject(localOldMaintainableValues, maintenanceDocument, getMethodToCall());
            GlobalVariables.getMessageMap().removeFromErrorPath("document.oldMaintainableObject");

            GlobalVariables.getMessageMap().addToErrorPath("document.newMaintainableObject");
            // update the main object
            Map cachedValues = 
            	maintenanceDocument.getNewMaintainableObject().populateBusinessObject(localNewMaintainableValues, maintenanceDocument, getMethodToCall());
            
            if(maintenanceDocument.getFileAttachment() != null) {
                populateAttachmentPropertyForBO(maintenanceDocument);
            }
            
            // update add lines
            localNewCollectionValues = KimApiServiceLocator.getPersonService().resolvePrincipalNamesToPrincipalIds((BusinessObject)maintenanceDocument.getNewMaintainableObject().getBusinessObject(), localNewCollectionValues);
            cachedValues.putAll( maintenanceDocument.getNewMaintainableObject().populateNewCollectionLines( localNewCollectionValues, maintenanceDocument, getMethodToCall() ) );
            GlobalVariables.getMessageMap().removeFromErrorPath("document.newMaintainableObject");

            if (cachedValues.size() > 0) {
                GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, RiceKeyConstants.ERROR_DOCUMENT_MAINTENANCE_FORMATTING_ERROR);
                for (Iterator iter = cachedValues.keySet().iterator(); iter.hasNext();) {
                    String propertyName = (String) iter.next();
                    String value = (String) cachedValues.get(propertyName);
                    cacheUnconvertedValue(KRADConstants.MAINTENANCE_NEW_MAINTAINABLE + propertyName, value);
                }
            }
        }
    }

    protected void populateAttachmentPropertyForBO(MaintenanceDocumentBase maintenanceDocument) {
        try {
            Object dataObject = maintenanceDocument.getNewMaintainableObject().getDataObject();
            if (dataObject instanceof PersistableAttachment) {
                Class type = ObjectUtils.easyGetPropertyType(maintenanceDocument.getNewMaintainableObject().getDataObject(), maintenanceDocument.getAttachmentPropertyName());
                ObjectUtils.setObjectProperty(maintenanceDocument.getNewMaintainableObject().getBusinessObject(), maintenanceDocument.getAttachmentPropertyName(), type, maintenanceDocument.getFileAttachment());
            }
        } catch (FormatException e) {
            throw new RuntimeException("Exception occurred while setting attachment property on NewMaintainable bo", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Exception occurred while setting attachment property on NewMaintainable bo", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Exception occurred while setting attachment property on NewMaintainable bo", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Exception occurred while setting attachment property on NewMaintainable bo", e);
        }
    }
    
    /**
     * Merges rows of old and new for each section (tab) of the ui. Also, renames fields to prevent naming conflicts and does
     * setting of read only fields.
     * 
     * @return Returns the maintenanceSections.
     */
    public List getSections() {
        if (getDocument() == null) {
            throw new RuntimeException("Document not set in maintenance form.");
        }
        if (((MaintenanceDocumentBase) getDocument()).getNewMaintainableObject() == null) {
            throw new RuntimeException("New maintainable not set in document.");
        }
        if ((KRADConstants.MAINTENANCE_EDIT_ACTION.equals(this.getMaintenanceAction())
        		|| KRADConstants.MAINTENANCE_COPY_ACTION.equals(this.getMaintenanceAction())
        		|| KRADConstants.MAINTENANCE_DELETE_ACTION.equals(this.getMaintenanceAction()))
        		&& ((MaintenanceDocumentBase) getDocument()).getOldMaintainableObject() == null) {
            throw new RuntimeException("Old maintainable not set in document.");
        }

        // if the authorization stuff hasnt been applied yet, then apply it
        //if (authorizations == null) {
        //    applyAuthorizations();
        //}

        
        // get business object being maintained and its keys
        List keyFieldNames = KNSServiceLocator.getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(((MaintenanceDocumentBase) getDocument()).getNewMaintainableObject().getBusinessObject().getClass());

        // sections for maintenance document
        Maintainable oldMaintainable = ((MaintenanceDocumentBase) getDocument()).getOldMaintainableObject();
        oldMaintainable.setMaintenanceAction(getMaintenanceAction());
        List oldMaintSections = oldMaintainable.getSections((MaintenanceDocument) getDocument(), null);
        
        Maintainable newMaintainable = ((MaintenanceDocumentBase) getDocument()).getNewMaintainableObject();
        newMaintainable.setMaintenanceAction(getMaintenanceAction());
        List newMaintSections = newMaintainable.getSections((MaintenanceDocument) getDocument(), oldMaintainable);
        WorkflowDocument workflowDocument = this.getDocument().getDocumentHeader().getWorkflowDocument();
        String documentStatus =  workflowDocument.getStatus().getCode();
        String documentInitiatorPrincipalId = workflowDocument.getInitiatorPrincipalId();
        

        // mesh sections for proper jsp display
        List meshedSections = FieldUtils
                .meshSections(oldMaintSections, newMaintSections, keyFieldNames, getMaintenanceAction(), isReadOnly(),
                        authorizations, documentStatus, documentInitiatorPrincipalId);

        return meshedSections;
    }    

    /**
     * @return Returns the maintenanceAction.
     */
    public String getMaintenanceAction() {
        return maintenanceAction;
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
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the isReadOnly.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @param readOnly The isReadOnly to set.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @return Returns the newMaintainableValues.
     */
    public Map getNewMaintainableValues() {
        return newMaintainableValues;
    }

    /**
     * @return Returns the oldMaintainableValues.
     */
    public Map getOldMaintainableValues() {
        return oldMaintainableValues;
    }

    /**
     * @param maintenanceAction The maintenanceAction to set.
     */
    public void setMaintenanceAction(String maintenanceAction) {
        this.maintenanceAction = maintenanceAction;
    }

    /**
     * Gets the authorizations attribute.
     * 
     * @return Returns the authorizations.
     */
    public MaintenanceDocumentRestrictions getAuthorizations() {
        return authorizations;
    }

    /**
     * Sets the authorizations attribute value.
     * 
     * @param authorizations The authorizations to set.
     */
    public void setAuthorizations(MaintenanceDocumentRestrictions authorizations) {
        this.authorizations = authorizations;
    }

    /**
     * Sets the newMaintainableValues attribute value.
     * 
     * @param newMaintainableValues The newMaintainableValues to set.
     */
    public void setNewMaintainableValues(Map newMaintainableValues) {
        this.newMaintainableValues = newMaintainableValues;
    }


    /**
     * Sets the oldMaintainableValues attribute value.
     * 
     * @param oldMaintainableValues The oldMaintainableValues to set.
     */
    public void setOldMaintainableValues(Map oldMaintainableValues) {
        this.oldMaintainableValues = oldMaintainableValues;
    }


    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }


    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }


    public String getLookupResultsBOClassName() {
        return lookupResultsBOClassName;
    }


    public void setLookupResultsBOClassName(String lookupResultsBOClassName) {
        this.lookupResultsBOClassName = lookupResultsBOClassName;
    }


    public String getLookedUpCollectionName() {
        return lookedUpCollectionName;
    }


    public void setLookedUpCollectionName(String lookedUpCollectionName) {
        this.lookedUpCollectionName = lookedUpCollectionName;
    }

    public String getAdditionalSectionsFile() {
        if ( businessObjectClassName != null ) {
            try {
                MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService = KNSServiceLocator
                        .getMaintenanceDocumentDictionaryService();
                String docTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(Class.forName(businessObjectClassName));
                return maintenanceDocumentDictionaryService.getMaintenanceDocumentEntry(businessObjectClassName).getAdditionalSectionsFile();
            } catch ( ClassNotFoundException ex ) {
                LOG.error( "Unable to resolve business object class", ex);
            }
        }else{
            MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService = KNSServiceLocator
                    .getMaintenanceDocumentDictionaryService();
            return maintenanceDocumentDictionaryService.getMaintenanceDocumentEntry(this.getDocTypeName()).getAdditionalSectionsFile();
        }
        return null;
    }

	/**
	 * This overridden method handles the case where maint doc properties do not reflect the true nature of the 
	 * 
	 * @see KualiForm#retrieveFormValueForLookupInquiryParameters(String, String)
	 */
	@Override
	public String retrieveFormValueForLookupInquiryParameters(String parameterName, String parameterValueLocation) {
		MaintenanceDocument maintDoc = (MaintenanceDocument) getDocument();
		if (parameterValueLocation.toLowerCase().startsWith(KRADConstants.MAINTENANCE_OLD_MAINTAINABLE.toLowerCase())) {
			String propertyName = parameterValueLocation.substring(KRADConstants.MAINTENANCE_OLD_MAINTAINABLE.length());
			if (maintDoc.getOldMaintainableObject() != null && maintDoc.getOldMaintainableObject().getBusinessObject() != null) {
				Object parameterValue = ObjectUtils.getPropertyValue(maintDoc.getOldMaintainableObject().getBusinessObject(), propertyName);
				if (parameterValue == null) {
					return null;
				}
				if (parameterValue instanceof String) {
					return (String) parameterValue;
				}
				Formatter formatter = Formatter.getFormatter(parameterValue.getClass());
				return (String) formatter.format(parameterValue); 
			}
		}
		if (parameterValueLocation.toLowerCase().startsWith(KRADConstants.MAINTENANCE_NEW_MAINTAINABLE.toLowerCase())) {
			// remove MAINT_NEW_MAINT from the pVL
			String propertyName = parameterValueLocation.substring(KRADConstants.MAINTENANCE_NEW_MAINTAINABLE.length());
			String addPrefix = KRADConstants.ADD_PREFIX.toLowerCase() + ".";

			if (propertyName.toLowerCase().startsWith(addPrefix)) { // 
				propertyName = propertyName.substring(addPrefix.length()); // remove addPrefix from the propertyName
				String collectionName = parseAddCollectionName(propertyName);
				propertyName = propertyName.substring(collectionName.length()); // remove collectionName from pN
				if (propertyName.startsWith(".")) { propertyName = propertyName.substring(1); } // strip beginning "."
				PersistableBusinessObject newCollectionLine = 
					maintDoc.getNewMaintainableObject().getNewCollectionLine(collectionName);
				Object parameterValue = ObjectUtils.getPropertyValue(newCollectionLine, propertyName);
				if (parameterValue == null) {
					return null;
				}
				if (parameterValue instanceof String) {
					return (String) parameterValue;
				}
				Formatter formatter = Formatter.getFormatter(parameterValue.getClass());
				return (String) formatter.format(parameterValue);
			} else if (maintDoc.getNewMaintainableObject() != null && maintDoc.getNewMaintainableObject().getBusinessObject() != null) {
				Object parameterValue = ObjectUtils.getPropertyValue(maintDoc.getNewMaintainableObject().getBusinessObject(), propertyName);
				if (parameterValue == null) {
					return null;
				}
				if (parameterValue instanceof String) {
					return (String) parameterValue;
				}
				Formatter formatter = Formatter.getFormatter(parameterValue.getClass());
				return (String) formatter.format(parameterValue); 
			}
		}
		return super.retrieveFormValueForLookupInquiryParameters(parameterName, parameterValueLocation);
	}

	/**
	 * This method returns the collection name (including nested collections) from a propertyName string
	 * 
	 * @param propertyName a parameterValueLocation w/ KRADConstants.MAINTENANCE_NEW_MAINTAINABLE +
	 * KRADConstants.ADD_PREFIX + "." stripped off the front
	 * @return the collectionName
	 */
	protected String parseAddCollectionName(String propertyName) {
		StringBuilder collectionNameBuilder = new StringBuilder();

		boolean firstPathElement = true;
        for (String pathElement : propertyName.split("\\.")) {
            if (!StringUtils.isBlank(pathElement)) {
                if (firstPathElement) {
                    firstPathElement = false;
                } else {
                    collectionNameBuilder.append(".");
                }
                collectionNameBuilder.append(pathElement);
                if (!(pathElement.endsWith("]") && pathElement.contains("[")))
                    break;
            }
        }
		String collectionName = collectionNameBuilder.toString();
		return collectionName;
	}


	/**
	 * This overridden method ...
	 * 
	 * @see KualiDocumentFormBase#shouldPropertyBePopulatedInForm(String, HttpServletRequest)
	 */
	@Override
	public boolean shouldPropertyBePopulatedInForm(
			String requestParameterName, HttpServletRequest request) {
		// the user clicked on a document initiation link
		//add delete check for 3070
		String methodToCallActionName = request.getParameter(KRADConstants.DISPATCH_REQUEST_PARAMETER);
		if (StringUtils.equals(methodToCallActionName, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL) ||
				StringUtils.equals(methodToCallActionName, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL) ||
				StringUtils.equals(methodToCallActionName, KRADConstants.MAINTENANCE_NEW_METHOD_TO_CALL) ||
				StringUtils.equals(methodToCallActionName, KRADConstants.MAINTENANCE_NEWWITHEXISTING_ACTION) ||
				StringUtils.equals(methodToCallActionName, KRADConstants.MAINTENANCE_DELETE_METHOD_TO_CALL)) {
			return true;
		}
		if ( StringUtils.indexOf(methodToCallActionName, KRADConstants.TOGGLE_INACTIVE_METHOD ) == 0 ) {
			return true;
		}
		return super.shouldPropertyBePopulatedInForm(requestParameterName, request);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see KualiDocumentFormBase#shouldMethodToCallParameterBeUsed(String, String, HttpServletRequest)
	 */
	@Override
	public boolean shouldMethodToCallParameterBeUsed(
			String methodToCallParameterName,
			String methodToCallParameterValue, HttpServletRequest request) {
		// the user clicked on a document initiation link
		if (StringUtils.equals(methodToCallParameterValue, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL) ||
				StringUtils.equals(methodToCallParameterValue, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL) ||
				StringUtils.equals(methodToCallParameterValue, KRADConstants.MAINTENANCE_NEW_METHOD_TO_CALL) ||
				StringUtils.equals(methodToCallParameterValue, KRADConstants.MAINTENANCE_NEWWITHEXISTING_ACTION) ||
				StringUtils.equals(methodToCallParameterValue, KRADConstants.MAINTENANCE_DELETE_METHOD_TO_CALL)) {
			return true;
		}
		if ( StringUtils.indexOf(methodToCallParameterName, KRADConstants.DISPATCH_REQUEST_PARAMETER + "." + KRADConstants.TOGGLE_INACTIVE_METHOD ) == 0 ) {
			return true;
		}
		return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName,
				methodToCallParameterValue, request);
	}
}


