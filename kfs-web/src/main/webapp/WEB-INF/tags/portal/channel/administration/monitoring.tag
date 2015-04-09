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

<channel:portalChannelTop channelTitle="Monitoring" />
<div class="body">
	<strong>Service Bus</strong></br>
   	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Message Queue" url="${ConfigProperties.ksb.client.url}/${ConfigProperties.message.queue.url}" /></li>
		<li><portal:portalLink displayTitle="true" title="Service Registry" url="${ConfigProperties.ksb.server.url}/${ConfigProperties.service.registry.url}" /></li>
		<li><portal:portalLink displayTitle="true" title="Thread Pool" url="${ConfigProperties.ksb.client.url}/${ConfigProperties.thread.pool.url}" /></li>
		<li><portal:portalLink displayTitle="true" title="Quartz" url="${ConfigProperties.ksb.client.url}/Quartz.do" /></li>
		<li><portal:portalLink displayTitle="true" title="Security Management" url="${ConfigProperties.ksb.client.url}/JavaSecurityManagement.do" /></li>
	</ul>
	<strong>Workflow</strong></br>
   	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Document Operation" url="${ConfigProperties.kew.url}/DocumentOperation.do" /></li>
		<li><portal:portalLink displayTitle="true" title="Statistics Report" url="${ConfigProperties.kew.url}/Stats.do" /></li>
	</ul>	
</div>
<channel:portalChannelBottom />

