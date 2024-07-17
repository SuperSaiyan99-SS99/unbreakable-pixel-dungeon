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
package com.watabou.unbreakablepixeldungeon.actors.blobs;

import com.watabou.noosa.audio.Sample;
import com.watabou.unbreakablepixeldungeon.Assets;
import com.watabou.unbreakablepixeldungeon.Dungeon;
import com.watabou.unbreakablepixeldungeon.Journal;
import com.watabou.unbreakablepixeldungeon.Journal.Feature;
import com.watabou.unbreakablepixeldungeon.actors.buffs.Buff;
import com.watabou.unbreakablepixeldungeon.actors.buffs.Disguise;
import com.watabou.unbreakablepixeldungeon.actors.hero.Hero;
import com.watabou.unbreakablepixeldungeon.effects.BlobEmitter;
import com.watabou.unbreakablepixeldungeon.effects.Speck;
import com.watabou.unbreakablepixeldungeon.items.Generator;
import com.watabou.unbreakablepixeldungeon.items.Item;
import com.watabou.unbreakablepixeldungeon.items.Generator.Category;
import com.watabou.unbreakablepixeldungeon.items.potions.Potion;
import com.watabou.unbreakablepixeldungeon.items.potions.PotionOfMight;
import com.watabou.unbreakablepixeldungeon.items.potions.PotionOfStrength;
import com.watabou.unbreakablepixeldungeon.items.rings.Ring;
import com.watabou.unbreakablepixeldungeon.items.scrolls.Scroll;
import com.watabou.unbreakablepixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.watabou.unbreakablepixeldungeon.items.scrolls.ScrollOfEnchantment;
import com.watabou.unbreakablepixeldungeon.items.wands.Wand;
import com.watabou.unbreakablepixeldungeon.items.weapon.melee.*;
import com.watabou.unbreakablepixeldungeon.plants.Plant;
import com.watabou.unbreakablepixeldungeon.sprites.HeroSprite;
import com.watabou.unbreakablepixeldungeon.utils.GLog;

public class WaterOfTransmutation extends WellWater {
	
	private static final String TXT_PROCCED =
		"As you take a sip, you feel something changes in you.";
	
	@Override
	protected boolean affectHero( Hero hero ) {
		
		Sample.INSTANCE.play( Assets.SND_DRINK );
		
		Disguise disguise = hero.buff( Disguise.class );
		if (disguise != null) {
			Buff.affect( hero, Disguise.class, Disguise.DURATION ).choose( hero.heroClass, disguise.costume );
		} else {
			Buff.affect( hero, Disguise.class, Disguise.DURATION ).choose( hero.heroClass, null );
		}
		
		Dungeon.hero.interrupt();
		
		((HeroSprite)hero.sprite).updateTexture();
		
		GLog.h( TXT_PROCCED );
		
		Journal.remove( Journal.Feature.WELL_OF_TRANSMUTATION );
		
		return true;
	}
	
	@Override
	protected Item affectItem( Item item ) {
		
		if (item instanceof MeleeWeapon) {			
			item = changeWeapon( (MeleeWeapon)item );		
		} else if (item instanceof Scroll) {	
			item = changeScroll( (Scroll)item );	
		} else if (item instanceof Potion) {
			item = changePotion( (Potion)item );
		} else if (item instanceof Ring) {
			item = changeRing( (Ring)item );
		} else if (item instanceof Wand) {	
			item = changeWand( (Wand)item );
		} else if (item instanceof Plant.Seed) {
			item = changeSeed( (Plant.Seed)item );
		} else {
			item = null;
		}
		
		if (item != null) {
			Journal.remove( Feature.WELL_OF_TRANSMUTATION );
		}
		
		return item;
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );	
		emitter.start( Speck.factory( Speck.CHANGE ), 0.2f, 0 );
	}
	
	private MeleeWeapon changeWeapon( MeleeWeapon w ) {
		
		MeleeWeapon n = null;
		
		if (w instanceof Knuckles) {
			n = new Dagger();
		} else if (w instanceof Dagger) {
			n = new Knuckles();
		}
		
		else if (w instanceof Spear) {
			n = new Quarterstaff();
		} else if (w instanceof Quarterstaff) {
			n = new Spear();
		}
		
		else if (w instanceof Sword) {
			n = new Mace();
		} else if (w instanceof Mace) {
			n = new Sword();
		}
		
		else if (w instanceof Longsword) {
			n = new BattleAxe();
		} else if (w instanceof BattleAxe) {
			n = new Longsword();
		}
		
		else if (w instanceof Glaive) {
			n = new WarHammer();
		} else if (w instanceof WarHammer) {
			n = new Glaive();
		}
		
		if (n != null) {
			
			int level = w.level();
			if (level > 0) {
				n.upgrade( level );
			} else if (level < 0) {
				n.degrade( -level );
			}
			
			if (w.isEnchanted()) {
				n.enchant();
			}
			
			n.levelKnown = w.levelKnown;
			n.cursedKnown = w.cursedKnown;
			n.cursed = w.cursed;
			
			Journal.remove( Feature.WELL_OF_TRANSMUTATION );
			
			return n;
		} else {
			return null;
		}
	}
	
	private Ring changeRing( Ring r ) {
		Ring n;
		do {
			n = (Ring)Generator.random( Category.RING );
		} while (n.getClass() == r.getClass());
		
		n.level( 0 );
		
		int level = r.level();
		if (level > 0) {
			n.upgrade( level );
		} else if (level < 0) {
			n.degrade( -level );
		}
		
		n.levelKnown = r.levelKnown;
		n.cursedKnown = r.cursedKnown;
		n.cursed = r.cursed;
		
		return n;
	}
	
	private Wand changeWand( Wand w ) {
		
		Wand n;
		do {
			n = (Wand)Generator.random( Category.WAND );
		} while (n.getClass() == w.getClass());
		
		n.level( 0 );
		n.upgrade( w.level() );
		
		n.levelKnown = w.levelKnown;
		n.cursedKnown = w.cursedKnown;
		n.cursed = w.cursed;
		
		return n;
	}
	
	private Plant.Seed changeSeed( Plant.Seed s ) {
		
		Plant.Seed n;
		
		do {
			n = (Plant.Seed)Generator.random( Category.SEED );
		} while (n.getClass() == s.getClass());
		
		return n;
	}
	
	private Scroll changeScroll( Scroll s ) {
		if (s instanceof ScrollOfUpgrade) {
			
			return new ScrollOfEnchantment();
			
		} else if (s instanceof ScrollOfEnchantment) {
			
			return new ScrollOfUpgrade();
			
		} else {
			
			Scroll n;
			do {
				n = (Scroll)Generator.random( Category.SCROLL );
			} while (n.getClass() == s.getClass());
			return n;
		}
	}
	
	private Potion changePotion( Potion p ) {
		if (p instanceof PotionOfStrength) {
			
			return new PotionOfMight();
			
		} else if (p instanceof PotionOfMight) {
			
			return new PotionOfStrength();
			
		} else {
			
			Potion n;
			do {
				n = (Potion)Generator.random( Category.POTION );
			} while (n.getClass() == p.getClass());
			return n;
		}
	}
	
	@Override
	public String tileDesc() {
		return 
			"Power of change radiates from the water of this well. " +
			"Throw an item into the well to turn it into something else.";
	}
}
