package example.rne.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSystemLogger implements SystemLogger {
	private final Logger log = LoggerFactory.getLogger(DefaultSystemLogger.class);
	
	@Override
	public void log(String message) {
		log.info(message);
	}
}
