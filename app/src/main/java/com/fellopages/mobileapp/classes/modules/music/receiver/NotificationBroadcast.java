/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.fellopages.mobileapp.classes.modules.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;

import com.fellopages.mobileapp.classes.modules.music.MusicView;
import com.fellopages.mobileapp.classes.modules.music.controls.Controls;
import com.fellopages.mobileapp.classes.modules.music.service.SongService;
import com.fellopages.mobileapp.classes.modules.music.utils.PlayerConstants;

public class NotificationBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
			assert keyEvent != null;
			if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                	if(!PlayerConstants.SONG_PAUSED){
    					Controls.pauseControl(context);
                	}else{
    					Controls.playControl(context);
                	}
                	break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                	break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                	break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                	break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                	Controls.nextControl(context);
                	break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                	Controls.previousControl(context);
                	break;
            }
		}  else{
            	if (intent.getAction().equals(SongService.NOTIFY_PLAY)) {
					if (!SongService.isMediaPlayerPlaying())
    				Controls.playControl(context);
        		} else if (intent.getAction().equals(SongService.NOTIFY_PAUSE)) {
					if (SongService.isMediaPlayerPlaying())
						Controls.pauseControl(context);
        		} else if (intent.getAction().equals(SongService.NOTIFY_NEXT)) {
					if (SongService.isSongPlaying) {
						SongService.isSongPlaying = false;
						MusicView.counter++;
						Controls.nextControl(context);
						MusicView.mSeekBar.setEnabled(false);
						MusicView.loadingProgressBar.setVisibility(View.VISIBLE);
					}
        		} else if (intent.getAction().equals(SongService.NOTIFY_DELETE)) {
					if (SongService.isMediaPlayerIntialized) {
						SongService.isSongPlaying = true;
						PlayerConstants.SONG_NUMBER = -1;
						MusicView.counter = 0;
						Intent i = new Intent(context, SongService.class);
						context.stopService(i);
						if (MusicView.control_container != null)
							MusicView.control_container.setVisibility(View.GONE);
						SongService.isMediaPlayerIntialized = false;
						if (SongService.mp != null) {
							SongService.mp.stop();
							SongService.mp.reset();
						}
					}
        		}else if (intent.getAction().equals(SongService.NOTIFY_PREVIOUS)) {
					if (SongService.isSongPlaying) {
						SongService.isSongPlaying = false;
						MusicView.counter++;
						Controls.previousControl(context);
						MusicView.mSeekBar.setEnabled(false);
						MusicView.loadingProgressBar.setVisibility(View.VISIBLE);
					}
        		}
		}
	}
	
	public String ComponentName() {
		return this.getClass().getName(); 
	}
}
