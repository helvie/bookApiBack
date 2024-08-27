package com.bookApi.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class BookRequested extends Book {

    private Date requestDate;
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requester;

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

	public User getRequester() {
		return requester;
	}

	public void setRequester(User requester) {
		this.requester = requester;
	}

}