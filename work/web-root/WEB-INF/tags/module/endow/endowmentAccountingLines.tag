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

<%@ attribute name="editingMode" required="false" description="used to decide if items may be edited" type="java.util.Map"%>
<%@ attribute name="isSource" required="true" %>
<script language="JavaScript" type="text/javascript" src="scripts/module/endow/endowmentAccountingLine.js"></script>

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<c:if test="${isSource}" >
  <c:set var="lineAttributes" value="${DataDictionary.SourceEndowmentAccountingLine.attributes}" />
  <c:set var="newAccountingLine" value="newSourceAccountingLine" />
  <c:set var="methodToCallAdd" value="methodToCall.insertSourceAccountingLine" />
  <c:set var="methodToCallDelete" value="methodToCall.deleteSourceAccountingLine" />
  <c:set var="methodToCallBalanceInquiry" value="methodToCall.performBalanceInquiryForSourceAccountingLine" />
  <c:set var="transLines" value="document.sourceAccountingLines"/> 
  <c:set var="uploadAccountingLines" value="uploadSourceLines"/> 
  <c:set var="importFile" value="sourceFile"/> 
  <c:set var="errorKeyMatch" value="${EndowConstants.SOURCE_ACCOUNTING_LINES_ERRORS}" />
</c:if>
<c:if test="${not isSource}">
  <c:set var="lineAttributes" value="${DataDictionary.TargetEndowmentAccountingLine.attributes}" />
  <c:set var="newAccountingLine" value="newTargetAccountingLine" />
  <c:set var="methodToCallAdd" value="methodToCall.insertTargetAccountingLine" />
  <c:set var="methodToCallDelete" value="methodToCall.deleteTargetAccountingLine" />
  <c:set var="methodToCallBalanceInquiry" value="methodToCall.performBalanceInquiryForTargetAccountingLine" />
  <c:set var="transLines" value="document.targetAccountingLines"/>
  <c:set var="uploadAccountingLines" value="uploadTargetLines"/> 
  <c:set var="importFile" value="targetFile"/> 
  <c:set var="errorKeyMatch" value="${EndowConstants.TARGET_ACCOUNTING_LINES_ERRORS}" />
</c:if>

<table cellpadding="0" cellspacing="0" class="datatable" summary="Accounting Lines section">
	    <tr>
	            <td colspan="1" class="tab-subhead" style="border-right: none;" align="left">
	            <c:if test="${isSource}">From</c:if>
	            <c:if test="${not isSource}">To</c:if>
	            </td>    
	            <td colspan="8" class="tab-subhead" style="border-right: none;border-left: none;" >
	            &nbsp;
	            </td>
	            <td colspan="2" class="tab-subhead" align="right" nowrap="nowrap" style="border-left: none;">
	            <c:if test="${isSource and (not readOnly)}">
                
					<SCRIPT type="text/javascript">
                		<!--
                  		function hideSourceEndowImport() {
                      		document.getElementById("showLinkSourceEndow").style.display="inline";
                      		document.getElementById("uploadDivSourceEndow").style.display="none";
                  		}
                  		function showSourceEndowImport() {
                      		document.getElementById("showLinkSourceEndow").style.display="none";
                      		document.getElementById("uploadDivSourceEndow").style.display="inline";
                  		}
                  		document.write(
                    		'<a id="showLinkSourceEndow" href="#" onclick="showSourceEndowImport();return false;">' +
                      		'<img src="${ConfigProperties.externalizable.images.url}tinybutton-importlines.gif" title="import accounting lines from file" alt="import accounting lines from file"' +
                      		'     width=72 height=15 border=0 align="right" class="det-button">' +
                    		'<\/a>' +
                    		'<div id="uploadDivSourceEndow" style="display:none;" >' +
                      		'<html:file size="30" property="${importFile}" />' +
                      		'<html:image property="methodToCall.${uploadAccountingLines}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
                                    styleClass="tinybutton" alt="add imported accounting lines" title="add imported accounting lines" />' +
                      		'<html:image property="methodToCall.cancelImport" src="${ConfigProperties.externalizable.images.url}tinybutton-cancelimport.gif"
                                    styleClass="tinybutton" alt="cancel import" title="cancel import" onclick="hideSourceEndowImport();return false;" />' +
                    		'<\/div>');
                		//-->
            		</SCRIPT>
					<NOSCRIPT>
						Import lines
						<html:file size="30" property="${importFile}" style="font:10px;height:16px;" />
						<html:image property="methodToCall.${uploadAccountingLines}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add imported items" title="add imported items" />
					</NOSCRIPT>

				</c:if>
				<c:if test="${not isSource and (not readOnly)}">
					<SCRIPT type="text/javascript">
                		<!--
                  		function hideTargetEndowImport() {
                      		document.getElementById("showLinkTargetEndow").style.display="inline";
                      		document.getElementById("uploadDivTargetEndow").style.display="none";
                  		}
                  		function showTargetEndowImport() {
                      		document.getElementById("showLinkTargetEndow").style.display="none";
                      		document.getElementById("uploadDivTargetEndow").style.display="inline";
                  		}
                  		document.write(
                    		'<a id="showLinkTargetEndow" href="#" onclick="showTargetEndowImport();return false;">' +
                      		'<img src="${ConfigProperties.externalizable.images.url}tinybutton-importlines.gif" title="import accounting lines from file" alt="import accounting lines from file"' +
                      		'     width=72 height=15 border=0 align="right" class="det-button">' +
                    		'<\/a>' +
                    		'<div id="uploadDivTargetEndow" style="display:none;" >' +
                      		'<html:file size="30" property="${importFile}" />' +
                      		'<html:image property="methodToCall.${uploadAccountingLines}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
                                    styleClass="tinybutton" alt="add imported accounting lines" title="add imported accounting lines" />' +
                      		'<html:image property="methodToCall.cancelImport" src="${ConfigProperties.externalizable.images.url}tinybutton-cancelimport.gif"
                                    styleClass="tinybutton" alt="cancel import" title="cancel import" onclick="hideTargetEndowImport();return false;" />' +
                    		'<\/div>');
                		//-->
            		</SCRIPT>
					<NOSCRIPT>
						Import lines
						<html:file size="30" property="${importFile}" style="font:10px;height:16px;" />
						<html:image property="methodToCall.${uploadAccountingLines}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add imported items" title="add imported items" />
					</NOSCRIPT>
				
				</c:if>
				</td>
	    </tr>
	    
	   <kul:displayIfErrors keyMatch="${errorKeyMatch}">
	    <tr>
            <td colspan="11"><kul:errors keyMatch="${errorKeyMatch}" errorTitle="Errors found in this Section:" /></td>
        </tr>
       </kul:displayIfErrors>
	    
		<tr>

            <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" colspan="2"/>
    
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.chartOfAccountsCode}"/>
	        <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.accountNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.subAccountNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.financialObjectCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.financialSubObjectCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.projectCode}"/>
           	<kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.organizationReferenceId}"/>
           	<kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.amount}"/>
            <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
		</tr>
        <c:if test="${not readOnly}">
            <tr>
               <kul:htmlAttributeHeaderCell literalLabel="add:" scope="row" colspan="2"/> 
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.chartOfAccountsCode}" 
                		property="${newAccountingLine}.chartOfAccountsCode"
                		onblur="loadFinChartOfAccountDescription('${newAccountingLine}.chartOfAccountsCode', '${newAccountingLine}.chart.finChartOfAccountDescription.div');"/>&nbsp;
	                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Chart"
					                fieldConversions="chartOfAccountsCode:${newAccountingLine}.chartOfAccountsCode"
					                lookupParameters="${newAccountingLine}.chartOfAccountsCode:chartOfAccountsCode" />
				    <br/>
	
					<div id="${newAccountingLine}.chart.finChartOfAccountDescription.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.chartOfAccountsCode}" property="${newAccountingLine}.chartOfAccountsCode" extraReadOnlyProperty="${newAccountingLine}.chart.finChartOfAccountDescription" readOnly="true" />
            		</div>	
			                
				</td>
                <td class="infoline">
                <kul:htmlControlAttribute attributeEntry="${lineAttributes.accountNumber}" property="${newAccountingLine}.accountNumber"
	                	onblur="loadAccountName('${newAccountingLine}.accountNumber', '${newAccountingLine}.chartOfAccountsCode', 'document.${newAccountingLine}.accountNumber.name.div');"/>&nbsp;
	                	<kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Account"
					                fieldConversions="accountNumber:${newAccountingLine}.accountNumber,chartOfAccountsCode:${newAccountingLine}.chartOfAccountsCode"
					                lookupParameters="${newAccountingLine}.accountNumber:accountNumber,${newAccountingLine}.chartOfAccountsCode:chartOfAccountsCode"/>
				    <br/>

					<div id="document.${newAccountingLine}.accountNumber.name.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.accountNumber}" property="${newAccountingLine}.accountNumber" extraReadOnlyProperty="${newAccountingLine}.account.accountName" readOnly="true" />
            		</div>

            	</td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.subAccountNumber}" 
                		property="${newAccountingLine}.subAccountNumber" 
                		onblur="loadSubAccountName('${newAccountingLine}.subAccountNumber', '${newAccountingLine}.accountNumber', '${newAccountingLine}.chartOfAccountsCode', 'document.${newAccountingLine}.subAccount.name.div');"/>
	                	<kul:lookup boClassName="org.kuali.kfs.coa.businessobject.SubAccount"
					                fieldConversions="subAccountNumber:${newAccountingLine}.subAccountNumber,accountNumber:${newAccountingLine}.accountNumber,chartOfAccountsCode:${newAccountingLine}.chartOfAccountsCode"
					                lookupParameters="${newAccountingLine}.subAccountNumber:subAccountNumber,${newAccountingLine}.accountNumber:accountNumber,${newAccountingLine}.chartOfAccountsCode:chartOfAccountsCode" />                		
 					<br/>

					<div id="document.${newAccountingLine}.subAccount.name.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.subAccountNumber}" property="${newAccountingLine}.subAccountNumber"  extraReadOnlyProperty="${newAccountingLine}.subAccount.subAccountName" readOnly="true" />
            		</div>  
          		
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.financialObjectCode}" 
                		property="${newAccountingLine}.financialObjectCode"
                    	onblur="loadObjectCodeName('${newAccountingLine}.financialObjectCode', '${newAccountingLine}.chartOfAccountsCode', 'document.${newAccountingLine}.objectCode.financialObjectCodeName.div');"/>
                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ObjectCode"
					            fieldConversions="financialObjectCode:${newAccountingLine}.financialObjectCode,chartOfAccountsCode:${newAccountingLine}.chartOfAccountsCode"
					            lookupParameters="${newAccountingLine}.financialObjectCode:financialObjectCode,${newAccountingLine}.chartOfAccountsCode:chartOfAccountsCode" />
					<br/>					

					<div id="document.${newAccountingLine}.objectCode.financialObjectCodeName.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.financialObjectCode}" property="${newAccountingLine}.financialObjectCode" extraReadOnlyProperty="${newAccountingLine}.objectCode.financialObjectCodeName" readOnly="true" />
            		</div>	
			                
                </td>
                    	
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.financialSubObjectCode}" 
                	property="${newAccountingLine}.financialSubObjectCode" 
                	onblur="loadSubObjectCodeName('${newAccountingLine}.financialSubObjectCode', '${newAccountingLine}.chartOfAccountsCode', '${newAccountingLine}.accountNumber', '${newAccountingLine}.financialObjectCode', 'document.${newAccountingLine}.subObjectCode.financialSubObjectCodeName.div');"/>
                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.SubObjectCode"
					            fieldConversions="financialSubObjectCode:${newAccountingLine}.financialSubObjectCode,chartOfAccountsCode:${newAccountingLine}.chartOfAccountsCode,accountNumber:${newAccountingLine}.accountNumber,financialObjectCode:${newAccountingLine}.financialObjectCode" 
					            lookupParameters="${newAccountingLine}.financialSubObjectCode:financialSubObjectCode,${newAccountingLine}.chartOfAccountsCode:chartOfAccountsCode,${newAccountingLine}.accountNumber:accountNumber,${newAccountingLine}.financialObjectCode:financialObjectCode" />
					<br/>

					<div id="document.${newAccountingLine}.subObjectCode.financialSubObjectCodeName.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.financialSubObjectCode}" property="${newAccountingLine}.financialSubObjectCode" extraReadOnlyProperty="${newAccountingLine}.subObjectCode.financialSubObjectCodeName" readOnly="true" />
            		</div>

                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.projectCode}" 
                		property="${newAccountingLine}.projectCode"
                		onblur="loadProjectCodeName('${newAccountingLine}.projectCode', 'document.${newAccountingLine}.project.name.div');"/>
                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ProjectCode"
					            fieldConversions="code:${newAccountingLine}.projectCode"
					            lookupParameters="${newAccountingLine}.projectCode:code" />
					<br/>
					
					<div id="document.${newAccountingLine}.project.name.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.projectCode}" property="${newAccountingLine}.projectCode" extraReadOnlyProperty="${newAccountingLine}.project.name" readOnly="true" />
            		</div>
                </td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.organizationReferenceId}" property="${newAccountingLine}.organizationReferenceId"/></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.amount}" property="${newAccountingLine}.amount" styleClass="right"/></td>
                
	                <td class="infoline">
	                  	<c:if test="${isSource}">
		                	<div align="center">
		                		<html:image property="${methodToCallAdd}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add Accounting Line" title="add" styleClass="tinybutton"/>
		                	</div>
	                	</c:if>
	                	<c:if test="${not isSource}">
		                	<div align="center">
		                		<html:image property="${methodToCallAdd}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add Accounting Line" title="add" styleClass="tinybutton"/>
		                	</div>
	                	</c:if>
	                </td>
            </tr>
        </c:if>
      
        <logic:iterate id="item" name="KualiForm" property="${transLines}" indexId="ctr">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="${ctr+1}:" scope="row" colspan="2"></kul:htmlAttributeHeaderCell>

                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.chartOfAccountsCode}" 
                		property="${transLines}[${ctr}].chartOfAccountsCode"
                		onblur="loadFinChartOfAccountDescription('${transLines}[${ctr}].chartOfAccountsCode', '${transLines}[${ctr}].chart.finChartOfAccountDescription.div');"
	            		readOnly="${readOnly}"/>&nbsp;
	                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Chart"
					                fieldConversions="chartOfAccountsCode:${transLines}[${ctr}].chartOfAccountsCode"
					                lookupParameters="${transLines}[${ctr}].chartOfAccountsCode:chartOfAccountsCode" />
				    <br/>

					<div id="${transLines}[${ctr}].chart.finChartOfAccountDescription.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.chartOfAccountsCode}" property="${transLines}[${ctr}].chartOfAccountsCode" extraReadOnlyProperty="${transLines}[${ctr}].chart.finChartOfAccountDescription" readOnly="true" />
            		</div>	
		                
				</td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.accountNumber}" property="${transLines}[${ctr}].accountNumber"
	                	onblur="loadAccountName('${transLines}[${ctr}].accountNumber', '${transLines}[${ctr}].chartOfAccountsCode', 'document.${transLines}[${ctr}].accountNumber.name.div');"
	                	readOnly="${readOnly}"/>&nbsp;
	                <c:if test="${not readOnly}">
	                	<kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Account"
					                fieldConversions="accountNumber:${transLines}[${ctr}].accountNumber,chartOfAccountsCode:${transLines}[${ctr}].chartOfAccountsCode"
					                lookupParameters="${transLines}[${ctr}].accountNumber:accountNumber,${transLines}[${ctr}].chartOfAccountsCode:chartOfAccountsCode"/>
					</c:if>				    
					<div id="document.${transLines}[${ctr}].accountNumber.name.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.accountNumber}" property="${transLines}[${ctr}].account" extraReadOnlyProperty="${transLines}[${ctr}].account.accountName" readOnly="true" />
            		</div>
                
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.subAccountNumber}" 
                		property="${transLines}[${ctr}].subAccountNumber"
                		onblur="loadSubAccountName('${transLines}[${ctr}].subAccountNumber', '${transLines}[${ctr}].accountNumber', '${transLines}[${ctr}].chartOfAccountsCode', 'document.${transLines}[${ctr}].subAccount.name.div');"
                		readOnly="${readOnly}" />
						<kul:lookup boClassName="org.kuali.kfs.coa.businessobject.SubAccount"
					                fieldConversions="subAccountNumber:${transLines}[${ctr}].subAccountNumber,accountNumber:${transLines}[${ctr}].accountNumber,chartOfAccountsCode:${transLines}[${ctr}].chartOfAccountsCode"
					                lookupParameters="${transLines}[${ctr}].subAccountNumber:subAccountNumber,${transLines}[${ctr}].accountNumber:accountNumber,${transLines}[${ctr}].chartOfAccountsCode:chartOfAccountsCode" />                		
 					<br/>

					<div id="document.${transLines}[${ctr}].subAccount.name.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.subAccountNumber}" property="${transLines}[${ctr}].subAccountNumber" extraReadOnlyProperty="${transLines}[${ctr}].subAccount.subAccountName" readOnly="true" />
            		</div>              		
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.financialObjectCode}" 
                    	property="${transLines}[${ctr}].financialObjectCode"
                    	onblur="loadObjectCodeName('${transLines}[${ctr}].financialObjectCode', '${transLines}[${ctr}].chartOfAccountsCode','document.${transLines}[${ctr}].objectCode.financialObjectCodeName.div');"
	            		readOnly="${readOnly}"/>
                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ObjectCode"
					            fieldConversions="financialObjectCode:${transLines}[${ctr}].financialObjectCode,chartOfAccountsCode:${transLines}[${ctr}].chartOfAccountsCode"
					            lookupParameters="${transLines}[${ctr}].financialObjectCode:financialObjectCode,${transLines}[${ctr}].chartOfAccountsCode:chartOfAccountsCode" />	            		
					<br/>

					<div id="document.${transLines}[${ctr}].objectCode.financialObjectCodeName.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.financialObjectCode}" property="${transLines}[${ctr}].financialObjectCode" extraReadOnlyProperty="${transLines}[${ctr}].objectCode.financialObjectCodeName" readOnly="true" />
            		</div>	         		
	            </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.financialSubObjectCode}"
                		property="${transLines}[${ctr}].financialSubObjectCode" 
                		onblur="loadSubObjectCodeName('${transLines}[${ctr}].financialSubObjectCode', '${transLines}[${ctr}].chartOfAccountsCode', '${transLines}[${ctr}].accountNumber', '${transLines}[${ctr}].financialObjectCode', 'document.${transLines}[${ctr}].subObjectCode.financialSubObjectCodeName.div');"
                		readOnly="${readOnly}"/>
                	<kul:lookup boClassName="org.kuali.kfs.coa.businessobject.SubObjectCode"
					            fieldConversions="financialSubObjectCode:${transLines}[${ctr}].financialSubObjectCode,chartOfAccountsCode:${transLines}[${ctr}].chartOfAccountsCode,accountNumber:${transLines}[${ctr}].accountNumber,financialObjectCode:${transLines}[${ctr}].financialObjectCode" 
					            lookupParameters="${transLines}[${ctr}].financialSubObjectCode:financialSubObjectCode,${transLines}[${ctr}].chartOfAccountsCode:chartOfAccountsCode,${transLines}[${ctr}].accountNumber:accountNumber,${transLines}[${ctr}].financialObjectCode:financialObjectCode" />
					<br/>
					<div id="document.${transLines}[${ctr}].subObjectCode.financialSubObjectCodeName.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.financialSubObjectCode}" property="${transLines}[${ctr}].financialSubObjectCode" extraReadOnlyProperty="${transLines}[${ctr}].subObjectCode.financialSubObjectCodeName" readOnly="true" />
            		</div>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.projectCode}" 
                		property="${transLines}[${ctr}].projectCode" 
                		onblur="loadProjectCodeName('${transLines}[${ctr}].projectCode', 'document.${transLines}[${ctr}].project.name.div');"
                		readOnly="${readOnly}"/>
					<kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ProjectCode"
					                fieldConversions="code:${transLines}[${ctr}].projectCode"
					                lookupParameters="${transLines}[${ctr}].projectCode:code" />
					<br/>
					<div id="document.${transLines}[${ctr}].project.name.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.projectCode}" property="${transLines}[${ctr}].projectCode"  extraReadOnlyProperty="${transLines}[${ctr}].project.name" readOnly="true" />
            		</div>              		
                </td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.organizationReferenceId}" property="${transLines}[${ctr}].organizationReferenceId" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.amount}" property="${transLines}[${ctr}].amount" styleClass="right" readOnly="${readOnly}"/></td>
                
                <td class="datacell">
                <div align="center">
                <c:if test="${not readOnly}" >
                	<html:image property="${methodToCallBalanceInquiry}.line${ctr}" src="${ConfigProperties.externalizable.images.url}tinybutton-balinquiry.gif" title="Balance Inquiry for Line ${ctr+1}" alt="Balance Inquiry for Line ${ctr+1}" styleClass="tinybutton"/>
                	<html:image property="${methodToCallDelete}.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="Delete Accounting Line ${ctr+1}" alt="Delete Accounting Line  ${ctr+1}" styleClass="tinybutton"/>
                </c:if>
                </div>
                </td>
            </tr>
        </logic:iterate>
        <tr>
				<td class="total-line" colspan="10">
					&nbsp;
				</td>
			
				<td class="total-line">
					<strong>Total Amount:
						${KualiForm.document.totalAccountingLinesAmount}</strong>				
				</td>
		</tr>
</table>       