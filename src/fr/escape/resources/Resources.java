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

package fr.escape.resources;

import java.awt.Font;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Objects;

import fr.escape.app.Foundation;
import fr.escape.game.entity.ships.ShipFactory;
import fr.escape.game.scenario.Scenario;
import fr.escape.graphics.Texture;
import fr.escape.resources.font.FontLoader;
import fr.escape.resources.scenario.ScenarioLoader;
import fr.escape.resources.scenario.ScenarioParser;
import fr.escape.resources.texture.TextureLoader;

/**
 * <p>
 * Create a Index of available Resource and load them in memory if needed.
 * 
 * <p>
 * You cannot unload them after that.
 * 
 * <p>
 * For loading: simply use <i>get...(...)</i> 
 * 
 * <p>
 * <b>Note:</b> After instantiate this Object; You <b>HAVE TO</b> call <b>load()</b>
 *
 */
public final class Resources {
	
	static final String TAG = Resources.class.getSimpleName();
	
	private final HashMap<String, FontLoader> fontLoader;
	private final HashMap<String, TextureLoader> textureLoader;
	private final HashMap<String, ScenarioLoader> scenarioLoader;
	
	/**
	 * Is all resources loaded in memory ?
	 */
	private boolean loaded;
	
	/**
	 * <p>
	 * Default Constructor for {@link Resources}.
	 * 
	 * <p>
	 * Don't forget to call load() after instantiation.
	 */
	public Resources() {
		fontLoader = new HashMap<String, FontLoader>();
		textureLoader = new HashMap<String, TextureLoader>();
		scenarioLoader = new HashMap<String, ScenarioLoader>();
		loaded = false;
	}
	
	/**
	 * Create and Load {@link ResourcesLoader} in List
	 */
	public void load() {
		if(loaded == false) {
			
			// Load Font
			postFontLoader(FontLoader.VISITOR_ID, 18.0f);
			
			// Load Scenario
			postScenarioLoader(ScenarioLoader.EARTH_1);
			
			// Load Texture
			postTextureLoader(TextureLoader.BACKGROUND_ERROR);
			postTextureLoader(TextureLoader.BACKGROUND_LOST);
			postTextureLoader(TextureLoader.BACKGROUND_MENU);
			postTextureLoader(TextureLoader.BACKGROUND_VICTORY);
			postTextureLoader(TextureLoader.WEAPON_UI_ACTIVATED);
			postTextureLoader(TextureLoader.WEAPON_UI_DISABLED);
			postTextureLoader(TextureLoader.WEAPON_BLACKHOLE);
			postTextureLoader(TextureLoader.WEAPON_FIREBALL);
			postTextureLoader(TextureLoader.WEAPON_MISSILE);
			postTextureLoader(TextureLoader.WEAPON_SHIBOLEET);
			postTextureLoader(TextureLoader.WEAPON_MISSILE_SHOT);
			postTextureLoader(TextureLoader.WEAPON_FIREBALL_CORE_SHOT);
			postTextureLoader(TextureLoader.WEAPON_FIREBALL_RADIUS_SHOT);
			postTextureLoader(TextureLoader.WEAPON_SHIBOLEET_SHOT);
			postTextureLoader(TextureLoader.WEAPON_BLACKHOLE_CORE_SHOT);
			postTextureLoader(TextureLoader.WEAPON_BLACKHOLE_LEFT_SHOT);
			postTextureLoader(TextureLoader.WEAPON_BLACKHOLE_RIGHT_SHOT);
			postTextureLoader(TextureLoader.WEAPON_BLACKHOLE_EVENT_HORIZON_SHOT);
			postTextureLoader(TextureLoader.BONUS_WEAPON_MISSILE);
			postTextureLoader(TextureLoader.BONUS_WEAPON_FIREBALL);
			postTextureLoader(TextureLoader.BONUS_WEAPON_SHIBOLEET);
			postTextureLoader(TextureLoader.BONUS_WEAPON_BLACKHOLE);
			
			postTextureLoader(TextureLoader.SHIP_SWING);
			postTextureLoader(TextureLoader.SHIP_SWING_1);
			postTextureLoader(TextureLoader.SHIP_SWING_2);
			postTextureLoader(TextureLoader.SHIP_SWING_3);
			postTextureLoader(TextureLoader.SHIP_SWING_4);
			postTextureLoader(TextureLoader.SHIP_SWING_5);
			postTextureLoader(TextureLoader.SHIP_SWING_6);
			postTextureLoader(TextureLoader.SHIP_SWING_7);
			postTextureLoader(TextureLoader.SHIP_SWING_8);
			postTextureLoader(TextureLoader.SHIP_SWING_9);
			
		}
		
		loaded = true;
	}
	
	/**
	 * Load and return Font from {@link ResourcesLoader} 
	 * 
	 * @param name Font name
	 * @return Font
	 * @throws NoSuchElementException
	 */
	public Font getFont(String name) throws NoSuchElementException {
		Objects.requireNonNull(name);
		checkIfLoaded();
		try {
			
			FontLoader loader = fontLoader.get(name);
			return loader.load();
			
		} catch(Exception e) {
			NoSuchElementException exception = new NoSuchElementException("Cannot load the given Font: "+name);
			exception.initCause(e);
			throw exception;
		}
	}
	
	/**
	 * Load and return Texture from {@link ResourcesLoader}
	 * 
	 * @param name Texture name
	 * @return Texture
	 * @throws NoSuchElementException
	 */
	public Texture getTexture(String name) throws NoSuchElementException {
		Objects.requireNonNull(name);
		checkIfLoaded();
		try {
			
			TextureLoader loader = textureLoader.get(name);
			return loader.load();
			
		} catch(Exception e) {
			NoSuchElementException exception = new NoSuchElementException("Cannot load the given Texture: "+name);
			exception.initCause(e);
			throw exception;
		}
	}
	
	/**
	 * Load and return Scenario from {@link ResourcesLoader}
	 * 
	 * @param name Scenario name
	 * @param factory Ship Factory for Scenario
	 * @return Scenario
	 * @throws NoSuchElementException
	 */
	public Scenario getScenario(String name, ShipFactory factory) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(factory);
		checkIfLoaded();
		try {
			
			ScenarioLoader loader = scenarioLoader.get(name);
			loader.addShipCreator(factory);
			return loader.load();
			
		} catch(Exception e) {
			NoSuchElementException exception = new NoSuchElementException("Cannot load the given Texture: "+name);
			exception.initCause(e);
			throw exception;
		}
	}
	
	/**
	 * Create a FontLoader for the given Font name.
	 * 
	 * @param fontID Font name
	 * @param size Font size
	 * @return FontLoader which will load the Font
	 */
	private static FontLoader createFontLoader(final String fontID, final float size) {
		return new FontLoader() {

			private Font font;
			
			@Override
			public Font load() throws Exception {
				if(font == null) {
					Foundation.ACTIVITY.debug(TAG, "Load Font: "+fontID);
					font = Font.createFont(Font.TRUETYPE_FONT, getPath().resolve(fontID).toFile());
					font = font.deriveFont(size);
				}
				return font;
			}

		};
	}
	
	/**
	 * Create a TextureLoader for the given Texture name.
	 * 
	 * @param textureID Texture name
	 * @return TextureLoader which will load the Texture
	 */
	private static TextureLoader createTextureLoader(final String textureID) {
		return new TextureLoader() {
			
			private Texture texture = null;
			
			@Override
			public Texture load() throws Exception {
				if(texture == null) {
					Foundation.ACTIVITY.debug(TAG, "Load Texture: "+textureID);
					texture = new Texture(getPath().resolve(textureID).toFile());
				}
				return texture;
			}
			
		};
	}
	
	/**
	 * Create a ScenarioLoader for the given Scenario name.
	 * 
	 * @param scenarioID Scenario name
	 * @return ScenarioLoader which will load the Scenario
	 */
	private static ScenarioLoader createScenarioLoader(final String scenarioID) {
		return new ScenarioLoader() {
			
			private Scenario scenario = null;
			
			@Override
			public Scenario load() throws Exception {
				if(scenario == null) {
					Foundation.ACTIVITY.debug(TAG, "Load Scenario: "+scenarioID);
					scenario = ScenarioParser.parse(getShipCreator(), getPath().resolve(scenarioID).toFile());
				}
				return scenario;
			}
			
		};
	}
	
	/**
	 * Create a TextureLoader and add it for a given Texture name.
	 * 
	 * @param textureID Texture name
	 */
	private void postTextureLoader(String textureID) {
		textureLoader.put(textureID, createTextureLoader(textureID));
	}
	
	/**
	 * Create a FontLoader and add it for a given Font name and size.
	 * 
	 * @param fontID Font name
	 * @param size Font size
	 */
	private void postFontLoader(String fontID, float size) {
		fontLoader.put(fontID, createFontLoader(fontID, size));
	}
	
	/**
	 * Create a ScenarioLoader and add it for a given Scenario name.
	 * 
	 * @param scenarioID Scenario name
	 */
	private void postScenarioLoader(String scenarioID) {
		scenarioLoader.put(scenarioID, createScenarioLoader(scenarioID));
	}
	
	/**
	 * <p>
	 * Check if the {@link Resources} object is loaded.<br>
	 * (ie: {@link Resources#load()} has been called once).
	 * 
	 * <p>
	 * Otherwise, throw an {@link IllegalStateException}.
	 */
	private void checkIfLoaded() {
		if(loaded == false) {
			throw new IllegalStateException("You must load all resources before using them. Use load()");
		}
	}
	
}
