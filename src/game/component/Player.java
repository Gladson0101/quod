package game.component;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

import game.component.Util;

public class Player extends Ship {

	protected ImageIcon ship;
	private boolean see;
	int cont = 0;

	/*
	 * Construtor
	 */
	public Player() {
		super(Util.PLAYER_POSITION_X, Util.PLAYER_POSITION_Y, Util.PLAYER_WIDTH, Util.PLAYER_HEIGHT, Util.SPEED_MEDIUM,
				Util.SPEED_MEDIUM, true);

	}

	@Override
	public void draw(Graphics g) {
		// Altera a imagem do array
		if (Util.hit == false)
				ship = new ImageIcon("res\\ship\\PlayerShip\\PlayerShipSprite.png");
			

		if (Util.hit) {

			if (see) {
				ship = new ImageIcon("res\\ship\\PlayerShip\\palyerHit.png");
				see = false;
				cont++;

			} else {
				ship = new ImageIcon("res\\ship\\PlayerShip\\PlayerShipSprite.png");
				see = true;
				cont++;
			}
		}

		if (cont >= 50) {
			Util.hit = false;
			cont = 0;
		}
		// Verifica se pode disparar
		if (shoot) {
			alLaser.add(new PlayerLaser(getX() + 25, getY() + 5, 47, 30, true));

			setShoot(false);
		}

		// Verifica se pode disparar
		if (shoot) {
			alLaser.add(new PlayerLaser(getX() + 25, getY() + 5, 47, 30, true));

			setShoot(false);
		}

		Image imageShip = ship.getImage();
		g.drawImage(imageShip, getX(), getY(), getWidth(), getHeight(), null);
	}
}