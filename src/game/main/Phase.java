package game.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import game.component.Laser;
import game.component.Player;
import game.component.Util;
import game.enemy.*;

public class Phase extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static int score;
	public Timer timerEnemy;

	protected ImageIcon background;
	protected ImageIcon imgLife;

	public ArrayList<Enemy> alEnemy;
	public Player player;
	public Image explosion;

	protected int posLife;
	public int life;
	public int moveBackground;
	public boolean side = true;

	public JButton jbStop;

	protected ImageIcon imgText;
	protected ImageIcon imgBack;

	public Phase(String backgroundPath, int lastScore) {

		background = new ImageIcon(backgroundPath);

		life = 3;

		player = new Player();
		alEnemy = new ArrayList<Enemy>();

		explosion = new ImageIcon("res\\effects\\explosion.gif").getImage();
		imgLife = new ImageIcon("res\\ship\\life.png");

		moveBackground = -(Util.DEFAULT_SCREEN_HEIGHT * 9);
		score = lastScore;

		// Botao de pausa
		imgBack = new ImageIcon(" ");
		imgText = new ImageIcon("res\\menu\\stop.jpg");

		jbStop = new JButton();

		setLayout(null);

		jbStop.setBounds(Util.DEFAULT_SCREEN_WIDTH - 50, 5, 35, 35);
		add(jbStop);

		jbStop.setText(null);
		jbStop.setIcon(imgText); // texto do botão
		jbStop.setPressedIcon(imgBack); // Imagem ao clicar

		// borda
		jbStop.setBorderPainted(false);
		jbStop.setContentAreaFilled(false);

		// temporizado para gerar novo inimigo
		timerEnemy = new Timer(1750, new NewEnemy());

	}

	/*
	 * Essa classe instancia os novos inimigos no Timer
	 */
	private class NewEnemy implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			alEnemy.add(new EnemyTier04(new Random().nextInt(Util.DEFAULT_SCREEN_WIDTH - Util.ENEMY_WIDTH)));
		}

	}

	/*
	 * Esse metodo zera todos os atributos
	 */
	public void phaseClear() {

		player.alLaser.clear();
		player = new Player();

		for (int i = 0; i < alEnemy.size(); i++)
			alEnemy.get(i).alLaser.clear();

		alEnemy.clear();

	}

	/*
	 * Esse metodo faz a verificacao necessaria de colisao e de remocao de objetos
	 */
	public void phaseControl(Graphics g) {

		if (!Util.STOP) {
			// Movimenta o fundo
			if (moveBackground <= -Util.DEFAULT_SCREEN_HEIGHT)
				moveBackground += 3;
			else
				moveBackground = -(Util.DEFAULT_SCREEN_HEIGHT * 9);
		}

		/* Verificação colisão do Inimigo e do Laser */
		for (int i = 0; i < player.alLaser.size(); i++) {

			// Verifica se o laser saiu da tela
			if (player.alLaser.get(i).getY() <= -player.alLaser.get(i).getHeight())
				player.alLaser.get(i).setActive(false);

			// Verifica se atingiu algum inimigo ou se saiu da tela
			for (int j = 0; j < alEnemy.size(); j++) {
				if (Util.colision(player.alLaser.get(i), alEnemy.get(j)) && alEnemy.get(j).isActive()) {
					player.alLaser.get(i).setActive(false);
					alEnemy.get(j).setActive(false);

					score += 100;
				}

				if (alEnemy.get(j).getY() >= +Util.DEFAULT_SCREEN_HEIGHT) {
					alEnemy.get(j).setActive(false);
				}
			}
		}

		/* Verificação colisão do Player e do Laser */
		for (int i = 0; i < alEnemy.size(); i++) {
			Enemy enemy = alEnemy.get(i);

			for (int j = 0; j < enemy.alLaser.size(); j++) {
				Laser enemyLaser = enemy.alLaser.get(j);

				if (Util.colision(enemyLaser, player) && enemyLaser.isActive()) {
					enemyLaser.setActive(false);
					life--;
				}
			}

		}

		// Remove o laser do player
		for (int i = 0; i < player.alLaser.size(); i++) {
			if (!player.alLaser.get(i).isActive()) {
				player.alLaser.remove(i);
			}
		}

		// Desenha o Laser do player
		for (int i = 0; i < player.alLaser.size(); i++) {
			if (player.alLaser.get(i).isActive() && player.isActive()) {
				player.alLaser.get(i).draw(g);
			}
		}

		// Remove o laser inimigo
		for (int i = 0; i < alEnemy.size(); i++) {
			Enemy enemy = alEnemy.get(i);

			for (int j = 0; j < enemy.alLaser.size(); j++) {
				Laser enemyLaser = enemy.alLaser.get(j);

				if (!enemyLaser.isActive() || !enemy.isActive()) {
					enemy.alLaser.remove(j);
				}
			}
		}

		// Desenha o laser inimigo
		for (int i = 0; i < alEnemy.size(); i++) {
			Enemy enemy = alEnemy.get(i);

			if (enemy.isActive()) {
				for (int j = 0; j < enemy.alLaser.size(); j++) {
					Laser enemyLaser = enemy.alLaser.get(j);

					if (enemyLaser.isActive()) {
						enemyLaser.draw(g);
					}
				}
			}
		}

		/* Verificação do estado do Player */
		if (life > 0)
			player.draw(g);
		else if (!player.isExplode()) {
			try {
				AudioInputStream as = AudioSystem.getAudioInputStream(new File("res\\sound\\playerExplosion.wav"));
				Clip clip = AudioSystem.getClip();
				clip.open(as);
				clip.start();
			} catch (Exception e) {
				e.printStackTrace();
			}

			player.setExplode(true);

		} else if (player.getCountExplosion() < Util.EXPLOSION_TIME) {
			if (!Util.STOP) {
				g.drawImage(explosion, player.getX(), player.getY(), player.getWidth(), player.getHeight(), this);
				player.countExplosionUp();
				player.setActive(false);
			}
		} else {
			Util.PLAYING = false;
		}

		/* Verificação de Inimigos na Tela */
		for (int i = 0; i < alEnemy.size(); i++) {
			// Verifica se atingiu o player
			if (Util.colision(player, alEnemy.get(i)) && alEnemy.get(i).isActive()) {
				alEnemy.get(i).setActive(false);
				life--;
			}

			// Verifica se saiu da tela
			if (alEnemy.get(i).getY() >= Util.DEFAULT_SCREEN_HEIGHT) {
				alEnemy.get(i).setActive(false);
				alEnemy.get(i).setExplode(true);
			}

			// Desenha se estiver ativo
			if (alEnemy.get(i).isActive())
				alEnemy.get(i).draw(g);
			else if (!alEnemy.get(i).isExplode()) {
				alEnemy.get(i).setExplode(true);
				alEnemy.get(i).explode();
			} else if (alEnemy.get(i).getCountExplosion() < Util.EXPLOSION_TIME) {
				alEnemy.get(i).moveDown();
				
				g.drawImage(explosion, alEnemy.get(i).getX(), alEnemy.get(i).getY(), alEnemy.get(i).getWidth(),
						alEnemy.get(i).getHeight(), this);

				alEnemy.get(i).countExplosionUp();
			} else {
				alEnemy.remove(i);
			}

		}

	}

	@Override
	public void paintComponent(Graphics g) {

		if (!Util.STOP) {
			// Cor padrão da fonte
			g.setColor(Color.WHITE);

			// Desenha o background e define as cores da fonte
			Image imageBackground = background.getImage();
			g.drawImage(imageBackground, 0, moveBackground, getWidth(), Util.DEFAULT_SCREEN_HEIGHT * 10, this);

			// Executa o controlador da fase at� pausar
			phaseControl(g);

			// Desenha o status do Laser
			Image laserStatus = new ImageIcon(Util.LASER_CHARGE[Util.SHOOT_COUNT]).getImage();
			g.drawImage(laserStatus, 0, 60, 50, 80, null);

			// Pontuação
			g.drawString("Pontos: " + score, 20, 20);

			// Vida do Player
			Image img = imgLife.getImage();

			posLife = 15;

			for (int i = 0; i < life; i++) {
				g.drawImage(img, posLife, 34, 20, 20, this);
				posLife += 25;
			}
		}
	}

}