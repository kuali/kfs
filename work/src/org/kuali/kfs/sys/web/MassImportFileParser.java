/*
 * Copyright 2013 The Kuali Foundation.
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

import java.util.List;

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.document.MassImportDocument;


public interface MassImportFileParser {

    /**
     * @see org.kuali.kfs.module.purap.util.ItemParser#parseItem(org.apache.struts.upload.FormFile,java.lang.Class,java.lang.String)
     */
    public abstract List<MassImportLineBase> importLines(FormFile importFile, Class<? extends MassImportLineBase> itemClass, MassImportDocument document, String errorPathPrefix);

}