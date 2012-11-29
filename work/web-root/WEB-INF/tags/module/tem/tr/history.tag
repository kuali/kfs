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
