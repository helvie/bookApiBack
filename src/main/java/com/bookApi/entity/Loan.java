package com.bookApi.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date loanDate;
    private Date returnDate;
    private Date newLoanDate;
    private String status;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookToLend bookToLend;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private User borrower;

    @ManyToOne
    @JoinColumn(name = "lender_id")
    private User lender;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getLoanDate() {
		return loanDate;
	}

	public void setLoanDate(Date loanDate) {
		this.loanDate = loanDate;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public Date getNewLoanDate() {
		return newLoanDate;
	}

	public void setNewLoanDate(Date newLoanDate) {
		this.newLoanDate = newLoanDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BookToLend getBookToLend() {
		return bookToLend;
	}

	public void setBook(BookToLend bookToLend) {
		this.bookToLend = bookToLend;
	}

	public User getBorrower() {
		return borrower;
	}

	public void setBorrower(User borrower) {
		this.borrower = borrower;
	}

	public User getLender() {
		return lender;
	}

	public void setLender(User lender) {
		this.lender = lender;
	}

}