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
	<a name="${TEMConstants.DISTRIBUTION_ANCHOR }" id="${TEMConstants.DISTRIBUTION_ANCHOR }"></a>
	<kul:tab tabTitle="Assign Accounts" defaultOpen="${fullEntryMode }" tabErrorKey="${TemKeyConstants.TRVL_ACCOUNT_DIST}">
	<c:choose>
		<c:when test="${KualiForm.hasSelectedDistributionRemainingAmount}">
			<sys-java:accountingLines>
		        <sys-java:accountingLineGroup newLinePropertyName="accountDistributionnewSourceLine" collectionPropertyName="accountDistributionsourceAccountingLines" collectionItemPropertyName="accountDistributionsourceAccountingLines" attributeGroupName="accountDistribution" />
		    </sys-java:accountingLines>
		    <div class="tab-container" align="left">
		    	<c:choose>
		    		<c:when test="${fn:length(KualiForm.accountDistributionsourceAccountingLines) > 0}" >
		    			<html:image
							property="methodToCall.distributeAccountingLines"
							src="${ConfigProperties.externalizable.images.url}tinybutton-assignaccounts.gif"
							alt="Assign Accounts" title="Assign Accounts"
							styleClass="tinybutton" />
		    		</c:when>
		    		<c:otherwise>
		    			<img src="${ConfigProperties.externalizable.images.url}tinybutton-assignaccounts1.gif" alt="Assign Accounts Disabled" title="Assign Accounts Disabled" />
		    		</c:otherwise>
		    	</c:choose>
			    
				<hr />
			</div>
		</c:when>
		<c:otherwise>
			<div class="tab-container" align="left">
				${TemConstants.ASSIGN_ACCOUNTS_DISABLED_MESSAGE }
			</div>
		</c:otherwise>
	</c:choose>
	
	</kul:tab>
<script type="text/javascript">
	function updatePercent(amountField) {
		var currentAmount = parseFloat(document.getElementById("selectedDistributionAmount").value);
		var thisFieldAmount = parseFloat(document.getElementById(amountField).value);
		if (isNaN(thisFieldAmount)) {
			thisFieldAmount = 0.00;
			document.getElementById(amountField).value = "0.00";
		}
		var strName = amountField.split(".amount");
		var percentField = document.getElementById(strName[0] + ".accountLinePercent");
		var percent = thisFieldAmount * 100 / currentAmount;
		percent = roundNumber(percent, 5);
		percentField.value = percent;
		//if this is called from the new line, do not update itself again base on the collections
		if (amountField.indexOf("[") != -1) {
			var success = updateNewAssignAccountsPercentAmount(currentAmount);
			if (!success) {
				var newAmount = parseFloat(document.getElementById("accountDistributionnewSourceLine.amount").value);
				var newPercent = parseFloat(document.getElementById("accountDistributionnewSourceLine.accountLinePercent").value);
				document.getElementById(amountField).value = thisFieldAmount + newAmount;
				percentField.value = percent + newPercent;
				document.getElementById("accountDistributionnewSourceLine.amount").value = "0.00";
				document.getElementById("accountDistributionnewSourceLine.accountLinePercent").value = "0.00";
			}
		}
	}
	function updateAmount(percentField) {
		var currentAmount = parseFloat(document.getElementById("selectedDistributionAmount").value);
		var thisFieldPercent = parseFloat(document.getElementById(percentField).value);
		thisFieldPercent = roundNumber(thisFieldPercent, 5);
		if (isNaN(thisFieldPercent)) {
			thisFieldAmount = 0.00;
			document.getElementById(amountField).value = "0.00";
		}
		var strName = percentField.split(".accountLinePercent");
		var amountField = document.getElementById(strName[0] + ".amount");
		var amount = thisFieldPercent * currentAmount / 100;
		amount = roundNumber(amount, 2);
		amountField.value = amount;
		//if this is called from the new line, do not update itself again base on the collections
		if (percentField.indexOf("[") != -1) {
			var success = updateNewAssignAccountsPercentAmount(currentAmount);
			if (!success) {
				var newAmount = parseFloat(document.getElementById("accountDistributionnewSourceLine.amount").value);
				var newPercent = parseFloat(document.getElementById("accountDistributionnewSourceLine.accountLinePercent").value);
				document.getElementById(percentField).value = thisFieldPercent	+ newPercent;
				amountField.value = amount + newAmount;
				document.getElementById("accountDistributionnewSourceLine.amount").value = "0.00";
				document.getElementById("accountDistributionnewSourceLine.accountLinePercent").value = "0.00";
			}
		}
	}
	function updateNewAssignAccountsPercentAmount(currentAmount) {
		var totalAmount = 0.00;
		var totalPercent = 0.00;
		var counter = 0;
		var fieldPercent = document.getElementById("accountDistributionsourceAccountingLines["	+ counter + "].accountLinePercent");
		var fieldAmount = document.getElementById("accountDistributionsourceAccountingLines["	+ counter + "].amount");

		while (fieldAmount != null) {
			if (isNaN(parseFloat(fieldAmount.value))) {
				fieldAmount.value = "0.00";
			} else {
				totalAmount += parseFloat(fieldAmount.value);
			}
			if (isNaN(parseFloat(fieldPercent.value))) {
				fieldAmount.value = "0.00";
			} else {
				totalPercent += parseFloat(fieldPercent.value);
			}
			counter++;
			fieldPercent = document.getElementById("accountDistributionsourceAccountingLines["	+ counter + "].accountLinePercent");
			fieldAmount = document.getElementById("accountDistributionsourceAccountingLines["	+ counter + "].amount");
		}
		//if there is any change from the existing assign accounting lines
		if (totalAmount != "0.00") {
			document.getElementById("accountDistributionnewSourceLine.amount").value = roundNumber(currentAmount - totalAmount, 2);
			document.getElementById("accountDistributionnewSourceLine.accountLinePercent").value = roundNumber(	100 - totalPercent, 5);
		}
		return currentAmount - totalAmount >= 0;
	}
</script>
