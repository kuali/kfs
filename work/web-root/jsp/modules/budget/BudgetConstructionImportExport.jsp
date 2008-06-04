<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
<c:set var="budgetAttributes" value="${DataDictionary.BudgetConstructionRequestImport.attributes}" />

<kul:page showDocumentInfo="false"
    htmlFormAction="${KualiForm.htmlFormAction}" renderMultipart="true"
    docTitle="${KualiForm.title}"
    transactionalDocument="false">
 <script type="text/javascript">

        var excludeSubmitRestriction = true;

    </script>     
      
    <!-- kul:tab tabTitle="Select File to Import" defaultOpen="false" tabErrorKey="${EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS}"-->
    	<!-- div class="tab-container" align=center-->
		    <table align="center" cellpadding="0" cellspacing="0" class="datatable-100">
		    
		    <html:hidden name="KualiForm" property="universityFiscalYear" />
		    <html:hidden name="KualiForm" property="reportMode" />
		    <html:hidden property="returnAnchor" />
            <html:hidden property="returnFormKey" />
            
            <c:if test="${KualiForm.reportMode != 'requestImport'}">  
	            <html:hidden property="chartOfAccountsCode" />
	            <html:hidden property="accountNumber" />
	            <html:hidden property="subAccountNumber" />
	            <html:hidden property="backLocation" />
	        </c:if>
		    	<c:if test="${KualiForm.reportMode == 'requestImport'}">  
					<tr>
			        	<th class="grid" align="right" colspan="1"><kul:htmlAttributeLabel attributeEntry="${budgetAttributes.fileName}" noColon="false" /></th>
			            <td class="grid"><html:file property="file" /></td>
			       	</tr>
		        	<tr>
			        	<th class="grid" align="right" colspan="1"><kul:htmlAttributeLabel attributeEntry="${budgetAttributes.fileType}" noColon="false" /></th>
			            <td class="grid"><kul:htmlControlAttribute attributeEntry="${budgetAttributes.fileType}" property="fileType" readOnly="false" /></td>
					</tr>
				</c:if>
				<tr>
		        	<th class="grid" align="right" colspan="1"><kul:htmlAttributeLabel attributeEntry="${budgetAttributes.fieldDelimiter}" noColon="false" /></th>
		            <td class="grid"><kul:htmlControlAttribute attributeEntry="${budgetAttributes.fieldDelimiter}" property="fieldDelimiter" readOnly="false" /> &nbsp; <kul:htmlControlAttribute attributeEntry="${budgetAttributes.otherFieldDelimiter}" property="otherFieldDelimiter" readOnly="false" /></td>
				</tr>
				
				<tr>
		        	<th class="grid" align="right" colspan="1"><kul:htmlAttributeLabel attributeEntry="${budgetAttributes.textFieldDelimiter}" noColon="false" /></th>
		            <td class="grid"><kul:htmlControlAttribute attributeEntry="${budgetAttributes.textFieldDelimiter}" property="textFieldDelimiter" readOnly="false" /> &nbsp; <kul:htmlControlAttribute attributeEntry="${budgetAttributes.otherTextFieldDelimiter}" property="otherTextFieldDelimiter" readOnly="false" /></td>
				</tr>
				
				<tr>
					<td class="grid" class="infoline" colspan="2">
						<div align="center">
							<html:image property="methodToCall.submit" src="kr/static/images/buttonsmall_submit.gif" title="Import File" alt="Import File" styleClass="tinybutton" /> &nbsp;&nbsp;&nbsp;
							<html:image property="methodToCall.close" src="kr/static/images/buttonsmall_close.gif" title="Close Window" alt="Close Window" styleClass="tinybutton" />
						</div>
					</td>
				</tr>
			</table>
		<!-- /div-->
	<!-- /kul:tab-->
</kul:page>
