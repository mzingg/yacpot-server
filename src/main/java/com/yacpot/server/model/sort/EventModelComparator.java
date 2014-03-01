package com.yacpot.server.model.sort;

import com.yacpot.server.model.Event;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class EventModelComparator extends AbstractModelComparator<Event, EventModelComparator> {

  private boolean sortByEventDate;
  private LocalDateTime anchorDate;

  public EventModelComparator() {
    super();
    this.sortByEventDate = true;
    this.anchorDate = LocalDateTime.now();
  }

  public void anchorDate(LocalDateTime anchorDate) {
    this.anchorDate = anchorDate;
  }

  public EventModelComparator sortByEventDate(boolean flag) {
    this.sortByEventDate = flag;
    return this;
  }

  @Override
  public int compare(Event o1, Event o2) {
    if (!sortByEventDate) return super.compare(o1, o2);

    LocalDateTime sortDate1 = o1.timeline().sortDate(anchorDate);
    LocalDateTime sortDate2 = o2.timeline().sortDate(anchorDate);

    if (sortDate1.equals(sortDate2)) {
      byLabel();
      return super.compare(o1, o2);
    }

    return sortDate1.compareTo(sortDate2);
  }
}
