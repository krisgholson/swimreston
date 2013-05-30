package org.rsta.swimreston.shared;

import java.util.Date;

public class RecordDetail extends Record {

	private static final long serialVersionUID = 1L;

	private Integer lowAge;
	private Integer highAge;
	private String text;
	private Date date;
	private Integer time;
	private String team;
	private Integer distance;
	private Integer stroke;
	private String sex;
	private Boolean relay;

	public Integer getLowAge() {
		return lowAge;
	}

	public void setLowAge(Integer lowAge) {
		this.lowAge = lowAge;
	}

	public Integer getHighAge() {
		return highAge;
	}

	public void setHighAge(Integer highAge) {
		this.highAge = highAge;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public Integer getStroke() {
		return stroke;
	}

	public void setStroke(Integer stroke) {
		this.stroke = stroke;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Boolean isRelay() {
		return relay;
	}

	public void setRelay(Boolean relay) {
		this.relay = relay;
	}

}
