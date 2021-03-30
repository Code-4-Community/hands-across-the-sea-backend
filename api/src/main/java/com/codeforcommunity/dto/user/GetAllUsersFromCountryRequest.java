package com.codeforcommunity.dto.user;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.exceptions.HandledException;
import java.util.List;

public class GetAllUsersFromCountryRequest extends ApiDto {

  Country country;

  public GetAllUsersFromCountryRequest(Country country) {
    this.country = country;
  }

  public Country getCountry() {
    return this.country;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    return null;
  }
}
