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
package org.kuali.kfs.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Miscalenious Utility Methods.
 */
public class KFSUtils {
    
    /**
     * Picks off the filename from the full path. Takes care of different OS seperators.
     */
    public final static List<String> getFileNameFromPath(List<String> fullFileNames) {
        List<String> fileNameList = new ArrayList();
        
        for (String fullFileName: fullFileNames) {
            if (StringUtils.contains(fullFileName, "/")) {
                fileNameList.add(StringUtils.substringAfterLast(fullFileName, "/"));
            }
            else {
                fileNameList.add(StringUtils.substringAfterLast(fullFileName, "\\"));
            }
        }    
        
        return fileNameList;
    }
}
