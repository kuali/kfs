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
