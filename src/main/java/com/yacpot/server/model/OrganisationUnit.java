package com.yacpot.server.model;

import com.yacpot.server.model.sort.GenericModelComparator;

import java.util.*;

public class OrganisationUnit extends GenericModel<OrganisationUnit> {

  private final Set<Room> roomList;
  private final List<SecurityRole> roleList;

  public OrganisationUnit() {
    super();
    roomList = new TreeSet<>(new GenericModelComparator<>().byLabel());
    roleList = new ArrayList<>();
  }

  public OrganisationUnit room(Room room) {
    roomList.add(room);
    return this;
  }

  public Collection<Room> rooms() {
    return Collections.unmodifiableCollection(roomList);
  }

  public OrganisationUnit role(SecurityRole role) {
    roleList.add(role);
    return this;
  }

  public Collection<SecurityRole> roles() {
    return Collections.unmodifiableCollection(roleList);
  }
}
