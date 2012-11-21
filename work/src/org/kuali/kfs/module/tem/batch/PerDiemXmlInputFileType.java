/*
 * Copyright 2011 The Kuali Foundation.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.batch.businessobject.PerDiemForLoad;
import org.kuali.kfs.module.tem.batch.service.PerDiemLoadService;
import org.kuali.kfs.module.tem.batch.service.PerDiemLoadValidationService;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.kfs.sys.exception.XmlErrorHandler;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.springframework.core.io.UrlResource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class PerDiemXmlInputFileType extends XmlBatchInputFileTypeBase {
    private static Logger LOG = Logger.getLogger(PerDiemXmlInputFileType.class);

    private DateTimeService dateTimeService;
    private String fileNamePrefix;

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(java.lang.String, java.lang.Object, java.lang.String)
     */
    @Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifier) {
        StringBuilder fileName = new StringBuilder();

        fileUserIdentifier = StringUtils.deleteWhitespace(fileUserIdentifier);
        fileUserIdentifier = StringUtils.remove(fileUserIdentifier, TemConstants.FILE_NAME_PART_DELIMITER);

        fileName.append(this.getFileNamePrefix()).append(TemConstants.FILE_NAME_PART_DELIMITER);
        fileName.append(principalName).append(TemConstants.FILE_NAME_PART_DELIMITER);
        fileName.append(fileUserIdentifier).append(TemConstants.FILE_NAME_PART_DELIMITER);

        fileName.append(dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()));

        return fileName.toString();
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    @Override
    public String getFileTypeIdentifer() {
        return TemConstants.PER_DIEM_XML_INPUT_FILE_TYPE_INDENTIFIER;
    }

    /**
     * @see org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase#parse(byte[])
     */
    @Override
    public Object parse(byte[] fileByteContent) throws ParseException {
        List<PerDiemForLoad> perDiemList = (List<PerDiemForLoad>)(super.parse(fileByteContent));

        PerDiemLoadService perDiemLoadService = SpringContext.getBean(PerDiemLoadService.class);
        perDiemLoadService.updatePerDiem(perDiemList);

        return perDiemList;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    @Override
    public boolean validate(Object parsedFileContents) {
        PerDiemLoadValidationService perDiemLoadValidationService = SpringContext.getBean(PerDiemLoadValidationService.class);
        List<PerDiemForLoad> perDiemList = (List<PerDiemForLoad>)parsedFileContents;

        return perDiemLoadValidationService.validate(perDiemList);
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getAuthorPrincipalName(java.io.File)
     */
    @Override
    public String getAuthorPrincipalName(File file) {
        return StringUtils.substringBetween(file.getName(), this.getFileNamePrefix(), TemConstants.FILE_NAME_PART_DELIMITER);
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    @Override
    public String getTitleKey() {
        return TemKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_PER_DIEM_XML_FILE;
    }

    /**
     * @see org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase#validateContentsAgainstSchema(java.lang.String, java.io.InputStream)
     */
    @Override
    protected void validateContentsAgainstSchema(String schemaLocation, InputStream fileContents) throws ParseException {

        try {
            // get schemaFile
            UrlResource schemaResource = new UrlResource(schemaLocation);

            // load a WXS schema, represented by a Schema instance
            Source schemaSource = new StreamSource(schemaResource.getInputStream());

            // create a SchemaFactory capable of understanding WXS schemas
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(schemaSource);

            // create a validator instance, which can be used to validate an instance document
            Validator validator = schema.newValidator();
            validator.setErrorHandler(new XmlErrorHandler());

            Source source = this.transform(fileContents);
            validator.validate(source);
        }
        catch (MalformedURLException e2) {
            LOG.error("error getting schema url: " + e2.getMessage());
            throw new RuntimeException("error getting schema url:  " + e2.getMessage(), e2);
        }
        catch (SAXException e) {
            LOG.error("error encountered while parsing xml " + e.getMessage());
            throw new ParseException("Schema validation error occured while processing file: " + e.getMessage(), e);
        }
        catch (IOException e1) {
            LOG.error("error occured while validating file contents: " + e1.getMessage());
            throw new RuntimeException("error occurred while validating file contents: " + e1.getMessage(), e1);
        }
    }

    protected Source transform(InputStream fileContents) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document document = documentBuilder.parse(fileContents);
            document.getDocumentElement().setAttribute("xmlns", "http://www.kuali.org/kfs/tem/perDiem");
            Source domSource = new DOMSource(document);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            StreamResult streamResult = new StreamResult(outputStream);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Transform the document to the result stream
            transformer.transform(domSource, streamResult);


            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            return new StreamSource(inputStream);
        }
        catch (TransformerConfigurationException ex) {
            LOG.error("error occurred while validating file contents: " + ex.getMessage());
            throw new RuntimeException("error occurred while validating file contents: " + ex.getMessage(), ex);
        }
        catch (TransformerException ex) {
            LOG.error("error occurred while validating file contents: " + ex.getMessage());
            throw new RuntimeException("error occurred while validating file contents: " + ex.getMessage(), ex);
        }
        catch (SAXException e) {
            LOG.error("error encountered while parsing xml " + e.getMessage());
            throw new ParseException("Schema validation error occured while processing file: " + e.getMessage(), e);
        }
        catch (IOException e1) {
            LOG.error("error occured while validating file contents: " + e1.getMessage());
            throw new RuntimeException("error occurred while validating file contents: " + e1.getMessage(), e1);
        }
        catch (ParserConfigurationException ex) {
            LOG.error("error occurred while validating file contents: " + ex.getMessage());
            throw new RuntimeException("error occurred while validating file contents: " + ex.getMessage(), ex);
        }
    }

    /**
     * Gets the dateTimeService attribute.
     *
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the fileNamePrefix attribute.
     *
     * @return Returns the fileNamePrefix.
     */
    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    /**
     * Sets the fileNamePrefix attribute value.
     *
     * @param fileNamePrefix The fileNamePrefix to set.
     */
    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }
}
