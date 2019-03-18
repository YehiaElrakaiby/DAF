package daf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DAF {

	Set<int[]> complete_extensions = new HashSet<int[]>();
	Set<int[]> preferred_extensions = new HashSet<int[]>();

	Integer size;
	int[][] attacks;
	int[] arguments;
	int[] arguments_in_preferred;
	int[] skeptical_arguments;
	int[] credulous_arguments;

	int[] in,out,undecided;
	List<Integer> undecided_locations;

	public DAF(int[][] att) {
		this.attacks = att;
		this.size = attacks.length;
		skeptical_arguments = new int[size];
		credulous_arguments = new int[size];
		arguments_in_preferred = new int[size];
		in = new int[size];
		out = new int[size];
		undecided = new int[size];
		undecided_locations = new ArrayList<Integer>();
		boolean newIn = true;
		while(newIn) {
			newIn = false;
			/*
			 * label in arguments that are not attacked
			 */
			for(int i=0; i< size; i++) {
				if(in[i]!=1) {
					boolean attacked = false;
					for(int j=0; j<size; j++) {
						if(attacks[j][i]==1 && out[j]!=1) {
							attacked = true;
							break;
						}
					}
					if(!attacked) {
						in[i] = 1;
						newIn = true;
					}
				}
			}


			/*
			 * label out arguments that are attacked by arguments that are in
			 */
			for(int i=0; i< size; i++) {
				if(in[i]==1) {
					for(int j=0; j<size; j++) {
						if(attacks[i][j]==1) {
							out[j] = 1;
						}
					}
				}
			}
		}
		System.out.println("\nIn Agruments");

		print(in);
		System.out.println("\nOut Agruments");

		print(out);

		//			/*
		//			 * label in arguments that are attacked only by arguments that are out
		//			 */
		//			for(int i=0; i< size; i++) {
		//				boolean attacked = false;
		//				for(int j=0; j<size; j++) {
		//					if(attacks[j][i]==1 && out[j]!=1) {
		//						attacked = true;
		//						break;
		//					}
		//				}
		//				if(!attacked) {
		//					in[i] = 1;
		//				}
		//			}


		for(int i=0; i< size; i++) {
			if(in[i]!=1 && out[i]!=1) {
				undecided[i] = 1;
				undecided_locations.add(i);
			}
		}




		//		System.out.println("\nIn Agruments");
		//
		//		print(in);
		//		System.out.println("\nOut Agruments");

		//	print(out);
		System.out.println("\nUndecided Agruments");

		print(undecided);


		int size_undecided = undecided_locations.size();
		System.out.println("\nNumber of Undecided "+size_undecided);

		// Run a loop from 0 to 2^n
		for (int i = 0; i < (1 << size_undecided); i++) {
			// System.out.print("{ ");
			int[] subset = new int[size_undecided];
			int m = 1; // m is used to check set bit in binary representation.
			// Print current subset
			for (int j = 0; j < size_undecided; j++) {
				if ((i & m) > 0) {
					// System.out.print(j + " ");
					subset[j] = 1;
				}
				m = m << 1;
			}

			int[] set = findSubset(in,undecided_locations,subset);
			if(conflictFree(set)) {
				if(admissible(set)) {
					if(complete(set)) {
						complete_extensions.add(set);
						//print(subset);
					}
				}
			}

		}
		System.out.println("\nNumber of complete is: "+complete_extensions.size());

		for (int[] extension : complete_extensions) {
			boolean preferred = true;
			for (int[] extension2 : complete_extensions) {
				if (subsetOf(extension, extension2)) {
					preferred = false;
					break;
				}
			}
			if (preferred) {
				preferred_extensions.add(extension);
				for (int k = 0; k < extension.length; k++) {
					if (extension[k] == 1) {
						arguments_in_preferred[k] = 1;
					}
				}
				// System.out.println("Preferred " + preferred_extensions.size());
				// print(extension);
			}
		}

		for (int i = 0; i < size; i++) {
			if (arguments_in_preferred[i] == 1) {
				boolean skeptical = true;
				boolean credulous = false;

				for (int[] extension : preferred_extensions) {
					if (extension[i] == 0) {
						skeptical = false;
					} else {
						credulous = true;
					}
				}
				if (skeptical) {
					skeptical_arguments[i] = 1;
					// System.out.println("SK "+i);
				} else {
					if (credulous) {
						credulous_arguments[i] = 1;
						// System.out.println("CR "+i);
					}
				}
			}
		}
	}

	private int[] findSubset(int[] in2, List<Integer> undecided_locations2, int[] subset) {
		int[] set = in2;
		for(int k =0; k<subset.length; k++) {
			if(subset[k]==1) {
				set[undecided_locations2.get(k)]=1;
			}
		}
		return set;
	}

	@SuppressWarnings("unused")
	private void print(int[] subset) {
		for (int k = 0; k < subset.length; k++) {
			// if(subset[k]==1)
			System.out.print(subset[k] + " ");
			// List<int[]> mn = Arrays.asList(subset);
		}
		System.out.println();
	}

	private boolean subsetOf(int[] extension, int[] extension2) {
		for (int i = 0; i < extension.length; i++) {
			if (extension[i] == 1 && extension2[i] != 1) {
				return false;
			}
		}
		if (extension.equals(extension2)) {
			return false;
		}
		return true;
	}


	/*
	 * A set is admissible if every time one of its members is attacked by an argument, 
	 * then this argument is attacked by the set

	 */

	private boolean admissible(int[] set) { //iterate over members of the set
		for(int i=0; i<set.length; i++) { if(set[i]==1) { //identify attackers of the member
			for(int j=0; j<size; j++ ) { if(attacks[j][i]==1) { boolean
				attacked_not_defended = true; for(int k=0; k<set.length;k++) { if(set[k]==1)
				{ if(attacks[k][j]==1) { attacked_not_defended =false; break; } } }
				if(attacked_not_defended) { return false; } } } } } return true; }

	/*
	 * A set is conflict free if it does not include two members who attack each other
	 */


	private boolean conflictFree(int[] set) { for(int i=0; i<set.length; i++) {
		if(set[i]==1) { for(int j=i+1; j<set.length; j++ ) { if(set[j]==1) {
			if(attacks[i][j]==1 || attacks[j][i]==1) { return false; } } } } } return
					true; }

	/*
	 * A set is complete if it includes all arguments that are acceptable w.r.t. it
	 */

	private boolean complete(int[] set) { //iterate over arguments //iterate over members of the set 
		for(int i=0; i<size; i++) { //identify attackers of the member
			// if i is not member of the set
			if(set[i]==0) {
				//assume i is acceptable
				boolean acceptable = true;
				for(int k=0; k<size; k++) {
					// if k attacks i 
					if(attacks[k][i]==1) {
						//then the attack on i by k is defended by a member j the set
						boolean attacked_by_set = false;
						for(int j=0; j<size;j++) {
							if(set[j]==1 && attacks[j][k]==1) {
								attacked_by_set=true; 
								break;
							}
						}
						if(!attacked_by_set) {
							acceptable = false;
							break;
						}
					}
				}
				if(acceptable) {
					return false;
				}
			}
		} 
		return true;
	}
	
//	private boolean complete(int[] set) { //iterate over arguments //iterate over members of the set 
//		for(int i=0; i<size; i++) { //identify attackers of the member
//			boolean acceptable =true; 
//			for(int j=0; j<size; j++ ) {
//				if(attacks[j][i]==1) { 
//					acceptable=false; 
//					for(int k=0; k<set.length;k++) {
//						if(set[k]==1) { 
//							if(attacks[k][j]==1) { 
//								acceptable=true; 
//							} 
//						} 
//					}
//					if(acceptable && !in_conflict_with_set(i,set) && set[i]==0) { 
//						return false; 
//					} 
//				}
//			}
//		} 
//		return true; 
//	}


//	private boolean in_conflict_with_set(int v, int[] set) { for(int
//			i=0;i<set.length;i++) { if(set[i]==1) { if(attacks[i][v]==1 ||
//			attacks[v][i]==1) { return true; } } } return false; }

	public boolean exists_smaller(Set<String> set, Set<Set<String>> extensions) {
		for (Set<String> extension : extensions) {
			if (extension.containsAll(set) && (extension.size() > set.size())) {
				return true;
			}
		}
		return false;
	}

	public boolean exists_larger(Collection<Integer> set, Set<Set<Integer>> extensions) {
		for (Set<Integer> extension : extensions) {
			if (set.containsAll(extension) && (set.size() > extension.size())) {
				return true;
			}
		}
		return false;
	}

	public Set<int[]> getComplete_extensions() {
		return complete_extensions;
	}

	public void setComplete_extensions(Set<int[]> complete_extensions) {
		this.complete_extensions = complete_extensions;
	}

	public Set<int[]> getPreferred_extensions() {
		return preferred_extensions;
	}

	public void setPreferred_extensions(Set<int[]> preferred_extensions) {
		this.preferred_extensions = preferred_extensions;
	}

	public int[][] getAttacks() {
		return attacks;
	}

	public void setAttacks(int[][] attacks) {
		this.attacks = attacks;
	}

	public int[] getSkeptical_arguments() {
		return skeptical_arguments;
	}

	public void setSkeptical_arguments(int[] skeptical_arguments) {
		this.skeptical_arguments = skeptical_arguments;
	}

	public int[] getCredulous_arguments() {
		return credulous_arguments;
	}

	public void setCredulous_arguments(int[] credulous_arguments) {
		this.credulous_arguments = credulous_arguments;
	}

}
