package org.rsta.swimreston.client;

import java.util.ArrayList;
import java.util.List;

import org.rsta.swimreston.shared.Athlete;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;

public class RpcSuggestAthlete extends SuggestOracle {

	private FindAthletesServiceAsync findAthletesService;

	public RpcSuggestAthlete(FindAthletesServiceAsync findAthletesService) {
		this.findAthletesService = findAthletesService;
	}

	@Override
	public void requestSuggestions(final Request request,
			final Callback callback) {

		String query = request.getQuery();
		// We only support one keyword for now.
		if (!query.contains(" ") && query.length() > 2) {

			findAthletesService.findAthletes(query,
					new AsyncCallback<List<Athlete>>() {

						@Override
						public void onSuccess(List<Athlete> result) {
							if (result != null) {
								ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
								for (final Athlete athlete : result) {
									suggestions.add(new AthleteSuggestionImpl(
											athlete));
								}
								Response resp = new Response(suggestions);
								callback.onSuggestionsReady(request, resp);
							}

						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Error while getting suggestions.");

						}
					});
		} else {
			Response resp = new Response(new ArrayList<Suggestion>());
			callback.onSuggestionsReady(request, resp);
		}
	}
}
