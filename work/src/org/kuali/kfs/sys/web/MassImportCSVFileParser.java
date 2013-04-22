/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.MassImportDocument;
import org.kuali.kfs.sys.exception.MassImportFileParserException;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class MassImportCSVFileParser implements MassImportFileParser {
    private static final Logger LOG = Logger.getLogger(MassImportCSVFileParser.class);
    private Integer lineNo = 0;

    protected String retrieveAttributeLabel(Class clazz, String attributeName) {
        String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(clazz, attributeName);
        if (StringUtils.isBlank(label)) {
            label = attributeName;
        }
        return label;
    }

    /**
     * Checks whether the specified item class is a subclass of PurApItem; throws exceptions if not.
     *
     * @param itemClass the specified item class
     */
    protected void checkImportLineClass(Class importLineClass) {
        if (!MassImportLineBase.class.isAssignableFrom(importLineClass)) {
            throw new IllegalArgumentException("unknown import class: " + importLineClass);
        }
    }

    /**
     * Checks whether the specified item import file is not null and of a valid format; throws exceptions if conditions not
     * satisfied.
     *
     * @param itemClass the specified item import file
     */
    protected void checkImportFile(FormFile importFile) {
        String fileName = importFile.getFileName();
        if (StringUtils.isNotBlank(fileName) && !StringUtils.lowerCase(fileName).endsWith(".csv")) {
            throw new MassImportFileParserException("Unsupported item import file format: " + fileName, fileName);
        }
    }

    /**
     * Parses a line of item data from a csv file and retrieves the attributes as key-value string pairs into a map.
     *
     * @param itemLine a string read from a line in the item import file
     * @return a map containing item attribute name-value string pairs
     */
    protected Map<String, String> retrieveLineAttributes(String importedLine, Class lineClass, MassImportDocument document) {
        String[] attributeNames = document.getOrderedFieldList();
        if (attributeNames == null) {
            throw new MassImportFileParserException("wrong import line class " + lineClass.getName(), "", new String[] { lineClass.getName() });
        }
        String[] attributeValues = StringUtils.splitPreserveAllTokens(importedLine, ',');

        Map<String, String> attributeValueMap = new HashMap<String, String>();
        for (int i = 0; i < Math.min(attributeValues.length, attributeNames.length); i++) {
            attributeValueMap.put(attributeNames[i], attributeValues[i].trim());
        }
        return attributeValueMap;
    }

    /**
     * Generates an item instance and populates it with the specified attribute map.
     *
     * @param itemMap the specified attribute map from which attributes are populated
     * @param itemClass the class of which the new item instance shall be created
     * @return the populated item
     */
    protected MassImportLineBase genSingleObjectWithRetrievedAttributes(Map<String, String> attributeValueMap, Class<? extends MassImportLineBase> importedLineClass, String importedLine) {
        MassImportLineBase lineObject;
        try {
            lineObject = importedLineClass.newInstance();
            for (Entry<String, String> entry : attributeValueMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                try {
                    try {
                        Class entryType = ObjectUtils.easyGetPropertyType(lineObject, entry.getKey());
                        if (String.class.isAssignableFrom(entryType)) {
                            entry.setValue(entry.getValue().toUpperCase());
                        }
                        ObjectUtils.setObjectProperty(lineObject, key, value);
                    }
                    catch (IllegalArgumentException e) {
                        LOG.error("", e);
                        throw new MassImportFileParserException("", KFSKeyConstants.ERROR_MASSIMPORT_FILEUPLOAD_GENERAL, new String[] { e.getMessage() });
                    }
                }
                catch (FormatException e) {
                    String[] errorParameters = { entry.getValue().toString(), retrieveAttributeLabel(importedLine.getClass(), entry.getKey()), importedLine };
                    throw new MassImportFileParserException("invalid '" + entry.getKey() + "=" + entry.getValue() + "for " + importedLine, KFSKeyConstants.ERROR_MASSIMPORT_INVALIDPROPERTYVALUE, errorParameters);

                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("unable to complete import line population.", e);
        }
        // force input to uppercase
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(lineObject);
        lineObject.refresh();
        return lineObject;
    }


    /**
     * @see org.kuali.kfs.module.purap.util.ItemParser#parseItem(java.lang.String,java.lang.Class,java.lang.String)
     */
    public MassImportLineBase parseSingleLine(String importedLine, Class<? extends MassImportLineBase> lineClass, MassImportDocument document) {
        Map<String, String> attributeValueMap = retrieveLineAttributes(importedLine, lineClass, document);
        MassImportLineBase line = genSingleObjectWithRetrievedAttributes(attributeValueMap, lineClass, importedLine);
        return line;
    }

    /**
     * @see org.kuali.kfs.module.purap.util.ItemParser#parseItem(org.apache.struts.upload.FormFile,java.lang.Class,java.lang.String)
     */
    @Override
    public List<MassImportLineBase> importLines(FormFile importFile, Class<? extends MassImportLineBase> itemClass, MassImportDocument document, String errorPathPrefix) {
        // validate import file extension
        List<MassImportLineBase> importedLines = new ArrayList();
        if (validateImportFile(importFile, itemClass, errorPathPrefix)) {
            // open input stream
            BufferedReader br = null;
            try {
                InputStream is = importFile.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                // parse line by line
                lineNo = 0;
                boolean failed = false;
                String line = null;
                while ((line = br.readLine()) != null) {
                    lineNo++;
                    if (StringUtils.isBlank(StringUtils.remove(StringUtils.deleteWhitespace(line), ","))) {
                        continue;
                    }
                    importedLines.add(parseSingleLine(line, itemClass, document));
                }
            }
            catch (Exception e) {
                LOG.error("Error occurred... ", e);
                throw new MassImportFileParserException("", KFSKeyConstants.ERROR_MASSIMPORT_FILEUPLOAD_GENERAL, new String[] { e.getMessage() });
            }
            finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                }
                catch (IOException e) {
                    LOG.error("Error occurred... ", e);
                    throw new RuntimeException("Unable to close BufferReader in ChartFileParserBase", e);
                }
            }

        }
        return importedLines;
    }

    /**
     * validate the import file format
     *
     * @param importFile
     * @param itemClass
     * @param errorPathPrefix
     * @return
     */
    private boolean validateImportFile(FormFile importFile, Class<? extends MassImportLineBase> itemClass, String errorPathPrefix) {
        boolean valid = true;
        // check input parameters
        try {
            checkImportLineClass(itemClass);
            checkImportFile(importFile);
        }
        catch (MassImportFileParserException e) {
            GlobalVariables.getMessageMap().putError(errorPathPrefix, KFSKeyConstants.ERROR_MASSIMPORT_FILEUPLOAD_GENERAL, new String[] { e.getMessage() });
            valid = false;
        }
        catch (IllegalArgumentException e) {
            LOG.error("", e);
            throw new RuntimeException("Unable to import file.", e);
        }
        return valid;
    }

}
