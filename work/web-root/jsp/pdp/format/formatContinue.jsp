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
<c:set var="disbursementNumberRangeAttributes"
	value="${DataDictionary.DisbursementNumberRange.attributes}" />
<c:set var="formatResultAttributes"
	value="${DataDictionary.FormatResult.attributes}" />
<c:set var="customerProfileAttributes"
	value="${DataDictionary.CustomerProfile.attributes}" />
<c:set var="dummyAttributes"
	value="${DataDictionary.AttributeReferenceDummy.attributes}" />

<kul:page headerTitle="Format Disbursement Summary"
	transactionalDocument="false" showDocumentInfo="false" errorKey="foo"
	htmlFormAction="pdp/format" docTitle="Format Disbursement Summary">
	
	<table width="100%" border="0"><tr><td>	
	  <kul:errors keyMatch="${Constants.GLOBAL_ERRORS}" errorTitle="Errors Found On Page:"/>
	</td></tr></table>  
	</br>
	
	<pdp:formatSelectedPayments
		disbursementNumberRangeAttributes="${disbursementNumberRangeAttributes}"
		customerProfileAttributes="${customerProfileAttributes}"
		formatResultAttributes="${formatResultAttributes}" />
	<kul:panelFooter />
	<div id="globalbuttons" class="globalbuttons">
		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_continue.gif"
			styleClass="globalbuttons" property="methodToCall.continueFormat"
			title="begin format" alt="continue format" />
		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif"
			styleClass="globalbuttons" property="methodToCall.cancel"
			title="cancel" alt="cancel" />
	</div>

</kul:page>
