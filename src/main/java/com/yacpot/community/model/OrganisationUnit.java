package com.yacpot.community.model;

import com.yacpot.core.model.AbstractGenericModel;
import com.yacpot.core.model.sort.GenericModelComparator;
import com.yacpot.server.model.SecurityRole;

import java.util.*;

public class OrganisationUnit extends AbstractGenericModel<OrganisationUnit> {

  private final SortedSet<Room> roomList;
  private final List<SecurityRole> roleList;

  public OrganisationUnit() {
    super();
    roomList = new TreeSet<>(new GenericModelComparator<>().byLabel());
    roleList = new ArrayList<>();
  }

  public OrganisationUnit addRoom(Room room) {
    roomList.add(room);
    return this;
  }

  public SortedSet<Room> getRooms() {
    return Collections.unmodifiableSortedSet(roomList);
  }

  public OrganisationUnit addRole(SecurityRole role) {
    roleList.add(role);
    return this;
  }

  public Collection<SecurityRole> getRoles() {
    return Collections.unmodifiableCollection(roleList);
  }
}
