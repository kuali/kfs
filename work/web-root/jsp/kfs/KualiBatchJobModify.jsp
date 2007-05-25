<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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
<style type="text/css">
	TABLE.positioningTable {
		border: 0!important;
 		margin : 0!important;
		padding : 0!important;
		empty-cells : show;
		border-collapse : collapse;
 	}
	TABLE.positioningTable TD {
		border: 0!important;
 		margin : 0!important;
		padding : 0!important;
 	}
</style>
<c:set var="lookupReturnLink" value="<a href=\"lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.batch.BatchJobStatus&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true&conversionFields=name:name,group:group\">Return to Lookup</a>" />
<kul:page showDocumentInfo="false"
	headerTitle="Modify Batch Job" docTitle="Modify Batch Job"
	transactionalDocument="false" htmlFormAction="batchModify"
	errorKey="*">
	<div style="text-align: right;">
		${lookupReturnLink}
	</div>
	<html:hidden property="refreshCaller" />
	<input type="hidden" name="name" value="${job.name}" />
	<input type="hidden" name="group" value="${job.group}" />
	<kul:tabTop tabTitle="Job Info" defaultOpen="true">
		<div class="tab-container" align="center">
	      <table width="100%" cellpadding=0 cellspacing=0 class="datatable">
			<tr>
				<td colspan="4" class="tab-subhead">Job Info</td>
				<c:if test="${canRunJob}">
					<td colspan="1" class="tab-subhead">Running</td>
				</c:if>
				<c:if test="${canSchedule || canUnschedule || canStopJob}">
					<td colspan="1" class="tab-subhead">Other Commands</td>
				</c:if>
			</tr>
	      	<tr>
				<td><kul:htmlAttributeLabel attributeEntryName="DataDictionary.BatchJobStatus.attributes.name" /></td>
				<td>${job.name}&nbsp;</td>
				<td><kul:htmlAttributeLabel attributeEntryName="DataDictionary.BatchJobStatus.attributes.group" /></td>
				<td>${job.group}&nbsp;</td>
				<c:if test="${canRunJob}">
					<td rowspan="2">
						<c:if test="${job.group == 'unscheduled' && !job.running}">
							<table class="positioningTable">
								<tr>
									<td>Start Step: </td>
									<td><input type="text" name="startStep" value="1" size="3" /></td>
								</tr>
								<tr>
									<td>End Step: </td>
									<td><input type="text" name="endStep" value="${job.numSteps}" size="3" /></td>
								</tr>
								<tr>
									<td>Start Date/Time: </td>
									<td>
										<input type="text" name="startTime" id="startTime" value="" />
										<img src="${ConfigProperties.kr.externalizable.images.url}cal.gif" id="startTime_datepicker" style="cursor: pointer;" alt="Date selector" title="Date selector" onmouseover="this.style.background='#F00';" onmouseout="this.style.background='#FFF';" />    
										(format: mm/dd/yy hh:mm)
									</td>
								</tr>
								<tr>
									<td>Results Email Address: </td>
									<td>
										<input type="text" name="emailAddress" id="emailAddress" value="" />
										<button type="button" onclick="this.form.emailAddress.value = '${userEmailAddress}';">Mail To Me</button>
									</td>
								</tr>
								<tr>
									<td></td>
									<td><button type="submit" name="methodToCall" value="runJob">Run Job</button></td>
								</tr>
							</table>
							<script type="text/javascript">
								var today = new Date();
								var years = new Array(1);
								years[0] = today.getFullYear();
								years[1] = today.getFullYear() + 1;
								Calendar.setup(
								{
							    	inputField : "startTime", // ID of the input field
							    	ifFormat : "%m/%d/%Y %H:%M", // the date format
							    	button : "startTime_datepicker", // ID of the button
							    	showsTime: true,
							    	dateStatusFunc: function(date) { return date <= today; },
							    	range: years
							    }
							    );
							</script>
						</c:if>	
						&nbsp;				
					</td>
				</c:if>
				<c:if test="${canSchedule || canUnschedule || canStopJob}">
					<td rowspan="2">
						<c:if test="${canSchedule && !job.scheduled}">
							<button type="submit" name="methodToCall" value="schedule">Add to Standard Schedule</button>
							<br />
						</c:if>				
						<c:if test="${canUnschedule && job.scheduled}">
							<button type="submit" name="methodToCall" value="unschedule">Remove From Standard Schedule</button>
							<br />
						</c:if>
						<c:if test="${canStopJob && job.running}">
							<button type="submit" name="methodToCall" value="stopJob">Stop Running Job</button>
							<br />
						</c:if>
						&nbsp;
					</td>
				</c:if>
			</tr>
			<tr>
				<td><kul:htmlAttributeLabel attributeEntryName="DataDictionary.BatchJobStatus.attributes.status" /></td>
				<td>${job.status}&nbsp;</td>
				<td>Logs URL:</td>
				<td><a href="${ConfigProperties.htdocs.logs.url}" target="_blank">${ConfigProperties.htdocs.logs.url}</a></td>
			</tr>
		</table>
	  </div>		
	</kul:tabTop>
	<kul:tab tabTitle="Steps" defaultOpen="true">
		<div class="tab-container" align="center">
	      <table width="100%" cellpadding=0 cellspacing=0 class="datatable">
			<tr>
				<th class="tab-subhead">#</th>
				<td class="tab-subhead">Name</td>
			</tr>
	      	<c:forEach items="${job.steps}" var="step" varStatus="status">
		      	<tr>
		      		<th>${status.count}</th>
					<td>${step.name}&nbsp;</td>
				</tr>
			</c:forEach>
		</table>
	  </div>		
	</kul:tab>
	<kul:tab tabTitle="Dependencies" defaultOpen="true">
		<div class="tab-container" align="center">
	      <table width="100%" cellpadding=0 cellspacing=0 class="datatable">
	      	<c:forEach items="${job.dependencies}" var="dep">
		      	<tr>
					<td>${dep.key} (${dep.value})&nbsp;</td>
				</tr>
			</c:forEach>
		</table>
	  </div>		
	</kul:tab>
	<kul:panelFooter />
</kul:page>
