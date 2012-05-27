package uk.co.unclealex.hammers.calendar.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.util.CanWait;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;

public class WaitingPresenter {

  public static interface Display extends IsWidget {
    public PopupPanel getPopupPanel();
  }
  
  private final Display i_display;
  private int i_registrations;  
  
  @Inject
  public WaitingPresenter(Display display) {
    super();
    i_display = display;
    Window.ScrollHandler windowScrollHandler = new Window.ScrollHandler() {
      @Override
      public void onWindowScroll(ScrollEvent event) {
        getDisplay().getPopupPanel().setPopupPosition(event.getScrollLeft(), event.getScrollTop());
      }
    };
    Window.addWindowScrollHandler(windowScrollHandler);
  }

  public void registerAsWaiting(CanWait source) {
    int registrations = getRegistrations();
    if (registrations == 0) {
      getDisplay().getPopupPanel().show();
    }
    setRegistrations(registrations + 1);
  }
  
  public void unregisterAsWaiting(CanWait source) {
    int registrations = getRegistrations() - 1;
    setRegistrations(registrations);
    if (registrations == 0) {
      getDisplay().getPopupPanel().hide();
    }
  }

  protected int getRegistrations() {
    return i_registrations;
  }

  protected void setRegistrations(int registrations) {
    i_registrations = registrations;
  }

  public Display getDisplay() {
    return i_display;
  }
}
