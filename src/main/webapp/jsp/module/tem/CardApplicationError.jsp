<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
