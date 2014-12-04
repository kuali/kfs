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
			<ul>
				<li>
					<a href="temTravelReimbursement.do?methodToCall=printCoversheet&${PropertyConstants.DOCUMENT_NUMBER}=${KualiForm.document.documentNumber}">
						<bean:message key="label.document.travelReimbursement.printCoverSheet" />
					</a> 
					<html:img src="${ConfigProperties.externalizable.images.url}icon-pdf.png" title="print cover sheet" alt="print cover sheet" width="16" height="16" />
				</li>
<%-- 				<c:if test="${KualiForm.displayNonEmployeeForm}"> --%>
					<li>
						<a href="temTravelReimbursement.do?methodToCall=viewNonEmployeeForms&${PropertyConstants.DOCUMENT_NUMBER}=${KualiForm.document.documentNumber}">
							<bean:message key="label.document.travel.nonEmployeeForms" /> 
						</a> 
						<html:img src="${ConfigProperties.externalizable.images.url}icon-pdf.png" title="print Non-Employee Forms" alt="print Non-Employee Forms" width="16" height="16" />
					</li>
<%-- 				</c:if> --%>				
				<li>
					<a href="temTravelReimbursement.do?methodToCall=viewExpenseSummary&${PropertyConstants.DOCUMENT_NUMBER}=${KualiForm.document.documentNumber}">
						<bean:message key="label.document.travelReimbursement.expenseSummary" />
					</a> 
					<html:img src="${ConfigProperties.externalizable.images.url}icon-pdf.png" title="print expense summary" alt="print expense summary" width="16" height="16" />
				</li>
				<li>
					<a href="temTravelReimbursement.do?methodToCall=viewSummaryByDay&${PropertyConstants.DOCUMENT_NUMBER}=${KualiForm.document.documentNumber}">
						<bean:message key="label.document.travelReimbursement.summaryByDay" />
					</a> 
					<html:img src="${ConfigProperties.externalizable.images.url}icon-pdf.png" title="print summary by day" alt="print summary by day" width="16" height="16" />
				</li>
			</ul>
		</div>
	</div>
</kul:tab>
