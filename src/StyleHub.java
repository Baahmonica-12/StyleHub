import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class StyleHub extends Application{
    private List<Product> cartItems = new ArrayList<>();
    private Label cartCounterLabel;
    private Stage mainStage;
    private String currentUser = "Guest";
    private double discountMultiplier = 1.0;

    private final String APP_NAME = "StyleHub";
    private final String BRAND_COLOR = "#fb7701"; // Temu Orange

    private ObservableList<Product> masterData = FXCollections.observableArrayList();
    private FlowPane grid = new FlowPane(15, 15);
    public static class Product {
        String name, imagePath, category, description;
        double price;
        public Product(String name, double price, String imagePath, String category, String description) {
            this.name = name; this.price = price; this.imagePath = imagePath; this.category = category; this.description = description;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.mainStage = primaryStage;
        loadData();
        showMainScene();
    }

    private void showLoginScene() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #fff3e0, #ffffff); " +"-fx-border-color: " + BRAND_COLOR + "; " + "-fx-border-width: 0 0 8 0;");

        Image logoImg = new Image(getClass().getResourceAsStream("/Resource/images/shop_icon.jpg"));
        ImageView logoView = new ImageView(logoImg);
        logoView.setFitWidth(80);
        logoView.setPreserveRatio(true);
        layout.getChildren().add(logoView);

        Label logo = new Label(APP_NAME.toUpperCase());
        logo.setFont(Font.font("Arial Black", 45));
        logo.setTextFill(Color.web(BRAND_COLOR));
        logo.setEffect(new javafx.scene.effect.DropShadow(15, Color.web("#d3d3d3")));

        Label slogan = new Label("Shop the Best, Forget the Rest");

        slogan.setStyle("-fx-font-style: italic; -fx-text-fill: #666; -fx-font-size: 14;");

        TextField userIn = new TextField();
        userIn.setPromptText("Enter Username");
        userIn.setPrefHeight(45);
        userIn.setMaxWidth(300);
        userIn.setStyle("-fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: #ccc; -fx-padding: 0 20 0 20;");

        PasswordField passIn = new PasswordField();
        passIn.setPrefHeight(45);
        passIn.setMaxWidth(300);
        passIn.setPromptText("Enter Password");
        passIn.setStyle("-fx-background-radius: 30; -fx-border-radius: 30;");


         CheckBox remember = new CheckBox("Remember Me");
         Hyperlink forgot = new Hyperlink("Forgot Password?");
         HBox extras = new HBox(50, remember, forgot);
    extras.setAlignment(Pos.CENTER);

        Button loginBtn = new Button("Login & Checkout");
        loginBtn.setStyle("-fx-background-color: " + BRAND_COLOR + "; -fx-text-fill: white; " + "-fx-font-weight: bold; -fx-background-radius: 30;");
        loginBtn.setPrefSize(300, 45);

        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #e66a00; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 30; -fx-font-size: 16;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: " + BRAND_COLOR + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;"));

        loginBtn.setOnAction(e -> {
            if(!userIn.getText().isEmpty()) currentUser = userIn.getText();
            showCheckoutScene();
        });

        layout.getChildren().addAll(logo, new Label("Team up, Price down!"), userIn,passIn, extras, loginBtn);
        mainStage.setScene(new Scene(layout, 420, 750));
    }


    public void showMainScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f4f4;");


        VBox header = new VBox(10);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: white;");

        Label welcome = new Label("Welcome, " + currentUser);
        welcome.setFont(Font.font("System", FontWeight.BOLD, 14));

        TextField searchBar = new TextField();
        searchBar.setPromptText("Search " + APP_NAME + "...");
        searchBar.setStyle("-fx-background-radius: 20; -fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 20;");

        MenuButton categoryBtn = new MenuButton("Categories");
        MenuItem all = new MenuItem("All Products");
        MenuItem acc = new MenuItem("Accessories");
        MenuItem elec = new MenuItem("Electronics");
        MenuItem fash = new MenuItem("Fashion");
        categoryBtn.getItems().addAll(all, acc, elec, fash);

        FilteredList<Product> filteredData = new FilteredList<>(masterData, p -> true);

        all.setOnAction(e -> filteredData.setPredicate(p -> true));
        acc.setOnAction(e -> filteredData.setPredicate(p -> p.category.equals("Accessories")));
        elec.setOnAction(e -> filteredData.setPredicate(p -> p.category.equals("Electronics")));
        fash.setOnAction(e -> filteredData.setPredicate(p -> p.category.equals("Fashion")));

        searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(product -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lowerCaseFilter = newVal.toLowerCase();
                return product.name.toLowerCase().contains(lowerCaseFilter);
            });
            renderGrid(filteredData);
        });

        HBox filterBar = new HBox(10, searchBar, categoryBtn);
        HBox.setHgrow(searchBar, Priority.ALWAYS);

        header.getChildren().addAll(welcome, filterBar);


        grid.setPadding(new  Insets(25, 15, 25, 15));
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setAlignment(Pos.TOP_CENTER);
        renderGrid(filteredData);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: #f4f4f4;");

        Button cartBtn = new Button("🛒 Cart (" + cartItems.size() + ")");
        cartBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
        cartBtn.setOnAction(e -> showCheckoutScene());


        Button logoutBtn = new Button("Log Out");
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999;");
        logoutBtn.setOnAction(e ->{currentUser = "Guest";
        showMainScene(); });

        HBox nav = new HBox(60, new Label("Home"), new Label("Categories"), cartBtn);
        nav.setPadding(new Insets(15));
        nav.setAlignment(Pos.CENTER);
        nav.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");

        root.setTop(header);
        root.setCenter(scroll);
        root.setBottom(nav);

        Scene scene = new Scene(root, 420, 750);
        mainStage.setScene(scene);
        mainStage.show();
    }

    private void renderGrid(FilteredList<Product> data) {
        grid.getChildren().clear();
        for (Product p : data) {
            VBox card = new VBox(8);
            card.setPrefWidth(180);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color: white; " + "-fx-background-radius: 15; " + "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.1), 10, 0, 0, 4);");


            StackPane imgFrame = new StackPane();
            try {
                Image imgFile = new Image(getClass().getResourceAsStream(p.imagePath));
                ImageView imageView = new ImageView(imgFile);
                imageView.setFitWidth(180);
                imageView.setFitHeight(180);

                imageView.setPreserveRatio(true);
                imageView.setOnMouseClicked(e -> showProductDetails(p));
                imgFrame.getChildren().add(imageView);
            } catch (Exception e) {
                Rectangle placeholder = new Rectangle(180, 180, Color.LIGHTGREY);
                imgFrame.getChildren().add(placeholder);
            }

            Label name = new Label(" " + p.name);
            Label price = new Label(" GH₵" + p.price);
            price.setFont(Font.font("System", FontWeight.BOLD, 16));

            Button add = new Button("+");
            add.setStyle("-fx-background-color: " + BRAND_COLOR + "; -fx-text-fill: white; -fx-background-radius: 15;");
            add.setOnAction(e -> {
                cartItems.add(p);
                showMainScene();
            });

            HBox row = new HBox(price, new Region(), add);
            HBox.setHgrow(new Region(), Priority.ALWAYS);
            row.setPadding(new Insets(0, 10, 10, 5));

            card.getChildren().addAll(imgFrame, name, row);
            grid.getChildren().add(card);
        }

    }

    private void showProductDetails(Product p) {
        Stage detailStage = new Stage();
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: white; -fx-border-color: " + BRAND_COLOR + "; -fx-border-width: 2;");

        ImageView fullImg = new ImageView(new Image(getClass().getResourceAsStream(p.imagePath)));
        fullImg.setFitWidth(300); fullImg.setPreserveRatio(true);

        Label name = new Label(p.name);
        name.setFont(Font.font("System", FontWeight.BOLD, 22));

        Label desc = new Label(p.description);
        desc.setWrapText(true);
        desc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button addBtn = new Button("Add to Cart");
        addBtn.setStyle("-fx-background-color: " + BRAND_COLOR + "; -fx-text-fill: white;");
        addBtn.setOnAction(e -> {
            cartItems.add(p);
            cartCounterLabel.setText("🛒 Cart (" + cartItems.size() + ")");
            detailStage.close();
        });

        Button close = new Button("Close");
        close.setOnAction(e -> detailStage.close());

        layout.getChildren().addAll(fullImg, name, desc, close);
        detailStage.setScene(new Scene(layout, 400, 550));
        detailStage.show();
    }


    private void showCheckoutScene() {
        if(currentUser.equals("Guest")){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please login to view cart and place an order!");
            alert.showAndWait();
            showLoginScene();
            return;
        }
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Button back = new Button("← Back to Shop");
        back.setOnAction(e -> { discountMultiplier = 1.0; showMainScene(); });

        Label title = new Label("Your Cart Items");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));

        VBox itemsBox = new VBox(10);
        updateCartList(itemsBox);

        // Discount Logic
        TextField promo = new TextField();
        promo.setPromptText("Enter Code (SAVE10)");
        Button apply = new Button("Apply");
        apply.setOnAction(e -> {
            if(promo.getText().equalsIgnoreCase("SAVE10")) {
                discountMultiplier = 0.90;
                updateCartList(itemsBox);
            }
        });
        HBox discountRow = new HBox(10, promo, apply);

        Button placeOrder = new Button("Confirm Order");
        placeOrder.setPrefWidth(380);
        placeOrder.setPrefHeight(50);
        placeOrder.setStyle("-fx-background-color: " + BRAND_COLOR + "; -fx-text-fill: white; -fx-background-radius: 25; -fx-font-weight: bold;");
        placeOrder.setOnAction(e -> {
            if(cartItems.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Cart is empty!").show();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Success! Thank you for shopping with " + APP_NAME).showAndWait();
                cartItems.clear();
                discountMultiplier = 1.0;
                showMainScene();
            }
        });

        layout.getChildren().addAll(back, title, new ScrollPane(itemsBox), new Separator(), discountRow, placeOrder);
        mainStage.setScene(new Scene(layout, 420, 750));
    }

    private void updateCartList(VBox box) {
        box.getChildren().clear();
        double subtotal = 0;

        for (Product p : new ArrayList<>(cartItems)) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label info = new Label(p.name + " - GH₵" + p.price);
            Button del = new Button("X");
            del.setTextFill(Color.RED);
            del.setOnAction(e -> {
                cartItems.remove(p);
                updateCartList(box);
            });
            row.getChildren().addAll(info, new Region(), del);
            HBox.setHgrow(new Region(), Priority.ALWAYS);
            box.getChildren().add(row);
            subtotal += p.price;
        }

        double total = subtotal * discountMultiplier;
        Label totalLbl = new Label("Total Payable: GH₵" + String.format("%.2f", total));
        totalLbl.setFont(Font.font("System", FontWeight.BOLD, 18));
        if(discountMultiplier < 1.0) totalLbl.setTextFill(Color.GREEN);

        box.getChildren().addAll(new Separator(), totalLbl);
    }

    private void loadData() {
        masterData.add(new Product("Fashionable Glasses", 62.74, "/Resource/images/fashionable glasses.jpg", "Accessories", "Stylish UV400 protection."));
        masterData.add(new Product("Wireless Headphone", 118.42, "/Resource/images/wireless headphone.jpg", "Electronics", "Wireless Bluetooth bass boost."));
        masterData.add(new Product("Watch", 210.00, "/Resource/images/watch.jpg", "Accessories", "Stainless steel water resistant."));
        masterData.add(new Product("Bracelet", 45.00, "/Resource/images/bracelet.jpg", "Fashion", "Handmade beaded bracelet."));
        masterData.add(new Product("Laptop", 10000.00,  "/Resource/images/laptop.jpg", "Electronics", "Powerful 15-inch laptop with 16GB RAM and 512GB SSD.Perfect for student and professionals."));
        masterData.add(new Product("Wig", 750.00, "/Resource/images/wig.jpg", "Fashion", "Water wave 5 by 5 closure HD Lace Glueless Human Hair wig."));
        masterData.add(new Product("Phone", 9300.00, "/Resource/images/phone.jpg", "Electronics", "Features a bright 6.1-inch Super Retina XDR display, advanced dual-camera system for cinematic video, and the lightning-fast A15 Bionic chip."));
        masterData.add(new Product("Waist Beads", 118.42, "/Resource/images/waist beads.jpg", "Fashion", "Handmade African waist beads."));
        masterData.add(new Product("Bag", 210.00, "/Resource/images/bag.jpg", "Fashion", "Quality lather and also durable."));
        masterData.add(new Product("Dress", 130.00,"/Resource/images/dress.jpg", "Fashion", "White cotton dress."));
        masterData.add(new Product("Shoes", 240.00,"/Resource/images/shoes.jpg", "Fashion", "Lather sport shoe for ladies."));
    }

    public static void main(String[] args) { launch(args); }
}

