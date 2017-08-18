package net.aircommunity.platform.model.domain;

/**
 * Domain constants. (although use interface as constants holder is not a semantically good idea, just for simplicity
 * and readability)
 * 
 * @author Bin.Zhang
 */
interface DomainConstants {

	// UUID
	int ID_LEN = 36;
	int DEFAULT_FIELD_LEN = 255;

	// ***********
	// Account
	// ***********

	// current used max is 16
	int ACCOUNT_ROLE_LEN = 20;
	// current used is 32 (UUID without hyphen)
	int ACCOUNT_APIKEY_LEN = 32;

	// current used is 60 (BCrypt hashed)
	// ref:
	// https://stackoverflow.com/questions/5881169/what-column-type-length-should-i-use-for-storing-a-bcrypt-hashed-password-in-a-d
	int ACCOUNT_PASSWORD_LEN = 60;

	// current used max is 8
	int ACCOUNT_STATUS_LEN = 8;

	// current used max is 8 (just make it a little bit longer for now)
	int ACCOUNT_AUTHTYPE_LEN = 10;

	// allow username, email, mobile etc. And email max length can be 254 (ref:
	// https://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address)
	int ACCOUNT_PRINCIPAL_LEN = 255;

	// store OAuth access token, just in case its longer than 255
	int ACCOUNT_CREDENTIAL_LEN = 512;
	int USER_RANK_LEN = 10;
	// current max is 10
	int TENANT_VERIFICATION_STATUS_LEN = 10;

	// ***********
	// Product
	// ***********
	int PRODUCT_NAME_LEN = 50;
	int PRODUCT_REVIEW_STATUS_LEN = 10;
	// current used max is 12
	int PRODUCT_TYPE_LEN = 15;
	// current used max is 12
	int PRODUCT_CATEGORY_LEN = 15;
	int PRODUCT_FAQ_TITLE_LEN = 255;
	// current used max is 5
	int PRODUCT_COMMENT_SOURCE_LEN = 8;
	// current used max is 10
	int APRON_TYPE_LEN = 10;
	int SCHOOL_NAME_LEN = 50;
	// XXX is it a reasonable length? (same as city name)
	int COURSE_LOCATION_LEN = 50;

	// ***********
	// Order
	// ***********
	// current used len is 15
	int ORDER_NO_LEN = 18;
	// normally request no is used when refund
	int ORDER_REQUEST_NO_LEN = 32;
	// just in case it's long
	int TRADE_NO_LEN = 50;
	// current used max is 12
	int ORDER_TYPE_LEN = 15;
	// current used max is 18
	int ORDER_STATUS_LEN = 20;
	// e.g. iOS, Android, Windows Phone, H5, Web etc. TODO: improve to reduce length?
	int ORDER_CHANNEL_LEN = 50;
	// is it enough?
	int SALES_PACKAGE_NAME_LEN = 20;
	int SALES_PACKAGE_PRICES_LEN = 255;
	int PAYMENT_METHOD_LEN = 10;
	int TRADE_STATUS_LEN = 10;

	// ***********
	// Aircraft
	// ***********
	// a bit longer for now. e.g. B-12345
	// ref: https://zh.wikipedia.org/wiki/%E8%88%AA%E7%A9%BA%E5%99%A8%E8%A8%BB%E5%86%8A%E7%B7%A8%E8%99%9F
	// https://en.wikipedia.org/wiki/Flight_number
	int AIRCRAFT_FLIGHT_NO_LEN = 15;
	int AIRCRAFT_TYPE_LEN = 40;
	int AIRCRAFT_NAME_LEN = 40;
	int AIRCRAFT_CATEGORY_LEN = 20;
	// unavailable, available, unknown etc.
	int AIRCRAFT_STATUS_LEN = 20;
	// current max is 9
	int ORDER_ITEM_CANDIDATE_STATUS_LEN = 10;
	// the number of seats, e.g. 11-14 guests, TODO define a standard format to reduce length?
	int AIRCRAFT_CAPACITY_LEN = 10;
	// Private Pilot Certificate license
	int AIRCRAFT_LICENSE_LEN = 10;
	// TODO better make it int instead of string (used in AirTaxi, AirTour)
	int FLYING_DISTANCE_LEN = 10;
	int FLYING_DURATION_LEN = 10;

	// ***********
	// Common
	// ***********
	int SETTING_NAME_LEN = 50;
	int SETTING_CATEGORY_LEN = 50;
	// https://en.wikipedia.org/wiki/ISO_4217 (3 chars)
	int CURRENCY_UNIT_LEN = 3;
	// current format is, HH:mm-HH:mm, e.g. 08:00-09:00
	int TIMESLOT_LEN = 11;
	// TODO: a better reasonable length?
	int AIRPORT_NAME_LEN = 50;
	int COUNTRY_NAME_LEN = 50;
	int PROVINCE_NAME_LEN = 50;
	int CITY_NAME_LEN = 50;
	int TIMEZONE_LEN = 50;
	int IATA3_LEN = 3;
	int ICAO4_LEN = 4;
	// geo-address
	int ADDRESS_LEN = 1024;
	// e.g. contact of school
	int CONTACT_LEN = 255;
	// the entire number should be 15 digits or shorter, ref: http://en.wikipedia.org/wiki/Telephone_number
	int MOBILE_LEN = 15;
	// ID card (15 or 18), passport(range from 8 to 9 digits), etc. just make it a bit longer
	int IDENTITY_LEN = 20;
	int GENDER_LEN = 6;
	// And email max length can be 254 (ref:
	// https://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address)
	int EMAIL_LEN = 255;
	// person name, ref:
	// https://stackoverflow.com/questions/30485/what-is-a-reasonable-length-limit-on-person-name-fields
	int PERSON_NAME_LEN = 70;
	int DOMAIN_NAME_LEN = 255;
	// just make it shorter
	// ref: https://stackoverflow.com/questions/417142/what-is-the-maximum-length-of-a-url-in-different-browsers
	int URL_LEN = 2000;
	int IMAGE_URL_LEN = URL_LEN;
	// Link type (product | content)
	int LINK_TYPE_LEN = 8;

	// push notification type
	int PUSH_NOTIFICATION_TYPE_LEN = 15;
	// push notification status
	int PUSH_NOTIFICATION_STATUS_LEN = 15;

	//make alias the same length as principal len;
	int PUSH_NOTIFICATION_ALIAS_LEN = ACCOUNT_PRINCIPAL_LEN;
}
