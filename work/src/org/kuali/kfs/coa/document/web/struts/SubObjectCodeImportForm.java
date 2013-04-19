/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.coa.document.web.struts;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.document.web.struts.MassImportTransactionalDocumentFormBase;
import org.kuali.kfs.sys.web.MassImportCSVFileParser;
import org.kuali.kfs.sys.web.MassImportFileParser;

/**
 * Mass upload sub-account, sub-object, project. This class is the Struts specific form object that works in conjunction with the
 * pojo utilities to build the UI.
 */
public class SubObjectCodeImportForm extends MassImportTransactionalDocumentFormBase {

    private static final Logger Log = Logger.getLogger(SubObjectCodeImportForm.class);

    @Override
    public MassImportFileParser getMassImportFileParser() {
        return new MassImportCSVFileParser();
    }
}
