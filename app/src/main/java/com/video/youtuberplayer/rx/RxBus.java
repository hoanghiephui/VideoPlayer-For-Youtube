package com.video.youtuberplayer.rx;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.Observable;

/**
 * Created by hoanghiep on 4/24/17.
 */

public class RxBus {
  private final Relay<Object> bus = PublishRelay.create().toSerialized();

  public void send(Object event) {
    bus.accept(event);
  }

  public Observable<Object> toObservable() {
    return bus;
  }

  public boolean hasObservers() {
    return bus.hasObservers();
  }
}
