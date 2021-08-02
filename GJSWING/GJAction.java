package GJSWING;

public interface GJAction {
	public void Clicked(int x,int y);
	public void Pressed();
	public void Release();
	public void MouseMove(int x,int y);
	public void MouseEntered();
	public void MouseExit();
}
