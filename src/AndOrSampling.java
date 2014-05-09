import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;

public class AndOrSampling {

	LinkedList<int[]> cachedSamples;
	ArrayList<OrNode> orNodes;
	OrNode root;
	
	public double startSampling(GraphicalModel model, int w, int N) {
		
		ArrayList<Variable> nonSoftEvidenceVariables = new ArrayList<>();
		for (Variable variable : model.nonEvidenceVars) {
			if(!variable.isEvidence) {
				nonSoftEvidenceVariables.add(variable);
			}
		}
		UniformSampler Q = new UniformSampler(nonSoftEvidenceVariables);
		Q.model = model;
		Q.overallTopOrderToSampleTopOrder(model.topologicalOrder());
		cachedSamples = new LinkedList<>();
		if (model.network.equals("MARKOV")) {
			Q.generateSamples();
		}
		
		for (int i = 0; i < N; i++) {
			Q.sample();
			cachedSamples.add(Q.dumpSampleInclusive());
		}
		
		PseudoTree pseudoTree = new PseudoTree(model);
		root = pseudoTree.generatePseudoTree();
		
		return postOrderTraverse(root);
	}
	
	public double postOrderTraverse() {
		return postOrderTraverse(root);
	}
	
	public double postOrderTraverse(OrNode orNode) {
		// virtual And Node
		//AndNode andNode = new AndNode(0);
		//andNode.parent = orNode;
		//andNode.children = orNode.children;
		double vOr = 0.0;
		int totalPresentSamples = 0;
		int[] andNodePresentSamples = new int[orNode.nodeVariable.domainSize()];
		for (int i = 0; i < orNode.nodeVariable.domainSize(); i++) {
			for (int[] sampleVals : cachedSamples) {
				if(sampleVals[orNode.nodeVariable.index] == i) {
					// present sample;
					totalPresentSamples++;
					andNodePresentSamples[i]++;
				}
			}	
		}
		
		if(totalPresentSamples == 0) {
			return 0.0;
		}
	
		for (int i = 0; i < orNode.nodeVariable.domainSize(); i++) {
			// Simulating And node that is the child of orNode
			orNode.value = i; 
			// caculate w for xi
			
			double w = underlyProduct(orNode) * orNode.nodeVariable.domainSize();
			double vAnd = 1.0;
			for (int j = 0; j < orNode.children.size(); j++) {
				OrNode secondOrNode = (OrNode) orNode.children.get(j);
				vAnd *= postOrderTraverse(secondOrNode);
			}
			
			vOr += (andNodePresentSamples[i] * w * vAnd) / totalPresentSamples;
		}
		
		return vOr;
	}
	
	public double underlyProduct(OrNode orNode) {
		double product = 1.0;
		for (Factor factor : orNode.cluster) {
			product *= factor.underlyProbability();
		}
		
		return product;
	}

	public static void usage() {
		System.out.println("java  ImportanceSampling " + "FILENAME " + "w "
				+ "N");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		if (2 > args.length) {
			usage();
			System.exit(0);
		}

		String fileName = args[0];
		int w = Integer.valueOf(args[1]);
		int N = Integer.valueOf(args[2]);
		//boolean isAdaptive = (args[3].equals("adaptive")) ? true : false;

		long startTime = System.currentTimeMillis();

		try {
			PrintStream writer = new PrintStream(fileName + ".output." + w
					+ "." + N);
			GraphicalModel model = new GraphicalModel(fileName);
			writer.println("Network data loading completed: "
					+ model.variables.size() + " variables, "
					+ model.factors.size() + " factors");
			writer.println(model.network + " network");
			model.readSoftEvidence(fileName + ".evid");

			writer.println("Evidence loaded, and variables instantiation completed. "
					+ model.evidenceCount + " evidence");

			AndOrSampling sampling = new AndOrSampling();
			double result = sampling.startSampling(model, w, N);

			writer.println("Elimination completed");
			writer.println("");
			writer.println("====================RESULT========================");
			if (model.network.equals("MARKOV")) {
				writer.println("Z = " + result);
			} else {
				writer.println("The probability of evidence = " + result);
				writer.println("");
				System.out.println("Empty Factor Count = "
						+ model.emptyFactorCount);
				System.out.println("probe = " + model.probe);
			}

			long endTime = System.currentTimeMillis();
			writer.println("Running Time = " + (double) (endTime - startTime)
					/ 1000 + "secs");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Succeed!");
		System.out.println("Output file is " + fileName + ".output");
	}

}
