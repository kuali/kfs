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
package org.kuali.kfs.module.purap.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;

public class PurapSearchUtils {

    public static String getWorkFlowStatusString(FinancialSystemDocumentHeader documentHeader){
        
        if (StringUtils.equals(KFSConstants.DocumentStatusCodes.INITIATED,documentHeader.getFinancialDocumentStatusCode())){
            return "INITIATED";
        }else if (StringUtils.equals(KFSConstants.DocumentStatusCodes.ENROUTE,documentHeader.getFinancialDocumentStatusCode())){
            return "ENROUTE";
        } else if (StringUtils.equals(KFSConstants.DocumentStatusCodes.DISAPPROVED,documentHeader.getFinancialDocumentStatusCode())){
            return "DISAPPROVED";
        } else if (StringUtils.equals(KFSConstants.DocumentStatusCodes.CANCELLED,documentHeader.getFinancialDocumentStatusCode())){
            return "CANCELLED";
        } else if (StringUtils.equals(KFSConstants.DocumentStatusCodes.APPROVED,documentHeader.getFinancialDocumentStatusCode())){
            return "APPROVED";
        }else{
            return StringUtils.EMPTY;
        }
    }
}
