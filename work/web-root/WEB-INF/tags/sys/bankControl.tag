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

<script language="JavaScript" type="text/javascript" src="scripts/fp/objectInfo.js"></script>
<script language="JavaScript" type="text/javascript" src="dwr/interface/BankService.js"></script>

<%@ attribute name="property" required="true" description="name of the property that holds the bank code value in the form" %>
<%@ attribute name="objectProperty" required="true" description="name of the property that holds the bank object in the form" %>
<%@ attribute name="depositOnly" required="false" description="boolean indicating whether the bank lookup call should request only deposit banks" %>
<%@ attribute name="disbursementOnly" required="false" description="boolean indicating whether the bank lookup call should request only disbursement banks" %>
<%@ attribute name="readOnly" required="false" description="boolean indicating whether the document is readOnly. If not an additional check is made to verify the bank edit mode was exported." %>
<%@ attribute name="style" required="false" description="style class for the cell" %>

<c:if test="${KualiForm.editingMode[KFSConstants.BANK_ENTRY_VIEWABLE_EDITING_MODE]}">

  <c:if test="${empty KualiForm.documentActions[KFSConstants.KFS_ACTION_CAN_EDIT_BANK]}">
    <c:set var="readOnly" value="true" />
  </c:if>
  
  <c:if test="${empty style}">
    <c:set var="style" value="datacell-nowrap" />
  </c:if>

  <c:if test="${depositOnly}">
    <c:set var="lookupParameters" value="'Y':bankDepositIndicator" />
    <c:set var="readOnlyFields" value="bankDepositIndicator" />
  </c:if>

  <c:if test="${disbursementOnly}">
    <c:set var="lookupParameters" value="'Y':bankDisbursementIndicator" />
    <c:set var="readOnlyFields" value="bankDisbursementIndicator" />
  </c:if>

  <td class="${style}">
    <kul:htmlControlAttribute attributeEntry="${DataDictionary.Bank.attributes.bankCode}" property="${property}" readOnly="${readOnly}" onblur="loadBankInfo(document.forms['KualiForm'], '${property}', '${objectProperty}');" />
    <c:if test="${not readOnly}">
      &nbsp;
      <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Bank" fieldConversions="bankCode:${property},bankName:${objectProperty}.bankName" lookupParameters="${lookupParameters}" readOnlyFields="${readOnlyFields}" autoSearch="true"/>
    </c:if>
  
    <br/>
    <div id="${objectProperty}.div" class="fineprint">
       <bean:write name="KualiForm" property="${objectProperty}.bankName"/>&nbsp;
    </div>
  </td>

</c:if>
