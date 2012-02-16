<%--
 Copyright 2007-2009 The Kuali Foundation
 
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
<style type="text/css">
div#reports li {
	text-align: left;
	list-style-type: none;
	padding: 5 5 5 5;
}

div#reports li:hover {
	background-color: #6B6B6B;
	color: #ffffff;
	text-decoration: underline
}

div#reports li a {
	color: inherit;
	text-decoration: none;
}
</style>
<kul:tab tabTitle="Reports" defaultOpen="false">
	<div id="reports" class="tab-container" align="center">
		<h3>Links to Generated Reports</h3>

		<div>
			<bean:message key="label.document.travel.report" />
			<ul>
				<li>
					<a href="temTravelEntertainment.do?methodToCall=printCoversheet&${PropertyConstants.DOCUMENT_NUMBER}=${KualiForm.document.documentNumber}">
						<bean:message key="label.document.travelRelocation.faxCoverSheet" />
					</a> 
					<html:img src="${ConfigProperties.externalizable.images.url}icon-pdf.png" title="Coversheet" alt="Coversheet" width="16" height="16" />
				</li>			
<%-- 				<c:if test="${KualiForm.displayNonEmployeeForm}"> --%>
					<li>
						<a href="temTravelEntertainment.do?methodToCall=viewNonEmployeeForms&${PropertyConstants.DOCUMENT_NUMBER}=${KualiForm.document.documentNumber}">
							<bean:message key="label.document.travel.nonEmployeeForms" /> 
						</a> 
						<html:img src="${ConfigProperties.externalizable.images.url}icon-pdf.png" title="print Non-Employee Forms" alt="print Non-Employee Forms" width="16" height="16" />
					</li>
<%-- 				</c:if> --%>
<%-- 				<c:if test="${KualiForm.canPrintHostCertification}"> --%>
					<li>
						<a href="temTravelEntertainment.do?methodToCall=viewEntertainmentCertification&${PropertyConstants.DOCUMENT_NUMBER}=${KualiForm.document.documentNumber}">
							<bean:message key="label.document.travelEntertainment.EntertainmentCertification" />
						</a> 
						<html:img src="${ConfigProperties.externalizable.images.url}icon-pdf.png" title="Print TEM Host Certification" alt="Print TEM Host Certification" width="16" height="16" />
					</li>
<%-- 				</c:if> --%>
			</ul>
		</div>
	</div>
</kul:tab>
