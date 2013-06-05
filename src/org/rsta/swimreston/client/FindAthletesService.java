package org.rsta.swimreston.client;

import java.util.List;

import org.rsta.swimreston.shared.Athlete;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("findAthletes")
public interface FindAthletesService extends RemoteService {
	List<Athlete> findAthletes(String namePart);
}
