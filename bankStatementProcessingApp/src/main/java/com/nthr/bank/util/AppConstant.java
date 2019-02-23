package com.nthr.bank.util;

public class AppConstant {
	
	public final static  String ROOT_DIRECTORY = "rootdirectory";
	public final static  String PROCESSING_FILE_NAME = "records";
	public static final String SUCCESS_FLDR = "success";
	public static final String FAILURE_FLDR = "failure";
	public static final String BACKUP_FLDR = "backup";
	
	public static final String CSV_EXTN = ".csv";
	public static final String XML_EXTN = ".xml";
	public static final String ZIP_EXTN = ".zip";
	
	public static final String CSV_FLDR = SUCCESS_FLDR + "/csv/";
	public static final String XML_FLDR = SUCCESS_FLDR + "/xml/";
	
	//ERRORS
	public final static  String E000 = "E000";
	public final static  String E001 = "E001";
	
	public final static String DUPLICATE_TRANSACTION_ID="Duplicte Transaction ID";
	public final static String ERROR_IN_BALANCE="Error in End Balance";
	
	public final static String MESSAGE_UPON_DUPLLICATE = "Found duplicate records, track files with <accountnumber>_";
	public final static String MESSAGE_UPON_SUCCESS = "Success. Track files with the files with <accountnumber>_";
}
