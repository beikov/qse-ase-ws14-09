package at.ac.tuwien.ase09.exception;

public class EntityNotFoundException extends AppException {
	private static final long serialVersionUID = 1L;

	public EntityNotFoundException() {
        this(null, null);
    }

    public EntityNotFoundException(String message) {
        this(message, null);
    }

    public EntityNotFoundException(Throwable cause) {
        this(null, cause);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EntityNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
