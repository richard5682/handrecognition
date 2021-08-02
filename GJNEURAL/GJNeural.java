package GJNEURAL;

import java.util.Random;

import javax.xml.crypto.NodeSetData;

import GJMATH.GJMATHUTIL;

public class GJNeural {
	public static Node[] SplitNodes(Node[] nodes,int startingpos,int size){
		Node[] buffer = new Node[size];
		for(int i=startingpos,v=0;i<size+startingpos;i++,v++){
			buffer[v] = nodes[i];
		}
		return buffer;
	}
	public static Node[] JoinNodes(Node[][] nodes,int size){
		Node[] buffer = new Node[size];
		int c=0;
		for(int i=0;i<nodes.length;i++){
			for(int v=0;v<nodes[0].length;v++){
				buffer[c] = nodes[i][v];
				c++;
			}
		}
		return buffer;
	}
	public static class Organism{
		public NeuralNetwork neuralnetwork;
		public Organism(int no_input,int[] no_node,int no_layer,int no_output,float learningrate){
			Input_Layer input_layer;
			Layer[] layers = new Layer[no_layer];
			Layer output_layer;
			input_layer = new Input_Layer(no_input);
			for(int i=0;i<no_layer;i++){
				layers[i] = new Layer(no_node[i],i);
			}
			
			output_layer = new Layer(no_output,no_layer);
			this.neuralnetwork = new NeuralNetwork(input_layer,layers,output_layer,learningrate,true);
			this.neuralnetwork.Initialize_Network();
		}
		public Organism(String data){
			String[] parse1 = data.split(":::");
			String[] no_nodes_perlayer = parse1[0].split(",");
			String[] layer_data = new String[parse1.length-1];
			Input_Layer input_layer = new Input_Layer(Integer.parseInt(no_nodes_perlayer[0]));
			Layer[] layers = new Layer[layer_data.length-1];
			Layer output_layer;
			for(int i=1;i<parse1.length;i++){
				layer_data[i-1] = parse1[i];
			}
			for(int i=0;i<layer_data.length-1;i++){
				layers[i] = new Layer(layer_data[i],i);
			}
			output_layer = new Layer(layer_data[layer_data.length-1],layer_data.length-1);
			this.neuralnetwork = new NeuralNetwork(input_layer,layers,output_layer,0.01f,false);
		}
		public void ComputeOutput(float[] values){
			this.neuralnetwork.ComputeOutput(values);
		}
		public void PrintOutput(){
			this.neuralnetwork.output_layer.printValues();
		}
		public void Train(float[][] values,float [][] desired){
			this.neuralnetwork.BackPropagate(values, desired);
		}
		public void AddRandomness(float factor){
			this.neuralnetwork.AddRandomness(factor);
		}
		public void ScaleLearningRate(float factor){
			this.neuralnetwork.learningrate = this.neuralnetwork.learningrate*factor;
		}
		public String GetData(){
			return this.neuralnetwork.GetData();
		}
	}
	
	public static class NeuralNetwork{
		Input_Layer input_layer;
		Layer[] layers;
		public Layer output_layer;
		public float past_fitness = 10;
		public float fitness = 10;
		public float learningrate;
		float time=10;
		public NeuralNetwork(Input_Layer input_layer,Layer[] layers,Layer output_layer,float learningrate,boolean initialize){
			this.layers = layers;
			this.input_layer = input_layer;
			this.output_layer = output_layer;
			this.learningrate = learningrate;
			this.input_layer.network = this;
			this.output_layer.network = this;
			for(int i=0;i<this.layers.length;i++){
				this.layers[i].network = this;
			}
			if(initialize){
				this.Initialize_Network();
			}
			
		}
		public void Initialize_Network(){
			for(int i=0;i<layers.length;i++){
				if(i==0){
					layers[0].Generate_Node(input_layer.no_nodes);
				}else{
					layers[i].Generate_Node(layers[i-1].no_nodes);
				}
			}
			output_layer.Generate_Node(layers[layers.length-1].no_nodes);
		}
		public void AddRandomness(float factor){
			float fitnessfactor = (float) (Math.pow(fitness,4)*factor);
			for(int i=0;i<layers.length;i++){
				layers[i].AddRandomness(fitnessfactor);
			}
			output_layer.AddRandomness(fitnessfactor);
		}
		public Layer Get_Preceding_Layer(int index){
			if(index == 0){
				return this.input_layer;
			}else if(index > 0){
				return this.layers[index-1];
			}
			return null;
		}
		public Layer Get_Next_Layer(int index){
			if(index == layers.length-1){
				return this.output_layer;
			}else if(index < layers.length-1){
				return this.layers[index+1];
			}
			return null;
		}
		public void ComputeOutput(float[] values){
			if(values.length == input_layer.nodes.length){
				for(int i=0;i<values.length;i++){
					input_layer.nodes[i].value = values[i];
				}
			}
			for(int i=0;i<this.layers.length;i++){
				this.layers[i].ComputeLayer();
			}
			this.output_layer.ComputeLayer();
//			this.output_layer.SoftMaxNodes();
		}
		public void BackPropagate(float[][] values,float [][] desired){
			if(values.length == desired.length){
				float acc_fitness_overall = 0;
				for(int i=0;i<values.length;i++){
					float acc_fitness = 0;
					ComputeOutput(values[i]);
					for(int v=0;v<output_layer.nodes.length;v++){
						acc_fitness += (float) Math.pow(output_layer.nodes[v].value-desired[i][v], 2);
					}
					acc_fitness_overall += acc_fitness;
					output_layer.GetLayerAdjustment(desired[i]);
					for(int v=layers.length-1;v>=0;v--){
						layers[v].GetLayerAdjustment();
					}
					output_layer.AddAdjustment();
					for(int v=layers.length-1;v>=0;v--){
						layers[v].AddAdjustment();
					}
				}
				past_fitness = fitness;
				fitness = acc_fitness_overall/values.length;
				output_layer.ApplyChanges(learningrate);
				for(int v=layers.length-1;v>=0;v--){
					layers[v].ApplyChanges(learningrate);
				}
			}
		}
		public void ReduceLearningRate(){
//			time += 0.01+(1/(fitness*10000000));
//			learningrate = (float) Math.exp(-time)*10;
		}
		public String GetData(){
			String buffer = ""+input_layer.no_nodes+',';
			for(int i=0;i<layers.length;i++){
				buffer += layers[i].no_nodes+",";
			}
			buffer += output_layer.no_nodes+":::";
			for(int i=0;i<layers.length;i++){
				buffer += GetLayerData(layers[i]);
				buffer += ":::";
			}
			buffer += GetLayerData(output_layer);
			return buffer;
		}
	}
	public static String GetLayerData(Layer l){
		String buffer = "";
		for(int i=0;i<l.nodes.length;i++){
			buffer += l.nodes[i].bias;
			if(i!=l.nodes.length-1){
				buffer += ",";
			}
		}
		buffer += "::";
		for(int i=0;i<l.nodes.length;i++){
			for(int v=0;v<l.nodes[i].weights.length;v++){
				buffer += l.nodes[i].weights[v];
				if(v!=l.nodes[i].weights.length-1){
					buffer += ",";
				}
			}
			if(i!=l.nodes.length-1){
				buffer += ":";
			}
		}
		return buffer;
	}
	public static class Input_Layer extends Layer{
		public Input_Layer(int no_nodes) {
			super(no_nodes,-1);
			this.nodes = new Node[no_nodes];
			for(int i=0;i<this.nodes.length;i++){
				this.nodes[i] = new Node(0,i,this);
			}
		}
	}
	public static class NodeThread implements Runnable{
		Node[] nodes;
		public NodeThread(Node[] nodes){
			this.nodes = nodes;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			for(int i=0;i<nodes.length;i++){
				this.nodes[i].GetNodeAdjustment();
				this.nodes[i].GetWeightAdjustment();
			}
		}
	}
	public static class Layer{
		public Node[] nodes;
		int no_nodes;
		int index;
		NeuralNetwork network;
		int remainder;
		int size0;
		int size1;
		int size2;
		int size3;
		public Layer(int no_nodes,int index){
			this.no_nodes = no_nodes;
			this.index = index;
			remainder = no_nodes%4;
			size0 = (no_nodes-remainder)/4;
			size1 = size0;
			size2 = size0;
			size3 = size0;
			if(remainder == 3){
				size1++;
				size2++;
				size3++;
			}else if (remainder == 2){
				size2++;
				size3++;
			}else if (remainder == 1){
				size3++;
			}
		}
		public Layer(String layer_data,int index){
			String[] parse1 = layer_data.split("::");
			String[] biases = parse1[0].split(",");
			String[] node_weights = parse1[1].split(":");
			this.index = index;
			this.no_nodes = biases.length;
			nodes = new Node[this.no_nodes];
			for(int i=0;i<no_nodes;i++){
				nodes[i] = new Node(this,Float.parseFloat(biases[i]),i,node_weights[i]);
			}
		}
		public void SoftMaxNodes(){
			float[] values = new float[nodes.length];
			for(int i=0;i<nodes.length;i++){
				values[i] = nodes[i].value;
			}
			values = GJMATHUTIL.SoftMax(values);
			for(int i=0;i<nodes.length;i++){
				nodes[i].value = values[i];
			}
		}
		public void printValues(){
			System.out.println("LAYER INDEX : "+index+" VALUES");
			for(int i=0;i<nodes.length;i++){
				System.out.print("NODE INDEX "+i+" VALUE : ");
				System.out.println(nodes[i].value);
			}
		}
		public void Generate_Node(int no_preceding_nodes){
			nodes = new Node[no_nodes];
			for(int i=0;i<no_nodes;i++){
				nodes[i] = new Node(no_preceding_nodes,i,this);
			}
		}
		private void GetLayerAdjustment(float[] desired){
			for(int i=0;i<nodes.length;i++){
				nodes[i].adj_node = -2*(nodes[i].value-desired[i]);
				nodes[i].GetWeightAdjustment();
			}
		}
		private void GetLayerAdjustment(){
			if(nodes.length >= 4 && false){
				Node[] node0 = SplitNodes(nodes, 0, nodes.length/2);
				Node[] node1 = SplitNodes(nodes,nodes.length/2, nodes.length/2);
//				Node[] node0 = SplitNodes(nodes, 0, size0);
//				Node[] node1 = SplitNodes(nodes, size0, size1);
//				Node[] node2 = SplitNodes(nodes, size0+size1, size2);
//				Node[] node3 = SplitNodes(nodes, size0+size1+size2, size3);
				Thread t0 = new Thread(new NodeThread(node0),"Core 0");
				Thread t1 = new Thread(new NodeThread(node1),"Core 1");
//				Thread t2 = new Thread(new NodeThread(node2),"Core 2");
//				Thread t3 = new Thread(new NodeThread(node3),"Core 3");
				t0.start();
				t1.start();
//				t2.start();
//				t3.start();
				try {
					t0.join();
					t1.join();
//					t2.join();
//					t3.join();
					Node[][] newnode = {node0,node1};
					this.nodes = JoinNodes(newnode, nodes.length);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else{
				for(int i=0;i<nodes.length;i++){
					nodes[i].GetNodeAdjustment();
					nodes[i].GetWeightAdjustment();
				}
			}
			
		}
		private void AddAdjustment(){
			for(int i=0;i<nodes.length;i++){
				nodes[i].AddAdjustment();
			}
		}
		private void ApplyChanges(float learningrate){
			for(int i=0;i<nodes.length;i++){
				nodes[i].ApplyChanges(learningrate);
				nodes[i].ResetData();;
			}
		}
		
		private Layer Get_Preceding_Layer(){
			return this.network.Get_Preceding_Layer(this.index);
		}
		private Layer Get_Next_Layer(){
			return this.network.Get_Next_Layer(this.index);
		}
		private void ComputeLayer(){
			for(int i=0;i<nodes.length;i++){
				nodes[i].ComputeNodes();
			}
		}
		private void AddRandomness(float factor){
			Random r = new Random();
			for(int i=0;i<nodes.length;i++){
				nodes[i].bias += factor*(r.nextFloat()-.5f);
				for(int v=0;v<nodes[i].weights.length;v++){
					nodes[i].weights[v] = factor*(r.nextFloat()-.5f);
				}
			}
		}
	}
	public static class Node{
		Layer layer;
		public float value=0;
		public float[] weights;
		float bias = 0;
		float adj_node;
		float[] adj_weight;
		float[] acc_adj_weight;
		float[] past_adj_weight;
		float past_adj_bias;
		float acc_adj_bias;
		float past_learningrate;
		int acc_data=0;
		int index;
		public Node(int no_preceding_nodes,int index,Layer layer){
			this.layer = layer;
			this.index = index;
			this.weights = new float[no_preceding_nodes];
			this.past_adj_weight = new float[no_preceding_nodes];
			acc_adj_weight = new float[weights.length];
			Initialize_Node();
		}
		public Node(Layer layer,float bias,int index,String data){
			this.layer = layer;
			this.index = index;
			this.bias = bias;
			
			String[] weights = data.split(",");
			float[] weightsfloat = new float[weights.length];
			acc_adj_weight = new float[weights.length];
			this.past_adj_weight = new float[weights.length];
			for(int i=0;i<weights.length;i++){
				weightsfloat[i] = Float.parseFloat(weights[i]);
			}
			this.weights = weightsfloat;
		}
		public void GetNodeAdjustment(){
			Node[] next_nodes = layer.Get_Next_Layer().nodes;
			adj_node = 0;
			
			for(int i=0;i<next_nodes.length;i++){
				adj_node += next_nodes[i].adj_node*next_nodes[i].weights[index];
			}
			adj_node = adj_node/next_nodes.length;
		}
		public void GetWeightAdjustment(){
			Node[] preceding_nodes = layer.Get_Preceding_Layer().nodes;
			adj_weight = new float[weights.length];
			for(int i=0;i<weights.length;i++){
				adj_weight[i] = preceding_nodes[i].value * this.adj_node;
			}
		}
		public void Initialize_Node(){
			Random r = new Random();
			this.bias = ((float)r.nextInt(20)-10)/10;
			for(int i=0;i<weights.length;i++){
				this.weights[i] = (1000-(float)r.nextInt(2000))/1000;
			}
			
		}
		public void ComputeNodes(){
			Node[] preceding_nodes = this.layer.Get_Preceding_Layer().nodes;
			float valuebuffer = 0;
			for(int i=0;i<weights.length;i++){
				valuebuffer += this.weights[i]*preceding_nodes[i].value;
			}
			this.value = GJMATH.GJMATHUTIL.sigmoid(valuebuffer+this.bias);
//			if(layer != layer.network.output_layer){
//				this.value = GJMATH.GJMATHUTIL.sigmoid(valuebuffer+this.bias);
//			}else{
//				this.value = valuebuffer+this.bias;
//			}
			
		}
		public void AddAdjustment(){
			acc_adj_bias += adj_node;
			for(int i=0;i<adj_weight.length;i++){
				acc_adj_weight[i] += adj_weight[i];
			}
			acc_data++;
		}
		public void ApplyChanges(float learningrate){
			for(int i=0;i<adj_weight.length;i++){
				
				weights[i] += acc_adj_weight[i]/acc_data*learningrate+(past_adj_weight[i]/acc_data*past_learningrate*0.3f);
			}
			bias += (acc_adj_bias/acc_data)*learningrate+(past_adj_bias/acc_data*past_learningrate*0.3f);
			past_adj_weight = acc_adj_weight;
			past_learningrate = learningrate;
			past_adj_bias = acc_adj_bias;
		}
		public void ResetData(){
			acc_adj_bias = 0;
			acc_data = 0;
			acc_adj_weight = new float[weights.length];
		}
	}
}
