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
package org.kuali.kfs.module.purap.util.cxml;

import java.io.File;

import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;

/**
 * This is the base class for all the b2b file type classes. All the methods in this class
 * returns a default value since there is no need to do anything in these methods in b2b.
 */

public abstract class B2BFileTypeBase  extends XmlBatchInputFileTypeBase{

    public String getFileName(String principalId, Object parsedFileContents, String fileUserIdentifer) {
        return null;
    }

    public boolean validate(Object parsedFileContents) {
        return false;
    }
    
    public String getTitleKey() {
        return null;
    }

    public String getAuthorPrincipalName(File file) {
        return null;
    }
}
