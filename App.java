import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        int boardwidth = 360;           //this is the game window width 
        int boardlength = 640;          //this is the game window height

        JFrame frame = new JFrame("Floppy Bird");   
        frame.setSize(boardwidth, boardlength);   
        //frame.setVisible(true);                    //to make frame visible
        frame.setLocationRelativeTo(null);         //to set the frame at cetre
        frame.setResizable(false);                 //to make sure user don't resize the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       //to terminate the program when window 'X' buttom pressed
        
        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);                   //to add the game to the frame
        frame.pack();    //make sure blue color doesn't go to the heading bar
        flappyBird.requestFocus(); 
        frame.setVisible(true);
    }
}
