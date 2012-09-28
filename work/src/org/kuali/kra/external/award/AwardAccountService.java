/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kra.external.award;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AwardAccountDTO;

@WebService(targetNamespace = KcConstants.KC_NAMESPACE_URI, name = KcConstants.AwardAccount.SOAP_SERVICE_NAME)
public interface AwardAccountService {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getAwardAccounts", targetNamespace = KcConstants.KC_NAMESPACE_URI, className = "kc.GetAwardAccounts")
    @WebMethod
    @ResponseWrapper(localName = "getAwardAccountsResponse", targetNamespace = KcConstants.KC_NAMESPACE_URI, className = "kc.GetAwardAccountsResponse")
    public java.util.List<AwardAccountDTO> getAwardAccounts(
        @WebParam(name = "financialAccountNumber", targetNamespace = "")                
        java.lang.String financialAccountNumber,
        @WebParam(name = "chartOfAccounts", targetNamespace = "")
        java.lang.String chartOfAccounts
    );
}
