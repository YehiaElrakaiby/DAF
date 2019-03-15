package daf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DAF {

	Set<int[]> complete_extensions= new HashSet<int[]>();
	Set<int[]> preferred_extensions= new HashSet<int[]>();

	Integer size;
	int[][] attacks;
	int[] arguments;
	int[] skeptical_arguments;
	int[] credulous_arguments;

	public DAF(int[][] att) {
		this.attacks = att;
		this.size = attacks.length;
		skeptical_arguments = new int[size];
		credulous_arguments = new int[size];

		// Run a loop from 0 to 2^n
		for (int i = 0; i < (1<<size); i++)
		{
			//System.out.print("{ ");
			int[] subset = new int[size] ;
			int m = 1; // m is used to check set bit in binary representation.
			// Print current subset
			for (int j = 0; j < size; j++)
			{
				if ((i & m) > 0)
				{		
					//System.out.print(j + " ");
					subset[j]=1;
				}
				m = m << 1;
			}

			//System.out.println("}");
			if(conflictFree(subset)) {
				if(admissible(subset)) {
					if(complete(subset)) {
						complete_extensions.add(subset);
						//print(subset);
					}
				}
			}
		}
		for(int[] extension : complete_extensions) {
			boolean preferred = true;
			for(int[] extension2 : complete_extensions) {
				if(subsetOf(extension,extension2)) {
					preferred = false;
					break;
				}
			}
			if(preferred) {
				preferred_extensions.add(extension);
				System.out.println("Preferred " + preferred_extensions.size());
				print(extension);
			}
		}

		for(int i =0; i< size; i++) {
			boolean skeptical = true;
			boolean credulous = false;

			for(int[] extension : preferred_extensions) {
				if(extension[i]==0) {
					skeptical = false;
				} else {
					credulous = true;
				}
			}
			if(skeptical) {
				skeptical_arguments[i]=1;
				System.out.println("SK "+i);
			} else {
				if(credulous) {
					credulous_arguments[i]=1;
					System.out.println("CR "+i);
				}
			}
		}
	}

	
	private void print(int[] subset) {
		for(int k=0;k<subset.length;k++) {
			//if(subset[k]==1)
			System.out.print(subset[k]+" ");
			//List<int[]> mn = Arrays.asList(subset);
		}
		System.out.println();		
	}
	private  boolean subsetOf(int[] extension, int[] extension2) {
		for(int i=0; i<extension.length;i++) {
			if(extension[i]==1 && extension2[i]!=1) {
				return false;
			}
		}
		if(extension.equals(extension2)) {
			return false;
		}
		return true;
	}
	/*
	 * A set is admissible if every time one of its members is attacked by an argument, then this argument is attacked by the set
	 */
	public boolean admissible(int[] set) {
		//iterate over members of the set
		for(int i=0; i<set.length; i++) {
			if(set[i]==1) {
				//identify attackers of the member
				for(int j=0; j<size; j++ ) {
					if(attacks[j][i]==1) {
						boolean attacked_not_defended = true;
						for(int k=0; k<set.length;k++) {
							if(set[k]==1) {
								if(attacks[k][j]==1) {
									attacked_not_defended =false;
									break;
								}
							}
						}
						if(attacked_not_defended) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	/*
	 * A set is conflict free if it does not include two members who attack each other
	 */
	public boolean conflictFree(int[] set) {
		for(int i=0; i<set.length; i++) {
			if(set[i]==1) {
				for(int j=i+1; j<set.length; j++ ) {
					if(set[j]==1) {
						if(attacks[i][j]==1 || attacks[j][i]==1) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	/*
	 * A set is complete if it includes all arguments that are acceptable w.r.t. it
	 */
	public  boolean complete(int[] set) {
		//iterate over arguments
		//iterate over members of the set
		for(int i=0; i<size; i++) {
			//identify attackers of the member
			boolean acceptable =true;
			for(int j=0; j<size; j++ ) {
				if(attacks[j][i]==1) {
					acceptable=false;
					for(int k=0; k<set.length;k++) {
						if(set[k]==1) {
							if(attacks[k][j]==1) {
								acceptable=true;
							}
						}
					}
					if(acceptable && !in_conflict_with_set(i,set) && set[i]==0) {
						return false;
					}
				}
			}
		}
		return true;
	}


	private  boolean in_conflict_with_set(int v, int[] set) {
		for(int i=0;i<set.length;i++) {
			if(set[i]==1) {
				if(attacks[i][v]==1 || attacks[v][i]==1) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean exists_smaller(Set<String> set, Set<Set<String>> extensions) {
		for(Set<String> extension : extensions) {
			if(extension.containsAll(set) && (extension.size()>set.size())) {
				return true;
			}
		}
		return false;
	}

	public boolean exists_larger(Collection<Integer> set, Set<Set<Integer>> extensions) {
		for(Set<Integer> extension : extensions) {
			if(set.containsAll(extension) && (set.size()>extension.size())) {
				return true;
			}
		}
		return false;
	}
	public  Set<int[]> getComplete_extensions() {
		return complete_extensions;
	}
	public void setComplete_extensions(Set<int[]> complete_extensions) {
		this.complete_extensions = complete_extensions;
	}
	public  Set<int[]> getPreferred_extensions() {
		return preferred_extensions;
	}
	public void setPreferred_extensions(Set<int[]> preferred_extensions) {
		this.preferred_extensions = preferred_extensions;
	}
	public  int[][] getAttacks() {
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
	public  int[] getCredulous_arguments() {
		return credulous_arguments;
	}
	public void setCredulous_arguments(int[] credulous_arguments) {
		this.credulous_arguments = credulous_arguments;
	}

}
