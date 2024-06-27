import java.awt.*;
import java.awt.event.*;
import java.nio.channels.Pipe;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener,KeyListener{
    int boardwidth = 360;
    int boardlength = 640;
    //images
    Image backgroundImg;
    Image birdImg;
    Image topPipImg;
    Image bottomPipImg;

    //Bird
    int birdX = boardwidth/8;
    int birdY = boardwidth/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }
    }

    //pipes
    int pipeX = boardwidth;
    int pipeY = 0;
    int pipeWidth = 64;       //scale by 1/6
    int pipeHeight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        Boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }
    
    //game logic
    Bird bird;
    int velocityX = -4;  //move pipes to the left speed(simulates bird moving right)
    int velocityY = 0;    //move bird up/down speed
    int gravity = 1;

    ArrayList<Pipe>  pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    Boolean gameOver = false;
    double score = 0;
    
    FlappyBird(){
        setPreferredSize(new Dimension(boardwidth, boardlength));
      //  setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);  

        // load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        
        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //game timer
        gameLoop = new Timer(1000/60, this);        // 1000/60 = 16.6
        gameLoop.start();
    }

    public void placePipes(){
        //(0-1) *  pipeHeight/2 ->(0 - 256)
        //128
        //0 - 128 - (0-256) -->pipeHeight/4 -> 3/4 pipeHeight
        int randomPipeY = (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardlength/4;

        Pipe topPipe = new Pipe(topPipImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //background
        g.drawImage(backgroundImg, 0, 0, boardwidth, boardlength, null);

        //bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for(int i=0;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height,null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Areal", Font.PLAIN, 32));
        if(gameOver){
            g.drawString("Game Over" + String.valueOf((int) score),10,35);
        }
        else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move(){
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y,0);
        
        //pipes
        for(int i = 0; i<pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x+pipe.width){
                score += 0.5;   //0.5 because ther are 2 pipes! so 0.5*2 = 1, 1 for each set of pipes
                pipe.passed = true;
            }

            if(collision(bird, pipe)){
                gameOver = true;
            }
        }

        if(bird.y > boardlength){
            gameOver = true;
        }
    }

    public Boolean collision(Bird a , Pipe b){
        return a.x < b.x + b.width &&    //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&    //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&   //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;     //a's bottom left corner passes b's top left corner
    }

    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    };

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;

            if(gameOver){
                //restart game by resetting conditions 
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placePipesTimer.start();
            }
        }

    };
    
    public void keyReleased(KeyEvent e) {};
   
    public void keyTyped(KeyEvent e) {};

}
