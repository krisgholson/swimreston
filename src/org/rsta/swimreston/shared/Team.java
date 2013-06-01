package org.rsta.swimreston.shared;

import java.io.Serializable;

public class Team implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String code;
	private String name;
	private String shortName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}
