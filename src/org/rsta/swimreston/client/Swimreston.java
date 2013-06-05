package org.rsta.swimreston.client;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rsta.swimreston.shared.ApplicationConstants;
import org.rsta.swimreston.shared.Athlete;
import org.rsta.swimreston.shared.Record;
import org.rsta.swimreston.shared.RecordDetail;
import org.rsta.swimreston.shared.Result;
import org.rsta.swimreston.shared.Utilities;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Swimreston implements EntryPoint {

	private Logger logger = Logger.getLogger(this.getClass().getName());

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

	private final FindAthletesServiceAsync findAthletesService = GWT
			.create(FindAthletesService.class);

	private final AthleteResultsServiceAsync athleteResultsService = GWT
			.create(AthleteResultsService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
		dockLayoutPanel.addNorth(new HTML("Header Placeholder"), 2);
		dockLayoutPanel.addSouth(new HTML("Footer Placeholder"), 2);
		StackLayoutPanel stackPanel = new StackLayoutPanel(Unit.EM);

		final VerticalPanel contentPanel = new VerticalPanel();

		// Individual Swimmer report is the default content
		// for the main panel.
		buildIndividualSwimmerReportPanel(contentPanel);

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.add(contentPanel);

		final FlowPanel recordsPanel = new FlowPanel();
		HTML recordsHeader = new HTML(constants.records());

		final FlowPanel reportsPanel = new FlowPanel();
		HTML reportsHeader = new HTML(constants.reports());

		Button individualSwimmerButton = new Button(
				constants.individualAthlete());
		individualSwimmerButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				buildIndividualSwimmerReportPanel(contentPanel);
			}
		});

		reportsPanel.add(individualSwimmerButton);

		stackPanel.add(reportsPanel, reportsHeader, 2);
		stackPanel.add(recordsPanel, recordsHeader, 2);

		dockLayoutPanel.addWest(stackPanel, 10);

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
											timeColumn
													.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

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

	private void buildIndividualSwimmerReportPanel(final Panel panel) {
		panel.clear();

		HTML suggestAthleteLabel = new HTML(constants.labelSearchAthlete()
				+ ":");

		SuggestBox suggestAthleteBox = new SuggestBox(new RpcSuggestAthlete(
				findAthletesService));
		suggestAthleteBox.setTitle(constants.labelSearchAthlete());

		// Panel to hold the results obtained after looking up the athlete
		final VerticalPanel athleteResultsPanel = new VerticalPanel();

		panel.add(suggestAthleteLabel);
		panel.add(suggestAthleteBox);
		panel.add(athleteResultsPanel);

		suggestAthleteBox
				.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {

					@Override
					public void onSelection(SelectionEvent<Suggestion> event) {
						Suggestion suggestion = event.getSelectedItem();
						if (suggestion != null) {
							AthleteSuggestion athleteSuggestion = (AthleteSuggestion) suggestion;
							final Athlete athlete = athleteSuggestion
									.getAthlete();
							if (athlete != null) {
								logger.info("athlete id:" + athlete.getId()
										+ " year:" + athlete.getYear());
								athleteResultsService.findAthleteResults(
										athlete,
										new AsyncCallback<List<Result>>() {

											@Override
											public void onFailure(
													Throwable caught) {
												logger.log(
														Level.SEVERE,
														"Failed to get athlete results for athlete id:"
																+ athlete
																		.getId()
																+ " year:"
																+ athlete
																		.getYear(),
														caught);
												athleteResultsPanel
														.add(new HTML(
																SERVER_ERROR));

											}

											@Override
											public void onSuccess(
													List<Result> result) {

												CellTable<Result> resultTable = new CellTable<Result>();

												resultTable.setPageSize(result
														.size());

												TextColumn<Result> dateColumn = new TextColumn<Result>() {
													@Override
													public String getValue(
															Result result) {
														Date date = result
																.getMeet() != null
																&& result
																		.getMeet()
																		.getDate() != null ? result
																.getMeet()
																.getDate()
																: null;

														return date != null ? DateTimeFormat
																.getFormat(
																		PredefinedFormat.DATE_SHORT)
																.format(date)
																: "";
													}
												};

												TextColumn<Result> eventColumn = new TextColumn<Result>() {
													@Override
													public String getValue(
															Result result) {

														String event = Utilities
																.formatEventName(
																		result.getDistance(),
																		result.getStroke(),
																		result.isRelay());
														return event;
													}
												};

												TextColumn<Result> scoreColumn = new TextColumn<Result>() {
													@Override
													public String getValue(
															Result result) {
														return Utilities
																.formatDuration(result
																		.getScore());
													}
												};
												scoreColumn
														.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

												resultTable.addColumn(
														dateColumn,
														constants.date());

												resultTable.addColumn(
														eventColumn,
														constants.event());

												resultTable.addColumn(
														scoreColumn,
														constants.score());

												ListDataProvider<Result> dataProvider = new ListDataProvider<Result>();

												dataProvider
														.addDataDisplay(resultTable);

												// Add the data to the data
												// provider, which automatically
												// pushes it to the
												// widget.
												List<Result> list = dataProvider
														.getList();
												for (Result record : result) {
													list.add(record);
												}

												athleteResultsPanel.clear();
												athleteResultsPanel
														.add(resultTable);

											}
										});
							}
						}

					}
				});

	}
}
