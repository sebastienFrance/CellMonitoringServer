package com.seb.imonserver.datamodel;

public class Alarm {
	
	private int _severity;
	private String _probableCause;
	private long _dateAndTime;
	private int _alarmType;
	private String _additionalText;
	private Boolean _isAcknowledged;
	
	public Alarm(int severity, String probableCause, long dateAndTime, int alarmType, String additionalText, Boolean isAcknowledged) {
		_severity = severity;
		_probableCause = probableCause;
		_dateAndTime = dateAndTime;
		_alarmType = alarmType;
		_additionalText = additionalText;
		_isAcknowledged = isAcknowledged;
	}
	
	public int getSeverity() {
		return _severity;
	}
	
	public String getProbableCause() {
		return _probableCause;
	}
	
	public long getDateAndTime() {
		return _dateAndTime;
	}
	
	public int getAlarmType() {
		return _alarmType;
	}
	
	public String getAdditionalText() {
		return _additionalText;
	}
	
	public Boolean getIsAcknowledged() {
		return _isAcknowledged;
	}

}
