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

<%-- helpful messages --%>
<script type="text/javascript">
  function paymentMethodMessages(selectedMethod) {
    if (selectedMethod == 'W') {
		alert('<bean:message key="message.dv.feewarning"/>');
<%-- KULFDBCK-892
		alert('<bean:message key="message.dv.wirescreen"/>');
--%>
    }
		  
    if (selectedMethod == 'F') {
		alert('<bean:message key="message.dv.foreigndraft"/>');
    }
   }
		
   function exceptionMessage(exceptionIndicator) {
	 if (exceptionIndicator.checked == true) {
		alert('<bean:message key="message.dv.exception"/>');
     } 
   }
		
   function specialHandlingMessage(specialHandlingIndicator) {
     if (specialHandlingIndicator.checked == true) {
		alert('<bean:message key="message.dv.specialhandling"/>');
     } else {
		<c:if test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && KualiForm.editingMode['specialHandlingChangingEntry']}">
			clearSpecialHandlingTab();
		</c:if>
	 }
   }
   
   function clearSpecialHandlingTab() {
	var prefix = "document.dvPayeeDetail.";
	var ctrl;
	
	ctrl = kualiElements[prefix+"disbVchrSpecialHandlingPersonName"];
	ctrl.value = "";
	
	ctrl = kualiElements[prefix + "disbVchrSpecialHandlingCityName"]
	ctrl.value = "";
	
	ctrl = kualiElements[prefix + "disbVchrSpecialHandlingLine1Addr"];
	ctrl.value = "";
	
	ctrl = kualiElements[prefix + "disbVchrSpecialHandlingStateCode"];
	ctrl.value = "";
	
	ctrl = kualiElements[prefix + "disbVchrSpecialHandlingLine2Addr"];
	ctrl.value = "";
	
	ctrl = kualiElements[prefix + "disbVchrSpecialHandlingZipCode"];
	ctrl.value = "";
	
	ctrl = kualiElements[prefix + "disbVchrSpecialHandlingCountryCode"];
	ctrl.value = "";
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
		
