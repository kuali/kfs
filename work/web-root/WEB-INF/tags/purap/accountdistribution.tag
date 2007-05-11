<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>
<%@ taglib tagdir="/WEB-INF/tags/fin" prefix="fin"%>
<%@ taglib tagdir="/WEB-INF/tags/purap" prefix="purap"%>

<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>

<html:hidden property="hideDistributeAccounts" />

<br />
<div align="center">
<c:if test="${KualiForm.hideDistributeAccounts}">
	<html:image
	property="methodToCall.setupAccountDistribution"
	src="images/tinybutton-setaccdist.gif"
	alt="setup account distribution" title="setup account distribution"
	styleClass="tinybutton" />
</c:if>
<c:if test="${!KualiForm.hideDistributeAccounts}">
	<img src="images/tinybutton-setaccdist.gif"
	alt="setup account distribution" border="0"
	styleClass="tinybutton" />
</c:if>

<html:image
property="methodToCall.removeAccounts"
src="images/tinybutton-remaccitems.gif"
alt="remove accounts from all items"
title="remove accounts from all items" styleClass="tinybutton" />
</div>

	
<c:if test="${!KualiForm.hideDistributeAccounts}">

	<purap:puraccountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		sourceAccountingLinesOnly="true"
		optionalFields="accountLinePercent"
		accountingLineAttributes="${accountingLineAttributes}"
		accountPrefix="accountDistribution" hideTotalLine="true"
		hideFields="amount" accountingAddLineIndex="-2" />

	<div align="center">
		<html:image
		property="methodToCall.doAccountDistribution"
		src="images/tinybutton-disttoitems.gif"
		alt="do account distribution"
		title="do account distribution" styleClass="tinybutton" />
		<html:image
		property="methodToCall.cancelAccountDistribution"
		src="images/tinybutton-cancel.gif"
		alt="cancel account distribution"
		title="cancel account distribution" styleClass="tinybutton" />
	</div>
</c:if>
<br />
