package me.kiporenko.system.exception;

public class EntityNotFoundException extends RuntimeException {

	public static final String WITH_ID = " with ID ";
	public static final String NOT_FOUND = " not found";

	public EntityNotFoundException(String entityType, Long id) {
		super(entityType + WITH_ID + id + NOT_FOUND);
	}
}