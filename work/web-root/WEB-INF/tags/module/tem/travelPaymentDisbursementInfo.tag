<%--
 Copyright 2006-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="sourceDocumentNumber" required="true"
              description="Document number to get disbursement info for" %>
<%@ attribute name="sourceDocumentType" required="true"
              description="Document type to get disbursement info for" %>   
              
<c:url var="page" value="${KualiForm.disbursementInfoUrl}">
  <c:param name="custPaymentDocNbr" value="${sourceDocumentNumber}"/>
  <c:param name="financialDocumentTypeCode" value="${sourceDocumentType}"/>
</c:url>
<c:url var="image" value="${ConfigProperties.externalizable.images.url}tinybutton-disbursinfo.gif"/>
							  
&nbsp;<a href="${page}" target="_pdp"><img src="${image}" border="0"/></a>              
