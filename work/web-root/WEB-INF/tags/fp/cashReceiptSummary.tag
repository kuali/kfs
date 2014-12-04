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

<%@ attribute name="receipt" required="true" type="org.kuali.kfs.fp.document.CashReceiptDocument"%>
<%@ attribute name="receiptIndex" required="true" %>

<c:set var="checkBaseAttributes" value="${DataDictionary.CheckBase.attributes}" />

<c:if test="${receipt.checkCount > 0}">
    <tr>
        <td class="infoline" rowspan="${receipt.checkCount + 1}" colspan="2">&nbsp;</td>
        <td class="infoline">
            <kul:htmlAttributeLabel attributeEntry="${checkBaseAttributes.checkNumber}" />
        </td>
        <td class="infoline">
            <kul:htmlAttributeLabel attributeEntry="${checkBaseAttributes.checkDate}" />
        </td>
        <td class="infoline">
            <kul:htmlAttributeLabel attributeEntry="${checkBaseAttributes.description}" />
        </td>
        <td class="infoline">
            <kul:htmlAttributeLabel attributeEntry="${checkBaseAttributes.amount}" />
        </td>
        <td class="infoline" rowspan="${receipt.checkCount + 1}">&nbsp;</td>
    </tr>

    <logic:iterate name="receipt" property="checks" id="check" indexId="checkIndex">
        <tr>
            <td>${check.checkNumber}</td>
            <td>${check.checkDate}</td>
            <td>${check.description}</td>
            <td>${check.amount}</td>
        </tr>
    </logic:iterate>
</c:if>
