package org.rsta.swimreston.client;

import java.util.List;

import org.rsta.swimreston.shared.Athlete;
import org.rsta.swimreston.shared.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AthleteResultsServiceAsync {

	void findAthleteResults(Athlete athlete,
			AsyncCallback<List<Result>> callback);

}
