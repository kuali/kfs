<%--
 Copyright 2007-2008 The Kuali Foundation
 
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

<c:set var="disbursementNumberRangeAttributes"
	value="${DataDictionary.DisbursementNumberRange.attributes}" />
<c:set var="bankAttributes" value="${DataDictionary.Bank.attributes}" />
<c:set var="customerProfileAttributes"
	value="${DataDictionary.CustomerProfile.attributes}" />
<c:set var="paymentGroupAttributes"
	value="${DataDictionary.PaymentGroup.attributes}" />
<c:set var="dummyAttributes"
	value="${DataDictionary.AttributeReferenceDummy.attributes}" />

<kul:page headerTitle="Format Disbursements"
	transactionalDocument="false" showDocumentInfo="false" errorKey="foo"
	htmlFormAction="pdp/format" docTitle="Format Disbursements">
	<c:if test="${empty ErrorPropertyList}">
	
    <table width="100%" border="0"><tr><td>	
	  <kul:errors keyMatch="${Constants.GLOBAL_ERRORS}" errorTitle="Errors Found On Page:"/>
	</td></tr></table>  
	</br>

	<pdp:formatDisbursementRanges
		disbursementNumberRangeAttributes="${disbursementNumberRangeAttributes}"
		bankAttributes="${bankAttributes}" />
	<pdp:formatOptions paymentGroupAttributes="${paymentGroupAttributes}" />
	<pdp:formatCustomers
		customerProfileAttributes="${customerProfileAttributes}"
		dummyAttributes="${dummyAttributes}" />
	<kul:panelFooter />
	<div id="globalbuttons" class="globalbuttons">
		<html:image
			src="${ConfigProperties.externalizable.images.url}buttonsmall_beginformat.gif"
			styleClass="globalbuttons" property="methodToCall.prepare"
			title="begin format" alt="begin format" />
		<html:image
			src="${ConfigProperties.externalizable.images.url}buttonsmall_reset.gif"
			styleClass="globalbuttons" property="methodToCall.start"
			title="reset" alt="reset" />
		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_clear.gif"
			styleClass="globalbuttons" property="methodToCall.clear"
			title="clear" alt="clear" />			
	</div>
	</c:if>

</kul:page>

