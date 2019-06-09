import animatefx.animation.*;
import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ComparisonResult;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


public class dashboardController implements Initializable {

    @FXML
    public Label headerLabel;

    @FXML
    public JFXProgressBar progressBar;

    @FXML
    public VBox expectedImagePane;

    @FXML
    public VBox actualImagePane;

    @FXML
    public VBox configPane;

    @FXML
    public VBox resultPane;

    @FXML
    public JFXButton backButton;

    @FXML
    public JFXButton forwardButton;

    @FXML
    public JFXButton selectExpectedButton;

    @FXML
    public ImageView expectedImageView;

    @FXML
    public ImageView actualImageView;

    @FXML
    public ImageView expectedImageViewFinal;

    @FXML
    public ImageView actualImageViewFinal;

    @FXML
    public Label errorLabel;

    @FXML
    public Label successLabel;

    @FXML
    public JFXTextField thresholdField;

    @FXML
    public JFXTextField rectangleLineWidthField;

    @FXML
    public JFXTextField maximalRectangleCountField;

    @FXML
    public JFXTextField minimalRectangleSizeField;

    @FXML
    public ImageView resultImageView;

    @FXML
    public JFXButton compareButton;

    private int currentPane = -1;
    /*
    0 - expectedImagePane
    1 - actualImagePane
    2 - configPane
    3 - resultPane
     */

    private BufferedImage expectedImage;
    private BufferedImage actualImage;
    private BufferedImage result;

    private VBox[] allPanes = new VBox[4];
    private String[] allPanesHeading = new String[4];


    private boolean isExpectedImageGiven = false;
    private boolean isActualImageGiven = false;

    private Image noImageSelected = new Image("images/no_image_selected.png");
    private Image loading = new Image("images/loading.png");


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allPanes[0] = expectedImagePane;
        allPanes[1] = actualImagePane;
        allPanes[2] = configPane;
        allPanes[3] = resultPane;

        allPanesHeading[0] = "Expected Image";
        allPanesHeading[1] = "Actual Image";
        allPanesHeading[2] = "Finalize";
        allPanesHeading[3] = "Result";

        showPane(0);

        thresholdField.setOnKeyReleased(event -> new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                try
                {
                    Integer.parseInt(thresholdField.getText());
                }
                catch (Exception e)
                {
                    Platform.runLater(() -> thresholdField.setText("5"));
                }
                return null;
            }
        }).start());

        rectangleLineWidthField.setOnKeyReleased(event -> new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                try
                {
                    Integer.parseInt(rectangleLineWidthField.getText());
                }
                catch (Exception e)
                {
                    Platform.runLater(() -> rectangleLineWidthField.setText("1"));
                }
                return null;
            }
        }).start());

        maximalRectangleCountField.setOnKeyReleased(event -> new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                try
                {
                    Integer.parseInt(maximalRectangleCountField.getText());
                }
                catch (Exception e)
                {
                    Platform.runLater(() -> maximalRectangleCountField.setText(""));
                }
                return null;
            }
        }).start());

        minimalRectangleSizeField.setOnKeyReleased(event -> new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                try
                {
                    Integer.parseInt(minimalRectangleSizeField.getText());
                }
                catch (Exception e)
                {
                    Platform.runLater(() -> minimalRectangleSizeField.setText("1"));
                }
                return null;
            }
        }).start());
    }

    private void showPane(int requiredPane)
    {
        if(currentPane > -1)
        {
            if(currentPane < requiredPane)
            {
                new FadeOutLeft(allPanes[currentPane]).play();
                new FadeInRight(allPanes[requiredPane])
                        .setDelay(Duration.millis(400))
                        .play();
            }
            else if(currentPane > requiredPane)
            {
                new FadeOutRight(allPanes[currentPane]).play();
                new FadeInLeft(allPanes[requiredPane])
                        .setDelay(Duration.millis(400))
                        .play();
            }
        }
        else
        {
            new FadeInRight(allPanes[requiredPane]).play();
        }

        headerLabel.setText(allPanesHeading[requiredPane]);

        allPanes[requiredPane].toFront();

        if(requiredPane == 0)
        {
            backButton.setDisable(true);
            forwardButton.setDisable(false);
        }
        else if(requiredPane == 2)
        {
            backButton.setDisable(false);
            forwardButton.setDisable(true);
        }
        else if(requiredPane == 3)
        {
            backButton.setDisable(false);
            forwardButton.setDisable(true);
        }
        else
        {
            backButton.setDisable(false);
            forwardButton.setDisable(false);
        }

        currentPane = requiredPane;
    }

    @FXML
    public void backButtonClicked()
    {
        showPane(currentPane - 1);
    }

    @FXML
    public void forwardButtonClicked()
    {
        int newPane = currentPane + 1;
        if(newPane == 1)
        {
            //Check for expectedImage
            if(!isExpectedImageGiven)
            {
                showError("Invalid Expected Image");
            }
            else
            {
                showPane(newPane);
            }
        }
        else if(newPane == 2)
        {
            //Check for actualImage
            if(!isActualImageGiven)
            {
                showError("Invalid Actual Image");
            }
            else
            {
                showPane(newPane);
            }
        }

    }

    private void showError(String error)
    {
        if(errorLabel.getOpacity() == 0)
        {
            errorLabel.setText(error);
            new FadeInUp(errorLabel).play();
            new FadeOutDown(errorLabel)
                    .setDelay(Duration.seconds(3))
                    .play();
        }
    }

    private void showSuccess(String success)
    {
        if(successLabel.getOpacity() == 0)
        {
            successLabel.setText(success);
            new FadeInUp(successLabel).play();
            new FadeOutDown(successLabel)
                    .setDelay(Duration.seconds(3))
                    .play();
        }
    }

    @FXML
    public void selectExpectedButtonClicked()
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG File", "*.png")
                ,new FileChooser.ExtensionFilter("JPG File", "*.jpg")
                ,new FileChooser.ExtensionFilter("JPEG File","*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(Main.mainStage);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                try
                {
                    if(selectedFile == null)
                    {
                        return null;
                    }

                    Platform.runLater(() -> progressBar.setProgress(-1));
                    expectedImageView.setImage(loading);
                    expectedImage = ImageComparisonUtil.readImageFromFile(selectedFile);
                    expectedImage1 = SwingFXUtils.toFXImage(expectedImage, null);
                    expectedImageView.setImage(expectedImage1);
                    expectedImageViewFinal.setImage(expectedImage1);
                    isExpectedImageGiven = true;
                }
                catch (Exception e)
                {
                    Platform.runLater(() -> showError("Invalid Expected Image."));
                    isExpectedImageGiven = false;
                    expectedImageView.setImage(noImageSelected);
                    e.printStackTrace();
                }
                Platform.runLater(() -> progressBar.setProgress(0));
                return null;
            }
        }).start();
    }


    private Image actualImage1;
    private Image expectedImage1;
    @FXML
    public void selectActualButtonClicked()
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG File", "*.png")
                ,new FileChooser.ExtensionFilter("JPG File", "*.jpg")
                ,new FileChooser.ExtensionFilter("JPEG File","*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(Main.mainStage);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                try
                {
                    if(selectedFile == null)
                    {
                        return null;
                    }

                    Platform.runLater(() -> progressBar.setProgress(-1));
                    actualImageView.setImage(loading);
                    actualImage = ImageComparisonUtil.readImageFromFile(selectedFile);
                    actualImage1 = SwingFXUtils.toFXImage(actualImage, null);
                    actualImageView.setImage(actualImage1);
                    actualImageViewFinal.setImage(actualImage1);
                    isActualImageGiven = true;
                }
                catch (Exception e)
                {
                    Platform.runLater(() -> showError("Invalid Actual Image."));
                    isActualImageGiven = false;
                    actualImageView.setImage(noImageSelected);
                    e.printStackTrace();
                }
                Platform.runLater(() -> progressBar.setProgress(0));
                return null;
            }
        }).start();
    }

    @FXML
    public void compareButtonClicked()
    {
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                try
                {
                    Platform.runLater(() -> {
                        compareButton.setDisable(true);
                        progressBar.setProgress(-1);
                    });

                    int threshold = Integer.parseInt(thresholdField.getText());
                    int rectangleLineWidth = Integer.parseInt(rectangleLineWidthField.getText());

                    ImageComparison ic = new ImageComparison(expectedImage,actualImage);

                    if(!minimalRectangleSizeField.getText().equals(""))
                    {
                        ic.setMaximalRectangleCount(Integer.parseInt(minimalRectangleSizeField.getText()));
                    }
                    int minimalRectangleSize = Integer.parseInt(minimalRectangleSizeField.getText());


                    ic.setThreshold(threshold);
                    ic.setRectangleLineWidth(rectangleLineWidth);

                    ic.setMinimalRectangleSize(minimalRectangleSize);

                    ComparisonResult cr = ic.compareImages();
                    result = cr.getResult();

                    resultImageView.setImage(SwingFXUtils.toFXImage(result,null));

                    Platform.runLater(() -> {
                        showPane(3);
                        progressBar.setProgress(0);
                        compareButton.setDisable(false);
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        }).start();
    }

    @FXML
    public void saveButtonClicked()
    {
        DirectoryChooser dc = new DirectoryChooser();
        File pathToSave = dc.showDialog(Main.mainStage);
        File finalPath = new File(pathToSave.toString()+"/result.png");
        if(pathToSave.isDirectory())
        {
            try
            {
                ImageComparisonUtil.saveImage(finalPath,result);
                showSuccess("Saved to "+finalPath.toString());
            }
            catch (Exception e)
            {
                showError("Unable to Save! Check StackTrace");
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void compareNewImagesButtonClicked()
    {
        isActualImageGiven = false;
        isExpectedImageGiven = false;
        actualImageView.setImage(noImageSelected);
        expectedImageView.setImage(noImageSelected);
        showPane(0);
    }

    @FXML
    public void infoButtonClicked()
    {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.getDialogPane().setMinWidth(500);
        infoAlert.getDialogPane().setMinHeight(275);
        infoAlert.setTitle("About");
        infoAlert.setHeaderText("About");
        infoAlert.setContentText("Made by Debayan Sutradhar : github.com/ladiesman6969\n" +
                "Based on the library 'image-comparison' by Roman Beskrovny : github.com/romankh3\n\n" +
                "Source Code:\n" +
                "This Example : https://github.com/ladiesman6969/desktop-image-compression\n" +
                "Original Library : https://github.com/romankh3/image-comparison\n\n" +
                "08-06-2019");
        infoAlert.show();
    }
}
