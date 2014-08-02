package com.yacpot.core.persistence;

import com.yacpot.core.model.AbstractModel;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;

import java.util.*;

public class TestModel extends AbstractModel<TestModel> {

  private byte byteValue;

  private short shortValue;

  private int intValue;

  private long longValue;

  private float floatValue;

  private double doubleValue;

  private boolean boolValue;

  private char charValue;

  private String stringValue;

  private LocalDateTime jodaTimeValue;

  private Date dateValue;

  private Collection<String> simpleList;

  private Map<Integer, String> simpleMap;

  private TestSubModelOne subModelValue;

  private final Collection<TestSubModelOne> subModels;

  private Map<String, TestSubModelOne> stringKeyMap;

  private Map<TestSubModelOne, TestSubModelTwo> modelKeyMap;

  private Map<TestSubModelOne, Collection<TestSubModelTwo>> modelKeyMapWithListValue;

  public TestModel() {
    super();

    stringValue = StringUtils.EMPTY;

    jodaTimeValue = LocalDateTime.now();
    dateValue = new Date();

    simpleList = new ArrayList<>();
    simpleMap = new HashMap<>();

    subModels = new ArrayList<>();
    stringKeyMap = new HashMap<>();
    modelKeyMap = new HashMap<>();
    modelKeyMapWithListValue = new HashMap<>();
  }

  public Collection<String> getSimpleList() {
    return Collections.unmodifiableCollection(simpleList);
  }

  // Normal setter (no adder)
  public TestModel setSimpleList(Collection<String> simpleList) {
    this.simpleList = simpleList;
    return this;
  }

  public Collection<TestSubModelOne> getSubModels() {
    return Collections.unmodifiableCollection(subModels);
  }

  // Adder
  public TestModel addSubModel(TestSubModelOne subModel) {
    this.subModels.add(subModel);
    return this;
  }

  public Map<String, TestSubModelOne> getStringKeyMap() {
    return Collections.unmodifiableMap(stringKeyMap);
  }

  public TestModel setStringKeyMap(Map<String, TestSubModelOne> stringKeyMap) {
    this.stringKeyMap = stringKeyMap;
    return this;
  }

  public Map<TestSubModelOne, TestSubModelTwo> getModelKeyMap() {
    return Collections.unmodifiableMap(modelKeyMap);
  }

  public TestModel setModelKeyMap(Map<TestSubModelOne, TestSubModelTwo> modelKeyMap) {
    this.modelKeyMap = modelKeyMap;
    return this;
  }

  public Map<TestSubModelOne, Collection<TestSubModelTwo>> getModelKeyMapWithListValue() {
    return Collections.unmodifiableMap(modelKeyMapWithListValue);
  }

  public TestModel setModelKeyMapWithListValue(Map<TestSubModelOne, Collection<TestSubModelTwo>> modelKeyMapWithListValue) {
    this.modelKeyMapWithListValue = modelKeyMapWithListValue;
    return this;
  }

  public TestSubModelOne getSubModelValue() {
    return subModelValue;
  }

  public TestModel setSubModelValue(TestSubModelOne subModelValue) {
    this.subModelValue = subModelValue;
    return this;
  }

  public byte getByteValue() {
    return byteValue;
  }

  public Map<Integer, String> getSimpleMap() {
    return Collections.unmodifiableMap(simpleMap);
  }

  public TestModel setSimpleMap(Map<Integer, String> simpleMap) {
    this.simpleMap = simpleMap;
    return this;
  }

  public TestModel setByteValue(byte byteValue) {
    this.byteValue = byteValue;
    return this;
  }

  public short getShortValue() {
    return shortValue;
  }

  public TestModel setShortValue(short shortValue) {
    this.shortValue = shortValue;
    return this;
  }

  public int getIntValue() {
    return intValue;
  }

  public TestModel setIntValue(int intValue) {
    this.intValue = intValue;
    return this;
  }

  public long getLongValue() {
    return longValue;
  }

  public TestModel setLongValue(long longValue) {
    this.longValue = longValue;
    return this;
  }

  public float getFloatValue() {
    return floatValue;
  }

  public TestModel setFloatValue(float floatValue) {
    this.floatValue = floatValue;
    return this;
  }

  public double getDoubleValue() {
    return doubleValue;
  }

  public TestModel setDoubleValue(double doubleValue) {
    this.doubleValue = doubleValue;
    return this;
  }

  public boolean isBoolValue() {
    return boolValue;
  }

  public TestModel setBoolValue(boolean boolValue) {
    this.boolValue = boolValue;
    return this;
  }

  public char getCharValue() {
    return charValue;
  }

  public TestModel setCharValue(char charValue) {
    this.charValue = charValue;
    return this;
  }

  public String getStringValue() {
    return stringValue;
  }

  public TestModel setStringValue(String stringValue) {
    this.stringValue = stringValue;
    return this;
  }

  public LocalDateTime getJodaTimeValue() {
    return jodaTimeValue;
  }

  public TestModel setJodaTimeValue(LocalDateTime jodaTimeValue) {
    this.jodaTimeValue = jodaTimeValue;
    return this;
  }

  public Date getDateValue() {
    return dateValue;
  }

  public TestModel setDateValue(Date dateValue) {
    this.dateValue = dateValue;
    return this;
  }
}