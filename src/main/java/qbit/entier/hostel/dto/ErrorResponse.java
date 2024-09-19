package qbit.entier.hostel.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class ErrorResponse {
	@Getter
	@Setter
    private long timestamp;
	@Getter
	@Setter
    private int status;
	@Getter
	@Setter
    private String error;
	@Getter
	@Setter
    private String message;
	@Getter
	@Setter
    private String path;

	
	public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = Instant.now().toEpochMilli();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
