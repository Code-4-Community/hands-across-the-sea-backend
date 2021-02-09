package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.SCHOOLS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeforcommunity.JooqMock;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.UpsertSchoolRequest;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.AdminOnlyRouteException;
import com.codeforcommunity.processor.authenticated.ProtectedSchoolProcessorImpl;
import java.util.Random;
import org.jooq.generated.tables.records.SchoolsRecord;
import org.junit.jupiter.api.Test;

public class ProtectedSchoolProcessorImplTest {

  private JooqMock myJooqMock;
  private ProtectedSchoolProcessorImpl schoolProcessor;

  private void setup() {
    this.myJooqMock = new JooqMock();
    this.schoolProcessor = new ProtectedSchoolProcessorImpl(myJooqMock.getContext());
  }

  /** Test attempting to create a school as a standard user. */
  @Test
  public void testCreateSchoolNonAdmin() {
    this.setup();
    Random rand = new Random();

    String name = generateRandomString(6, 24);
    String addr = generateRandomString(6, 24);
    String email = generateRandomString(6, 24);
    String phone = generateRandomString(6, 24);
    String notes = generateRandomString(6, 24);
    String area = generateRandomString(6, 24);
    Boolean hidden = rand.nextBoolean();

    Country[] countries = Country.values();
    Country country = countries[rand.nextInt(countries.length)];

    LibraryStatus[] statuses = LibraryStatus.values();
    LibraryStatus status = statuses[rand.nextInt(statuses.length)];

    // Create the new school request
    UpsertSchoolRequest request = new UpsertSchoolRequest();
    request.setName(name);
    request.setAddress(addr);
    request.setEmail(email);
    request.setPhone(phone);
    request.setNotes(notes);
    request.setArea(area);
    request.setHidden(hidden);
    request.setCountry(country);
    request.setLibraryStatus(status);

    // Mock the DB
    JWTData user = mock(JWTData.class);
    when(user.getPrivilegeLevel()).thenReturn(PrivilegeLevel.STANDARD);
    when(user.isAdmin()).thenReturn(false);

    try {
      schoolProcessor.createSchool(user, request);
      fail();
    } catch (AdminOnlyRouteException e) {
      // Expected exception
    }
  }

  /** Test attempting to create a school as a standard user. */
  @Test
  public void testCreateSchoolAdmin() {
    this.setup();
    Random rand = new Random();

    String name = generateRandomString(6, 24);
    String addr = generateRandomString(6, 24);
    String email = generateRandomString(6, 24);
    String phone = generateRandomString(6, 24);
    String notes = generateRandomString(6, 24);
    String area = generateRandomString(6, 24);
    Boolean hidden = rand.nextBoolean();

    Country[] countries = Country.values();
    Country country = countries[rand.nextInt(countries.length)];

    LibraryStatus[] statuses = LibraryStatus.values();
    LibraryStatus status = statuses[rand.nextInt(statuses.length)];

    // Create the new school request
    UpsertSchoolRequest request = new UpsertSchoolRequest();
    request.setName(name);
    request.setAddress(addr);
    request.setEmail(email);
    request.setPhone(phone);
    request.setNotes(notes);
    request.setArea(area);
    request.setHidden(hidden);
    request.setCountry(country);
    request.setLibraryStatus(status);

    // Mock the DB
    JWTData user = mock(JWTData.class);
    when(user.getPrivilegeLevel()).thenReturn(PrivilegeLevel.ADMIN);
    when(user.isAdmin()).thenReturn(true);

    // Mock the event
    SchoolsRecord record = myJooqMock.getContext().newRecord(SCHOOLS);
    record.setId(0);
    record.setName(name);
    record.setAddress(addr);
    record.setEmail(email);
    record.setPhone(phone);
    record.setNotes(notes);
    record.setArea(area);
    record.setHidden(hidden);
    record.setCountry(country);
    record.setLibraryStatus(status);

    myJooqMock.addEmptyReturn("SELECT"); // Check if school exists
    myJooqMock.addReturn("INSERT", record); // Insert school

    School res = schoolProcessor.createSchool(user, request);
    assertEquals(record.getName(), res.getName());
    assertEquals(record.getAddress(), res.getAddress());
    assertEquals(record.getEmail(), res.getEmail());
    assertEquals(record.getPhone(), res.getPhone());
    assertEquals(record.getNotes(), res.getNotes());
    assertEquals(record.getArea(), res.getArea());
    assertEquals(record.getHidden(), res.getHidden());
    assertEquals(record.getCountry(), res.getCountry());
    assertEquals(record.getLibraryStatus(), res.getLibraryStatus());
  }

  private String generateRandomString(int minLength, int maxLength) {
    Random random = new Random();
    return random
        .ints(97, 149)
        .limit(random.nextInt((maxLength - minLength)) + minLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
