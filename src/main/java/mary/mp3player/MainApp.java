package mary.mp3player;

import java.io.File;

import javafx.application.*;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;                         
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;


public class MainApp extends Application{
	
	Stage stage;
		
	final int FRAME_WIDTH = 400;
	final int FRAME_HEIGHT = 300;
	
	private ObservableList<Track> tracks = FXCollections.observableArrayList();
	Track currentTrack;
	Track selectedTrack;

	static Label playTime;

	private static Slider timeSlider;
	

	HBox hbButtons;

	
	ScrollPane tracksScroll;
	ListView<Track> tracksView;
	
	FileChooser chooser;
	Button addTrackButton;
	Button removeTrackButton;
	

	@Override
	public void start(Stage arg0) throws Exception {		
		DBWork.connect();
		initUI(arg0);
	}
	
	public ObservableList<Track> getTracks(){
		return tracks;
	}
	
	void initUI(final Stage stage) {
		

		stage.setTitle("Player");
       
	  GridPane mainGrid = new GridPane(); 
      mainGrid.setHgap(10);
      mainGrid.setVgap(10);
      mainGrid.setMinSize(FRAME_WIDTH, FRAME_HEIGHT);
      
      
      final Slider volumeSlider = new Slider();
      volumeSlider.setPrefWidth(FRAME_WIDTH*0.20);
      volumeSlider.setMinWidth(volumeSlider.getPrefWidth());
      volumeSlider.setValue(50);
      
      final Button playPauseButton = new Button(">");      
      final Button stopButton = new Button("#");
      final Button addTrackButton = new Button("+");
      final Button removeTrackButton = new Button("-");
      
      HBox hbButtons = new HBox(4);
      hbButtons.setAlignment(Pos.CENTER_LEFT);
      hbButtons.getChildren().addAll(new Text(" "), stopButton, playPauseButton, addTrackButton, removeTrackButton, volumeSlider);            
      mainGrid.add(hbButtons, 0, 1);
            
      timeSlider = new Slider();
      timeSlider.setPrefWidth(FRAME_WIDTH*0.8);
      timeSlider.setMinWidth(FRAME_WIDTH*0.8);
      playTime = new Label("0:00/0:00");
      playTime.setMinWidth(FRAME_WIDTH*0.1);
      HBox hbTime = new HBox();
      hbTime.setAlignment(Pos.CENTER);
      HBox.setHgrow(timeSlider, Priority.ALWAYS);
      hbTime.getChildren().addAll(timeSlider, playTime);
      mainGrid.add(hbTime, 0, 2);

      final ListView tracksView = new ListView<Track>();
      tracksView.setMinWidth(FRAME_WIDTH);
      tracksView.setPrefWidth(tracksView.getMinWidth());
	  tracks = DBWork.getAllTracks();
	  for(int j = 0; j < tracks.size() ; j++)
	  {
		  if (!new File(tracks.get(j).getPath()).exists())
			  removeTrack(tracks.get(j));
	  }
	  
	  tracksView.setItems(tracks);
      tracksView.getSelectionModel().select(0);
      if (!tracks.isEmpty()) {
    	  selectedTrack = tracks.get(tracksView.getSelectionModel().getSelectedIndex());
    	  currentTrack = selectedTrack;
    	}
      if (currentTrack == null) {
		  currentTrack = selectedTrack;
		}
      
      tracksScroll = new ScrollPane(tracksView);
      
      mainGrid.add(tracksScroll, 0, 3);
      
      
      Scene scene = new Scene(mainGrid, FRAME_WIDTH, FRAME_HEIGHT);
      stage.setScene(scene);
      stage.setResizable(false);
      
      playPauseButton.setOnAction(new EventHandler<ActionEvent>() {

    		public void handle(ActionEvent arg0) {
    				if (currentTrack == null)
    					currentTrack = selectedTrack;
    				Player.playPause(currentTrack);
    				if (Player.getStatusString().equals("PLAYING")) {
    					playPauseButton.setText(">");
    				}
    				else
    					playPauseButton.setText("||");
    			}			
           });
      
      stopButton.setOnAction(new EventHandler<ActionEvent>() {

		public void handle(ActionEvent event) {	
			Player.stop();
			playPauseButton.setText(">");
		}
    	  
      });
        
      
      addTrackButton.setOnAction(new EventHandler<ActionEvent>() {

  		public void handle(ActionEvent arg0) {			
  				addTrack(getPathForAdding());
  				tracksView.getSelectionModel().select(0);
  				selectedTrack = tracks.get(tracksView.getSelectionModel().getSelectedIndex());
  			}			
      	   
         });
      
      removeTrackButton.setOnAction(new EventHandler<ActionEvent>(){
    	  public void handle(ActionEvent arg0) {
    		  removeTrack(selectedTrack);
    	  }
      });
      
      tracksView.setOnMouseClicked(new EventHandler<MouseEvent>() {
    	  
    	  public void handle(MouseEvent event) {
    		  if (event.getButton().equals(MouseButton.PRIMARY)) {
    				  selectedTrack = tracks.get(tracksView.getSelectionModel().getSelectedIndex());
    				  System.out.println("selected: " + selectedTrack.toString());
    				  if (new File(selectedTrack.getPath()).exists()) {
    					  if (event.getClickCount() == 2) {
    						  currentTrack = selectedTrack;
    						  System.out.println("current: " + currentTrack.toString());
    						  Player.stop();
    						  Player.playPause(currentTrack);   					  
    						  playPauseButton.setText("||");
    					  }
    					}
    				  else
    					  removeTrack(selectedTrack);
    		  			}
    		  		
    	  }
      });
   
      timeSlider.valueProperty().addListener(new InvalidationListener() {
  	    public void invalidated(Observable ov) {
  	       if (timeSlider.isValueChanging()) {	    	   
  	          Player.seek(timeSlider.getValue());
  	          updateValues();
  	       }
  	    }
  	});
      
      timeSlider.setOnMousePressed(new EventHandler<MouseEvent>(){
  		public void handle(MouseEvent event) {
  			if (event.getButton().equals(MouseButton.PRIMARY)) {
  				Player.seek(timeSlider.getValue());
  				updateValues();
  			} 			
  		}      	   
         });
      
      
    //change volume by slide the volumeslider
      volumeSlider.valueProperty().addListener(new InvalidationListener() {
   	   public void invalidated(Observable ov) {
   		   if (volumeSlider.isValueChanging()) {
   			  Player.setVolume(volumeSlider.getValue());
   		   }
   	   }
      });
      
      //change volume by click the volumeslider
      volumeSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
   	   public void handle(MouseEvent event) {
   		   if (event.getButton().equals(MouseButton.PRIMARY)) {
   			   Player.setVolume(volumeSlider.getValue());
   		   }
   	   }
      });
      
 
      stage.show();
	}

	String getPathForAdding() {
		chooser = new FileChooser();
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio (*.mp3)", "*.mp3"));
		File file = chooser.showOpenDialog(stage);
		if (file != null) {
			return (file.getAbsolutePath().replace("\\", "/"));
		}
		return null;
	}
	
	
	void addTrack(String path) {
		
		tracks.add(new Track(DBWork.addTrack(path), path, MetadataExtractor.getMetaTitle(path), 
				MetadataExtractor.getMetaArtist(path), MetadataExtractor.getMetaAlbum(path)));
		
	}
	
	void removeTrack(Track track) {
		DBWork.removeTrack(track.getId());
		tracks.remove(track);
	}
	
	protected static void updateValues() {
		  if (playTime != null && timeSlider != null) /*&& volumeSlider != null) */{
		     Platform.runLater(new Runnable() {
		        public void run() {
		          playTime.setText(formatTime(Player.getCurrentTime(), Player.getTrackDuration()));
		          timeSlider.setDisable(Player.getTrackDuration().isUnknown());
		          if (!timeSlider.isDisabled() 
		            && Player.getTrackDuration().greaterThan(Duration.ZERO) 
		            && !timeSlider.isValueChanging()) {
		        	  timeSlider.setValue(Player.getCurrentTime().divide(Player.getTrackDuration()).toMillis()
			                  * 100.0);
		          }
		        }
		     });
		  }
		}
	
	private static String formatTime(Duration elapsed, Duration duration) {
		   int intElapsed = (int)Math.floor(elapsed.toSeconds());
		   int elapsedHours = intElapsed / (60 * 60);
		   if (elapsedHours > 0) {
		       intElapsed -= elapsedHours * 60 * 60;
		   }
		   int elapsedMinutes = intElapsed / 60;
		   int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 
		                           - elapsedMinutes * 60;
		 
		   if (duration.greaterThan(Duration.ZERO)) {
		      int intDuration = (int)Math.floor(duration.toSeconds());
		      int durationHours = intDuration / (60 * 60);
		      if (durationHours > 0) {
		         intDuration -= durationHours * 60 * 60;
		      }
		      int durationMinutes = intDuration / 60;
		      int durationSeconds = intDuration - durationHours * 60 * 60 - 
		          durationMinutes * 60;
		      if (durationHours > 0) {
		         return String.format("%d:%02d:%02d/%d:%02d:%02d", 
		            elapsedHours, elapsedMinutes, elapsedSeconds,
		            durationHours, durationMinutes, durationSeconds);
		      } else {
		          return String.format("%02d:%02d/%02d:%02d",
		            elapsedMinutes, elapsedSeconds,durationMinutes, 
		                durationSeconds);
		      }
		      } else {
		          if (elapsedHours > 0) {
		             return String.format("%d:%02d:%02d", elapsedHours, 
		                    elapsedMinutes, elapsedSeconds);
		            } else {
		                return String.format("%02d:%02d",elapsedMinutes, 
		                    elapsedSeconds);
		            }
		        }
		    }
}
