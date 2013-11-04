/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.batch;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class TemProfileExportStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TemProfileExportStep.class);

    private TemProfileService temProfileService;
    private PersonService personService;

    private String fileDirectoryName;
    private String fileName;

	@Override
	public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
		List<TemProfile> profiles = temProfileService.getAllActiveTemProfile();

		//Accessing EXPORT_FILE_FORMAT sys param for export file extension
        LOG.info("Accessing EXPORT_FILE_FORMAT system parameter for file extension");
        String extension = getParameterService().getParameterValueAsString(TemProfileExportStep.class, TemConstants.TemProfileParameters.EXPORT_FILE_FORMAT);

        //Creating export file name
        String exportFile = fileDirectoryName + File.separator + fileName + "." + extension;

        //Initializing the output stream
        PrintStream OUTPUT_GLE_FILE_ps = null;
        try {
			OUTPUT_GLE_FILE_ps = new PrintStream(exportFile);
		} catch (FileNotFoundException ex) {
            throw new RuntimeException(ex.toString(), ex);
		}

		//Create file based on extension
		if(extension.equalsIgnoreCase("xml")) {
			try {
				OUTPUT_GLE_FILE_ps.printf("%s\n", generateXMLDoc(profiles));
			} catch (ParserConfigurationException ex) {
	            throw new RuntimeException(ex.toString(), ex);
			} catch (TransformerException ex) {
	            throw new RuntimeException(ex.toString(), ex);
			}
		} else {
			OUTPUT_GLE_FILE_ps.printf("%s\n", getDateTimeService().toDateTimeString(getDateTimeService().getCurrentDate()) + "," + getParameterService().getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, KfsParameterConstants.INSTITUTION_NAME));
			for(TemProfile profile : profiles) {
				try {
					OUTPUT_GLE_FILE_ps.printf("%s\n", generateCSVEntry(profile));
		        }
		        catch (Exception e) {
		            throw new RuntimeException(e.toString(), e);
		        }
			}
		}

        OUTPUT_GLE_FILE_ps.close();

		return false;
	}

	private String generateXMLDoc(List<TemProfile> profiles) throws ParserConfigurationException, TransformerException {

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        //Create the Profile Header
        Element profileHeader = doc.createElement("profileHeader");
        profileHeader.appendChild(createElement(doc, "dateOfExport", getDateTimeService().toDateTimeString(getDateTimeService().getCurrentDate())));
        profileHeader.appendChild(createElement(doc, "universityName", getParameterService().getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, KfsParameterConstants.INSTITUTION_NAME)));

        //Create the Profile Detail section
		Element profileDetailList = doc.createElement("profiles");
        for(TemProfile profile : profiles) {
        	profileDetailList.appendChild(generateProfileDetailElement(doc, profile));
		}

        //Create the Root Element, add the Profile Header and Detail sections, add to document
		Element rootElement = doc.createElement("temProfileExport");
        rootElement.appendChild(profileHeader);
        rootElement.appendChild(profileDetailList);
        doc.appendChild(rootElement);

        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);

        return sw.toString();
	}

	private Element generateProfileDetailElement(Document doc, TemProfile profile) {
		Element profileDetail = doc.createElement("profileDetail");

		profileDetail.appendChild(createElement(doc, "travelerProfileID", profile.getProfileId().toString()));
		profileDetail.appendChild(createElement(doc, "travelerType", StringUtils.defaultIfEmpty(profile.getTravelerTypeCode(), "")));
		profileDetail.appendChild(createElement(doc, "travelerEmployeeID", StringUtils.defaultIfEmpty(profile.getEmployeeId(), "")));
		profileDetail.appendChild(createElement(doc, "principalName", getPrincipalName(profile.getPrincipalId())));
		profileDetail.appendChild(createElement(doc, "firstName", StringUtils.defaultIfEmpty(profile.getFirstName(), "")));
		profileDetail.appendChild(createElement(doc, "middleName", StringUtils.defaultIfEmpty(profile.getMiddleName(), "")));
		profileDetail.appendChild(createElement(doc, "lastName", StringUtils.defaultIfEmpty(profile.getLastName(), "")));
		profileDetail.appendChild(createElement(doc, "fullName", StringUtils.defaultIfEmpty(profile.getName(), "")));
		profileDetail.appendChild(createElement(doc, "dateOfBirth", ObjectUtils.isNotNull(profile.getDateOfBirth()) ? getDateTimeService().toDateString(profile.getDateOfBirth()) : ""));
		profileDetail.appendChild(createElement(doc, "gender", StringUtils.defaultIfEmpty(profile.getGender(), "")));
		if(ObjectUtils.isNotNull(profile.getTemProfileAddress())) {
			profileDetail.appendChild(createElement(doc, "streetAddress1", StringUtils.defaultIfEmpty(profile.getTemProfileAddress().getStreetAddressLine1(), "")));
			profileDetail.appendChild(createElement(doc, "streetAddress2", StringUtils.defaultIfEmpty(profile.getTemProfileAddress().getStreetAddressLine2(), "")));
			profileDetail.appendChild(createElement(doc, "city", StringUtils.defaultIfEmpty(profile.getTemProfileAddress().getCityName(), "")));
			profileDetail.appendChild(createElement(doc, "state", StringUtils.defaultIfEmpty(profile.getTemProfileAddress().getStateCode(), "")));
			profileDetail.appendChild(createElement(doc, "zipCode", StringUtils.defaultIfEmpty(profile.getTemProfileAddress().getZipCode(), "")));
			profileDetail.appendChild(createElement(doc, "country", StringUtils.defaultIfEmpty(profile.getTemProfileAddress().getCountryCode(), "")));
		} else {
			//No address for this profile
			profileDetail.appendChild(createElement(doc, "streetAddress1", ""));
			profileDetail.appendChild(createElement(doc, "streetAddress2", ""));
			profileDetail.appendChild(createElement(doc, "city", ""));
			profileDetail.appendChild(createElement(doc, "state", ""));
			profileDetail.appendChild(createElement(doc, "zipCode", ""));
			profileDetail.appendChild(createElement(doc, "country", ""));
		}
		profileDetail.appendChild(createElement(doc, "phoneNumber", StringUtils.defaultIfEmpty(profile.getPhoneNumber(), "")));
		profileDetail.appendChild(createElement(doc, "emailAddress", StringUtils.defaultIfEmpty(profile.getEmailAddress(), "")));
		profileDetail.appendChild(createElement(doc, "primaryDepartment", StringUtils.defaultIfEmpty(profile.getHomeDepartment(), "")));

		return profileDetail;
	}

	private Element createElement(Document doc, String elementName, String elementValue) {
		//create element
        Element element = doc.createElement(elementName);

        //add a text element to the child
        Text text = doc.createTextNode(elementValue);
        element.appendChild(text);

        return element;
	}

	private String generateCSVEntry(TemProfile profile) {
		StringBuffer line = new StringBuffer();

		line.append(profile.getProfileId().toString()).append(",");
		line.append(StringUtils.defaultIfEmpty(profile.getTravelerTypeCode(), "")).append(",");
		line.append(StringUtils.defaultIfEmpty(profile.getEmployeeId(), "")).append(",");
		line.append(getPrincipalName(profile.getPrincipalId())).append(",");
		line.append(StringUtils.defaultIfEmpty(profile.getFirstName(), "")).append(",");
		line.append(StringUtils.defaultIfEmpty(profile.getMiddleName(), "")).append(",");
		line.append(StringUtils.defaultIfEmpty(profile.getLastName(), "")).append(",");
		line.append(StringUtils.defaultIfEmpty(profile.getName(), "")).append(",");
		line.append(ObjectUtils.isNotNull(profile.getDateOfBirth()) ? getDateTimeService().toDateString(profile.getDateOfBirth()) : "").append(",");
		line.append(StringUtils.defaultIfEmpty(profile.getGender(), "")).append(",");
		if(ObjectUtils.isNotNull(profile.getTemProfileAddress())) {
			line.append(StringUtils.defaultIfEmpty(profile.getTemProfileAddress().getStreetAddressLine1(), "")).append(",");
			line.append(StringUtils.defaultIfEmpty(profile.getTemProfileAddress().getStreetAddressLine2(), "")).append(",");
			line.append(StringUtils.defaultIfEmpty(profile.getTemProfileAddress().getCityName(), "")).append(",");
			line.append(StringUtils.defaultIfEmpty(profile.getTemProfileAddress().getStateCode(), "")).append(",");
			line.append(StringUtils.defaultIfEmpty(profile.getTemProfileAddress().getZipCode(), "")).append(",");
			line.append(StringUtils.defaultIfEmpty(profile.getTemProfileAddress().getCountryCode(), "")).append(",");
		} else {
			//No address for this profile
			line.append("").append(",");
			line.append("").append(",");
			line.append("").append(",");
			line.append("").append(",");
			line.append("").append(",");
			line.append("").append(",");
		}
		line.append(StringUtils.defaultIfEmpty(profile.getPhoneNumber(), "")).append(",");
		line.append(StringUtils.defaultIfEmpty(profile.getEmailAddress(), "")).append(",");
		line.append(StringUtils.defaultIfEmpty(profile.getHomeDepartment(), ""));

		return line.toString();
	}

	private String getPrincipalName(String principalId) {
		String principalName = "";

		if(StringUtils.isNotEmpty(principalId)) {
			Person person = personService.getPerson(principalId);

			principalName = person != null ? person.getPrincipalName() : "";
		}

		return principalName;
	}

	/**
	 * Gets the temProfileService attribute.
	 * @return Returns the temProfileService.
	 */
	public TemProfileService getTemProfileService() {
		return temProfileService;
	}

	/**
	 * Sets the temProfileService attribute value.
	 * @param temProfileService The temProfileService to set.
	 */
	public void setTemProfileService(TemProfileService temProfileService) {
		this.temProfileService = temProfileService;
	}

	/**
	 * Gets the fileDirectoryName attribute.
	 * @return Returns the fileDirectoryName.
	 */
	public String getFileDirectoryName() {
		return fileDirectoryName;
	}

	/**
	 * Sets the fileDirectoryName attribute value.
	 * @param fileDirectoryName The fileDirectoryName to set.
	 */
	public void setFileDirectoryName(String fileDirectoryName) {
		this.fileDirectoryName = fileDirectoryName;
	}

	/**
	 * Gets the fileName attribute.
	 * @return Returns the fileName.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the fileName attribute value.
	 * @param fileName The fileName to set.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the personService attribute.
	 * @return Returns the personService.
	 */
	public PersonService getPersonService() {
		return personService;
	}

	/**
	 * Sets the personService attribute value.
	 * @param personService The personService to set.
	 */
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

}
