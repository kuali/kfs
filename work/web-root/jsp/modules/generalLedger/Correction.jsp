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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp" %>

<kul:page showDocumentInfo="true" docTitle="${KualiForm.docTitle}"
	htmlFormAction="${KualiForm.htmlFormAction}" transactionalDocument="false"
	renderMultipart="true" showTabButtons="true">
  <c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

  <kul:hiddenDocumentFields isFinancialDocument="false" />
  <kul:documentOverview editingMode="${KualiForm.editingMode}"/>

  <html:hidden property="document.correctionChangeGroupNextLineNumber"/>
  <html:hidden property="dataLoadedFlag"/>
  <html:hidden property="editableFlag"/>
  <html:hidden property="manualEditFlag"/>
  <html:hidden property="deleteFileFlag"/>
  <html:hidden property="showOutputFlag"/>
  <html:hidden property="inputFileName"/>
  <html:hidden property="showSummaryOutputFlag"/>
  <html:hidden property="glcpSearchResultsSequenceNumber"/>
  <html:hidden property="restrictedFunctionalityMode"/>
  <html:hidden property="inputGroupIdFromLastDocumentLoad"/>
  <html:hidden property="inputGroupIdFromLastDocumentLoadIsMissing"/>
  <html:hidden property="persistedOriginEntriesMissing"/>

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
          <tr><td>inputGroupId</td><td>${KualiForm.document.correctionInputGroupId}</td></tr>
          <tr><td>outputGroupId</td><td>${KualiForm.outputGroupId}</td></tr>
          <tr><td>inputFileName</td><td>${KualiForm.inputFileName}</td></tr>
          <tr><td>dataLoadedFlag</td><td>${KualiForm.dataLoadedFlag}</td></tr>
          <tr><td>matchCriteriaOnly</td><td>${KualiForm.matchCriteriaOnly}</td></tr>
          <tr><td>editableFlag</td><td>${KualiForm.editableFlag}</td></tr>
          <tr><td>deteleFileFlag</td><td>${KualiForm.deleteFileFlag}</td></tr>
          <tr><td>allEntries.size</td><td>${KualiForm.allEntriesSize}</td></tr>
          <tr><td>readOnly</td><td>${readOnly}</td></tr>
        </table>
      </div>
    </kul:tab>
  </c:if>
  <kul:tab tabTitle="Summary" defaultOpen="true" tabErrorKey="summary">
    <c:if test="${KualiForm.document.correctionTypeCode ne 'R' and (not (KualiForm.persistedOriginEntriesMissing && KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.inputGroupId)) && ((KualiForm.dataLoadedFlag and !KualiForm.restrictedFunctionalityMode) or KualiForm.document.correctionOutputGroupId != null or not empty KualiForm.editingMode['viewOnly'])}" >
      <html:hidden property="document.correctionDebitTotalAmount"/>
      <html:hidden property="document.correctionCreditTotalAmount"/>
      <html:hidden property="document.correctionRowCount"/>
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
    <c:if test="${KualiForm.persistedOriginEntriesMissing && KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.document.correctionInputGroupId}">
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
    <c:if test="${KualiForm.restrictedFunctionalityMode && not KualiForm.persistedOriginEntriesMissing && KualiForm.editingMode['fullEntry']}" >
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
            <td align="left" valign="middle" class="subhead"><span class="subhead-left"></span>Select System and Edit Method</td>
          </tr>
          <tr>
            <td>
              <center>
                <html:select property="chooseSystem">
                  <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|CorrectionChooseSystemValuesFinder" label="label" value="key"/>
                </html:select>
                <html:hidden property="previousChooseSystem"/>
                <html:select property="editMethod">
                  <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|CorrectionEditMethodValuesFinder" label="label" value="key"/>
                </html:select>
                <html:hidden property="previousEditMethod"/>
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
                  <html:select property="document.correctionInputGroupId" size="10" >
                    <c:if test="${KualiForm.inputGroupIdFromLastDocumentLoadIsMissing and KualiForm.inputGroupId eq KualiForm.inputGroupIdFromLastDocumentLoad}">
                      <option value="<c:out value="${KualiForm.inputGroupIdFromLastDocumentLoad}"/>" selected="selected"><c:out value="${KualiForm.inputGroupIdFromLastDocumentLoad}"/> Document was last saved with this origin entry group selected.  Group is no longer in system.</option>
                    </c:if>
                    
                  <c:choose>
					<c:when test="${KualiForm.documentType == 'LLCP'}" >
						<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|labor|web|optionfinder|CorrectionLaborGroupEntriesFinder" label="label" value="key" />
                   	</c:when>
                   	<c:otherwise>
                   		<c:choose>
	                      <c:when test="${KualiForm.editMethod eq 'R'}">
    	                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|ProcessingCorrectionGroupEntriesFinder" label="label" value="key" />
        	              </c:when>
            	          <c:otherwise>
                	        <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|CorrectionGroupEntriesFinder" label="label" value="key" />
                    	  </c:otherwise>
                    	</c:choose>
                   	</c:otherwise>
                  </c:choose>
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                  </html:select>
                  
                  <html:hidden property="previousInputGroupId"/>
                  <br/><br/>
                  <c:if test="${KualiForm.editMethod eq 'R'}">
                    <html:image property="methodToCall.confirmDeleteDocument.anchor${currentTabIndex}" src="${ConfigProperties.externalizable.images.url}tinybutton-remgrpproc.gif" styleClass="tinybutton" alt="deleteDocument" title="Remove Group From Processing" />
                  </c:if>
                  <c:if test="${KualiForm.editMethod eq 'M' or KualiForm.editMethod eq 'C'}">
                    <html:image property="methodToCall.loadGroup.anchor${currentTabIndex}" src="${ConfigProperties.externalizable.images.url}tinybutton-loadgroup.gif" styleClass="tinybutton" alt="ShowAllEntries" title="Show All Entries"/>
                  </c:if>
                  <html:image property="methodToCall.saveToDesktop.anchor${currentTabIndex}" src="${ConfigProperties.externalizable.images.url}tinybutton-cpygrpdesk.gif" styleClass="tinybutton" alt="saveToDeskTop" title="Save To Desktop" onclick="excludeSubmitRestriction=true" />
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
          <div class="h2-container">
            <h2>Corrections File Upload</h2>
          </div>
          <table cellpadding=0 class="datatable" summary=""> 
            <tr>
              <td class="bord-l-b" style="padding: 4px;">
                <html:hidden property="document.correctionInputGroupId"/>
                <html:file size="30" property="sourceFile" />
                <html:image property="methodToCall.uploadFile.anchor${currentTabIndex}" src="${ConfigProperties.externalizable.images.url}tinybutton-loaddoc.gif" styleClass="tinybutton" alt="uploadFile" title="upload file"/>
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
      <c:if test="${KualiForm.restrictedFunctionalityMode && KualiForm.persistedOriginEntriesMissing && KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.document.correctionInputGroupId}">
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
      <c:if test="${KualiForm.chooseSystem != null and KualiForm.editMethod != null and KualiForm.dataLoadedFlag == true and !KualiForm.restrictedFunctionalityMode and !(KualiForm.persistedOriginEntriesMissing && KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.document.correctionInputGroupId)}" >
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
					        <c:out value="${column.columnTitle}"/><c:if test="${empty column.columnTitle}">$nbsp;</c:if>
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
    	                        <td><html:image property="methodToCall.addManualEntry.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" alt="edit"/></td>
        	                  </c:when>
            	              <c:otherwise>
                	            <td>
                    	          <html:hidden property="laborEntryForManualEdit.versionNumber"/>
                        	      <html:hidden property="laborEntryForManualEdit.entryId"/>
                            	  <html:hidden property="laborEntryForManualEdit.entryGroupId"/>
                              	<html:image property="methodToCall.saveManualEntry.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif" styleClass="tinybutton" alt="edit"/>
                            	</td>
                         	 </c:otherwise>
	                        </c:choose>
                        	<td><html:text property="laborEntryUniversityFiscalYear" size="5"/></td>
	                        <td><html:text property="laborEntryForManualEdit.chartOfAccountsCode" size="5"/></td>
    	                    <td><html:text property="laborEntryForManualEdit.accountNumber" size="7"/></td>
        	                <td><html:text property="laborEntryForManualEdit.subAccountNumber" size="7"/></td>
            	            <td><html:text property="laborEntryForManualEdit.financialObjectCode" size="5"/></td>
                	        <td><html:text property="laborEntryForManualEdit.financialSubObjectCode" size="6"/></td>
                    	    <td><html:text property="laborEntryForManualEdit.financialBalanceTypeCode" size="8"/></td>
	                        <td><html:text property="laborEntryForManualEdit.financialObjectTypeCode" size="6"/></td>
    	                    <td><html:text property="laborEntryForManualEdit.universityFiscalPeriodCode" size="6"/></td>
        	                <td><html:text property="laborEntryForManualEdit.financialDocumentTypeCode" size="10"/></td>
                	        <td><html:text property="laborEntryForManualEdit.financialSystemOriginationCode" size="6"/></td>
            	            <td><html:text property="laborEntryForManualEdit.documentNumber" size="14"/></td>
    	                    <td><html:text property="laborEntryTransactionLedgerEntrySequenceNumber" size="9"/></td>
	                        <td><html:text property="laborEntryForManualEdit.positionNumber" size="14"/></td>
        	                <td><html:text property="laborEntryForManualEdit.projectCode" size="7"/></td>
	                        <td><html:text property="laborEntryForManualEdit.transactionLedgerEntryDescription" size="11"/></td>
	                        <td><html:text property="laborEntryTransactionLedgerEntryAmount" size="7"/></td>
            	            <td><html:text property="laborEntryForManualEdit.transactionDebitCreditCode" size="9"/></td>
                	        <td><html:text property="laborEntryTransactionDate" size="12"/></td>
                    	    <td><html:text property="laborEntryForManualEdit.organizationDocumentNumber" size="12"/></td>
	                        <td><html:text property="laborEntryForManualEdit.organizationReferenceId" size="13"/></td>
        	                <td><html:text property="laborEntryForManualEdit.referenceFinancialDocumentTypeCode" size="10"/></td>
            	            <td><html:text property="laborEntryForManualEdit.referenceFinancialSystemOriginationCode" size="10"/></td>
                	        <td><html:text property="laborEntryForManualEdit.referenceFinancialDocumentNumber" size="9"/></td>
                    	    <td><html:text property="laborEntryFinancialDocumentReversalDate" size="8"/></td>
                            <td><html:text property="laborEntryForManualEdit.transactionEncumbranceUpdateCode" size="13"/></td>
	                        <td><html:text property="laborEntryTransactionPostingDate" size="14"/></td>
	                        <td><html:text property="laborEntryPayPeriodEndDate" size="14"/></td>
	                        <td><html:text property="laborEntryTransactionTotalHours" size="14"/></td>
	                        <td><html:text property="laborEntryPayrollEndDateFiscalYear" size="14"/></td>
	                        <td><html:text property="laborEntryForManualEdit.payrollEndDateFiscalPeriodCode" size="14"/></td>
	                        <td><html:text property="laborEntryForManualEdit.emplid" size="14"/></td>
	                        <td><html:text property="laborEntryEmployeeRecord" size="14"/></td>
	                        <td><html:text property="laborEntryForManualEdit.earnCode" size="14"/></td>
	                        <td><html:text property="laborEntryForManualEdit.payGroup" size="14"/></td>
	                        <td><html:text property="laborEntryForManualEdit.salaryAdministrationPlan" size="14"/></td>
	                        <td><html:text property="laborEntryForManualEdit.grade" size="14"/></td>
  	                        <td><html:text property="laborEntryForManualEdit.runIdentifier" size="14"/></td>
 	                        <td><html:text property="laborEntryForManualEdit.laborLedgerOriginalChartOfAccountsCode" size="14"/></td>
	                        <td><html:text property="laborEntryForManualEdit.laborLedgerOriginalAccountNumber" size="14"/></td>
	                        <td><html:text property="laborEntryForManualEdit.laborLedgerOriginalSubAccountNumber" size="14"/></td>
	                        <td><html:text property="laborEntryForManualEdit.laborLedgerOriginalFinancialObjectCode" size="14"/></td>
	                        <td><html:text property="laborEntryForManualEdit.laborLedgerOriginalFinancialSubObjectCode" size="14"/></td>
	                        <td><html:text property="laborEntryForManualEdit.hrmsCompany" size="14"/></td>
	                        <td><html:text property="laborEntryForManualEdit.setid" size="14"/></td>
    	 				  </c:when>
						  <c:otherwise>
						  <c:choose>
	                          <c:when test="${KualiForm.entryForManualEdit.entryId == 0}">
    	                        <td><html:image property="methodToCall.addManualEntry.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" alt="edit"/></td>
        	                  </c:when>
            	              <c:otherwise>
                	            <td>
                    	          <html:hidden property="entryForManualEdit.versionNumber"/>
                        	      <html:hidden property="entryForManualEdit.entryId"/>
                            	  <html:hidden property="entryForManualEdit.entryGroupId"/>
                              	<html:image property="methodToCall.saveManualEntry.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif" styleClass="tinybutton" alt="edit"/>
                            	</td>
                         	 </c:otherwise>
	                        </c:choose>
	                        
			            	<td><html:text property="entryUniversityFiscalYear" size="5"/></td>
	                        <td><html:text property="entryForManualEdit.chartOfAccountsCode" size="5"/></td>
    	                    <td><html:text property="entryForManualEdit.accountNumber" size="7"/></td>
        	                <td><html:text property="entryForManualEdit.subAccountNumber" size="7"/></td>
            	            <td><html:text property="entryForManualEdit.financialObjectCode" size="5"/></td>
                	        <td><html:text property="entryForManualEdit.financialSubObjectCode" size="6"/></td>
                    	    <td><html:text property="entryForManualEdit.financialBalanceTypeCode" size="8"/></td>
	                        <td><html:text property="entryForManualEdit.financialObjectTypeCode" size="6"/></td>
    	                    <td><html:text property="entryForManualEdit.universityFiscalPeriodCode" size="6"/></td>
        	                <td><html:text property="entryForManualEdit.financialDocumentTypeCode" size="10"/></td>
                	        <td><html:text property="entryForManualEdit.financialSystemOriginationCode" size="6"/></td>
            	            <td><html:text property="entryForManualEdit.documentNumber" size="14"/></td>
    	                    <td><html:text property="entryTransactionLedgerEntrySequenceNumber" size="9"/></td>
	                        <td><html:text property="entryForManualEdit.transactionLedgerEntryDescription" size="11"/></td>
        	                <td><html:text property="entryTransactionLedgerEntryAmount" size="7"/></td>
            	            <td><html:text property="entryForManualEdit.transactionDebitCreditCode" size="9"/></td>
                	        <td><html:text property="entryTransactionDate" size="12"/></td>
                    	    <td><html:text property="entryForManualEdit.organizationDocumentNumber" size="12"/></td>
	                        <td><html:text property="entryForManualEdit.projectCode" size="7"/></td>
    	                    <td><html:text property="entryForManualEdit.organizationReferenceId" size="13"/></td>
        	                <td><html:text property="entryForManualEdit.referenceFinancialDocumentTypeCode" size="10"/></td>
            	            <td><html:text property="entryForManualEdit.referenceFinancialSystemOriginationCode" size="10"/></td>
                	        <td><html:text property="entryForManualEdit.referenceFinancialDocumentNumber" size="9"/></td>
                    	    <td><html:text property="entryFinancialDocumentReversalDate" size="8"/></td>
                        	<td><html:text property="entryForManualEdit.transactionEncumbranceUpdateCode" size="13"/></td>
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
                <html:image property="methodToCall.manualEdit.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif" styleClass="tinybutton" alt="show edit" />
              </td>
            </c:if>
          </table>
        </div>
      </c:if>
    </kul:tab>
    <kul:tab tabTitle="Edit Options and Action" defaultOpen="true" tabErrorKey="Edit Options and Action">
      <c:if test="${KualiForm.deleteFileFlag == true or (KualiForm.dataLoadedFlag == false && !KualiForm.restrictedFunctionalityMode) or ((KualiForm.editMethod != 'C') and (KualiForm.editMethod != 'M' or KualiForm.editableFlag == false))}">
        <%-- this is the negation of the next if statement, since the form defaults to true for processInBatch, we need to pass along the real value for this attribute --%>
        <html:hidden property="processInBatch"/>
      </c:if>
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
                    <html:checkbox property="processInBatch" title="processInBatch" /> <STRONG> Process In Batch </STRONG> &nbsp; &nbsp; &nbsp; &nbsp;  
                    <input type="hidden" name="processInBatch${Constants.CHECKBOX_PRESENT_ON_FORM_ANNOTATION}" value="checkboxOnScreen"/>
                    <html:checkbox property="matchCriteriaOnly" title="matchCriteriaOnly"/> <STRONG> Output only records which match criteria? </STRONG>
                  </center>
                </td>
              </tr>
              <c:if test="${KualiForm.restrictedFunctionalityMode == false}">
                <tr>
                  <td>
                    <center>
                      <c:if test="${KualiForm.showOutputFlag == true}">
                        <strong>Show Input Group</strong>
                        <html:image property="methodToCall.showOutputGroup.anchor${currentTabIndex - 1}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" styleClass="tinybutton" alt="show Input Group" />
                      </c:if>
                      <c:if test="${KualiForm.showOutputFlag == false}">
                        <strong>Show Output Group</strong>
                        <html:image property="methodToCall.showOutputGroup.anchor${currentTabIndex - 1}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" styleClass="tinybutton" alt="show Output Group" />
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
                    <html:checkbox property="processInBatch" title="processInBatch" /> <STRONG> Process In Batch </STRONG>
                    <input type="hidden" name="processInBatch${Constants.CHECKBOX_PRESENT_ON_FORM_ANNOTATION}" value="checkboxOnScreen"/>
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
                  <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].versionNumber"/>
                  <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeGroupLineNumber"/>
                  <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].documentNumber"/>
                  <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaNextLineNumber"/>
                  <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeNextLineNumber"/>
                  <strong>Group:</strong>
                  <html:image property="methodToCall.removeCorrectionGroup.group${group.correctionChangeGroupLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="delete correction group" />
                </td>
              </tr>
              <tr style="border-bottom: 1px solid #333;"> 
                <td class="bord-l-b" style="padding: 4px; vertical-align: top;">
                  Field:
                  <html:select property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName">
                    <option value=""></option>
	                    <c:choose>
    	                	<c:when test="${KualiForm.documentType == 'LLCP'}" >
        	            		<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|labor|web|optionfinder|LaborOriginEntryFieldFinder" label="label" value="key"/>
							</c:when>
							<c:otherwise>
		            	        <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|OriginEntryFieldFinder" label="label" value="key"/>
		                	</c:otherwise>
		                </c:choose>
                  </html:select>
                  Operator:
                  <html:select property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode">
                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|SearchOperatorsFinder" label="label" value="key"/>
                  </html:select>
                  Value:
                  <html:text property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue"/>
                  <html:image property="methodToCall.addCorrectionCriteria.criteria${group.correctionChangeGroupLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" alt="Add Search Criteria" /><br>
                  <c:forEach items="${group.correctionCriteria}" var="criteria" varStatus="cc">
                    Field:
                    <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].versionNumber"/>
                    <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].documentNumber"/>
                    <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionChangeGroupLineNumber"/>
                    <html:select property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName">
                      
                      <c:choose>
    	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
        	          		<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|labor|web|optionfinder|LaborOriginEntryFieldFinder" label="label" value="key"/>
						</c:when>
						<c:otherwise>
		            	    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|OriginEntryFieldFinder" label="label" value="key"/>
		                </c:otherwise>
		              </c:choose>
                      
                    </html:select>
                    Operator:
                    <html:select property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode">
                      <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|SearchOperatorsFinder" label="label" value="key"/>
                    </html:select>
                    Value:
                    <html:text property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue"/>
                    <html:image property="methodToCall.removeCorrectionCriteria.criteria${group.correctionChangeGroupLineNumber}-${criteria.correctionCriteriaLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="delete search criterion" />
                    <br>
                  </c:forEach>
                </td>
                <td class="bord-l-b" style="padding: 4px; vertical-align: top;">
                  Field:
                  <html:select property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldName">
                    <option value=""></option>
                     <c:choose>
    	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
							<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|labor|web|optionfinder|LaborOriginEntryFieldFinder" label="label" value="key"/>
						</c:when>
						<c:otherwise>
		                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|OriginEntryFieldFinder" label="label" value="key"/>
		                </c:otherwise>
		              </c:choose>

                  </html:select>
                  Replacement Value:
                  <html:text property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldValue"/>
                  <html:image property="methodToCall.addCorrectionChange.change${group.correctionChangeGroupLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add replacement specification" styleClass="tinybutton" /> <br>
                  <c:forEach items="${group.correctionChange}" var="change">
                    Field:
                    <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].versionNumber"/>
                    <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].documentNumber"/>
                    <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionChangeGroupLineNumber"/>
                    <html:select property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName">
				      <c:choose>
    	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
							<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|labor|web|optionfinder|LaborOriginEntryFieldFinder" label="label" value="key"/>
						</c:when>
						<c:otherwise>
		                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|OriginEntryFieldFinder" label="label" value="key"/>
		                </c:otherwise>
		              </c:choose>
                    </html:select>
                    Replacement Value:
                    <html:text property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue"/>
                    <html:image property="methodToCall.removeCorrectionChange.change${group.correctionChangeGroupLineNumber}-${change.correctionChangeLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="delete search specification" styleClass="tinybutton" />
                    <br>
                  </c:forEach>
                </td>
              </tr>
            </c:forEach>
            <tr>
              <td colspan="2" align="left" class="bord-l-b" style="padding: 4px; vertical-align: top;"> 
                <center>
                  <STRONG>Add Groups </STRONG>
                  <html:image property="methodToCall.addCorrectionGroup.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add correction group" styleClass="tinybutton" />
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
                  <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].versionNumber"/>
                  <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeGroupLineNumber"/>
                  <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].documentNumber"/>
                  <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaNextLineNumber"/>
                  <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeNextLineNumber"/>
                  <c:forEach items="${group.correctionCriteria}" var="criteria" varStatus="cc">
                    Field:
                    <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].versionNumber"/>
                    <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].documentNumber"/>
                    <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionChangeGroupLineNumber"/>
                    <html:select property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName">
                     <c:choose>
    	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
							<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|labor|web|optionfinder|LaborOriginEntryFieldFinder" label="label" value="key"/>
						</c:when>
						<c:otherwise>
		                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|OriginEntryFieldFinder" label="label" value="key"/>
		                </c:otherwise>
		              </c:choose> 
                    </html:select>
                    Operator:
                    <html:select property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode">
                      <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|SearchOperatorsFinder" label="label" value="key"/>
                    </html:select>
                    Value:
                    <html:text property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue"/>
                    <html:image property="methodToCall.removeCorrectionCriteria.criteria${group.correctionChangeGroupLineNumber}-${criteria.correctionCriteriaLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="Remove Search Criteria" />
                    <br>
                  </c:forEach>
                  Field:
                  <html:select property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName">
                    <option value=""></option>
                     <c:choose>
    	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
							<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|labor|web|optionfinder|LaborOriginEntryFieldFinder" label="label" value="key"/>
						</c:when>
						<c:otherwise>
		                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|OriginEntryFieldFinder" label="label" value="key"/>
		                </c:otherwise>
		              </c:choose> 
                    
                    
                  </html:select>
                  Operator:
                  <html:select property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode">
                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|SearchOperatorsFinder" label="label" value="key"/>
                  </html:select>
                  Value:
                  <html:text property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue"/>
                  <html:image property="methodToCall.addCorrectionCriteria.criteria${group.correctionChangeGroupLineNumber}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" alt="Add Search Criteria" />
                </c:forEach>
              </td>
            </tr>
            <tr>
              <td>
                <center>
                  <c:if test="${KualiForm.showOutputFlag == true}">
                    <strong>Show All Entries</strong>
                    <html:image property="methodToCall.searchCancelForManualEdit.anchor${currentTabIndex - 3}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" styleClass="tinybutton" alt="Show Matching Entries" />
                  </c:if>
                  <c:if test="${KualiForm.showOutputFlag == false}">
                    <strong>Show Matching Entries</strong>
                    <html:image property="methodToCall.searchForManualEdit.anchor${currentTabIndex - 3}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" styleClass="tinybutton" alt="Show All Entries" />
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
    <html:hidden property="correctionDocument.correctionTypeCode"/>
    <html:hidden property="correctionDocument.correctionSelection" />
    <html:hidden property="correctionDocument.correctionFileDelete"/>
    <html:hidden property="correctionDocument.correctionInputFileName"/>
    <html:hidden property="correctionDocument.correctionInputGroupId"/>
    <html:hidden property="correctionDocument.correctionOutputGroupId"/>
    <c:forEach var="group" items="${KualiForm.document.correctionChangeGroup}"> 
      <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].versionNumber"/>
      <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeGroupLineNumber"/>
      <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].documentNumber"/>
      <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaNextLineNumber"/>
      <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeNextLineNumber"/>
      <c:forEach items="${group.correctionCriteria}" var="criteria" varStatus="cc">
        <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].versionNumber"/>
        <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].documentNumber"/>
        <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionChangeGroupLineNumber"/>
        <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName"/>
        <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode"/>
        <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue"/>
      </c:forEach>
      <c:forEach items="${group.correctionChange}" var="change">
        <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].versionNumber"/>
        <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].documentNumber"/>
        <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionChangeGroupLineNumber"/>
        <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName"/>
        <html:hidden property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue"/>
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
        <c:if test="${KualiForm.document.correctionInputGroupId != null}">
          <tr>
            <td width="20%" align="left" valign="middle" > Input Group ID: </td> 
            <td align="left" valign="middle" > <c:out value="${KualiForm.document.correctionInputGroupId}" /></td>
          </tr>
        </c:if>
        <tr>
          <td width="20%" align="left" valign="middle" > Output Group ID: </td> 
          <c:if test="${KualiForm.document.correctionOutputGroupId != null}">
            <td align="left" valign="middle" > <c:out value="${KualiForm.document.correctionOutputGroupId}" /></td>
          </c:if>
          <c:if test="${KualiForm.document.correctionOutputGroupId == null}">
            <c:if test="${KualiForm.document.correctionTypeCode eq 'R'}">
              <td align="left" valign="middle" ><c:out value="${Constants.NOT_AVAILABLE_STRING}"/></td>
            </c:if>
            <c:if test="${KualiForm.document.correctionTypeCode ne 'R'}">
              <td align="left" valign="middle" > The output group ID is unavailable until the document has a status of FINAL.</td>
            </c:if>
          </c:if>
        </tr>
        <c:if test="${KualiForm.document.correctionInputFileName != null}">
          <tr>
            <td width="20%" align="left" valign="middle" > Input File Name: </td> 
            <td align="left" valign="middle" > <c:out value="${KualiForm.document.correctionInputFileName}" /></td>
          </tr>
        </c:if>
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
              <html:checkbox property="processInBatch" title="processInBatch" disabled="true"/> <STRONG> Process In Batch </STRONG> &nbsp; &nbsp; &nbsp; &nbsp;  
              <c:if test="${KualiForm.document.correctionTypeCode == 'C'}" >
                <html:checkbox property="matchCriteriaOnly" alt="matchCriteriaOnly" disabled="true"/> <STRONG> Output only records which match criteria? </STRONG>
                <html:hidden property="matchCriteriaOnly"/><%--disabled checkbox above is not submitted, so we create a hidden input --%>
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
                  Field:
                  <html:select disabled="true" property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName">
                  <c:choose>
   	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
						<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|labor|web|optionfinder|LaborOriginEntryFieldFinder" label="label" value="key"/>
					</c:when>
					<c:otherwise>
	                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|OriginEntryFieldFinder" label="label" value="key"/>
	                </c:otherwise>
	              </c:choose>
                  </html:select>
                  Operator:
                  <html:select disabled="true" property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode">
                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|SearchOperatorsFinder" label="label" value="key"/>
                  </html:select>
                  Value:
                  <html:text disabled="true" property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue"/>
                  <br>
                </c:forEach>
              </td>
              <td class="bord-l-b" style="padding: 4px; vertical-align: top;">
                <c:forEach items="${group.correctionChange}" var="change">
                  Field:
                  <html:select disabled="true" property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName">
	              <c:choose>
   	              	<c:when test="${KualiForm.documentType == 'LLCP'}" >
						<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|labor|web|optionfinder|LaborOriginEntryFieldFinder" label="label" value="key"/>
					</c:when>
					<c:otherwise>
	                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|module|gl|web|optionfinder|OriginEntryFieldFinder" label="label" value="key"/>
	                </c:otherwise>
	              </c:choose>
                  </html:select>
                  Replacement Value:
                  <html:text disabled="true" property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue"/>
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
  <kul:panelFooter/>
  <kul:documentControls transactionalDocument="false" />
</kul:page>