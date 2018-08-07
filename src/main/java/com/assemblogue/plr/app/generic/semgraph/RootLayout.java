package com.assemblogue.plr.app.generic.semgraph;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.assemblogue.plr.lib.EntityNode;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class RootLayout extends BorderPane {

	// @FXML
	// SplitPane base_pane;
	@FXML
	AnchorPane basepane;
	@FXML
	AnchorPane right_pane;
	@FXML
	ScrollPane scrollpane;
	@FXML
	BorderPane borderpane;

	private DragIcon mDragOverIcon = null;
	private GraphActor graphAct;
	private EventHandler<DragEvent> mIconDragOverRoot = null;
	private EventHandler<DragEvent> mIconDragDropped = null;
	private EventHandler<DragEvent> mIconDragOverRightPane = null;
	private EventHandler<MouseEvent> mGetonMousePressed = null;
	private EventHandler<MouseEvent> mContextonMouseClick = null;
	private AnimatedZoomOperator zoomOperator = new AnimatedZoomOperator();
	private PlrActor plrAct;

	public RootLayout(GraphActor gact) {

		try {
			this.graphAct = gact;

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RootLayout.fxml"));

			fxmlLoader.setRoot(this);
			fxmlLoader.setController(this);

			fxmlLoader.load();

		} catch (Exception exception) {

			exception.printStackTrace();
			// throw new RuntimeException(exception);
		}

		borderpane.setCenter(scrollpane);
		scrollpane.setContent(right_pane);

	}

	@FXML
	private void initialize() {

		// Add one icon that will be used for the drag-drop process
		// This is added as a child to the root anchorpane so it can be visible
		// on both sides of the split pane.

		// Listen to scroll events (similarly you could listen to a button click,
		// slider, ...)

		this.plrAct = AppController.plrAct;
		mDragOverIcon = new DragIcon();

		mDragOverIcon.setVisible(false);
		mDragOverIcon.setOpacity(0.65);
		getChildren().add(mDragOverIcon);
		scrollpane.setContent(basepane);
		buildDragHandlers();
		// buildNodeClickHandlers();
		/*
		 * right_pane.addEventFilter(MouseEvent.MOUSE_PRESSED, new
		 * EventHandler<MouseEvent>() { public void handle(MouseEvent event) {
		 * System.out.println("mouse clickd double"); boolean doubleClicked =
		 * event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2;
		 * System.out.println("doubleClicked" + doubleClicked); if (doubleClicked) {
		 * System.out.println("function starts"); // DragIcon icn = (DragIcon)
		 * event.getSource(); // mDragOverIcon.setType(icn.getType()); //
		 * event.getSource(); DraggableNode dragnode = new DraggableNode(graphAct);
		 *
		 * // new Point2D(event.getSceneX(), event.getSceneY())
		 * dragnode.setLayoutX(event.getSceneX());
		 * dragnode.setLayoutY(event.getSceneY()); //
		 * dragnode.screenToLocal(getLayoutX(), getLayoutY());
		 * right_pane.getChildren().add(dragnode);
		 *
		 * event.consume(); } } });
		 */

		right_pane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					System.out.println("mouse clickd double");
					boolean doubleClicked = event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2;
					System.out.println("doubleClicked" + doubleClicked);

					if (doubleClicked) {
						System.out.println("function starts");
						// DragIcon icn = (DragIcon) event.getSource();
						// mDragOverIcon.setType(icn.getType());
						// event.getSource();
						DraggableNode dragnode = new DraggableNode(graphAct);

						// new Point2D(event.getSceneX(), event.getSceneY())
						dragnode.setLayoutX(event.getSceneX());
						dragnode.setLayoutY(event.getSceneY());
						// dragnode.screenToLocal(getLayoutX(), getLayoutY());
						right_pane.getChildren().add(dragnode);

						event.consume();
					}
				} /*
					 * else if(event.getButton().equals(MouseButton.SECONDARY)) { double zoomFactor
					 * = 1.5; if (event.isControlDown()) { // zoom out zoomFactor = 1 / zoomFactor;
					 * } zoomOperator.zoom(right_pane, zoomFactor, event.getSceneX(),
					 * event.getSceneY()); event.consume(); }
					 */

			}
		});

		right_pane.setOnScroll((new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				double zoomFactor = 1.5;
				if (event.getDeltaY() <= 0) {
					// zoom out
					zoomFactor = 1 / zoomFactor;
				}
				zoomOperator.zoom(right_pane, zoomFactor, event.getSceneX(), event.getSceneY());
			}
		}));

	}

	private void buildDragHandlers() {

		// drag over transition to move widget form left pane to right pane
		mIconDragOverRoot = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				Point2D p = right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());

				// turn on transfer mode and track in the right-pane's context
				// if (and only if) the mouse cursor falls within the right pane's bounds.
				if (!right_pane.boundsInLocalProperty().get().contains(p)) {

					event.acceptTransferModes(TransferMode.ANY);
					mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
					return;
				}

				event.consume();
			}
		};

		mIconDragOverRightPane = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				event.acceptTransferModes(TransferMode.ANY);

				// convert the mouse coordinates to scene coordinates,
				// then convert back to coordinates that are relative to
				// the parent of mDragIcon. Since mDragIcon is a child of the root
				// pane, coodinates must be in the root pane's coordinate system to work
				// properly.
				mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
				event.consume();
			}
		};

		mIconDragDropped = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

				container.addData("scene_coords", new Point2D(event.getSceneX(), event.getSceneY()));

				ClipboardContent content = new ClipboardContent();
				content.put(DragContainer.AddNode, container);

				event.getDragboard().setContent(content);
				event.setDropCompleted(true);
			}
		};

		this.setOnDragDone(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				right_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRightPane);
				right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
				right_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);

				mDragOverIcon.setVisible(false);

				// Create node drag operation
				DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

				if (container != null) {
					if (container.getValue("scene_coords") != null) {

						if (container.getValue("type").equals(DragIconType.cubic_curve.toString())) {
							CubicCurveDemo curve = new CubicCurveDemo();

							right_pane.getChildren().add(curve);

							Point2D cursorPoint = container.getValue("scene_coords");
							System.out.println("Entered in changing position");
							curve.relocateToPoint(new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32));
						} else {

							DraggableNode node = new DraggableNode(graphAct);

							node.setType(DragIconType.valueOf(container.getValue("type")));
							right_pane.getChildren().add(node);

							Point2D cursorPoint = container.getValue("scene_coords");

							node.relocateToPoint(new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32));
						}
					}
				}

				// Move node drag operation
				container = (DragContainer) event.getDragboard().getContent(DragContainer.DragNode);
				if (container != null) {
					if (container.getValue("type") != null)
						System.out.println("Moved node " + container.getValue("type"));
				}

				// AddLink drag operation
				container = (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);

				if (container != null) {

					// bind the ends of our link to the nodes whose id's are stored in the drag
					// container
					String sourceId = container.getValue("source");
					String targetId = container.getValue("target");

					if (sourceId != null && targetId != null) {

						// System.out.println(container.getData());
						NodeLink link = new NodeLink(graphAct);

						// add our link at the top of the rendering order so it's rendered first
						right_pane.getChildren().add(0, link);

						DraggableNode source = null;
						DraggableNode target = null;

						for (Node n : right_pane.getChildren()) {

							if (n.getId() == null)
								continue;

							if (n.getId().equals(sourceId))
								source = (DraggableNode) n;

							if (n.getId().equals(targetId))
								target = (DraggableNode) n;

						}

						if (source != null && target != null) {
							System.out.println("Entered in adjusting loction");
							link.bindEnds(source, target, new Point2D(event.getX(), event.getY()), link);
						}
					}

				}

				event.consume();
			}
		});
	}

	public void openDraggableGraph() {

		Map<String, EntityNode> footprint = new LinkedHashMap<>();

		double x = 20;
		double y = 20;
		for (EntityNode node : graphAct.getEntryNodes()) {
			if (graphAct.isVisibleRoot(node)) {
				// ルートノードとして表示可能
				// DraggableNode dragnode = createNodeCell(node);

				List<NodeInfo<com.assemblogue.plr.lib.Node>> list = graphAct.list(node);
				for (NodeInfo<com.assemblogue.plr.lib.Node> ni : list) {
					System.out.println("Enterd in NodeInfo");
					// Map<String, com.assemblogue.plr.lib.Node> properties =
					// plrAct.listToMap(ni.getNode().asEntity());
					DraggableNode dragnode = createNodeCell();
					dragnode.setDisplayText(ni.getNode().getId());
					right_pane.getChildren().add(dragnode);
					dragnode.setLayoutX(x);
					dragnode.setLayoutX(y);

					x = x + 375;
					y = y + 50;

				}

			}
		}

	}

	public DraggableNode createNodeCell() {
		// Map<String, com.assemblogue.plr.lib.Node> node_properties =
		// plrAct.listToMap(node);

		/*
		 * EntityNode node_hypernode = null; if
		 * (node_properties.containsKey(AppProperty.LT_HYPERNODE)) { node_hypernode =
		 * node_properties.get(AppProperty.LT_HYPERNODE).asEntity(); }
		 */
		DraggableNode dragnode = new DraggableNode(graphAct);

		return dragnode;

	}

	private String getDisplayContents(EntityNode node, Map<String, com.assemblogue.plr.lib.Node> properties) {
		if (properties == null) {
			properties = plrAct.listToMap(node);
		}

		// 決め打ち：ここで各ノードボタンの表示テキストを決める
		String val = null;
		if (properties.containsKey(AppProperty.ITEM_ID_CNT)) {
			com.assemblogue.plr.lib.Node literal = properties.get(AppProperty.ITEM_ID_CNT);
			val = literal.asLiteral().getValue().toString();
		} else {
			val = Messages.getString("nodecountents.prompt");
		}

		return val;
	}

}