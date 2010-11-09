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

<script type='text/javascript' src="dwr/interface/KEMIDService.js"></script>
<script type='text/javascript' src="dwr/interface/EndowmentTransactionCodeService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/module/endow/endowmentTransactionCode.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/module/endow/kemIdForTransactionLines.js"></script>

<%@ attribute name="editingMode" required="false" description="used to decide if items may be edited" type="java.util.Map"%>
<%@ attribute name="isSource" required="true" %>
<%@ attribute name="hasUnits" required="true" %>
<%@ attribute name="isTransAmntReadOnly" required="true" %>
<%@ attribute name="showImport" required="false" %>

<c:set var="sourceGroupLabel" value="${KualiForm.sourceGroupLabelName}" />
<c:set var="targetGroupLabel" value="${KualiForm.targetGroupLabelName}" />
<c:set var="showIncomeTotalAmount" value="${KualiForm.showIncomeTotalAmount}" />
<c:set var="showPrincipalTotalAmount" value="${KualiForm.showPrincipalTotalAmount}" />
<c:set var="showIncomeTotalUnits" value="${KualiForm.showIncomeTotalUnits}" />
<c:set var="showPrincipalTotalUnits" value="${KualiForm.showPrincipalTotalUnits}" />
<c:set var="setFieldValueToPrincipal" value="${KualiForm.fieldValueToPrincipal}" />
<c:set var="showETranCode" value="${KualiForm.showETranCode}" />
<c:set var="showUnitAdjustmentAmount" value="${KualiForm.showUnitAdjustmentAmount}" />
<c:set var="showSourceImport" value="${KualiForm.showSourceImport}" />
<c:set var="showTargetImport" value="${KualiForm.showTargetImport}" />
<c:set var="showSourceAdd" value="${KualiForm.showSourceAdd and isSource}" />
<c:set var="showTargetAdd" value="${KualiForm.showTargetAdd and not isSource}" />
<c:set var="sourceIncomePrincipalIndicatorReadOnly" value="${KualiForm.sourceIncomePrincipalIndicatorReadOnly and isSource}" />
<c:set var="targetIncomePrincipalIndicatorReadOnly" value="${KualiForm.targetIncomePrincipalIndicatorReadOnly and not isSource}" />
<c:set var="sourceKemidReadOnly" value="${KualiForm.sourceKemidReadOnly and isSource}" />
<c:set var="targetKemidReadOnly" value="${KualiForm.targetKemidReadOnly and not isSource}" />
<c:set var="showSourceTransLines" value="${KualiForm.showSourceTransLines and isSource}" />
<c:set var="showTargetTransLines" value="${KualiForm.showTargetTransLines and not isSource}" />
<c:set var="showSourceRefresh" value="${KualiForm.showSourceRefresh and isSource}" />
<c:set var="showTargetRefresh" value="${KualiForm.showTargetRefresh and not isSource}" />
<c:set var="showSourceBalance" value="${KualiForm.showSourceBalance and isSource}" />
<c:set var="showTargetBalance" value="${KualiForm.showTargetBalance and not isSource}" />
<c:set var="showSourceDelete" value="${KualiForm.showSourceDelete and isSource}" />
<c:set var="showTargetDelete" value="${KualiForm.showTargetDelete and not isSource}" />

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<c:if test="${isSource}" >
  <c:set var="lineAttributes" value="${DataDictionary.EndowmentSourceTransactionLine.attributes}" />
  <c:set var="newTransactionLine" value="newSourceTransactionLine" />
  <c:set var="methodToCallAdd" value="methodToCall.insertSourceTransactionLine" />
  <c:set var="methodToCallDelete" value="methodToCall.deleteSourceTransactionLine" />
  <c:set var="methodToCallRefreshTaxLotLines" value="methodToCall.refreshSourceTaxLots" />
  <c:set var="methodToCallBalanceInquiry" value="methodToCall.performBalanceInquiryForSourceTransactionLine" />
  <c:set var="transLines" value="document.sourceTransactionLines"/> 
  <c:set var="totalIncomeAmount" value="${KualiForm.document.sourceIncomeTotal}"/> 
  <c:set var="totalPrincipalAmount" value="${KualiForm.document.sourcePrincipalTotal}"/>
  <c:set var="totalIncomeUnits" value="${KualiForm.document.sourceIncomeTotalUnits}"/>
  <c:set var="totalPrincipalUnits" value="${KualiForm.document.sourcePrincipalTotalUnits}"/>
  <c:set var="importLineAction" value="importSourceTransactionLines"/>
  <c:set var="errorKeyMatch" value="${EndowConstants.SOURCE_TRANSACTION_LINES_ERRORS}" />
</c:if>
<c:if test="${not isSource}">
  <c:set var="lineAttributes" value="${DataDictionary.EndowmentTargetTransactionLine.attributes}" />
  <c:set var="newTransactionLine" value="newTargetTransactionLine" />
  <c:set var="methodToCallAdd" value="methodToCall.insertTargetTransactionLine" />
  <c:set var="methodToCallDelete" value="methodToCall.deleteTargetTransactionLine" />
  <c:set var="methodToCallRefreshTaxLotLines" value="methodToCall.refreshTargetTaxLots" />
  <c:set var="methodToCallBalanceInquiry" value="methodToCall.performBalanceInquiryForTargetTransactionLine" />
  <c:set var="transLines" value="document.targetTransactionLines"/>
  <c:set var="totalIncomeAmount" value="${KualiForm.document.targetIncomeTotal}"/> 
  <c:set var="totalPrincipalAmount" value="${KualiForm.document.targetPrincipalTotal}"/>
  <c:set var="totalIncomeUnits" value="${KualiForm.document.targetIncomeTotalUnits}"/>
  <c:set var="totalPrincipalUnits" value="${KualiForm.document.targetPrincipalTotalUnits}"/>
  <c:set var="importLineAction" value="importTargetTransactionLines"/>
  <c:set var="errorKeyMatch" value="${EndowConstants.TARGET_TRANSACTION_LINES_ERRORS}" />
</c:if>

<table cellpadding="0" cellspacing="0" class="datatable" summary="Transaction Lines section">
	    <tr>
	            <td colspan="1" class="tab-subhead" style="border-right: none;" align="left">
	            <c:if test="${isSource}">${sourceGroupLabel}</c:if>
	            <c:if test="${not isSource}">${targetGroupLabel}</c:if>
	            </td>    
	            <td colspan="5" class="tab-subhead" style="border-right: none;border-left: none;" >
	            &nbsp;
	            </td>
				
				<td colspan="2" class="tab-subhead" align="right" nowrap="nowrap" style="border-left: none;">
			    <c:if test="${isSource and (not readOnly) and showSourceImport}">
					<SCRIPT type="text/javascript">
                		<!--
                  		function hideSourceTranImport() {
                      		document.getElementById("showLinkSourceTran").style.display="inline";
                      		document.getElementById("uploadDivSourceTran" + ${isSource}).style.display="none";
                  		}
                  		function showSourceTranImport() {
                      		document.getElementById("showLinkSourceTran").style.display="none";
                      		document.getElementById("uploadDivSourceTran").style.display="inline";
                  		}
                  		document.write(
                    		'<a id="showLinkSourceTran" href="#" onclick="showSourceTranImport();return false;">' +
                      		'<img src="${ConfigProperties.externalizable.images.url}tinybutton-importlines.gif" title="import transaction lines from file" alt="import transaction lines from file"' +
                      		'     width=72 height=15 border=0 align="right" class="det-button">' +
                    		'<\/a>' +
                    		'<div id="uploadDivSourceTran" style="display:none;" >' +
                      		'<html:file size="30" property="transactionSourceLinesImportFile" />' +
                      		'<html:image property="methodToCall.${importLineAction}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
                                    styleClass="tinybutton" alt="add imported transaction lines" title="add imported transaction lines" />' +
                      		'<html:image property="methodToCall.cancelImport" src="${ConfigProperties.externalizable.images.url}tinybutton-cancelimport.gif"
                                    styleClass="tinybutton" alt="cancel import" title="cancel import" onclick="hideSourceTranImport();return false;" />' +
                    		'<\/div>');
                		//-->
            		</SCRIPT>
					<NOSCRIPT>
						Import lines
						<html:file size="30" property="transactionSourceLinesImportFile" style="font:10px;height:16px;" />
						<html:image property="methodToCall.${importLineAction}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add imported items" title="add imported items" />
					</NOSCRIPT>
				</c:if>
				<c:if test="${not isSource and (not readOnly) and showTargetImport}">
					<SCRIPT type="text/javascript">
                		<!--
                  		function hideTargetTranImport() {
                      		document.getElementById("showLinkTargetTran").style.display="inline";
                      		document.getElementById("uploadDivTargetTran").style.display="none";
                  		}
                  		function showTargetTranImport() {
                      		document.getElementById("showLinkTargetTran").style.display="none";
                      		document.getElementById("uploadDivTargetTran").style.display="inline";
                  		}
                  		document.write(
                    		'<a id="showLinkTargetTran" href="#" onclick="showTargetTranImport();return false;">' +
                      		'<img src="${ConfigProperties.externalizable.images.url}tinybutton-importlines.gif" title="import transaction lines from file" alt="import transaction lines from file"' +
                      		'     width=72 height=15 border=0 align="right" class="det-button">' +
                    		'<\/a>' +
                    		'<div id="uploadDivTargetTran" style="display:none;" >' +
                      		'<html:file size="30" property="transactionLineImportFile" />' +
                      		'<html:image property="methodToCall.${importLineAction}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
                                    styleClass="tinybutton" alt="add imported transaction lines" title="add imported transaction lines" />' +
                      		'<html:image property="methodToCall.cancelImport" src="${ConfigProperties.externalizable.images.url}tinybutton-cancelimport.gif"
                                    styleClass="tinybutton" alt="cancel import" title="cancel import" onclick="hideTargetTranImport();return false;" />' +
                    		'<\/div>');
                		//-->
            		</SCRIPT>
					<NOSCRIPT>
						Import lines
						<html:file size="30" property="transactionLineImportFile" style="font:10px;height:16px;" />
						<html:image property="methodToCall.${importLineAction}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add imported items" title="add imported items" />
					</NOSCRIPT>
					</c:if>
				</td>
				
	    </tr>
	    
	   <endow:displayIfErrorsOrWarnings keyMatch="${errorKeyMatch}">
	    <tr>
            <td colspan="8"><kul:errors keyMatch="${errorKeyMatch}" errorTitle="Errors found in this Section:" /></td>
        </tr>
       </endow:displayIfErrorsOrWarnings>
	    
		<tr>
		    <c:if test="${showIncomeTotalAmount}">
        	    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
		    </c:if>
		    
		    <c:if test="${!showIncomeTotalAmount}">
            	<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" colspan="2"/>
		    </c:if>		    
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.kemid}"/>
            <c:if test="${showETranCode}">
	            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.etranCode}"/>
            </c:if>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.transactionLineDescription}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.transactionIPIndicatorCode}"/>
            <c:if test="${KualiForm.showTransactionAmount}">
            	<kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.transactionAmount}"/>
            </c:if>
            <c:if test="${KualiForm.showUnitAdjustmentAmount}">
            	<kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.unitAdjustmentAmount}"/>
            </c:if>
            <c:if test="${hasUnits}">
           		<kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.transactionUnits}" forceRequired="true"/>
            </c:if>	

            <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
		</tr>
        <c:if test="${not readOnly}">
            <tr>
		    <c:if test="${showIncomeTotalAmount}">            
                <kul:htmlAttributeHeaderCell literalLabel="add:" scope="row"/>
            </c:if>
		    <c:if test="${!showIncomeTotalAmount}">            
                <kul:htmlAttributeHeaderCell literalLabel="add:" scope="row" colspan="2"/>
            </c:if>   
                <td class="infoline">
                  <c:choose>
                    <c:when test="${targetKemidReadOnly or sourceKemidReadOnly}">
                	  <kul:htmlControlAttribute attributeEntry="${lineAttributes.kemid}" 
                		  property="${newTransactionLine}.kemid"
                		  onblur="loadTransactionLineKEMIDShortTitle('${newTransactionLine}.kemid', '${newTransactionLine}.kemidObj.ShortTitle.div');"
	            		  readOnly="true"/>&nbsp;
                    </c:when>
                  
                    <c:otherwise>
                      <kul:htmlControlAttribute attributeEntry="${lineAttributes.kemid}" 
                          property="${newTransactionLine}.kemid"
                          onblur="loadTransactionLineKEMIDShortTitle('${newTransactionLine}.kemid', '${newTransactionLine}.kemidObj.ShortTitle.div');"
                          readOnly="false"/>&nbsp;
                          <kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.KEMID"
                                      fieldConversions="kemid:${newTransactionLine}.kemid" />
                    </c:otherwise>
                  </c:choose>              
                                    
				  <br/>
				  <div id="${newTransactionLine}.kemidObj.ShortTitle.div" class="fineprint">
            	    <kul:htmlControlAttribute attributeEntry="${lineAttributes.kemid}" property="${newTransactionLine}.kemidObj.shortTitle" readOnly="true" />
            	  </div>					                
				</td>
				<c:if test="${showETranCode}">				
	                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.etranCode}"
	                	 property="${newTransactionLine}.etranCode" onblur="loadEndowmentTransactionName('${newTransactionLine}.etranCode', '${newTransactionLine}.etranCodeObj.name.div');" readOnly="${readOnly}"/>&nbsp;
	                    <kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode"
					                fieldConversions="code:${newTransactionLine}.etranCode" />&nbsp;
					    <br/>
						<div id="${newTransactionLine}.etranCodeObj.name.div" class="fineprint">
	            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.etranCode}" property="${newTransactionLine}.etranCodeObj.name" readOnly="true" />
	            		</div>						                
					                
	                </td>
	            </c:if> 
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionLineDescription}" property="${newTransactionLine}.transactionLineDescription" /></td>

				<c:if test="${setFieldValueToPrincipal}">
                	<td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionIPIndicatorCode}" property="${newTransactionLine}.transactionIPIndicatorCode" readOnly="true"/></td>
				</c:if>
				<c:if test="${not setFieldValueToPrincipal}">
                	<td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionIPIndicatorCode}" property="${newTransactionLine}.transactionIPIndicatorCode" readOnly="${targetIncomePrincipalIndicatorReadOnly or readOnly}"/></td>
				</c:if>
				<c:if test="${KualiForm.showTransactionAmount}">
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionAmount}" property="${newTransactionLine}.transactionAmount" styleClass="right" readOnly="${isTransAmntReadOnly}"/></td>
                </c:if>
	            <c:if test="${KualiForm.showUnitAdjustmentAmount}">
	            	<td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.unitAdjustmentAmount}" property="${newTransactionLine}.unitAdjustmentAmount" styleClass="right" readOnly="${readOnly}"/></td>
            	</c:if>
                <c:if test="${hasUnits}">
                	<td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionUnits}" property="${newTransactionLine}.transactionUnits" styleClass="right"/></td>
                </c:if>
                <c:choose>
                  <c:when test="${showTargetAdd or showSourceAdd}">
                    <td class="infoline">
                        <c:if test="${isSource && KualiForm.showFromTransactionLine}">
                            <div align="center">
                                <html:image property="${methodToCallAdd}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add Transaction Line" title="add" styleClass="tinybutton"/>
                            </div>
                        </c:if>
                        <c:if test="${not isSource && KualiForm.showToTransactionLine}">
                            <div align="center">
                               <html:image property="${methodToCallAdd}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add Transaction Line" title="add" styleClass="tinybutton"/>
                            </div>
                        </c:if>
                    </td>
                   </c:when>
                   <c:otherwise>
                    <td class="infoline">
                      <div align="center">&nbsp;</div>
                    </td>
                   </c:otherwise>
                </c:choose>
            </tr>
        </c:if>
      
        <c:if test="${showTargetTransLines or showSourceTransLines}">
        <logic:iterate id="item" name="KualiForm" property="${transLines}" indexId="ctr">
            <tr>
		    <c:if test="${showIncomeTotalAmount}">            
                <kul:htmlAttributeHeaderCell literalLabel="${ctr+1}:" scope="row" ></kul:htmlAttributeHeaderCell>
            </c:if>
		    <c:if test="${!showIncomeTotalAmount}">            
                <kul:htmlAttributeHeaderCell literalLabel="${ctr+1}:" scope="row" colspan="2"></kul:htmlAttributeHeaderCell>
            </c:if>     
                   
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.kemid}" property="${transLines}[${ctr}].kemid"
						onblur="loadTransactionLineKEMIDShortTitle('${transLines}[${ctr}].kemid', 'document.${transLines}[${ctr}].kemidObj.ShortTitle.div');"                	
                	    readOnly="${readOnly or targetKemidReadOnly or sourceKemidReadOnly}"/>&nbsp;
                	<c:if test="${not readOnly and (not (targetKemidReadOnly or sourceKemidReadOnly))}">
                		<kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.KEMID"
				                    fieldConversions="kemid:${transLines}[${ctr}].kemid" />
                    </c:if>	
				    <br/>
					<div id="document.${transLines}[${ctr}].kemidObj.ShortTitle.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.kemid}" property="${transLines}[${ctr}].kemidObj.shortTitle" readOnly="true" />
            		</div>					                
                </td>
				<c:if test="${showETranCode}">                
	                <td class="datacell">
	                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.etranCode}" property="${transLines}[${ctr}].etranCode"
	                		onblur="loadEndowmentTransactionName('${transLines}[${ctr}].etranCode, 'document.${transLines}[${ctr}].etranCodeObj.name.div');"
	                		readOnly="${readOnly}"/>&nbsp;
	                	<c:if test="${not readOnly}">
	                		<kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode"
					                    fieldConversions="code:${transLines}[${ctr}].etranCode" />
					    </c:if>
				    <br/>
					<div id="document.${transLines}[${ctr}].etranCodeObj.name.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.etranCode}" property="${transLines}[${ctr}].etranCodeObj.name" readOnly="true" />
            		</div>					                
	                </td>
	            </c:if>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionLineDescription}" property="${transLines}[${ctr}].transactionLineDescription" readOnly="${readOnly}"/></td>
				<c:if test="${setFieldValueToPrincipal}">
	                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionIPIndicatorCode}" property="${transLines}[${ctr}].transactionIPIndicatorCode" readOnly="true"/></td>					
				</c:if>
				<c:if test="${not setFieldValueToPrincipal}">
	                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionIPIndicatorCode}" property="${transLines}[${ctr}].transactionIPIndicatorCode" readOnly="${readOnly or targetIncomePrincipalIndicatorReadOnly}"/></td>
				</c:if>
				<c:if test="${KualiForm.showTransactionAmount}">
                	<td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionAmount}" property="${transLines}[${ctr}].transactionAmount" readOnly="${readOnly or isTransAmntReadOnly}" styleClass="right"/></td>
                </c:if>
				<c:if test="${KualiForm.showUnitAdjustmentAmount}">
                	<td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.unitAdjustmentAmount}" property="${transLines}[${ctr}].unitAdjustmentAmount" readOnly="${readOnly}" styleClass="right"/></td>
                </c:if>                
                <c:if test="${hasUnits}">
	                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionUnits}" property="${transLines}[${ctr}].transactionUnits" readOnly="${readOnly}" styleClass="right"/></td>              
                </c:if>
                <td class="datacell">
                <div align="center">
                <c:if test="${not readOnly}" >
                  <c:if test="${hasUnits and (showTargetRefresh or showSourceRefresh)}">
                    <html:image property="${methodToCallRefreshTaxLotLines}.line${ctr}" src="${ConfigProperties.externalizable.images.url}tinybutton-refresh.gif" title="Refresh" alt="Refresh" styleClass="tinybutton" />
                  </c:if>
                  <c:if test="${showTargetBalance or showSourceBalance}">
                    <html:image property="${methodToCallBalanceInquiry}.line${ctr}" src="${ConfigProperties.externalizable.images.url}tinybutton-balinquiry.gif" title="Balance Inquiry for Line ${ctr+1}" alt="Balance Inquiry for Line ${ctr+1}" styleClass="tinybutton"/>
                  </c:if>
                  <c:if test="${showTargetDelete or showSourceDelete}">
                    <html:image property="${methodToCallDelete}.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="Delete Transaction Line ${ctr+1}" alt="Delete Transaction Line  ${ctr+1}" styleClass="tinybutton"/>
                  </c:if>
                </c:if>
                    
                </div>
                </td>
            </tr>
        </logic:iterate>
        </c:if>
        <c:if test="${showIncomeTotalAmount or showPrincipalTotalAmount}" >
        <tr>
				<td class="total-line" colspan="5">
					&nbsp;
				</td>
			
				<td class="total-line">
				<c:if test="${showIncomeTotalAmount}">
					<strong>Total Income Amount:
						${totalIncomeAmount}</strong>
				</c:if>
				</td>
			
				<td class="total-line">
				<c:if test="${showPrincipalTotalAmount}">
				    <strong>Total Principal Amount:
						${totalPrincipalAmount}</strong>
                </c:if>							
				</td>
			
				<c:if test="${!readOnly}">
					<td class="total-line">
						&nbsp;
					</td>
				</c:if>
		</tr>
		</c:if>
		<c:if test="${hasUnits}">
        <tr>
			<td class="total-line" colspan="5">
				&nbsp;
			</td>
			
			<c:if test="${showIncomeTotalUnits}">
				<td class="total-line">
					<strong>Total Income Units:
						${totalIncomeUnits}</strong>
				</td>
			</c:if>
			
			<c:if test="${showPrincipalTotalUnits}">			
				<td class="total-line">
				    <strong>Total Principal Units:
						${totalPrincipalUnits}</strong>
				</td>
			</c:if>
			
			<c:if test="${!readOnly}">
				<td class="total-line">
					&nbsp;
				</td>
			</c:if>
		</tr>
		</c:if>
</table>		