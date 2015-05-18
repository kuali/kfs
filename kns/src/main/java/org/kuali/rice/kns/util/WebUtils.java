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
package org.kuali.rice.kns.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServletWrapper;
import org.apache.struts.upload.CommonsMultipartRequestHandler;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.RecipientType;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.datadictionary.KNSDocumentEntry;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.kns.web.struts.action.KualiMultipartRequestHandler;
import org.kuali.rice.kns.web.struts.form.InquiryForm;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.struts.form.KualiMaintenanceForm;
import org.kuali.rice.kns.web.struts.form.pojo.PojoFormBase;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.rice.krad.datadictionary.mask.MaskFormatter;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * General helper methods for handling requests.
 */
public class WebUtils {
	private static final Logger LOG = Logger.getLogger(WebUtils.class);

	private static final String IMAGE_COORDINATE_CLICKED_X_EXTENSION = ".x";
	private static final String IMAGE_COORDINATE_CLICKED_Y_EXTENSION = ".y";

	private static final String APPLICATION_IMAGE_URL_PROPERTY_PREFIX = "application.custom.image.url";
	private static final String DEFAULT_IMAGE_URL_PROPERTY_NAME = "kr.externalizable.images.url";

    /**
     * Prefixes indicating an absolute url
     */
    private static final String[] SCHEMES = { "http://", "https://" };

	/**
	 * A request attribute name that indicates that a
	 * {@link org.kuali.rice.kns.exception.FileUploadLimitExceededException} has already been thrown for the
	 * request.
	 */
	public static final String FILE_UPLOAD_LIMIT_EXCEEDED_EXCEPTION_ALREADY_THROWN = "fileUploadLimitExceededExceptionAlreadyThrown";

	private static ConfigurationService configurationService;

	/**
	 * Checks for methodToCall parameter, and picks off the value using set dot
	 * notation. Handles the problem of image submits.
	 * 
	 * @param request
	 * @return methodToCall String
	 */
	public static String parseMethodToCall(ActionForm form, HttpServletRequest request) {
		String methodToCall = null;

		// check if is specified cleanly
		if (StringUtils.isNotBlank(request.getParameter(KRADConstants.DISPATCH_REQUEST_PARAMETER))) {
			if (form instanceof KualiForm
					&& !((KualiForm) form).shouldMethodToCallParameterBeUsed(KRADConstants.DISPATCH_REQUEST_PARAMETER,
							request.getParameter(KRADConstants.DISPATCH_REQUEST_PARAMETER), request)) {
				throw new RuntimeException("Cannot verify that the methodToCall should be "
						+ request.getParameter(KRADConstants.DISPATCH_REQUEST_PARAMETER));
			}
			methodToCall = request.getParameter(KRADConstants.DISPATCH_REQUEST_PARAMETER);
			// include .x at the end of the parameter to make it consistent w/
			// other parameters
			request.setAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE, KRADConstants.DISPATCH_REQUEST_PARAMETER + "."
					+ methodToCall + IMAGE_COORDINATE_CLICKED_X_EXTENSION);
		}

		/**
		 * The reason why we are checking for a ".x" at the end of the parameter
		 * name: It is for the image names that in addition to sending the form
		 * data, the web browser sends the x,y coordinate of where the user
		 * clicked on the image. If the image input is not given a name then the
		 * browser sends the x and y coordinates as the "x" and "y" input
		 * fields. If the input image does have a name, the x and y coordinates
		 * are sent using the format name.x and name.y.
		 */
		if (methodToCall == null) {
			// iterate through parameters looking for methodToCall
			for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
				String parameterName = (String) i.nextElement();

				// check if the parameter name is a specifying the methodToCall
				if (isMethodToCall(parameterName)) {
					methodToCall = getMethodToCallSettingAttribute(form, request, parameterName);
					break;
				}
				else {
					// KULRICE-1218: Check if the parameter's values match (not
					// just the name)
					for (String value : request.getParameterValues(parameterName)) {
						// adding period to startsWith check - don't want to get
						// confused with methodToCallFoobar
						if (isMethodToCall(value)) {
							methodToCall = getMethodToCallSettingAttribute(form, request, value);
							// why is there not a break outer loop here?
						}
					}
				}
			}
		}

		return methodToCall;
	}

    /**
	 * Checks if a string signifies a methodToCall string
	 * 
	 * @param string
	 *            the string to check
	 * @return true if is a methodToCall
	 */
	private static boolean isMethodToCall(String string) {
		// adding period to startsWith check - don't want to get confused with
		// methodToCallFoobar
		return string.startsWith(KRADConstants.DISPATCH_REQUEST_PARAMETER + ".");
	}

	/**
	 * Parses out the methodToCall command and also sets the request attribute
	 * for the methodToCall.
	 * 
	 * @param form
	 *            the ActionForm
	 * @param request
	 *            the request to set the attribute on
	 * @param string
	 *            the methodToCall string
	 * @return the methodToCall command
	 */
	private static String getMethodToCallSettingAttribute(ActionForm form, HttpServletRequest request, String string) {

		if (form instanceof KualiForm
				&& !((KualiForm) form).shouldMethodToCallParameterBeUsed(string, request.getParameter(string), request)) {
			throw new RuntimeException("Cannot verify that the methodToCall should be " + string);
		}
		// always adding a coordinate even if not an image
		final String attributeValue = endsWithCoordinates(string) ? string : string
				+ IMAGE_COORDINATE_CLICKED_X_EXTENSION;
		final String methodToCall = StringUtils.substringBetween(attributeValue,
				KRADConstants.DISPATCH_REQUEST_PARAMETER + ".", ".");
		request.setAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE, attributeValue);
		return methodToCall;
	}

	/**
	 * Iterates through and logs (at the given level) all attributes and
	 * parameters of the given request onto the given Logger
	 * 
	 * @param request
	 * @param logger
	 */
	public static void logRequestContents(Logger logger, Level level, HttpServletRequest request) {
		if (logger.isEnabledFor(level)) {
			logger.log(level, "--------------------");
			logger.log(level, "HttpRequest attributes:");
			for (Enumeration e = request.getAttributeNames(); e.hasMoreElements();) {
				String attrName = (String) e.nextElement();
				Object attrValue = request.getAttribute(attrName);

				if (attrValue.getClass().isArray()) {
					logCollection(logger, level, attrName, Arrays.asList((Object[]) attrValue));
				}
				else if (attrValue instanceof Collection) {
					logCollection(logger, level, attrName, (Collection) attrValue);
				}
				else if (attrValue instanceof Map) {
					logMap(logger, level, attrName, (Map) attrValue);
				}
				else {
					logObject(logger, level, attrName, attrValue);
				}
			}

			logger.log(level, "--------------------");
			logger.log(level, "HttpRequest parameters:");
			for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
				String paramName = (String) i.nextElement();
				String[] paramValues = (String[]) request.getParameterValues(paramName);

				logArray(logger, level, paramName, paramValues);
			}

			logger.log(level, "--------------------");
		}
	}

	private static void logArray(Logger logger, Level level, String arrayName, Object[] array) {
		StringBuffer value = new StringBuffer("[");
		for (int i = 0; i < array.length; ++i) {
			if (i > 0) {
				value.append(",");
			}
			value.append(array[i]);
		}
		value.append("]");

		logThing(logger, level, arrayName, value);
	}

	private static void logCollection(Logger logger, Level level, String collectionName, Collection c) {
		StringBuffer value = new StringBuffer("{");
		for (Iterator i = c.iterator(); i.hasNext();) {
			value.append(i.next());
			if (i.hasNext()) {
				value.append(",");
			}
		}
		value.append("}");

		logThing(logger, level, collectionName, value);
	}

	private static void logMap(Logger logger, Level level, String mapName, Map m) {
		StringBuffer value = new StringBuffer("{");
		for (Iterator i = m.entrySet().iterator(); i.hasNext();) {
			Map.Entry e = (Map.Entry) i.next();
			value.append("('" + e.getKey() + "','" + e.getValue() + "')");
		}
		value.append("}");

		logThing(logger, level, mapName, value);
	}

	private static void logObject(Logger logger, Level level, String objectName, Object o) {
		logThing(logger, level, objectName, "'" + o + "'");
	}

	private static void logThing(Logger logger, Level level, String thingName, Object thing) {
		logger.log(level, "    '" + thingName + "' => " + thing);
	}

	/**
	 * A file that is not of type text/plain or text/html can be output through
	 * the response using this method.
	 * 
	 * @param response
	 * @param contentType
	 * @param byteArrayOutputStream
	 * @param fileName
	 */
	public static void saveMimeOutputStreamAsFile(HttpServletResponse response, String contentType,
			ByteArrayOutputStream byteArrayOutputStream, String fileName) throws IOException {

        // If there are quotes in the name, we should replace them to avoid issues.
        // The filename will be wrapped with quotes below when it is set in the header
        String updateFileName;
        if(fileName.contains("\"")) {
            updateFileName = fileName.replaceAll("\"", "");
        } else {
            updateFileName =  fileName;
        }

		// set response
		response.setContentType(contentType);
        response.setHeader("Content-disposition", "attachment; filename=\"" + updateFileName + "\"");
        response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setContentLength(byteArrayOutputStream.size());

		// write to output
		OutputStream outputStream = response.getOutputStream();
		byteArrayOutputStream.writeTo(response.getOutputStream());
		outputStream.flush();
		outputStream.close();
	}

	/**
	 * A file that is not of type text/plain or text/html can be output through
	 * the response using this method.
	 * 
	 * @param response
	 * @param contentType
	 * @param inStream
	 * @param fileName
	 */
	public static void saveMimeInputStreamAsFile(HttpServletResponse response, String contentType,
			InputStream inStream, String fileName, int fileSize) throws IOException {

        // If there are quotes in the name, we should replace them to avoid issues.
        // The filename will be wrapped with quotes below when it is set in the header
        String updateFileName;
        if(fileName.contains("\"")) {
            updateFileName = fileName.replaceAll("\"", "");
        } else {
            updateFileName =  fileName;
        }

		// set response
		response.setContentType(contentType);
        response.setHeader("Content-disposition", "attachment; filename=\"" + updateFileName + "\"");
        response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setContentLength(fileSize);

		// write to output
		OutputStream out = response.getOutputStream();
		while (inStream.available() > 0) {
			out.write(inStream.read());
		}
		out.flush();
	}

	/**
	 * JSTL function to return the tab state of the tab from the form.
	 * 
	 * @param form
	 * @param tabKey
	 * @return
	 */
	public static String getTabState(KualiForm form, String tabKey) {
		return form.getTabState(tabKey);
	}

	public static void incrementTabIndex(KualiForm form, String tabKey) {
		form.incrementTabIndex();
	}

    /**
     * Attempts to reopen sub tabs which would have been closed for inactive records
     *
     * @param sections the list of Sections whose rows and fields to set the open tab state on
     * @param tabStates the map of tabKey->tabState.  This map will be modified to set entries to "OPEN"
     * @param collectionName the name of the collection reopening
     */
    public static void reopenInactiveRecords(List<Section> sections, Map<String, String> tabStates, String collectionName) {
        for (Section section : sections) {
            for (Row row: section.getRows()) {
                for (Field field : row.getFields()) {
                    if (field != null) {
                        if (Field.CONTAINER.equals(field.getFieldType()) && StringUtils.startsWith(field.getContainerName(), collectionName)) {
                            final String tabKey = WebUtils.generateTabKey(FieldUtils.generateCollectionSubTabName(field));
                            tabStates.put(tabKey, KualiForm.TabState.OPEN.name());
                        }
                    }
                }
            }
        }
    }

	/**
	 * Generates a String from the title that can be used as a Map key.
	 * 
	 * @param tabTitle
	 * @return
	 */
	public static String generateTabKey(String tabTitle) {
		String key = "";
		if (!StringUtils.isBlank(tabTitle)) {
			key = tabTitle.replaceAll("\\W", "");
			// if (key.length() > 25) {
			// key = key.substring(0, 24);
			// }
		}

		return key;
	}

	public static void getMultipartParameters(HttpServletRequest request, ActionServletWrapper servletWrapper,
			ActionForm form, ActionMapping mapping) {
		Map params = new HashMap();

		// Get the ActionServletWrapper from the form bean
		// ActionServletWrapper servletWrapper = getServletWrapper();

		try {
			CommonsMultipartRequestHandler multipartHandler = new CommonsMultipartRequestHandler();
			if (multipartHandler != null) {
				// Set servlet and mapping info
				if (servletWrapper != null) {
					// from pojoformbase
					// servlet only affects tempdir on local disk
					servletWrapper.setServletFor(multipartHandler);
				}
				multipartHandler.setMapping((ActionMapping) request.getAttribute(Globals.MAPPING_KEY));
				// Initialize multipart request class handler
				multipartHandler.handleRequest(request);

				Collection<FormFile> files = multipartHandler.getFileElements().values();
				Enumeration keys = multipartHandler.getFileElements().keys();

				while (keys.hasMoreElements()) {
					Object key = keys.nextElement();
					FormFile file = (FormFile) multipartHandler.getFileElements().get(key);
					long maxSize = WebUtils.getMaxUploadSize(form);
					if (LOG.isDebugEnabled()) {
						LOG.debug(file.getFileSize());
					}
					if (maxSize > 0 && Long.parseLong(file.getFileSize() + "") > maxSize) {

						GlobalVariables.getMessageMap().putError(key.toString(),
								RiceKeyConstants.ERROR_UPLOADFILE_SIZE,
								new String[] { file.getFileName(), Long.toString(maxSize) });

					}
				}

				// get file elements for kualirequestprocessor
				if (servletWrapper == null) {
					request.setAttribute(KRADConstants.UPLOADED_FILE_REQUEST_ATTRIBUTE_KEY,
							getFileParametersForMultipartRequest(request, multipartHandler));
				}
			}
		}
		catch (ServletException e) {
			throw new ValidationException("unable to handle multipart request " + e.getMessage(), e);
		}
	}

	public static long getMaxUploadSize(ActionForm form) {
		long max = 0L;
		KualiMultipartRequestHandler multipartHandler = new KualiMultipartRequestHandler();
		if (form instanceof PojoFormBase) {
			max = multipartHandler.calculateMaxUploadSizeToMaxOfList(((PojoFormBase) form).getMaxUploadSizes());
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Max File Upload Size: " + max);
		}
		return max;
	}

	private static Map getFileParametersForMultipartRequest(HttpServletRequest request,
			MultipartRequestHandler multipartHandler) {
		Map parameters = new HashMap();
		Hashtable elements = multipartHandler.getFileElements();
		Enumeration e = elements.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			parameters.put(key, elements.get(key));
		}

		if (request instanceof MultipartRequestWrapper) {
			request = (HttpServletRequest) ((MultipartRequestWrapper) request).getRequest();
			e = request.getParameterNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				parameters.put(key, request.getParameterValues(key));
			}
		}
		else {
			LOG.debug("Gathering multipart parameters for unwrapped request");
		}
		return parameters;
	}

	// end multipart

	public static void registerEditableProperty(PojoFormBase form, String editablePropertyName) {
		form.registerEditableProperty(editablePropertyName);
	}

	public static boolean isDocumentSession(Document document, PojoFormBase docForm) {
		boolean sessionDoc = document instanceof org.kuali.rice.krad.document.SessionDocument;
		boolean dataDictionarySessionDoc = false;
		if (!sessionDoc) {
			DataDictionary dataDictionary = KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary();
			if (docForm instanceof KualiMaintenanceForm) {
				KualiMaintenanceForm maintenanceForm = (KualiMaintenanceForm) docForm;
				if (dataDictionary != null) {
					if (maintenanceForm.getDocTypeName() != null) {
                        MaintenanceDocumentEntry maintenanceDocumentEntry = (MaintenanceDocumentEntry) dataDictionary.getDocumentEntry(maintenanceForm.getDocTypeName());
						dataDictionarySessionDoc = maintenanceDocumentEntry.isSessionDocument();
					}
				}
			}
			else {
				if (document != null && dataDictionary != null) {
					KNSDocumentEntry documentEntry = (KNSDocumentEntry) dataDictionary.getDocumentEntry(document.getClass().getName());
					dataDictionarySessionDoc = documentEntry.isSessionDocument();
				}
			}
		}
		return sessionDoc || dataDictionarySessionDoc;
	}

	public static boolean isFormSessionDocument(PojoFormBase form) {
		Document document = null;
		if (KualiDocumentFormBase.class.isAssignableFrom(form.getClass())) {
			KualiDocumentFormBase docForm = (KualiDocumentFormBase) form;
			document = docForm.getDocument();
		}
		return isDocumentSession(document, form);
	}

	public static String KEY_KUALI_FORM_IN_SESSION = "KualiForm";

	public static ActionForm getKualiForm(PageContext pageContext) {
		return getKualiForm((HttpServletRequest) pageContext.getRequest());
	}

	public static ActionForm getKualiForm(HttpServletRequest request) {
		if (request.getAttribute(KEY_KUALI_FORM_IN_SESSION) != null) {
			return (ActionForm) request.getAttribute(KEY_KUALI_FORM_IN_SESSION);
		}
		else {
			final HttpSession session = request.getSession(false);
			return session != null ? (ActionForm) session.getAttribute(KEY_KUALI_FORM_IN_SESSION) : null;
		}
	}

	public static boolean isPropertyEditable(Set<String> editableProperties, String propertyName) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("isPropertyEditable(" + propertyName + ")");
		}

		boolean returnVal = editableProperties == null
				|| editableProperties.contains(propertyName)
				|| (getIndexOfCoordinateExtension(propertyName) == -1 ? false : editableProperties
						.contains(propertyName.substring(0, getIndexOfCoordinateExtension(propertyName))));
		if (!returnVal) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("isPropertyEditable(" + propertyName + ") == false / editableProperties: "
						+ editableProperties);
			}
		}
		return returnVal;
	}

	public static boolean endsWithCoordinates(String parameter) {
		return parameter.endsWith(WebUtils.IMAGE_COORDINATE_CLICKED_X_EXTENSION)
				|| parameter.endsWith(WebUtils.IMAGE_COORDINATE_CLICKED_Y_EXTENSION);
	}

	public static int getIndexOfCoordinateExtension(String parameter) {
		int indexOfCoordinateExtension = parameter.lastIndexOf(WebUtils.IMAGE_COORDINATE_CLICKED_X_EXTENSION);
        if (indexOfCoordinateExtension == -1) {
            indexOfCoordinateExtension = parameter.lastIndexOf(WebUtils.IMAGE_COORDINATE_CLICKED_Y_EXTENSION);
        }
		return indexOfCoordinateExtension;
	}

    public static boolean isInquiryHiddenField(String className, String fieldName, Object formObject, String propertyName) {
    	boolean isHidden = false;
    	String hiddenInquiryFields = getKualiConfigurationService().getPropertyValueAsString(className + ".hidden");
    	if (StringUtils.isEmpty(hiddenInquiryFields)) {
    		return isHidden;
    	}
    	List hiddenFields = Arrays.asList(hiddenInquiryFields.replaceAll(" ", "").split(","));
    	if (hiddenFields.contains(fieldName.trim())) {
    		isHidden = true;
    	}
    	return isHidden;
    }

    public static boolean isHiddenKimObjectType(String type, String configParameter) {
    	boolean hideType = false;
    	String hiddenTypes = getKualiConfigurationService().getPropertyValueAsString(configParameter);
    	if (StringUtils.isEmpty(hiddenTypes)) {
    		return hideType;
    	}
    	List hiddenTypeValues = Arrays.asList(hiddenTypes.replaceAll(" ", "").split(","));
    	if (hiddenTypeValues.contains(type.trim())) {
    		hideType = true;
    	}
    	return hideType;
    }

	public static String getFullyMaskedValue(String className, String fieldName, Object formObject, String propertyName) {
		String displayMaskValue = null;
		Object propertyValue = ObjectUtils.getPropertyValue(formObject, propertyName);

		DataDictionaryEntryBase entry = (DataDictionaryEntryBase) KRADServiceLocatorWeb.getDataDictionaryService()
				.getDataDictionary().getDictionaryObjectEntry(className);
		AttributeDefinition a = entry.getAttributeDefinition(fieldName);

		AttributeSecurity attributeSecurity = a.getAttributeSecurity();
		if (attributeSecurity != null && attributeSecurity.isMask()) {
			MaskFormatter maskFormatter = attributeSecurity.getMaskFormatter();
			displayMaskValue = maskFormatter.maskValue(propertyValue);

		}
		return displayMaskValue;
	}

	public static String getPartiallyMaskedValue(String className, String fieldName, Object formObject,
			String propertyName) {
		String displayMaskValue = null;
		Object propertyValue = ObjectUtils.getPropertyValue(formObject, propertyName);

		DataDictionaryEntryBase entry = (DataDictionaryEntryBase) KRADServiceLocatorWeb.getDataDictionaryService()
				.getDataDictionary().getDictionaryObjectEntry(className);
		AttributeDefinition a = entry.getAttributeDefinition(fieldName);

		AttributeSecurity attributeSecurity = a.getAttributeSecurity();
		if (attributeSecurity != null && attributeSecurity.isPartialMask()) {
			MaskFormatter partialMaskFormatter = attributeSecurity.getPartialMaskFormatter();
			displayMaskValue = partialMaskFormatter.maskValue(propertyValue);

		}
		return displayMaskValue;
	}

	public static boolean canFullyUnmaskField(String businessObjectClassName, String fieldName, KualiForm form) {
		Class businessObjClass = null;
		try {
			businessObjClass = Class.forName(businessObjectClassName);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to resolve class name: " + businessObjectClassName);
		}
		if (form instanceof KualiDocumentFormBase) {
			return KNSServiceLocator.getBusinessObjectAuthorizationService().canFullyUnmaskField(
					GlobalVariables.getUserSession().getPerson(), businessObjClass, fieldName,
					((KualiDocumentFormBase) form).getDocument());
		}
		else {
			return KNSServiceLocator.getBusinessObjectAuthorizationService().canFullyUnmaskField(
					GlobalVariables.getUserSession().getPerson(), businessObjClass, fieldName, null);
		}
	}

	public static boolean canPartiallyUnmaskField(String businessObjectClassName, String fieldName, KualiForm form) {
		Class businessObjClass = null;
		try {
			businessObjClass = Class.forName(businessObjectClassName);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to resolve class name: " + businessObjectClassName);
		}
		if (form instanceof KualiDocumentFormBase) {
			return KNSServiceLocator.getBusinessObjectAuthorizationService().canPartiallyUnmaskField(
					GlobalVariables.getUserSession().getPerson(), businessObjClass, fieldName,
					((KualiDocumentFormBase) form).getDocument());
		}
		else {
			return KNSServiceLocator.getBusinessObjectAuthorizationService().canPartiallyUnmaskField(
					GlobalVariables.getUserSession().getPerson(), businessObjClass, fieldName, null);
		}
	}

	public static boolean canAddNoteAttachment(Document document) {
		boolean canViewNoteAttachment = false;
		DocumentAuthorizer documentAuthorizer = KNSServiceLocator.getDocumentHelperService().getDocumentAuthorizer(
				document);
		canViewNoteAttachment = documentAuthorizer.canAddNoteAttachment(document, null, GlobalVariables
				.getUserSession().getPerson());
		return canViewNoteAttachment;
	}

	public static boolean canViewNoteAttachment(Document document, String attachmentTypeCode) {
		boolean canViewNoteAttachment = false;
		DocumentAuthorizer documentAuthorizer = KNSServiceLocator.getDocumentHelperService().getDocumentAuthorizer(
				document);
		canViewNoteAttachment = documentAuthorizer.canViewNoteAttachment(document, attachmentTypeCode, GlobalVariables
				.getUserSession().getPerson());
		return canViewNoteAttachment;
	}

	public static boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode,
			String authorUniversalIdentifier) {
		boolean canDeleteNoteAttachment = false;
		DocumentAuthorizer documentAuthorizer = KNSServiceLocator.getDocumentHelperService().getDocumentAuthorizer(
				document);
		canDeleteNoteAttachment = documentAuthorizer.canDeleteNoteAttachment(document, attachmentTypeCode, "false",
				GlobalVariables.getUserSession().getPerson());
		if (canDeleteNoteAttachment) {
			return canDeleteNoteAttachment;
		}
		else {
			canDeleteNoteAttachment = documentAuthorizer.canDeleteNoteAttachment(document, attachmentTypeCode, "true",
					GlobalVariables.getUserSession().getPerson());
			if (canDeleteNoteAttachment
					&& !authorUniversalIdentifier.equals(GlobalVariables.getUserSession().getPerson().getPrincipalId())) {
				canDeleteNoteAttachment = false;
			}
		}
		return canDeleteNoteAttachment;
	}

	public static void reuseErrorMapFromPreviousRequest(KualiDocumentFormBase kualiDocumentFormBase) {
		if (kualiDocumentFormBase.getMessageMapFromPreviousRequest() == null) {
			LOG.error("Error map from previous request is null!");
			return;
		}
		MessageMap errorMapFromGlobalVariables = GlobalVariables.getMessageMap();
		if (kualiDocumentFormBase.getMessageMapFromPreviousRequest() == errorMapFromGlobalVariables) {
			// if we've switched them already, then return early and do nothing
			return;
		}
		if (!errorMapFromGlobalVariables.hasNoErrors()) {
			throw new RuntimeException("Cannot replace error map because it is not empty");
		}
		GlobalVariables.setMessageMap(kualiDocumentFormBase.getMessageMapFromPreviousRequest());
		GlobalVariables.getMessageMap().clearErrorPath();
	}

	/**
	 * Excapes out HTML to prevent XSS attacks, and replaces the following
	 * strings to allow for a limited set of HTML tags
	 * 
	 * <li>[X] and [/X], where X represents any 1 or 2 letter string may be used
	 * to specify the equivalent tag in HTML (i.e. &lt;X&gt; and &lt;/X&gt;) <li>
	 * [font COLOR], where COLOR represents any valid html color (i.e. color
	 * name or hexcode preceeded by #) will be filtered into &lt;font
	 * color="COLOR"/&gt; <li>[/font] will be filtered into &lt;/font&gt; <li>
	 * [table CLASS], where CLASS gives the style class to use, will be filter
	 * into &lt;table class="CLASS"/&gt; <li>[/table] will be filtered into
	 * &lt;/table&gt; <li>[td CLASS], where CLASS gives the style class to use,
	 * will be filter into &lt;td class="CLASS"/&gt;
	 * 
	 * @param inputString
	 * @return
	 */
	public static String filterHtmlAndReplaceRiceMarkup(String inputString) {
		String outputString = StringEscapeUtils.escapeHtml(inputString);
		// string has been escaped of all <, >, and & (and other characters)

        Map<String, String> findAndReplacePatterns = new LinkedHashMap<String, String>();

        // now replace our rice custom markup into html

        // DON'T ALLOW THE SCRIPT TAG OR ARBITRARY IMAGES/URLS/ETC. THROUGH

        //strip out instances where javascript precedes a URL
        findAndReplacePatterns.put("\\[a ((javascript|JAVASCRIPT|JavaScript).+)\\]", "");
        //turn passed a href value into appropriate tag
        findAndReplacePatterns.put("\\[a (.+)\\]", "<a href=\"$1\">");
        findAndReplacePatterns.put("\\[/a\\]", "</a>");
        
		// filter any one character tags
		findAndReplacePatterns.put("\\[([A-Za-z])\\]", "<$1>");
		findAndReplacePatterns.put("\\[/([A-Za-z])\\]", "</$1>");
		// filter any two character tags
		findAndReplacePatterns.put("\\[([A-Za-z]{2})\\]", "<$1>");
		findAndReplacePatterns.put("\\[/([A-Za-z]{2})\\]", "</$1>");
		// filter the font tag
		findAndReplacePatterns.put("\\[font (#[0-9A-Fa-f]{1,6}|[A-Za-z]+)\\]", "<font color=\"$1\">");
		findAndReplacePatterns.put("\\[/font\\]", "</font>");
		// filter the table tag
		findAndReplacePatterns.put("\\[table\\]", "<table>");
		findAndReplacePatterns.put("\\[table ([A-Za-z]+)\\]", "<table class=\"$1\">");
		findAndReplacePatterns.put("\\[/table\\]", "</table>");
		// fiter td with class
		findAndReplacePatterns.put("\\[td ([A-Za-z]+)\\]", "<td class=\"$1\">");

		for (String findPattern : findAndReplacePatterns.keySet()) {
			Pattern p = Pattern.compile(findPattern);
			Matcher m = p.matcher(outputString);
			if (m.find()) {
				String replacePattern = findAndReplacePatterns.get(findPattern);
				outputString = m.replaceAll(replacePattern);
			}
		}

		return outputString;
	}

    /**
	 * Determines and returns the URL for question button images; looks first
	 * for a property "application.custom.image.url", and if that is missing,
	 * uses the image url returned by getDefaultButtonImageUrl()
	 * 
	 * @param imageName
	 *            the name of the image to find a button for
	 * @return the URL where question button images are located
	 */
	public static String getButtonImageUrl(String imageName) {
		String buttonImageUrl = getKualiConfigurationService().getPropertyValueAsString(
                WebUtils.APPLICATION_IMAGE_URL_PROPERTY_PREFIX + "." + imageName);
		if (StringUtils.isBlank(buttonImageUrl)) {
			buttonImageUrl = getDefaultButtonImageUrl(imageName);
		}
		return buttonImageUrl;
	}

    public static String getAttachmentImageForUrl(String contentType) {
        String image = getKualiConfigurationService().getPropertyValueAsString(KRADConstants.ATTACHMENT_IMAGE_PREFIX + contentType);
        if (StringUtils.isEmpty(image)) {
            return getKualiConfigurationService().getPropertyValueAsString(KRADConstants.ATTACHMENT_IMAGE_DEFAULT);
        }
        return image;
    }

	/**
	 * Generates a default button image URL, in the form of:
	 * ${kr.externalizable.images.url}buttonsmall_${imageName}.gif
	 * 
	 * @param imageName
	 *            the image name to generate a default button name for
	 * @return the default button image url
	 */
	public static String getDefaultButtonImageUrl(String imageName) {
		return getKualiConfigurationService().getPropertyValueAsString(WebUtils.DEFAULT_IMAGE_URL_PROPERTY_NAME)
				+ "buttonsmall_" + imageName + ".gif";
	}

    /**
	 * @return an implementation of the KualiConfigurationService
	 */
	public static ConfigurationService getKualiConfigurationService() {
		if (configurationService == null) {
			configurationService = KRADServiceLocator.getKualiConfigurationService();
		}
		return configurationService;
	}
	
    /**
     * Takes a string an converts the whitespace which would be ignored in an
     * HTML document into HTML elements so the whitespace is preserved
     * 
     * @param startingString The string to preserve whitespace in
     * @return A string whose whitespace has been converted to HTML elements to preserve the whitespace in an HTML document
     */
    public static String preserveWhitespace(String startingString) {
    	String convertedString = startingString.replaceAll("\n", "<br />");
    	convertedString = convertedString.replaceAll("  ", "&nbsp;&nbsp;").replaceAll("(&nbsp; | &nbsp;)", "&nbsp;&nbsp;");
    	return convertedString;
    }
    
    public static String getKimGroupDisplayName(String groupId) {
    	if(StringUtils.isBlank(groupId)) {
    		throw new IllegalArgumentException("Group ID must have a value");
    	}
    	return KimApiServiceLocator.getGroupService().getGroup(groupId).getName();
    }
    
    public static String getPrincipalDisplayName(String principalId) {
    	if(StringUtils.isBlank(principalId)) {
    		throw new IllegalArgumentException("Principal ID must have a value");
    	}
        if (KimApiServiceLocator.getIdentityService().getDefaultNamesForPrincipalId(principalId) == null){
            return "";
        }
        else {
    	    return KimApiServiceLocator.getIdentityService().getDefaultNamesForPrincipalId(principalId).getDefaultName().getCompositeName();
        }
    }

    /**
     * Takes an {@link ActionRequest} with a recipient type of
     * {@link RecipientType#ROLE} and returns the display name for the role.
     *
     * @param actionRequest the action request
     * @return the display name for the role
     * @throws IllegalArgumentException if the action request is null, or the recipient type is not ROLE
     */
    public static String getRoleDisplayName(ActionRequest actionRequest) {
        String result;

        if(actionRequest == null) {
            throw new IllegalArgumentException("actionRequest must be non-null");
        }
        if (RecipientType.ROLE != actionRequest.getRecipientType()) {
            throw new IllegalArgumentException("actionRequest recipient must be a Role");
        }

        Role role = KimApiServiceLocator.getRoleService().getRole(actionRequest.getRoleName());

        if (role != null) {
            result = role.getName();
        } else if (!StringUtils.isBlank(actionRequest.getQualifiedRoleNameLabel())) {
            result = actionRequest.getQualifiedRoleNameLabel();
        } else {
            result = actionRequest.getRoleName();
        }

        return result;
    }

    /**
     * Returns an absolute URL which is a combination of a base part plus path,
     * or in the case that the path is already an absolute URL, the path alone
     * @param base the url base path
     * @param path the path to append to base
     * @return an absolute URL representing the combination of base+path, or path alone if it is already absolute
     */
    public static String toAbsoluteURL(String base, String path) {
        boolean abs = false;
        if (StringUtils.isBlank(path)) {
            path = "";
        } else {
            for (String scheme: SCHEMES) {
                if (path.startsWith(scheme)) {
                    abs = true;
                    break;
                }
            }
        }
        if (abs) {
            return path;
        }
        return base + path;
    }

}
