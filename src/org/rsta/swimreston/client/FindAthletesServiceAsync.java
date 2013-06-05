package org.rsta.swimreston.client;

import java.util.List;

import org.rsta.swimreston.shared.Athlete;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FindAthletesServiceAsync {

	void findAthletes(String namePart, AsyncCallback<List<Athlete>> callback);

}
