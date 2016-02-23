import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.signals.*;
import ddf.minim.spi.*;
import ddf.minim.ugens.*;

Minim minim;
AudioPlayer music;

Actor player;
Actor enemy;
Message messagebox;
Dungeon dungeon;
PFont font;
int dungeoncount = 0;

void setup() {
  size(640, 400);
  player = new Actor(40, 8, 4);
  enemy = new Actor(20, 8, 4);
  
  minim = new Minim(this);
  music = minim.loadFile("music/HDE.mp3");
  music.loop();
  
  messagebox = new Message();
  dungeon = new Dungeon();
  font = loadFont("Font.vlw");
  textFont(font);
  noSmooth();
}

void draw()  {
   background(0);
   update();
   fill(255,0,0);
   scale(1,2);
   drawStatWindow();
   drawMessageWindow();
   scale(1,0.5);
   for (int y = 1; y <= 400; y+=2){
     stroke(0,0,0);
     line(0, y, 640, y);
   }
}

void update(){
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
void keyPressed(){
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
        dungeon.enemypos[int(random(dungeon.pos+1, 14))] = true;
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
void drawStatWindow(){
  text("PIMIKO", 16, 16);
  text("ENEMY", width-128, 16);
  fill(0,255,255);
  text(player.stats, 32, 32);
  text(enemy.stats, width-112, 32);
}

void drawMessageWindow(){
  fill(255,255,0);
  text("MONEY : \n  " + player.currency, width/2 - 96, 16);
  fill(255,255,255);
  text(messagebox.currentMessage, 16, 120);
}