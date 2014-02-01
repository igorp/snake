import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Snake extends javax.swing.JFrame implements KeyListener, Runnable {

    final static int BLOCK_SIZE = 20;
    final static int screenX = 700;
    final static int screenY = 400;
    final static int length = 2;
    int hiScore;
    //Thread to repaint
    Thread t = new Thread(this);
    boolean stopped = false;
    //Instance of logic class
    SnakeLogic snake = new SnakeLogic(length);

    public Snake() {
        super("Snake");
        t.start();

        JPanel p = new JPanel();
        p.setFocusable(true);
        p.addKeyListener(this);
        add(p);

        setIgnoreRepaint(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(screenX, screenY);
        setBackground(Color.WHITE);

        setIconImage(new ImageIcon("icon.png").getImage());
        setResizable(false);

        setVisible(true);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Snake();
            }
        });
    }

    public void paint(Graphics g) {

        Point2 food = snake.getFoodLocation();
        g.clearRect(0, 0, screenX, screenY);
        //draw snake
        if (snake.lightsOut() == false) {
            normalPaint(g);
        } else {
            lightsOffPaint(g);
        }
        //food drawn
        g.setColor(new Color(34, 139, 34));
        g.fillRect(BLOCK_SIZE * food.getX() + 10, BLOCK_SIZE * food.getY() + 10, BLOCK_SIZE, BLOCK_SIZE);
        g.setColor(Color.BLACK);
        //powerup drawn
        if (snake.getPowerUpOnScreen() == true) {
            Point2 powerUp = snake.getPowerUpLocation();
            g.setColor(Color.RED);
            g.fillRect(BLOCK_SIZE * powerUp.getX() + 10, BLOCK_SIZE * powerUp.getY() + 10, BLOCK_SIZE, BLOCK_SIZE);
            g.setColor(Color.BLACK);
        }
        //score drawn
        g.setFont(new Font("LucidaSans", Font.PLAIN, 16));
        g.drawString("Score: " + snake.getScore(), 20, 50);
        g.drawString("Hi-Score: " + hiScore, 20, 70);
    }

    public void normalPaint(Graphics g) {
        Point2[] snakeLocation = snake.getLocation();
        for (int j = 0; j < snake.getLength(); j++) {
            //each block drawn individually, 20 is size of snake, -10 is to match window borders
            g.fillRect(BLOCK_SIZE * snakeLocation[j].getX() + 10, BLOCK_SIZE * snakeLocation[j].getY() + 10, BLOCK_SIZE, BLOCK_SIZE);
        }
    }

    public void lightsOffPaint(Graphics g) {
        Point2[] snakeLocation = snake.getLocation();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenX, screenY);
        g.setColor(Color.WHITE);
        g.fillRect(BLOCK_SIZE * snakeLocation[0].getX() + 10, BLOCK_SIZE * snakeLocation[0].getY() + 10, BLOCK_SIZE, BLOCK_SIZE);
    }

    //KeyListener functions
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                snake.setDirection(Direction.UP);
                break;
            case KeyEvent.VK_RIGHT:
                snake.setDirection(Direction.RIGHT);
                break;
            case KeyEvent.VK_DOWN:
                snake.setDirection(Direction.DOWN);
                break;
            case KeyEvent.VK_LEFT:
                snake.setDirection(Direction.LEFT);
                break;
        }

    }

    public void keyReleased(KeyEvent e) {
    }

    public void run() {
        long tick = System.currentTimeMillis();

        while (!stopped) {
            try {
                t.sleep(snake.period);
            } catch (InterruptedException e) {
            }

            snake.updateSnake();
            if (snake.endGame()) {
                try {
                    t.sleep(2000);
                } catch (InterruptedException e) {
                }
                if (snake.getScore() > hiScore) {
                    hiScore = snake.getScore();
                }
                snake.init(length);
            }
            tick = System.currentTimeMillis();
            repaint();
        }
    }
}
