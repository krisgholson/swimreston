package org.rsta.swimreston.shared;

import java.io.Serializable;
import java.util.Date;

public class Meet implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private Date date;
	private String location;
	private Integer year;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

}
