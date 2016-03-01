package pedidos;

import java.util.ArrayList;

public class Pedidos_parent {
	private String id;
	private String mTitle;
	private ArrayList<Pedidos_child> mArrayChildren;

	 
	//Setters
	public void setId(String Id) {
		this.id = Id;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	 
	public void setArrayChildren(ArrayList<Pedidos_child> mArrayChildren) {
		this.mArrayChildren = mArrayChildren;
	}

	
	//Getters
	public String getId() {
		return id;
	}
	 
	public String getTitle() {
		return mTitle;
	}

	public ArrayList<Pedidos_child> getArrayChildren() {
		return mArrayChildren;
	}
}
