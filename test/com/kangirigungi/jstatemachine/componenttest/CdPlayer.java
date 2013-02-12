package com.kangirigungi.jstatemachine.componenttest;

import org.junit.Test;

public class CdPlayer {
	
	private static enum States {
		Empty, Stopped, Playing, Paused, Open
	}
	
	private static enum Events {
		CdDetected, PLay, Stop, Pause, OpenClose 
	}
	
	private static class Actions {
		
	}
	
	@Test
	public void cdPlayer() {
		
	}
}
