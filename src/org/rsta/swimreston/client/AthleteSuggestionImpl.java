package org.rsta.swimreston.client;

import org.rsta.swimreston.shared.Athlete;
import org.rsta.swimreston.shared.Utilities;

public class AthleteSuggestionImpl implements AthleteSuggestion {

	private Athlete athlete;

	public AthleteSuggestionImpl(Athlete athlete) {
		this.athlete = athlete;
	}

	@Override
	public String getDisplayString() {
		return Utilities.formatAthleteDisplay(athlete);
	}

	@Override
	public String getReplacementString() {
		return Utilities.formatAthleteName(athlete.getFirstName(),
				athlete.getMiddleInitial(), athlete.getLastName(),
				athlete.getAge());
	}

	@Override
	public Athlete getAthlete() {
		return athlete;
	}

}
