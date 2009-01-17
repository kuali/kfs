/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.util.cxml;

import java.io.File;

import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;

public abstract class B2BFileTypeBase  extends BatchInputFileTypeBase{

    public String getFileName(String principalId, Object parsedFileContents, String fileUserIdentifer) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean validate(Object parsedFileContents) {
        // TODO Auto-generated method stub
        return false;
    }
    
    public String getTitleKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getAuthorPrincipalId(File file) {
        // TODO Auto-generated method stub
        return null;
    }
}
