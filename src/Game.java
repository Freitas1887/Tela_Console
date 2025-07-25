import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Game extends Canvas implements Runnable {

    public static JFrame frame;
    private Thread thread;
    private boolean isRunning = true;
    private final int WIDTH = 160;
    private final int HEIGHT = 120;
    private final int SCALE = 4;

    private BufferedImage image;

    private Spritesheet sheet;
    private BufferedImage player;
    private int x = 0;

    public Game(){
        sheet = new Spritesheet("/spritesheet.png");
        player = sheet.getSprite(0, 0, 16,16);
        setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        initFrame();
        image = new BufferedImage(160,120, BufferedImage.TYPE_INT_ARGB);
    }
    public void initFrame(){
        frame = new JFrame("Game #1");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void start(){
        thread = new Thread(this);
        isRunning = true;
        thread.start();

    }

    public  synchronized void stop(){
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String args[]){
    Game game = new Game();
    game.start();
    }

    public void tick(){
        x++;
    }
    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = image.getGraphics();
        g.setColor(new Color(250,216,168));
        g.fillRect(0,0,WIDTH,HEIGHT);

//        g.setColor(Color.GREEN);
//        g.fillRect(20,20,20,20);
//        g.setFont(new Font("Arial", Font.BOLD, 10));
//        g.setColor(Color.red);
//        g.drawString("Olá mundo", 90, 90);
        Graphics2D g2 = (Graphics2D) g;
        g2.rotate(Math.toRadians(90), 90, 90);
        g.drawImage(player, x,20,null);
        g.dispose();
        g = bs.getDrawGraphics();
        g.drawImage(image, 0 ,0, WIDTH*SCALE, HEIGHT*SCALE, null);
        bs.show();

    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();

        while(isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                tick();
                render();
                frames++;
                delta--;
            }

            if (System.currentTimeMillis() - timer >= 1000){
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }
        stop();
    }
}