package com.yacpot.server.model.sort;

import com.yacpot.server.model.Event;

public class EventModelComparator extends GenericModelComparator<Event> {

  private boolean sortByEventDate;

  public EventModelComparator() {
    super();
    this.sortByEventDate = true;
  }

  public EventModelComparator sortByEventDate(boolean flag) {
    this.sortByEventDate = flag;
    return this;
  }

  @Override
  public int compare(Event o1, Event o2) {
    if (!sortByEventDate) return super.compare(o1, o2);

    // TODO: Implement event sorting
    return 0;
  }
}
