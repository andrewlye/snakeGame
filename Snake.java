// CS3 Final Project: Snake Game
// Andrew Ye, May 18 2022

import java.io.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.plaf.ActionMapUIResource;

import java.util.*;

public class Snake extends JPanel {
	private static final long serialVersionUID = -7797149062100634189L;
	private JFrame frame;
	private JLabel labelInstructions;
	private Timer resetImageTimer;
	private Timer resetMouthTimer;
	// snake, food, and game over images
	private Image imageHeadRight;
	private Image imageHeadRightOpen;
	private Image imageHeadLeft;
	private Image imageHeadLeftOpen;
	private Image imageHeadUp;
	private Image imageHeadUpOpen;
	private Image imageHeadDown;
	private Image imageHeadDownOpen;
	private Image imageHead; 
	private Image imageBody;
	private Image imageFood;
	private Image imageGameOver;
	// directional booleans
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	//dots keeps track of snake length
	private int dots;
	//position arrays for dots
	private ArrayList<Integer> xPos;
	private ArrayList<Integer> yPos;
	//food x and y coordinates
	private int foodX;
	private int foodY;
	private boolean gameOver;
	private boolean toMove; // boolean to prevent multiple successive inputs
	private int score;
	private boolean mouthOpen;

	public Snake() {
		resetImageTimer = null;
		resetMouthTimer = null;
		Dimension dimension = new Dimension(300, 300);
		setPreferredSize(dimension);
		frame = new JFrame("Snake Game");
		labelInstructions = new JLabel("Welcome to Snake Game!");
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		textPanel.add(labelInstructions);
		frame.getContentPane().add(this, "North");
		frame.getContentPane().add(textPanel, "South");
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		left = false;
		right = true;
		up = false;
		down = false;
		dots = 3; // starting length of 3 dots + head
		xPos = new ArrayList<Integer>();
		yPos = new ArrayList<Integer>();
		xPos.add(150);
		yPos.add(150);
		gameOver = false;
		score = 0;
		foodX = ((int) (Math.random() * 30)) * 10; // food is initialized to a random position
		foodY = ((int) (Math.random() * 30)) * 10;
		toMove = true;
		mouthOpen = false;

		for (int i = 1; i <= dots; i++) // adding initial dot x and y positions
		{
			yPos.add(yPos.get(0) + (10 * i));
			xPos.add(xPos.get(0));
		}

		try {
			File imageFileHeadRight = new File("headright.png");
			imageHeadRight = ImageIO.read(imageFileHeadRight).getScaledInstance(10, 10, Image.SCALE_DEFAULT);
			File imageFileHeadRightOpen = new File("headrightopen.png");
			imageHeadRightOpen = ImageIO.read(imageFileHeadRightOpen).getScaledInstance(10, 10, Image.SCALE_DEFAULT);
			File imageFileHeadLeft = new File("headleft.png");
			imageHeadLeft = ImageIO.read(imageFileHeadLeft).getScaledInstance(10, 10, Image.SCALE_DEFAULT);
			File imageFileHeadLeftOpen = new File("headleftopen.png");
			imageHeadLeftOpen = ImageIO.read(imageFileHeadLeftOpen).getScaledInstance(10, 10, Image.SCALE_DEFAULT);
			File imageFileHeadUp = new File("headup.png");
			imageHeadUp = ImageIO.read(imageFileHeadUp).getScaledInstance(10, 10, Image.SCALE_DEFAULT);
			File imageFileHeadUpOpen = new File("headupopen.png");
			imageHeadUpOpen = ImageIO.read(imageFileHeadUpOpen).getScaledInstance(10, 10, Image.SCALE_DEFAULT);
			File imageFileHeadDown = new File("headdown.png");
			imageHeadDown = ImageIO.read(imageFileHeadDown).getScaledInstance(10, 10, Image.SCALE_DEFAULT);
			File imageFileHeadDownOpen = new File("headdownopen.png");
			imageHeadDownOpen = ImageIO.read(imageFileHeadDownOpen).getScaledInstance(10, 10, Image.SCALE_DEFAULT);
			File imageFileDot = new File("dot.png");
			imageBody = ImageIO.read(imageFileDot).getScaledInstance(10, 10, Image.SCALE_DEFAULT);
			File imageFileFood = new File("apple.png");
			imageFood = ImageIO.read(imageFileFood).getScaledInstance(10, 10, Image.SCALE_DEFAULT);
			File imageFileGameOver = new File("gameover.png");
			imageGameOver = ImageIO.read(imageFileGameOver).getScaledInstance(300, 300, Image.SCALE_DEFAULT);
			imageHead = imageHeadRight;
		} catch (IOException e) {
			System.out.println("Exception: " + e);
		}


		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				int key = e.getKeyCode();

				if ((key == KeyEvent.VK_LEFT) && (!right) && toMove)
				{
					left = true;
					up = false;
					down = false;
					toMove = false;
				}
				if ((key == KeyEvent.VK_RIGHT) && (!left) && toMove)
				{
					right = true;
					up = false;
					down = false;
					toMove = false;
				}
				if ((key == KeyEvent.VK_UP) && (!down) && toMove)
				{
					up = true;
					left = false;
					right = false;
					toMove = false;
				}
				if ((key == KeyEvent.VK_DOWN) && (!up) && toMove)
				{
					down = true;
					left = false;
					right = false;
					toMove = false;
				}
			}
		});

		

		resetImageTimer = new Timer(200, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				labelInstructions.setText("Score: " + score + " | Arrow keys to move");
				move();
				toMove = true;
				checkCollision();
				checkFood();
				repaint();	
			}
		});

		resetMouthTimer = new Timer(130, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (mouthOpen)
				{
					mouthOpen = false;
					if (left)
						imageHead = imageHeadLeftOpen;
					if (right)
						imageHead = imageHeadRightOpen;
					if (down)
						imageHead = imageHeadDownOpen;
					if (up)
						imageHead = imageHeadUpOpen;
				}
				else
				{
					mouthOpen = true;
					if (left)
						imageHead = imageHeadLeft;
					if (right)
						imageHead = imageHeadRight;
					if (down)
						imageHead = imageHeadDown;
					if (up)
						imageHead = imageHeadUp;
				}
				repaint();
			}

		});

		frame.setVisible(true);
		frame.setFocusable(true);
		frame.setAutoRequestFocus(true);

		resetImageTimer.start();
		resetMouthTimer.start();
	}

	private void move()
	{
		for (int i = dots; i > 0; i--)
		{
			xPos.set(i, xPos.get(i - 1));
			yPos.set(i, yPos.get(i - 1));
		}

		if (left)
			xPos.set(0, xPos.get(0) - 10);
		if (right)
			xPos.set(0, xPos.get(0) + 10);
		if (up)
			yPos.set(0, yPos.get(0) - 10);
		if (down)
			yPos.set(0, yPos.get(0) + 10);
	}

	private void checkCollision()
	{
		int x = xPos.get(0);
		int y = yPos.get(0);

		for(int i = dots; i > 0; i--)
		{
			if (x == xPos.get(i) && y == yPos.get(i))
				gameOver = true;
		}

		if (xPos.get(0) > 300 || xPos.get(0) < 0)
		{
			gameOver = true;
		}
		if (yPos.get(0) > 300 || yPos.get(0) < 0)
		{
			gameOver = true;
		}
	}

	private void checkFood()
	{
		if (xPos.get(0) <= foodX + 5 && xPos.get(0) >= foodX - 5)
		{
			if (yPos.get(0) <= foodY + 5 && yPos.get(0) >= foodY - 5)
			{
				dots++;
				score++;
				foodX = ((int) (Math.random() * 20)) * 10;
				foodY = ((int) (Math.random() * 20)) * 10;

				if (right)
				{
					xPos.add(xPos.get(xPos.size() - 1) - 10);
					yPos.add(yPos.get(yPos.size() - 1));
					
				}
				if (left)
				{
					xPos.add(xPos.get(xPos.size() - 1) + 10);
					yPos.add(yPos.get(yPos.size() - 1));
				}
				if (up)
				{
					yPos.add(yPos.get(yPos.size() - 1) + 10);
					xPos.add(xPos.get(xPos.size() - 1));
				}
				if (down)
				{
					yPos.add(yPos.get(yPos.size() - 1) - 10);
					xPos.add(xPos.get(xPos.size() - 1));
				}
			}
		}
	}

	public void paint(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		g2d.setBackground(Color.BLACK);
		g2d.clearRect(0, 0, 300, 300);
		if (gameOver)
		{
			g2d.drawImage(this.imageGameOver, 0, 0, null);
			return;
		}
		g2d.drawImage(this.imageHead, xPos.get(0), yPos.get(0), null); // draw head

		for(int i = 1; i < xPos.size(); i++)
		{
			g2d.drawImage(this.imageBody, xPos.get(i), yPos.get(i), null); // draw body
		}

		g2d.drawImage(this.imageFood, foodX, foodY, null); // draw food

	}

	public static void main(String[] args) throws Exception {
		new Snake(); // constructor leaves the UI running
	}
}