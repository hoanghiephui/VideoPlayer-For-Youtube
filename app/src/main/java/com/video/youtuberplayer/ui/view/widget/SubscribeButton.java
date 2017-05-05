package com.video.youtuberplayer.ui.view.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.video.youtuberplayer.R;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class SubscribeButton extends AppCompatButton {
  public SubscribeButton(Context context) {
    super(context);
  }

  /**
   * Is user subscribed to a channel?
   */
  private boolean isUserSubscribed = false;


  public SubscribeButton(Context context, AttributeSet attrs) {
    super(context, attrs);
  }


  public boolean isUserSubscribed() {
    return isUserSubscribed;
  }


  /**
   * Set the button's state to subscribe (i.e. once clicked, the user indicates that he wants to
   * subscribe).
   */
  public void setSubscribeState() {
    setText(R.string.subscribe);
    isUserSubscribed = false; // the user is currently NOT subscribed
  }


  /**
   * Set the button's state to unsubscribe (i.e. once clicked, the user indicates that he wants to
   * unsubscribe).
   */
  public void setUnsubscribeState() {
    setText(R.string.unsubscribe);
    isUserSubscribed = true;
  }

}
