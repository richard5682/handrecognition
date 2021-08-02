package jgmath;

import java.text.DecimalFormat;

public class jgMath {
	public static final DecimalFormat decimalformat = new DecimalFormat("0.00000000");
	public static final float FLOAT_EPSILON = 0.000000000000000000000001f;
	public static final char
				jgPLUS = '+',
				jgMINUS = '-',
				jgDIVIDE = '/',
				jgMULTIPLY = '*',
				jgSINE = 's',
				jgCOSINE = 'c',
				jgNEGATIVE = 'n';
	public static float StringParser(String s,jgVariable[] var,float time,boolean usevalueset){
		int length = s.length();
		char[] c = new char[length];
		for(int i=0;i<length;i++){
			c[i] = s.charAt(i);
		}
		while(checkNegative(c)){
			c = replaceNegative(c);
		}
		//CHECK  ALL PARENTHESIS AND SOLVE
		while(checkParenthesis(c)){
			int[] index = getParenthesis(c);
			char[] csolveexpression;
			//System.out.print("NOW SOLVING : ");
			//System.out.println(c);
			csolveexpression = solveExpression(getExpression(c,index),var,time,usevalueset);
			//System.out.print("Answer : ");
			//System.out.println(csolveexpression);
			c = insertNumber(c,csolveexpression,index);
		}
		//System.out.print("NOW SOLVING : ");
		//System.out.println(c);
		c = solveExpression(c,var,time,usevalueset);
		c = getNegative(c);
		//System.out.print("Answer : ");
		//System.out.println(c);
		//RETURN FINAL RESULT
		return Float.parseFloat(new String(c));
	}
	public static float StringParser(String s,jgVariable[] var,float time){
		int length = s.length();
		char[] c = new char[length];
		for(int i=0;i<length;i++){
			c[i] = s.charAt(i);
		}
		while(checkNegative(c)){
			c = replaceNegative(c);
		}
		//CHECK  ALL PARENTHESIS AND SOLVE
		while(checkParenthesis(c)){
			int[] index = getParenthesis(c);
			char[] csolveexpression;
			//System.out.print("NOW SOLVING : ");
			//System.out.println(c);
			csolveexpression = solveExpression(getExpression(c,index),var,time,false);
			//System.out.print("Answer : ");
			//System.out.println(csolveexpression);
			c = insertNumber(c,csolveexpression,index);
		}
		//System.out.print("NOW SOLVING : ");
		//System.out.println(c);
		c = solveExpression(c,var,time,false);
		c = getNegative(c);
		//System.out.print("Answer : ");
		//System.out.println(c);
		//RETURN FINAL RESULT
		return Float.parseFloat(new String(c));
	}
	public static char[] replaceNegative(char[] c){
		int counter=0;
		int[] index;
		char[] cinsert = new char[1];
		cinsert[0] = 'n';
		char[] cbuffer = c;
		int[] ind = new int[2];
		for(int i=0;i<c.length;i++){
			if(c[i] == jgMINUS){
				if(i-1 >= 0 && i+1 <c.length){
					if(c[i-1] == '(' && c[i+1] == ')'){
						ind[0] = i-1;
						ind[1] = i+1;
						break;
					}
				}
			}
		}
		cbuffer = insertNumber(cbuffer,cinsert,ind);
		return cbuffer;
	}
	public static boolean checkNegative(char[] c){
		for(int i=0;i<c.length;i++){
			if(c[i] == jgMINUS){
				if(i-1 >= 0 && i+1 <c.length){
					if(c[i-1] == '(' && c[i+1] == ')'){
						return true;
					}
				}
			}
		}
		return false;
	}
	public static boolean checkParenthesis(char[] c){
		for(int i=c.length-1;i>=0;i--){
			if(c[i] == '('){
				return true;
			}
		}
		return false;
	}
	public static int[] getParenthesis(char[] c){
		int[] index = new int[2];
		for(int i=c.length-1;i>=0;i--){
			if(c[i] == '('){
				index[0] = i;
				break;
			}
		}
		for(int i=index[0];i<c.length;i++){
			if(c[i] == ')'){
				index[1] = i;
				break;
			}
		}
		return index;
	}
	public static char[] getExpression(char[] c,int[] index){
		char[] cbuffer = new char[index[1]-index[0]-1];
		int indexcounter=0;
		for(int i=index[0]+1;i<index[1];i++){
			cbuffer[indexcounter] = c[i];
			indexcounter++;
		}
		return cbuffer;
	}

	//EVALUATE SINE AND COSINE FUNCTION AND RECONSTRUCT char
	public static char[] solveExpression(char[] c,jgVariable[] var,float time,boolean usevalueset){
		char[] cbuffer = c;
		while(checkOperation(cbuffer,jgSINE)){
			cbuffer = solveSine(cbuffer,var,time,usevalueset);
		}
		while(checkOperation(cbuffer,jgCOSINE)){
			cbuffer = solveCosine(cbuffer,var,time,usevalueset);
		}
		while(checkOperation(cbuffer,jgMULTIPLY)){
			cbuffer = solveMultiplication(cbuffer,var,time,usevalueset);
		}
		while(checkOperation(cbuffer,jgDIVIDE)){
			cbuffer = solveDivision(cbuffer,var,time,usevalueset);
		}
		while(checkOperation(cbuffer,jgPLUS)){
			cbuffer = solveAddition(cbuffer,var,time,usevalueset);
		}
		while(checkOperation(cbuffer,jgMINUS)){
			cbuffer = solveSubtraction(cbuffer,var,time,usevalueset);
		}
		if(!isANumber(c[0])){
			cbuffer = getVarValue(c[0],var,time,usevalueset);
		}
		return cbuffer;
	}
	public static char[] solveMultiplication(char[] c,jgVariable[] var,float time,boolean usevalueset){
		for(int i=0;i<c.length;i++){
			if(c[i] == jgMULTIPLY){
				float[] number = getAdjacentNumber(c,i,var,time,usevalueset);
				float result = number[0]*number[1];
				char[] cresult = decimalformat.format(result).toCharArray();
				if(cresult[0] == '-'){
					cresult[0] = 'n';
				}
				int[] index = new int[2];
				index[0] = (int) number[2];
				index[1] = (int) number[3];
				return insertNumber(c,cresult,index);
			}
		}
		return null;
	}
	public static char[] solveDivision(char[] c,jgVariable[] var,float time,boolean usevalueset){
		for(int i=0;i<c.length;i++){
			if(c[i] == jgDIVIDE){
				float[] number = getAdjacentNumber(c,i,var,time,usevalueset);
				//System.out.println(number[0] + "  /  " + number[1]);
				float result = number[0]/number[1];
				char[] cresult = decimalformat.format(result).toCharArray();
				if(cresult[0] == '-'){
					cresult[0] = 'n';
				}
				int[] index = new int[2];
				index[0] = (int) number[2];
				index[1] = (int) number[3];
				return insertNumber(c,cresult,index);
			}
		}
		return null;
	}
	public static char[] solveAddition(char[] c,jgVariable[] var,float time,boolean usevalueset){
		for(int i=0;i<c.length;i++){
			if(c[i] == jgPLUS){
				float[] number = getAdjacentNumber(c,i,var,time,usevalueset);
				float result = number[0]+number[1];
				char[] cresult = decimalformat.format(result).toCharArray();
				if(cresult[0] == '-'){
					cresult[0] = 'n';
				}
				int[] index = new int[2];
				index[0] = (int) number[2];
				index[1] = (int) number[3];
				return insertNumber(c,cresult,index);
			}
		}
		return null;
	}
	public static char[] solveSubtraction(char[] c,jgVariable[] var,float time,boolean usevalueset){
		for(int i=0;i<c.length;i++){
			if(c[i] == jgMINUS){
				float[] number = getAdjacentNumber(c,i,var,time,usevalueset);
				float result = number[0]-number[1];
				char[] cresult = decimalformat.format(result).toCharArray();
				if(cresult[0] == '-'){
					cresult[0] = 'n';
				}
				int[] index = new int[2];
				index[0] = (int) number[2];
				index[1] = (int) number[3];
				return insertNumber(c,cresult,index);
			}
		}
		return null;
	}
	public static char[] solveSine(char[] c, jgVariable[] var,float time,boolean usevalueset){
		for(int i=0;i<c.length;i++){
			if(c[i] == jgSINE){
				float[] number = getRightNumber(c,i,var,time,usevalueset);
				float result = (float) Math.sin(Math.toRadians(number[0]));
				
				if(i-1 >= 0){
					if(c[i-1] == 'n'){
						result = result*-1;
						number[1]--;
					}
				}
				char[] cresult = decimalformat.format(result).toCharArray();
				if(cresult[0] == '-'){
					cresult[0] = 'n';
				}
				int[] index = new int[2];
				index[0] = (int) number[1];
				index[1] = (int) number[2];
				return insertNumber(c,cresult,index);
			}
		}
		return null;
	}
	public static char[] solveCosine(char[] c, jgVariable[] var,float time,boolean usevalueset){
		for(int i=0;i<c.length;i++){
			if(c[i] == jgCOSINE){
				float[] number = getRightNumber(c,i,var,time,usevalueset);
				float result = (float) Math.cos(Math.toRadians(number[0]));
				
				if(i-1 >= 0){
					if(c[i-1] == 'n'){
						result = result*-1;
						number[1]--;
					}
				}
				char[] cresult = decimalformat.format(result).toCharArray();
				if(cresult[0] == '-'){
					cresult[0] = 'n';
				}
				int[] index = new int[2];
				index[0] = (int) number[1];
				index[1] = (int) number[2];
				return insertNumber(c,cresult,index);
			}
		}
		return null;
	}
	public static char[] insertNumber(char[] c,char[] cinsert, int[] index){
		char[] cbuffer = new char[c.length-(index[1]-index[0]+1)+cinsert.length];
		int counter = 0;
		for(int i=0;i<index[0];i++){
			cbuffer[counter] = c[i];
			counter++;
		}
		for(int i=0;i<cinsert.length;i++){
			cbuffer[counter] = cinsert[i];
			counter++;
		}
		for(int i=index[1]+1;i<c.length;i++){
			cbuffer[counter] = c[i];
			counter++;
		}
		return cbuffer;
	}
	//NO BUG
	public static float[] getAdjacentNumber(char[] c,int index,jgVariable[] var,float time,boolean usevalueset){
		float[] number = new float[4];
		
		//FIRST NUMBER TO THE RIGHT
		int pointer1 = 1;
		int indexstart,indexend;
		char[] cbuffer1;
		if(isANumber(c[index+1])){
			while(isANumber(c[index+pointer1])){
				pointer1++;
				if(index+pointer1 >= c.length){
					break;
				}
			}
			cbuffer1 = new char[pointer1-1];
			int counter1 = 0;
			for(int i=index+1;i<index+pointer1;i++){
				cbuffer1[counter1] = c[i];
				counter1++;
			}
			indexend = index+pointer1-1;
		}else{
			indexend = index+1;
			//System.out.println(time);
			cbuffer1 = getVarValue(c[index+pointer1],var,time,usevalueset);
		}
		//SECOND NUMBER FROM THE LEFT
		
		int pointer2 = -1;
		char[] cbuffer2;
		if(isANumber(c[index-1])){
			while(isANumber(c[index+pointer2])){
				pointer2--;
				if(index+pointer2 < 0){
					break;
				}
			}
			cbuffer2 = new char[(pointer2*-1)-1];
			int counter2 = 0;
			indexstart = index+pointer2+1;
			for(int i=index+pointer2+1;i<index;i++){
				cbuffer2[counter2] = c[i];
				counter2++;
			}
		}else{
			indexstart = index+pointer2;
			cbuffer2 = getVarValue(c[index+pointer2],var,time,usevalueset);
		}
		cbuffer1 = getNegative(cbuffer1);
		cbuffer2 = getNegative(cbuffer2);
		number[0] = Float.parseFloat(new String(cbuffer2));
		number[1] = Float.parseFloat(new String(cbuffer1));
		
		number[2] = indexstart;
		number[3] = indexend;
		return number;
	}
	public static float[] getRightNumber(char[] c,int index,jgVariable[] var,float time,boolean usevalueset){
		float[] number = new float[3];
		
		//FIRST NUMBER TO THE RIGHT
		int pointer1 = 1;
		int indexend;
		char[] cbuffer1;
		if(isANumber(c[index+1])){
			while(isANumber(c[index+pointer1])){
				pointer1++;
				if(index+pointer1 >= c.length){
					break;
				}
			}
			cbuffer1 = new char[pointer1-1];
			int counter1 = 0;
			for(int i=index+1;i<index+pointer1;i++){
				cbuffer1[counter1] = c[i];
				counter1++;
			}
			indexend = index+pointer1-1;
		}else{
			indexend = index+1;
			cbuffer1 = getVarValue(c[index+pointer1],var,time,usevalueset);
		}
		cbuffer1 = getNegative(cbuffer1);
		number[0] = Float.parseFloat(new String(cbuffer1));
		number[1] = index;
		number[2] = indexend;
		return number;
	}
	public static char[] getVarValue(char c,jgVariable[] var,float time,boolean usevalset){
		for(int i=0;i<var.length;i++){
			if(var[i].name == c){
				if(usevalset){
					//return var[i].getValueChar(var[i].valuetset);
				}else{
					return var[i].getValueChar(time);
				}
			}
		}
		System.out.println("VARIABLE ERROR NO VARIABLE "+c+" DEFINE ");
		return null;
	}
	public static char[] getNegative(char[] c){
		if(c[0] == 'n'){
			c[0] = '-';
			return c;
		}else{
			return c;
		}
	}
	public static boolean isANumber(char c){
		if(
				c!='0' &&
				c!='1' &&
				c!='2' &&
				c!='3' &&
				c!='4' &&
				c!='5' &&
				c!='6' &&
				c!='7' &&
				c!='8' &&
				c!='9' &&
				c!='.' &&
				c!='n' &&
				c!='c' &&
				c!='s')
		{
			return false;
		}else{
			return true;
		}
	}
	public static boolean checkOperation(char[] c, char operation){
		for(int i=0;i<c.length;i++){
			if(c[i] == operation){
				return true;
			}
		}
		return false;
	}

	/////////////////////MATH
	public static Vector2[] GetBoundingBox(Vector2[] v){
		float maxX = v[0].x;
		float minX = v[0].x;
		float maxY = v[0].y;
		float minY = v[0].y;
		for(int i=1;i<v.length;i++){
			if(v[i].x > maxX){
				maxX = v[i].x;
			}else if(v[i].x <minX){
				minX = v[i].x;
			}
			if(v[i].y > maxY){
				maxY = v[i].y;
			}else if(v[i].y <minY){
				minY = v[i].y;
			}
		}
		Vector2 min = new Vector2(minX,minY);
		Vector2 max = new Vector2(maxX,maxY);
		Vector2[] returnvec = {min,max};
		return returnvec;
	}
	public static Vector2[] GetBoundingBox(Vector3[] v){
		float maxX = v[0].x;
		float minX = v[0].x;
		float maxY = v[0].y;
		float minY = v[0].y;
		for(int i=1;i<v.length;i++){
			if(v[i].x > maxX){
				maxX = v[i].x;
			}else if(v[i].x <minX){
				minX = v[i].x;
			}
			if(v[i].y > maxY){
				maxY = v[i].y;
			}else if(v[i].y <minY){
				minY = v[i].y;
			}
		}
		Vector2 min = new Vector2(minX,minY);
		Vector2 max = new Vector2(maxX,maxY);
		Vector2[] returnvec = {min,max};
		return returnvec;
	}
}
