package com.nthr.bank.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.nthr.bank.dto.ErrorRecord;
import com.nthr.bank.dto.Record;
import com.nthr.bank.exception.StmtProcessingException;
import com.nthr.bank.util.AppConstant;
import com.nthr.bank.util.FileManagerUtil;

@Service
@Configuration
@PropertySources(
		{ 
			@PropertySource(value = "classpath:appenv.properties"),
			@PropertySource("classpath:errorcode.properties") 
		}
	)

/**
 * Simple Service that validates the INPUT(.csv)FILE 
 * and bucket the records into multiple folders and files.
 * Use Case:
 * 	>If Duplicate Records Found, 
 * 		Details of the Error will be served to the caller
 * 		Details of the Error will be inserted into various file format (.csv,.xml,.zip)
 * 		Account number will be the file name while creating such error files.
 *  >Take backup of the originally served file.
 *  
 * @author Ganesh K Thiagarajan
 *
 */
public class StatementProcessingService {

	@Autowired
	private Environment env;

	@Autowired
	private FileManagerUtil fileReader;

	/**
	 * This method is responsible for creating files based on the input file
	 * (Customer Records CSV file) 
	 * UseCase: Check for FILE EXIST If FILE NOT FOUND
	 * Throw FILE NOT FOUND EXCEPTION If File FOUND, Read the data and validate
	 * every individual Record. 
	 * If DUPLICATE Record found during validation, return error list
	 * 
	 * @param currentTimeInMillis - to track the file that is created
	 * @return
	 * @throws StmtProcessingException
	 */
	public List<ErrorRecord> processStatement(Long currentTimeInMillis) throws StmtProcessingException {
		List<ErrorRecord> errorList = new ArrayList<>();
		Map<String, List<Record>> customerRec = null;
		String rootDirectory = "";
		
			rootDirectory = env.getProperty(AppConstant.ROOT_DIRECTORY);
			String fileName = rootDirectory + AppConstant.PROCESSING_FILE_NAME + AppConstant.CSV_EXTN;
			File orginalFile = new File(fileName);
			if (!orginalFile.exists()) {
				throw new StmtProcessingException(AppConstant.E000, env.getProperty(AppConstant.E000));
			}

			try {
				// Read the File Content and store the details to LIST
				List<Record> recordList = fileReader.getRecordList(fileName);
				// Move the ORGINAL File to another Director for Back UP by appending a TIME STAMP.
				String bkFilePath = rootDirectory + AppConstant.BACKUP_FLDR + "/" + AppConstant.PROCESSING_FILE_NAME + "_"+ currentTimeInMillis + AppConstant.CSV_EXTN;
				orginalFile.renameTo(new File(bkFilePath));
				//validate the recordList to get customer record and prepare error list
				customerRec = validateRecords(recordList, errorList, currentTimeInMillis);
				//write to various files
				fileReader.writeToFiles(rootDirectory, customerRec, currentTimeInMillis);
				// write to failure folder
				fileReader.errorReport(rootDirectory, errorList,currentTimeInMillis);
			} catch (Exception e) {
				e.printStackTrace();
				throw new StmtProcessingException(AppConstant.E001, e.getMessage().toUpperCase());
			}

		return errorList;
	}
	
	/**
	 * This method is responsible for validating the record list
	 * Validation includes identifying 
	 * 	duplicate records based on the transaction ID, 
	 * 	account balance
	 * @param recordList
	 * @param errorList
	 * @return
	 */
	private Map<String, List<Record>> validateRecords(List<Record> recordList, List<ErrorRecord> errorList, Long currentTimeInMillis) {
		Map<Long, Record> recordMap = new HashMap<>();
		Set<Long> duplicateSet = new HashSet<>();
		recordList.forEach(item -> {
			if (duplicateSet.contains(item.getReference())) {
				errorList.add(buildErrorDetails(item, AppConstant.DUPLICATE_TRANSACTION_ID,currentTimeInMillis));
			} else {
				if (recordMap.get(item.getReference()) == null) {
					recordMap.put(item.getReference(), item);
					if (validateActBalance(item, currentTimeInMillis) != null)
						errorList.add(validateActBalance(item, currentTimeInMillis));
				} else {
					Record record = recordMap.get(item.getReference());
					duplicateSet.add(item.getReference());
					errorList.add(buildErrorDetails(record, AppConstant.DUPLICATE_TRANSACTION_ID,currentTimeInMillis));
					recordMap.remove(item.getReference());
					errorList.add(buildErrorDetails(item, AppConstant.DUPLICATE_TRANSACTION_ID,currentTimeInMillis));
				}
			}
		});
		return recordMap.values().stream().collect(Collectors.groupingBy(Record::getAccountNumber));
	}
	
	/** 
	 * This method is responsible for validating the account balance for a given Record (Item)
	 * @param record
	 * @return
	 */
	private ErrorRecord validateActBalance(Record record, Long currentTimeInMillis) {
		BigDecimal calcEndBalance = record.getStartBalance().add(record.getMutation());
		if (calcEndBalance.compareTo(record.getEndBalance()) != 0) {
			buildErrorDetails(record, AppConstant.ERROR_IN_BALANCE,currentTimeInMillis);
		}
		return null;
	}
	
	/**
	 * This method is responsible for preparing the Error Record details.
	 * @param record
	 * @param msg
	 * @return
	 */
	private ErrorRecord buildErrorDetails(Record record, String msg, Long currentTimeInMillis) {
		ErrorRecord errorRecord = new ErrorRecord();
		errorRecord.setReference(record.getReference());
		errorRecord.setAccountNumber(record.getAccountNumber());
		errorRecord.setDescription(msg);
		errorRecord.setFileReference(record.getAccountNumber()+"_"+currentTimeInMillis);
		return errorRecord;
	}
	
	/**
	 * Simulator Method that validates the FOLDER PATH as per the ENV Details.
	 * For now we are simulating the validation.
	 * If Not found will create folders/SubFolders. 
	 */
	@PostConstruct
	private void  checkDirectoriesExist(){
		List<String> foldersList = new ArrayList<String>();
		String root = env.getProperty(AppConstant.ROOT_DIRECTORY);
		foldersList.add(root);
		foldersList.add(root + AppConstant.BACKUP_FLDR);
		foldersList.add(root + AppConstant.FAILURE_FLDR);
		foldersList.add(root + AppConstant.SUCCESS_FLDR);
		foldersList.add(root + AppConstant.CSV_FLDR);
		foldersList.add(root + AppConstant.XML_FLDR);
	
			for (String fldfPath : foldersList) {
				
				File fileName = new File(fldfPath);
				if (!fileName.exists()) {
					Path p1 = Paths.get(fldfPath);
					try {
						Files.createDirectories(p1);
						System.out.println(p1.getFileName() + " directory not found, hence creating");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		
			}
	}
}
