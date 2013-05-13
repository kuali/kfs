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

import static org.kuali.kfs.sys.KFSConstants.SchemaBuilder.DD_VALIDATION_PREFIX;
import static org.kuali.kfs.sys.KFSConstants.SchemaBuilder.SCHEMA_FILE_DD_VALIDATION_PLACEHOLDER_BEGIN;
import static org.kuali.kfs.sys.KFSConstants.SchemaBuilder.SCHEMA_FILE_DD_VALIDATION_PLACEHOLDER_END;
import static org.kuali.kfs.sys.KFSConstants.SchemaBuilder.XSD_VALIDATION_PREFIX;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Called during the build process to output schema files with validation built from the data dictionary or set to defaults
 */
public class SchemaBuilder {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SchemaBuilder.class);
    private static Level logLevel = Level.INFO;

    /**
     * <pre>
     * Performs schema build process. 
     * 
     * Build directory path containing the schema files, static directory that schema files will be
     * outputted to, and flag for whether to use data dictionary validation all must given as arguments. 
     * 
     * Schema files in build directory should contain place-holders for which the validation will be substituted. The place-holder begin symbol is ${,
     * and the end symbol is }. Then the place-holder should contain two parts, first the xsd type to use if data dictionary
     * validation is not on. The second is the data dictionary entry (businessObjectEntry.attributeName) prefixed with 'dd:' that
     * will be pulled for dd validation. The parts should be separated with a comma. Any type values without a place-holder will
     * not be modified. 
     * 
     * Program also fills in externalizable.static.content.url place-holder. Value to set should be passed as the fourth program argument
     * </pre>
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 5) {
            System.err.println("ERROR: You must pass the build directory, static directory, dd flag, external content url, and rebuild types flag as arguments");
            System.exit(8);
        }
        try {
            // initialize log4j
            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.WARN);
            LOG.setLevel(logLevel);

            String buildDirectoryPath = args[0];
            if (StringUtils.isBlank(buildDirectoryPath)) {
                logAndThrowException("Build directory must be passed as first argument");
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Build directory set to " + buildDirectoryPath);
            }

            String staticDirectoryPath = args[1];
            if (StringUtils.isBlank(staticDirectoryPath)) {
                logAndThrowException("Static directory must be passed as second argument");
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Static directory set to " + staticDirectoryPath);
            }

            String dataDictionaryValidation = args[2];
            if (StringUtils.isBlank(dataDictionaryValidation)) {
                logAndThrowException("Use data dictionary validation must be passed as third argument");
            }

            String externalizableContentUrl = args[3];
            if (StringUtils.isBlank(externalizableContentUrl)) {
                logAndThrowException("Externalizalbe static content URL must be passed as fourth argument");
            }

            String rebuildDDTypesFlag = args[4];
            if (StringUtils.isBlank(rebuildDDTypesFlag)) {
                logAndThrowException("Rebuild DD flags must be passed as fifth argument");
            }

            boolean useDataDictionaryValidation = Boolean.parseBoolean(dataDictionaryValidation);
            boolean rebuildDDTypes = Boolean.parseBoolean(rebuildDDTypesFlag);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Use data dictionary validation set to " + useDataDictionaryValidation);
            }

            // if using dd validation must start up spring so we can read DD
            if (useDataDictionaryValidation && rebuildDDTypes) {
                SpringContextForBatchRunner.initializeKfs();
            }

            LOG.debug("Getting build schema files");
            Collection buildSchemaFiles = getBuildSchemaFiles(buildDirectoryPath);

            LOG.debug("Building schema files");
            try {
                buildSchemaFiles(buildSchemaFiles, staticDirectoryPath, buildDirectoryPath, useDataDictionaryValidation, externalizableContentUrl, rebuildDDTypes);
            }
            catch (IOException ex) {
                LOG.error("Error building schema files: " + ex.getMessage(), ex);
                throw new RuntimeException("Error building schema files: " + ex.getMessage(), ex);
            }

            LOG.info("Finished building schema files.");
            System.exit(0);
        }
        catch (Throwable t) {
            System.err.println("ERROR: Exception caught: " + t.getMessage());
            t.printStackTrace(System.err);
            System.exit(8);
        }
    }

    /**
     * Returns Collection of File objects for all .xsd files found in given directory (including sub-directories)
     * 
     * @param buildDirectoryPath Directory to look for schema files
     * @return Collection of File objects
     */
    protected static Collection getBuildSchemaFiles(String buildDirectoryPath) {
        File buildDirectory = new File(buildDirectoryPath);

        return FileUtils.listFiles(buildDirectory, new String[] { "xsd" }, true);
    }

    /**
     * Iterates through build schema files processing validation place-holders and outputting to static directory. Include file
     * for referenced schema types is also written out
     * 
     * @param buildSchemaFiles collection of File objects for build schema files
     * @param staticDirectoryPath path that processed schema files will be written to
     * @param buildDirectoryPath path of build schema files
     * @param useDataDictionaryValidation indicates whether data dictionary validation should be used, if false the general xsd
     *            datatype in the place-holder will be used
     * @param externalizableContentUrl URL to set for externalizable.static.content.url token
     * @throws IOException thrown for any read/write errors encountered
     */
    protected static void buildSchemaFiles(Collection buildSchemaFiles, String staticDirectoryPath, String buildDirectoryPath, boolean useDataDictionaryValidation, String externalizableContentUrl, boolean rebuildDDTypes) throws IOException {
        // initialize dd type schema
        Collection typesSchemaLines = initalizeDataDictionaryTypesSchema();
        Set<String> builtTypes = new HashSet<String>();

        // convert static directory path to abstract path
        File staticDirectory = new File(staticDirectoryPath);
        String staticPathName = staticDirectory.getAbsolutePath();

        for (Iterator iterator = buildSchemaFiles.iterator(); iterator.hasNext();) {
            File buildSchemFile = (File) iterator.next();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Processing schema file: " + buildSchemFile.getName());
            }

            String outSchemaFilePathName = staticPathName + getRelativeFilePathName(buildSchemFile, buildDirectoryPath);
            LOG.info("Building schema file: " + outSchemaFilePathName);

            buildSchemaFile(buildSchemFile, outSchemaFilePathName, useDataDictionaryValidation, typesSchemaLines, externalizableContentUrl, builtTypes, rebuildDDTypes);
        }

        // finalize dd type schema
        typesSchemaLines.addAll(finalizeDataDictionaryTypesSchema());

        if (rebuildDDTypes) {
            LOG.debug("Writing ddTypes schema file");
            File ddTypesFile = new File(staticPathName + File.separator + "xsd" + File.separator + "sys" + File.separator + "ddTypes.xsd");
            File ddTypesFileBuild = new File(buildDirectoryPath + File.separator + "xsd" + File.separator + "sys" + File.separator + "ddTypes.xsd");
            FileUtils.writeLines(ddTypesFile, typesSchemaLines);
            FileUtils.copyFile(ddTypesFile, ddTypesFileBuild);
        }
    }

    /**
     * Process a single schema file (setting validation and externalizable token) and outputs to static directory. Any new data
     * dictionary types encountered are added to the given Collection for later writing to the types include file
     * 
     * @param buildSchemFile build schema file that should be processed
     * @param outSchemaFilePathName full file path name for the outputted schema
     * @param useDataDictionaryValidation indicates whether data dictionary validation should be used, if false the general xsd
     *            datatype in the place-holder will be used
     * @param typesSchemaLines collection of type XML lines to add to for any new types
     * @param externalizableContentUrl URL to set for externalizable.static.content.url token
     * @param builtTypes - Set of attribute names for which a schema validation type has been built
     * @throws IOException thrown for any read/write errors encountered
     */
    protected static void buildSchemaFile(File buildSchemFile, String outSchemaFilePathName, boolean useDataDictionaryValidation, Collection typesSchemaLines, String externalizableContentUrl, Set<String> builtTypes, boolean rebuildDDTypes) throws IOException {
        Collection buildSchemaLines = FileUtils.readLines(buildSchemFile);
        Collection outSchemaLines = new ArrayList();
        int lineCount = 1;
        for (Iterator iterator = buildSchemaLines.iterator(); iterator.hasNext();) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Processing line " + lineCount + "of file " + buildSchemFile.getAbsolutePath());
            }
            String buildLine = (String) iterator.next();
            String outLine = buildLine;

            // check for externalizable.static.content.url token and replace if found
            if (StringUtils.contains(buildLine, "@externalizable.static.content.url@")) {
                outLine = StringUtils.replace(buildLine, "@externalizable.static.content.url@", externalizableContentUrl);
            }

            // check for validation place-holder and process if found
            else if (StringUtils.contains(buildLine, SCHEMA_FILE_DD_VALIDATION_PLACEHOLDER_BEGIN) && StringUtils.contains(buildLine, SCHEMA_FILE_DD_VALIDATION_PLACEHOLDER_END)) {
                String validationPlaceholder = StringUtils.substringBetween(buildLine, SCHEMA_FILE_DD_VALIDATION_PLACEHOLDER_BEGIN, SCHEMA_FILE_DD_VALIDATION_PLACEHOLDER_END);
                if (StringUtils.isBlank(validationPlaceholder)) {
                    logAndThrowException(String.format("File %s line %s: validation placeholder cannot be blank", buildSchemFile.getAbsolutePath(), lineCount));
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Found dd validation placeholder: " + validationPlaceholder);
                }
                if (!StringUtils.contains(validationPlaceholder, ",")) {
                    logAndThrowException(String.format("File %s, line %s: Invalid format of placehoder value: %s, must contain a ',' seperating parts", buildSchemFile.getAbsolutePath(), lineCount, validationPlaceholder));
                }

                outLine = processValidationPlaceholder(validationPlaceholder, buildLine, buildSchemFile.getAbsolutePath(), lineCount, useDataDictionaryValidation, typesSchemaLines, builtTypes, rebuildDDTypes);
            }

            outSchemaLines.add(outLine);
            lineCount++;
        }

        LOG.debug("Writing schema file to static directory");
        File schemaFile = new File(outSchemaFilePathName);
        FileUtils.writeLines(schemaFile, outSchemaLines);
    }

    /**
     * Performs logic to processes a validation place-holder for a line. First collects the configuration given in the
     * place-holder (general xsd type and data dictionary attribute). If use data dictionary validation is set to false, then the
     * place-holder will be set to the xsd type. If data dictionary validation is set to true, the general xsd type will be
     * removed, and the corresponding the data dictionary will be consulted to build the dd type
     * 
     * <pre>
     * ex. type="${xsd:token,dd:Chart.chartOfAccountsCode}" with useDataDictionaryValidation=false becomes type="xsd:token"
     *    type="${xsd:token,dd:Chart.chartOfAccountsCode}" with useDataDictionaryValidation=true becomes type="dd:Chart.chartOfAccountsCode" and XML lines created for dd Types file
     * </pre>
     * 
     * @param validationPlaceholder the parsed place-holder contents
     * @param buildLine the complete line being read
     * @param fileName the name for the file being processed
     * @param lineCount count for the line being read
     * @param useDataDictionaryValidation indicates whether data dictionary validation should be used, if false the general xsd
     *            datatype in the place-holder will be used
     * @param typesSchemaLines collection of type XML lines to add to for any new types
     * @param builtTypes - Set of attribute names for which a schema validation type has been built
     * @return String the out XML line (which validation filled in)
     */
    protected static String processValidationPlaceholder(String validationPlaceholder, String buildLine, String fileName, int lineCount, boolean useDataDictionaryValidation, Collection typesSchemaLines, Set<String> builtTypes, boolean rebuildDDTypes) {
        String orignalPlaceholderValue = validationPlaceholder;

        // remove whitespace
        validationPlaceholder = StringUtils.deleteWhitespace(validationPlaceholder);

        // get two parts of validation place-holder
        String[] validationParts = StringUtils.split(validationPlaceholder, ",");
        String xsdValidation = validationParts[0];
        if (StringUtils.isBlank(xsdValidation) || !xsdValidation.startsWith(XSD_VALIDATION_PREFIX)) {
            logAndThrowException(String.format("File %s, line %s: specified xsd validation is invalid, must start with %s", fileName, lineCount, XSD_VALIDATION_PREFIX));
        }

        String ddAttributeName = validationParts[1];
        if (StringUtils.isBlank(ddAttributeName) || !ddAttributeName.startsWith(DD_VALIDATION_PREFIX)) {
            logAndThrowException(String.format("File %s, line %s: specified dd validation is invalid, must start with %s", fileName, lineCount, DD_VALIDATION_PREFIX));
        }

        String outLine = buildLine;
        if (useDataDictionaryValidation) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting validation to use type: " + ddAttributeName);
            }
            outLine = StringUtils.replace(outLine, SCHEMA_FILE_DD_VALIDATION_PLACEHOLDER_BEGIN + orignalPlaceholderValue + SCHEMA_FILE_DD_VALIDATION_PLACEHOLDER_END, ddAttributeName);

            if (rebuildDDTypes) {
                buildDataDictionarySchemaValidationType(ddAttributeName, typesSchemaLines, builtTypes);
            }
        }
        else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting validation to use type: " + xsdValidation);
            }
            outLine = StringUtils.replace(outLine, SCHEMA_FILE_DD_VALIDATION_PLACEHOLDER_BEGIN + orignalPlaceholderValue + SCHEMA_FILE_DD_VALIDATION_PLACEHOLDER_END, xsdValidation);
        }

        return outLine;
    }

    /**
     * Constructs new AttributeSchemaValidationBuilder for the given attribute name to build the type XML lines which are added to
     * the given collection
     * 
     * @param ddAttributeName attribute entry name (business object class and attribute name) with dd: namespace prefix
     * @param typesSchemaLines collection of type XML lines to add to for any new types
     * @param builtTypes - Set of attribute names for which a schema validation type has been built
     * @see org.kuali.kfs.sys.context.AttributeSchemaValidationBuilder
     */
    protected static void buildDataDictionarySchemaValidationType(String ddAttributeName, Collection typesSchemaLines, Set<String> builtTypes) {
        // strip prefix from attribute name so we can find it in dd map
        String attributeEntryName = StringUtils.removeStart(ddAttributeName, DD_VALIDATION_PREFIX);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Retrieving entry from data dictionary for attribute: " + attributeEntryName);
        }

        // only build one type for the attribute name
        if (!builtTypes.contains(attributeEntryName)) {
            AttributeSchemaValidationBuilder schemaBuilder = new AttributeSchemaValidationBuilder(attributeEntryName);
            typesSchemaLines.addAll(schemaBuilder.toSchemaType());
            typesSchemaLines.add(" ");

            builtTypes.add(attributeEntryName);
        }
    }

    /**
     * Builds header XML lines for the data dictionary types include
     * 
     * @return Collection containing the XML lines
     */
    protected static Collection initalizeDataDictionaryTypesSchema() {
        LOG.debug("Initializing dd types schema");
        Collection typesSchemaLines = new ArrayList();

        typesSchemaLines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        typesSchemaLines.add("<xsd:schema elementFormDefault=\"qualified\"");
        typesSchemaLines.add("    targetNamespace=\"http://www.kuali.org/kfs/sys/ddTypes\"");
        typesSchemaLines.add("    xmlns:dd=\"http://www.kuali.org/kfs/sys/ddTypes\"");
        typesSchemaLines.add("    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
        typesSchemaLines.add("");

        return typesSchemaLines;
    }

    /**
     * Builds footer XML lines for the data dictionary types include .
     * 
     * @return Collection containing the XML lines
     */
    protected static Collection finalizeDataDictionaryTypesSchema() {
        LOG.debug("Finalizing dd types schema");
        Collection typesSchemaLines = new ArrayList();

        typesSchemaLines.add("</xsd:schema>");

        return typesSchemaLines;
    }

    /**
     * Determines what the relative path of the given file is relative to the given parent path. Since parentPath is configured
     * string method checks for / or \\ path separators .
     * 
     * <pre>
     * eg. File path - /build/project/xsd/gl/collector.xsd, Parent Path - /build/project/xsd returns gl/collector.xsd
     * </pre>
     * 
     * @param file File for which we want to find the relative path
     * @param parentPath Path to parent directory
     * @return String the relative path of the file
     */
    protected static String getRelativeFilePathName(File file, String parentPath) {
        // create File for parentPath so we can compare path to schema File
        File parentDirectory = new File(parentPath);

        String fullParentPathName = parentDirectory.getAbsolutePath();
        String fullFilePathName = file.getAbsolutePath();

        String relativeFilePathName = StringUtils.substringAfter(fullFilePathName, fullParentPathName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("sub-directory for schema: " + relativeFilePathName);
        }

        if (StringUtils.isBlank(relativeFilePathName)) {
            String msg = String.format("Cannot find relative path for file name %s from parent directory %s", fullFilePathName, fullParentPathName);
            LOG.error(msg);
            throw new RuntimeException(msg);
        }

        return relativeFilePathName;
    }

    /**
     * Helper method for logging an error and throwing a new RuntimeException
     * 
     * @param msg message for logging and exception
     */
    protected static void logAndThrowException(String msg) {
        LOG.error(msg);
        throw new RuntimeException(msg);
    }

}
