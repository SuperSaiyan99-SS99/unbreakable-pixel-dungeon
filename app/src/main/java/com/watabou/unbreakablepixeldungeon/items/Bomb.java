/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.unbreakablepixeldungeon.items;

import com.watabou.noosa.audio.Sample;
import com.watabou.unbreakablepixeldungeon.Assets;
import com.watabou.unbreakablepixeldungeon.Dungeon;
import com.watabou.unbreakablepixeldungeon.ResultDescriptions;
import com.watabou.unbreakablepixeldungeon.actors.Actor;
import com.watabou.unbreakablepixeldungeon.actors.Char;
import com.watabou.unbreakablepixeldungeon.actors.buffs.Buff;
import com.watabou.unbreakablepixeldungeon.actors.buffs.Paralysis;
import com.watabou.unbreakablepixeldungeon.effects.CellEmitter;
import com.watabou.unbreakablepixeldungeon.effects.particles.BlastParticle;
import com.watabou.unbreakablepixeldungeon.effects.particles.SmokeParticle;
import com.watabou.unbreakablepixeldungeon.levels.Level;
import com.watabou.unbreakablepixeldungeon.scenes.GameScene;
import com.watabou.unbreakablepixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.unbreakablepixeldungeon.utils.GLog;
import com.watabou.unbreakablepixeldungeon.utils.Utils;
import com.watabou.utils.Random;

public class Bomb extends Item {
	
	{
		name = "bomb";
		image = ItemSpriteSheet.BOMB;
		defaultAction = AC_THROW;
		stackable = true;
	}
	
	@Override
	protected void onThrow( int cell ) {
		if (Level.pit[cell]) {
			super.onThrow( cell );
		} else {
			Sample.INSTANCE.play( Assets.SND_BLAST, 2 );
			
			if (Dungeon.visible[cell]) {
				CellEmitter.center( cell ).burst( BlastParticle.FACTORY, 30 );
			}
			
			boolean terrainAffected = false;
			for (int n : Level.NEIGHBOURS9) {
				int c = cell + n;
				if (c >= 0 && c < Level.LENGTH) {
					if (Dungeon.visible[c]) {
						CellEmitter.get( c ).burst( SmokeParticle.FACTORY, 4 );
					}
					
					if (Level.flamable[c]) {
						Dungeon.level.destroy( c );
						GameScene.updateMap( c );
						terrainAffected = true;	
					}
					
					Char ch = Actor.findChar( c );
					if (ch != null) {
						int dmg = Random.Int( 1 + Dungeon.depth, 10 + Dungeon.depth * 2 ) - Random.Int( ch.dr() );
						if (dmg > 0) {
							ch.damage( dmg, this );
							if (ch.isAlive()) {
								Buff.prolong( ch, Paralysis.class, 2 );
							} else if (ch == Dungeon.hero) {
								Dungeon.fail( Utils.format( ResultDescriptions.BOMB, Dungeon.depth ) );
								GLog.n( "You killed yourself with a bomb..." );
							}
						}
					}
				}
			}
			
			if (terrainAffected) {
				Dungeon.observe();
			}
		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public Item random() {
		quantity = Random.IntRange( 1, 3 );
		return this;
	}	
	
	@Override
	public int price() {
		return 10 * quantity;
	}
	
	@Override
	public String info() {
		return
			"This is a relatively small bomb, filled with black powder. Conveniently, its fuse is lit automatically when the bomb is thrown.";
	}
}
