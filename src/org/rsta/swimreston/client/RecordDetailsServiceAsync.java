package org.rsta.swimreston.client;

import java.util.List;

import org.rsta.swimreston.shared.RecordDetail;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RecordDetailsServiceAsync {

	void getRecordDetails(String name,
			AsyncCallback<List<RecordDetail>> callback);

}
