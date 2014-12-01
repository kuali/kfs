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

<c:set var="documentAttributes"
     value="${DataDictionary.TravelReimbursementDocument.attributes}" />
<c:set var="docHeaderAttributes"
     value="${DataDictionary.DocumentHeader.attributes}" />
<kul:tab tabTitle="View Reimbursement History" defaultOpen="true">
    <div class="tab-container" align=center > 
		<h3>Reimbursement History - Travel Reimbursements ${KualiForm.document.travelDocumentIdentifier}</h3>
		<table class="datatable" summary="Reimbursement History - Travel Reimbursements" cellpadding="0">
           <tbody>
           <tr>
             <th class="bord-l-b">
			   <kul:htmlAttributeLabel attributeEntry="${docHeaderAttributes.documentNumber}" />
             </th>
             <th class="bord-l-b">
			   <kul:htmlAttributeLabel attributeEntry="${documentAttributes.tripBegin}" />
             </th>
             <th class="bord-l-b">Status</th>
             <th class="bord-l-b">Hold</th>
             <th class="bord-l-b">Request Cancel</th>
             <th class="bord-l-b">Amount</th>
           </tr>
           <c:forEach var="reimbursement" items="${KualiForm.history}">
           <tr>
             <td><c:out value="${reimbursement.documentNumber}"/></td>
             <td><c:out value="${reimbursement.date}"/></td>
             <td><c:out value="${reimbursement.status}"/></td>
             <td><c:out value="${reimbursement.onHold}"/></td>
             <td><c:out value="${reimbursement.cancel}"/></td>
             <td><c:out value="${reimbursement.amount}"/></td>
		   </tr>             
           </c:forEach>
           </tbody>
        </table>
    </div>
</kul:tab>
