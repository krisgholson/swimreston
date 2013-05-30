package org.rsta.swimreston.client;

import java.util.List;
import org.rsta.swimreston.shared.Record;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("records")
public interface RecordsService extends RemoteService {
	List<Record> getRecords();
}
