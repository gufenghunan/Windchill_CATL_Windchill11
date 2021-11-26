package com.catl.line.transfer;
public class FileConvertException extends Exception {
	private static final long serialVersionUID = 1L;
	public FileConvertException(String message) {
        super(message);
    }

    public FileConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
