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

package fr.escape.app;

import java.util.LinkedList;
import java.util.Queue;

import fr.escape.E;
import fr.umlv.zen2.Application;
import fr.umlv.zen2.ApplicationCode;
import fr.umlv.zen2.ApplicationContext;

public final class Activity {

	public static final int LOG_NONE = 0;
	public static final int LOG_DEBUG = 3;
	public static final int LOG_INFO = 2;
	public static final int LOG_ERROR = 1;
	
	private final Graphics graphics;
	private final Queue<Runnable> runnables = new LinkedList<Runnable>();
	//private final Audio audio;
	//private final Files files;
	//private final Input input;
	private final String title;
	private int logLevel;
	
	public Activity(Game game) {
		this(game, new Configuration());
	}
	
	public Activity(Game game, Configuration configuration) {
		
		graphics = new Graphics(game, configuration);
		logLevel = LOG_INFO;
		title = configuration.title;
		
		E.activity = this;
		E.graphics = graphics;

		initialize();
	}
	
	private void initialize () {
		
		Application.run(title, graphics.getWidth(), graphics.getHeight(), new ApplicationCode() {
			
			@Override
			public void run(ApplicationContext context) {
				
				try {
					
					debug("Activity", "Application started");
					
					for(;;) {
						
						/**
						 * May need to implements QoS in Runnable execution.
						 */
						int executionTime = 0;
						synchronized(runnables) {
							
							if(runnables.isEmpty()) {
								
								long start = System.currentTimeMillis();
								Runnable next;
								
								while((next = runnables.poll()) != null) {
									try {
										next.run();
									} catch(Throwable t) {
										error("Activity - Runnable", "Error while executing a Runnable", t);
									}
								}
								
								executionTime = (int) (System.currentTimeMillis() - start);
								debug("Activity - Runnable", "Runnable(s) executed in "+executionTime+" ms");
							}
						}
						
						try {
							
							int sleep = graphics.getNextWakeUp() - executionTime;
							if(sleep > 0) {
								Thread.sleep(sleep);
							}
							
						} catch(InterruptedException e) {
							Thread.currentThread().interrupt();
						}
						
						
					}
					
				} finally {
					debug("Activity", "Application closed");			
				}
				
			}
			
		});
		
	}
	
	
	
	/** 
	 * Logs a message to the console.
	 */
	public void log(String tag, String message) {
		if(logLevel >= LOG_INFO) {
			System.out.println(tag + ": " + message);
		}
	}

	/**
	 * Logs a message to the console.
	 */
	public void log(String tag, String message, Exception exception) {
		if(logLevel >= LOG_INFO) {
			log(tag, message);
			exception.printStackTrace(System.out);
		}
	}

	/** 
	 * Logs an error message to the console.
	 */
	public void error(String tag, String message) {
		if (logLevel >= LOG_ERROR) {
			System.err.println(tag + ": " + message);
		}
	}

	/** 
	 * Logs an error message to the console.
	 */
	public void error(String tag, String message, Throwable exception) {
		if(logLevel >= LOG_ERROR) {
			error(tag, message);
			exception.printStackTrace(System.err);
		}
	}

	/** 
	 * Logs a debug message to the console.
	 */
	public void debug(String tag, String message) {
		if(logLevel >= LOG_DEBUG) {
			System.out.println(tag + ": " + message);
		}
	}

	/** 
	 * Logs a debug message to the console.
	 */
	public void debug(String tag, String message, Throwable exception) {
		if(logLevel >= LOG_DEBUG) {
			debug(tag, message);
			exception.printStackTrace(System.out);
		}
	}

	/** 
	 * Sets the log level. 
	 * 
	 * {@link #LOG_NONE} will mute all log output. 
	 * {@link #LOG_ERROR} will only let error messages through.
	 * {@link #LOG_INFO} will let all non-debug messages through, and {@link #LOG_DEBUG} will let all messages through.
	 * 
	 * @param logLevel {@link #LOG_NONE}, {@link #LOG_ERROR}, {@link #LOG_INFO}, {@link #LOG_DEBUG}. 
	 */
	public void setLogLevel(int logLevel) {
		switch(logLevel) {
			case LOG_NONE: case LOG_ERROR: case LOG_INFO: case LOG_DEBUG: {
				this.logLevel = logLevel;
				break;
			}
			default: {
				throw new IllegalArgumentException("Unknown Log Level");
			}
		}
		
	}
	
	public Graphics getGraphics () {
		return graphics;
	}
	
	/**
	 * // TODO
	 * 
	 * @param runnable Runnable to execute.
	 */
	public void post(Runnable runnable) {
		synchronized (runnables) {
			runnables.add(runnable);
			// graphics.requestRendering();
		}
	}
	
}
