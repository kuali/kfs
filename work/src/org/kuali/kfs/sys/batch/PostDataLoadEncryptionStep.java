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
package org.kuali.kfs.batch;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.core.io.FileSystemResource;

public class PostDataLoadEncryptionStep extends AbstractDatabaseImportExportStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostDataLoadEncryptionStep.class);
    private String attributesToEncryptProperties;

    public boolean execute() {
        Properties attributesToEncryptProperties = new Properties();
        try {
            attributesToEncryptProperties.load(new FileSystemResource(this.attributesToEncryptProperties).getInputStream());
        }
        catch (Exception e) {
            throw new IllegalArgumentException("PostDataLoadEncrypter requires the full, absolute path to a properties file where the keys are the names of the BusinessObject classes that should be processed and the values are the list of attributes on each that require encryption", e);
        }
        for (Object businessObjectClassName : attributesToEncryptProperties.keySet()) {
            Class businessObjectClass;
            try {
                businessObjectClass = Class.forName((String) businessObjectClassName);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(new StringBuffer("Unable to load Class ").append(businessObjectClassName).append(" specified by name in attributesToEncryptProperties file ").append(attributesToEncryptProperties).toString(), e);
            }
            Set<String> attributeNames = null;
            try {
                attributeNames = new HashSet(Arrays.asList(StringUtils.split((String) attributesToEncryptProperties.get(businessObjectClassName), ",")));
            }
            catch (Exception e) {
                throw new IllegalArgumentException(new StringBuffer("Unable to load attributeNames Set from comma-delimited list of attribute names specified as value for property with Class name ").append(businessObjectClassName).append(" key in attributesToEncryptProperties file ").append(attributesToEncryptProperties).toString(), e);
            }
            databaseImportExportService.checkArguments(businessObjectClass, attributeNames);
            databaseImportExportService.createBackupTable(businessObjectClass);
            try {
                databaseImportExportService.prepClassDescriptor(businessObjectClass, attributeNames);
                Collection objectsToEncrypt = KNSServiceLocator.getBusinessObjectService().findAll(businessObjectClass);
                databaseImportExportService.truncateTable(businessObjectClass);
                for (Object businessObject : objectsToEncrypt) {
                    databaseImportExportService.encrypt((PersistableBusinessObject) businessObject, attributeNames);
                }
                databaseImportExportService.restoreClassDescriptor(businessObjectClass, attributeNames);
                LOG.info(new StringBuffer("Encrypted ").append(attributesToEncryptProperties.get(businessObjectClassName)).append(" attributes of Class ").append(businessObjectClassName));
            }
            catch (Exception e) {
                databaseImportExportService.restoreTableFromBackup(businessObjectClass);
                LOG.error(new StringBuffer("Caught exception, while encrypting ").append(attributesToEncryptProperties.get(businessObjectClassName)).append(" attributes of Class ").append(businessObjectClassName).append(" and restored table from backup"), e);
            }
            databaseImportExportService.dropBackupTable(businessObjectClass);
        }
        return true;
    }

    /**
     * Sets the attributesToEncryptProperties attribute value.
     * @param attributesToEncryptProperties The attributesToEncryptProperties to set.
     */
    public void setAttributesToEncryptProperties(String attributesToEncryptProperties) {
        this.attributesToEncryptProperties = attributesToEncryptProperties;
    }
}
