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

<%-- helpful messages --%>
<script type="text/javascript">
  function paymentMethodMessages(selectedMethod) {
    if (selectedMethod == 'W') {
		alert('<bean:message key="message.payment.feewarning"/>');
    }
		  
    if (selectedMethod == 'F') {
		alert('<bean:message key="message.payment.foreigndraft"/>');
    }
   }
		
   function exceptionMessage(exceptionIndicator) {
	 if (exceptionIndicator.checked == true) {
		alert('<bean:message key="message.payment.exception"/>');
     } 
   }
		
   function specialHandlingMessage(specialHandlingIndicator) {
     if (specialHandlingIndicator.checked == true) {
		alert('<bean:message key="message.payment.specialhandling"/>');
     } else {
		<c:if test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && KualiForm.editingMode['specialHandlingChangingEntry']}">
			clearSpecialHandlingTab();
		</c:if>
	 }
   }
		
   function documentationMessage(selectedDocumentationLocation) {
     if (selectedDocumentationLocation == 'N') {
     	// Reference error message because this error can occur via multiple paths and
     	// it didn't make sense to duplicate the error text under multiple names in ApplicationResources.properties
     	// simply for the sake of naming consistency.
		alert('<bean:message key="error.document.noDocumentationNote"/>');
     } 
     if (selectedDocumentationLocation == 'O') {
		alert('<bean:message key="message.document.initiatingOrgDocumentation"/>');
     }
    }
		
   function paymentReasonMessages(selectedPaymentReason) {
	/* commenting out as part of the fix for KULRNE-5891
	Leaving this here as it will be used in future release for dynamic pop-up message
	  if (selectedPaymentReason == 'N') {
		 alert('<bean:message key="message.dv.travelnonemployee"/>');
	  } 
	  if (selectedPaymentReason == 'P') {
		 alert('<bean:message key="message.dv.travelprepaid"/>');
	  } */
    }
</script>
		
