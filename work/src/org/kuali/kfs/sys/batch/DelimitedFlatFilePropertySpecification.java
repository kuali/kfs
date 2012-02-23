package org.kuali.kfs.sys.batch;


public class DelimitedFlatFilePropertySpecification extends AbstractFlatFilePropertySpecificationBase {
	private int lineSegmentIndex;

	public int getLineSegmentIndex() {
		return lineSegmentIndex;
	}
	
	public void setLineSegmentIndex(int lineSegmentIndex) {
		this.lineSegmentIndex = lineSegmentIndex;
	}
}
