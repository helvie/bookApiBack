package com.bookApi.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.Collection;
import java.util.Date;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    private String lastname;
    private String firstname;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role;
    
    public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Column(nullable = false)
    private boolean enabled; 

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToMany(mappedBy = "requester")
    private List<RequestedBook> requestedBooks;

    @OneToMany(mappedBy = "owner")
    private List<BookToLend> booksToLend;

    @OneToMany(mappedBy = "borrower")
    private List<Loan> loansAsBorrower;

    @OneToMany(mappedBy = "lender")
    private List<Loan> loansAsLender;

    @OneToMany(mappedBy = "sender")
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver")
    private List<Message> receivedMessages;

    public User() {}

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<RequestedBook> getRequestedBooks() {
        return requestedBooks;
    }

    public void setRequestedBooks(List<RequestedBook> requestedBooks) {
        this.requestedBooks = requestedBooks;
    }

    public List<BookToLend> getBooksToLend() {
        return booksToLend;
    }

    public void setBooksToLend(List<BookToLend> booksToLend) {
        this.booksToLend = booksToLend;
    }

    public List<Loan> getLoansAsBorrower() {
        return loansAsBorrower;
    }

    public void setLoansAsBorrower(List<Loan> loansAsBorrower) {
        this.loansAsBorrower = loansAsBorrower;
    }

    public List<Loan> getLoansAsLender() {
        return loansAsLender;
    }

    public void setLoansAsLender(List<Loan> loansAsLender) {
        this.loansAsLender = loansAsLender;
    }

    public List<Message> getSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(List<Message> sentMessages) {
        this.sentMessages = sentMessages;
    }

    public List<Message> getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(List<Message> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Implement your logic for roles/authorities here if needed
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
