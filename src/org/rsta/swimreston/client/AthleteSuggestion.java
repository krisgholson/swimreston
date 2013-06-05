package org.rsta.swimreston.client;

import org.rsta.swimreston.shared.Athlete;

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public interface AthleteSuggestion extends Suggestion {

	Athlete getAthlete();

}
