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
	public static final Code UNSUPPORTED_FILE_TYPE = Code.invalidArgument(11_000_0007);

	// account
	public static final Code ACCOUNT_UNAUTHORIZED_PERMISSION = Code.notAuthorized(11_001_0001);
	public static final Code ACCOUNT_CREATION_FAILURE = Code.notAuthorized(11_001_0002);
	public static final Code ACCOUNT_INVALID_VERIFICATION_CODE = Code.invalidArgument(11_001_0003);
	public static final Code ACCOUNT_INVALID_USERNAME = Code.invalidArgument(11_001_0004);
	public static final Code ACCOUNT_PASSWORD_MISMATCH = Code.invalidArgument(11_001_0005);
	public static final Code ACCOUNT_NOT_FOUND = Code.notFound(11_001_0006);
	public static final Code ACCOUNT_ALREADY_EXISTS = Code.alreadyExists(11_001_0007);
	public static final Code ACCOUNT_USERNAME_ALREADY_EXISTS = Code.alreadyExists(11_001_0008);
	public static final Code ACCOUNT_MOBILE_ALREADY_EXISTS = Code.alreadyExists(11_001_0009);
	public static final Code ACCOUNT_MOBILE_NOT_FOUND = Code.notFound(11_001_0010);
	public static final Code ACCOUNT_EMAIL_ALREADY_EXISTS = Code.alreadyExists(11_001_0011);
	public static final Code ACCOUNT_EMAIL_NOT_BIND = Code.notAuthorized(11_001_0012);
	public static final Code ACCOUNT_ADDRESS_NOT_ALLOWED = Code.illegalAccess(11_001_0013);

	// apikey
	public static final Code APIKEY_NOT_FOUND = Code.notFound(11_002_0001);
	public static final Code APIKEY_ILLEGAL_ACCESS = Code.illegalAccess(11_002_0002);

	// AIR JET
	public static final Code PRODUCT_NOT_FOUND = Code.notFound(11_003_0001);
	public static final Code ORDER_NOT_FOUND = Code.notFound(11_003_0002);
	public static final Code AIRJET_ALREADY_EXISTS = Code.alreadyExists(11_003_0004);
	public static final Code AIRJET_NOT_FOUND = Code.notFound(11_003_0005);
	public static final Code AIRCRAFT_ALREADY_EXISTS = Code.alreadyExists(11_003_0006);
	public static final Code AIRCRAFT_NOT_FOUND = Code.notFound(11_003_0007);

	// fleet
	public static final Code FLEET_NOT_FOUND = Code.notFound(11_004_0001);
	public static final Code FLEET_ALREADY_EXISTS = Code.alreadyExists(11_004_0002);
	public static final Code CHARTERORDER_NOT_FOUND = Code.notFound(11_004_0003);

	// ferry flight
	public static final Code FERRYFLIGHTI_NOT_FOUND = Code.notFound(11_005_0001);
	public static final Code FERRYFLIGHTI_ALREADY_EXISTS = Code.alreadyExists(11_005_0002);
	public static final Code FERRYFLIGHT_ORDER_NOT_FOUND = Code.notFound(11_005_0003);

	// jet card
	public static final Code JETCARD_NOT_FOUND = Code.notFound(11_006_0001);
	public static final Code JETCARD_ORDER_NOT_FOUND = Code.notFound(11_006_0002);

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
	public static final Code COURSE_NOT_FOUND = Code.notFound(11_011_0001);
	public static final Code ENROLLMENT_NOT_FOUND = Code.notFound(12_010_0001);

	// comment
	public static final Code COMMENT_NOT_FOUND = Code.notFound(11_013_0001);
	public static final Code COMMENT_NOT_ALLOWED = Code.notAuthorized(11_013_0002);

	private Codes() {
		throw new AssertionError();
	}
}