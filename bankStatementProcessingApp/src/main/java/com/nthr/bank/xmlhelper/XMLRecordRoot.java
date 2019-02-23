package com.nthr.bank.xmlhelper;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "records")
public class XMLRecordRoot implements Serializable {
	private static final long serialVersionUID = 1L;
	public List<XMLRecord> getRecords() {
		return records;
	}
	public void setRecords(List<XMLRecord> records) {
		this.records = records;
	}
	private List<XMLRecord> records;
}
