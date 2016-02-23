import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 
import ddf.minim.effects.*; 
import ddf.minim.signals.*; 
import ddf.minim.spi.*; 
import ddf.minim.ugens.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Pimiko extends PApplet {








Minim minim;
AudioPlayer music;

Actor player;
Actor enemy;
Message messagebox;
Dungeon dungeon;
PFont font;
int dungeoncount = 0;

public void setup() {
  
  player = new Actor(40, 8, 4);
  enemy = new Actor(20, 8, 4);
  
  minim = new Minim(this);
  music = minim.loadFile("data/music/HDE.mp3");
  music.loop();
  
  messagebox = new Message();
  dungeon = new Dungeon();
  font = loadFont("Font.vlw");
  textFont(font);
  
}

public void draw()  {
   background(0);
   update();
   fill(255,0,0);
   scale(1,2);
   drawStatWindow();
   drawMessageWindow();
   scale(1,0.5f);
   for (int y = 1; y <= 400; y+=2){
     stroke(0,0,0);
     line(0, y, 640, y);
   }
}

public void update(){
  player.updateStats();
  enemy.updateStats();
  messagebox.update();
  if (player.alive == false){
    messagebox.current = 18;
  }
}


//==================================
// ISOLATION ZONE
//==================================
public void keyPressed(){
  if (player.alive == true){
  if (key == ' '){
    switch (messagebox.current){
    case 0:
      messagebox.current = 1;
      break;
    case 1:
      messagebox.current = 2;
      break;
    case 10:
      messagebox.current = 11;
      break;
    }
  }
  if (key == 'h'){
    messagebox.current = 1;
  }
  if (key == 'a'){
    if (dungeon.enemypos[dungeon.pos] == true){
      enemy.hurt(player.attack());
      messagebox.current = 6;
      if (enemy.alive == true){
      player.hurt(enemy.attack());
      messagebox.current = 7;
      } else {
        messagebox.current = 8;
        dungeon.enemypos[dungeon.pos] = false;
        if (dungeon.pos <= 12){
        dungeon.enemypos[PApplet.parseInt(random(dungeon.pos+1, 14))] = true;
        enemy = new Actor((20*dungeoncount),(7*dungeoncount),(4*dungeoncount));
        }
      }
    }
  }
  if (key == 'm'){
    dungeon.move();
  }
  if (key == 's'){
    dungeon.shop();
  }
  if (key == 'e'){
    if (dungeon.enemypos[dungeon.pos] == true){
      messagebox.current = 19;
    } else {
    dungeon.examine();
    }
  }
  if (key == 'E'){
     exit();
  }
  if (key == 'R'){
    if(dungeon.pos == 14){
      dungeon = new Dungeon();
      dungeoncount += 1;
      enemy = new Actor((20*dungeoncount),(7*dungeoncount),(4*dungeoncount));
      messagebox.current = 16;
    } else {
      messagebox.current = 17;
    }
  }
  if (messagebox.current == 11){
      if ((key == '1') || (key == '2') || (key == ' ') || (key == '3')){
        switch(key){
          case '1':
            dungeon.buy(1);
            break;
          case '2':
            dungeon.buy(2);
            break;
           case '3':
            dungeon.buy(3);
            break;
          case 'm':
            messagebox.current = 14;
            break;
        }
      } else {
        messagebox.current = 14;
      }
  }
  } else {
    if (key == 'y'){
      player = new Actor(40, 12, 10);
      dungeon = new Dungeon();
      enemy = new Actor(20, 8, 4);
      messagebox = new Message();
      dungeoncount = 0;
    }
    if (key == 'n'){
      exit();
    }
  }
}


/*

  This is where the stat windows live. Yeah yeah hardcoding, magic numbers, what can ya say!

*/
public void drawStatWindow(){
  text("PIMIKO", 16, 16);
  text("ENEMY", width-128, 16);
  fill(0,255,255);
  text(player.stats, 32, 32);
  text(enemy.stats, width-112, 32);
}

public void drawMessageWindow(){
  fill(255,255,0);
  text("MONEY : \n  " + player.currency, width/2 - 96, 16);
  fill(255,255,255);
  text(messagebox.currentMessage, 16, 120);
}
/*
  =============
  | Entities! |
  =============
  
  This source file contains all the entities in the game.
  - "Actor" is a class covering any "creature" in the game, including Pimiko
  - "Message" is just a collection of messages. Uses an arraylist to store messages at setup.
  - "Dungeon" is the container class for the environment. Lumpee is not an actor!
  
  =============
  | Extension |
  =============
  
  Here's some ideas to extend the classes for your own work:
  - What if the dungeon were 2 dimensional with randomly placed obstacles instead of linear?
  - What if enemies moved from place to place in the 2D dungeon?
  
  - What if you added more statistics to the "Actor" class so you can make skill checks?
  - How about an inventory and equipment for the player!
*/

class Actor{
  public int hp, str, def;
  public int currency;
  public boolean alive;
  public String stats;
  
  Actor(int ihp, int istr, int idef){
    hp = ihp + PApplet.parseInt(random(0,20));
    str = istr + PApplet.parseInt(random(0,5));
    def = idef + PApplet.parseInt(random(0,5));
    currency = 0;
    alive = true;
    stats = ("HP: " + hp + "\nSTR: " + str + "\nDEF: " + def);
  }
  
  public void updateStats(){
    stats = ("HP: " + hp + "\nSTR: " + str + "\nDEF: " + def);
  }
  
  public int attack(){
    int crit = PApplet.parseInt(random(1,64));
    int dmg = PApplet.parseInt(random(1,5));
    if (crit >= 48){
      dmg = PApplet.parseInt(random(str,(str*2)));
    }
    return str + dmg;
  }
  
  public void hurt(int dmg){
    dmg -= def;
    if (dmg < 0){
      hp -= 1;
    } else {
      hp -= dmg;
    }
  
    if (hp <= 0){
      hp = 0;
      alive = false;
    }
  }
  
}

class Message{
  public int current;
  public String exit;
  public String currentMessage;
  public StringList messageArray;
  public int prizemoney;
  
  Message(){
    messageArray = new StringList();
    prizemoney = 0;
    //Messages 0 thru 4
    messageArray.append("Welcome to Pimiko! Push SPACE to begin.");
    messageArray.append("Hit H for help, M to move to the next room, A to attack, and S to access the shop fairy.\nSPACE advances a message and E examines a room.");
    messageArray.append("You can exit the game by pressing SHIFT + E.");
    messageArray.append("Lucky! You found some money!");
    messageArray.append("There's nothing in here.");
    //Messages 5 thru 9
    messageArray.append("There's an enemy here! Fight to the death!");
    messageArray.append("Scored a hit!");
    messageArray.append("Ouch! You took some damage!");
    messageArray.append("The enemy is dead! You can search the room now.");
    messageArray.append("You can't go any further! You need to reroll the dungeon (press SHIFT+R to reroll).");
    //Messages 10 thru 14
    messageArray.append("Hello, I'm Lumpee the shop fairy. What would you like to buy?");
    messageArray.append("1: Adventurer's Kit (+3 STR, +1 DEF, +3 HP)\n2: Guardian's Kit (+1 STR, +2 DEF, +6 HP)\n3: Heal (+20 HP)\nAll items are 15 shekels.");
    messageArray.append("Thank you for the doughnut money!");
    messageArray.append("Your wallet is even tinier than me... no upgrades for you!");
    messageArray.append("Fine then, you don't get to have any of my Tumy magic!");
    //Messages 15 thru 19
    messageArray.append("Looks like the coast is clear here.");
    messageArray.append("Rolled a new dungeon! Looks like the monsters will be tougher...");
    messageArray.append("You need to reach the end before you can reroll, it seems.");
    messageArray.append("You died! Try again? Y/N");
    messageArray.append("Kill the monster first!");
    
    current = 0;
    exit = "See you next time!";
  }
  
  public void update(){
    currentMessage = messageArray.get(current);
  }
}

class Dungeon{
  public boolean enemypos[];
  public boolean items[];
  public int size;
  public int pos;
  
  Dungeon(){
    pos = 0;
    size = 15;
    enemypos = new boolean[size];
    items = new boolean[size];
    enemypos[PApplet.parseInt(random(3, 14))] = true;
    for (int i = 0; i < items.length; i++){
      items[i] = true;
    }
  }
  
  public void examine(){
    if (items[pos] == true){
      messagebox.prizemoney = PApplet.parseInt(random(1,10));
      messagebox.current = 3;
      player.currency += messagebox.prizemoney;
      items[pos] = false;
    } else {
      messagebox.current = 4;
    }
  }
  
  public void shop(){
    if (player.currency < 15){
      messagebox.current = 13;
    } else {
      messagebox.current = 10;
    }
  }
  
  public void buy(int option){
    switch(option){
        case 1:
          player.currency -= 15;
          player.str += 3;
          player.def += 1;
          player.hp += 3;
          messagebox.current = 12;
          break;
        case 2:
          player.currency -= 15;
          player.str += 1;
          player.def += 2;
          player.hp += 6;
          messagebox.current = 12;
          break;
        case 3:
          player.currency -= 15;
          player.hp += 20;
          messagebox.current = 12;
          break;
      }
  }
  public void move(){
    if (enemypos[pos] == true){
      messagebox.current = 5;
    } else {
      messagebox.current = 15;
      if (pos < 14){
        pos++;
        if (enemypos[pos] == true){
          messagebox.current = 5;
        }
      } else {
      messagebox.current = 9;
      }
    }
  }
}
  public void settings() {  size(640, 400);  noSmooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Pimiko" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
