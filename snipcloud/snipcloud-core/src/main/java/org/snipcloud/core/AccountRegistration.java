package org.snipcloud.core;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlRootElement
@XmlType(factoryMethod="newInstance")
public class AccountRegistration {
	@XmlElement
	private User user;
	@XmlElement
	private String password;
	
	public AccountRegistration(User user, String password) {
		this.user = user;
		this.password = password;
	}

	public User getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "AccountRegistration [user=" + user + ", password=" + password
				+ "]";
	}
	
	public void checkPassword() throws RegistrationError {
		if (password.length() < 8) {
			throw new RegistrationError("Password too short");
		}
		if (password.matches("[a-zA-Z ]*")) {
			throw new RegistrationError("Password must contain at least one digit or special character");
		}
	}


	//Xml jaxb magic
	//Do not call any of these methods
	
	private AccountRegistration() {
		user = null;
		password = null;
	}
	
	public static AccountRegistration newInstance() {
		return new AccountRegistration();
	}
}
