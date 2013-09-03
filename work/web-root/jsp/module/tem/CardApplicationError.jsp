<%--
 Copyright 2006-2008 The Kuali Foundation
 
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

<kul:documentPage showDocumentInfo="true"
    documentTypeName="${KualiForm.docTypeName}"
    htmlFormAction="temCorporateCardApplication" renderMultipart="true"
    showTabButtons="true">
	<c:if test="${KualiForm.emptyProfile }">
		<div align="center" style="color:#ff0000">
			This user doesn't have a TEM Profile.  The application cannot be completed until a TEM Profile is created for this user.
		</div>
	</c:if>
	<c:if test="${KualiForm.emptyAccount}">
		<div align="center" style="color:#ff0000">
			This user's TEM Profile is missing Default Accounting information. In order to initiate an application, the Default Accounting section of the TEM Profile must be completed.
		</div>
	</c:if>
	<c:if test="${KualiForm.multipleApplications}">
		<div align="center" style="color:#ff0000">
			This user already has an application that is either enroute, or already approved.  Please contact Travel Office for more information.
		</div>
	</c:if>
</kul:documentPage>