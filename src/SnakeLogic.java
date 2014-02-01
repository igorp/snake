import java.util.Random;

enum Direction {
    UP, DOWN, LEFT, RIGHT
};

public class SnakeLogic {

    public static int period = 300;
    int snakeLength;
    int score;
    int startLength;
    final static int powerUpTime = 60;
    boolean lightsOut;
    int activePowerUpTime;
    Point2[] snakeLocation;
    Direction snakeDirection;
    Direction soughtDirection;
    Point2 food;
    Random rand;
    Point2 powerUp;
    boolean powerUpOnScreen;
    int powerUpCounter;
    boolean fastSpeed;

    public SnakeLogic(int startLength) {
        init(startLength);
    }

    public void init(int length) {
        rand = new Random();
        food = new Point2();
        powerUp = new Point2();
        snakeLocation = new Point2[128];
        snakeLength = length;
        powerUpCounter = powerUpTime;
        startLength = length;

        score = 0;
        powerUpOnScreen = false;
        lightsOut = false;

        snakeDirection = soughtDirection = Direction.RIGHT;
        setStartLocation(10, 10);
        setPointLocation(food);

        powerUp = new Point2();
    }

    //By knowing where head of snake is we set the rest of its coordinates
    private void setStartLocation(int x, int y) {
        //Make sure not out of bounds
        if (x > snakeLength) {
            for (int j = 0; j < snakeLength; j++) {
                snakeLocation[j] = new Point2((x - j), y);
            }
        } else {
            System.out.print("ERROR: snakeLength too long.");
            System.exit(0);
        }
    }

    public Point2[] getLocation() {
        return snakeLocation;
    }

    public boolean getPowerUpOnScreen() {
        return powerUpOnScreen;
    }

    public int getLength() {
        return snakeLength;
    }

    private void setPointLocation(Point2 item) {
        int Xrand;
        int Yrand;
        do {
            Xrand = rand.nextInt(33);
            Yrand = rand.nextInt(17) + 1;
        } while (checkCollisions(Xrand, Yrand, snakeLocation, 0) || checkPointCollision(Xrand, Yrand, food));
        item.setLocation(Xrand, Yrand);
    }

    private boolean checkPointCollision(int x, int y, Point2 p) {
        if (x == p.getX() && y == p.getY()) {
            return true;
        }
        return false;
    }

    public Point2 getFoodLocation() {
        return food;
    }

    public Point2 getPowerUpLocation() {
        return powerUp;
    }

    public void moveSnake() {
        //increase each point
        for (int j = snakeLength - 1; j > 0; j--) {
            snakeLocation[j].setX(snakeLocation[j - 1].getX());
            snakeLocation[j].setY(snakeLocation[j - 1].getY());
        }
    }

    public void updateSnake() {
        Point2 nextPoint = getNextPoint(new Point2(snakeLocation[0].getX(), snakeLocation[0].getY()));

        if (checkPowerUp(nextPoint)) {
            nextPoint = getNextPoint(new Point2(snakeLocation[0].getX(), snakeLocation[0].getY()));

        }
        checkFood(nextPoint);
        checkPowerUpTimeLeft();
        finalDirectionCheck();
        moveSnake();

        snakeLocation[0].setX(nextPoint.getX());
        snakeLocation[0].setY(nextPoint.getY());

    }

    private Point2 getNextPoint(Point2 point) {

        switch (snakeDirection) {
            case RIGHT:
                point.setX(point.getX() + 1);
                break;
            case UP:
                point.setY(point.getY() - 1);
                break;
            case DOWN:
                point.setY(point.getY() + 1);
                break;
            case LEFT:
                point.setX(point.getX() - 1);
                break;
        }
        //check borders
        if (point.getX() < 0) {
            point.setX(33);
        }
        if (point.getX() > 33) {
            point.setX(0);
        }
        if (point.getY() < 1) {
            point.setY(18);
        }
        if (point.getY() > 18) {
            point.setY(1);
        }

        return point;
    }

    //loop through array too see if coordinate (x, y) is there start is the starting point of checking (0 is head, 1 is else)
    private boolean checkCollisions(int x, int y, Point2[] array, int start) {
        for (int j = start; j < snakeLength; j++) {
            if (array[j].getX() == x && array[j].getY() == y) {
                return true;
            }
        }
        return false;
    }

    private void checkFood(Point2 point) {
        if (food.getX() == point.getX() && food.getY() == point.getY()) {
            score++;
            setPointLocation(food);
            snakeLength += 1;
            snakeLocation[snakeLength - 1] = new Point2();
            if (score % 10 == 0) {
                powerUpOnScreen = true;
                powerUp = new Point2();
                setPointLocation(powerUp);
            }
        }
    }

    public boolean checkPowerUp(Point2 point) {
        if (getPowerUpOnScreen()) {
            if (powerUp.getX() == point.getX() && powerUp.getY() == point.getY()) {
                activatePowerUp();
                score += 5;
                powerUpCounter = powerUpTime;
                powerUpOnScreen = false;
                powerUp = null;
                return true;
            }
            //make counter
            if (powerUpCounter-- == 0) {
                powerUpOnScreen = false;
                powerUp = null;
                powerUpCounter = powerUpTime;
            }
        }
        return false;
    }

    private void activatePowerUp() {
        int randNum = rand.nextInt(3);
        
        switch (randNum) {
            case 0:
                activateReverse();
                break;
            case 1:
                activateLightsOut();
                break;
            case 2:
                activateFastSpeed();
                break;
            default:
                break;
        }

    }

    private void activateLightsOut() {
        lightsOut = true;
        activePowerUpTime = 100;
    }

    private void activateReverse() {
        int tempX;
        int tempY;
        for (int h = 0; h < snakeLength / 2; h++) {
            tempX = snakeLocation[h].getX();
            snakeLocation[h].setX(snakeLocation[snakeLength - h - 1].getX());
            snakeLocation[snakeLength - h - 1].setX(tempX);

            tempY = snakeLocation[h].getY();
            snakeLocation[h].setY(snakeLocation[snakeLength - h - 1].getY());
            snakeLocation[snakeLength - h - 1].setY(tempY);
        }
        reverseDirection();
    }

    private void activateFastSpeed() {
        activePowerUpTime = 125;
        period = 30;
    }

    private void activateSlowSpeed() {
        activePowerUpTime = 25;
        period = 120;
    }

    private void reverseDirection() {
        if (snakeLocation[0].getX() + 1 == snakeLocation[1].getX()) {
            snakeDirection = Direction.LEFT;
        }
        if (snakeLocation[0].getX() - 1 == snakeLocation[1].getX()) {
            snakeDirection = Direction.RIGHT;
        }
        if (snakeLocation[0].getY() + 1 == snakeLocation[1].getY()) {
            snakeDirection = Direction.UP;
        }
        if (snakeLocation[0].getY() - 1 == snakeLocation[1].getY()) {
            snakeDirection = Direction.DOWN;
        }
    }

    public boolean lightsOut() {
        return lightsOut;
    }

    public void checkPowerUpTimeLeft() {
        if (activePowerUpTime-- < 1) {
            lightsOut = false;
            period = 60;
        }
    }

    public boolean endGame() {
        if (checkCollisions(snakeLocation[0].getX(), snakeLocation[0].getY(), snakeLocation, 1)) {
            return true;
        }
        return false;
    }

    public int getScore() {
        return score;
    }

    public void setDirection(Direction d) {

        boolean allow = true;
        switch (snakeDirection) {
            case RIGHT:
                if (d == Direction.LEFT) {
                    allow = false;
                }
                break;
            case UP:
                if (d == Direction.DOWN) {
                    allow = false;
                }
                break;
            case DOWN:
                if (d == Direction.UP) {
                    allow = false;
                }
                break;
            case LEFT:
                if (d == Direction.RIGHT) {
                    allow = false;
                }
                break;
        }
        if (allow) {
            soughtDirection = d;
        }
    }

    private void finalDirectionCheck() {
        boolean allow = true;
        switch (snakeDirection) {
            case RIGHT:
                if (soughtDirection == Direction.LEFT) {
                    allow = false;
                }
                break;
            case UP:
                if (soughtDirection == Direction.DOWN) {
                    allow = false;
                }
                break;
            case DOWN:
                if (soughtDirection == Direction.UP) {
                    allow = false;
                }
                break;
            case LEFT:
                if (soughtDirection == Direction.RIGHT) {
                    allow = false;
                }
                break;
        }
        if (allow) {
            snakeDirection = soughtDirection;
        }
    }

    //deprecated method for testing
    /*
    private void printSnake(Point2[] array) {
        for (int j = 0; j < snakeLength; j++) {
            System.out.println("Ar: " + j + " [" + array[j].getX() + "]" + "[" + array[j].getY() + "]");
        }
    }
    */
}
