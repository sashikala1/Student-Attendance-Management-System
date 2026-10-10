package com.sams.util;

/*Single unchecked exception used by all layers; its message is safe to show to
 * the user.
 */
public class AppException extends RuntimeException {

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
