package com.nthr.bank.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nthr.bank.exception.StmtProcessingException;
import com.nthr.bank.service.StatementProcessingService;
import com.nthr.bank.util.AppConstant;
import com.nthr.bank.util.ListResponse;

@RestController
@RequestMapping("/statement")
public class CustStmtProcessingController {
	@Autowired
	private StatementProcessingService processingService;

	@RequestMapping(path = "/process", method = RequestMethod.GET )
	@Produces("application/json")
	public @ResponseBody ListResponse processStatement() throws StmtProcessingException {
		List<?> responseList = null;
		ListResponse response = new ListResponse();
		try {
			Long currrentTimeInMillis = System.currentTimeMillis();
			responseList = processingService.processStatement(currrentTimeInMillis);
			response.setStatus(true);
			if(responseList!=null && responseList.size()>0) {
				response.setMessage(AppConstant.MESSAGE_UPON_DUPLLICATE+ currrentTimeInMillis);
			}else {
				response.setMessage(AppConstant.MESSAGE_UPON_SUCCESS + currrentTimeInMillis);
			}
			response.setList(responseList);
			response.setException(null);
		}catch (Exception e) {
			responseList = new ArrayList<>();
			response.setStatus(false);
			response.setMessage("failure");
			response.setList(responseList);
			response.setException(e.getMessage());
		}
		return response;
	}

}
