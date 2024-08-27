package com.bookApi.DTO;

import java.util.Date;

public class BookToLendDTO {
	
    private Date registrationDate;
    private String status;
	public Date getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
