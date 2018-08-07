package com.assemblogue.plr.app.generic.semgraph;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.lang.Math;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.When;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Polygon;

public class NodeLink extends Pane {
	@FXML
	Pane rootpane;
	@FXML
	CubicCurve node_link;
	@FXML
	Polygon arrowleft;
	@FXML
	Polygon arrowright;
	@FXML
	AnchorPane anchorpaneleft;
	@FXML
	AnchorPane anchorpaneright;

	@FXML
	Pane attribute;

	@FXML
	HBox relation;
	private GraphActor graphAct;
	private OntMenu ontmenu;

	private final DoubleProperty mControlOffsetX = new SimpleDoubleProperty();
	private final DoubleProperty mControlOffsetY = new SimpleDoubleProperty();
	private final DoubleProperty mControlDirectionX1 = new SimpleDoubleProperty();
	private final DoubleProperty mControlDirectionY1 = new SimpleDoubleProperty();
	private final DoubleProperty mControlDirectionX2 = new SimpleDoubleProperty();
	private final DoubleProperty mControlDirectionY2 = new SimpleDoubleProperty();

	private EventHandler<MouseEvent> ArrowLeftVisible;
	private EventHandler<MouseEvent> ArrowRightVisible;

	public NodeLink(GraphActor gact) {
		this.graphAct = gact;

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NodeLink.fxml"));

		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();

		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		// provide a universally unique identifier for this object
		setId(UUID.randomUUID().toString());
	}

	@FXML
	private void initialize() {

		mControlOffsetX.set(100.0);
		mControlOffsetY.set(50.0);
		arrowleft.setVisible(false);
		arrowright.setVisible(true);
		//ArrowHandler();
		//anchorpaneleft.setOnMousePressed(ArrowLeftVisible);
		//anchorpaneright.setOnMousePressed(ArrowRightVisible);

		anchorpaneleft.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("arrow click LEFT");
				arrowleft.setVisible(true);
				arrowright.setVisible(false);
				event.consume();

			}

		});

		anchorpaneright.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("arrow click RIGHT");
				arrowright.setVisible(true);
				arrowleft.setVisible(false);
				event.consume();

			}

		});

		double diffX = (node_link.getStartX() - node_link.getEndX()) / 2;
		double diffY = (node_link.getStartY() - node_link.getEndY()) / 2;

		mControlDirectionX1.bind(
				new When(node_link.startXProperty().greaterThan(node_link.endXProperty())).then(-1.0).otherwise(1.0));

		mControlDirectionX2.bind(
				new When(node_link.startXProperty().greaterThan(node_link.endXProperty())).then(1.0).otherwise(-1.0));

		node_link.controlX1Property()
				.bind(Bindings.add(node_link.startXProperty(), mControlOffsetX.multiply(mControlDirectionX1)));

		node_link.controlX2Property()
				.bind(Bindings.add(node_link.endXProperty(), mControlOffsetX.multiply(mControlDirectionX2)));

		node_link.controlY1Property()
				.bind(Bindings.add(node_link.startYProperty(), mControlOffsetY.multiply(mControlDirectionY1)));

		node_link.controlY2Property()
				.bind(Bindings.add(node_link.endYProperty(), mControlOffsetY.multiply(mControlDirectionY2)));

		parentProperty().addListener(new ChangeListener() {

			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				rootpane = (AnchorPane) getParent();

				// choicebox.setItems(FXCollections.observableArrayList("莉伜刈逧・未菫・,"豁｣莉伜刈髢｢菫・,"雋莉伜刈髢｢菫・,"豁｣蝗譫憺未菫・,"雋蝗譫憺未菫・));
			}

		});

	}

	private void ArrowHandler() {

		ArrowLeftVisible = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				System.out.println("arrow click LEFT");
				arrowleft.setVisible(true);
				arrowright.setVisible(false);
				event.consume();

			}

		};

		ArrowRightVisible = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				System.out.println("arrow click RIGHT");
				arrowright.setVisible(true);
				arrowleft.setVisible(false);
				event.consume();

			}

		};

	}

	private void createOntMenu() {
		System.out.println("Entered in crete menussss");
		String item_id = "#Entity";
		String omi_id = "#rel";
		String val = "";
		// HBox relmenu = new HBox(10d);
		// dragnode.ontMenu = new OntMenu(graphAct, graphAct.getOss().getNode(item_id));
		// List<OntMenu.OntMenuItem> ranged_omi = ndc.ontMenu.getRangedClassItem();
		System.out.println("graphAct" + graphAct);
		System.out.println("graphAct.getOss()" + graphAct.getOss());
		System.out.println("graphAct.getOss().getNode(item_id)" + graphAct.getOss().getNode(item_id));
		OntMenu ontMenu = new OntMenu(graphAct, graphAct.getOss().getNode(item_id));
		List<OntMenu.OntMenuItem> sub_omi_list = ontMenu.makeMenuList2(omi_id);

		for (OntMenu.OntMenuItem sub_omi : sub_omi_list) {
			// 設定済みの属性値(OntologyIte.label)をラベル名とする
			// fxLabelには、初期状態ではオントロジ属性名が入る
			if (val != null && !val.equals("")) {
				// child.dispRelStr = val;
				sub_omi.fxLabel.setText(val);
				sub_omi.fxLabel.setTextFill(Color.BLACK);
			} else {
				sub_omi.fxLabel.setTextFill(Color.GRAY);
			}
			System.out.println(" sub_omi.fxLabel" + sub_omi.fxLabel.getText());
			relation.getChildren().add(sub_omi.fxLabel);

			// 属性値が選択されたら、ラベル値を書き換えるので、ラベル値のリスナで選択された属性値を取得する
			sub_omi.fxLabel.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if (newValue != null) {
						// title_bar.setText(newValue);
						System.out.println("sub_omi.id"+sub_omi.id);
						if(sub_omi.id.equals("#rel")) {
							arrowleft.setVisible(false);
							arrowright.setVisible(false);
						}
						System.out.println("newValue" + newValue);

					}
				}
			});
		}

		// attribute.getChildren().add(relation);

		attribute.setVisible(true);
	}

	public void setStart(Point2D startPoint) {

		node_link.setStartX(startPoint.getX());
		node_link.setStartY(startPoint.getY());

	}

	public void setEnd(Point2D endPoint) {

		node_link.setEndX(endPoint.getX());
		node_link.setEndY(endPoint.getY());

		// arrow.setLayoutX(endPoint.getX());
		// arrow.setLayoutY(endPoint.getY());

		attribute.setLayoutX(endPoint.getX());
		attribute.setLayoutY(endPoint.getY());
		//
		// arrow1.setLayoutX(endPoint.getX());
		// arrow1.setLayoutY(endPoint.getY() + 40);

	}

	public void setArrowDropped(Point2D endPoint) {

		// node_link.setEndX(endPoint.getX());
		// node_link.setEndY(endPoint.getY());

		System.out.println("Reached in drop event");
		// arrow.setLayoutX(endPoint.getX());
		// arrow.setLayoutY(endPoint.getY());

		attribute.setLayoutX(endPoint.getX() - 10);
		attribute.setLayoutY(endPoint.getY() - 10);

		System.out.println("Reached in drop event");
		// arrow1.setLayoutX(endPoint.getX());
		// arrow1.setLayoutY(endPoint.getY()-40);
	}

	public void bindEnds(DraggableNode source, DraggableNode target, Point2D endPoint, NodeLink target1) {
		node_link.startXProperty().bind(Bindings.add(source.layoutXProperty(), (source.getHeight() / 2.0)));

		node_link.startYProperty().bind(Bindings.add(source.layoutYProperty(), (source.getHeight() / 2.0)));

		node_link.endXProperty().bind(Bindings.add(target.layoutXProperty(), (target.getHeight() / 2.0)));

		node_link.endYProperty().bind(Bindings.add(target.layoutYProperty(), (target.getHeight() / 2.0)));

		// rootpane.getChildren().add(attribute);
		/*
		 * node_link.endYProperty().bind(
		 * Bindings.add(target.layoutYProperty(),target.layoutYProperty()));
		 */
System.out.println("target.getwidth" +target.getWidth());
System.out.println("target.layoutX" +target.layoutXProperty());
System.out.println("target.layoutY" +target.layoutYProperty());

		System.out.println("Reached in drop bindends X" + endPoint.getX());
		System.out.println("Reached in drop bindends Y" + endPoint.getY());

		attribute.layoutXProperty()
				.bind(Bindings.add(node_link.controlX1Property(), node_link.controlX2Property()).divide(2.5));
		attribute.layoutYProperty()
				.bind(Bindings.add(node_link.controlY1Property(), node_link.controlY2Property()).divide(2.5));
		createOntMenu();

		source.registerLink(getId());
		target.registerLink(getId());

		System.out.println("target" + getId());
	}

}