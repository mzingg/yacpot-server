package com.yacpot.server.tests.model;

import com.yacpot.server.model.Event;
import com.yacpot.server.model.Room;
import org.junit.Test;

import static com.yacpot.server.tests.ModelTestUtil.toJoinedString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RoomModelTest {

  @Test
  public void testChannelNotNull() {
    Room room = new Room();

    assertNotNull(room.channel());
  }

  @Test
  public void testAddEvents() {
    Room room = new Room().event(new Event().label("Event A")).event(new Event().label("Event B"));

    assertEquals("Event A,Event B", toJoinedString(room.events()));
  }
}
