package org.rsta.swimreston.client;

import java.util.List;

import org.rsta.swimreston.shared.ApplicationConstants;
import org.rsta.swimreston.shared.Record;
import org.rsta.swimreston.shared.RecordDetail;
import org.rsta.swimreston.shared.Utilities;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Swimreston implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	private final RecordsServiceAsync recordsService = GWT
			.create(RecordsService.class);

	private final RecordDetailsServiceAsync recordDetailsService = GWT
			.create(RecordDetailsService.class);

	private final ApplicationConstants constants = GWT
			.create(ApplicationConstants.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
		dockLayoutPanel.addNorth(new HTML("Header Placeholder"), 2);
		dockLayoutPanel.addSouth(new HTML("Footer Placeholder"), 2);

		StackLayoutPanel stackPanel = new StackLayoutPanel(Unit.EM);
		final FlowPanel recordsPanel = new FlowPanel();
		HTML recordsHeader = new HTML(constants.records());

		stackPanel.add(recordsPanel, recordsHeader, 2);
		stackPanel.add(new HTML("Coming Soon!"), new HTML("Results"), 2);
		stackPanel.add(new HTML("Coming Soon!"), new HTML("Reports"), 2);

		dockLayoutPanel.addWest(stackPanel, 10);

		final VerticalPanel contentPanel = new VerticalPanel();

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.add(contentPanel);

		dockLayoutPanel.add(scrollPanel);

		RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();
		rootLayoutPanel.add(dockLayoutPanel);

		recordsService.getRecords(new AsyncCallback<List<Record>>() {

			@Override
			public void onFailure(Throwable caught) {
				recordsPanel.add(new HTML(SERVER_ERROR));
			}

			@Override
			public void onSuccess(List<Record> result) {
				for (final Record record : result) {
					final Button recordButton = new Button(record
							.getDescription());
					recordsPanel.add(recordButton);
					recordButton.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							recordDetailsService.getRecordDetails(
									record.getName(),
									new AsyncCallback<List<RecordDetail>>() {

										@Override
										public void onFailure(Throwable caught) {
											contentPanel.clear();
											contentPanel.add(new HTML(
													SERVER_ERROR));
										}

										@Override
										public void onSuccess(
												List<RecordDetail> result) {

											CellTable<RecordDetail> recordTable = new CellTable<RecordDetail>();
											RecordDetail firstRecord = result
													.get(0);

											String title = firstRecord
													.getDescription()
													+ " "
													+ constants.records();

											recordTable.setTitle(title);

											recordTable.setPageSize(result
													.size());

											TextColumn<RecordDetail> textColumn = new TextColumn<RecordDetail>() {
												@Override
												public String getValue(
														RecordDetail record) {
													return record.getText();
												}
											};

											TextColumn<RecordDetail> sexColumn = new TextColumn<RecordDetail>() {
												@Override
												public String getValue(
														RecordDetail record) {
													return record.getSex();
												}
											};

											TextColumn<RecordDetail> ageColumn = new TextColumn<RecordDetail>() {
												@Override
												public String getValue(
														RecordDetail record) {
													return record.getLowAge()
															+ "-"
															+ record.getHighAge();
												}
											};

											TextColumn<RecordDetail> eventColumn = new TextColumn<RecordDetail>() {
												@Override
												public String getValue(
														RecordDetail record) {

													String event = Utilities.formatEventName(
															record.getDistance(),
															record.getStroke(),
															record.isRelay());
													return event;
												}
											};

											TextColumn<RecordDetail> dateColumn = new TextColumn<RecordDetail>() {
												@Override
												public String getValue(
														RecordDetail record) {
													return DateTimeFormat
															.getFormat(
																	PredefinedFormat.YEAR)
															.format(record
																	.getDate());
												}
											};

											TextColumn<RecordDetail> timeColumn = new TextColumn<RecordDetail>() {
												@Override
												public String getValue(
														RecordDetail record) {
													return Utilities
															.formatDuration(record
																	.getTime());
												}
											};
											timeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

											recordTable.addColumn(textColumn,
													constants.name());
											recordTable.addColumn(sexColumn,
													constants.sex());
											recordTable.addColumn(ageColumn,
													constants.age());
											recordTable.addColumn(eventColumn,
													constants.event());
											recordTable.addColumn(dateColumn,
													constants.year());
											recordTable.addColumn(timeColumn,
													constants.time());

											ListDataProvider<RecordDetail> dataProvider = new ListDataProvider<RecordDetail>();

											dataProvider
													.addDataDisplay(recordTable);

											// Add the data to the data
											// provider, which automatically
											// pushes it to the
											// widget.
											List<RecordDetail> list = dataProvider
													.getList();
											for (RecordDetail record : result) {
												list.add(record);
											}

											contentPanel.clear();
											contentPanel.add(new HTML("<h1>"
													+ title + "</h1>"));
											contentPanel.add(recordTable);

										}
									});
						}
					});
				}

			}
		});

	}
}
