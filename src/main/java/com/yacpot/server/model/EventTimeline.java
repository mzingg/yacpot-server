package com.yacpot.server.model;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventTimeline extends AbstractGenericModel<EventTimeline> {

  private final List<EventIncarnation> incarnations;

  public EventTimeline() {
    this.incarnations = new ArrayList<>();
  }

  public EventTimeline addIncarnation(EventIncarnation toAdd) {
    this.incarnations.add(toAdd);
    return this;
  }

  public List<EventIncarnation> getSortedIncarnations(LocalDateTime anchorDate) {
    List<EventIncarnation> clonedList = new ArrayList<>(incarnations);

    final LocalDateTime anchorDateForAnonClass = anchorDate;
    Collections.sort(clonedList, new Comparator<EventIncarnation>() {
      @Override
      public int compare(EventIncarnation o1, EventIncarnation o2) {
        return o1.getSortDate(anchorDateForAnonClass).compareTo(o2.getSortDate(anchorDateForAnonClass));
      }
    });

    return Collections.unmodifiableList(clonedList);
  }

  public boolean hasIncarnationsDuring(LocalDate startDate, LocalDate endDate) {
    for (EventIncarnation inc : incarnations) {
      if (inc.takesPlaceDuring(startDate, endDate)) {
        return true;
      }
    }
    return false;
  }

  public LocalDateTime getSortDate(LocalDateTime anchorDate) {
    if (incarnations.size() == 0) {
      return getTimestamp();
    }

    // The algorithm below should work as well without this if.
    // But since having only one addIncarnation will be the main use case the statement is here for optimzation.
    if (incarnations.size() == 1) {
      return incarnations.get(0).getSortDate(anchorDate);
    }

    List<EventIncarnation> sortedIncarnations = getSortedIncarnations(anchorDate);

    // Multiple incarnations: find the nearest to the anchor getDate of these values < anchorDate
    // if there are values > anchorDate return the first of these.
    int resultIndex = 0;
    int minimalSecondDifference = Integer.MAX_VALUE;
    for (int i = 0; i < sortedIncarnations.size(); i++) {
      LocalDateTime currentDate = sortedIncarnations.get(i).getSortDate(anchorDate);
      if (currentDate.isAfter(anchorDate)) {
        resultIndex = i;
        break;
      }
      int difference = Seconds.secondsBetween(currentDate, anchorDate).getSeconds();
      if (difference < minimalSecondDifference) {
        minimalSecondDifference = difference;
        resultIndex = i;
      }
    }

    return incarnations.get(resultIndex).getSortDate(anchorDate);
  }
}
