/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.external.kc.dto;

import java.io.Serializable;
import java.util.Date;

import org.kuali.rice.core.api.util.type.KualiDecimal;

public class AwardBillingUpdateDto implements Serializable {

	private static final long serialVersionUID = -2561088105556250344L;

	private boolean doLastBillDateUpdate;
	private Date lastBillDate;
	private boolean restorePreviousBillDate;

	private boolean doLocCreationTypeUpdate;
	private String locCreationType;

	private boolean doAmountToDrawUpdate;
	private KualiDecimal amountToDraw;

	private boolean doLocReviewUpdate;
	private boolean locReviewIndicator;

	private boolean doFinalBilledUpdate;
	private boolean finalBilledIndicator;

	public boolean isDoLastBillDateUpdate() {
		return doLastBillDateUpdate;
	}
	public void setDoLastBillDateUpdate(boolean doLastBillDateUpdate) {
		this.doLastBillDateUpdate = doLastBillDateUpdate;
	}
	public Date getLastBillDate() {
		return lastBillDate;
	}
	public void setLastBillDate(Date lastBillDate) {
		this.lastBillDate = lastBillDate;
	}
	public boolean isRestorePreviousBillDate() {
		return restorePreviousBillDate;
	}
	public void setRestorePreviousBillDate(boolean restorePreviousBillDate) {
		this.restorePreviousBillDate = restorePreviousBillDate;
	}
	public boolean isDoLocCreationTypeUpdate() {
		return doLocCreationTypeUpdate;
	}
	public void setDoLocCreationTypeUpdate(boolean doLocCreationTypeUpdate) {
		this.doLocCreationTypeUpdate = doLocCreationTypeUpdate;
	}
	public String getLocCreationType() {
		return locCreationType;
	}
	public void setLocCreationType(String locCreationType) {
		this.locCreationType = locCreationType;
	}
	public boolean isDoAmountToDrawUpdate() {
		return doAmountToDrawUpdate;
	}
	public void setDoAmountToDrawUpdate(boolean doAmountToDrawUpdate) {
		this.doAmountToDrawUpdate = doAmountToDrawUpdate;
	}
	public KualiDecimal getAmountToDraw() {
		return amountToDraw;
	}
	public void setAmountToDraw(KualiDecimal amountToDraw) {
		this.amountToDraw = amountToDraw;
	}
	public boolean isDoLocReviewUpdate() {
		return doLocReviewUpdate;
	}
	public void setDoLocReviewUpdate(boolean doLocReviewUpdate) {
		this.doLocReviewUpdate = doLocReviewUpdate;
	}
	public boolean isLocReviewIndicator() {
		return locReviewIndicator;
	}
	public void setLocReviewIndicator(boolean locReviewIndicator) {
		this.locReviewIndicator = locReviewIndicator;
	}
	public boolean isDoFinalBilledUpdate() {
		return doFinalBilledUpdate;
	}
	public void setDoFinalBilledUpdate(boolean doFinalBilledUpdate) {
		this.doFinalBilledUpdate = doFinalBilledUpdate;
	}
	public boolean isFinalBilledIndicator() {
		return finalBilledIndicator;
	}
	public void setFinalBilledIndicator(boolean finalBilledIndicator) {
		this.finalBilledIndicator = finalBilledIndicator;
	}
}
