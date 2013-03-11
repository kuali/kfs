package org.kuali.kfs.module.ar.batch.report;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* @author mramawat
* FSKD-686 iu-custom
* 
* Store result of each lock box payment batch file.
*
*/

public class LockboxLoadFileResult {



	private String filename ;
	private List<String[]> messages;
	private Map lockboxMap;


	public LockboxLoadFileResult(String filename) {
		this.filename = filename;
		lockboxMap = new HashMap<String, LockboxLoadResult>();
		messages = new ArrayList<String[]>();
	}

	public LockboxLoadResult getOrAddLockbox(String lockboxNumber, LockboxLoadResult result) {
		if (!lockboxMap.containsKey(lockboxNumber)) {
			lockboxMap.put(lockboxNumber,result);
		}
		return (LockboxLoadResult)lockboxMap.get(lockboxNumber);
	}



	public String getFilename() {
		return filename;
	}

	public List<String[]> getMessages() {
		return messages;
	}

	public void addFileErrorMessage(String message) {
		this.messages.add(new String[] { LockboxLoadResult.getEntryTypeString(LockboxLoadResult.EntryType.ERROR), message });
	}

	public void addFileInfoMessage(String message) {
		this.messages.add(new String[] { LockboxLoadResult.getEntryTypeString(LockboxLoadResult.EntryType.INFO), message });
	}

	public Set<String> getLockboxNumbers() {
		return this.lockboxMap.keySet();
	}

	public LockboxLoadResult getLockbox(String lockboxNumber) {
		return (LockboxLoadResult)lockboxMap.get(lockboxNumber);
	}



	public Map getLockboxMap() {
		return lockboxMap;
	}



	public void setLockboxMap(Map lockboxMap) {
		this.lockboxMap = lockboxMap;
	}
}
