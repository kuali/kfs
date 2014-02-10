<%--
 Copyright 2009 The Kuali Foundation
 
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

<%@ attribute name="invoiceAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="propertyName" required="true" description="The DataDictionary entry containing attributes for this row's fields."%>
<c:set var="invoiceGeneralDetails" value="${DataDictionary.InvoiceGeneralDetail.attributes}" />
<c:set var="accountDetails" value="${DataDictionary.Account.attributes}" />

<tr>
	<td><kul:htmlControlAttribute attributeEntry="${invoiceAttributes.documentNumber}" property="${propertyName}.documentNumber" readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${accountDetails.accountNumber}" property="${propertyName}.accountDetails[0].accountNumber" readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${invoiceAttributes.billingDate}" property="${propertyName}.billingDate"	readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${invoiceAttributes.age}" property="${propertyName}.age" readOnly="true" />	</td>
	<td><kul:htmlControlAttribute attributeEntry="${invoiceAttributes.sourceTotal}" property="${propertyName}.sourceTotal" readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${invoiceAttributes.openAmount}" property="${propertyName}.openAmount" readOnly="true" />
	<td><kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetails.dunningLetterTemplateSentDate}" property="${propertyName}.invoiceGeneralDetail.dunningLetterTemplateSentDate" readOnly="true" />
</tr>
