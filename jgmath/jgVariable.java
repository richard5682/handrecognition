package jgmath;


public class jgVariable {
	public float value;
	public char name;
	public jgVariable(float value,char name){
		this.value = value;
		this.name = name;
	}
	public void updateValue(){
	}
	public void setValue(float value){
		this.value = value;
	}
	public char[] getValueChar(float f){
		return Float.toString(value).toCharArray();
	}
}
