<%--
 Copyright 2006 The Kuali Foundation
 
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
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>


<kul:documentPage showDocumentInfo="true" documentTypeName="${KualiForm.docTitle}"
	htmlFormAction="${KualiForm.htmlFormAction}"
	renderMultipart="true" showTabButtons="true">
  <c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

  <sys:documentOverview editingMode="${KualiForm.editingMode}"/>

  
  <c:if test="${debug == true}">
    <kul:tab tabTitle="Debug" defaultOpen="true" tabErrorKey="debug">
      <div class="tab-container" align="center"> 
	    <table cellpadding="0" class="datatable" summary=""> 
          <tr>
            <td align="left" valign="middle" class="subhead"><span class="subhead-left">Debug</span></td>
          </tr>
        </table>
        <table cellpadding="0" class="datatable">
          <tr><td width="10%">editableFlag</td><td>${KualiForm.editableFlag}</td></tr>
          <tr><td>manualEditFlag</td><td>${KualiForm.manualEditFlag}</td></tr>
          <tr><td>processInBatch</td><td>${KualiForm.processInBatch}</td></tr>
          <tr><td>chooseSystem</td><td>${KualiForm.chooseSystem}</td></tr>
          <tr><td>editMethod</td><td>${KualiForm.editMethod}</td></tr>
          <tr><td>inputGroupId</td><td>${KualiForm.document.correctionInputFileName}</td></tr>
		  <tr><td>outputGroupId</td><td>${KualiForm.document.correctionOutputFileName}</td></tr>
          <tr><td>inputFileName</td><td>${KualiForm.inputFileName}</td></tr>
          <tr><td>dataLoadedFlag</td><td>${KualiForm.dataLoadedFlag}</td></tr>
          <tr><td>matchCriteriaOnly</td><td>${KualiForm.matchCriteriaOnly}</td></tr>
          <tr><td>editableFlag</td><td>${KualiForm.editableFlag}</td></tr>
          <tr><td>deleteFileFlag</td><td>${KualiForm.deleteFileFlag}</td></tr>
          <tr><td>allEntries.size</td><td>${KualiForm.allEntriesSize}</td></tr>
          <tr><td>readOnly</td><td>${readOnly}</td></tr>
        </table>
      </div>
    </kul:tab>
  </c:if>
  <kul:tab tabTitle="Summary" defaultOpen="true" tabErrorKey="summary">
    <c:if test="${KualiForm.document.correctionTypeCode ne 'R' and (not (KualiForm.persistedOriginEntriesMissing && KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.inputGroupId)) && ((KualiForm.dataLoadedFlag and !KualiForm.restrictedFunctionalityMode) or KualiForm.document.correctionOutputFileName != null or !KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT])}" >
      
      <div class="tab-container" align="center"> 
	    <table cellpadding="0" class="datatable" summary=""> 
          <tr>
            <c:if test="${KualiForm.showOutputFlag == true or KualiForm.showSummaryOutputFlag == true}">
              <td align="left" valign="middle" class="subhead"><span class="subhead-left">Summary of Output Group</span></td>
            </c:if>
            <c:if test="${KualiForm.showOutputFlag == false}">
              <td align="left" valign="middle" class="subhead"><span class="subhead-left">Summary of Input Group</span></td>
            </c:if>
          </tr>
        </table>
        <table cellpadding="0" class="datatable">
          <tr>
            <td width="20%" align="left" valign="middle" > Total Debits: </td> 
            <td align="right" valign="middle"> <fmt:formatNumber value="${KualiForm.document.correctionDebitTotalAmount}" groupingUsed="true" minFractionDigits="2"/></td>
          </tr>
          <tr>
            <td width="20%" align="left" valign="middle" > Total Credits: </td> 
            <td align="right" valign="middle"> <fmt:formatNumber value="${KualiForm.document.correctionCreditTotalAmount}" groupingUsed="true" minFractionDigits="2"/></td>
          </tr>
          <tr>
            <td width="20%" align="left" valign="middle" > Total No DB/CR: </td> 
            <td align="right" valign="middle"> <fmt:formatNumber value="${KualiForm.document.correctionBudgetTotalAmount}" groupingUsed="true" minFractionDigits="2"/></td>
          </tr>
          <tr>
            <td width="20%" align="left" valign="middle" > Rows output: </td> 
            <td align="right" valign="middle"> <fmt:formatNumber value="${KualiForm.document.correctionRowCount}" groupingUsed="true"/></td>
          </tr>
        </table>
      </div>
    </c:if>
    <c:if test="${KualiForm.persistedOriginEntriesMissing && KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.document.correctionInputFileName}">
      <div class="tab-container" align="center"> 
	    <table cellpadding="0" class="datatable" summary=""> 
          <tr>
            <c:if test="${KualiForm.showOutputFlag == true or KualiForm.showSummaryOutputFlag == true}">
              <td align="left" valign="middle" class="subhead"><span class="subhead-left">Summary of Output Group</span></td>
            </c:if>
            <c:if test="${KualiForm.showOutputFlag == false or KualiForm.showSummaryOutputFlag == true}">
              <td align="left" valign="middle" class="subhead"><span class="subhead-left">Summary of Input Group</span></td>
            </c:if>
          </tr>
        </table>
        <table cellpadding="0" class="datatable">
          <tr>
            <td>The summary is unavailable because the origin entries are unavailable.</td> 
          </tr>
        </table>
      </div>
    </c:if>
    <c:if test="${KualiForm.restrictedFunctionalityMode && not KualiForm.persistedOriginEntriesMissing && KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" >
      <div class="tab-container" align="center"> 
	    <table cellpadding="0" class="datatable" summary=""> 
          <tr>
            <c:if test="${KualiForm.showOutputFlag == true or KualiForm.showSummaryOutputFlag == true}">
              <td align="left" valign="middle" class="subhead"><span class="subhead-left">Summary of Output Group</span></td>
            </c:if>
            <c:if test="${KualiForm.showOutputFlag == false or KualiForm.showSummaryOutputFlag == true}">
              <td align="left" valign="middle" class="subhead"><span class="subhead-left">Summary of Input Group</span></td>
            </c:if>
          </tr>
        </table>
        <table cellpadding="0" class="datatable">
          <tr>
            <td>The summary is unavailable because the selected origin entry group is too large.</td> 
          </tr>
        </table>
      </div>
    </c:if>
  </kul:tab>

<%-- ------------------------------------------------------------ This is read/write mode --------------------------------------------------- --%>
  <c:if test="${readOnly == false}">
    <kul:tab tabTitle="Correction Process" defaultOpen="true" tabErrorKey="systemAndEditMethod">
      <div class="tab-container" align="center" >
        <table cellpadding=0 class="datatable" summary=""> 
          <tr>
            <td align="left" valign="middle" class="subhead"><span class="subhead-left"></span><label for="chooseSystem">Select System</label> and <label for"editMethod">Edit Method</label></td>
          </tr>
          <tr>
            <td>
              <center>
                <html:select property="chooseSystem" styleId="chooseSystem" title="Select System">
                  <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|CorrectionChooseSystemValuesFinder" label="value" value="key"/>
                </html:select>
                
                <html:select property="editMethod" styleId="editMethod" title="Edit Method">
                  <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|CorrectionEditMethodValuesFinder" label="value" value="key"/>
                </html:select>
                <html:image property="methodToCall.selectSystemEditMethod.anchor${currentTabIndex}" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" styleClass="tinybutton" alt="Select System and Edit Method" title="Select System and Edit Method"/>
              </center>
            </td>
          </tr>
        </table>
      </div>
    </kul:tab>  
    <kul:tab tabTitle="Documents in System" defaultOpen="true" tabErrorKey="documentsInSystem">
      <c:if test="${KualiForm.chooseSystem == 'D'}" >
        <div class="tab-container" align="center" > 
          <table cellpadding=0 class="datatable" summary="">
            <tr>
              <td align="left" valign="middle" class="subhead"><span class="subhead-left"></span>Documents in System</td>
            </tr>
            <tr>
              <td colspan="2" class="bord-l-b" style="padding: 4px; vertical-align: top;"> 
                <center>
                  <label for="inputGroupId"><strong>Origin Entry Group</strong></label><br/><br/>
                  <html:select property="document.correctionInputFileName" size="10" styleId="inputGroupId" title="Origin Entry Group" >
                    <c:if test="${KualiForm.inputGroupIdFromLastDocumentLoadIsMissing and KualiForm.inputGroupId eq KualiForm.inputGroupIdFromLastDocumentLoad}">
                      <option value="<c:out value="${KualiForm.inputGroupIdFromLastDocumentLoad}"/>" selected="selected"><c:out value="${KualiForm.inputGroupIdFromLastDocumentLoad}"/> Document was last saved with this origin entry group selected.  Group is no longer in system.</option>
                    </c:if>
                    
                  <c:choose>
					<c:when test="${KualiForm.documentType == 'LLCP'}" >
						<c:choose>
		                      <c:when test="${KualiForm.editMethod eq 'R'}">
    		                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|ProcessingCorrectionLaborGroupEntriesFinder" label="value" value="key" />
        		              </c:when>
            		          <c:otherwise>
                		        <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|CorrectionLaborGroupEntriesFinder" label="value" value="key" />
                    		  </c:otherwise>
                    	</c:choose>
                   	</c:when>
                   	<c:otherwise>
                   		<c:choose>
	                      <c:when test="${KualiForm.editMethod eq 'R'}">
    	                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|ProcessingCorrectionGroupEntriesFinder" label="value" value="key" />
        	              </c:when>
            	          <c:otherwise>
                	        <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|CorrectionGroupEntriesFinder" label="value" value="key" />
                    	  </c:otherwise>
                    	</c:choose>
                   	</c:otherwise>
                  </c:choose>
                  </html:select>
                  
                  <br/><br/>
                  <c:if test="${KualiForm.editMethod eq 'R'}">
                    <html:image property="methodToCall.confirmDeleteDocument.anchor${currentTabIndex}" src="${ConfigProperties.externalizable.images.url}tinybutton-remgrpproc.gif" styleClass="tinybutton" alt="Remove Group From Processing" title="Remove Group From Processing" />
                  </c:if>
                  <c:if test="${KualiForm.editMethod eq 'M' or KualiForm.editMethod eq 'C'}">
                    <html:image property="methodToCall.loadGroup.anchor${currentTabIndex}" src="${ConfigProperties.externalizable.images.url}tinybutton-loadgroup.gif" styleClass="tinybutton" alt="Show All Entries" title="Show All Entries"/>
                  </c:if>
                  <html:image property="methodToCall.saveToDesktop.anchor${currentTabIndex}" src="${ConfigProperties.externalizable.images.url}tinybutton-cpygrpdesk.gif" styleClass="tinybutton" alt="Save To Desktop" title="Save To Desktop" onclick="excludeSubmitRestriction=true" />
                </center> 
              </td>
            </tr>
          </table>
        </div>
      </c:if>
    </kul:tab>
    <kul:tab tabTitle="Correction File Upload" defaultOpen="true" tabErrorKey="fileUpload">
      <c:if test="${KualiForm.chooseSystem == 'U'}" >
        <div class="tab-container" align="center"> 
            <h3>Corrections <label for="sourceFile">File Upload</upload></h3>
          <table cellpadding=0 class="datatable" summary=""> 
            <tr>
              <td class="bord-l-b" style="padding: 4px;">
              	
                <html:file size="30" property="sourceFile" styleId="sourceFile" title="File Upload" />
                <html:image property="methodToCall.uploadFile.anchor${currentTabIndex}" src="${ConfigProperties.externalizable.images.url}tinybutton-loaddoc.gif" styleClass="tinybutton" alt="upload file" title="upload file"/>
              </td>
            </tr>
          </table>
        </div>
      </c:if>
    </kul:tab>
    <kul:tab tabTitle="Search Results" defaultOpen="true" tabErrorKey="searchResults">
      <c:if test="${KualiForm.restrictedFunctionalityMode && !KualiForm.persistedOriginEntriesMissing}">
        <div class="tab-container" align="center">
          <table cellpadding=0 class="datatable" summary=""> 
            <tr>
              <td align="left" valign="middle" class="subhead">Search Results</td>
            </tr>
            <tr>
              <td><bean:message key="gl.correction.restricted.functionality.search.results.label" /></td>
            </tr>
          </table>
        </div>
      </c:if>
      <c:if test="${KualiForm.restrictedFunctionalityMode && KualiForm.persistedOriginEntriesMissing && KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.document.correctionInputFileName}">
        <div class="tab-container" align="center">
          <table cellpadding=0 class="datatable" summary=""> 
            <tr>
              <td align="left" valign="middle" class="subhead">Search Results</td>
            </tr>
            <tr>
              <td><bean:message key="gl.correction.persisted.origin.entries.missing" /></td>
            </tr>
          </table>
        </div>
      </c:if>
      <c:if test="${KualiForm.chooseSystem != null and KualiForm.editMethod != null and KualiForm.dataLoadedFlag == true and !KualiForm.restrictedFunctionalityMode and !(KualiForm.persistedOriginEntriesMissing && KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.document.correctionInputFileName)}" >
        <div class="tab-container" align="left" style="overflow: scroll; width: 100% ;"> 
          <table cellpadding=0 class="datatable" summary=""> 
            <tr>
              <c:choose>
                <c:when test="${KualiForm.showOutputFlag == true and KualiForm.editMethod == 'C'}">
                  <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Results - Output Group</span></td>
                </c:when>
                <c:when test="${KualiForm.showOutputFlag == false and KualiForm.editMethod == 'C'}">
                  <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Results - Input Group</span></td>
                </c:when>
                <c:when test="${KualiForm.showOutputFlag == true and KualiForm.editMethod == 'M'}">
                  <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Results - Matching Entries Only</span></td>
                </c:when>
                <c:when test="${KualiForm.showOutputFlag == false and KualiForm.editMethod == 'M'}">
                  <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Results - All Entries</span></td>
                </c:when>
              </c:choose>
            </tr>
            <tr>
            	<c:choose>
	            	<c:when test="${KualiForm.documentType == 'LLCP'}" >
		              <td>
    		          	<ld:displayLaborOriginEntrySearchResults laborOriginEntries="${KualiForm.displayEntries}"/>
        		      </td>
        		    </c:when>
            		<c:otherwise>  
		              <td>
    		            <glcp:displayOriginEntrySearchResults originEntries="${KualiForm.displayEntries}"/>
        	 	      </td>
	        	    </c:otherwise>
	        	</c:choose>    
            </tr>
            <c:if test="${KualiForm.editMethod == 'M' and KualiForm.editableFlag == true}">
              <tr>
                <td align="left" valign="middle" class="subhead"><span class="subhead-left">Manual Editing</span></td>
              </tr>
              <tr>                
                <td>
                  <table id="eachEntryForManualEdit">
                    <thead>
                      <tr>
                        <th>Manual Edit</th>
                        
                        <c:forEach items="${KualiForm.tableRenderColumnMetadata}" var="column">
                       
				          <th class="sortable">
				          	<label for="<c:out value="${column.propertyName}"/>">
					        <c:out value="${column.columnTitle}"/><c:if test="${empty column.columnTitle}">$nbsp;</c:if>
					        </label>
				          </th>
			            </c:forEach>
                      </tr>
                    </thead>
                    <tbody>
                      <tr class="odd">
                        <c:choose>
						  <c:when test="${KualiForm.documentType == 'LLCP'}" >
			            	<c:choose>
	                          <c:when test="${KualiForm.laborEntryForManualEdit.entryId == 0}">
    	                        <td><html:image property="methodToCall.addManualEntry.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" alt="edit" title="edit"/></td>
        	                  </c:when>
            	              <c:otherwise>
                	            <td>
                	              
                              	<html:image property="methodToCall.saveManualEntry.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif" styleClass="tinybutton" alt="edit" title="edit"/>
                            	</td>
                         	 </c:otherwise>
	                        </c:choose>
                        	<td><html:text property="laborEntryUniversityFiscalYear" size="5" styleId="laborEntryUniversityFiscalYear"/></td>
	                        <td><html:text property="laborEntryForManualEdit.chartOfAccountsCode" size="5" styleId="laborEntryForManualEdit.chartOfAccountsCode"/></td>
    	                    <td><html:text property="laborEntryForManualEdit.accountNumber" size="7" styleId="laborEntryForManualEdit.accountNumber"/></td>
        	                <td><html:text property="laborEntryForManualEdit.subAccountNumber" size="7" styleId="laborEntryForManualEdit.subAccountNumber"/></td>
            	            <td><html:text property="laborEntryForManualEdit.financialObjectCode" size="5" styleId="laborEntryForManualEdit.financialObjectCode"/></td>
                	        <td><html:text property="laborEntryForManualEdit.financialSubObjectCode" size="6" styleId="laborEntryForManualEdit.financialSubObjectCode"/></td>
                    	    <td><html:text property="laborEntryForManualEdit.financialBalanceTypeCode" size="8" styleId="laborEntryForManualEdit.financialBalanceTypeCode"/></td>
	                        <td><html:text property="laborEntryForManualEdit.financialObjectTypeCode" size="6" styleId="laborEntryForManualEdit.financialObjectTypeCode"/></td>
    	                    <td><html:text property="laborEntryForManualEdit.universityFiscalPeriodCode" size="6" styleId="laborEntryForManualEdit.universityFiscalPeriodCode"/></td>
        	                <td><html:text property="laborEntryForManualEdit.financialDocumentTypeCode" size="10" styleId="laborEntryForManualEdit.financialDocumentTypeCode"/></td>
                	        <td><html:text property="laborEntryForManualEdit.financialSystemOriginationCode" size="6" styleId="laborEntryForManualEdit.financialSystemOriginationCode"/></td>
            	            <td><html:text property="laborEntryForManualEdit.documentNumber" size="14" styleId="laborEntryForManualEdit.documentNumber"/></td>
    	                    <td><html:text property="laborEntryTransactionLedgerEntrySequenceNumber" size="9" styleId="laborEntryTransactionLedgerEntrySequenceNumber"/></td>
	                        <td><html:text property="laborEntryForManualEdit.positionNumber" size="14" styleId="laborEntryForManualEdit.positionNumber"/></td>
        	                <td><html:text property="laborEntryForManualEdit.projectCode" size="7" styleId="laborEntryForManualEdit.projectCode"/></td>
	                        <td><html:text property="laborEntryForManualEdit.transactionLedgerEntryDescription" size="11" styleId="laborEntryForManualEdit.transactionLedgerEntryDescription"/></td>
	                        <td><html:text property="laborEntryTransactionLedgerEntryAmount" size="7" styleId="laborEntryTransactionLedgerEntryAmount"/></td>
            	            <td><html:text property="laborEntryForManualEdit.transactionDebitCreditCode" size="9" styleId="laborEntryForManualEdit.transactionDebitCreditCode"/></td>
                	        <td><html:text property="laborEntryTransactionDate" size="12" styleId="laborEntryTransactionDate"/></td>
                    	    <td><html:text property="laborEntryForManualEdit.organizationDocumentNumber" size="12" styleId="laborEntryForManualEdit.organizationDocumentNumber"/></td>
	                        <td><html:text property="laborEntryForManualEdit.organizationReferenceId" size="13" styleId="laborEntryForManualEdit.organizationReferenceId"/></td>
        	                <td><html:text property="laborEntryForManualEdit.referenceFinancialDocumentTypeCode" size="10" styleId="laborEntryForManualEdit.referenceFinancialDocumentTypeCode"/></td>
            	            <td><html:text property="laborEntryForManualEdit.referenceFinancialSystemOriginationCode" size="10" styleId="laborEntryForManualEdit.referenceFinancialSystemOriginationCode"/></td>
                	        <td><html:text property="laborEntryForManualEdit.referenceFinancialDocumentNumber" size="9" styleId="laborEntryForManualEdit.referenceFinancialDocumentNumber"/></td>
                    	    <td><html:text property="laborEntryFinancialDocumentReversalDate" size="8" styleId="laborEntryFinancialDocumentReversalDate"/></td>
                            <td><html:text property="laborEntryForManualEdit.transactionEncumbranceUpdateCode" size="13" styleId="laborEntryForManualEdit.transactionEncumbranceUpdateCode"/></td>
	                        <td><html:text property="laborEntryTransactionPostingDate" size="14" styleId="laborEntryTransactionPostingDate"/></td>
	                        <td><html:text property="laborEntryPayPeriodEndDate" size="14" styleId="laborEntryPayPeriodEndDate"/></td>
	                        <td><html:text property="laborEntryTransactionTotalHours" size="14" styleId="laborEntryTransactionTotalHours"/></td>
	                        <td><html:text property="laborEntryPayrollEndDateFiscalYear" size="14" styleId="laborEntryPayrollEndDateFiscalYear"/></td>
	                        <td><html:text property="laborEntryForManualEdit.payrollEndDateFiscalPeriodCode" size="14" styleId="laborEntryForManualEdit.payrollEndDateFiscalPeriodCode"/></td>
	                        <td><html:text property="laborEntryForManualEdit.emplid" size="14" styleId="laborEntryForManualEdit.emplid"/></td>
	                        <td><html:text property="laborEntryEmployeeRecord" size="14" styleId="laborEntryEmployeeRecord"/></td>
	                        <td><html:text property="laborEntryForManualEdit.earnCode" size="14" styleId="laborEntryForManualEdit.earnCode"/></td>
	                        <td><html:text property="laborEntryForManualEdit.payGroup" size="14" styleId="laborEntryForManualEdit.payGroup"/></td>
	                        <td><html:text property="laborEntryForManualEdit.salaryAdministrationPlan" size="14" styleId="laborEntryForManualEdit.salaryAdministrationPlan"/></td>
	                        <td><html:text property="laborEntryForManualEdit.grade" size="14" styleId="laborEntryForManualEdit.grade"/></td>
  	                        <td><html:text property="laborEntryForManualEdit.runIdentifier" size="14" styleId="laborEntryForManualEdit.runIdentifier"/></td>
 	                        <td><html:text property="laborEntryForManualEdit.laborLedgerOriginalChartOfAccountsCode" size="14" styleId="laborEntryForManualEdit.laborLedgerOriginalChartOfAccountsCode"/></td>
	                        <td><html:text property="laborEntryForManualEdit.laborLedgerOriginalAccountNumber" size="14" styleId="laborEntryForManualEdit.laborLedgerOriginalAccountNumber"/></td>
	                        <td><html:text property="laborEntryForManualEdit.laborLedgerOriginalSubAccountNumber" size="14" styleId="laborEntryForManualEdit.laborLedgerOriginalSubAccountNumber"/></td>
	                        <td><html:text property="laborEntryForManualEdit.laborLedgerOriginalFinancialObjectCode" size="14" styleId="laborEntryForManualEdit.laborLedgerOriginalFinancialObjectCode"/></td>
	                        <td><html:text property="laborEntryForManualEdit.laborLedgerOriginalFinancialSubObjectCode" size="14" styleId="laborEntryForManualEdit.laborLedgerOriginalFinancialSubObjectCode"/></td>
	                        <td><html:text property="laborEntryForManualEdit.hrmsCompany" size="14" styleId="laborEntryForManualEdit.hrmsCompany"/></td>
	                        <td><html:text property="laborEntryForManualEdit.setid" size="14" styleId="laborEntryForManualEdit.setid"/></td>
    	 				  </c:when>
						  <c:otherwise>
						  <c:choose>
	                          <c:when test="${KualiForm.entryForManualEdit.entryId == 0}">
    	                        <td><html:image property="methodToCall.addManualEntry.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" alt="edit" title="edit"/></td>
        	                  </c:when>
            	              <c:otherwise>
                	            <td>
                	              
                              	<html:image property="methodToCall.saveManualEntry.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif" styleClass="tinybutton" alt="edit" title="edit"/>
                            	</td>
                         	 </c:otherwise>
	                        </c:choose>
	                        
			            	<td><html:text property="entryUniversityFiscalYear" size="5" styleId="entryUniversityFiscalYear"/></td>
	                        <td><html:text property="entryForManualEdit.chartOfAccountsCode" size="5" styleId="entryForManualEdit.chartOfAccountsCode"/></td>
    	                    <td><html:text property="entryForManualEdit.accountNumber" size="7" styleId="entryForManualEdit.accountNumber."/></td>
        	                <td><html:text property="entryForManualEdit.subAccountNumber" size="7" styleId="entryForManualEdit.subAccountNumber"/></td>
            	            <td><html:text property="entryForManualEdit.financialObjectCode" size="5" styleId="entryForManualEdit.financialObjectCode"/></td>
                	        <td><html:text property="entryForManualEdit.financialSubObjectCode" size="6" styleId="entryForManualEdit.financialSubObjectCode"/></td>
                    	    <td><html:text property="entryForManualEdit.financialBalanceTypeCode" size="8" styleId="entryForManualEdit.financialBalanceTypeCode"/></td>
	                        <td><html:text property="entryForManualEdit.financialObjectTypeCode" size="6" styleId="entryForManualEdit.financialObjectTypeCode"/></td>
    	                    <td><html:text property="entryForManualEdit.universityFiscalPeriodCode" size="6" styleId="entryForManualEdit.universityFiscalPeriodCode"/></td>
        	                <td><html:text property="entryForManualEdit.financialDocumentTypeCode" size="10" styleId="entryForManualEdit.financialDocumentTypeCode"/></td>
                	        <td><html:text property="entryForManualEdit.financialSystemOriginationCode" size="6" styleId="entryForManualEdit.financialSystemOriginationCode"/></td>
            	            <td><html:text property="entryForManualEdit.documentNumber" size="14" styleId="entryForManualEdit.documentNumber"/></td>
    	                    <td><html:text property="entryTransactionLedgerEntrySequenceNumber" size="9" styleId="entryTransactionLedgerEntrySequenceNumber"/></td>
	                        <td><html:text property="entryForManualEdit.transactionLedgerEntryDescription" size="11" styleId="entryForManualEdit.transactionLedgerEntryDescription"/></td>
        	                <td><html:text property="entryTransactionLedgerEntryAmount" size="7" styleId="entryTransactionLedgerEntryAmount"/></td>
            	            <td><html:text property="entryForManualEdit.transactionDebitCreditCode" size="9" styleId="entryForManualEdit.transactionDebitCreditCode"/></td>
                	        <td><html:text property="entryTransactionDate" size="12" styleId="entryTransactionDate"/></td>
                    	    <td><html:text property="entryForManualEdit.organizationDocumentNumber" size="12" styleId="entryForManualEdit.organizationDocumentNumber"/></td>
	                        <td><html:text property="entryForManualEdit.projectCode" size="7" styleId="entryForManualEdit.projectCode"/></td>
    	                    <td><html:text property="entryForManualEdit.organizationReferenceId" size="13" styleId="entryForManualEdit.organizationReferenceId"/></td>
        	                <td><html:text property="entryForManualEdit.referenceFinancialDocumentTypeCode" size="10" styleId="entryForManualEdit.referenceFinancialDocumentTypeCode"/></td>
            	            <td><html:text property="entryForManualEdit.referenceFinancialSystemOriginationCode" size="10" styleId="entryForManualEdit.referenceFinancialSystemOriginationCode"/></td>
                	        <td><html:text property="entryForManualEdit.referenceFinancialDocumentNumber" size="9" styleId="entryForManualEdit.referenceFinancialDocumentNumber"/></td>
                    	    <td><html:text property="entryFinancialDocumentReversalDate" size="8" styleId="entryFinancialDocumentReversalDate"/></td>
                        	<td><html:text property="entryForManualEdit.transactionEncumbranceUpdateCode" size="13" styleId="entryForManualEdit.transactionEncumbranceUpdateCode"/></td>
                          </c:otherwise>	
                        </c:choose>	
                  
                      	</tr>
                    </tbody>
                  </table>
                </td>
              </tr>
            </c:if>
            <c:if test="${KualiForm.manualEditFlag == true}" >
              <td>
                <STRONG> Do you want to edit this document? </STRONG>
                <html:image property="methodToCall.manualEdit.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif" styleClass="tinybutton" alt="show edit" title="show edit" />
              </td>
            </c:if>
          </table>
        </div>
      </c:if>
    </kul:tab>
    <kul:tab tabTitle="Edit Options and Action" defaultOpen="true" tabErrorKey="Edit Options and Action">
      <c:if test="${KualiForm.deleteFileFlag == false and (KualiForm.dataLoadedFlag == true || KualiForm.restrictedFunctionalityMode) and ((KualiForm.editMethod == 'C') or (KualiForm.editMethod == 'M' and KualiForm.editableFlag == true))}">
        <div class="tab-container" align="center">
          <table cellpadding=0 class="datatable" summary="">
            <c:if test="${KualiForm.editMethod == 'C'}">
              <tr>
                <td align="left" valign="middle" class="subhead"><span class="subhead-left">Edit Options and Action</span></td>
              </tr>
              <tr>
                <td>
                  <center>
                    <html:checkbox styleId="processInBatch" property="processInBatch" /> <STRONG> <label for="processInBatch">Process In Batch</label> </STRONG> &nbsp; &nbsp; &nbsp; &nbsp;  
                    <input type="hidden" name="processInBatch{CheckboxPresentOnFormAnnotation}" value="true"/>
                    <html:checkbox styleId="matchCriteriaOnly" property="matchCriteriaOnly" /> <STRONG> <label for="matchCriteriaOnly">Output only records which match criteria?</label> </STRONG>
                    <input type="hidden" name="matchCriteriaOnly{CheckboxPresentOnFormAnnotation}" value="true"/>
                  </center>
                </td>
              </tr>  
              <c:if test="${KualiForm.restrictedFunctionalityMode == false}">
                <tr>
                  <td>
                    <center>
                      <c:if test="${KualiForm.showOutputFlag == true}">
                        <strong>Show Input Group</strong>
                        <html:image property="methodToCall.showOutputGroup.anchor${currentTabIndex - 1}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" styleClass="tinybutton" alt="show Input Group" title="show Input Group" />
                      </c:if>
                      <c:if test="${KualiForm.showOutputFlag == false}">
                        <strong>Show Output Group</strong>
                        <html:image property="methodToCall.showOutputGroup.anchor${currentTabIndex - 1}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" styleClass="tinybutton" alt="show Output Group" title="show Output Group" />
                      </c:if>
                    </center>
                  </td>
                </tr>
              </c:if>
            </c:if>
            <c:if test="${KualiForm.editMethod == 'M' and KualiForm.editableFlag == true}">
              <tr>
                <td align="left" valign="middle" class="subhead"><span class="subhead-left">Edit Options and Action</span></td>
              </tr>
              <tr>
                <td>
                  <center>
                    <html:checkbox styleId="processInBatch" property="processInBatch" /> <STRONG> <label for="processInBatch">Process In Batch</label> </STRONG>
                    <input type="hidden" name="processInBatch{CheckboxPresentOnFormAnnotation}" value="true"/>
                  </center>
                </td>
              </tr>
            </c:if>
          </table>
        </div>
      </c:if>
    </kul:tab>
    <kul:tab tabTitle="Edit Criteria" defaultOpen="true" tabErrorKey="editCriteria">
      <c:if test="${KualiForm.deleteFileFlag == false and KualiForm.editMethod == 'C' and (KualiForm.dataLoadedFlag == true || KualiForm.restrictedFunctionalityMode == true)}">
        <div class="tab-container" align="center"> 
          <table cellpadding=0 class="datatable" summary="">
            <tr>
              <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Criteria</span></td> 
              <td align="left" valign="middle" class="subhead"><span class="subhead-left">Modification Criteria</span></td>
            </tr>
            <c:forEach var="group" items="${KualiForm.document.correctionChangeGroup}"> 
              <tr>
                <td colspan="2" align="left" class="bord-l-b" style="padding: 4px; vertical-align: top;"> 
                  <strong>Group:</strong>
                  <html:image property="methodToCall.removeCorrectionGroup.group${group.correctionChangeGroupLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="delete correction group" title="delete correction group" />
                </td>
              </tr>
              <tr style="border-bottom: 1px solid #333;"> 
                <td class="bord-l-b" style="padding: 4px; vertical-align: top;">
                  <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName">Field</label>:
                  <html:select property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName" styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName" title="Field">
                    <option value=""></option>
	                    <c:choose>
    	                	<c:when test="${KualiForm.documentType == 'LLCP'}" >
        	            		<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder" label="value" value="key"/>
							</c:when>
							<c:otherwise>
		            	        <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder" label="value" value="key"/>
		                	</c:otherwise>
		                </c:choose>
                  </html:select>
                  <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode">Operator</label>:
                  <html:select property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode" styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode" title="Operator">
                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|SearchOperatorsFinder" label="value" value="key"/>
                  </html:select>
                  <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue">Value</label>:
                  <html:text property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue" styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue" title="Value"/>
                  <html:image property="methodToCall.addCorrectionCriteria.criteria${group.correctionChangeGroupLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" alt="Add Search Criteria" title="Add Search Criteria" /><br>
                  <c:forEach items="${group.correctionCriteria}" var="criteria" varStatus="cc">
                    <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName">Field</label>:
                    <html:select property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName" styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName" title="field">
                      
                      <c:choose>
    	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
        	          		<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder" label="value" value="key"/>
						</c:when>
						<c:otherwise>
		            	    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder" label="value" value="key"/>
		                </c:otherwise>
		              </c:choose>
                      
                    </html:select>
                    <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode">Operator</label>:
                    <html:select property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode" styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode" title="Operator">
                      <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|SearchOperatorsFinder" label="value" value="key"/>
                    </html:select>
                    <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue">Value</label>:
                    <html:text property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue" styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue" title="Value"/>
                    <html:image property="methodToCall.removeCorrectionCriteria.criteria${group.correctionChangeGroupLineNumber}-${criteria.correctionCriteriaLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="delete search criterion" title="delete search criterion" />
                    <br>
                  </c:forEach>
                </td>
                <td class="bord-l-b" style="padding: 4px; vertical-align: top;">
                  <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldName">Field</label>:
                  <html:select property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldName" styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldName" title="Field">
                    <option value=""></option>
                     <c:choose>
    	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
							<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder" label="value" value="key"/>
						</c:when>
						<c:otherwise>
		                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder" label="value" value="key"/>
		                </c:otherwise>
		              </c:choose>

                  </html:select>
                  <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldValue">Replacement Value</label>:
                  <html:text property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldValue" styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldValue" title="Replacement Value"/>
                  <html:image property="methodToCall.addCorrectionChange.change${group.correctionChangeGroupLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add replacement specification" title="add replacement specification" styleClass="tinybutton" /> <br>
                  <c:forEach items="${group.correctionChange}" var="change">
                    <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName">Field</label>:
                    <html:select property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName" styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName" title="Field">
				      <c:choose>
    	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
							<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder" label="value" value="key"/>
						</c:when>
						<c:otherwise>
		                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder" label="value" value="key"/>
		                </c:otherwise>
		              </c:choose>
                    </html:select>
                    <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue">Replacement Value</label>:
                    <html:text property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue" styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue" title="Replacement Value"/>
                    <html:image property="methodToCall.removeCorrectionChange.change${group.correctionChangeGroupLineNumber}-${change.correctionChangeLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="delete search specification" title="delete search specification" styleClass="tinybutton" />
                    <br>
                  </c:forEach>
                </td>
              </tr>
            </c:forEach>
            <tr>
              <td colspan="2" align="left" class="bord-l-b" style="padding: 4px; vertical-align: top;"> 
                <center>
                  <STRONG>Add Groups </STRONG>
                  <html:image property="methodToCall.addCorrectionGroup.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add correction group" title="add correction group" styleClass="tinybutton" />
                </center>
              </td>
            </tr>
          </table>
        </div>
      </c:if>
    </kul:tab>
    <!--  Search for Manual Edit -->
    <kul:tab tabTitle="Search Criteria for Manual Edit" defaultOpen="true" tabErrorKey="manualEditCriteria">    
      <c:if test="${KualiForm.editMethod == 'M' and KualiForm.editableFlag == true and KualiForm.dataLoadedFlag == true}">
        <div class="tab-container" align="center">
          <table cellpadding=0 class="datatable" summary="">
            <tr>
              <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Criteria for Manual Edit</span></td>
            </tr>
            <tr style="border-bottom: 1px solid #333;"> 
              <td class="bord-l-b" style="padding: 4px; vertical-align: top;">
                <c:forEach var="group" items="${KualiForm.document.correctionChangeGroup}"> 
                  <c:forEach items="${group.correctionCriteria}" var="criteria" varStatus="cc">
                    <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName">Field</label>:
                    <html:select property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName" styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName" title="Field">
                     <c:choose>
    	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
							<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder" label="value" value="key"/>
						</c:when>
						<c:otherwise>
		                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder" label="value" value="key"/>
		                </c:otherwise>
		              </c:choose> 
                    </html:select>
                    <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode">Operator</label>:
                    <html:select property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode" styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode" title="Operator">
                      <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|SearchOperatorsFinder" label="value" value="key"/>
                    </html:select>
                    <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue">Value</label>:
                    <html:text property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue" styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue" title="Value"/>
                    <html:image property="methodToCall.removeCorrectionCriteria.criteria${group.correctionChangeGroupLineNumber}-${criteria.correctionCriteriaLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="Remove Search Criteria" title="Remove Search Criteria" />
                    <br />
                  </c:forEach>
                  <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName">Field</label>:
                  <html:select property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName" styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName" title="Field">
                    <option value=""></option>
                     <c:choose>
    	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
							<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder" label="value" value="key"/>
						</c:when>
						<c:otherwise>
		                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder" label="value" value="key"/>
		                </c:otherwise>
		              </c:choose>
                  </html:select>
                  <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode">Operator</label>:
                  <html:select property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode" styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode" title="Operator">
                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|SearchOperatorsFinder" label="value" value="key"/>
                  </html:select>
                  <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue">Value</label>:
                  <html:text property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue" styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue" title="Value"/>
                  <html:image property="methodToCall.addCorrectionCriteria.criteria${group.correctionChangeGroupLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" alt="Add Search Criteria" title="Add Search Criteria" />
                  <br />
                </c:forEach>
              </td>
            </tr>
            <tr>
              <td>
                <center>
                  <c:if test="${KualiForm.showOutputFlag == true}">
                    <strong>Show All Entries</strong>
                    <html:image property="methodToCall.searchCancelForManualEdit.anchor${currentTabIndex - 3}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" styleClass="tinybutton" alt="Show Matching Entries" title="Show Matching Entries" />
                  </c:if>
                  <c:if test="${KualiForm.showOutputFlag == false}">
                    <strong>Show Matching Entries</strong>
                    <html:image property="methodToCall.searchForManualEdit.anchor${currentTabIndex - 3}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" styleClass="tinybutton" alt="Show All Entries" title="Show All Entries" />
                  </c:if>
                </center>
              </td>
            </tr>
          </table>
        </div>
      </c:if>
    </kul:tab>
  </c:if>

<%-- ------------------------------------------------------------ This is read only mode --------------------------------------------------- --%>

  <c:if test="${readOnly == true}">
    <c:forEach var="group" items="${KualiForm.document.correctionChangeGroup}"> 
      <c:forEach items="${group.correctionCriteria}" var="criteria" varStatus="cc">
        
      </c:forEach>
      <c:forEach items="${group.correctionChange}" var="change">
       
      </c:forEach>
    </c:forEach>
    <div class="tab-container" align="center" >
      <table cellpadding=0 class="datatable" summary=""> 
        <tr>
          <td align="left" valign="middle" class="subhead"><span class="subhead-left"></span>System and Edit Method</td>
        </tr>
      </table>
      <table>
        <tr>
          <td width="20%" align="left" valign="middle"> System: </td>
          <td align="left" valign="middle" ><c:out value="${KualiForm.document.system}" /></td>
        </tr>
        <tr>
          <td width="20%" align="left" valign="middle"> Edit Method: </td>
          <td align="left" valign="middle" ><c:out value="${KualiForm.document.method}" /></td>
        </tr>
      </table>
    </div>
    <div class="tab-container" align="center" >
      <table cellpadding=0 class="datatable" summary=""> 
        <tr>
          <td align="left" valign="middle" class="subhead"><span class="subhead-left"></span>Input and Output File</td>
        </tr>
      </table>
      <table>
        <c:if test="${KualiForm.document.correctionInputFileName != null}">
          <tr>
            <td width="20%" align="left" valign="middle" > Input File Name: </td> 
            <td align="left" valign="middle" > <c:out value="${KualiForm.document.correctionInputFileName}" /></td>
          </tr>
        </c:if>
        <tr>
          <td width="20%" align="left" valign="middle" > Output File Name: </td> 
          <c:if test="${KualiForm.document.correctionOutputFileName != null}">
            <td align="left" valign="middle" > <c:out value="${KualiForm.document.correctionOutputFileName}" /></td>
          </c:if>
          <c:if test="${KualiForm.document.correctionOutputFileName == null}">
            <c:if test="${KualiForm.document.correctionTypeCode eq 'R'}">
              <td align="left" valign="middle" ><c:out value="${Constants.NOT_AVAILABLE_STRING}"/></td>
            </c:if>
            <c:if test="${KualiForm.document.correctionTypeCode ne 'R'}">
              <td align="left" valign="middle" > The output file name is unavailable until the document has a status of PROCESSED or FINAL.</td>
            </c:if>
          </c:if>
        </tr>
      </table>
    </div>
    <div class="tab-container" align="left" style="overflow: scroll; max-width: 100%;">
      <c:if test="${KualiForm.restrictedFunctionalityMode}">
        <div class="tab-container" align="center">
          <table cellpadding=0 class="datatable" summary=""> 
            <tr>
              <td align="left" valign="middle" class="subhead">Search Results</td>
            </tr>
            <tr>
              <td><bean:message key="gl.correction.restricted.functionality.search.results.label" /></td>
            </tr>
          </table>
        </div>
      </c:if>
      <c:if test="${!KualiForm.restrictedFunctionalityMode}">
        <table cellpadding=0 class="datatable" summary=""> 
          <tr>
            <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Results - Output Group</span></td>
          </tr>
          <tr>
         	 <c:choose>
	         	<c:when test="${KualiForm.documentType == 'LLCP'}" >
		    	    <td>
    		    	   	<ld:displayLaborOriginEntrySearchResults laborOriginEntries="${KualiForm.displayEntries}"/>
        		    </td>
        		</c:when>
            	<c:otherwise>  
		    	    <td>
    		    	    <glcp:displayOriginEntrySearchResults originEntries="${KualiForm.displayEntries}"/>
        	 	    </td>
	         	</c:otherwise>
	      	 </c:choose> 
          </tr>
        </table>
      </c:if>
    </div>
    <div class="tab-container" align="center"> 
      <table cellpadding=0 class="datatable" summary="">
        <tr>
          <td align="left" valign="middle" class="subhead"><span class="subhead-left">Edit Options and Action</span></td>
        </tr>
        <tr>
          <td>
            <center>
              <html:checkbox styleId="processInBatch" property="processInBatch" disabled="true"/> <STRONG> <label for="processInBatch">Process In Batch</label> </STRONG> &nbsp; &nbsp; &nbsp; &nbsp;  
              <c:if test="${KualiForm.document.correctionTypeCode == 'C'}" >
                <html:checkbox styleId="matchCriteriaOnly" property="matchCriteriaOnly" disabled="true"/> <STRONG> <label for="matchCriteriaOnly">Output only records which match criteria?</label> </STRONG>
                <%--disabled checkbox above is not submitted, so we create a hidden input --%>
              </c:if>
            </center>
          </td>
        </tr>
      </table>
    </div>
    <c:if test="${KualiForm.document.correctionTypeCode == 'C'}" >
      <div class="tab-container" align="center"> 
        <table cellpadding=0 class="datatable" summary="">
          <tr>
            <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Criteria</span></td> 
            <td align="left" valign="middle" class="subhead"><span class="subhead-left">Modification Criteria</span></td>
          </tr>
          <c:forEach var="group" items="${KualiForm.document.correctionChangeGroup}"> 
            <tr>
              <td colspan="2" align="left" class="bord-l-b" style="padding: 4px; vertical-align: top;"> 
                <strong>Group:</strong>
              </td>
            </tr>
            <tr style="border-bottom: 1px solid #333;"> 
              <td class="bord-l-b" style="padding: 4px; vertical-align: top;">
                <c:forEach items="${group.correctionCriteria}" var="criteria" varStatus="cc">
                  <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName">Field</label>:
                  <html:select disabled="true" property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName" styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName" title="Field">
                  <c:choose>
   	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
						<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder" label="value" value="key"/>
					</c:when>
					<c:otherwise>
	                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder" label="value" value="key"/>
	                </c:otherwise>
	              </c:choose>
                  </html:select>
                  <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode">Operator</label>:
                  <html:select disabled="true" property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode" styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode" title="Operator">
                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|SearchOperatorsFinder" label="value" value="key"/>
                  </html:select>
                  <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue">Value</label>:
                  <html:text disabled="true" property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue" styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue" title="Value"/>
                  <br>
                </c:forEach>
              </td>
              <td class="bord-l-b" style="padding: 4px; vertical-align: top;">
                <c:forEach items="${group.correctionChange}" var="change">
                  <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName">Field</label>:
                  <html:select disabled="true" property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName" styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName" title="Field">
	              <c:choose>
   	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
						<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder" label="value" value="key"/>
					</c:when>
					<c:otherwise>
	                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder" label="value" value="key"/>
	                </c:otherwise>
	              </c:choose>
                  </html:select>
                  <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue">Replacement Value</label>:
                  <html:text disabled="true" property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue" styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue" title="Replacement Value"/>
                  <br>
                </c:forEach>
              </td>
            </tr>
          </c:forEach>
        </table>
      </div>
    </c:if>
  </c:if>
  <kul:notes/>
  <kul:adHocRecipients />        
  <kul:routeLog/>
  <kul:superUserActions />
  <kul:panelFooter/>
  <sys:documentControls transactionalDocument="false" />
</kul:documentPage>
