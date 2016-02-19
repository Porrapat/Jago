package jagoclient.gmp;

public interface GMPInterface
{	public int getHandicap ();
	public int getColor ();
	public int getRules ();
	public int getBoardSize ();
	public void gotMove (int color, int pos);
	public void gotOk ();
	public void gotAnswer (int a);
}
