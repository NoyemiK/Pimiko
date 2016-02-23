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
    hp = ihp + int(random(0,20));
    str = istr + int(random(0,5));
    def = idef + int(random(0,5));
    currency = 0;
    alive = true;
    stats = ("HP: " + hp + "\nSTR: " + str + "\nDEF: " + def);
  }
  
  public void updateStats(){
    stats = ("HP: " + hp + "\nSTR: " + str + "\nDEF: " + def);
  }
  
  public int attack(){
    int crit = int(random(1,64));
    int dmg = int(random(1,5));
    if (crit >= 48){
      dmg = int(random(str,(str*2)));
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
    enemypos[int(random(3, 14))] = true;
    for (int i = 0; i < items.length; i++){
      items[i] = true;
    }
  }
  
  public void examine(){
    if (items[pos] == true){
      messagebox.prizemoney = int(random(1,10));
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