
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    /**
     * BOARD SIZE 360 x 640
     */
    int boardWidth = 360;
    int boardHeight = 640;

    /**
     * IMAGES
     * --------------------------------
     * (1) background
     * (2) bird
     * (3) top pipe
     * (4) bottom pipe
     */
    BufferedImage backgroundImg;
    BufferedImage birdImg;
    BufferedImage topPipeImg;
    BufferedImage bottomPipeImg;

    /**
     * BIRD
     * --------------------------------
     * BIRD SIZE 34 x 24
     * BIRD POSITION (birdX, birdY) = (boardWidth / 8, boardHeight / 2)
     */
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    /**
     * BIRD CLASS
     * (1) x, y, width, height
     * (2) img
     * (3) constructor -- Bird(Image img)
     */
    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        BufferedImage img;

        Bird(BufferedImage img) {
            this.img = img;
        }
    }

    /**
     * PIPE
     * --------------------------------
     * PIPE SIZE 64 x 512
     * PIPE POSITION (pipeX, pipeY) = (boardWidth, 0)
     */
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; // scaled by 1/6
    int pipeHeight = 512;

    /**
     * PIPE CLASS
     * (1) x, y, width, height
     * (2) img
     * (3) passed
     * (4) constructor -- Pipe(Image img)
     */
    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        BufferedImage img;
        boolean passed = false;

        Pipe(BufferedImage img) {
            this.img = img;
        }
    }

    /**
     * GAME LOGIC
     * --------------------------------
     * (1) bird
     * (2) velocityX, velocityY, gravity
     * (3) placePipeDelay
     * (4) pipes
     * (5) random
     * (6) TIMERS - gameLoop, placePipesTimer
     * (7) gameOver, gameWon, gameStarted
     * (8) score
     */
    Bird bird;
    int velocityX = -4; // move pipes to the left speed
    int velocityY = 0;
    int gravity = 1;
    int placePipeDelay = 2000; // 2 seconds
    // pipes
    ArrayList<Pipe> pipes;
    Random random = new Random();
    // timers
    Timer gameLoop;
    Timer placePipesTimer;
    // game state
    boolean gameOver = false;
    boolean gameWon = false;
    // score
    double score = 0;

    /**
     * CONSTRUCTOR
     * --------------------------------
     * (1) setPreferredSize - boardWidth, boardHeight
     * (2) setFocusable - true (focus on key events)
     * (3) addKeyListener - this (listen to key events)
     * (4) load images
     * (5) create bird
     * (6) create pipes
     * (7) place pipes timer
     * (8) game loop
     */

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.BLUE);
        setFocusable(true); // focus on key events
        addKeyListener(this); // listen to key events

        // load images
        // backgroundImg = new
        // ImageIcon(getClass().getResource("./img/flappybirdbg.png")).getImage();
        // birdImg = new
        // ImageIcon(getClass().getResource("./img/flappybird.png")).getImage();
        // topPipeImg = new
        // ImageIcon(getClass().getResource("./img/toppipe.png")).getImage();
        // bottomPipeImg = new
        // ImageIcon(getClass().getResource("./img/bottompipe.png")).getImage();

        // create bird

        loadImages();

        bird = new Bird(birdImg);
        // create pipes
        pipes = new ArrayList<Pipe>();

        // place pipes timer
        placePipesTimer = new Timer(placePipeDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        // game loop
        gameLoop = new Timer(1000 / 60, this); // 60 fps
        gameLoop.start();
    }

    public void loadImages() {

        ImageLoader loader = new ImageLoader();

        // ClassLoader cl = getClass().getClassLoader();
        // URL backgroundUrl = cl.getResource("./img/flappybirdbg.png");
        // URL birdUrl = cl.getResource("./img/flappybird.png");
        // URL topPipeUrl = cl.getResource("./img/toppipe.png");
        // URL bottomPipeUrl = cl.getResource("./img/bottompipe.png");

        backgroundImg = loader.loadImage("src/img/flappybirdbg.png");
        birdImg = loader.loadImage("src/img/flappybird.png");
        topPipeImg = loader.loadImage("src/img/toppipe.png");
        bottomPipeImg = loader.loadImage("src/img/bottompipe.png");

        backgroundImg.setData(backgroundImg.getData());
        birdImg.setData(birdImg.getData());
        topPipeImg.setData(topPipeImg.getData());
        bottomPipeImg.setData(bottomPipeImg.getData());
    }

    /**
     * PLACE PIPES
     * --------------------------------
     * (1) randomPipeY
     * (2) openingSpace
     * (3) topPipe
     * (4) bottomPipe
     */
    void placePipes() {
        // (0-1) * pipeHeight / 2 -> (0, 256)
        // 128
        // 0 - 128 - (0-256) --> pipeHeight/4 -> 3/4 pipeHeight
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY; // top pipe
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    /**
     * PAINT COMPONENT
     * --------------------------------
     * call super.paintComponent(g) from JPanel
     * call draw(g)
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    /**
     * DRAW
     * --------------------------------
     * (1) draw background
     * (2) draw bird
     * (3) draw pipes
     * (4) draw score
     * (5) repaint
     */
    public void draw(Graphics g) {
        // System.out.println("draw"); // debug
        // draw background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        /**
         * SCORE
         */
        g.setColor(Color.WHITE);
        g.setFont(new Font("Helvetica", Font.BOLD, 32));

        /**
         * GAME OVER
         * --------------------------------
         * if gameOver
         * (1) stop placePipesTimer
         * (2) stop gameLoop
         * (3) display "Game Over: score"
         * else
         * (1) display score
         */
        if (gameOver && !gameWon) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        /**
         * GAME WON
         * --------------------------------
         * if gameWon
         * (1) display "Game Won: score"
         */
        else if (gameWon) {
            g.drawString("Game Won: " + String.valueOf((int) score), boardWidth / 2, boardHeight / 2);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    /**
     * MOVE
     * --------------------------------
     * (1) bird
     * (2) pipes
     * (3) collision
     * (4) gameOver
     */
    public void move() {
        /**
         * BIRD
         * --------------------------------
         * (1) velocityY += gravity
         * (2) bird.y += velocityY
         * (3) bird.y = Math.max(bird.y, 0)
         */
        velocityY += gravity; // gravity
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); // bird.y >= 0 (top)

        /**
         * PIPES
         * --------------------------------
         * (1) pipe.x += velocityX
         */
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            /**
             * PASSING PIPES
             * --------------------------------
             * if pipe is not passed and bird is to the right of the pipe
             */
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5; // 0.5 for each pipe passed (top and bottom) (1 for each pair)
                pipe.passed = true;
            }

            /**
             * INCREASE PIPE SPEED
             * --------------------------------
             * for every 10 points increase pipe speed
             */
            if (score % 10 == 0 && score != 0) {
                increasePipeSpeed();
            }

            /**
             * COLLISION WITH PIPES | GAME OVER
             * --------------------------------
             * if bird collides with pipe
             */
            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        /**
         * GAME OVER
         * --------------------------------
         * if bird is below the boardHeight
         */
        if (bird.y > boardHeight) {
            gameOver = true;
        }

        /**
         * GAME WON
         * --------------------------------
         * if score reaches 100
         * gameWon = true
         * gameOver = true
         */
        if (score >= 100) {
            gameWon = true;
            gameOver = true;
        }

    }

    /**
     * COLLISION
     * --------------------------------
     * (1) bird left < pipe right
     * (2) bird right > pipe left
     * (3) bird top < pipe bottom
     * (4) bird bottom > pipe top
     */

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && // bird left < pipe right
                a.x + a.width > b.x && // bird right > pipe left
                a.y < b.y + b.height && // bird top < pipe bottom
                a.y + a.height > b.y; // bird bottom > pipe top
    }

    /**
     * INCREASE PIPE SPEED
     * --------------------------------
     * decrease placePipeDelay by 100 milliseconds
     */
    void increasePipeSpeed() {
        placePipeDelay -= 100;
    }

    /**
     * ACTION PERFORMED
     * --------------------------------
     * Override actionPerformed from ActionListener
     * (1) move
     * (2) repaint
     * (3) gameOver
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    /**
     * KEY EVENTS
     * --------------------------------
     * Override keyTyped, keyPressed, keyReleased from KeyListener
     * keyPressed -- space bar
     */
    /**
     * KEY PRESSED
     * --------------------------------
     * (1) space bar -- jump
     * 
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9; // jump
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (gameOver) {
                // reset game
                bird.y = birdY;
                pipes.clear();
                score = 0;
                gameOver = false;
                placePipesTimer.start();
                gameLoop.start();
            }
        }
    }

    /**
     * KEY TYPED
     * --------------------------------
     * not used
     * 
     * KEY RELEASED
     * --------------------------------
     * not used
     */

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
