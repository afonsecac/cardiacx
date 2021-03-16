/**
 * 
 */
package cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor;

/**
 * Provides a {@link #cleanup()} method, that can be run by the
 * {@link Converter} reading thread to perform clean ups (eg. close Hibernate
 * Session) at its end.
 * 
 * @author stengel
 * @date 10.11.2010
 * 
 */
public interface ConverterThreadCleanupCallback
{
	/**
	 * Clean up.
	 */
	public abstract void cleanup();

}
