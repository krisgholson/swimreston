package org.rsta.swimreston.shared;

import java.io.Serializable;

public class Result implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Meet meet;
	private Athlete athlete;
	private Boolean relay;
	private Team team;
	private Integer score;
	private Boolean noTime;
	private Integer age;
	private Integer distance;
	private Integer stroke;
	private Integer points;
	private Integer place;
	private Integer rank;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Meet getMeet() {
		return meet;
	}

	public void setMeet(Meet meet) {
		this.meet = meet;
	}

	public Athlete getAthlete() {
		return athlete;
	}

	public void setAthlete(Athlete athlete) {
		this.athlete = athlete;
	}

	public Boolean isRelay() {
		return relay;
	}

	public void setRelay(Boolean relay) {
		this.relay = relay;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Boolean isNoTime() {
		return noTime;
	}

	public void setNoTime(Boolean noTime) {
		this.noTime = noTime;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
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

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public Integer getPlace() {
		return place;
	}

	public void setPlace(Integer place) {
		this.place = place;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

}
