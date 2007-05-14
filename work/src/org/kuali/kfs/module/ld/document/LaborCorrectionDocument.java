/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.labor.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.service.CorrectionDocumentService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.ScrubberService;

/**
 * 
 */
public class LaborCorrectionDocument extends CorrectionDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCorrectionDocument.class);

    public LaborCorrectionDocument() {
        super();
    }
}
