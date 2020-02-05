
package ca.Pong;

import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

/**The classic game of Pong!
 * Pong
 *
 * @author Eamonn Alphin
 * @version 2017
 */
public class Pong extends Application {

    /**
     * The width of the screen.
     */
    private final int screenWidth = 500;
    
    
    /**
     * The height of the screen. 
     */
    private final int screenHeight = 500;
    
    
    /**
     * The width of each paddle. 
     */
    private final int paddleWidth = 10;
    
    
    /**
     * The height of each paddle. 
     */
    private final int paddleHeight = 100;
    
    /**
     * The width of the ball. 
     */
    private final int ballWidth = 10;
    
    /**
     * The height of each ball. 
     */
    private final int ballHeight = 10;
    
    /**
     * The farthest point down the screen a paddle can travel. 
     */
    private final int paddleLimit = screenHeight - paddleHeight;
    
    /**
     * Player 1's paddle (Player 1 is human). 
     */
    private Rectangle paddle1;
    
    /**
     * Player 2's paddle (Player 2 is the computer). 
     */
    private Rectangle paddle2;
    
    /**
     * The "ball". A rectangle, just like the original. 
     */
    private Rectangle ball;
    
    
    /**
     * Paddle 1's score. 
     */
    private int paddle1Score;
    
    
    /**
     * Paddle 2's score. 
     */
    private int paddle2Score;
    
    /**
     * The score text associated with Paddle 1.
     */
    private Text paddle1Text;
    
    /**
     * The score text associated with Paddle 2.
     */
    private Text paddle2Text;
    
    /**
     * The group containing everything on screen. 
     */
    private Group mainGroup;
    
    /**
     * The speed of the user's paddle. 1 is the fastest. 
     */
    private int userSpeed;
    
    /**
     * The starting speed of the computer paddle. 
     */
    private int compSpeed;
    
    
    /**
     * How much to increment or decrement the computer speed by
     * over time. 
     */
    private final int speedIncrement = 50;
    
    /**
     * How fast the ball moves. Higher is slower. 
     */
    private final int ballSpeed = 1000;
    
    /**
     * The fastest speed the ball can travel.
     */
    private final int maxCompSpeed = 10;
    
    /**
     * The initial x coordinate of the ball.
     */
    private final double ballStartX = screenWidth / 2;
    
    /**
     * The initial y coordinate of the ball.
     */
    private final double ballStartY = screenHeight / 2;
    
    /**
     * The y (up or down) direction the ball is traveling in. 
     */
    private double yDirection; 
    
    /**
     * Adjusts the angle in y the ball moves in. 
     */
    private double yMultiplier;
    
    /**
     * Adjusts the angle in y the ball moves in
     * by being multiplied by a random number between 0
     * and 1. The higher this number, the sharper the
     * angle the ball can bounce at. 
     */
    private final int yScaleSpeed = 5;
    
    /**
     * The rightmost limit the ball can travel.  
     */
    private final int maxX = screenWidth;
    
    /**
     * The leftmost limit the ball can travel. 
     */
    private final int minX = 0;
    
    /**
     * The bottom-most limit the ball can travel. 
     */
    private final int maxY = screenHeight - ballHeight;
    
    /**
     * The upper-most limit the ball can travel. 
     */
    private final int minY = 0;
    
    /**Drives the program. 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        userSpeed = 1;
        final int startingSpeed = 100;
        compSpeed = startingSpeed;
        
        
        paddle1 = new Rectangle(screenWidth - screenWidth, 
                screenHeight - screenHeight, 
                paddleWidth, 
                paddleHeight);
        paddle1.setFill(Color.WHITE);
        
        paddle2 = new Rectangle(screenWidth - paddleWidth, 
                screenHeight - paddleHeight, 
                paddleWidth, 
                paddleHeight);
        paddle2.setFill(Color.WHITE);
        
        ball = new Rectangle(screenWidth / 2,
                screenHeight / 2,
                ballWidth,
                ballHeight);
        ball.xProperty().addListener(this::ballMovedInX);
        ball.yProperty().addListener(this::ballMovedInY);
        ball.setFill(Color.WHITE);
        
        //start off in the center. 
        movePaddle(paddle1, ((screenHeight / 2) - (paddleHeight / 2)));
        movePaddle(paddle2, ((screenHeight / 2) - (paddleHeight / 2)));
        
        
        mainGroup = new Group(paddle1, paddle2, ball);
        Scene mainScene = new Scene(mainGroup, 
                screenWidth, 
                screenHeight, 
                Color.BLACK);
        mainScene.setOnMouseMoved(this::moveMousePaddle);
        primaryStage.setTitle("Pong");
        primaryStage.setScene(mainScene);

        moveBall(1);
        primaryStage.show();
        
        
    }
    
    
    /**Sets the position of the user's paddle to the mouse location. 
     * @param e - the mouse moving. 
     */
    private void moveMousePaddle(MouseEvent e) {
        Double mouseYPosition = e.getY() - (paddleHeight / 2);
        movePaddle(paddle1, mouseYPosition);
    }
    
    /**Moves a paddle up and down the screen. 
     * @param paddle - the paddle to move
     * @param paddleDestination - the destination y coordinate on the screen. 
     */
    private void movePaddle(Rectangle paddle, double paddleDestination) {
        Timeline timeline = new Timeline();
        int speed;
        
        if (paddle.equals(paddle1)) {
            speed = userSpeed;
        } else {
            if (compSpeed <= 0) {
                compSpeed = maxCompSpeed;
            }
            speed = compSpeed;
        } 

        Duration endDuration = new Duration(speed);
        if (paddleDestination <= 0) {
            paddleDestination = 0;
        }
        
        if (paddleDestination >= paddleLimit) {
            paddleDestination = paddleLimit;
        }
        
        KeyValue movePaddle = new KeyValue(paddle.yProperty(),
                paddleDestination);
        KeyFrame end = new KeyFrame(endDuration, movePaddle);
        timeline.getKeyFrames().addAll(end);
        timeline.play();
    }
    
    
    /**Moves the ball and paddle 2 back to the center. 
     * @param point - who made the last score. 
     */
    public void resetBall(int point) {
        
        ball.setX(ballStartX);
        ball.setY(ballStartY);
        
        paddle2.setY(ball.getY() - (paddleHeight / 2));
        
        final int delay = 1000;
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if (point == 1) {
            moveBall(1);
            
        } else {
            moveBall(-1);
        }
        
        
    }
    
    
    

    /**Moves the ball. 
     * @param xDir - the direction to move in x (left or right).
     */
    public void moveBall(int xDir) {
        double xEnd;
        
        switch (xDir) {
        case -1:
            xEnd = minX;
            break;
        case 0:
            xEnd = ball.getX();
            break;
        case 1:
            xEnd = maxX;
            break;
        default:
            xEnd = 0;
        }
        
        java.awt.Toolkit.getDefaultToolkit().beep();
        Timeline timeline = new Timeline();
        Duration endDuration = new Duration(ballSpeed);
        
        
        KeyValue moveX = new KeyValue(ball.xProperty(), 
                xEnd);

        KeyFrame end = new KeyFrame(endDuration, moveX);
        timeline.getKeyFrames().addAll(end);
        timeline.play();
        
    }
    
    
  
  /**Monitors the balls movement in the Y direction.  
 * @param property - the balls movement in Y.
 * @param oldValue - unused. 
 * @param newValue - unused
 */
private void ballMovedInY(ObservableValue<? extends Number> property,
        Object oldValue, Object newValue) {
      double ballYCoordinate = ball.getY() + ball.getTranslateY();
      boolean ballAtTop = (ballYCoordinate < minY);
      boolean ballAtBottom = (ballYCoordinate > (maxY));
      
     
     
      if (ballAtBottom) {
          yDirection = (-1);
      }
      if (ballAtTop) {
         yDirection = (1);
      }
      
  }
    
    /**Monitors the balls movement in the X direction.  
     * @param property - The balls movement in X. 
     * @param oldValue - unused. 
     * @param newValue - unused. 
     */
    private void ballMovedInX(ObservableValue<? extends Number> property,
            Object oldValue, Object newValue) {
        
        double ballXCoordinate = ball.getX();
        double ballYCoordinate = ball.getY();
        double paddle1Y = paddle1.getY() + paddle1.getTranslateY();
        double paddle2Y = paddle2.getY();
        double paddle1YStart = paddle1Y - ballHeight;
        double paddle1YEnd = paddle1Y + paddle1.getHeight();
        double paddle2YStart = paddle2Y - ballHeight;
        double paddle2YEnd = paddle2Y + paddle2.getHeight();

        boolean ballAtRight = (ballXCoordinate >= screenWidth);
        boolean ballAtLeft = (ballXCoordinate <= 0);
        boolean ballInPaddle1Range = (ballYCoordinate >= paddle1YStart 
                && ballYCoordinate <= paddle1YEnd);
        boolean ballInPaddle2Range = (ballYCoordinate >= paddle2YStart 
                && ballYCoordinate <= paddle2YEnd);
        
        //move paddle2
        movePaddle(paddle2, (ballYCoordinate - (paddleHeight / 2)));
        
       
        
        
        if (ballAtRight) {
            if (ballInPaddle2Range) {
                yDirection = randomY();
                int xdir = -1;
                moveBall(xdir);
                yMultiplier = Math.random() * yScaleSpeed;
               
            } else {
                scored(1);
            }
        } 
        
        if (ballAtLeft) {
            if (ballInPaddle1Range) {
                yDirection = randomY();
                int xdir = 1;
                moveBall(xdir);
                yMultiplier = Math.random() * yScaleSpeed;
            } else {
                scored(2); 
            }
        }
        
        
        ball.setY(ball.getY() + (yDirection * yMultiplier));
        updateScore();
        
        
 
    }
    
    
    
    /**Generates a random direction for the ball to travel in. 
     * @return 1 for up or -1 for down. 
     */
    private int randomY() {
        double toss = Math.random() * 1;
        int ydir;
        final double upperLimit = 0.5;
        if (toss > upperLimit) {
            ydir = 1;
        } else {
            ydir = -1;
        } 
        
        return ydir;
    }
    
    /**Adjusts the score and resets the ball when a player scores. 
     * @param player -the player that scored (1 for human).
     */
    private void scored(int player) {
        System.out.println("Score for Player " + player);
        if (player == 1) {
            paddle1Score++;
            resetBall(1);
            compSpeed -= speedIncrement;
        } else {
            paddle2Score++;
            resetBall(2);
            compSpeed += speedIncrement;
        }
    }
    
    /**
     * Updates the score on the screen. 
     */
    private void updateScore() {
        String p1String = "Player 1: " + paddle1Score;
        String p2String = "Player 2: " + paddle2Score;
        
        mainGroup.getChildren().remove(paddle1Text);
        mainGroup.getChildren().remove(paddle2Text);
        final double paddle1StringLocationX = (screenWidth / 2) - 100;
        final double stringLocationY = 10;
        final double paddle2StringLocationX = (screenWidth / 2) + 25;
        
        
        
        paddle1Text = new Text(paddle1StringLocationX,
                stringLocationY, p1String);
        paddle1Text.setFill(Color.WHITE);
        
        paddle2Text = new Text(paddle2StringLocationX,
                stringLocationY, p2String);
        paddle2Text.setFill(Color.WHITE);
        
        mainGroup.getChildren().addAll(paddle1Text, paddle2Text);
    }
    
    
    /**
     * Launches the JavaFX application.
     * 
     * @param args
     *            command line arguments
     */
    public static void main(String[] args) {
        
        launch(args);

    }

}
