package com.bookApi.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class BookToLend extends Book {

    private Date registrationDate;
    private String status;

    @OneToMany(mappedBy = "bookToLend")
    private List<Loan> loans;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

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

	public List<Loan> getLoans() {
		return loans;
	}

	public void setLoans(List<Loan> loans) {
		this.loans = loans;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

}