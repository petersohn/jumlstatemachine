package com.kangirigungi.jstatemachine.componenttest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.kangirigungi.jstatemachine.IState;
import com.kangirigungi.jstatemachine.ITransitionAction;
import com.kangirigungi.jstatemachine.StateMachine;

public class CdPlayer {
	
	private static enum States {
		Empty, Stopped, Playing, Paused, Open
	}
	
	private static enum Events {
		CdDetected, Play, Stop, Pause, OpenClose 
	}

	private static enum Actions {
		 StoreCdInfo, StartPlayback, StopPlayback, PausePlayback,
		 ResumePlayback, StopAndOpen, OpenDrawer, CloseDrawer
	}
	
	private StateMachine<States, Events> stateMachine;
	private Actions lastAction;
	
	private class ActionHandler implements 
			ITransitionAction<States, Events> {

		private Actions action;
		
		public ActionHandler(Actions action) {
			this.action = action;
		}
		
		@Override
		public void onTransition(IState<States, Events> fromState,
				IState<States, Events> toState, Events event) {
			System.out.println(fromState.getId().toString()+": "+
				event.toString()+"/"+action.toString()+" -> "+
				toState.getId().toString());
			lastAction = action;
		}
		
	}
	
	@Before
	public void initialize() {
		stateMachine = new StateMachine<States, Events>();
		
		stateMachine.addState(States.Empty);
		stateMachine.addState(States.Stopped);
		stateMachine.addState(States.Playing);
		stateMachine.addState(States.Open);
		stateMachine.addState(States.Paused);
		
		stateMachine.setInitialState(States.Empty);
		
		stateMachine.addTransition(States.Empty,    Events.CdDetected, 
				new ActionHandler(Actions.StoreCdInfo),    States.Stopped);
		stateMachine.addTransition(States.Empty,    Events.OpenClose, 
				new ActionHandler(Actions.OpenDrawer),     States.Open);
		stateMachine.addTransition(States.Stopped,  Events.Play, 
				new ActionHandler(Actions.StartPlayback),  States.Playing);
		stateMachine.addTransition(States.Stopped,  Events.OpenClose, 
				new ActionHandler(Actions.OpenDrawer),     States.Open);
		stateMachine.addTransition(States.Playing,  Events.Pause, 
				new ActionHandler(Actions.PausePlayback),  States.Paused);
		stateMachine.addTransition(States.Playing,  Events.Stop, 
				new ActionHandler(Actions.StopPlayback),   States.Stopped);
		stateMachine.addTransition(States.Playing,  Events.OpenClose, 
				new ActionHandler(Actions.StopAndOpen),    States.Open);
		stateMachine.addTransition(States.Paused,   Events.Pause, 
				new ActionHandler(Actions.ResumePlayback), States.Playing);
		stateMachine.addTransition(States.Paused,   Events.Stop, 
				new ActionHandler(Actions.StopPlayback),   States.Stopped);
		stateMachine.addTransition(States.Paused,   Events.OpenClose, 
				new ActionHandler(Actions.StopAndOpen),    States.Open);
		stateMachine.addTransition(States.Open,     Events.OpenClose, 
				new ActionHandler(Actions.CloseDrawer),    States.Empty);
		stateMachine.start();
	}
	
	@Test
	public void playStopOpenClose() {
		Assert.assertEquals(States.Empty, stateMachine.getcurrentState().getId());
		
		stateMachine.processEvent(Events.CdDetected);
		Assert.assertEquals(States.Stopped, stateMachine.getcurrentState().getId());
		Assert.assertEquals(Actions.StoreCdInfo, lastAction);
		
		stateMachine.processEvent(Events.Play);
		Assert.assertEquals(States.Playing, stateMachine.getcurrentState().getId());
		Assert.assertEquals(Actions.StartPlayback, lastAction);
		
		stateMachine.processEvent(Events.Stop);
		Assert.assertEquals(States.Stopped, stateMachine.getcurrentState().getId());
		Assert.assertEquals(Actions.StopPlayback, lastAction);
		
		stateMachine.processEvent(Events.OpenClose);
		Assert.assertEquals(States.Open, stateMachine.getcurrentState().getId());
		Assert.assertEquals(Actions.OpenDrawer, lastAction);
		
		stateMachine.processEvent(Events.OpenClose);
		Assert.assertEquals(States.Empty, stateMachine.getcurrentState().getId());
		Assert.assertEquals(Actions.CloseDrawer, lastAction);
	}
}
