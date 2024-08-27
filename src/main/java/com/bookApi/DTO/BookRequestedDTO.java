package com.bookApi.DTO;

import java.util.Date;

public class BookRequestedDTO extends BookDTO {
    
    private Date requestDate;
    private String status;
	public Date getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
    
}
