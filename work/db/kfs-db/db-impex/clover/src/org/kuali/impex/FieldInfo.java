/*
 * Copyright 2012 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.impex;

public class FieldInfo {
	protected String cloverFieldType;
	protected String columnName;
	protected int columnPosition;
	
	public FieldInfo(String columnName, String cloverFieldType, int columnPosition) {
		this.columnName = columnName;
		this.cloverFieldType = cloverFieldType;
	}

	public String getCloverFieldType() {
		return cloverFieldType;
	}
	public void setCloverFieldType(String cloverFieldType) {
		this.cloverFieldType = cloverFieldType;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getColumnPosition() {
		return columnPosition;
	}

	public void setColumnPosition(int columnPosition) {
		this.columnPosition = columnPosition;
	}
}
