<%--
 Copyright 2007-2008 The Kuali Foundation
 
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

<c:set var="DVDocNumbersNotFinalized" value="${KualiForm.DVDocNumbersNotFinalized}" />
<c:if test="${fn:length(DVDocNumbersNotFinalized) > 0}">
	${kfunc:registerEditableProperty(KualiForm, "methodToCall")}
    <div align="left">
   		<c:forEach items="${DVDocNumbersNotFinalized}" var="DVdocNumber">
   			<font color="red">The disbursement voucher was not finalized, complete the document <a target="_blank" href='financialDisbursementVoucher.do?methodToCall=docHandler&docId=<c:out value="${DVdocNumber}" />&command=displayDocSearchView#topOfForm"'>#<c:out value="${DVdocNumber}" /></a> manually and submit. Or, you may retrieve the document from the action list.</font>
   		</c:forEach>
    </div>
    <br>
</c:if>
