/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.service;

import java.util.List;

import org.kuali.kfs.module.tem.document.TemCorrectionProcessDocument;
import org.kuali.kfs.module.tem.document.web.struts.TemCorrectionForm;
import org.kuali.rice.kns.web.ui.Column;

public interface TemCorrectionDocumentService {
    public final static String CORRECTION_TYPE_MANUAL = "M";
    public final static String CORRECTION_TYPE_CRITERIA = "C";
    public final static String CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING = "R";

    public final static String SYSTEM_DATABASE = "D";
    public final static String SYSTEM_UPLOAD = "U";
    
    String getBatchFileDirectoryName();
    
    public List<Column> getTableRenderColumnMetadata(String docId);

    void persistAgencyEntryGroupsForDocumentSave(TemCorrectionProcessDocument document, TemCorrectionForm correctionForm);

}
