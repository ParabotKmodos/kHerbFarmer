package org.parabot.kmodos.kHerbFarmer;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;


import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.api.utils.Timer;
import org.parabot.environment.input.Keyboard;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.Loader;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.Npcs;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.methods.SceneObjects;
import org.rev317.min.api.methods.Skill;
import org.rev317.min.api.wrappers.Npc;
import org.rev317.min.api.wrappers.SceneObject;
import org.rev317.min.api.wrappers.Tile;


@ScriptManifest(author = "Kmodos", category = Category.FARMING, description = "Farms all herbs and banks them for exp and cash", name = "kHerbFarmer", servers = { "PKHonor" }, version = 1)
public class kHerbFarmer extends Script implements Paintable{

	/*FARMING AREAS*/
	private final int[] TP_HERB_AREA_FALADOR = {662, 0, 2495, 315};
	private final int[] TP_HERB_AREA_PORT = {663, 0, 2495, 315};
	private final int[] TP_HERB_AREA_CATHERBY = {664, 47, 2495, 315};
	private final int[] TP_HERB_AREA_ARDY = {665, 478, 2495 ,315};

	private final int[] TP_PORT = {662, 0, 2495, 315};
	private final int[] TP_CATHERBY = {663, 0, 2496, 315};
	private final int[] TP_ARDY = {664, 0, 2497, 315};
	private final int[] TP_FALADOR = {665, 0, 2494, 315};

	private final FarmingArea FALADOR = new FarmingArea(new Area(new Tile(3045, 3316), new Tile(3062, 3316), new Tile(3062, 3300), new Tile(3045, 3300)), 2323, TP_HERB_AREA_FALADOR, TP_PORT);
	private final FarmingArea PORT = new FarmingArea(new Area(new Tile(3593, 3535), new Tile(3593, 3517), new Tile(3609, 3517), new Tile(3609, 3535)), 2326, TP_HERB_AREA_PORT, TP_CATHERBY);
	private final FarmingArea CATHERBY = new FarmingArea(new Area(new Tile(2800, 3474), new Tile(2800, 3456), new Tile(2817, 3456), new Tile(2817, 3474)), 2324, TP_HERB_AREA_CATHERBY, TP_ARDY);
	private final FarmingArea ARDY = new FarmingArea(new Area(new Tile(2655, 3384), new Tile(2655,3365), new Tile(2676, 3365), new Tile(2676, 3384)), 2325, TP_HERB_AREA_ARDY,TP_FALADOR);

	private final FarmingArea[] areas = {FALADOR, PORT, CATHERBY, ARDY};
	/*VARS*/

	private FarmingArea currentArea;

	private Timer timer = new Timer();

	private boolean showPaint = false;

	private int herbsFarmed = 0;

	private Herb herb;

	private final int OBJ_PATCH = 8132;

	private final int OBJ_GROWN = 8143;

	private final int OBJ_DEPOSIT = 9398;

	private final int INTER_BANK = 23350;

	private final int INTERFACE_TELEPORT_HERB = 2492;

	private final int[] RANDOMS = { 410, 1091, 3117, 3022 };

	private final long EXP_START = Skill.FARMING.getExperience();

	private final int LEVEL_START = Skill.FARMING.getLevel();

	private final Area BOBS_ISLAND = new Area(new Tile(2511, 4765), new Tile(2511, 4790), new Tile(2542, 4790), new Tile(2542, 4765));
	/*END VARS*/

	/*HERBS*/
	private HashMap<String, Herb> map = new HashMap<String, Herb>();
	private Herb guam = new Herb("Guam", 250 ,5292);
	private Herb marrentill = new Herb("Marrentill", 252,5293);
	private Herb tarromin = new Herb("Tarromin", 254, 5294);
	private Herb harralander = new Herb("Harralander", 256, 5295);
	private Herb ranarr = new Herb("Ranarr", 258, 5296);
	private Herb toadflax = new Herb("Toadflax", 2999, 5297);
	private Herb irit = new Herb("Irit", 260, 5298);
	private Herb avantoe = new Herb("Avantoe", 262, 5299);
	private Herb kwuarm = new Herb("Kwuarm", 264, 5300);
	private Herb snapdragon = new Herb("Snapdragon", 3001, 5301);
	private Herb cadantine = new Herb("Cadantine", 266, 5302 );
	private Herb lantadyme =  new Herb("Lantadyme", 2482, 5303);
	private Herb dwarfWeed = new Herb("Dwarf Weed", 268, 5304);
	private Herb torstol = new Herb("Torstol", 270, 5305);

	private final String STRING_GUAM = "Guam";
	private final String STRING_MARRENTILL = "Marrentill";
	private final String STRING_TARROMIN = "Tarromin";
	private final String STRING_HARRALANDER = "Harralander";
	private final String STRING_RANARR = "Ranarr";
	private final String STRING_TOADFLAX = "Toadflax";
	private final String STRING_IRIT = "Irit";
	private final String STRING_AVANTOE = "Avantoe";
	private final String STRING_KWUARM = "Kwuarm";
	private final String STRING_SNAPDRAGON = "Snapdragon";
	private final String STRING_CADANTINE = "Cadantine";
	private final String STRING_LANTADYME = "Lantadyme";
	private final String STRING_DWARFWEED = "Dwarfweed";
	private final String STRING_TORSTOL = "Torstol";


	private final ArrayList<Strategy> strats = new ArrayList<Strategy>();

	@Override
	public boolean onExecute() {
		map.put(STRING_GUAM, guam);
		map.put(STRING_MARRENTILL, marrentill);
		map.put(STRING_TARROMIN, tarromin);
		map.put(STRING_HARRALANDER, harralander);
		map.put(STRING_RANARR, ranarr);
		map.put(STRING_TOADFLAX, toadflax);
		map.put(STRING_IRIT, irit);
		map.put(STRING_AVANTOE, avantoe);
		map.put(STRING_KWUARM, kwuarm);
		map.put(STRING_SNAPDRAGON, snapdragon);
		map.put(STRING_CADANTINE, cadantine);
		map.put(STRING_LANTADYME, lantadyme);
		map.put(STRING_DWARFWEED, dwarfWeed);
		map.put(STRING_TORSTOL, torstol);

		GUI g = new GUI();

		while(g.isVisible()){
			Time.sleep(100);
		}
		showPaint = true;
		currentArea = getArea();
		strats.add(new Antis());
		strats.add(new Relog());
		strats.add(new Pick());
		strats.add(new Plant());
		strats.add(new BankHerbs());
		strats.add(new Teleport());
		provide(strats);
		return true;
	}

	@Override
	public void onFinish() {

	}

	private final Color color1 = new Color(0, 0, 255, 130);
	private final Color color2 = new Color(255, 255, 255);

	private final BasicStroke stroke1 = new BasicStroke(1);

	private final Font font1 = new Font("Arial", 0, 12);
	private final Font font2 = new Font("Arial", 0, 10);

	@Override
	public void paint(Graphics g1) {
		if(showPaint){
			Graphics2D g = (Graphics2D)g1;
			g.setColor(color1);
			g.fillRoundRect(554, 205, 178, 252, 16, 16);
			g.setColor(color2);
			g.setStroke(stroke1);
			g.drawRoundRect(554, 205, 178, 252, 16, 16);
			g.setFont(font1);
			g.drawString("Kmodos' Herb Farmer", 563, 226);
			g.setFont(font2);
			g.drawString("Run Time: " + timer.toString(), 563, 250);
			g.drawString("Farming Exp Gained: " + (Skill.FARMING.getExperience() - EXP_START), 563, 275);
			g.drawString("Exp Per Hour:" + timer.getPerHour((int) (Skill.FARMING.getExperience() - EXP_START)), 563, 300);
			g.drawString("Farming Levels Gained: " + (Skill.FARMING.getLevel() - LEVEL_START), 563, 325);
			g.drawString("Herbs Farmed: " + herbsFarmed, 563, 350);
			g.drawString("Herbs/Hour: " + timer.getPerHour(herbsFarmed), 563, 375);
			g.drawString("Herb: " + herb.name, 563, 400);
		}
	}

	private void sleep(int min, int max){
		Time.sleep((int)(Math.random() *(max-min)) + min);
	}

	/***************************************************************************************************************************/

	public class Plant implements Strategy{

		@Override
		public boolean activate() {
			return SceneObjects.getClosest(OBJ_PATCH) != null;
		}

		@Override
		public void execute() {
			SceneObject patch = SceneObjects.getClosest(OBJ_PATCH);
			if(patch != null){
				Menu.sendAction(447, herb.seedID-1, 27, 3214);
				sleep(500,750);
				Menu.sendAction(62, patch.getHash(), patch.getLocalRegionX(), patch.getLocalRegionY());
				sleep(5000,6000);
			}
		}
	}

	public class Pick implements Strategy{

		@Override
		public boolean activate() {
			return SceneObjects.getClosest(OBJ_GROWN) != null;
		}

		@Override
		public void execute() {
			SceneObject grownPlot = SceneObjects.getClosest(OBJ_GROWN);
			if(grownPlot != null){
				Menu.sendAction(502, grownPlot.getHash(), grownPlot.getLocalRegionX(), grownPlot.getLocalRegionY());
				sleep(1400,1500);
			}
		}

	}

	public class BankHerbs implements Strategy{

		@Override
		public boolean activate() {
			return Inventory.getCount(true, herb.herbID) > 0;
		}

		@Override
		public void execute() {
			SceneObject bank = SceneObjects.getClosest(OBJ_DEPOSIT);
			if(bank != null){
				System.out.println("Banking");
				Menu.sendAction(502, bank.getHash(), bank.getLocalRegionX(), bank.getLocalRegionY());
				Time.sleep(new SleepCondition() {

					@Override
					public boolean isValid() {
						return Loader.getClient().getOpenInterfaceId() == INTER_BANK;
					}
				}, 5000);
				herbsFarmed += Inventory.getCount(true, herb.herbID);
				Menu.sendAction(432, herb.herbID - 1, 0, 5064);
				Time.sleep(1500, 2000);
			}
		}

	}

	public class Teleport implements Strategy{

		@Override
		public boolean activate() {
			return Inventory.getCount(true, herb.herbID) == 0 && SceneObjects.getClosest(OBJ_PATCH) == null && SceneObjects.getClosest(OBJ_GROWN) == null;
		}

		@Override
		public void execute() {
			Npc farmer = Npcs.getNearest(currentArea.npcId)[0];
			if(farmer!= null){
				Menu.sendAction(20, farmer.getIndex(), 0, 0);
				Time.sleep(new SleepCondition() {

					@Override
					public boolean isValid() {
						return Loader.getClient().getBackDialogId() == INTERFACE_TELEPORT_HERB;
					}
				}, 2000);
				Time.sleep(1000,2000);
				Menu.sendAction(currentArea.herbTp[3], currentArea.herbTp[0], currentArea.herbTp[1], currentArea.herbTp[2]);
				Time.sleep(new SleepCondition() {

					@Override
					public boolean isValid() {
						return Loader.getClient().getBackDialogId() == INTERFACE_TELEPORT_HERB;
					}
				}, 2000);
				Time.sleep(1000,2000);
				Menu.sendAction(currentArea.nextTp[3], currentArea.nextTp[0], currentArea.nextTp[1], currentArea.nextTp[2]);
				Time.sleep(new SleepCondition() {
					
					@Override
					public boolean isValid() {
						return !currentArea.area.contains(Players.getMyPlayer().getLocation());
					}
				}, 10000);
				currentArea = getArea();
				Time.sleep(1500,1600);
			}
		}

	}

	public class StopScript implements Strategy{

		@Override
		public boolean activate() {
			return Inventory.getCount(true, herb.seedID) == 0;
		}

		@Override
		public void execute() {
			setState(STATE_STOPPED);
		}

	}
	/**
	 * Sexy anti random by Minimal 
	 * http://www.parabot.org/community/user/10775-minimal/
	 */
	public class Antis implements Strategy{
		@Override
		public boolean activate(){
			for (Npc n : Npcs.getNearest(RANDOMS)){
				if (n.getLocation().distanceTo() < 3)
					return true;
			}
			return false;
		}

		@Override
		public void execute(){
			sleep(750);
			Npc[] n = Npcs.getNearest(RANDOMS);
			System.out.println("There is a random nearby!");
			sleep(750);
			if (n[0].getDef().getId() == 1091){
				SceneObject[] portal = SceneObjects.getNearest(8987);

				for (int i = 0; i < portal.length; i++){
					if (BOBS_ISLAND.contains(Players.getMyPlayer().getLocation())){
						final SceneObject portal2 = portal[i];
						portal2.interact(0);
						Time.sleep(new SleepCondition(){
							@Override
							public boolean isValid(){
								return portal2.getLocation().distanceTo() < 2;
							}
						}, 7500);
						portal2.interact(0);
						sleep(1000);
					}
					else
						break;
				}
				System.out.println("Bob's Island has been completed");
			}
			else if (n[0].getDef().getId() == 3022){
				System.exit(0);
				System.out.println("A mod called a Genie random onto you.\n" +
						"The client was closed to protect your account.");
			}
			else{
				n[0].interact(0);
				sleep(1500);
				System.out.println("Sandwich lady/Old man random has been completed");
			}
		}
	}

	/**
	 * Relog Handler by Minimal & Made better by Kmodos
	 * http://www.parabot.org/community/user/10775-minimal/
	 */
	public class Relog implements Strategy{
		public boolean activate(){
			for (@SuppressWarnings("unused") SceneObject so: SceneObjects.getNearest()){
				return false;
			}

			return true;
		}

		public void execute(){ 
			System.out.println("Relogging");
			if (!isLoggedIn()){
				Keyboard.getInstance().sendKeys("");
				sleep(6000);
			}
			if (!isLoggedIn()){
				Keyboard.getInstance().sendKeys("");
				sleep(6000);
			}
		}
	}

	public FarmingArea getArea(){
		for(int i = 0; i < areas.length; i++){
			if(areas[i].area.contains(Players.getMyPlayer().getLocation())){
				return areas[i];
			}
		}
		System.out.println("You are not in a farming area!");
		System.exit(1);
		return null;
	}

	/**
	 * By Minimal
	 */
	public boolean isLoggedIn(){
		SceneObject[] so = SceneObjects.getNearest();
		if (so.length > 0)
			return true;
		else
			return false;
	}

	/**
	 *
	 * @author Matt
	 *
	 */
	public class Area {
		private Polygon p;

		/**
		 * Initializes a PolygonArea with the tiles given
		 *
		 * @param tiles
		 *            tiles to use in the area
		 */
		public Area(Tile... tiles) {
			this.p = new Polygon();
			for (int i = 0; i < tiles.length; i++) {
				p.addPoint(tiles[i].getX(), tiles[i].getY());
			}
		}

		/**
		 * Checks if a tile is in the area
		 *
		 * @param tile
		 *            The tile to check
		 * @return <b>true</b> if area does contain the tile, otherwise <b>false</b>
		 */
		public boolean contains(Tile tile) {
			return this.contains(tile.getX(), tile.getY());
		}

		public boolean contains(int x, int y) {
			int i;
			int j;
			boolean result = false;
			for (i = 0, j = p.npoints - 1; i < p.npoints; j = i++) {
				if ((p.ypoints[i] > y - 1) != (p.ypoints[j] > y - 1)
						&& (x <= (p.xpoints[j] - p.xpoints[i]) * (y - p.ypoints[i])
						/ (p.ypoints[j] - p.ypoints[i]) + p.xpoints[i])) {
					result = !result;
				}
			}
			return result;
		}
	}

	/**
	 * Used to easily represent herbs.
	 */
	public class Herb{
		public int herbID;
		public String name;
		public int seedID;
		public Herb(String name, int id, int seed){
			this.name = name;
			herbID = id;
			seedID = seed;
		}
	}

	public class GUI extends JFrame implements ActionListener{

		private static final long serialVersionUID = 3124781341234L;
		private JButton start;
		private Container cont;
		private Container herbCont;
		private JComboBox<String> herbSelector;
		private JLabel herbLabel;

		private String[] herbs = {STRING_GUAM, STRING_TARROMIN, STRING_MARRENTILL, STRING_HARRALANDER, STRING_RANARR, STRING_TOADFLAX, STRING_IRIT, STRING_AVANTOE, STRING_KWUARM, STRING_SNAPDRAGON, STRING_CADANTINE, STRING_LANTADYME, STRING_DWARFWEED, STRING_TORSTOL};

		private final String TITLE = "kHerbFarmer";
		private final int WIDTH = 200;
		private final int HIEGHT = 75;

		public GUI(){
			cont = new Container();
			herbCont = new Container();

			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setSize(WIDTH, HIEGHT);
			setTitle(TITLE);
			setResizable(false);

			herbSelector = new JComboBox<>(herbs);
			herbLabel = new JLabel(" Herbs: ");

			start = new JButton("Start");
			start.addActionListener(this);

			herbCont.setLayout(new BoxLayout(herbCont, BoxLayout.X_AXIS));
			cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));

			herbCont.add(herbLabel);
			herbCont.add(herbSelector);

			cont.add(herbCont);
			cont.add(start);

			add(cont);

			setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(start)){
				herb = map.get(herbSelector.getSelectedItem());
				setVisible(false);
				dispose();
				timer = new org.parabot.environment.api.utils.Timer();
			}
		}
	}

	private class FarmingArea {
		public Area area;
		public int npcId;
		public int[] nextTp;
		public int[] herbTp;
		public FarmingArea(Area area, int npcId, int[] herbTp, int[] nextTp){
			this.area = area;
			this.npcId = npcId;
			this.nextTp = nextTp;
			this.herbTp = herbTp;
		}
	}

}