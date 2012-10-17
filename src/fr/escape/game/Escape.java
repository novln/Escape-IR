/*****************************************************************************
 * 
 * Copyright 2012 See AUTHORS file.
 * 
 * This file is part of Escape-IR.
 * 
 * Escape-IR is free software: you can redistribute it and/or modify
 * it under the terms of the zlib license. See the COPYING file.
 * 
 *****************************************************************************/

package fr.escape.game;

import fr.escape.app.Game;
import fr.escape.game.screen.Splash;

public class Escape extends Game {

	private Splash splash;
	
	@Override
	public void create() {
		splash = new Splash(this);
		// Other Screen if any ...
		
		setScreen(splash);
	}
	
}
