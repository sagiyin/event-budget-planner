package budgeteventplanner.client;

import java.util.List;

import budgeteventplanner.client.entity.BudgetItem;
import budgeteventplanner.shared.Pair;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;

public class BudgetBarChart extends Composite {

  private final BudgetServiceAsync budgetService = GWT.create(BudgetService.class);

  private final List<Pair<String, BudgetItem>> catBitPairList = Lists.newArrayList();
  private List<Pair<String, Double>> catCostPairList = Lists.newArrayList();
  private final VerticalPanel panel = new VerticalPanel();
  ColumnChart columnChart;
  int selectionID;
  DialogBox promptModifyBox = new DialogBox();
  TextBox modificationBox = new TextBox();

  public BudgetBarChart(final String budgetId) {
    super();
    panel.clear();
    final Anchor btnDraw = new Anchor("Draw ColumnChart");
    btnDraw.setEnabled(false);
    panel.add(btnDraw);
    modificationBox.setEnabled(false);
    initializeServiceRequestList(budgetId);
    budgetService.getLimitsByBudgetId(budgetId,
        new AsyncCallback<List<Pair<String, BudgetItem>>>() {
          @Override
          public void onFailure(Throwable caught) {
          }

          @Override
          public void onSuccess(List<Pair<String, BudgetItem>> result) {
            catBitPairList.addAll(result);
            btnDraw.setEnabled(true);
            modificationBox.setEnabled(true);
          }
        });

    promptModifyBox.hide();
    promptModifyBox.setText("Enter Value to Modify:");
    promptModifyBox.setAnimationEnabled(true);
    promptModifyBox.setTitle("Modification");
    modificationBox = new TextBox();
    modificationBox.setFocus(true);
    modificationBox.addKeyPressHandler(new KeyPressHandler() {

      @Override
      public void onKeyPress(KeyPressEvent event) {

        if (event.getUnicodeCharCode() == 13 && !modificationBox.getText().isEmpty()) {
          BudgetItem temp = catBitPairList.get(selectionID).getB();
          temp = new BudgetItem.Builder(temp).setLimit(
              Double.parseDouble(modificationBox.getText())).build();
          // Window.alert(modificationBox.getText()+" entered");

          // Update coreChartData locally
          catBitPairList.set(selectionID,
              new Pair<String, BudgetItem>(catBitPairList.get(selectionID).getA(), temp));

          // Update coreChartData to Server
          updateEntryToServer(catBitPairList.get(selectionID).getB().getBudgetItemId(),
              Double.parseDouble(modificationBox.getText()));

          modificationBox.setText("");
          promptModifyBox.hide();
        } else if (event.getUnicodeCharCode() == 13) {
          promptModifyBox.hide();
        }

      }
    });

    promptModifyBox.add(modificationBox);
    panel.add(promptModifyBox);
    promptModifyBox.setVisible(false);

    btnDraw.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        Runnable onLoadCallback = new Runnable() {
          public void run() {
            try {
              panel.remove(columnChart);
            } catch (Exception e) {
            }

            // Synchronize with server
            budgetService.getLimitsByBudgetId(budgetId,
                new AsyncCallback<List<Pair<String, BudgetItem>>>() {
                  @Override
                  public void onFailure(Throwable caught) {
                    Window.alert("budgetService.getLimitsByBudgetId - failure");
                  }

                  @Override
                  public void onSuccess(List<Pair<String, BudgetItem>> result) {
                    catBitPairList.clear();
                    catBitPairList.addAll(result);
                  }
                });

            DataTable dataTable = DataTable.create();

            if (dataTable.getNumberOfColumns() == 0) {
              dataTable.addColumn(ColumnType.STRING, "Category");
              dataTable.addColumn(ColumnType.NUMBER, "Expense");
              dataTable.addColumn(ColumnType.NUMBER, "Limit");
            }

            for (Pair<String, BudgetItem> pair : catBitPairList) {
              dataTable.addRow();
              dataTable.setValue(dataTable.getNumberOfRows() - 1, 0, pair.getA());
              dataTable.setValue(dataTable.getNumberOfRows() - 1, 1,
                  mapToServiceRequestList(pair.getA()));
              dataTable.setValue(dataTable.getNumberOfRows() - 1, 2, pair.getB().getLimit());
            }

            columnChart = new ColumnChart(dataTable, createOptions());

            columnChart.addSelectHandler(createSelectHandler(columnChart));
            panel.add(columnChart);
          }
        };
        VisualizationUtils.loadVisualizationApi(onLoadCallback, CoreChart.PACKAGE);

      }
    });

    initWidget(panel);
  }

  private SelectHandler createSelectHandler(final CoreChart chart) {
    return new SelectHandler() {
      @Override
      public void onSelect(SelectEvent event) {
        String message = "";
        // May be multiple selections.
        JsArray<Selection> selections = chart.getSelections();

        for (int i = 0; i < selections.length(); i++) {
          // add a new line for each selection
          message += i == 0 ? "" : "\n";

          Selection selection = selections.get(i);

          if (selection.isCell()) {
            // isCell() returns true if a cell has been selected.

            // getRow() returns the row number of the selected cell.
            int row = selection.getRow();
            // getColumn() returns the column number of the selected
            // cell.
            int column = selection.getColumn();
            message += "cell " + row + ":" + column + " selected";
            if (column == 2) {
              selectionID = row;
              promptModifyBox.center();
            }

          } else if (selection.isRow()) {
            // isRow() returns true if an entire row has been
            // selected.
            // getRow() returns the row number of the selected row.
            int row = selection.getRow();
            message += "row " + row + " selected";

          } else {
            // unreachable
            message += "Pie chart selections should be either row selections or cell selections.";
            message += "  Other visualizations support column selections as well.";
          }
        }
      }
    };
  }

  private Options createOptions() {
    Options options = Options.create();
    options.setWidth(400);
    options.setHeight(240);
    options.setTitle("Column Chart");
    options.set("is3D", true);
    return options;
  }

  void updateEntryToServer(String budgetItemId, Double limit) {
    budgetService.updateBudgetItemLimit(budgetItemId, limit, new AsyncCallback<Void>() {
      @Override
      public void onFailure(Throwable caught) {
      }

      @Override
      public void onSuccess(Void result) {
      }
    });
  }

  private Double mapToServiceRequestList(String catId) {
	  Double subtotal = 0.0;
		
		for (Pair<String, Double> e: catCostPairList) {
			if(e.getA().equals(catId))
				subtotal += e.getB();
		}
		return subtotal;

  }

  void initializeServiceRequestList(String budgetId) {
    budgetService.getSubtotalsByBudgetId(budgetId, new AsyncCallback<List<Pair<String, Double>>>() {

      @Override
      public void onFailure(Throwable caught) {
        Window
            .alert("initializeServiceRequestList -> budgetService.getSubtotalsByEventId - failure\n");
      }

      @Override
      public void onSuccess(List<Pair<String, Double>> result) {
        // Window.alert("server responds");
        catCostPairList = result;
      }
    });

  }
}
