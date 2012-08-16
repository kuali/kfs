/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.sys.service.impl;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.FlatFileProcessor;

public class FlatFileProcessorImpl implements FlatFileProcessor {

    @Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifier) {
        return KFSConstants.EMPTY_STRING;
    }

    @Override
    public void process(String fileName, Object parsedFileContents) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean validate(Object parsedFileContents) {
        // TODO Auto-generated method stub
        return true;
    }

}
