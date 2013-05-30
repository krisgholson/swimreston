package org.rsta.swimreston.client;

import java.util.List;

import org.rsta.swimreston.shared.Record;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RecordsServiceAsync {
	void getRecords(AsyncCallback<List<Record>> callback);
}
