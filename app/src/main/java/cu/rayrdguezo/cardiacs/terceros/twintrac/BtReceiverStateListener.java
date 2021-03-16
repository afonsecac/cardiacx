package cu.rayrdguezo.cardiacs.terceros.twintrac;

/**
 * This listener should be used to capture error messages or behavior chages.
 * 
 * @author Hakan Sahin
 * @date 21.02.2017 15:14:15 Hakan Sahin
 */
public interface BtReceiverStateListener
{
	public void btReceiverFinishedWithException();


	public void stopFillingAutomaticValues();
}
