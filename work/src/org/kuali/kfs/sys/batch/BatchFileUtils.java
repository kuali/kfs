/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sys.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

public class BatchFileUtils {
    public static List<File> retrieveBatchFileLookupRootDirectories() {
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        List<File> directories = new ArrayList<File>();
        String configProperty = kualiConfigurationService.getPropertyString(KFSConstants.BATCH_FILE_LOOKUP_ROOT_DIRECTORIES);
        
        String[] directoryNames = StringUtils.split(configProperty, ";");
        for (String directoryName : directoryNames) {
            File rootDirectory = new File(directoryName).getAbsoluteFile();
            directories.add(rootDirectory);
        }
        
        return directories;
    }
    
    public static boolean isDirectoryAccessible(String directory) {
        List<String> pathNames = null;
        
        Class<? extends KeyValuesFinder> keyValuesFinderClass = SpringContext.getBean(DataDictionaryService.class).getAttributeValuesFinderClass(BatchFile.class, "path");
        try {
            if (keyValuesFinderClass != null) {
                KeyValuesFinder valuesGenerator = keyValuesFinderClass.newInstance();
                pathNames = new ArrayList<String>();
                
                List<KeyLabelPair> keyValues = valuesGenerator.getKeyValues();
                for (KeyLabelPair keyValue : keyValues) {
                    pathNames.add(new File((String) keyValue.getKey()).getAbsolutePath());
                }
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("can't instiantiate class " + keyValuesFinderClass, e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("can't instiantiate class " + keyValuesFinderClass, e);
        }

        File directoryAbsolute = new File(directory).getAbsoluteFile();
        String directoryAbsolutePath = directoryAbsolute.getAbsolutePath();
        if (pathNames != null) {
            if (!pathNames.contains(directoryAbsolutePath)) {
                return false;
            }
        }
        
        List<File> rootDirectories = retrieveBatchFileLookupRootDirectories();
        for (File rootDirectory : rootDirectories) {
            if (isSuperDirectoryOf(rootDirectory, directoryAbsolute)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isSuperDirectoryOf(File superDirectory, File directory) {
        superDirectory = superDirectory.getAbsoluteFile();
        
        while (directory != null) {
            directory = directory.getAbsoluteFile();
            if (directory.equals(superDirectory)) {
                return true;
            }
            directory = directory.getParentFile();
        }
        return false;
    }
}
