/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web.struts;

import org.kuali.kfs.fp.document.CapitalAssetEditable;

/**
 * Struts form for <code>{@link YearEndGeneralErrorCorrectionDocument}</code>. This class is mostly empty because it inherits
 * everything it needs from its parent non-year end version. This is necessary to adhere to the transactional document framework.
 */
public class YearEndGeneralErrorCorrectionForm extends GeneralErrorCorrectionForm  implements CapitalAssetEditable{
    /**
     * Constructs a YearEndGeneralErrorCorrectionForm instance.
     */
    public YearEndGeneralErrorCorrectionForm() {
        super();
    }
    
    @Override
    protected String getDefaultDocumentTypeName() {
        return "YEGE";
    }
}
