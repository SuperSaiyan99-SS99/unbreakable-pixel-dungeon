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
package com.watabou.unbreakablepixeldungeon.windows;

import com.watabou.unbreakablepixeldungeon.Dungeon;
import com.watabou.unbreakablepixeldungeon.actors.hero.Hero;
import com.watabou.unbreakablepixeldungeon.actors.mobs.npcs.Imp;
import com.watabou.unbreakablepixeldungeon.items.Item;
import com.watabou.unbreakablepixeldungeon.items.quest.DwarfToken;
import com.watabou.unbreakablepixeldungeon.utils.GLog;

public class WndImp extends WndQuest {
	
	private static final String TXT_MESSAGE	= 
		"Oh yes! You are my hero!\n" +
		"Regarding your reward, I don't have cash with me right now, but I have something better for you. " +
		"This is my family heirloom ring: my granddad took it off a dead paladin's finger.";
	private static final String TXT_REWARD		= "Take the ring";
	
	private Imp imp;
	private DwarfToken tokens;
	
	public WndImp( final Imp imp, final DwarfToken tokens ) {
		
		super( imp, TXT_MESSAGE, TXT_REWARD );
		
		this.imp = imp;
		this.tokens = tokens;
	}
	
	@Override
	protected void onSelect( int index ) {
		
		tokens.detach( Dungeon.hero.belongings.backpack );
		
		Item reward = Imp.Quest.reward;
		reward.identify();
		if (reward.doPickUp( Dungeon.hero )) {
			GLog.i( Hero.TXT_YOU_NOW_HAVE, reward.name() );
		} else {
			Dungeon.level.drop( reward, imp.pos ).sprite.drop();
		}
		
		imp.flee();
		
		Imp.Quest.complete();
	}
}
