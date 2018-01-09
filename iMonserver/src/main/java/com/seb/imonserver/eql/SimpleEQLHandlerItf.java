package com.seb.imonserver.eql;

public interface SimpleEQLHandlerItf {
	void processRow(String line);
	void processError();
	void endOfData();
}