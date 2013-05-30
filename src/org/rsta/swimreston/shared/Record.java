package org.rsta.swimreston.shared;

import java.io.Serializable;

public class Record implements Serializable {

	private static final long serialVersionUID = 1L;

	public Record() {
		super();
	}

	public Record(String name, String description) {
		super();
		this.setName(name);
		this.setDescription(description);
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private String description;

}
