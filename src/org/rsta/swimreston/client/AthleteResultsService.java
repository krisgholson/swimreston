package org.rsta.swimreston.client;

import java.util.List;

import org.rsta.swimreston.shared.Athlete;
import org.rsta.swimreston.shared.Result;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("athleteResults")
public interface AthleteResultsService extends RemoteService {
	List<Result> findAthleteResults(Athlete athlete);
}
