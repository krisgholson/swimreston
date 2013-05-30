package org.rsta.swimreston.client;

import java.util.List;

import org.rsta.swimreston.shared.RecordDetail;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("recordDetails")
public interface RecordDetailsService extends RemoteService {
	List<RecordDetail> getRecordDetails(String name) ;
}
