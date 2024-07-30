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
package com.watabou.unbreakablepixeldungeon.scenes;

import android.content.Intent;
import android.net.Uri;

import com.watabou.input.Touchscreen.Touch;
import com.watabou.noosa.BitmapTextMultiline;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TouchArea;
import com.watabou.unbreakablepixeldungeon.UnbreakablePixelDungeon;
import com.watabou.unbreakablepixeldungeon.effects.Flare;
import com.watabou.unbreakablepixeldungeon.sprites.ItemSprite;
import com.watabou.unbreakablepixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.unbreakablepixeldungeon.ui.Archs;
import com.watabou.unbreakablepixeldungeon.ui.ExitButton;
import com.watabou.unbreakablepixeldungeon.ui.Icons;
import com.watabou.unbreakablepixeldungeon.ui.Window;

public class AboutScene extends PixelScene {
	
	private static final String UNBREAKABLE_TTL = "Unbreakable";
	
	private static final String UNBREAKABLE_TXT =
		"Modifications: SuperSaiyan99\n\n" +
		"This mod removes the degradation from items " +
		"and its related features.";
	
	private static final String TTL = "Pixel Dungeon";

	private static final String TXT = 
		"Code & graphics: Watabou\n" +
		"Music: Cube_Code\n\n" + 
		"This game is inspired by Brian Walker's Brogue. " +
		"Try it on Windows, Mac OS or Linux - it's awesome! ;)\n\n" +
		"Please visit official website for additional info:";
	
	private static final String LNK = "pixeldungeon.watabou.ru";
	
	private static final float GAP = 26;
	
	@Override
	public void create() {
		super.create();
		
		float col;
		float colOffset;
		
		if (UnbreakablePixelDungeon.landscape()) {
			col = Camera.main.width / 2f;
			colOffset = col;
		} else {
			col = Camera.main.width;
			colOffset = 0;
		}
		
		BitmapTextMultiline title = createMultiline( TTL, 8 );
		title.maxWidth = Math.min( (int)col, 120 );
		title.measure();
		title.hardlight( Window.TITLE_COLOR );
		add( title );
		
		BitmapTextMultiline text = createMultiline( TXT, 8 );
		text.maxWidth = title.maxWidth;
		text.measure();
		add( text );
		
		BitmapTextMultiline link = createMultiline( LNK, 8 );
		link.maxWidth = title.maxWidth;
		link.measure();
		link.hardlight( Window.TITLE_COLOR );
		add( link );
		
		TouchArea hotArea = new TouchArea( link ) {
			@Override
			protected void onClick( Touch touch ) {
				Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "http://" + LNK ) );
				Game.instance.startActivity( intent );
			}
		};
		add( hotArea );
		
		Image wata = Icons.WATA.get();
		add( wata );
		
		BitmapTextMultiline unbreakableTitle = createMultiline( UNBREAKABLE_TTL, 8 );
		unbreakableTitle.maxWidth = title.maxWidth;
		unbreakableTitle.measure();
		unbreakableTitle.hardlight( 0xD6A07A );
		add( unbreakableTitle );
		
		BitmapTextMultiline unbreakableText = createMultiline( UNBREAKABLE_TXT, 8 );
		unbreakableText.maxWidth = title.maxWidth;
		unbreakableText.measure();
		add( unbreakableText );
		
		Image unbreakableChest = new ItemSprite( ItemSpriteSheet.LOCKED_CHEST, new ItemSprite.Glowing( 0xCC8888 ) );
		add( unbreakableChest );
		
		float totalHeight = UnbreakablePixelDungeon.landscape() ?
			title.height() + GAP + text.height() + link.height() :
			unbreakableTitle.height() + GAP + unbreakableText.height() + GAP + title.height() + GAP + text.height() + link.height();
		float vertMargin =  (Camera.main.height - totalHeight) / 2;
		
		unbreakableTitle.x = align( (col - unbreakableTitle.width()) / 2 );
		unbreakableTitle.y = align( vertMargin );
		
		unbreakableChest.x = align( (col - unbreakableChest.width) / 2 );
		unbreakableChest.y = unbreakableTitle.y + unbreakableTitle.height() + (GAP - unbreakableChest.height) / 2;
		
		unbreakableText.x = align( (col - unbreakableText.width()) / 2 );
		unbreakableText.y = unbreakableTitle.y + unbreakableTitle.height() + GAP;
		
		title.x = align( colOffset + (col - title.width()) / 2 );
		title.y = UnbreakablePixelDungeon.landscape() ? align( vertMargin ) : unbreakableText.y + unbreakableText.height() + GAP;
		
		wata.x = align( colOffset + (col - wata.width) / 2 );
		wata.y = title.y + title.height() + (GAP - wata.height) / 2;
		
		text.x = align( colOffset + (col - text.width()) / 2 );
		text.y = title.y + title.height() + GAP;
		
		link.x = text.x;
		link.y = text.y + text.height();
		
		new Flare( 7, 64 ).color( 0x332222, true ).show( unbreakableChest, 0 ).angularSpeed = +20;
		new Flare( 7, 64 ).color( 0x112233, true ).show( wata, 0 ).angularSpeed = +20;
		
		Archs archs = new Archs();
		archs.setSize( Camera.main.width, Camera.main.height );
		addToBack( archs );
		
		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );
		
		fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		UnbreakablePixelDungeon.switchNoFade( TitleScene.class );
	}
}
