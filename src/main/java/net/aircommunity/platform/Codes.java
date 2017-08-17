package net.aircommunity.platform;

/**
 * Error Codes of AIR Platform.
 *
 * @author Bin.Zhang
 */
public final class Codes {
	// error code pattern: xxx_xx_xxx_xxx -> (HTTP status code) - (product) (component) (details)

	// generic
	public static final Code SUCCESS = Code.success(11_000_0001);
	public static final Code INTERNAL_ERROR = Code.internalError(11_000_0002);
	public static final Code SERVICE_UNAVAILABLE = Code.serviceUnavailable(11_000_0003);
	public static final Code REQUEST_TIMEOUT = Code.gatewayTimeout(11_000_0004);

	// json processing
	public static final Code READ_JSON_FAILURE = Code.invalidArgument(11_000_0005);
	public static final Code WRITE_JSON_FAILURE = Code.invalidArgument(11_000_0006);

	// file upload
	public static final Code INVALID_FORM_INPUT = Code.invalidArgument(11_000_0007);
	public static final Code UNSUPPORTED_FILE_TYPE = Code.invalidArgument(11_000_0008);
	public static final Code TOO_MANY_VERIFICATION_REQUEST = Code.rateLimiting(11_000_0009);

	// account
	public static final Code ACCOUNT_UNAUTHORIZED = Code.notAuthorized(11_001_0001);
	public static final Code ACCOUNT_PERMISSION_DENIED = Code.notPerimitted(11_001_0001);
	public static final Code ACCOUNT_CREATION_FAILURE = Code.invalidArgument(11_001_0002);
	public static final Code ACCOUNT_NOT_TENANT = Code.invalidArgument(11_001_0002);
	public static final Code ACCOUNT_INVALID_VERIFICATION_CODE = Code.invalidArgument(11_001_0003);
	public static final Code ACCOUNT_INVALID_USERNAME = Code.invalidArgument(11_001_0004);
	public static final Code ACCOUNT_INVALID_ROLE = Code.invalidArgument(11_001_0005);
	public static final Code ACCOUNT_INVALID_IDCARD = Code.invalidArgument(11_001_0006);
	public static final Code ACCOUNT_PASSWORD_MISMATCH = Code.invalidArgument(11_001_0007);
	public static final Code ACCOUNT_NOT_FOUND = Code.notFound(11_001_0008);
	public static final Code ACCOUNT_ALREADY_EXISTS = Code.alreadyExists(11_001_0009);
	public static final Code ACCOUNT_TYPE_MISMATCH = Code.invalidArgument(11_001_0010);
	public static final Code ACCOUNT_USERNAME_ALREADY_EXISTS = Code.alreadyExists(11_001_0011);
	public static final Code ACCOUNT_MOBILE_ALREADY_EXISTS = Code.alreadyExists(11_001_0012);
	public static final Code ACCOUNT_MOBILE_NOT_FOUND = Code.notFound(11_001_0013);
	public static final Code ACCOUNT_EMAIL_ALREADY_EXISTS = Code.alreadyExists(11_001_0014);
	public static final Code ACCOUNT_EMAIL_NOT_BIND = Code.illegalAccess(11_001_0015);
	public static final Code ACCOUNT_EMAIL_ALREADY_VERIFIED = Code.illegalAccess(11_001_0016);
	public static final Code ACCOUNT_ADDRESS_NOT_ALLOWED = Code.illegalAccess(11_001_0017);
	public static final Code ACCOUNT_PASSENGER_NOT_ALLOWED = Code.illegalAccess(11_001_0018);
	public static final Code ACCOUNT_PASSENGER_ALREADY_EXISTS = Code.alreadyExists(11_001_0019);
	public static final Code ACCOUNT_ADD_PASSENGER_FAILURE = Code.invalidArgument(11_001_0020);

	// apikey
	public static final Code APIKEY_NOT_FOUND = Code.notFound(11_002_0001);
	public static final Code APIKEY_ILLEGAL_ACCESS = Code.illegalAccess(11_002_0002);

	// product
	public static final Code PRODUCT_NOT_FOUND = Code.notFound(11_003_0001);
	public static final Code PRODUCT_NOT_PUBLISHED = Code.illegalState(11_003_0002);
	public static final Code PRODUCT_PROMOTION_NOT_FOUND = Code.notFound(11_003_0003);
	public static final Code PRODUCT_FAQ_NOT_FOUND = Code.notFound(11_003_0004);
	public static final Code PRODUCT_FAMILY_NOT_FOUND = Code.notFound(11_003_0005);
	public static final Code PRODUCT_FAMILY_NOT_APPROVED = Code.notFound(11_003_0006);
	public static final Code PRODUCT_INVALID_DEPARTURE_DATE = Code.notFound(11_003_0007);
	public static final Code PRODUCT_CANNOT_BE_DELETED = Code.illegalState(11_003_0008);
	public static final Code PRODUCT_CANNOT_BE_UPDATED = Code.illegalState(11_003_0009);
	public static final Code PRODUCT_FAMILY_CANNOT_BE_DELETED = Code.illegalState(11_003_0010);

	// order
	public static final Code ORDER_ACCESS_DENIED = Code.notPerimitted(11_003_1001);
	public static final Code ORDER_NOT_PAYABLE = Code.illegalState(11_003_1002);
	public static final Code ORDER_NOT_FOUND = Code.notFound(11_003_1003);
	public static final Code ORDER_ILLEGAL_STATUS = Code.illegalAccess(11_003_1004);
	public static final Code ORDER_INSTALMENT_NOT_FOUND = Code.illegalAccess(11_003_1005);
	public static final Code ORDER_REFUND_FAILURE = Code.illegalState(11_003_1006);
	public static final Code ORDER_INVALID_TOTAL_AMOUNT = Code.invalidArgument(11_003_1007);
	public static final Code ORDER_CANNOT_BE_DELETED = Code.illegalState(11_003_1008);
	public static final Code ORDER_ITEM_CANDIDATE_NOT_FOUND = Code.notFound(11_003_1009);
	public static final Code ORDER_CANNOT_UPDATE_PRICE = Code.invalidArgument(11_003_1010);

	// common product related
	public static final Code AIRJET_ALREADY_EXISTS = Code.alreadyExists(11_003_2001);
	public static final Code AIRJET_NOT_FOUND = Code.notFound(11_003_2002);
	public static final Code APRON_NOT_FOUND = Code.notFound(11_003_2003);
	public static final Code BANNER_NOT_FOUND = Code.notFound(11_003_2004);
	public static final Code BANNER_INVALID_LINK_CATEGORY = Code.invalidArgument(11_003_2005);

	public static final Code AIRCRAFT_ALREADY_EXISTS = Code.alreadyExists(11_003_2005);
	public static final Code AIRCRAFT_CANNOT_BE_DELETED = Code.illegalState(11_003_2005);
	public static final Code AIRCRAFT_NOT_FOUND = Code.notFound(11_003_2006);
	public static final Code SALESPACKAGE_NOT_FOUND = Code.notFound(11_003_2007);
	public static final Code PASSENGER_NOT_FOUND = Code.notFound(11_003_2008);
	public static final Code PASSENGER_REQUIRED = Code.invalidArgument(11_003_2009);
	public static final Code AIRPORT_NOT_FOUND = Code.notFound(11_003_2010);
	public static final Code AIRPORT_ALREADY_EXISTS = Code.notFound(11_003_2011);
	public static final Code AIRPORT_INVALID_CODE = Code.invalidArgument(11_003_2012);
	public static final Code CITYSITE_NOT_FOUND = Code.notFound(11_003_2013);


	// fleet
	public static final Code FLEET_NOT_FOUND = Code.notFound(11_004_0001);
	public static final Code FLEET_ALREADY_EXISTS = Code.alreadyExists(11_004_0002);
	public static final Code CHARTER_ORDER_NOT_FOUND = Code.notFound(11_004_0003);

	// ferry flight
	public static final Code FERRYFLIGHT_NOT_FOUND = Code.notFound(11_005_0001);
	public static final Code FERRYFLIGHT_ORDER_NOT_FOUND = Code.notFound(11_005_0003);

	// jet travel
	public static final Code JETTRAVEL_NOT_FOUND = Code.notFound(11_006_0001);
	public static final Code JETTRAVEL_ORDER_NOT_FOUND = Code.notFound(11_006_0002);

	// transport
	public static final Code AIRTRANSPORT_NOT_FOUND = Code.notFound(11_007_0001);
	public static final Code AIRTRANSPORT_ORDER_NOT_FOUND = Code.notFound(11_007_0002);

	// tour
	public static final Code TOUR_NOT_FOUND = Code.notFound(11_008_0001);
	public static final Code TOUR_ORDER_NOT_FOUND = Code.notFound(11_008_002);

	// taxi
	public static final Code TAXI_NOT_FOUND = Code.notFound(11_009_0001);
	public static final Code TAXI_ORDER_NOT_FOUND = Code.notFound(11_009_0002);

	// school/course/enrollment
	public static final Code SCHOOL_NOT_FOUND = Code.notFound(11_010_0001);
	public static final Code SCHOOL_CANNOT_BE_DELETED = Code.illegalState(11_010_0002);
	public static final Code COURSE_NOT_FOUND = Code.notFound(11_011_0001);
	public static final Code COURSEORDER_NOT_FOUND = Code.notFound(11_011_0002);

	// quick flight
	public static final Code QUICKFLIGHT_ORDER_NOT_FOUND = Code.notFound(11_012_0001);

	// comment
	public static final Code COMMENT_NOT_FOUND = Code.notFound(11_013_0001);
	public static final Code COMMENT_NOT_ALLOWED = Code.notPerimitted(11_013_0002);
	public static final Code COMMENT_INVALID_DATA = Code.invalidArgument(11_013_0003);

	// air class
	public static final Code AIRCLASS_NOT_FOUND = Code.notFound(11_014_0001);


	//push noitification
	public static final Code PUSHNOTIFICATION_NOT_FOUND = Code.notFound(11_015_0001);
	public static final Code PUSHNOTIFICATION_CANNOT_BE_DELETED = Code.illegalState(11_015_0002);

	private Codes() {
		throw new AssertionError();
	}
}