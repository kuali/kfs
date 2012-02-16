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
package org.kuali.kfs.module.tem.util;

import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_UPLOADPARSER_INVALID_FILE_FORMAT;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_UPLOADPARSER_INVALID_NUMERIC_VALUE;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_UPLOADPARSER_LINE;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_UPLOADPARSER_PROPERTY;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_UPLOADPARSER_WRONG_PROPERTY_NUMBER;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_UPLOADPARSER_EXCEEDED_MAX_LENGTH;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_UPLOADPARSER_INVALID_VALUE;
import static org.kuali.kfs.sys.context.SpringContext.getBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.tem.exception.UploadParserException;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.exception.InfrastructureException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.format.FormatException;

/**
 * 
 * This class provides a way to import a file into a list of objects.
 * A generalized version of kfs purap ItemParserBase
 * 
 */
public class UploadParser {

    protected static final String[] DEFAULT_FILE_EXTENSIONS = {"csv"};
    
    public static String[] getFileFormat(Class<?> c) {
        return (String[]) TemObjectUtils.getSettableFieldNames(c).toArray();
    }
    
    public static List<String> getDefaultAcceptableFileExtensions() {
        return Arrays.asList(DEFAULT_FILE_EXTENSIONS);
    }    

    public static String getExpectedLineFormatAsString(Class<?> c, String[] attributeNames) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (String attributeName : attributeNames) {
            if (!first) {
                sb.append(",");
            }
            else {
                first = false;
            }
            sb.append( getAttributeLabel( c, attributeName ) );
        }
        return sb.toString();
    }

    /**
     * 
     * This method retrieves the attribute label for the specified attribute.
     * @param clazz
     * @param attributeName
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected static String getAttributeLabel(Class clazz, String attributeName) {
        String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(clazz, attributeName);
        if (StringUtils.isBlank(label)) {
            label = attributeName;
        }
        return label;
    }

    /**
     * This method checks whether the specified import file is not null and of a valid format;
     * throws exceptions if conditions not satisfied.
     * @param formFile
     * @param fileExtensions
     */
    protected static void checkFormFile(FormFile formFile, List<String> fileExtensions) {
        if (formFile == null) {
            throw new UploadParserException("invalid (null) import file", KFSKeyConstants.ERROR_UPLOADFILE_NULL);
        }
        String fileName = formFile.getFileName();
        if (StringUtils.isNotBlank(fileName)) {
            String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1);
            if(!fileExtensions.contains(fileExtension)){
                throw new UploadParserException("unsupported file format: " + fileName, ERROR_UPLOADPARSER_INVALID_FILE_FORMAT, fileName);
            }
        }
    }

    /**
     * 
     * This method parses a line of data from a csv file and retrieves the attributes as key-value string pairs into a map.
     * @param line
     * @param attributeNames
     * @param lineNo
     * @param tabErrorKey
     * @return
     */
    protected static Map<String, String> retrieveObjectAttributes(String line, String[] attributeNames,Map<String, List<String>> defaultValues, Integer[] attributeMaxLength, Integer lineNo, String tabErrorKey) {
        String[] attributeValues = StringUtils.splitPreserveAllTokens(line, ',');
        if (attributeNames.length != attributeValues.length) {
            String[] errorParams = { "" + attributeNames.length, "" + attributeValues.length, "" + lineNo };
            GlobalVariables.getMessageMap().putError(tabErrorKey, ERROR_UPLOADPARSER_WRONG_PROPERTY_NUMBER, errorParams);
            throw new UploadParserException("wrong number of properties: " + attributeValues.length + " exist, " + attributeNames.length + " expected (line " + lineNo + ")", ERROR_UPLOADPARSER_WRONG_PROPERTY_NUMBER, errorParams);
        }

        for (int i = 0; i < attributeNames.length; i++) {
            if (defaultValues != null && defaultValues.get(attributeNames[i]) != null) {
                List<String> defaultValue = defaultValues.get(attributeNames[i]);
                boolean found = false;
                for (String value : defaultValue) {
                    if (attributeValues[i].equalsIgnoreCase(value))
                        found = true;
                }
                if (!found) {
                    GlobalVariables.getMessageMap().putWarning(tabErrorKey, MESSAGE_UPLOADPARSER_INVALID_VALUE, attributeNames[i], attributeValues[i], (" " + lineNo));
                    throw new UploadParserException("Invalid value " + attributeValues[i] + " exist, " + "in line (" + lineNo + ")", ERROR_UPLOADPARSER_WRONG_PROPERTY_NUMBER);
                }
            }

            if (attributeMaxLength != null) {
                if (attributeValues[i] != null && attributeValues[i].length() > attributeMaxLength[i]) {
                    attributeValues[i] = attributeValues[i].substring(0, attributeMaxLength[i]);
                    String[] errorParams = { "" + attributeNames[i], "" + attributeMaxLength[i], "" + lineNo };
                    GlobalVariables.getMessageMap().putWarning(tabErrorKey, MESSAGE_UPLOADPARSER_EXCEEDED_MAX_LENGTH, errorParams);
                }
            }
        }

        Map<String, String> objectMap = new HashMap<String, String>();
        for (int i = 0; i < attributeNames.length; i++) {
            objectMap.put(attributeNames[i], attributeValues[i]);
        }
        
        return objectMap;
    }
    
    /**
     * 
     * This method generates an object instance and populates it with the specified attribute map.
     * @param objectMap
     * @param c
     * @param lineNo
     * @param tabErrorKey
     * @return
     */
    protected static Object genObjectWithRetrievedAttributes(Map<String, String> objectMap, Class<?> c, Integer lineNo, String tabErrorKey) {
        Object object;
        try {
            object = c.newInstance();
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("unable to complete line population.", e);
        }
        catch (InstantiationException e) {
            throw new InfrastructureException("unable to complete line population.", e);
        }
        
        boolean failed = false;
        for (Entry<String, String> entry : objectMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();          
            try {
                try {
                    ObjectUtils.setObjectProperty(object, key, value);
                }
                catch (FormatException e) {
                    String[] errorParams = { value, key, "" + lineNo };
                    throw new UploadParserException("invalid numeric property value: " + key + " = " + value + " (line " + lineNo + ")", ERROR_UPLOADPARSER_INVALID_NUMERIC_VALUE, errorParams);
                }
            }
            catch (UploadParserException e) {
                // continue to parse the rest of the properties after the current property fails
                GlobalVariables.getMessageMap().putError(tabErrorKey, e.getErrorKey(), e.getErrorParameters());
                failed = true;
            }
            catch (IllegalAccessException e) {
                throw new InfrastructureException("unable to complete line population.", e);
            }
            catch (NoSuchMethodException e) {
                throw new InfrastructureException("unable to complete line population.", e);
            }
            catch (InvocationTargetException e) {
                throw new InfrastructureException("unable to complete line population.", e);
            }
        }

        if (failed) {
            throw new UploadParserException("empty or invalid properties in line " + lineNo + ")", ERROR_UPLOADPARSER_PROPERTY, ""+lineNo);             
        }
        return object;
    }
    
    /**
     * 
     * This method parses the formFile
     * @param line
     * @param c
     * @param attributeNames
     * @param lineNo
     * @param tabErrorKey
     * @return
     */
    protected static PersistableBusinessObject parseFile( String line, Class<?> c, String[] attributeNames,Map<String, List<String>> defaultValues, Integer[] attributeMaxLength, Integer lineNo, String tabErrorKey) {
        Map<String, String> objectMap = retrieveObjectAttributes(line, attributeNames, defaultValues, attributeMaxLength, lineNo, tabErrorKey);
        PersistableBusinessObject obj = (PersistableBusinessObject) genObjectWithRetrievedAttributes(objectMap, c, lineNo, tabErrorKey);
        obj.refresh();
        return obj;
    }
    
    /**
     * 
     * This method imports the file and convert it to a list of objects (of the class specified in the parameter)
     * @param formFile
     * @param c
     * @param attributeNames
     * @param tabErrorKey
     * @return
     */
    //TODO: re-evaluate KUALITEM-954 in regards to defaultValues and attributeMaxLength. Validation should not happen at parsing (these param are only used by importAttendees in TravelEntertainmentAction).
    public static List<Object> importFile(FormFile formFile, Class<?> c, String[] attributeNames, Map<String, List<String>> defaultValues, Integer[] attributeMaxLength, String tabErrorKey) {
        if(attributeMaxLength != null && attributeNames.length != attributeMaxLength.length){
            throw new UploadParserException("Invalid parser configuration, the number of attribute names and attribute max length should be the same"); 
        }
        
        return importFile(formFile, c, attributeNames, defaultValues, attributeMaxLength, tabErrorKey, getDefaultAcceptableFileExtensions());
    }
    
    /**
     * 
     * This method imports the file and convert it to a list of objects (of the class specified in the parameter)
     * @param formFile
     * @param c
     * @param attributeNames
     * @param tabErrorKey
     * @param fileExtensions
     * @return
     */
    public static List<Object> importFile( FormFile formFile, Class<?> c, String[] attributeNames,Map<String, List<String>> defaultValues, Integer[] attributeMaxLength, String tabErrorKey, List<String> fileExtensions) {
        // check input parameters
        try {
            checkFormFile(formFile,fileExtensions);
        }
        catch (IllegalArgumentException e) {
            throw new InfrastructureException("unable to import lines.", e);
        }

        // open input stream
        List<Object> importedObjects = new ArrayList<Object>();
        InputStream is;
        BufferedReader br;
        try {
            is = formFile.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
        }
        catch (IOException e) {
            throw new InfrastructureException("unable to open import file.", e);
        }
        
        // parse file line by line
        Integer lineNo = 0;
        boolean failed = false;
        String line = null;
        try {
            while ( (line = br.readLine()) != null ) {
                lineNo++;
                try {
                    Object o = parseFile(line, c, attributeNames, defaultValues, attributeMaxLength, lineNo, tabErrorKey);
                    importedObjects.add(o);
                }
                catch (UploadParserException e) {
                    // continue to parse the rest of the lines after the current line fails
                    // error messages are already dealt with inside parseFile, so no need to do anything here
                    failed = true;
                }                
            }
            
            if (failed) {
                throw new UploadParserException("errors in parsing lines in file " + formFile.getFileName(), ERROR_UPLOADPARSER_LINE, formFile.getFileName());             
            }
        }
        catch (IOException e) {
            throw new InfrastructureException("unable to read line from BufferReader", e);
        }
        finally {
            try {
                br.close();
            }
            catch (IOException e) {
                throw new InfrastructureException("unable to close BufferReader", e);
            }
        }

        return importedObjects;
    }
    
    protected DictionaryValidationService getDictionaryValidationService() {
        return getBean(DictionaryValidationService.class);
    }
}
