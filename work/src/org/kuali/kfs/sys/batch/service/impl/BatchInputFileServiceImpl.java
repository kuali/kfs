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
package org.kuali.kfs.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.exception.InitException;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants.ParameterGroups;
import org.kuali.kfs.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.batch.BatchInputFileType;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.exceptions.FileStorageException;
import org.kuali.kfs.exceptions.XMLParseException;
import org.kuali.kfs.exceptions.XmlErrorHandler;
import org.kuali.kfs.service.BatchInputFileService;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;

/**
 * Provides batch input file management, including listing files, parsing, downloading, storing, and deleting.
 */
public class BatchInputFileServiceImpl implements BatchInputFileService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchInputFileServiceImpl.class);

    /**
     * Uses the apache commons digestor to unmarshell the xml. The BatchInputFileType specifies the location of the digestor rules
     * xml which tells the digestor how to build the object graph from the xml.
     * 
     * @see org.kuali.kfs.service.BatchInputFileService#parse(org.kuali.kfs.batch.BatchInputFileType, byte[])
     */
    public Object parse(BatchInputFileType batchInputFileType, byte[] fileByteContent) throws XMLParseException {
        if (batchInputFileType == null || fileByteContent == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        // handle zero byte contents, xml parsers don't deal with them well
        if (fileByteContent.length == 0) {
            LOG.error("an invalid argument was given, empty input stream");
            throw new IllegalArgumentException("an invalid argument was given, empty input stream");
        }

        // validate contents against schema
        ByteArrayInputStream validateFileContents = new ByteArrayInputStream(fileByteContent);
        validateContentsAgainstSchema(batchInputFileType.getSchemaLocation(), validateFileContents);

        // setup digester for parsing the xml file
        Digester digester = buildDigester(batchInputFileType.getSchemaLocation(), batchInputFileType.getDigestorRulesFileName());

        Object parsedContents = null;
        try {
            ByteArrayInputStream parseFileContents = new ByteArrayInputStream(fileByteContent);
            parsedContents = digester.parse(parseFileContents);
        }
        catch (Exception e) {
            LOG.error("Error parsing xml contents", e);
            throw new XMLParseException("Error parsing xml contents: " + e.getMessage(), e);
        }

        return parsedContents;
    }

    /**
     * Validates the xml contents against the batch input type schema using the java 1.5 validation package.
     * 
     * @param schemaLocation - location of the schema file
     * @param fileContents - xml contents to validate against the schema
     */
    private void validateContentsAgainstSchema(String schemaLocation, InputStream fileContents) throws XMLParseException {

        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // get schemaFile from classpath
        ClassPathResource resource = new ClassPathResource(schemaLocation);
        File schemaFile;
        try {
            schemaFile = resource.getFile();
        }
        catch (IOException e2) {
            LOG.error("unable to get schema file: " + e2.getMessage());
            throw new RuntimeException("unable to get schema file: " + e2.getMessage());
        }

        // load a WXS schema, represented by a Schema instance
        Source schemaSource = new StreamSource(schemaFile);
        Schema schema = null;
        try {
            schema = factory.newSchema(schemaSource);
        }
        catch (SAXException e) {
            LOG.error("error occured while setting schema file: " + e.getMessage());
            throw new RuntimeException("error occured while setting schema file: " + e.getMessage(), e);
        }

        // create a Validator instance, which can be used to validate an instance document
        Validator validator = schema.newValidator();
        validator.setErrorHandler(new XmlErrorHandler());

        // validate
        try {
            validator.validate(new StreamSource(fileContents));
        }
        catch (SAXException e) {
            LOG.error("error encountered while parsing xml " + e.getMessage());
            throw new XMLParseException("Schema validation error occured while processing file: " + e.getMessage(), e);
        }
        catch (IOException e1) {
            LOG.error("error occured while validating file contents: " + e1.getMessage());
            throw new RuntimeException("error occured while validating file contents: " + e1.getMessage(), e1);
        }
    }

    /**
     * Defers to batch type to do any validation on the parsed contents.
     * 
     * @see org.kuali.kfs.service.BatchInputFileService#validate(org.kuali.kfs.batch.BatchInputFileType, java.lang.Object)
     */
    public boolean validate(BatchInputFileType batchInputFileType, Object parsedObject) {
        if (batchInputFileType == null || parsedObject == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        boolean contentsValid = true;
        contentsValid = batchInputFileType.validate(parsedObject);

        return contentsValid;
    }

    /**
     * @see org.kuali.kfs.service.BatchInputFileService#save(org.kuali.core.bo.user.UniversalUser,
     *      org.kuali.kfs.batch.BatchInputFileType, java.lang.String, java.io.InputStream)
     */
    public String save(UniversalUser user, BatchInputFileType batchInputFileType, String fileUserIdentifer, InputStream fileContents, Object parsedObject) throws AuthorizationException, FileStorageException {
        if (user == null || batchInputFileType == null || fileContents == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        // check user is authorized to upload a file for the batch type
        if (!isUserAuthorizedForBatchType(batchInputFileType, user)) {
            LOG.error("User " + user.getPersonUserIdentifier() + " is not authorized to upload a file of batch type " + batchInputFileType.getFileTypeIdentifer());
            throw new AuthorizationException(user.getPersonUserIdentifier(), "upload", batchInputFileType.getFileTypeIdentifer());
        }

        // defer to batch input type to add any security or other needed information to the file name
        String saveFileName = batchInputFileType.getDirectoryPath() + "/" + batchInputFileType.getFileName(user, parsedObject, fileUserIdentifer);
        saveFileName += "." + batchInputFileType.getFileExtension();

        // consruct the file object and check for existence
        File fileToSave = new File(saveFileName);
        if (fileToSave.exists()) {
            LOG.error("cannot store file, name already exists " + saveFileName);
            throw new FileStorageException("Cannot store file because the name " + saveFileName + " already exists on the file system.");
        }

        try {
            FileWriter fileWriter = new FileWriter(fileToSave);
            while (fileContents.available() > 0) {
                fileWriter.write(fileContents.read());
            }
            fileWriter.flush();
            fileWriter.close();

            createDoneFile(fileToSave);
        }
        catch (IOException e) {
            LOG.error("unable to save contents to file " + saveFileName, e);
            throw new RuntimeException("errors encountered while writing file " + saveFileName, e);
        }

        return saveFileName;
    }

    /**
     * Creates a '.done' file with the name of the batch file.
     */
    private void createDoneFile(File batchFile) {
        String doneFileName = StringUtils.substringBeforeLast(batchFile.getName(), ".") + ".done";
        String donePath = StringUtils.substringBeforeLast(batchFile.getPath(), File.separator);
        File doneFile = new File(donePath + "/" + doneFileName);

        if (!doneFile.exists()) {
            boolean doneFileCreated = false;
            try {
                doneFileCreated = doneFile.createNewFile();
            }
            catch (IOException e) {
                LOG.error("unable to create done file " + doneFileName, e);
                throw new RuntimeException("Errors encountered while saving the file: Unable to create .done file " + doneFileName, e);
            }

            if (!doneFileCreated) {
                LOG.error("unable to create done file " + doneFileName);
                throw new RuntimeException("Errors encountered while saving the file: Unable to create .done file " + doneFileName);
            }
        }
    }

    /**
     * @see org.kuali.kfs.service.BatchInputFileService#delete(org.kuali.core.bo.user.UniversalUser,
     *      org.kuali.kfs.batch.BatchInputFileType, java.lang.String)
     */
    public void delete(UniversalUser user, BatchInputFileType batchInputFileType, String deleteFileName) throws AuthorizationException, FileNotFoundException {
        if (user == null || batchInputFileType == null || StringUtils.isBlank(deleteFileName)) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        // check user is authorized to delete a file for the batch type
        if (!this.isUserAuthorizedForBatchType(batchInputFileType, user)) {
            LOG.error("User " + user.getPersonUserIdentifier() + " is not authorized to delete a file of batch type " + batchInputFileType.getFileTypeIdentifer());
            throw new AuthorizationException(user.getPersonUserIdentifier(), "delete", batchInputFileType.getFileTypeIdentifer());
        }

        File fileToDelete = new File(deleteFileName);
        if (fileToDelete.exists()) {
            fileToDelete.delete();

            // check for associated .done file and remove as well
            String doneFileName = StringUtils.substringBeforeLast(fileToDelete.getPath(), ".") + ".done";
            File doneFile = new File(doneFileName);
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
        else {
            LOG.error("unable to delete file " + deleteFileName + " because it doesn not exist.");
            throw new FileNotFoundException("Unable to delete file " + deleteFileName + ". File does not exist on server.");
        }
    }

    /**
     * @see org.kuali.kfs.service.BatchInputFileService#download(org.kuali.core.bo.user.UniversalUser,
     *      org.kuali.kfs.batch.BatchInputFileType, java.lang.String)
     */
    public File download(UniversalUser user, BatchInputFileType batchInputFileType, String downloadFileName) throws AuthorizationException, FileNotFoundException {
        if (user == null || batchInputFileType == null || StringUtils.isBlank(downloadFileName)) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        // check user is authorized to download a file for the batch type
        if (!this.isUserAuthorizedForBatchType(batchInputFileType, user)) {
            LOG.error("User " + user.getPersonUserIdentifier() + " is not authorized to download a file of batch type " + batchInputFileType.getFileTypeIdentifer());
            throw new AuthorizationException(user.getPersonUserIdentifier(), "download", batchInputFileType.getFileTypeIdentifer());
        }

        File fileToDownload = new File(downloadFileName);
        if (!fileToDownload.exists()) {
            LOG.error("unable to download file " + downloadFileName + " because it doesn not exist.");
            throw new FileNotFoundException("Unable to download file " + downloadFileName + ". File does not exist on server.");
        }

        return fileToDownload;
    }

    /**
     * @see org.kuali.kfs.service.BatchInputFileService#isBatchInputTypeActive(org.kuali.kfs.batch.BatchInputFileType)
     */
    public boolean isBatchInputTypeActive(BatchInputFileType batchInputFileType) {
        if (batchInputFileType == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        String[] activeInputTypes = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValues(ParameterGroups.BATCH_UPLOAD_SECURITY_GROUP_NAME, SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME);

        boolean activeBatchType = false;
        if (activeInputTypes.length > 0 && (Arrays.asList(activeInputTypes)).contains(batchInputFileType.getFileTypeIdentifer())) {
            activeBatchType = true;
        }

        return activeBatchType;
    }

    /**
     * @see org.kuali.kfs.service.BatchInputFileService#isUserAuthorizedForBatchType(org.kuali.kfs.batch.BatchInputFileType,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public boolean isUserAuthorizedForBatchType(BatchInputFileType batchInputFileType, UniversalUser user) {
        if (batchInputFileType == null || user == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        String workgroupParameterName = batchInputFileType.getWorkgroupParameterName();
        String authorizedWorkgroupName = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(ParameterGroups.BATCH_UPLOAD_SECURITY_GROUP_NAME, workgroupParameterName);
 
        return user.isMember(authorizedWorkgroupName);
    }

    /**
     * Fetches workgroup for batch type from system parameter and verifies user is a member. Then a list of all files for the batch
     * type are retrieved. For each file, the file and user is sent through the checkAuthorization method of the batch input type
     * implementation for finer grained security. If the method returns true, the filename is added to the user's list.
     * 
     * @see org.kuali.kfs.service.BatchInputFileService#listBatchTypeFilesForUser(org.kuali.kfs.batch.BatchInputFileType,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public List<String> listBatchTypeFilesForUser(BatchInputFileType batchInputFileType, UniversalUser user) throws AuthorizationException {
        if (batchInputFileType == null || user == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        if (!this.isUserAuthorizedForBatchType(batchInputFileType, user)) {
            LOG.error("User " + user.getPersonUserIdentifier() + " is not authorized to list a file of batch type " + batchInputFileType.getFileTypeIdentifer());
            throw new AuthorizationException(user.getPersonUserIdentifier(), "list", batchInputFileType.getFileTypeIdentifer());
        }

        File[] filesInBatchDirectory = listFilesInBatchTypeDirectory(batchInputFileType);

        List<String> userFileList = new ArrayList();
        for (int i = 0; i < filesInBatchDirectory.length; i++) {
            File batchFile = filesInBatchDirectory[i];
            String fileExtension = StringUtils.substringAfterLast(batchFile.getName(), ".");
            if (batchInputFileType.getFileExtension().equals(fileExtension)) {
                boolean userAuthorizedForFile = batchInputFileType.checkAuthorization(user, batchFile);
                if (userAuthorizedForFile) {
                    userFileList.add(batchFile.getPath());
                }
            }
        }

        return userFileList;
    }

    /**
     * Returns List of filenames for existing files in the directory given by the batch input type.
     */
    private File[] listFilesInBatchTypeDirectory(BatchInputFileType batchInputFileType) {
        File batchTypeDirectory = new File(batchInputFileType.getDirectoryPath());
        return batchTypeDirectory.listFiles();
    }

    /**
     * @see org.kuali.kfs.service.BatchInputFileService#listInputFileNamesWithDoneFile(org.kuali.kfs.batch.BatchInputFileType)
     */
    public List<String> listInputFileNamesWithDoneFile(BatchInputFileType batchInputFileType) {
        if (batchInputFileType == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        File batchTypeDirectory = new File(batchInputFileType.getDirectoryPath());
        File[] doneFiles = batchTypeDirectory.listFiles(new DoneFilenameFilter());

        List<String> batchInputFiles = new ArrayList();
        for (int i = 0; i < doneFiles.length; i++) {
            File doneFile = doneFiles[i];
            File dataFile = new File(StringUtils.substringBeforeLast(doneFile.getPath(), ".") + "." + batchInputFileType.getFileExtension());
            if (dataFile.exists()) {
                batchInputFiles.add(dataFile.getPath());
            }
        }

        return batchInputFiles;
    }

    /**
     * Retrieves files in a directory with the .done extension.
     */
    private class DoneFilenameFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name) {
            return name.endsWith(".done");
        }
    }

    /**
     * @return fully-initialized Digester used to process entry XML files
     */
    private Digester buildDigester(String schemaLocation, String digestorRulesFileName) {
        Digester digester = new Digester();
        digester.setNamespaceAware(false);
        digester.setValidating(true);
        digester.setErrorHandler(new XmlErrorHandler());
        digester.setSchema(schemaLocation);

        Rules rules = loadRules(digestorRulesFileName);

        digester.setRules(rules);

        return digester;
    }

    /**
     * @return Rules loaded from the appropriate XML file
     */
    private final Rules loadRules(String digestorRulesFileName) {
        // locate Digester rules
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL rulesUrl = classLoader.getResource(digestorRulesFileName);
        if (rulesUrl == null) {
            throw new InitException("unable to locate digester rules file " + digestorRulesFileName);
        }

        // create and init digester
        Digester digester = DigesterLoader.createDigester(rulesUrl);

        return digester.getRules();
    }
}
