package com.nthr.bank.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import com.nthr.bank.dto.ErrorRecord;
import com.nthr.bank.dto.Record;
import com.nthr.bank.xmlhelper.XMLRecord;
import com.nthr.bank.xmlhelper.XMLRecordRoot;

/**
 * File Utility that is responsible for managing the CSV files 
 * @author Ganesh K Thiagarajan
 *
 */
@Component
public class FileManagerUtil {
	/**
	 * This method is responsible for returning the List of Record extracted from the file.
	 * Data from CSV file will be transformed into a RECORD and collected into a LIST.
	 * @param fileName - csv file name
	 * @return
	 * @throws Exception
	 */
	public List<Record> getRecordList(String fileName) throws Exception {
		File file = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(file));
		CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(br);
		List<Record> records = new ArrayList<>();
		for (CSVRecord record : parser) {
			Record recordVal = new Record();
			String refNumber = record.get("Reference").trim();
			recordVal.setReference(Long.parseLong(refNumber));
			recordVal.setAccountNumber(record.get("AccountNumber").trim());
			recordVal.setDescription(record.get("Description").trim());
			recordVal.setStartBalance(new BigDecimal(record.get("Start Balance")));
			recordVal.setMutation(new BigDecimal(record.get("Mutation").trim()));
			recordVal.setEndBalance(new BigDecimal(record.get("End Balance")));
			records.add(recordVal);
		}
		parser.close();
		return records;
	}
	/**
	 * This method is responsible for creating Failure Record with header details
	 * @param filePath
	 * @param errorList
	 * @param currentTimeInMillis
	 * @throws Exception
	 */
	public void errorReport(String filePath, List<ErrorRecord> errorList, Long currentTimeInMillis) throws Exception {
		File filecsv = new File(filePath + AppConstant.FAILURE_FLDR + "/" + AppConstant.FAILURE_FLDR + "_"
				+ currentTimeInMillis + AppConstant.CSV_EXTN);
		BufferedWriter writercsv = new BufferedWriter(new FileWriter(filecsv));
		@SuppressWarnings("resource")
		CSVPrinter csvPrinter = new CSVPrinter(writercsv,
				CSVFormat.DEFAULT.withHeader("reference", "description", "accountNumber"));
		for (ErrorRecord errorRecord : errorList) {
			csvPrinter.printRecord(errorRecord.getReference(), errorRecord.getDescription(),
					errorRecord.getAccountNumber());
		}
		csvPrinter.flush();
		csvPrinter.close();
	}
	/**
	 * This method is responsible for writing the customer record into appropriate files.
	 * @param filePath
	 * @param customerRec
	 * @param currentTimeInMillis
	 * @throws Exception
	 */
	public void writeToFiles(String filePath, Map<String, List<Record>> customerRec, Long currentTimeInMillis)
			throws Exception {
		for (String key : customerRec.keySet()) {
			List<Record> records = customerRec.get(key);
			File csvFile = new File(filePath + AppConstant.CSV_FLDR + key + "_"+ currentTimeInMillis + AppConstant.CSV_EXTN);
			BufferedWriter csvWritter = new BufferedWriter(new FileWriter(csvFile));
			CSVPrinter csvPrinter = new CSVPrinter(csvWritter, CSVFormat.DEFAULT.withHeader("reference", "accountNumber",
					"description", "startBalance", "mutation", "endBalance"));
			
			XMLRecordRoot recordJaxbRoot = new XMLRecordRoot();
			List<XMLRecord> recordJaxbs = new ArrayList<>();
			XMLRecord recordJaxb = null;
			for (Record record : records) {
				recordJaxb = new XMLRecord();
				csvPrinter.printRecord(record.getReference(), record.getAccountNumber(), record.getDescription(),
						record.getStartBalance(), record.getMutation(), record.getEndBalance());
				recordJaxb.setReference(record.getReference());
				recordJaxb.setAccountNumber(record.getAccountNumber());
				recordJaxb.setDescription(record.getDescription());
				recordJaxb.setAccountNumber(record.getAccountNumber());
				recordJaxb.setStartBalance(record.getStartBalance());
				recordJaxb.setEndBalance(record.getEndBalance());
				recordJaxb.setMutation(record.getMutation());
				recordJaxbs.add(recordJaxb);
				recordJaxbRoot.setRecords(recordJaxbs);
			}
			csvPrinter.flush();
			csvPrinter.close();
			String xmlFilePath = filePath + AppConstant.XML_FLDR + key + "_"+ currentTimeInMillis + AppConstant.XML_EXTN;
			File xmlFile = new File(xmlFilePath);
			writeToXML(recordJaxbRoot, xmlFile);
		}
	}
	/**
	 * Method is responsible for writing the Object Details into XML Format.
	 * @param dataObject
	 * @param file
	 * @throws Exception
	 */
	private void writeToXML(Object dataObject,File file)throws Exception{
        JAXBContext jaxbContext = JAXBContext.newInstance(dataObject.getClass());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(dataObject, file);
        marshaller.marshal(dataObject, System.out);
	}
}
